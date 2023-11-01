package com.danius.fireeditor.data;

import com.danius.fireeditor.data.model.ClassModel;
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

public class ClassDb {

    private static final ClassDb database = new ClassDb();
    private List<ClassModel> classList;

    public ClassDb() {
        readClasses();
        setMaxId();
    }

    public static List<ClassModel> getClassesFromUnit(Unit unit) {
        List<ClassModel> allClasses = new ArrayList<>();
        //The base clases are retrieved
        List<ClassModel> baseClasses = getUnitBaseClasses(unit.getUnitId(), unit.isFemale());
        //If it is a child unit, the inherited classes are retrieved too
        List<ClassModel> inheritClasses = new ArrayList<>();
        if (unit.rawChild != null) inheritClasses = getInheritClasses(unit);
        allClasses.addAll(baseClasses);
        allClasses.addAll(inheritClasses);
        allClasses = removeDuplicates(allClasses);

        //The classes are conveniently sorted by ID
        Comparator<ClassModel> idComparator = Comparator.comparingInt(ClassModel::getId);
        allClasses.sort(idComparator);
        return allClasses;
    }

    public static List<ClassModel> getUnitBaseClasses(int unitId, boolean isFemale) {
        List<ClassModel> allClasses = new ArrayList<>();
        List<ClassModel> promotedClasses = new ArrayList<>();
        //Base classes
        List<Integer> classesId = (isFemale) ? UnitDb.getUnitFemaleReclasses(unitId) : UnitDb.getUnitMaleReclasses(unitId);
        for (Integer integer : classesId) {
            ClassModel classModel = getClass(integer);
            allClasses.add(classModel);
        }

        //Promoted Classes
        for (ClassModel classModel : allClasses) {
            List<ClassModel> promoted = getClassesPromoted(classModel.getId());
            promotedClasses.addAll(promoted);
        }
        allClasses.addAll(promotedClasses);

        //Tactician Tree
        List<ClassModel> tacticianTree = new ArrayList<>();
        for (ClassModel classModel : allClasses) {
            //Let's go for a big ride
            if (classModel.isTactician()) {
                List<ClassModel> tacticianClasses = getTacticianTree(isFemale);
                tacticianTree.addAll(tacticianClasses);
                break;
            }
        }
        allClasses.addAll(tacticianTree);

        //Item/DLC Classes
        List<ClassModel> itemClasses = getItemClasses(isFemale);
        allClasses.addAll(itemClasses);

        return removeDuplicates(allClasses);
    }

    public static List<ClassModel> getItemClasses(boolean isFemale) {
        List<ClassModel> allClasses = new ArrayList<>();
        List<ClassModel> database = getAllClasses();
        for (ClassModel classModel : database) {
            //If it is a DLC class and matches the gender flag
            if (classModel.isDlc()) {
                if (isClassFemale(classModel.getId()) == isFemale) allClasses.add(classModel);
            }
        }
        return allClasses;
    }

    public static List<ClassModel> getInheritClasses(Unit unit) {
        List<ClassModel> allClasses = new ArrayList<>();
        if (unit.rawChild == null) return allClasses;
        boolean isFemale = unit.isFemale();
        for (int i = 0; i < 6; i++) {
            int parent = unit.rawChild.parentId(i);
            if (parent == 0xFFFF) continue;
            List<ClassModel> parentClasses = getUnitBaseClasses(parent, isFemale);
            parentClasses.removeIf(model -> !model.canBeInherited()); //Remove un-inheritable classes
            allClasses.addAll(parentClasses);
        }
        return removeDuplicates(allClasses);
    }

    public static List<ClassModel> getInheritClasses(Unit unit, int parentSlot) {
        List<ClassModel> allClasses = new ArrayList<>();
        if (unit.rawChild == null) return allClasses;
        boolean isFemale = unit.isFemale();

        int parent = unit.rawChild.parentId(parentSlot);
        if (parent == 0xFFFF) return allClasses;
        List<ClassModel> parentClasses = getUnitBaseClasses(parent, isFemale);
        parentClasses.removeIf(model -> !model.canBeInherited()); //Remove un-inheritable classes
        allClasses.addAll(parentClasses);

        return removeDuplicates(allClasses);
    }

