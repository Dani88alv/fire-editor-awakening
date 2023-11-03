package com.danius.fireeditor.data;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.data.model.ChapterModel;
import com.danius.fireeditor.savefile.Constants;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ChapterDb {
    public static int MAX_CHAPTER_COUNT = 55;
    public static int MAX_OVERWORLD_COUNT = 51;

    private static final ChapterDb database = new ChapterDb();

    private List<ChapterModel> chapterList = new ArrayList<>();

    public ChapterDb() {
        readChapters();
    }

    public static ChapterModel getChapterById(int id) {
        for (int i = 0; i < MAX_CHAPTER_COUNT; i++) {
            ChapterModel chapter = database.chapterList.get(i);
            if (chapter.getChapterId() == id) return chapter;
        }
        ChapterModel chapter = new ChapterModel();
        chapter.setChapterId(id - MAX_CHAPTER_COUNT);
        return chapter;
    }

    private static ChapterModel getChapterByMapId(int id) {
        for (int i = 0; i < MAX_CHAPTER_COUNT; i++) {
            ChapterModel chapter = getChapterById(i);
            if (chapter.getMapId() == id) return chapter;
        }
        ChapterModel chapter = new ChapterModel();
        int mapId = id - MAX_OVERWORLD_COUNT;
        chapter.setMapId(mapId);
        chapter.setChapterName("Modded Chapter #" + mapId);
        return chapter;
    }

    public static String getChapterName(int chapterId) {
        if (!isInvalid(chapterId)) {
            ChapterModel chapterModel = getChapterById(chapterId);
            return chapterModel.getChapterName();
        }
        return "Invalid Chapter #" + (chapterId - MAX_CHAPTER_COUNT);
    }

    public static String getOverWorldName(int mapId) {
        for (int i = 0; i < MAX_OVERWORLD_COUNT; i++) {
            ChapterModel chapter = getChapterByMapId(i);
            if (chapter.getMapId() == mapId) return chapter.getChapterName();
        }
        return "Modded Map #" + (MAX_OVERWORLD_COUNT - mapId);
    }

    public static String getDescription(int chapterId) {
        if (!isInvalid(chapterId)) {
            ChapterModel chapterModel = getChapterById(chapterId);
            return chapterModel.getDescription();
        }
        return "Invalid Chapter #" + (chapterId - MAX_CHAPTER_COUNT);
    }

    public static List<String> getAllChapterNames() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < MAX_CHAPTER_COUNT; i++) {
            names.add(getChapterName(i));
        }
        return names;
    }

    public static List<String> getOverWorldNames() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < MAX_OVERWORLD_COUNT; i++) {
            names.add(getOverWorldName(i));
        }
        return names;
    }

    public static List<String> getDescriptions() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < MAX_CHAPTER_COUNT; i++) {
            names.add(getDescription(i));
        }
        return names;
    }

    private static int overWorldCount() {
        int count = 0;
        for (ChapterModel chapterModel : database.chapterList) {
            if (chapterModel.getMapId() != -1) count++;
        }
        return count;
    }

    private static boolean isInvalid(int chapterId) {
        return chapterId < 0 || chapterId >= MAX_CHAPTER_COUNT;
    }

    private static int chapterCount() {
        return database.chapterList.size();
    }


    public void readChapters() {
        File file = FireEditor.readResource(Constants.ADDON_XML + "chapters.xml");
        String xmlFilePath = Constants.RES_XML + "chapters.xml";
        chapterList = new ArrayList<>();

        try {
            InputStream is;
            // Check if the file exists
            if (file != null && file.exists()) is = new FileInputStream(file);
            else {
                is = UnitDb.class.getResourceAsStream(xmlFilePath);
                if (is == null) throw new FileNotFoundException("Resource not found: " + xmlFilePath);
            }
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(is);
            Element rootElement = document.getRootElement();
            // Iterate through character elements in the XML
            for (Element element : rootElement.getChildren("chapter")) {
                ChapterModel chapterModel = new ChapterModel();
                // Parse attributes from the XML
                chapterModel.setChapterId(Integer.parseInt(element.getAttributeValue("chapterId")));
                chapterModel.setChapterName(element.getAttributeValue("chapterName"));

                String stringMapId = element.getAttributeValue("mapId");
                int mapId = (stringMapId.equals("")) ? -1 : Integer.parseInt(stringMapId);
                chapterModel.setMapId(mapId);

                chapterModel.setDescription(element.getAttributeValue("description"));

                chapterList.add(chapterModel);
            }
        } catch (IOException | JDOMException ex) {
            throw new RuntimeException(ex);
        }
    }
}
