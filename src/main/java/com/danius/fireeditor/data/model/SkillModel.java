package com.danius.fireeditor.data.model;

public class SkillModel {

    private int id;
    private String name;
    private boolean inherit;
    private boolean item;

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

    @Override
    public String toString() {
        return getName();
    }
}
