package com.danius.fireeditor.data.model;

import java.util.List;

public class EinherjarModel {

    private int logId;
    private String name;
    private List<Integer> skills;
    private List<Integer> flags;
    private int avatarClass;
    private int asset;
    private int flaw;
    private int build;
    private int face;
    private int color;
    private int voice;
    private boolean isFemale;
    private String hairColor;

    public EinherjarModel() {

    }

    public int getLogId() {
        return this.logId;
    }

    public void setLogId(int id) {
        this.logId = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getFlags() {
        return this.flags;
    }

    public void setFlags(List<Integer> list) {
        this.flags = list;
    }

    public List<Integer> getSkills() {
        return this.skills;
    }

    public void setSkills(List<Integer> list) {
        this.skills = list;
    }

    public int getAvatarClass() {
        return avatarClass;
    }

    public void setAvatarClass(int avatarClass) {
        this.avatarClass = avatarClass;
    }

    public int getAsset() {
        return asset;
    }

    public void setAsset(int asset) {
        this.asset = asset;
    }

    public int getFlaw() {
        return flaw;
    }

    public void setFlaw(int flaw) {
        this.flaw = flaw;
    }

    public int getBuild() {
        return build;
    }

    public void setBuild(int build) {
        this.build = build;
    }

    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getVoice() {
        return voice;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }

    public boolean isFemale() {
        return isFemale;
    }

    public void setFemale(boolean female) {
        isFemale = female;
    }

    public String getHairColor() {
        return hairColor;
    }

    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }

    @Override
    public String toString() {
        return getName();
    }
}
