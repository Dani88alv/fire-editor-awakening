package com.danius.fireeditor.util;

import com.danius.fireeditor.data.ClassDb;
import com.danius.fireeditor.data.UnitDb;
import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.units.Unit;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Objects;

public class Portrait {

    public static Image[] setImage(Unit unit) {
        int unitId = unit.getUnitId();
        Image[] sprites = new Image[3];
        try {
            boolean isPlayable = UnitDb.isUnitPlayable(unitId);
            //Outrealm Flag
            if (unit.rawFlags.hasBattleFlag(27)) {
                sprites[0] = setImageEnemy(unit);
            }
            //Logbook portraits
            else if (unit.rawLog != null) {
                sprites = setImageLog(unit);
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
                    String path = Constants.RES + "characters/" + unitId + ".png";
                    sprites[0] = new Image(Objects.requireNonNull(Portrait.class.getResourceAsStream(path)));
                }
            }
            //Invalid
            else {
                sprites[0] = setInvalid();
            }
            return sprites;
        } catch (Exception e) {
            sprites[0] = setInvalid();
            return sprites;
        }

    }

    private static Image setInvalid() {
        String path = Constants.RES + "characters/" + "what" + ".png";
        return new Image(Objects.requireNonNull(Portrait.class.getResourceAsStream(path)));
    }

    private static Image setImageEnemy(Unit unit) {
        int army = unit.rawFlags.army(); //9
        int unitClass = unit.rawBlock1.unitClass();
        String path = Constants.RES + "monster/" + (unitClass + 1);
        //If Risen Army and valid risen portrait
        if (army == 9 && ClassDb.hasRisenPortrait(unitClass)) path += "_r";
        path += ".png";
        return new Image(Objects.requireNonNull(Portrait.class.getResourceAsStream(path)));
    }

    private static Image[] setImageChildren(Unit unit) {
        Image[] sprites = new Image[3];
        String path = Constants.RES + "children/" + unit.getUnitId();
        String buildPath = path + ".png";
        sprites[0] = new Image(Objects.requireNonNull(Portrait.class.getResourceAsStream(buildPath)));
        String hairPath = path + "_hair.png";
        sprites[2] = new Image(Objects.requireNonNull(Portrait.class.getResourceAsStream(hairPath)));
        String backPath = path + "_back.png";
        Image backSprite = new Image(Objects.requireNonNull(Portrait.class.getResourceAsStream(backPath)));
        sprites[1] = fillImageWithColor(backSprite, unit.rawBlockEnd.getHairColorFx());
        return sprites;
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
            imgBuild = new Image(Objects.requireNonNull(Portrait.class.getResourceAsStream(path)));
        }
        //Regular Avatar
        else if (!unit.rawLog.isEinherjar() && face <= 0x4) {
            //Path
            String path = "/com/danius/fireeditor/avatar_";
            if (female) path += "f/";
            else path += "m/";
            //Build sprite
            String buildPath = path + "build_0" + build + "_0" + face + ".png";
            imgBuild = new Image(Objects.requireNonNull(Portrait.class.getResourceAsStream(buildPath)));
            //Hair sprite
            String hairPath = path + "hair_0" + build + "_0" + hair + ".png";
            imgHair = new Image(Objects.requireNonNull(Portrait.class.getResourceAsStream(hairPath)));
            //Hair color
            String backPath = path + "back_0" + build + "_0" + hair + ".png";
            Image backSprite = new Image(Objects.requireNonNull(Portrait.class.getResourceAsStream(backPath)));
            imgHairColor = fillImageWithColor(backSprite, unit.rawBlockEnd.getHairColorFx());
        }
        //SpotPass Units
        else if (unit.rawLog.isEinherjar() && unit.rawLog.hasSpotPassPortrait()) {
            int logId = unit.rawLog.getLogIdLastByte();

            String path = "/com/danius/fireeditor/spotpass/" + logId + ".png";
            imgBuild = new Image(Objects.requireNonNull(Portrait.class.getResourceAsStream(path)));
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
}
