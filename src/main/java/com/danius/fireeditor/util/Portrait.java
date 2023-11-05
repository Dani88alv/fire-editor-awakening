package com.danius.fireeditor.util;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.data.ClassDb;
import com.danius.fireeditor.data.UnitDb;
import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.savefile.units.extrablock.LogBlock;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.Objects;

public class Portrait {

    public static Image[] setImage(Unit unit) {
        Image[] sprites = new Image[3];
        if (unit == null) return sprites;
        int unitId = unit.getUnitId();
        try {
            boolean isPlayable = UnitDb.isUnitPlayable(unitId);
            //Outrealm Flag
            if (unit.rawFlags.hasBattleFlag(27)) {
                sprites[0] = setImageEnemy(unit);
            }
            //Logbook portraits
            else if (unit.rawLog != null) {
                sprites = setImageLog(unit.rawLog, unit.rawBlockEnd.getHairColorFx());
            }
            //Valid non-playable unit IDs
            else if (!isPlayable && ClassDb.hasEnemyPortrait(unit.rawBlock1.unitClass())) {
                sprites[0] = setImageEnemy(unit);
            }
            //Playable Characters
            else if (isPlayable) {
                //Children Units
                if (UnitDb.hasUnitCustomHairColor(unitId)) {
                    sprites = setImageChildren(unit);
                }
                //Adult Units
                else {
                    String path = "characters/" + unitId + ".png";
                    sprites[0] = readImage(path);
                }
            }
            //Invalid
            else {
                sprites[0] = getInvalid();
            }
            return sprites;
        } catch (Exception e) {
            sprites[0] = getInvalid();
            return sprites;
        }

    }

    private static Image getInvalid() {
        String path = Constants.RES + "characters/" + "what" + ".png";
        return new Image(Objects.requireNonNull(Portrait.class.getResourceAsStream(path)));
    }

    private static Image setImageEnemy(Unit unit) {
        String folderName = "monster/";
        int army = unit.rawFlags.army(); //9
        int unitClass = unit.rawBlock1.unitClass();
        String fileName = String.valueOf((unitClass + 1));
        //If Risen Army and valid risen portrait
        if (army == 9 && ClassDb.hasRisenPortrait(unitClass)) fileName += "_r";
        fileName += ".png";

        return readImage(folderName + fileName);
    }

    private static Image[] setImageChildren(Unit unit) {
        Image[] sprites = new Image[3];
        String path = "children/" + unit.getUnitId();
        String buildPath = path + ".png";
        sprites[0] = readImage(buildPath);
        String hairPath = path + "_hair.png";
        sprites[2] = readImage(hairPath);
        String backPath = path + "_back.png";
        Image backSprite = readImage(backPath);
        sprites[1] = fillImageWithColor(backSprite, unit.rawBlockEnd.getHairColorFx());
        return sprites;
    }

    private static Image[] setImageLog(LogBlock unit, Color color) {
        Image imgHair = null;
        Image imgBuild = null;
        Image imgHairColor = null;
        //The values are gotten
        int build = unit.getFullBuild()[0];
        int face = unit.getFullBuild()[1];
        int hair = unit.getFullBuild()[2];
        boolean female = (unit.getFullBuild()[4] > 0);
        //PRIORITY ORDER
        //DLC Units (Eldigan will be considered SpotPass)
        if (unit.hasFaceDlc()) {
            String path = "dlc/dlc_" + face + ".png";
            imgBuild = readImage(path);
        }
        //Regular Avatar
        else if (!unit.isEinherjar() && face <= 0x4) {
            //Path
            String path = "avatar_";
            if (female) path += "f/";
            else path += "m/";
            //Build sprite
            String buildPath = path + "build_0" + build + "_0" + face + ".png";
            imgBuild = readImage(buildPath);
            //Hair sprite
            String hairPath = path + "hair_0" + build + "_0" + hair + ".png";
            imgHair = readImage(hairPath);
            //Hair color
            String backPath = path + "back_0" + build + "_0" + hair + ".png";
            Image backSprite = readImage(backPath);
            imgHairColor = fillImageWithColor(backSprite, color);
        }
        //SpotPass Units
        else if (unit.isEinherjar() && unit.hasSpotPassPortrait()) {
            int logId = unit.getLogIdLastByte();

            String path = "spotpass/" + logId + ".png";
            imgBuild = readImage(path);
        }
        //Invalid
        else {
            String path = "/com/danius/fireeditor/spotpass/placeholder.png";
            imgBuild = new Image(Objects.requireNonNull(Portrait.class.getResourceAsStream(path)));
        }
        return new Image[]{imgBuild, imgHairColor, imgHair};
    }

    /*
   Changes the color of the hair color sprite
    */
    public static Image fillImageWithColor(Image image, Color fillColor) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage filledImage = new WritableImage(width, height);
        PixelReader pixelReader = image.getPixelReader();
        PixelWriter pixelWriter = filledImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixelColor = pixelReader.getColor(x, y);
                Color newColor = new Color(
                        fillColor.getRed(),
                        fillColor.getGreen(),
                        fillColor.getBlue(),
                        fillColor.getOpacity()
                );

                if (pixelColor.isOpaque()) {
                    // Use the new color for opaque pixels
                    pixelWriter.setColor(x, y, newColor);
                } else {
                    // Preserve transparency for transparent pixels
                    pixelWriter.setColor(x, y, pixelColor);
                }
            }
        }

        return filledImage;
    }

    private static Image readImage(String filePath) {
        File imageFile = FireEditor.readResource(filePath);
        try {
            if (imageFile != null && imageFile.exists()) {
                // Load the image from the file
                return new Image(imageFile.toURI().toString());
            } else {
                // Load the image from project resources if the file doesn't exist
                return new Image(Objects.requireNonNull(
                        Portrait.class.getResourceAsStream(Constants.RES + filePath)));
            }
        } catch (Exception e) {
            return getInvalid();
        }
    }
}
