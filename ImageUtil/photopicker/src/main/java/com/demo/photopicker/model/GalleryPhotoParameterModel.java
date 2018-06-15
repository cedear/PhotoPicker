package com.demo.photopicker.model;

import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by bjhl on 2018/6/7.
 */

public class GalleryPhotoParameterModel implements Serializable{
    //索引
    public int index;
    // 图片的类型
    public Object photoObj;
    // 在屏幕上的位置
    public int[] locOnScreen = new int[]{-1, -1};
    // 图片的宽
    public int photoWidth = 0;
    // 图片的高
    public int photoHeight = 0;
    // ImageView的宽
    public int imageWidth = 0;
    // ImageView的高
    public int imageHeight = 0;
    //
    public ImageView.ScaleType scaleType;
}
