package com.danius.fireeditor.data;

import com.danius.fireeditor.data.model.ClassModel;
import com.danius.fireeditor.data.model.SkillModel;
import com.danius.fireeditor.data.model.UnitModel;
import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.units.Unit;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SkillDb {

    private static final SkillDb database = new SkillDb();

    private List<SkillModel> skillList = new ArrayList<>();

    public SkillDb() {
        readSkills();
    }

    public static SkillModel getSkill(int id) {
        for (SkillModel skill : database.skillList) {
            if (skill.getId() == id) return skill;
        }
        return new SkillModel();
    }

    public static List<SkillModel> getLegalSkills(Unit unit) {
        List<SkillModel> allSkills = new ArrayList<>();

        //Child inheritance skills
        List<SkillModel> inheritSkills = new ArrayList<>();
        if (unit.rawChild != null) {
            //Inherited classes
            boolean isFemale = unit.isFemale();
            List<ClassModel> childClasses = ClassDb.getInheritClasses(unit);
            List<SkillModel> childClassesSkills = getSkillsFromClasses(childClasses, true);
            inheritSkills.addAll(childClassesSkills);

            //Personal Skills from the parents (ignore the 1 skill passing limit, just calculate everything lol)
            List<SkillModel> childPersonalSkills = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                int parent = unit.rawChild.parentId(i);
                if (parent == 0xFFFF) continue;

                //Check if the parent has a Hero/Lucina flag and force Rightful King/Aether
                else if (UnitDb.unitHasFlag(parent, 1) || UnitDb.unitHasFlag(parent, 12)) {
                    if (isFemale) childPersonalSkills.add(getSkill(72));
                    else childPersonalSkills.add(getSkill(20));
                    continue;
                }
                //Checks Walhart flag and force Conqueror
                else if (UnitDb.unitHasFlag(parent, 13)) {
                    childPersonalSkills.add(getSkill(86));
                    continue;
                }
                //Checks Aversa flag and force Shadowgift
                else if (UnitDb.unitHasFlag(parent, 14)) {
                    childPersonalSkills.add(getSkill(87));
                    continue;
                }

                //Class skills from the parent
                List<ClassModel> parentClasses = ClassDb.getUnitBaseClasses(parent, isFemale);
                List<SkillModel> parentClassesSkills = getSkillsFromClasses(parentClasses, true);
                childPersonalSkills.addAll(parentClassesSkills);

                //Personal skills from the parent
                List<SkillModel> parentPersonal = getPersonalSkills(parent);
                for (SkillModel skillModel : parentPersonal) {
                    if (skillModel.isInherit()) childPersonalSkills.add(skillModel);
                }
                inheritSkills.addAll(childPersonalSkills);
                childPersonalSkills = removeDuplicates(childPersonalSkills);
            }
            inheritSkills.addAll(childPersonalSkills);
        }

        //Base classes skills
        List<ClassModel> unitClasses = ClassDb.getClassesFromUnit(unit);
        List<SkillModel> classSkills = getSkillsFromClasses(unitClasses, false);
        List<SkillModel> currentClassSkills = getSkillsFromClass(unit.rawBlock1.unitClass(), false);

        //Personal Skills
        List<SkillModel> personalSkills = getPersonalSkills(unit);

        //Item Skills
        List<SkillModel> itemSkills = getItemSkills();

        allSkills.addAll(inheritSkills);
        allSkills.addAll(classSkills);
        allSkills.addAll(currentClassSkills);
        allSkills.addAll(personalSkills);
        allSkills.addAll(itemSkills);
        return removeDuplicates(allSkills);
    }

    public static List<SkillModel> getPersonalSkills(Unit unit) {
        List<SkillModel> allSkills = new ArrayList<>();
        //Base Personal Skills
        List<Integer> personalBase = UnitDb.getUnitSkills(unit.getUnitId());
        for (Integer integer : personalBase) {
            allSkills.add(getSkill(integer));
        }
        //SpotPass & DLC Personal Skills
        if (unit.rawLog != null) {
            if (unit.rawLog.isEinherjar() && unit.rawLog.hasEinherjarId()) {
                int logId = unit.rawLog.getLogIdLastByte();
                List<Integer> einherjarSkills = UnitDb.getEinSkills(logId);
                List<SkillModel> personalEinherjar = new ArrayList<>();
                for (Integer einherjarSkill : einherjarSkills) {
                    personalEinherjar.add(getSkill(einherjarSkill));
                }
                allSkills.addAll(personalEinherjar);
            }
        }
        return allSkills;
    }

    public static List<SkillModel> getPersonalSkills(int unitId) {
        List<SkillModel> allSkills = new ArrayList<>();
        //Base Personal Skills
        List<Integer> personalBase = UnitDb.getUnitSkills(unitId);
        for (Integer integer : personalBase) {
            allSkills.add(getSkill(integer));
        }
        return allSkills;
    }

    public static List<SkillModel> getSkillsFromClass(int classId, boolean inheritance) {
        List<Integer> skillId = ClassDb.getSkills(classId);
        List<SkillModel> skills = new ArrayList<>();
        for (Integer integer : skillId) {
            SkillModel skill = getSkill(integer);
            if (inheritance && skill.isInherit()) skills.add(skill);
            else skills.add(skill);
        }
        return skills;
    }

    public static List<SkillModel> getSkillsFromClasses(List<ClassModel> classes, boolean inheritance) {
        List<SkillModel> skills = new ArrayList<>();
        for (ClassModel aClass : classes) {
            int classId = aClass.getId();
            skills.addAll(getSkillsFromClass(classId, inheritance));
        }
        return skills;
    }

    public static List<SkillModel> getItemSkills() {
        List<SkillModel> allSkills = new ArrayList<>();
        for (SkillModel skillModel : database.skillList) {
            if (skillModel.isItem()) allSkills.add(skillModel);
        }
        return allSkills;
    }

    private static boolean isInvalid(int id) {
        return id < 0 || id >= getSkillCount();
    }

    public static int getSkillCount() {
        return database.skillList.size();
    }

    private static List<SkillModel> removeDuplicates(List<SkillModel> classList) {
        Set<Integer> seenIds = new HashSet<>();
        List<SkillModel> uniqueList = new ArrayList<>();

        for (SkillModel skillModel : classList) {
            int id = skillModel.getId();
            if (!seenIds.contains(id)) {
                seenIds.add(id);
                uniqueList.add(skillModel);
            }
        }
        return uniqueList;
    }


    public void readSkills() {
        String xmlFilePath = Constants.RES_XML + "skills.xml";
        skillList = new ArrayList<>();
        try (InputStream is = SkillDb.class.getResourceAsStream(xmlFilePath)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + xmlFilePath);
            }
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(is);
            Element rootElement = document.getRootElement();
            // Iterate through character elements in the XML
            for (Element element : rootElement.getChildren("skill")) {
                SkillModel skillModel = new SkillModel();
                // Parse attributes from the XML
                skillModel.setId(Integer.parseInt(element.getAttributeValue("id")));
                skillModel.setName(element.getAttributeValue("name"));

                boolean inherit = "true".equals(element.getAttributeValue("inherit"));
                skillModel.setInherit(inherit);
                boolean item = "true".equals(element.getAttributeValue("dlcItem"));
                skillModel.setItem(item);

                skillList.add(skillModel);
            }
        } catch (IOException | JDOMException ex) {
            throw new RuntimeException(ex);
        }
    }
}
