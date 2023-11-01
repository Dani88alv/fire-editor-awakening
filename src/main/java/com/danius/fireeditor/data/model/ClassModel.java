package com.danius.fireeditor.data.model;

import java.util.ArrayList;
import java.util.List;

public class ClassModel {
    private int id;
    private String name;
    private int[] skills;
    private int[] promoted;
    private List<Integer> promotedClasses;
    private List<Integer> skillList;
    private int[] statsBase;
    private int[] statsMax;
    private List<Integer> traitFlags;
    private boolean inherit;
    private boolean enemyPortrait;
    private boolean risenPortrait;
    private boolean tacticianTree;
    private boolean isTactician;
    private boolean isDlc;

    public ClassModel() {
        traitFlags = new ArrayList<>();
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

    public int[] getSkills() {
        return skills;
    }

    public void setSkills(int[] skills) {
        this.skills = skills;
    }

    public int[] getPromoted() {
        return promoted;
    }

    public void setPromoted(int[] promoted) {
        this.promoted = promoted;
    }

    public int[] getStatsBase() {
        return statsBase;
    }

    public void setStatsBase(int[] statsBase) {
        this.statsBase = statsBase;
    }

    public int[] getStatsMax() {
        return statsMax;
    }

    public void setStatsMax(int[] statsMax) {
        this.statsMax = statsMax;
    }

    public List<Integer> getFlags() {
        return this.traitFlags;
    }

    public void setTraitFlags(List<Integer> flags) {
        this.traitFlags = flags;
    }

    public boolean canBeInherited() {
        return inherit;
    }

    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

    public List<Integer> getPromotedClasses() {
        return promotedClasses;
    }

    public void setPromotedClasses(List<Integer> promotedClasses) {
        this.promotedClasses = promotedClasses;
    }

    public List<Integer> getSkillList() {
        return skillList;
    }

    public void setSkillList(List<Integer> skillList) {
        this.skillList = skillList;
    }

    public boolean isEnemyPortrait() {
        return enemyPortrait;
    }

    public void setEnemyPortrait(boolean enemyPortrait) {
        this.enemyPortrait = enemyPortrait;
    }

    public boolean isRisenPortrait() {
        return risenPortrait;
    }

    public void setRisenPortrait(boolean risenPortrait) {
        this.risenPortrait = risenPortrait;
    }

    public boolean isTacticianTree() {
        return tacticianTree;
    }

    public void setTacticianTree(boolean tacticianTree) {
        this.tacticianTree = tacticianTree;
    }

    public boolean isTactician() {
        return isTactician;
    }

    public void setTactician(boolean tactician) {
        isTactician = tactician;
    }

    public boolean isDlc() {
        return isDlc;
    }

    public void setDlc(boolean dlc) {
        isDlc = dlc;
    }

    @Override
    public String toString() {
        return getName();
    }
}
