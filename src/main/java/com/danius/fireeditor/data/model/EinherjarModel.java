package com.danius.fireeditor.data.model;

import javafx.scene.paint.Color;

import java.util.List;

public class EinherjarModel {

    private int logId;
    private int unitId;
    private String name;
    private int sprite;
    private List<Integer> skills;
    private List<Integer> flags;
    private List<Integer> items;
    private List<Integer> growth;
    private List<Integer> weaponExp;
    private int avatarClass;
    private int asset;
    private int flaw;
    private int build;
    private int face;
    private int hair;
    private int voice;
    private boolean isFemale;
    private Color hairColor;
    private List<String> textEn;
    private List<String> textJp;

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

    public int getHair() {
        return hair;
    }

    public void setHair(int hair) {
        this.hair = hair;
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

    public int getSprite() {
        return sprite;
    }

    public void setSprite(int sprite) {
        this.sprite = sprite;
    }

    public List<Integer> getItems() {
        return items;
    }

    public void setItems(List<Integer> items) {
        this.items = items;
    }

    public List<Integer> getGrowth() {
        return growth;
    }

    public void setGrowth(List<Integer> growth) {
        this.growth = growth;
    }

    public List<Integer> getWeaponExp() {
        return weaponExp;
    }

    public void setWeaponExp(List<Integer> weaponExp) {
        this.weaponExp = weaponExp;
    }

    public String getLanguageName(boolean isWest) {
        if (isWest) return textEn.get(0);
        else return textJp.get(0);
    }

    public String getLanguageGreeting(boolean isWest) {
        if (isWest) return textEn.get(1);
        else return textJp.get(1);
    }

    public String getLanguageChallenge(boolean isWest) {
        if (isWest) return textEn.get(2);
        else return textJp.get(2);
    }

    public String getLanguageRecruit(boolean isWest) {
        if (isWest) return textEn.get(3);
        else return textJp.get(3);
    }

    public void setTextEn(List<String> textEn) {
        this.textEn = textEn;
    }

    public void setTextJp(List<String> textJp) {
        this.textJp = textJp;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public void setHairColor(Color hairColor) {
        this.hairColor = hairColor;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    @Override
    public String toString() {
        return getName();
    }
}