    private static List<ClassModel> getTacticianTree(boolean isFemale) {
        List<ClassModel> allClasses = new ArrayList<>();
        List<ClassModel> database = getAllClasses();
        for (ClassModel classModel : database) {
            //If it is a valid tactician tree class
            if (classModel.isTacticianTree()) {
                boolean classModelFemale = isClassFemale(classModel.getId());
                //If both gender match, add it
                if (classModelFemale == isFemale) {
                    allClasses.add(classModel);
                }
            }
        }
        return allClasses;
    }

    private static List<ClassModel> removeDuplicates(List<ClassModel> classList) {
        Set<Integer> seenIds = new HashSet<>();
        List<ClassModel> uniqueList = new ArrayList<>();

        for (ClassModel classModel : classList) {
            int id = classModel.getId();
            if (!seenIds.contains(id)) {
                seenIds.add(id);
                uniqueList.add(classModel);
            }
        }
        return uniqueList;
    }

    private static ClassModel getClass(int id) {
        for (ClassModel unitClass : database.classList) {
            if (unitClass.getId() == id) return unitClass;
        }
        return new ClassModel();
    }

    public static List<ClassModel> getAllClasses() {
        return database.classList;
    }

    public static String getClassName(int id) {
        if (isInvalid(id)) return "Mod Class #" + (id - getClassMaxId());
        return getClass(id).getName();
    }

    public static List<String> getClassNames() {
        List<String> names = new ArrayList<>();
        for (ClassModel classModel : database.classList) names.add(classModel.getName());
        return names;
    }

    public static List<String> getClassNames(int max) {
        int vanillaMax = database.MAX_ID;
        List<String> names = getClassNames();
        for (int i = vanillaMax; i < max; i++) {
            names.add(getClassName(i + 1));
        }
        return names;
    }

    public static int[] getClassBaseStats(int id) {
        int[] stats = new int[8];
        if (isInvalid(id)) return stats;
        int[] all = getClass(id).getStatsBase();
        System.arraycopy(all, 0, stats, 0, stats.length);
        return stats;
    }

    public static int[] getClassMaxStats(int id) {
        int[] stats = new int[8];
        if (isInvalid(id)) return stats;
        int[] all = getClass(id).getStatsMax();
        System.arraycopy(all, 0, stats, 0, stats.length);
        return stats;
    }

    public static int getClassMove(int id) {
        if (isInvalid(id)) return 5;
        return getClass(id).getStatsBase()[8];
    }

    private static List<ClassModel> getClassesPromoted(int id) {
        if (isInvalid(id)) return new ArrayList<>();
        List<Integer> classesId = getClass(id).getPromotedClasses();
        List<ClassModel> classes = new ArrayList<>();
        for (Integer integer : classesId) {
            classes.add(getClass(integer));
        }
        return classes;
    }

    public static List<Integer> getSkills(int id) {
        if (isInvalid(id)) return new ArrayList<>();
        return getClass(id).getSkillList();
    }

    public static List<Integer> getClassFlags(int id) {
        if (isInvalid(id)) return new ArrayList<>();
        return getClass(id).getFlags();
    }

    public static boolean hasClassFlag(int id, int flag) {
        if (isInvalid(id)) return false;
        return getClassFlags(id).contains(flag);
    }

    public static boolean isClassFemale(int id) {
        if (isInvalid(id)) return false;
        return hasClassFlag(id, 0);
    }

    private static boolean isInvalid(int id) {
        return id < 0 || id > getClassMaxId();
    }

    public static int getClassCount() {
        return database.classList.size();
    }

    public static int getClassMaxId() {
        return database.MAX_ID;
    }

    public static boolean hasEnemyPortrait(int id) {
        if (isInvalid(id)) return false;
        return getClass(id).isEnemyPortrait();
    }

    public static boolean hasRisenPortrait(int id) {
        if (isInvalid(id)) return false;
        return getClass(id).isRisenPortrait();
    }

    public static int getRandomEnemyClass() {
        List<ClassModel> classes = new ArrayList<>();
        for (int i = 0; i < getClassCount(); i++) {
            classes.add(getClass(i));
        }
        Random random = new Random();
        int randomIndex = random.nextInt(classes.size()); // Generate a random index within the array's length
        return classes.get(randomIndex).getId();
    }

