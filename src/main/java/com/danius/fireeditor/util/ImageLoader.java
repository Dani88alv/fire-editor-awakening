package com.danius.fireeditor.util;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.Constants;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ImageLoader {
    private static final Map<String, Image> templateImageCache = new HashMap<>();

    public static void initialize() {
        loadImages();
    }

    private static void loadImages() {
        String[] subfolders = {
                "characters",
                "monster",
                "avatar_f",
                "avatar_m",
                "children",
                "dlc",
                "spotpass"
        };

        for (String subfolder : subfolders) {
            loadTemplateImages("templates/portrait/" + subfolder + "/");
        }
    }


    private static void loadTemplateImages(String templateDirectory) {
        if (templateDirectory != null) {
            File folder = new File(templateDirectory);

            if (folder.exists() && folder.isDirectory()) {
                for (File file : Objects.requireNonNull(folder.listFiles())) {
                    if (file.isFile()) {
                        String filePath = templateDirectory + file.getName();
                        cacheTemplateImage(filePath);
                    }
                }
            }
        }
    }

    private static void cacheTemplateImage(String filePath) {
        filePath = filePath.replace("templates/", "");
        File imageFile = FireEditor.readResource(filePath);
        if (imageFile != null) {
            Image image = new Image(imageFile.toURI().toString());
            ImageLoader.templateImageCache.put(filePath, image);
        }
    }

    public static Image getImage(String filePath) {
        Image templateImage;
        try {
            templateImage = templateImageCache.get(filePath);
        } catch (Exception e) {
            templateImage = new Image(Objects.requireNonNull(
                    Portrait.class.getResourceAsStream(Constants.RES + filePath)));
        }
        return Objects.requireNonNullElseGet(templateImage, () -> new Image(Objects.requireNonNull(
                Portrait.class.getResourceAsStream(Constants.RES + filePath))));
    }

}
