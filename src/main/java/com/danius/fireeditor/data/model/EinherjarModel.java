package com.danius.fireeditor.data.model;

import java.util.List;

public class EinherjarModel {

    private int logId;
    private String name;
    private List<Integer> skills;
    private List<Integer> flags;

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

}
