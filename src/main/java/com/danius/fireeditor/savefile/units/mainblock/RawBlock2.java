package com.danius.fireeditor.savefile.units.mainblock;

import com.danius.fireeditor.data.SkillDb;
import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.data.MiscDb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RawBlock2 {
    private byte[] skills; //Equipped Skills
    private byte[] weaponExp;

    public RawBlock2() {
        String path = Constants.RES_BLOCK + "rawUnitBlock2";
        try {
            byte[] bytes = Objects.requireNonNull(RawBlock2.class.getResourceAsStream(path)).readAllBytes();
            initialize(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public RawBlock2(byte[] blockBytes) {
        initialize(blockBytes);
    }

    public void initialize(byte[] blockBytes) {
        for (int i = 0; i <= 4; i++) {
            this.skills = Arrays.copyOfRange(blockBytes, 0x0, 5 * 2);
        }
        this.weaponExp = Arrays.copyOfRange(blockBytes, skills.length, blockBytes.length);
    }

    public byte[] bytes() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        fixCurrentSkills();
        outputStream.write(skills);
        outputStream.write(weaponExp);
        return outputStream.toByteArray();
    }

    /*
    Retrieves a list of the current equipped skills, 5 slots
     */
    public int[] getCurrentSkills() {
        int point = 0x0;
        return new int[]{skills[point] & 0xFF, skills[point + 2] & 0xFF,
                skills[point + 4] & 0xFF, skills[point + 6] & 0xFF, skills[point + 8] & 0xFF};
    }

    public void setCurrentSkill(int skill, int slot) {
        int point = 0x0;
        skills[point + (slot * 2)] = (byte) (skill & 0xFF);
    }

    //If Non-Active Skill is before any valid skills, the skill menu will be glitched
    public void fixCurrentSkills() {
        int[] activeSkills = getCurrentSkills();
        //The array if fixed
        for (int i = 0; i < activeSkills.length - 1; i++) {
            if (activeSkills[i] == 0) {
                for (int j = i + 1; j < activeSkills.length; j++) {
                    if (activeSkills[j] != 0) {
                        // Swap the elements
                        int temp = activeSkills[i];
                        activeSkills[i] = activeSkills[j];
                        activeSkills[j] = temp;
                        break;
                    }
                }
            }
        }
        //The values are updated
        for (int i = 0; i < activeSkills.length; i++) setCurrentSkill(activeSkills[i], i);
    }


    public void resetCurrentSkills() {
        for (int i = 0; i < 5; i++) setCurrentSkill(0, i);
    }

    /*
    Retrieves a list of the weapon exp values
    Order: Sword, Lance, Axe, Bow, Tome, Stave
    Dragonstones do not have weapon level
     */
    public int[] getWeaponExp() {
        return new int[]{
                weaponExp[0] & 0xFF, weaponExp[1] & 0xFF, weaponExp[2] & 0xFF,
                weaponExp[3] & 0xFF, weaponExp[4] & 0xFF, weaponExp[5] & 0xFF};
    }

    public void setWeaponExp(int exp, int slot) {
        weaponExp[slot] = (byte) (exp & 0xFF);
    }

    public void setMaxWeaponExp() {
        int exp = 90;
        for (int i = 0; i <= 5; i++) setWeaponExp(exp, i);
    }

    public boolean hasSkillEquipped(int skillId) {
        for (int i = 0; i < 5; i++) {
            if (getCurrentSkills()[i] == skillId) return true;
        }
        return false;
    }

    public boolean hasSkillsEquipped(List<Integer> skillIds) {
        int[] currentSkills = getCurrentSkills();
        for (int skill : currentSkills) {
            if (skillIds.contains(skill)) {
                return true;
            }
        }
        return false;
    }

    public String report() {
        StringBuilder report = new StringBuilder();
        report.append("Weapon EXP: ").append(Arrays.toString(getWeaponExp()));
        report.append("\n" + "Equipped Skills: ");
        for (int i = 0; i <= 4; i++) {
            report.append(SkillDb.getSkillNames().get(getCurrentSkills()[i])).append(", ");
        }
        report = new StringBuilder(report.substring(0, report.length() - 2));
        return report.toString();
    }

    public int length() {
        return skills.length + weaponExp.length;
    }
}
