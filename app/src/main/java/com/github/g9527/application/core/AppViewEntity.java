package com.github.g9527.application.core;

public class AppViewEntity {
    private long id;
    private int imgId;
    private int label;

    public AppViewEntity(long id, int imgId, int label) {
        this.id = id;
        this.imgId = imgId;
        this.label = label;
    }

    public int getImgId() {
        return imgId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }
}
