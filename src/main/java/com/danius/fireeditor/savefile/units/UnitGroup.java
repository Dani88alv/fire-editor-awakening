package com.danius.fireeditor.savefile.units;

import java.util.ArrayList;
import java.util.List;

public class UnitGroup {

    public List<Unit> unitList;
    private static final int MAX_SIZE = 255;

    public UnitGroup(List<Unit> unitList) {
        this.unitList = unitList;
    }

    public UnitGroup() {
        this.unitList = new ArrayList<>();
    }

    public int size() {
        return unitList.size();
    }

    public void setUnitList(List<Unit> unitList) {
        this.unitList = unitList;
    }

    public void addUnit(Unit unit) {
        if (unitList.size() != MAX_SIZE) {
            unitList.add(unit);
            System.out.println("Added " + unit.unitName() + " " +
                    size() + "/" + MAX_SIZE);
        } else {
            System.out.println("Reached max unit capacity!");
        }
    }

    public Unit get(int slot){
        return unitList.get(slot);
    }

    public void updateUnit(Unit unit, int slot) {
        this.unitList.set(slot, unit);
    }

    public void deleteUnit(int slot) {
        if (unitList.size() > 0) {
            System.out.println("Removed " + unitList.get(slot).unitName() + " " +
                    size() + "/" + MAX_SIZE);
            this.unitList.remove(slot);
        }
    }
}
