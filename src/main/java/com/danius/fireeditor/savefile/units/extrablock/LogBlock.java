package com.danius.fireeditor.savefile.units.extrablock;

import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.data.MiscDb;
import com.danius.fireeditor.util.Hex;
import javafx.scene.paint.Color;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import static com.danius.fireeditor.util.Hex.*;

public class LogBlock {
    public static final int MESSAGE_US = 0x42;
    public static final int MESSAGE_JP = 0x22;
    public int NAME_CHARACTERS;
    public int MESSAGE_CHARACTERS;
    public boolean isWest;
    public byte[] nameBlock; //Stores the name of the character
    public byte[] mainBlock; //Stores all the attributes
    public byte[] textStreet;
    public byte[] textGreeting;
    public byte[] textChallenge;
    public byte[] textRecruit;
    public byte[] footer;

    public static final int[] DLC_FACE_ID = new int[]{
            11, 16, 24, 56, 67, 72, 80, 91,
            96, 105, 114, 121, 128, 136, 144, 155
    };

    public LogBlock(byte[] blockBytes) {
        initialize(blockBytes);
    }

    public LogBlock() {
        String path = Constants.RES_BLOCK + "rawUnitLog";
        byte[] bytes = new byte[0];
        try {
            bytes = Objects.requireNonNull(LogBlock.class.getResourceAsStream(path)).readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        initialize(bytes);
    }

    //Checks if the name and build are empty to know if it is a temporal logbook block
    public boolean isBlank() {
        int[] build = getFullBuild();
        return getName().equals("") && build[0] == 0 && build[1] == 0 && build[2] == 0 && build[3] == 0;
    }

    /*
    Combines the blocks
     */
    public byte[] getBytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(nameBlock);
            outputStream.write(mainBlock);
            outputStream.write(textStreet);
            outputStream.write(textGreeting);
            outputStream.write(textChallenge);
            outputStream.write(textRecruit);
            outputStream.write(footer);
        } catch (Exception e) {
            throw new RuntimeException("Unable to compile logbook block: " + getLogId());
        }
        return outputStream.toByteArray();
    }

    private void initialize(byte[] blockBytes) {
        //The region is checked
        this.isWest = (blockBytes.length == Unit.LBLOCK_SIZE_US);
        //The block is split
        int nameSize = (isWest) ? 0x1A : 0xE; //Name block size
        this.nameBlock = Arrays.copyOfRange(blockBytes, 0x0, nameSize);
        this.mainBlock = Arrays.copyOfRange(blockBytes, nameBlock.length, nameBlock.length + 0x65);
        //StreetPass & Card Messages
        int offset = nameBlock.length + mainBlock.length;
        int textSize = (isWest) ? MESSAGE_US : MESSAGE_JP;
        this.textStreet = Arrays.copyOfRange(blockBytes, offset, offset + textSize);
        offset += textStreet.length;
        this.textGreeting = Arrays.copyOfRange(blockBytes, offset, offset + textSize);
        offset += textGreeting.length;
        this.textChallenge = Arrays.copyOfRange(blockBytes, offset, offset + textSize);
        offset += textChallenge.length;
        this.textRecruit = Arrays.copyOfRange(blockBytes, offset, offset + textSize);
        offset += textRecruit.length;
        this.footer = Arrays.copyOfRange(blockBytes, offset, blockBytes.length);
        //Character limit
        this.NAME_CHARACTERS = (nameBlock.length / 2) - 1;
        this.MESSAGE_CHARACTERS = (textStreet.length / 2) - 1;
    }

    public void setTerminator(boolean set) {
        footer[0x0] = (byte) ((set) ? 1 : 0);
    }

    public String getName() {
        //There is no character limit in US/EU, it is based on the character size, it can be
        // a limit of 9 characters or higher, reaching 12 at max for US
        byte[] nameArray = Arrays.copyOfRange(nameBlock, 0x0, nameBlock.length);
        return Hex.byteArrayToString(nameArray);
    }

    public void setName(String name) {
        if (name.length() > NAME_CHARACTERS) name = name.substring(0, NAME_CHARACTERS);
        byte[] nameBytes = Hex.stringToByteArray(name, NAME_CHARACTERS * 2);
        System.arraycopy(nameBytes, 0, nameBlock, 0, nameBytes.length);
    }

    public String getTextStreet() {
        byte[] nameArray = Arrays.copyOfRange(textStreet, 0x0, (textStreet.length - 1) * 2);
        return Hex.byteArrayToString(nameArray);
    }

    public void setTextStreet(String name) {
        if (name.length() > MESSAGE_CHARACTERS) name = name.substring(0, MESSAGE_CHARACTERS);
        byte[] nameBytes = Hex.stringToByteArray(name, MESSAGE_CHARACTERS * 2);
        System.arraycopy(nameBytes, 0, textStreet, 0, nameBytes.length);
    }

    public String getTextGreeting() {
        byte[] nameArray = Arrays.copyOfRange(textGreeting, 0x0, (textGreeting.length - 1) * 2);
        return Hex.byteArrayToString(nameArray);
    }

    public void setTextGreeting(String name) {
        if (name.length() > MESSAGE_CHARACTERS) name = name.substring(0, MESSAGE_CHARACTERS);
        byte[] nameBytes = Hex.stringToByteArray(name, MESSAGE_CHARACTERS * 2);
        System.arraycopy(nameBytes, 0, textGreeting, 0, nameBytes.length);
    }

    public String getTextChallenge() {
        byte[] nameArray = Arrays.copyOfRange(textChallenge, 0x0, (textChallenge.length - 1) * 2);
        return Hex.byteArrayToString(nameArray);
    }

    public void setTextChallenge(String name) {
        if (name.length() > MESSAGE_CHARACTERS) name = name.substring(0, MESSAGE_CHARACTERS);
        byte[] nameBytes = Hex.stringToByteArray(name, MESSAGE_CHARACTERS * 2);
        System.arraycopy(nameBytes, 0, textChallenge, 0, nameBytes.length);
    }

    public String getTextRecruit() {
        byte[] nameArray = Arrays.copyOfRange(textRecruit, 0x0, (textRecruit.length - 1) * 2);
        return Hex.byteArrayToString(nameArray);
    }

    public void setTextRecruit(String name) {
        if (name.length() > MESSAGE_CHARACTERS) name = name.substring(0, MESSAGE_CHARACTERS);
        byte[] nameBytes = Hex.stringToByteArray(name, MESSAGE_CHARACTERS * 2);
        System.arraycopy(nameBytes, 0, textRecruit, 0, nameBytes.length);
    }

    /*
    Changes a Logbook unit to West or JP
     */
    public void changeRegion(boolean isWest) {
        int nameSize = (isWest) ? 0x1A : 0xE;
        nameBlock = Hex.changeSizeArray(nameBlock, nameSize);
        int textSize = (isWest) ? MESSAGE_US : MESSAGE_JP;
        textStreet = Hex.changeSizeArray(textStreet, textSize);
        textGreeting = Hex.changeSizeArray(textGreeting, textSize);
        textChallenge = Hex.changeSizeArray(textChallenge, textSize);
        textRecruit = Hex.changeSizeArray(textRecruit, textSize);
        this.isWest = isWest;
        this.NAME_CHARACTERS = (nameBlock.length / 2) - 1;
        this.MESSAGE_CHARACTERS = (textStreet.length / 2) - 1;
    }


    //0x0-0x1
    public int[] getAssetFlaw() {
        int point = 0x0;
        return new int[]{
                mainBlock[point] & 0xFF,
                mainBlock[point + 1] & 0xFF};
    }

    public void setAsset(int asset) {
        int point = 0x0;
        mainBlock[point] = (byte) (asset & 0xFF);
    }

    public void setFlaw(int flaw) {
        int point = 0x1;
        mainBlock[point] = (byte) (flaw & 0xFF);
    }

    /*
    Retrieves a list of the Build, Face, Hair, Voice and Gender
    SpotPass & DLC units have their voice to 04
    0x2-0x5 / 0xA
    */
    public int[] getFullBuild() {
        int buildPoint = 0x3;
        int voicePoint = 0xA;
        int[] look = new int[5];
        look[0] = mainBlock[buildPoint] & 0xFF; //Build
        look[1] = mainBlock[buildPoint + 1] & 0xFF; //Face
        look[2] = mainBlock[buildPoint + 2] & 0xFF; //Hair
        look[3] = mainBlock[voicePoint] & 0xFF; //Voice
        look[4] = mainBlock[buildPoint - 1] & 0xFF; //Gender
        return look;
    }

    public void setBuild(int slot, int value) {
        int buildPoint = 0x3;
        mainBlock[buildPoint + slot] = (byte) (value & 0xFF);
    }

    public void setVoice(int value) {
        int voicePoint = 0xA;
        mainBlock[voicePoint] = (byte) (value & 0xFF);
    }

    public void setGender(boolean female) {
        int point = 0x2;
        if (female) mainBlock[point] = (byte) (1);
        else mainBlock[point] = (byte) (0);
    }

    public Color getHairColorFx() {
        int pointer = 0x6;
        return Hex.getColor(mainBlock, pointer);
    }

    public void setHairColorFx(Color color) {
        int pointer = 0x6;
        Hex.setColor(mainBlock, pointer, color);
    }


    //0xA: Voice

    /*
    Retrieves the birth month and birthday
    SpotPass and DLC units DO NOT have a birthday set, it's 00
    0xB-0xC
     */
    public int[] getBirthday() {
        int point = 0xB;
        return new int[]{
                mainBlock[point],
                mainBlock[point + 1]
        };
    }

    public void setBirthday(int day, int month) {
        int point = 0xB;
        mainBlock[point] = (byte) (day & 0xFF);
        mainBlock[point + 1] = (byte) (month & 0xFF);
    }

    /*
    Used to check if a unit can be updated or recruited from the Logbook
    SpotPass & DLC units have their own ID
    The ID also handles the SpotPass artworks
    0xD-0x19
     */
    public String getLogId() {
        int point = 0xD;
        byte[] id = new byte[13];
        System.arraycopy(mainBlock, point, id, 0, 13);

        byte[] reversedArray = new byte[id.length];
        for (int i = 0; i < id.length; i++) {
            reversedArray[i] = id[id.length - 1 - i];
        }

        return Hex.byteArrayToHexString(reversedArray);
    }

    //Checks if the logbook ID matches the Einherjar IDs, excludes DLC units
    public boolean hasSpotPassPortrait() {
        String logString = getLogId();
        //Checks large ID numbers, from avatars
        for (int i = 0; i < logString.length() - 2; i++) {
            if (logString.charAt(i) != '0') return false;
        }
        int logId = getLogIdLastByte();
        //Checks the DLC IDs
        //for (int number : DLC_LOG_ID) if (number == logId) return true;
        //Checks the regular Einherjar IDs
        return logId <= 120;
    }

    //Checks if the logbook ID matches the Einherjar IDs, including DLC units
    public boolean hasEinherjarId() {
        String logString = getLogId();
        //Checks large ID numbers, from avatars
        for (int i = 0; i < logString.length() - 2; i++) {
            if (logString.charAt(i) != '0') return false;
        }
        return true;
    }

    public int getLogIdLastByte() {
        String logString = getLogId();
        String lastTwoChars = logString.substring(logString.length() - 2).toUpperCase();
        short lastShort = Short.parseShort(lastTwoChars, 16);
        return lastShort & 0xFF;
    }


    public void setLogId(String hexString) {
        int point = 0xD;
        //Add remaining ceros to the left
        StringBuilder sb = new StringBuilder(hexString);
        while (sb.length() < (13 * 2)) {
            sb.insert(0, '0');
        }
        //Convert it to a byte array
        byte[] logId = Hex.hexStringToByteArray(String.valueOf(sb));
        //The byte order is reversed
        byte[] reversedArray = new byte[logId.length];
        for (int i = 0; i < logId.length; i++) {
            reversedArray[i] = logId[logId.length - 1 - i];
        }
        System.arraycopy(reversedArray, 0, mainBlock, point, 13);
    }

    public void setLogIdRandom() {
        StringBuilder logId = new StringBuilder();
        try {
            for (int i = 0; i < 26; i++) {
                Random random = new Random();
                int randomNumber = random.nextInt(16); // Generates a random number between 0x0 - 0xF
                String hexValue = Integer.toHexString(randomNumber); // Convert the random number to hexadecimal
                logId.append(hexValue);
            }
            setLogId(String.valueOf(logId));
        } catch (Exception e) {
            System.out.println(logId);
        }
    }


    //0x1A-0x1C: ?

    /*
    Avatar units can be MU or Einherjar/DLC
    Einherjar uses their own artwork
    0x1D
     */
    public boolean isEinherjar() {
        return hasBitFlag(mainBlock[0x1D], 1);
    }

    /*
    Sets to Einherjar or MU a unit
     */
    public void setEinherjar(boolean set) {
        setBitFlag(mainBlock, 0x1D, 1, set);
    }


    public boolean hasFaceDlc() {
        int face = getFullBuild()[1];
        return face > 4;
    }

    //0x1E: Card offset second digit?

    /*
    The card details displayed on the Logbook and shares with StreetPass
    0x1F-0x24
     */
    public int[] getProfileCard() {
        int point = 0x1F;
        return new int[]{
                mainBlock[point] & 0xFF, //Class
                mainBlock[point + 0x1] & 0xFF, //Expression
                mainBlock[point + 0x2] & 0xFF, //Trait
                mainBlock[point + 0x3] & 0xFF, //Home
                mainBlock[point + 0x4] & 0xFF, //Identity
                mainBlock[point + 0x5] & 0xFF //Values
        };
    }

    public void setProfileCard(int value, int slot) {
        int point = 0x1F;
        mainBlock[point + slot] = (byte) (value & 0xFF);
    }

    /*
    Retrieves the list of S-Pairings made by the user
    Only stored on MU and Logbook MU. First is male unit, second is female
    0x25-0x60
     */

    public int[] getPairing(int slot) {
        int point = 0x25;
        return new int[]{
                mainBlock[point + (slot * 2)] & 0xFF,
                mainBlock[point + (slot * 2) + 1] & 0xFF,
        };
    }

    public int pairingCount() {
        int counter = 0;
        for (int i = 0; i <= 29; i++) {
            int[] pairing = getPairing(i);
            if (pairing[0] == 0 & pairing[1] == 0) break;
            counter++;
        }
        return counter;
    }

    public void setPairingMale(int slot, int male) {
        int point = 0x25;
        mainBlock[point + (slot * 2)] = (byte) (male & 0xFF);
    }

    public void setPairingFemale(int slot, int female) {
        int point = 0x25;
        mainBlock[point + (slot * 2) + 1] = (byte) (female & 0xFF);
    }

    /*Game Mode Bitflag
    0 0x1: Casual
    1 0x2: Lunatic+
    2 0x4: Game Beaten
    3 0x8: Hidden
     */
    public boolean gameModeFlag(int slot) {
        int point = 0x61;
        return hasBitFlag(mainBlock[point], slot);
    }

    public void setGameModeFlag(int slot, boolean set) {
        int point = 0x61;
        setBitFlag(mainBlock, point, slot, set);
    }

    /*
    Card Data
    00 normal, 01 hard, 02 lunatic
     */
    public int difficulty() {
        int point = 0x62;
        return mainBlock[point] & 0xFF;
    }

    public void setDifficulty(int value) {
        int point = 0x62;
        mainBlock[point] = (byte) (value & 0xFF);
    }

    //0x63-0x64: ?

    public String report() {
        String text = "";
        //Type of unit
        text += (isEinherjar()) ? "(Einherjar) " : "(Avatar) ";
        text += "Logbook ID: " + getLogId();
        text += isWest ? " (US/EU)" : " (JP)";
        //Build
        text += "\n" + "Asset: " + MiscDb.modifNames.get(getAssetFlaw()[0]) + " Flaw: " + MiscDb.modifNames.get(getAssetFlaw()[1]);
        text += "\n" + "Build: " + Arrays.toString(getFullBuild()) + " Birthday: " + Arrays.toString(getBirthday());
        text += "\n" + "Logbook Hair: #" + getHairColorFx();
        return text;
    }


}
