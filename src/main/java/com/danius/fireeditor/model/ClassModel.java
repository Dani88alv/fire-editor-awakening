package com.danius.fireeditor.model;

import java.util.ArrayList;
import java.util.List;

public class ClassModel {
    private int id;
    private String name;
    private int[] skills;
    private int[] promoted;
    private int[] statsBase;
    private int[] statsMax;
    private List<Integer> traitFlags;

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


}
