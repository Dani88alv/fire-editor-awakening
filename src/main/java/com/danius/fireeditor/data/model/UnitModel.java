package com.danius.fireeditor.data.model;

import java.util.List;

public class UnitModel {

    private int id;
    private String name;
    private int[] statAdditions;
    private int[] statModifiers;
    private int[] classMale;
    private int[] classFemale;
    private List<Integer> skills;
    private int[] supportUnits;
    private int[] supportTypes;
    private List<Integer> flags;
    private int parent;

    public UnitModel() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getStatAdditions() {
        return statAdditions;
    }

    public void setStatAdditions(int[] statAdditions) {
        this.statAdditions = statAdditions;
    }

    public int[] getStatModifiers() {
        return statModifiers;
    }

    public void setStatModifiers(int[] statModifiers) {
        this.statModifiers = statModifiers;
    }

    public int[] getClassMale() {
        return classMale;
    }

    public void setClassMale(int[] classMale) {
        this.classMale = classMale;
    }

    public int[] getClassFemale() {
        return classFemale;
    }

    public void setClassFemale(int[] classFemale) {
        this.classFemale = classFemale;
    }

    public List<Integer> getSkills() {
        return skills;
    }

    public void setSkills(List<Integer> skills) {
        this.skills = skills;
    }

    public int[] getSupportUnits() {
        return supportUnits;
    }

    public void setSupportUnits(int[] supportUnits) {
        this.supportUnits = supportUnits;
    }

    public int[] getSupportTypes() {
        return supportTypes;
    }

    public void setSupportTypes(int[] supportTypes) {
        this.supportTypes = supportTypes;
    }

    public List<Integer> getFlags() {
        return this.flags;
    }

    public void setFlags(List<Integer> flags) {
        this.flags = flags;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }
}
