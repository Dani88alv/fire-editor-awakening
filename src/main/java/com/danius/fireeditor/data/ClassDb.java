package com.danius.fireeditor.data;

import com.danius.fireeditor.data.model.ClassModel;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ClassDb {

    private static final ClassDb database = new ClassDb();
    private List<ClassModel> classList;

    public ClassDb() {
        readClasses();
        setMaxId();
    }

    private static ClassModel getClass(int id) {
        for (ClassModel unitClass : database.classList) {
            if (unitClass.getId() == id) return unitClass;
        }
        return new ClassModel();
    }

    public static String getClassName(int id) {
        if (isInvalid(id)) return "Mod Class #" + (id - getClassMaxId());
        return getClass(id).getName();
    }

    public static int[] getClassSkills(int id) {
        if (isInvalid(id)) return new int[0];
        return getClass(id).getSkills();
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

    public static int[] getClassPromoted(int id) {
        if (isInvalid(id)) return new int[0];
        return getClass(id).getPromoted();
    }

    public static List<Integer> getClassesByGender(boolean isFemale) {
        List<Integer> classes = new ArrayList<>();
        for (int i = 0; i < database.classList.size(); i++) {
            //If it is not Enemy Only
            if (!hasClassFlag(i, 21)) {
                if (isFemale && hasClassFlag(i, 0)) classes.add(i);
                else if (!isFemale && !hasClassFlag(i, 0)) classes.add(i);
            }
        }
        return classes;
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

    private static boolean isInvalid(int id) {
        return id < 0 || id > getClassMaxId();
    }

    public static int getClassCount() {
        return database.classList.size();
    }

    public static int getClassMaxId() {
        return database.MAX_ID;
    }

    private int MAX_ID  = 0;

    private void setMaxId() {
        int id = 0;
        for (ClassModel classModel : classList) {
            int thisId = classModel.getId();
            if (thisId > id) id = thisId;
        }
        MAX_ID = id;
    }

    public void readClasses() {
        String path = "/com/danius/fireeditor/database/";
        String xmlFilePath = path + "classes.xml";
        classList = new ArrayList<>();
        try (InputStream is = UnitDb.class.getResourceAsStream(xmlFilePath)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + xmlFilePath);
            }
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(is);
            Element rootElement = document.getRootElement();
            String[] stats = new String[]{"hp", "str", "mag", "skl", "spd", "lck", "def", "res", "mov"};
            // Iterate through character elements in the XML
            for (Element characterElement : rootElement.getChildren("class")) {
                ClassModel unitClass = new ClassModel();
                // Parse attributes from the XML
                unitClass.setId(Integer.parseInt(characterElement.getAttributeValue("id")));
                unitClass.setName(characterElement.getAttributeValue("name"));
                //Skills
                String skill1 = characterElement.getAttributeValue("skill1");
                String skill2 = characterElement.getAttributeValue("skill2");
                if (skill1.equals("")) skill1 = "0";
                if (skill2.equals("")) skill2 = "0";
                int skillSize = 0;
                if (Integer.parseInt(skill1) > 0) skillSize++;
                if (Integer.parseInt(skill2) > 0) skillSize++;
                int[] skillArray = new int[skillSize];
                if (skillArray.length > 0) skillArray[0] = Integer.parseInt(skill1);
                if (skillArray.length > 1) skillArray[1] = Integer.parseInt(skill2);
                unitClass.setSkills(skillArray);

                //Promoted
                String promoted1 = characterElement.getAttributeValue("promoted1");
                String promoted2 = characterElement.getAttributeValue("promoted2");
                if (promoted1.equals("")) promoted1 = "0";
                if (promoted2.equals("")) promoted2 = "0";
                int promotedSize = 0;
                if (Integer.parseInt(promoted1) > 0) promotedSize++;
                if (Integer.parseInt(promoted2) > 0) promotedSize++;
                int[] promotedArray = new int[promotedSize];
                if (promotedArray.length > 0) promotedArray[0] = Integer.parseInt(promoted1);
                if (promotedArray.length > 1) promotedArray[1] = Integer.parseInt(promoted2);
                unitClass.setPromoted(promotedArray);

                //Base Stats
                Element elemBase = characterElement.getChild("base");
                int[] base = new int[9];
                for (int i = 0; i < base.length; i++) {
                    String value = elemBase.getAttributeValue(stats[i]);
                    base[i] = Integer.parseInt(value);
                }
                unitClass.setStatsBase(base);

                //Max Stats
                Element elemMax = characterElement.getChild("max");
                int[] max = new int[9];
                for (int i = 0; i < max.length; i++) {
                    max[i] = Integer.parseInt(elemMax.getAttributeValue(stats[i]));
                }
                unitClass.setStatsMax(max);

                //Flags
                Element elemFlags = characterElement.getChild("flags");
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
