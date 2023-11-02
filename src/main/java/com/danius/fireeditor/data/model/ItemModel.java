package com.danius.fireeditor.data.model;

public class ItemModel {
    private int id;
    private int type1;
    private String name;
    private int uses;
    private int might;
    private int hit;
    private int crit;
    private int[] buffs;
    private boolean isMagic; //Used for faire buffs only

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

    public int getType1() {
        return type1;
    }

    public void setType1(int type1) {
        this.type1 = type1;
    }

    public boolean isMagic() {
        return isMagic;
    }

    public void setMagic(boolean magic) {
        isMagic = magic;
    }

    @Override
    public String toString() {
        return getName();
    }
}
