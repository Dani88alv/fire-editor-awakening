package com.danius.fireeditor.savefile;

import static com.danius.fireeditor.data.ClassDb.*;

public class Constants {

    public static final int MAX_ITEM_COUNT = 201; //0 to 201
    public static final int MAX_FORGE_COUNT = 150;
    public static final int MAX_CLASSES = getClassMaxId(); //0 to 82
    public static final int MAX_SKILLS = 103; //0 to 103, 8 bytes
    public static final int MAX_ARMY = 11; //0 to 11
    public static final int MAX_MAPS = 0; //Over world Maps
    public static final int MAX_CHAPTERS = 51; //Credit Chapters
    public static final int MAX_PLAYABLE = 0x33;
    public static final int UNIT_LIMIT = 200;
    public static final int TEAM_LIMIT = 10;

    public static final String RES = "/com/danius/fireeditor/";
    public static final String RES_BLOCK = RES + "templates/blocks/";
    public static final String RES_XML = RES + "database/";
    public static final String RES_FXML = RES + "fxml/windows/";
}
