package com.danius.fireeditor.util;

import com.danius.fireeditor.savefile.Constants13;
import com.danius.fireeditor.savefile.units.Unit;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Objects;

public class Portrait13 {

    private static final int[] enemyClasses = new int[]{
            9, 11, 13, 15, 17, 19, 20, 21, 23, 25, 26,
            27, 29, 31, 33, 35, 37, 38, 39, 41, 43, 44,
            45, 46, 48, 50, 53, 55, 57, 59, 60, 61, 62,
            64, 65, 66, 72, 73, 74, 75, 76
    };

    public static boolean enemyPortrait(Unit unit) {
        int unitClass = unit.rawBlock1.unitClass();
        for (int value : enemyClasses) {
            if (value - 1 == unitClass) {
                return true;
            }
        }
        return false;
    }

    public static boolean isChildColor(Unit unit) {
        int unitId = unit.rawBlock1.unitId();
        return unitId >= 0x20 && unitId <= 0x2C;
    }

    public static Image[] setImage(Unit unit) {
        int unitId = unit.rawBlock1.unitId();
        Image[] sprites = new Image[3];
        //Logbook portraits
        if (unit.rawLog != null) {
            sprites = setImageLog(unit);
        }
        //Valid non-playable* unit IDs
        else if (unitId >= 0x34 && unitId <= 0x38 && enemyPortrait(unit)) {

            int army = unit.rawFlags.army(); //9
            int unitClass = unit.rawBlock1.unitClass();
            String aa = Names13.className(unitClass);
            String path = "/com/danius/fireeditor/monster/" + (unitClass + 1);
            if (army == 9 && unitClass < 73) path += "_r";
            path += ".png";
            sprites[0] = new Image(Objects.requireNonNull(Portrait13.class.getResourceAsStream(path)));
        } else if (unitId > 2 && unitId <= Constants13.MAX_PLAYABLE) {
            String path = "/com/danius/fireeditor/characters/" + unitId + ".png";
            sprites[0] = new Image(Objects.requireNonNull(Portrait13.class.getResourceAsStream(path)));
        } else {
            String path = "/com/danius/fireeditor/characters/" + "what" + ".png";
            sprites[0] = new Image(Objects.requireNonNull(Portrait13.class.getResourceAsStream(path)));
        }
        return sprites;
    }

    private static Image[] setImageEnemy(Unit unit) {
        return new Image[]{};
    }

    private static Image[] setImageLog(Unit unit) {
        Image imgHair = null;
        Image imgBuild = null;
        Image imgHairColor = null;
        //The values are gotten
        int build = unit.rawLog.getFullBuild()[0];
        int face = unit.rawLog.getFullBuild()[1];
        int hair = unit.rawLog.getFullBuild()[2];
        boolean female = (unit.rawLog.getFullBuild()[4] > 0);
        //PRIORITY ORDER
        //DLC Units (Eldigan will be considered SpotPass)
        if (unit.rawLog.hasFaceDlc()) {
            String path = "/com/danius/fireeditor/dlc/dlc_" + face + ".png";
            imgBuild = new Image(Objects.requireNonNull(Portrait13.class.getResourceAsStream(path)));
        }
        //Regular Avatar
        else if (!unit.rawLog.isEinherjar() && face <= 0x4) {
            //Path
            String path = "/com/danius/fireeditor/avatar_";
            if (female) path += "f/";
            else path += "m/";
            //Build sprite
            String buildPath = path + "build_0" + build + "_0" + face + ".png";
            imgBuild = new Image(Objects.requireNonNull(Portrait13.class.getResourceAsStream(buildPath)));
            //Hair sprite
            String hairPath = path + "hair_0" + build + "_0" + hair + ".png";
            imgHair = new Image(Objects.requireNonNull(Portrait13.class.getResourceAsStream(hairPath)));
            //Hair color
            String backPath = path + "back_0" + build + "_0" + hair + ".png";
            Image backSprite = new Image(Objects.requireNonNull(Portrait13.class.getResourceAsStream(backPath)));
            String hexColor = "#" + unit.rawLog.getLogHairColor();
            imgHairColor = fillImageWithColor(backSprite, hexColor);
        }
        //SpotPass Units
        else if (unit.rawLog.isEinherjar() && unit.rawLog.hasEinherjarId()) {
            int logId = unit.rawLog.getLogIdLastByte();
            String path = "/com/danius/fireeditor/spotpass/" + logId + ".png";
            imgBuild = new Image(Objects.requireNonNull(Portrait13.class.getResourceAsStream(path)));
        }
        //Invalid
        else {
            String path = "/com/danius/fireeditor/spotpass/placeholder.png";
            imgBuild = new Image(Objects.requireNonNull(Portrait13.class.getResourceAsStream(path)));
        }
        return new Image[]{imgBuild, imgHairColor, imgHair};
    }

    /*
   Changes the color of the hair color sprite
    */
    public static Image fillImageWithColor(Image image, String hexColor) {
        // Convert hex color to JavaFX Color
        Color fillColor = Color.web(hexColor);
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        // Create a writable image with the same dimensions as the original image
        WritableImage filledImage = new WritableImage(width, height);
        // Get the pixel reader for the original image
        PixelReader pixelReader = image.getPixelReader();
        // Get the pixel writer for the filled image
        PixelWriter pixelWriter = filledImage.getPixelWriter();
        // Fill the image with the specified color
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixelColor = pixelReader.getColor(x, y);
                if (pixelColor.isOpaque()) {
                    // Use the fill color for opaque pixels
                    pixelWriter.setColor(x, y, fillColor);
                } else {
                    // Preserve transparency for transparent pixels
                    pixelWriter.setColor(x, y, pixelColor);
                }
            }
        }
        return filledImage;
    }

    public static Image overlayImages(Image... images) {
        int maxWidth = 0;
        int maxHeight = 0;

        // Calculate the maximum width and height
        for (Image image : images) {
            maxWidth = Math.max(maxWidth, (int) image.getWidth());
            maxHeight = Math.max(maxHeight, (int) image.getHeight());
        }

        WritableImage combinedImage = new WritableImage(maxWidth, maxHeight);
        PixelWriter pixelWriter = combinedImage.getPixelWriter();

        for (Image image : images) {
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            PixelReader pixelReader = image.getPixelReader();

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int argb = pixelReader.getArgb(x, y);
                    pixelWriter.setArgb(x, y, argb);
                }
            }
        }

        return combinedImage;
    }
}
