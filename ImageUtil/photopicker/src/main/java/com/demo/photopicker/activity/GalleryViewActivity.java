package com.demo.photopicker.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.demo.photopicker.R;
import com.demo.photopicker.model.GalleryPhotoParameterModel;
import com.demo.photopicker.photointerface.GalleryPhotoInterface;
import com.demo.photopicker.view.GalleryView;

import java.io.Serializable;
import java.util.List;

public class GalleryViewActivity extends Activity {

    private GalleryView galleryView;
    private static final String LIST_GALLERY_PHOTO = "LIST_GALLERY_PHOTO";
    private static final String GALLERY_PHOTO_PARAMETER_MODEL = "GALLERY_PHOTO_PARAMETER_MODEL";
    private static final String IS_FULL_SCREEN = "IS_FULL_SCREEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getBoolean(IS_FULL_SCREEN,false)){
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        setContentView(R.layout.photo_picker_activity_gallery_view);
        galleryView = findViewById(R.id.activity_gallery_view);
        galleryView.setGalleryViewFadedListener(new GalleryView.OnGalleryViewFadedListener() {
            @Override
            public void onGalleryViewFaded() {
                finish();
            }
        });
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getSerializable(LIST_GALLERY_PHOTO) != null && getIntent().getExtras().getSerializable(GALLERY_PHOTO_PARAMETER_MODEL) != null) {
                galleryView.showPhotoGallery((List<GalleryPhotoInterface>)(getIntent().getExtras().getSerializable(LIST_GALLERY_PHOTO)), (GalleryPhotoParameterModel)(getIntent().getExtras().getSerializable(GALLERY_PHOTO_PARAMETER_MODEL)));
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    public static void launch (Context context, List<? extends GalleryPhotoInterface> photoList, GalleryPhotoParameterModel photoParameter, boolean isFullScreen) {
        Intent intent = new Intent(context, GalleryViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(LIST_GALLERY_PHOTO, (Serializable) photoList);
        bundle.putSerializable(GALLERY_PHOTO_PARAMETER_MODEL, photoParameter);
        bundle.putBoolean(IS_FULL_SCREEN, isFullScreen);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
