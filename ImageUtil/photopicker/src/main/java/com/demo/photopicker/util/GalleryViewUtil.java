package com.demo.photopicker.util;

import android.widget.ImageView;

import com.demo.photopicker.model.GalleryPhotoParameterModel;


/**
 * Created by bjhl on 2018/6/8.
 */

public class GalleryViewUtil {

    /**
     * 根据点击的图片及索引拼装model
     * @param index         点击图片在list中的索引
     * @param photoSource   点击图片的源（本地、网络，Drawable）
     * @param clickImage    点击图片
     * @return
     */
    public static GalleryPhotoParameterModel getGalleryPhotoParameterModel(int index, Object photoSource, ImageView clickImage) {
        GalleryPhotoParameterModel photoParameter = new GalleryPhotoParameterModel();
        //图片地址
        photoParameter.photoObj = photoSource;
        //图片在list中的索引
        photoParameter.index = index;
        int[] locationOnScreen = new int[2];
        //图片位置参数
        clickImage.getLocationOnScreen(locationOnScreen);
        photoParameter.locOnScreen = locationOnScreen;
        //图片的宽高
        int width = clickImage.getDrawable().getBounds().width();
        int height = clickImage.getDrawable().getBounds().height();
        photoParameter.imageWidth = clickImage.getWidth();
        photoParameter.imageHeight = clickImage.getHeight();
        photoParameter.photoHeight = height;
        photoParameter.photoWidth = width;
        //scaleType
        photoParameter.scaleType = clickImage.getScaleType();
        return photoParameter;
    }
}
