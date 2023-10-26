package com.danius.fireeditor.savefile.units.mainblock;

import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.model.MiscDb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RawSkill {

    public static final int MAX_SKILL = 0x66;
    public String skillString;

    public RawSkill(){
        String path = Constants.RES_BLOCK + "rawUnitSkill";
        try {
            byte[] bytes = Objects.requireNonNull(RawSkill.class.getResourceAsStream(path)).readAllBytes();
            this.skillString = byteArrayToBinaryString(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public RawSkill(byte[] blockBytes) {
        this.skillString = byteArrayToBinaryString(blockBytes);
    }

    public byte[] bytes() {
        return binaryStringToByteArray(skillString);
    }

    public List<Integer> getLearnedSkills() {
        List<Integer> learnedSkills = new ArrayList<>();
        for (int i = 1; i < skillString.length(); i++) {
            if (skillString.charAt(i) == '1') learnedSkills.add(i);
        }
        return learnedSkills;
    }

    public boolean isSkillSet(int slot){
        return skillString.charAt(slot) == '1';
    }

    /*
    Sets all the learned skills to true or false
     */
    public void setAll(boolean learn) {
        char[] charArray = skillString.toCharArray();
        charArray[0] = '0';
        for (int i = 1; i < skillString.length() - 1; i++) {
            setLearnedSkill(learn, i);
        }
    }

    /*
    Sets to true or false a learned skill
     */
    public void setLearnedSkill(Boolean isLearned, int skill) {
        char[] chars = skillString.toCharArray();
        if (isLearned) chars[skill] = '1';
        else chars[skill] = '0';
        skillString = new String(chars);
    }

    public String report() {
        String report = "";
        report += "Learned Skills (" + skillCount() + "): ";
        for (Integer learnedSkill : getLearnedSkills()) {
            report += MiscDb.skillNames.get(learnedSkill) + ", ";
        }
        report = report.substring(0, report.length() - 2);
        return report;
    }


    /*
    Converts a byte block to binary and reverses it to match the skill order
     */
    private static String byteArrayToBinaryString(byte[] bytes) {
        StringBuilder binaryString = new StringBuilder();
        for (byte b : bytes) {
            StringBuilder byteString = new StringBuilder();
            for (int i = 7; i >= 0; i--) {
                byteString.append((b & (1 << i)) != 0 ? "1" : "0");
            }
            binaryString.append(byteString.reverse());
        }
        return binaryString.toString();
    }

    /*
    Un-reverses the order of byteArrayToBinaryString to properly write the block to the unit
     */
    private static byte[] binaryStringToByteArray(String binaryString) {
        int length = binaryString.length();
        byte[] byteArray = new byte[length / 8];
        for (int i = 0; i < length; i += 8) {
            String byteString = binaryString.substring(i, i + 8);
            StringBuilder reversedByteString = new StringBuilder(byteString).reverse();
            try {
                byte b = (byte) Integer.parseInt(reversedByteString.toString(), 2);
                byteArray[i / 8] = b;
            } catch (NumberFormatException e) {
                System.err.println("Invalid binary digit found: " + reversedByteString);
                // Handle the error case as needed (e.g., assign a default value, skip the byte, etc.)
            }
        }
        return byteArray;
    }


    public int skillCount() {
        return getLearnedSkills().size();
    }

    public int length() {
        return binaryStringToByteArray(skillString).length;
    }
}
