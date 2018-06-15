package com.demo.photopicker.model;

import com.demo.photopicker.photointerface.GalleryPhotoInterface;

import java.io.Serializable;

/**
 * Created by bjhl on 2018/6/11.
 */

public class PhotoInfo implements Serializable, GalleryPhotoInterface {
    private int photoId;
    private String photoPath;
    private int width;
    private int height;

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public Object getPhotoResource() {
        return photoPath;
    }

}
