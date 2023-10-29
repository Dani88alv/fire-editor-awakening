package com.danius.fireeditor.data.model;

public class ChapterModel {
    private int chapterId;
    private int mapId;
    private String chapterName;
    private String description;

    public ChapterModel() {
        chapterId = -1;
        mapId = -1;
        chapterName = "";
        description = "";
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return getChapterName();
    }
}
