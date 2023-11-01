package com.danius.fireeditor.data.model;

import java.util.List;

public class UnitModel {

    private int id;
    private String name;
    private int[] statAdditions;
    private int[] statModifiers;
    private List<Integer> classMale;
    private List<Integer> classFemale;
    private List<Integer> skills;
    private int[] supportUnits;
    private int[] supportTypes;
    private List<Integer> flags;
    private int parent;
    private boolean isAvatar;
    private boolean hasCustomHair;
    private String hairColor;
    private boolean isPlayable;

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

    public List<Integer> getClassMale() {
        return classMale;
    }

    public void setClassMale(List<Integer> classMale) {
        this.classMale = classMale;
    }

    public List<Integer> getClassFemale() {
        return classFemale;
    }

    public void setClassFemale(List<Integer> classFemale) {
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

    public boolean isAvatar() {
        return isAvatar;
    }

    public void setIsAvatar(boolean avatar) {
        isAvatar = avatar;
    }

    public boolean hasCustomHair() {
        return hasCustomHair;
    }

    public void setHasCustomHair(boolean hasCustomHair) {
        this.hasCustomHair = hasCustomHair;
    }

    public String getHairColor() {
        return hairColor;
    }

    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }

    public boolean isPlayable() {
        return isPlayable;
    }

    public void setPlayable(boolean playable) {
        isPlayable = playable;
    }

    @Override
    public String toString() {
        return getName();
    }
}