    private int MAX_ID = 0;

    private void setMaxId() {
        int id = 0;
        for (ClassModel classModel : classList) {
            int thisId = classModel.getId();
            if (thisId > id) id = thisId;
        }
        MAX_ID = id;
    }

    public void readClasses() {
        String path = Constants.RES_XML;
        String xmlFilePath = path + "classes.xml";
        classList = new ArrayList<>();
        try (InputStream is = ClassDb.class.getResourceAsStream(xmlFilePath)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + xmlFilePath);
            }
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(is);
            Element rootElement = document.getRootElement();
            String[] stats = new String[]{"hp", "str", "mag", "skl", "spd", "lck", "def", "res", "mov"};
            // Iterate through character elements in the XML
            for (Element element : rootElement.getChildren("class")) {
                ClassModel unitClass = new ClassModel();
                // Parse attributes from the XML
                unitClass.setId(Integer.parseInt(element.getAttributeValue("id")));
                unitClass.setName(element.getAttributeValue("name"));

                boolean inherit = !"false".equals(element.getAttributeValue("inherit"));
                unitClass.setInherit(inherit);
                boolean enemyPortrait = "true".equals(element.getAttributeValue("portrait"));
                unitClass.setEnemyPortrait(enemyPortrait);
                boolean risenPortrait = "true".equals(element.getAttributeValue("risen"));
                unitClass.setRisenPortrait(risenPortrait);
                boolean tacticianTree = !"true".equals(element.getAttributeValue("exclusive"));
                unitClass.setTacticianTree(tacticianTree);
                boolean isTactician = "true".equals(element.getAttributeValue("tactician"));
                unitClass.setTactician(isTactician);
                boolean isDlc = "true".equals(element.getAttributeValue("dlc"));
                unitClass.setDlc(isDlc);

                //Skills
                String skill1 = element.getAttributeValue("skill1", "-1");
                String skill2 = element.getAttributeValue("skill2", "-1");
                if (skill1.isEmpty()) skill1 = "-1";
                if (skill2.isEmpty()) skill2 = "-1";

                List<Integer> skillList = new ArrayList<>();
                int skill1Value = Integer.parseInt(skill1);
                int skill2Value = Integer.parseInt(skill2);
                if (skill1Value >= 0) skillList.add(skill1Value);
                if (skill2Value >= 0) skillList.add(skill2Value);
                unitClass.setSkillList(skillList);

                //Promoted Classes
                String promoted1 = element.getAttributeValue("promoted1", "-1");
                String promoted2 = element.getAttributeValue("promoted2", "-1");
                if (promoted1.isEmpty()) promoted1 = "-1";
                if (promoted2.isEmpty()) promoted2 = "-1";

                List<Integer> promotedList = new ArrayList<>();
                int promoted1Value = Integer.parseInt(promoted1);
                int promoted2Value = Integer.parseInt(promoted2);
                if (promoted1Value >= 0) promotedList.add(promoted1Value);
                if (promoted2Value >= 0) promotedList.add(promoted2Value);
                unitClass.setPromotedClasses(promotedList);


                //Base Stats
                Element elemBase = element.getChild("base");
                int[] base = new int[9];
                for (int i = 0; i < base.length; i++) {
                    String value = elemBase.getAttributeValue(stats[i]);
                    base[i] = Integer.parseInt(value);
                }
                unitClass.setStatsBase(base);

                //Max Stats
                Element elemMax = element.getChild("max");
                int[] max = new int[9];
                for (int i = 0; i < max.length; i++) {
                    max[i] = Integer.parseInt(elemMax.getAttributeValue(stats[i]));
                }
                unitClass.setStatsMax(max);

                //Flags
                Element elemFlags = element.getChild("flags");
                List<Element> flagsElements = elemFlags.getChildren("flag");
                List<Integer> flags = new ArrayList<>();
                for (Element flagElement : flagsElements) {
                    int flagValue = Integer.parseInt(flagElement.getText());
                    // Add the integer value to the list
                    flags.add(flagValue);
                }
                unitClass.setTraitFlags(flags);

                classList.add(unitClass);
            }
        } catch (IOException | JDOMException ex) {
            throw new RuntimeException(ex);
        }
    }

}
