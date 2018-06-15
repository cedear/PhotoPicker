package com.demo.photopicker.view;

import android.content.Context;
import android.graphics.Canvas;

import com.bumptech.glide.Glide;
import com.demo.photopicker.model.PhotoInfo;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * Created by bjhl on 2018/6/5.
 */

public class PhotoPreviewGalleryView extends PhotoView {


    public PhotoPreviewGalleryView(Context context, PhotoInfo photoInfo) {
        super(context);
        Glide.with(getContext()).load(photoInfo.getPhotoPath()).into(PhotoPreviewGalleryView.this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
