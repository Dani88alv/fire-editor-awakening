package com.danius.fireeditor.data.model;

import java.util.List;

public class SkillModel {

    private int id;
    private String name;
    private boolean inherit;
    private boolean item;
    private List<Integer> forceFlags;
    private String forceGender;

    public SkillModel() {

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

    public boolean isInherit() {
        return inherit;
    }

    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

    public boolean isItem() {
        return item;
    }

    public void setItem(boolean item) {
        this.item = item;
    }

    public List<Integer> getForceFlags() {
        return forceFlags;
    }

    public void setForceFlags(List<Integer> forceFlags) {
        this.forceFlags = forceFlags;
    }

    public boolean isForcingGender() {
        return !forceGender.equals("");
    }

    public boolean isForcingFemale() {
        return forceGender.equals("F");
    }

    public boolean isForcingMale() {
        return forceGender.equals("M");
    }

    public void setForceGender(String forceGender) {
        this.forceGender = forceGender;
    }

    @Override
    public String toString() {
        return getName();
    }
}
