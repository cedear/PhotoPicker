package com.demo.photopicker.view;

/**
 * Created by bjhl on 2018/10/9.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.demo.photopicker.photointerface.GalleryPhotoInterface;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * Created by bjhl on 2018/6/7.
 */

public class MyPhotoView extends PhotoView {
    private GalleryPhotoInterface photoModel;

    private ProgressBar progressBar;
    private Context context;
    private OnPhotoLoadListener listener;

    public void setPhotoLoadListener(OnPhotoLoadListener listener) {
        this.listener = listener;
    }

    public MyPhotoView(Context context) {
        super(context);
    }

    public MyPhotoView(Context context, AttributeSet attr) {
        super(context, attr);
    }


    public void setPhotoModel(Context context, GalleryPhotoInterface photoModel) {
        this.context = context;
        this.photoModel = photoModel;
    }

    public void startGlide() {
        Glide.with(getContext())
                .load(photoModel.getPhotoResource())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (listener != null) {
                            listener.onPhotoLoadFail();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (listener != null) {
                            listener.onPhotoLoadComplete();
                        }
                        return false;
                    }
                })
                .into(MyPhotoView.this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public interface OnPhotoLoadListener{
        void onPhotoLoadComplete();
        void onPhotoLoadFail();
    }
}
