package com.danius.fireeditor.model;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Classes {

    private List<ClassModel> classList;

    public Classes() {
        readClasses();
    }

    public ClassModel getClass(int id) {
        for (ClassModel unitClass : classList) {
            if (unitClass.getId() == id) return unitClass;
        }
        return new ClassModel();
    }

    public String getName(int id) {
        if (invalid(id)) return "Outrealm Class #" + (id - size() + 1);
        return getClass(id).getName();
    }

    public int[] skills(int id) {
        if (invalid(id)) return new int[0];
        return getClass(id).getSkills();
    }

    public int[] getBase(int id) {
        int[] stats = new int[8];
        if (invalid(id)) return stats;
        int[] all = getClass(id).getStatsBase();
        System.arraycopy(all, 0, stats, 0, stats.length);
        return stats;
    }

    public int[] getMax(int id) {
        int[] stats = new int[8];
        if (invalid(id)) return stats;
        int[] all = getClass(id).getStatsMax();
        System.arraycopy(all, 0, stats, 0, stats.length);
        return stats;
    }

    public int getMove(int id) {
        if (invalid(id)) return 5;
        return getClass(id).getStatsBase()[8];
    }

    public int[] getPromoted(int id) {
        if (invalid(id)) return new int[0];
        return getClass(id).getPromoted();
    }

    public List<Integer> getGenderClasses(boolean isFemale) {
        List<Integer> classes = new ArrayList<>();
        for (int i = 0; i < classList.size(); i++) {
            //If it is not Enemy Only
            if (!hasFlag(i, 21)) {
                if (isFemale && hasFlag(i, 0)) classes.add(i);
                else if (!isFemale && !hasFlag(i, 0)) classes.add(i);
            }
        }
        return classes;
    }

    public List<Integer> getFlags(int id) {
        if (invalid(id)) return new ArrayList<>();
        return getClass(id).getFlags();
    }

    public boolean hasFlag(int id, int flag) {
        if (invalid(id)) return false;
        return getFlags(id).contains(flag);
    }

    public boolean isFemale(int id) {
        if (invalid(id)) return false;
        return hasFlag(id, 0);
    }

    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        for (ClassModel classModel : classList) names.add(classModel.getName());
        return names;
    }

    public List<Integer> getFamilyClass(int id) {
        List<Integer> list = new ArrayList<>();
        list.add(id);
        //The promoted classes are added
        int[] promoted = getPromoted(id);
        for (int k : promoted) list.add(k);
        //The lower classes are added
        for (int i = 0; i < classList.size(); i++) {
            int[] classToPromote = classList.get(i).getPromoted();
            for (int k : classToPromote) {
                if (k == id) {
                    list.add(i);
                }
            }
        }
        return list;
    }

    public boolean invalid(int id) {
        return id < 0 || id >= size();
    }

    public int size() {
        return classList.size();
    }

    public void readClasses() {
        String path = "/com/danius/fireeditor/database/";
        String xmlFilePath = path + "classes.xml";
        classList = new ArrayList<>();
        try {
            File file = new File(Objects.requireNonNull(Characters.class.getResource(xmlFilePath)).getFile());
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(file);
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
