package com.danius.fireeditor.data.model;

public class ItemModel {
    private int id;
    private String name;
    private int uses;
    private int might;
    private int hit;
    private int crit;
    private int[] buffs;

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

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }

    public int getMight() {
        return might;
    }

    public void setMight(int might) {
        this.might = might;
    }

    public int getHit() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public int getCrit() {
        return crit;
    }

    public void setCrit(int crit) {
        this.crit = crit;
    }

    public int[] getBuffs() {
        return buffs;
    }

    public void setBuffs(int[] buffs) {
        this.buffs = buffs;
    }
}
