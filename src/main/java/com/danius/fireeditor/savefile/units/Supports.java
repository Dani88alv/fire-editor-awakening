package com.danius.fireeditor.savefile.units;

import com.danius.fireeditor.util.Names;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Supports {

   /*
   Small database of the character supports
    */

    public static int[] getSupportUnits(int unitId) {
        if (unitId >= supportCharacters().size()) return supportCharacters().get(0x35);
        return supportCharacters().get(unitId);
    }

    public static int[] getSupportTypes(int unitId) {
        if (unitId >= supportTypes().size()) return supportTypes().get(0x35);
        return supportTypes().get(unitId);
    }

    /*
    C-Pending, B-Pending, A-Pending, S-Pending, S-Rank
     */
    public static HashMap<Integer, int[]> supportValues() {
        HashMap<Integer, int[]> values = new HashMap<Integer, int[]>();
        values.put(0x0, new int[]{0x3, 0x9, 0x11, 0x12, 0x12}); //Non-Romantic
        values.put(0x1, new int[]{0x4, 0x9, 0xF, 0x15, 0x16}); //Slow
        values.put(0x2, new int[]{0x3, 0x8, 0xD, 0x13, 0x14}); //Medium
        values.put(0x3, new int[]{0x2, 0x7, 0xC, 0x11, 0x12}); //Fast
        values.put(0x4, new int[]{0x0, 0x5, 0xF, 0x10, 0x10}); //Parent Support
        return values;
    }

    /*
    Stores the ID of each valid character (starting from 1)
     */
    private static HashMap<Integer, int[]> supportCharacters() {
        HashMap<Integer, int[]> chars = new HashMap<Integer, int[]>();
        chars.put(0x00, new int[]{4, 9, 7, 6, 10, 12, 14, 15,
                22, 18, 20, 26, 31, 33, 34, 35,
                39, 42, 43, 52, 47, 48, 50, 29,
                5, 8, 11, 13, 16, 17, 19, 21,
                23, 24, 25, 27, 28, 36, 37, 38,
                44, 45, 46, 32, 51, 49, 30}); //Avatar M
        chars.put(0x01, new int[]{5, 13, 11, 8, 16, 17, 19, 21,
                23, 24, 25, 27, 28, 36, 37, 38,
                44, 45, 46, 32, 51, 49, 30, 4,
                6, 7, 9, 10, 12, 14, 15, 22,
                18, 20, 26, 31, 33, 34, 35, 39,
                42, 43, 52, 47, 48, 50, 29}); //Avatar F
        chars.put(0x02, new int[]{}); //Logbook Unit
        chars.put(0x03, new int[]{1, 2, 5, 9, 18, 13, 8, 16, 24, 6}); //Chrom
        chars.put(0x04, new int[]{2, 1, 4, 16, 6, 10, 12, 15, 9, 22, 14, 20, 18, 7, 26, 31}); //Lissa
        chars.put(0x05, new int[]{1, 2, 26, 7, 13, 5, 8, 16, 19, 17, 21, 24, 25, 11, 23, 4}); //Frederick
        chars.put(0x06, new int[]{1, 2, 22, 6, 5, 8, 16, 19, 17, 21, 24, 25, 11, 23}); //Virion
        chars.put(0x07, new int[]{2, 1, 11, 13, 4, 6, 10, 12, 15, 9, 22, 14, 20, 18, 7, 26, 31}); //Sully
        chars.put(0x08, new int[]{1, 2, 14, 4, 5, 8, 16, 19, 17, 21, 24, 25, 11, 23}); //Vaike
        chars.put(0x09, new int[]{1, 2, 31, 12, 5, 8, 16, 19, 17, 21, 24, 25, 11, 23}); //Stahl
        chars.put(0x0A, new int[]{2, 1, 8, 25, 6, 10, 12, 15, 9, 22, 14, 20, 18, 7, 26, 31}); //Miriel
        chars.put(0x0B, new int[]{1, 2, 31, 10, 5, 8, 16, 19, 17, 21, 24, 25, 11, 23}); //Kellam
        chars.put(0x0C, new int[]{2, 1, 8, 19, 4, 6, 18, 26}); //Sumia
        chars.put(0x0D, new int[]{1, 2, 9, 20, 5, 8, 16, 19, 17, 21, 24, 25, 11, 23}); //Lon'qu
        chars.put(0x0E, new int[]{1, 2, 20, 26, 5, 8, 16, 19, 17, 21, 24, 25, 11, 23}); //Ricken
        chars.put(0x0F, new int[]{2, 1, 24, 5, 4, 6, 10, 12, 15, 9, 22, 14, 20, 18, 7, 26, 31}); //Maribelle
        chars.put(0x10, new int[]{2, 1, 19, 24, 6, 10, 12, 15, 9, 22, 14, 20, 18, 7, 26, 31}); //Panne
        chars.put(0x11, new int[]{1, 2, 22, 4, 13, 5, 8, 16, 19, 17, 21, 24, 25, 11, 23}); //Gaius
        chars.put(0x12, new int[]{2, 1, 17, 13, 6, 10, 12, 15, 9, 22, 14, 20, 18, 7, 26, 31}); //Cordelia
        chars.put(0x13, new int[]{1, 2, 15, 14, 5, 8, 16, 19, 17, 21, 24, 25, 11, 23}); //Gregor
        chars.put(0x14, new int[]{2, 1, 25, 23, 6, 10, 12, 15, 9, 22, 14, 20, 18, 7, 26, 31}); //Nowi
        chars.put(0x15, new int[]{1, 2, 7, 18, 5, 8, 16, 19, 17, 21, 24, 25, 11, 23}); //Libra
        chars.put(0x16, new int[]{2, 1, 21, 6, 10, 12, 15, 9, 22, 14, 20, 18, 7, 26, 31}); //Tharja
        chars.put(0x17, new int[]{2, 1, 16, 17, 4, 6, 10, 12, 15, 9, 22, 14, 20, 18, 7, 26, 31}); //Olivia
        chars.put(0x18, new int[]{2, 1, 21, 11, 6, 10, 12, 15, 9, 22, 14, 20, 18, 7, 26, 31}); //Cherche
        chars.put(0x19, new int[]{1, 2, 6, 15, 13, 5, 8, 16, 19, 17, 21, 24, 25, 11, 23}); //Henry
        chars.put(0x1A, new int[]{2, 1, 36, 46, 37, 39, 33, 34, 35, 42, 43, 40}); //Lucina
        chars.put(0x1B, new int[]{2, 1, 46}); //Say'ri
        chars.put(0x1C, new int[]{1, 2, 30}); //Basilio
        chars.put(0x1D, new int[]{2, 1, 29}); //Flavia
        chars.put(0x1E, new int[]{1, 2, 10, 12, 5, 8, 16, 19, 17, 21, 24, 25, 11, 23}); //Donnel
        chars.put(0x1F, new int[]{2, 1, 46}); //Anna
        chars.put(0x20, new int[]{1, 2, 34, 40, 35, 27, 36, 37, 38, 44, 45, 41}); //Owain
        chars.put(0x21, new int[]{1, 2, 39, 35, 33, 27, 36, 37, 38, 44, 45, 41}); //Inigo
        chars.put(0x22, new int[]{1, 2, 42, 34, 33, 27, 36, 37, 38, 44, 45, 41}); //Brady
        chars.put(0x23, new int[]{2, 1, 38, 27, 39, 33, 34, 35, 42, 43, 40}); //Kjelle
        chars.put(0x24, new int[]{2, 1, 45, 38, 27, 39, 33, 34, 35, 42, 43, 40}); //Cynthia
        chars.put(0x25, new int[]{2, 1, 36, 37, 44, 39, 33, 34, 35, 42, 43, 40}); //Severa
        chars.put(0x26, new int[]{1, 2, 43, 34, 27, 36, 37, 38, 44, 45, 41}); //Gerome
        chars.put(0x27, new int[]{33, 36, 27, 42, 37, 38, 44, 45}); //Morgan M
        chars.put(0x28, new int[]{44, 33, 39, 45, 34, 35, 42, 43}); //Morgan F
        chars.put(0x29, new int[]{1, 2, 35, 40, 43, 27, 36, 37, 38, 44, 45, 41}); //Yarne
        chars.put(0x2A, new int[]{1, 2, 39, 42, 27, 36, 37, 38, 44, 45, 41}); //Laurent
        chars.put(0x2B, new int[]{2, 1, 38, 41, 39, 33, 34, 35, 42, 43, 40}); //Noire
        chars.put(0x2C, new int[]{2, 1, 41, 46, 37, 39, 33, 34, 35, 42, 43, 40}); //Nah
        chars.put(0x2D, new int[]{2, 1, 27, 32, 45, 28}); //Tiki
        chars.put(0x2E, new int[]{1, 2}); //Gangrel
        chars.put(0x2F, new int[]{1, 2}); //Walhart
        chars.put(0x30, new int[]{2, 1}); //Emmeryn
        chars.put(0x31, new int[]{1, 2}); //Yen'fay
        chars.put(0x32, new int[]{2, 1}); //Aversa
        chars.put(0x33, new int[]{1, 2}); //Priam
        chars.put(0x34, new int[]{}); //Marth
        chars.put(0x35, new int[]{}); //Maiden - Dummy
        chars.put(0x36, new int[]{}); //Monster (Unpromoted)
        chars.put(0x37, new int[]{}); //Monster (Promoted)
        chars.put(0x38, new int[]{}); //Merchant
        return chars;
    }

    /*
    Type of each support
    0: Non-Romantic
    1: Slow
    2: Medium
    3: Fast
     */
    private static HashMap<Integer, int[]> supportTypes() {
        HashMap<Integer, int[]> chars = new HashMap<Integer, int[]>();
        chars.put(0x00, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}); //Avatar M
        chars.put(0x01, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}); //Avatar F
        chars.put(0x02, new int[]{}); //Logbook Unit
        chars.put(0x03, new int[]{0, 1, 0, 0, 0, 3, 2, 2, 1, 0}); //Chrom
        chars.put(0x04, new int[]{0, 1, 0, 0, 2, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1}); //Lissa
        chars.put(0x05, new int[]{0, 1, 0, 0, 1, 2, 1, 3, 1, 1, 1, 1, 1, 1, 1, 0}); //Frederick
        chars.put(0x06, new int[]{0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1}); //Virion
        chars.put(0x07, new int[]{0, 1, 0, 0, 2, 1, 2, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1}); //Sully
        chars.put(0x08, new int[]{0, 1, 0, 0, 3, 1, 1, 1, 1, 2, 1, 1, 1, 1}); //Vaike
        chars.put(0x09, new int[]{0, 1, 0, 0, 1, 2, 1, 1, 1, 1, 1, 1, 3, 1}); //Stahl
        chars.put(0x0A, new int[]{0, 1, 0, 0, 1, 3, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1}); //Miriel
        chars.put(0x0B, new int[]{0, 1, 0, 0, 1, 3, 1, 1, 1, 1, 1, 1, 2, 1}); //Kellam
        chars.put(0x0C, new int[]{0, 1, 0, 0, 3, 1, 1, 1}); //Sumia
        chars.put(0x0D, new int[]{0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}); //Lon'qu
        chars.put(0x0E, new int[]{0, 1, 0, 0, 1, 1, 1, 1, 3, 1, 1, 2, 1, 1}); //Ricken
        chars.put(0x0F, new int[]{0, 1, 0, 0, 2, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}); //Maribelle
        chars.put(0x10, new int[]{0, 1, 0, 0, 1, 1, 1, 3, 1, 2, 1, 1, 1, 1, 1, 1}); //Panne
        chars.put(0x11, new int[]{0, 1, 0, 0, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 3}); //Gaius
        chars.put(0x12, new int[]{0, 1, 0, 0, 1, 1, 1, 1, 1, 3, 1, 1, 2, 1, 1, 1}); //Cordelia
        chars.put(0x13, new int[]{0, 1, 0, 0, 1, 1, 1, 1, 1, 3, 2, 1, 1, 1}); //Gregor
        chars.put(0x14, new int[]{0, 1, 0, 0, 1, 1, 1, 1, 2, 1, 1, 3, 1, 1, 1, 1}); //Nowi
        chars.put(0x15, new int[]{0, 1, 0, 0, 1, 1, 1, 3, 2, 1, 1, 1, 1, 1}); //Libra
        chars.put(0x16, new int[]{0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 2, 1}); //Tharja
        chars.put(0x17, new int[]{0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 3, 1}); //Olivia
        chars.put(0x18, new int[]{0, 1, 0, 0, 1, 1, 1, 2, 1, 1, 1, 1, 1, 3, 1, 1}); //Cherche
        chars.put(0x19, new int[]{0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 2}); //Henry
        chars.put(0x1A, new int[]{1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 3, 1}); //Lucina
        chars.put(0x1B, new int[]{0, 1, 0}); //Say'ri
        chars.put(0x1C, new int[]{0, 1, 0}); //Basilio
        chars.put(0x1D, new int[]{0, 1, 0}); //Flavia
        chars.put(0x1E, new int[]{0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}); //Donnel
        chars.put(0x1F, new int[]{0, 1, 0}); //Anna
        chars.put(0x20, new int[]{0, 1, 0, 0, 0, 1, 3, 1, 1, 1, 1, 1}); //Owain
        chars.put(0x21, new int[]{0, 1, 0, 0, 0, 1, 1, 3, 1, 1, 1, 1}); //Inigo
        chars.put(0x22, new int[]{0, 1, 0, 0, 0, 1, 1, 1, 3, 1, 1, 1}); //Brady
        chars.put(0x23, new int[]{0, 1, 0, 0, 1, 3, 1, 1, 1, 1, 1}); //Kjelle
        chars.put(0x24, new int[]{0, 1, 0, 0, 0, 1, 1, 3, 1, 1, 1, 1}); //Cynthia
        chars.put(0x25, new int[]{0, 1, 0, 0, 0, 1, 1, 1, 3, 1, 1, 1}); //Severa
        chars.put(0x26, new int[]{0, 1, 0, 0, 1, 1, 1, 1, 1, 3, 1}); //Gerome
        chars.put(0x27, new int[]{0, 1, 1, 0, 1, 1, 1, 1}); //Morgan M
        chars.put(0x28, new int[]{0, 1, 1, 0, 1, 1, 1, 1}); //Morgan F
        chars.put(0x29, new int[]{0, 1, 0, 0, 0, 1, 1, 1, 1, 3, 1, 1}); //Yarne
        chars.put(0x2A, new int[]{0, 1, 0, 0, 3, 1, 1, 1, 1, 1, 1}); //Laurent
        chars.put(0x2B, new int[]{0, 1, 0, 0, 1, 1, 1, 1, 3, 1, 1}); //Noire
        chars.put(0x2C, new int[]{0, 1, 0, 0, 0, 3, 1, 1, 1, 1, 1, 1}); //Nah
        chars.put(0x2D, new int[]{0, 1, 0, 0, 0, 0}); //Tiki
        chars.put(0x2E, new int[]{0, 1}); //Gangrel
        chars.put(0x2F, new int[]{0, 1}); //Walhart
        chars.put(0x30, new int[]{0, 1}); //Emmeryn
        chars.put(0x31, new int[]{0, 1}); //Yen'fay
        chars.put(0x32, new int[]{0, 1}); //Aversa
        chars.put(0x33, new int[]{0, 1}); //Priam
        chars.put(0x34, new int[]{}); //Marth
        chars.put(0x35, new int[]{}); //Maiden - Dummy
        chars.put(0x36, new int[]{}); //Monster (Unpromoted)
        chars.put(0x37, new int[]{}); //Monster (Promoted)
        chars.put(0x38, new int[]{}); //Merchant
        return chars;
    }

}
