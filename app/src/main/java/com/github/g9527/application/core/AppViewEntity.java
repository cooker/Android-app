package com.github.g9527.application.core;

public class AppViewEntity {
    private long id;
    private int imgId;
    private String label;

    public AppViewEntity(long id, int imgId, String label) {
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
