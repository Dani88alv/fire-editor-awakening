package com.danius.fireeditor.model;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Characters {
    private static final int DEFAULT_UNIT = 0x35;
    private List<UnitModel> units;
    private List<EinherjarModel> einherjar;

    public Characters() {
        readUnits();
        readEinherjar();
    }

    private UnitModel getUnit(int id) {
        for (UnitModel unit : units) {
            if (unit.getId() == id) return unit;
        }
        return new UnitModel();
    }

    private EinherjarModel getEinherjar(int logId) {
        for (EinherjarModel unit : einherjar) {
            if (unit.getLogId() == logId) return unit;
        }
        return new EinherjarModel();
    }

    public String getName(int id) {
        int totalSize = size();
        if (invalidUnit(id)) return "Outrealm Unit #" + (id - totalSize + 1);
        return getUnit(id).getName();
    }

    public int[] getAddition(int id) {
        int[] addition = new int[8];
        if (invalidUnit(id)) id = DEFAULT_UNIT;
        int[] all = getUnit(id).getStatAdditions();
        System.arraycopy(all, 0, addition, 0, addition.length);
        return addition;
    }

    public int[] getModifiers(int id) {
        int[] modifiers = new int[8];
        if (invalidUnit(id)) id = DEFAULT_UNIT;
        int[] all = getUnit(id).getStatModifiers();
        System.arraycopy(all, 0, modifiers, 0, modifiers.length);
        return modifiers;
    }

    public int[] getReclassM(int id) {
        if (invalidUnit(id)) return units.get(DEFAULT_UNIT).getClassMale();
        return getUnit(id).getClassMale();
    }

    public int[] getReclassF(int id) {
        if (invalidUnit(id)) return units.get(DEFAULT_UNIT).getClassFemale();
        return getUnit(id).getClassFemale();
    }

    public List<Integer> getSkills(int id) {
        if (invalidUnit(id)) return new ArrayList<>();
        return getUnit(id).getSkills();
    }

    public int supportCount(int id) {
        if (invalidUnit(id)) return 0;
        return getSupportUnits(id).length;
    }

    public int[] getSupportUnits(int id) {
        if (invalidUnit(id)) return units.get(DEFAULT_UNIT).getSupportUnits();
        return getUnit(id).getSupportUnits();
    }

    public int[] getSupportTypes(int id) {
        if (invalidUnit(id)) return units.get(DEFAULT_UNIT).getSupportTypes();
        return getUnit(id).getSupportTypes();
    }

    public List<Integer> getFlags(int id) {
        if (invalidUnit(id)) return units.get(DEFAULT_UNIT).getFlags();
        return getUnit(id).getFlags();
    }

    public boolean hasFlag(int id, int flag) {
        if (invalidUnit(id)) return false;
        return getFlags(id).contains(flag);
    }

    public boolean isFemale(int id) {
        if (invalidUnit(id)) return false;
        return hasFlag(id, 0);
    }

    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            names.add(units.get(i).getName());
        }
        return names;
    }

    public List<Integer> getEinSkills(int logId) {
        if (invalidEinherjar(logId)) return new ArrayList<>();
        return getEinherjar(logId).getSkills();
    }

    public List<String> getEinherjarNames() {
        List<String> names = new ArrayList<>();
        for (EinherjarModel einherjarModel : einherjar) {
            names.add(einherjarModel.getName());
        }
        return names;
    }

    public boolean invalidUnit(int id) {
        return id < 0 || id >= size();
    }

    public boolean invalidEinherjar(int logId) {
        for (EinherjarModel einherjarModel : einherjar) {
            if (einherjarModel.getLogId() == logId) return false;
        }
        return true;
    }

    public int size() {
        return units.size();
    }

    public void readUnits() {
        String path = "/com/danius/fireeditor/database/";
        String xmlFilePath = path + "units.xml";
        units = new ArrayList<>();

        try (InputStream is = Characters.class.getResourceAsStream(xmlFilePath)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + xmlFilePath);
            }

            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(is);
            Element rootElement = document.getRootElement();

            String[] stats = new String[]{"hp", "str", "mag", "skl", "spd", "lck", "def", "res", "mov"};
            // Iterate through character elements in the XML
            for (Element characterElement : rootElement.getChildren("unit")) {
                UnitModel unit = new UnitModel();

                // Parse attributes from the XML
                unit.setId(Integer.parseInt(characterElement.getAttributeValue("id")));
                unit.setName(characterElement.getAttributeValue("name"));

                //Additions
                Element elemAdditions = characterElement.getChild("additions");
                int[] addition = new int[9];
                for (int i = 0; i < addition.length; i++) {
                    String value = elemAdditions.getAttributeValue(stats[i]);
                    addition[i] = Integer.parseInt(value);
                }
                unit.setStatAdditions(addition);

                //Modifiers
                Element elemModif = characterElement.getChild("modifiers");
                int[] modif = new int[9];
                for (int i = 0; i < modif.length; i++) {
                    modif[i] = Integer.parseInt(elemModif.getAttributeValue(stats[i]));
                }
                unit.setStatModifiers(modif);

                //Male Reclasses
                Element elemClassMale = characterElement.getChild("mClasses");
                List<Integer> mClasses = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    int value = Integer.parseInt(elemClassMale.getAttributeValue("class" + (i + 1)));
                    if (value >= 0) mClasses.add(value);
                }
                int[] mClassesArray = mClasses.stream().mapToInt(Integer::intValue).toArray();
                unit.setClassMale(mClassesArray);

                //Female Reclasses
                Element elemClassFemale = characterElement.getChild("fClasses");
                List<Integer> fClasses = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    int value = Integer.parseInt(elemClassFemale.getAttributeValue("class" + (i + 1)));
                    if (value >= 0) fClasses.add(value);
                }
                int[] fClassesArray = fClasses.stream().mapToInt(Integer::intValue).toArray();
                unit.setClassFemale(fClassesArray);

                //Recruited Skills
                Element elemSkill = characterElement.getChild("skills");
                List<Integer> skills = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    int value = Integer.parseInt(elemSkill.getAttributeValue("skill" + (i + 1)));
                    if (value > 0) skills.add(value);
                }
                unit.setSkills(skills);

                //Supports
                Element elemSupports = characterElement.getChild("supports");
                int[] supportUnits;
                int[] supportTypes;
                List<Element> supportElements = elemSupports.getChildren("support");
                supportUnits = new int[supportElements.size()];
                supportTypes = new int[supportElements.size()];

                for (int i = 0; i < supportElements.size(); i++) {
                    Element supportElement = supportElements.get(i);
                    int supportId = Integer.parseInt(supportElement.getAttributeValue("unit"));
                    int supportType = Integer.parseInt(supportElement.getAttributeValue("type"));
                    supportUnits[i] = supportId;
                    supportTypes[i] = supportType;
                }
                unit.setSupportUnits(supportUnits);
                unit.setSupportTypes(supportTypes);

                //Flags
                Element elemFlags = characterElement.getChild("flags");
                List<Element> flagsElements = elemFlags.getChildren("flag");
                List<Integer> flags = new ArrayList<>();
                for (Element flagElement : flagsElements) {
                    int flagValue = Integer.parseInt(flagElement.getText());
                    // Add the integer value to the list
                    flags.add(flagValue);
                }
                unit.setFlags(flags);

                units.add(unit);
            }
        } catch (IOException | JDOMException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void readEinherjar() {
        String path = "/com/danius/fireeditor/database/";
        String xmlFilePath = path + "einherjar.xml";
        einherjar = new ArrayList<>();
        try (InputStream is = Characters.class.getResourceAsStream(xmlFilePath)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + xmlFilePath);
            }
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(is);
            Element rootElement = document.getRootElement();
            // Iterate through character elements in the XML
            for (Element characterElement : rootElement.getChildren("unit")) {
                EinherjarModel unit = new EinherjarModel();

                // Parse attributes from the XML
                unit.setLogId(Integer.parseInt(characterElement.getAttributeValue("logId")));
                unit.setName(characterElement.getAttributeValue("name"));

                //Recruited Skills
                Element elemSkill = characterElement.getChild("skills");
                List<Integer> skills = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    int value = Integer.parseInt(elemSkill.getAttributeValue("skill" + (i + 1)));
                    if (value > 0) skills.add(value);
                }
                unit.setSkills(skills);

                //Flags
                Element elemFlags = characterElement.getChild("flags");
                List<Element> flagsElements = elemFlags.getChildren("flag");
                List<Integer> flags = new ArrayList<>();
                for (Element flagElement : flagsElements) {
                    int flagValue = Integer.parseInt(flagElement.getText());
                    // Add the integer value to the list
                    flags.add(flagValue);
                }
                unit.setFlags(flags);

                einherjar.add(unit);
            }
        } catch (IOException | JDOMException ex) {
            throw new RuntimeException(ex);
        }
    }


}
