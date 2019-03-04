package com.demo.photopicker.model;

import android.net.Uri;
import android.text.TextUtils;

import com.demo.photopicker.photointerface.GalleryPhotoInterface;

import java.io.File;
import java.io.Serializable;

/**
 * Created by bjhl on 2018/6/11.
 */

public class PhotoInfo implements Serializable, GalleryPhotoInterface {
    private int photoId;
    private String photoPath;
    private int width;
    private int height;
    private int folderId;
    private Uri uri;

    public PhotoInfo(String photoPath) {
        this.photoPath = photoPath;
    }

    public PhotoInfo() {}

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

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public Uri getUri() {
        if (!TextUtils.isEmpty(photoPath)) {
            return Uri.fromFile(new File(photoPath));
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PhotoInfo) {
            PhotoInfo photoInfo = (PhotoInfo) obj;
            return this.photoPath.equals(photoInfo.getPhotoPath());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return photoPath.hashCode();
    }

    @Override
    public Object getPhotoResource() {
        return photoPath;
    }

}
