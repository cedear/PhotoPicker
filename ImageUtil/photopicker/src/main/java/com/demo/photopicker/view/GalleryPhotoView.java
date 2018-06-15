package com.demo.photopicker.view;

import android.content.Context;
import android.graphics.Canvas;

import com.bumptech.glide.Glide;
import com.demo.photopicker.photointerface.GalleryPhotoInterface;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * Created by bjhl on 2018/6/7.
 */

public class GalleryPhotoView extends PhotoView {
    private GalleryPhotoInterface photoModel;

    public GalleryPhotoView(Context context, GalleryPhotoInterface photoModel) {
        super(context);
        this.photoModel = photoModel;
    }

    public void startGlide() {
        Glide.with(getContext()).load(photoModel.getPhotoResource()).into(GalleryPhotoView.this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}
