package com.demo.photopicker.model;

import android.support.annotation.DrawableRes;

import java.io.Serializable;

/**
 * Created by bjhl on 2018/6/7.
 */

public class GalleryPhotoModel implements Serializable{

    public Object photoSource;

    public GalleryPhotoModel(@DrawableRes int drawableRes) {
        this.photoSource = drawableRes;
    }

    public GalleryPhotoModel(String path) {
        this.photoSource = path;
    }

}
