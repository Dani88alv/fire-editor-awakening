package com.danius.fireeditor.data;

import com.danius.fireeditor.data.model.ClassModel;
import com.danius.fireeditor.data.model.SkillModel;
import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.savefile.units.extrablock.RawParent;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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

    /*
    Categories:
    Personal Skills (Including Einherjar)
    Item Skills
    Base Skills (from base re-classing options, no inheritance included) (Includes Einherjar starting class)
    Skills from Inherited Classes
    Skills exclusive to the father by last skill slot
    Skills exclusive to the mother by last skill slot
     */
    public static List<SkillModel> getLegalSkills(Unit unit) {
        List<Integer> currentSkills = unit.rawSkill.getLearnedSkills();
        List<SkillModel> allSkills = new ArrayList<>();

        //Base classes skills
        List<SkillModel> classSkills = getSkillsFromBaseClasses(unit);
        //Personal Skills
        List<SkillModel> personalSkills = getPersonalSkills(unit);
        //Item Skills
        List<SkillModel> itemSkills = getItemSkills();

        //Child inheritance skills
        List<SkillModel> inheritClassSkills = getExclusiveInheritClassSkills(unit);
        //Last Skill Slot exclusive skills (CANNOT BE GOTTEN BY ANY OTHER MEANS)
        List<SkillModel> inheritExclusiveFather = getExclusiveInheritSlotSkills(unit, 0);
        List<SkillModel> inheritExclusiveMother = getExclusiveInheritSlotSkills(unit, 1);

        //All the skills are set
        allSkills.addAll(classSkills);
        allSkills.addAll(personalSkills);
        allSkills.addAll(itemSkills);
        allSkills.addAll(inheritClassSkills);
        allSkills.addAll(inheritExclusiveFather);
        allSkills.addAll(inheritExclusiveMother);


        return removeDuplicates(allSkills);
    }

    public static List<SkillModel> getSkillsFromClasses(List<ClassModel> classes) {
        List<SkillModel> skills = new ArrayList<>();
        for (ClassModel aClass : classes) {
            int classId = aClass.getId();
            skills.addAll(getSkillsFromClass(classId));
        }
        return removeDuplicates(skills);
    }

    //Returns a list of available skills for a class
    public static List<SkillModel> getSkillsFromClass(int classId) {
        List<Integer> skillId = ClassDb.getSkills(classId);
        List<SkillModel> skills = new ArrayList<>();
        for (Integer integer : skillId) {
            SkillModel skill = getSkill(integer);
            skills.add(skill);
        }
        return skills;
    }

    //Checks the base classes, current class and Einherjar starting class
    public static List<SkillModel> getSkillsFromBaseClasses(Unit unit) {
        List<SkillModel> allSkills = new ArrayList<>();

        List<ClassModel> unitClasses = ClassDb.getUnitBaseClasses(unit.getUnitId(), unit.isFemale());
        allSkills = getSkillsFromClasses(unitClasses);
        List<SkillModel> currentClassSkills = getSkillsFromClass(unit.rawBlock1.unitClass());

        if (unit.rawLog != null) {
            if (unit.rawLog.isEinherjar() && unit.rawLog.hasEinherjarId()) {
                int avatarClass = UnitDb.getEinClass(unit.rawLog.getLogIdLastByte());
                currentClassSkills.addAll(getSkillsFromClass(avatarClass));
            }
        }
        allSkills.addAll(currentClassSkills);
        return removeDuplicates(allSkills);
    }

    //Retrieves personal skills, and Einherjar skills if matches
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

    //Retrieves personal skills
    public static List<SkillModel> getPersonalSkills(int unitId) {
        List<SkillModel> allSkills = new ArrayList<>();
        //Base Personal Skills
        List<Integer> personalBase = UnitDb.getUnitSkills(unitId);
        for (Integer integer : personalBase) {
            allSkills.add(getSkill(integer));
        }
        return allSkills;
    }

    //Retrieves item exclusive skills
    public static List<SkillModel> getItemSkills() {
        List<SkillModel> allSkills = new ArrayList<>();
        for (SkillModel skillModel : database.skillList) {
            if (skillModel.isItem()) allSkills.add(skillModel);
        }
        return allSkills;
    }

    public static List<SkillModel> getExclusiveInheritSlotSkills(Unit unit, int parentSlot) {
        List<SkillModel> exclusiveSkills = new ArrayList<>();
        if (unit.rawChild == null) return exclusiveSkills;
        List<SkillModel> inheritClassSkills = getSkillsFromInheritClasses(unit);
        //Last Skill Slot Inheritance
        List<SkillModel> lastSkillsParent = getSlotInheritSkills(unit, parentSlot);
        //The matching skills are removed
        exclusiveSkills = lastSkillsParent.stream()
                .filter(skill -> inheritClassSkills.stream().noneMatch(item -> item.getId() == skill.getId())).toList();

        return removeDuplicates(exclusiveSkills);
    }

    public static List<SkillModel> getExclusiveInheritClassSkills(Unit unit) {
        List<SkillModel> baseSkills = getSkillsFromBaseClasses(unit);
        // Create a third list for exclusive items
        List<SkillModel> allClassSkills = getSkillsFromInheritClasses(unit);
        List<SkillModel> exclusiveSkills = new ArrayList<>(allClassSkills);
        // Check for matching SkillModel between baseSkills and childClassesSkills
        exclusiveSkills.removeIf(model -> baseSkills.stream().anyMatch(baseSkill -> baseSkill.getId() == model.getId()));
        return removeDuplicates(exclusiveSkills);
    }

    //Retrieves all the skills available exclusively through parent classes
    private static List<SkillModel> getSkillsFromInheritClasses(Unit unit) {
        List<SkillModel> childClassesSkills = new ArrayList<>();
        if (unit.rawChild == null) return childClassesSkills;

        // Inherited classes
        List<ClassModel> childClasses = ClassDb.getInheritClasses(unit);
        // Remove item classes that cannot be inherited
        childClasses.removeIf(model -> model.isDlc() && !model.canBeInherited());
        // Skills from the inherited classes
        childClassesSkills = getSkillsFromClasses(childClasses);
        childClassesSkills.removeIf(model -> !model.isInherit()); // Remove un-inheritable skills

        return removeDuplicates(childClassesSkills);
    }

    //Retrieves ALL the skills that can be passed through last skill slot inheritance
    private static List<SkillModel> getSlotInheritSkills(Unit unit, int parentSlot) {
        List<SkillModel> fatherSkills = new ArrayList<>();
        boolean unitFemale = unit.isFemale();

        //First, the skills from the base class of the parent and grandparents are retrieved
        RawParent rawParent = unit.rawChild.getRawParent(parentSlot);
        for (int i = 0; i < 3; i++) {
            int parent = rawParent.parentId(i);
            if (parent == 0xFFFF) continue;

            //For this purpose, un-inheritable classes are not removed
            //Since this is last skill slot, the parent's gender class set is retrieved
            List<ClassModel> parentClasses = ClassDb.getUnitBaseClasses(parent, UnitDb.isUnitFemale(parent));
            //BUT if the class is item AND cannot be inherited, then it is removed
            parentClasses.removeIf(model -> model.isDlc() && !model.canBeInherited());

            //The skills from each class and personal skills are retrieved and sorted by ID
            List<SkillModel> parentClassesSkills = getSkillsFromClasses(parentClasses);
            parentClassesSkills.addAll(getPersonalSkills(parent));
            parentClassesSkills.removeIf(model -> !model.isInherit()); //Remove un-inheritable skills
            parentClassesSkills = removeDuplicates(parentClassesSkills);

            fatherSkills.addAll(parentClassesSkills);
        }

        //The trait flags from the parent and grandparents are mixed
        List<Integer> fatherFlags = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int parent = rawParent.parentId(i);
            fatherFlags.addAll(UnitDb.getUnitFlags(parent));
        }

        //Cool, then, check if the 2 parents have forcing skills
        //It will retrieve the FIRST forced skill based on skill ID
        SkillModel forcedFather = getForcedInheritSkill(fatherSkills, fatherFlags, unitFemale);

        //Ok, if a forced skill was found remove everything that was retrieved earlier lol
        if (forcedFather != null) {
            fatherSkills = new ArrayList<>();
            fatherSkills.add(forcedFather);
        }

        return removeDuplicates(fatherSkills);
    }

    /*
    Retrieves a forced skill, if found
    This function checks all the trait flags from the parent and mother (and their respective grandparents)
    It will check all the parameter skills, if any of them are set to be forced, the skills are ordered by ID
    If it finds a skill that is set to be forced and meet the criteria, return it
    The only conditions are matching trait flags (like Aversa/Walhart flag) and gender (RF/Aether)
     */
    public static SkillModel getForcedInheritSkill(List<SkillModel> skillList, List<Integer> traitFlags, boolean isUnitFemale) {
        SkillModel skillForced = null;
        skillList = removeDuplicates(skillList);
        for (SkillModel skillModel : skillList) {
            List<Integer> skillForceFlags = skillModel.getForceFlags();
            //If the skill is set to be forced
            for (int flagSkill : skillForceFlags) {
                //Alright, this skill is forced, now check gender
                if (traitFlags.contains(flagSkill)) {
                    //The skill is being forced, no matter the gender
                    if (!skillModel.isForcingGender()) {
                        skillForced = skillModel;
                        skillForced.setForceToUnit(true);
                        break;
                    }
                    //Skill is being forced by gender and both the skill and the unit are female
                    else if (skillModel.isForcingGender() && skillModel.isForcingFemale() && isUnitFemale) {
                        skillForced = skillModel;
                        skillForced.setForceToUnit(true);
                        break;
                    }
                    //Skill is being forced by gender and both the skill and the unit are male
                    else if (skillModel.isForcingGender() && skillModel.isForcingMale() && !isUnitFemale) {
                        skillForced = skillModel;
                        skillForced.setForceToUnit(true);
                        break;
                    }
                }
            }
            if (skillForced != null) break;
        }
        return skillForced;
    }

    private static boolean isInvalid(int id) {
        return id < 0 || id >= getSkillCount();
    }

    public static int getSkillCount() {
        return database.skillList.size();
    }

    /* Removes duplicated skills and sort them by ID */
    private static List<SkillModel> removeDuplicates(List<SkillModel> classList) {
        Set<Integer> seenIds = new HashSet<>();
        Map<Integer, SkillModel> uniqueSkillsMap = new HashMap<>();

        for (SkillModel skillModel : classList) {
            int id = skillModel.getId();
            if (!uniqueSkillsMap.containsKey(id) || skillModel.isForceToUnit()) {
                uniqueSkillsMap.put(id, skillModel);
            }
        }

        List<SkillModel> uniqueList = new ArrayList<>(uniqueSkillsMap.values());

        Comparator<SkillModel> idComparator = Comparator.comparingInt(SkillModel::getId);
        uniqueList.sort(idComparator);

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

                boolean inherit = !"false".equals(element.getAttributeValue("inherit"));
                skillModel.setInherit(inherit);
                boolean item = "true".equals(element.getAttributeValue("dlcItem"));
                skillModel.setItem(item);
                String forceGender = element.getAttributeValue("forceGender", "");
                skillModel.setForceGender(forceGender);

                Element forceFlagElement = element.getChild("forceFlag");
                List<Integer> flagList = new ArrayList<>();
                if (forceFlagElement != null) {
                    List<Element> flagElements = forceFlagElement.getChildren("flag");
                    for (Element flagElement : flagElements) {
                        int flag = Integer.parseInt(flagElement.getText());
                        flagList.add(flag);
                    }
                }
                skillModel.setForceFlags(flagList);

                skillList.add(skillModel);
            }
        } catch (IOException | JDOMException ex) {
            throw new RuntimeException(ex);
        }
    }
}
