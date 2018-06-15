package com.demo.photopicker.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by bjhl on 2018/6/7.
 */

public class ImageUtil {

    public static final int DISK_CACHE_STRATEGY_ALL = 0;
    public static final int DISK_CACHE_STRATEGY_NONE = 1;
    public static final int DISK_CACHE_STRATEGY_DATA = 2;
    public static final int DISK_CACHE_STRATEGY_RESOURCE = 3;
    public static final int DISK_CACHE_STRATEGY_AUTOMATIC = 4;

    int width, height;
    RequestOptions requestOptions;
    RequestManager requestManager;

    public static ImageUtil with(Context context) {
        return new ImageUtil(context);
    }

    public static ImageUtil with(FragmentActivity activity) {
        return new ImageUtil(activity);
    }

    public static ImageUtil with(Fragment fragment) {
        return new ImageUtil(fragment);
    }

    private ImageUtil() {

    }

    private ImageUtil(Context context) {
        requestManager = Glide.with(context);
        init();
    }

    private ImageUtil(FragmentActivity activity) {
        requestManager = Glide.with(activity);
        init();
    }

    private ImageUtil(Fragment fragment) {
        requestManager = Glide.with(fragment);
        init();
    }

    /**
     * Glide内部缩放图片
     * @param width
     * @param height
     * @return
     */
    public ImageUtil override(int width, int height) {
        requestOptions.override(width, height);
        return this;
    }

    /**
     * 阿里云服务缩放图片
     * @param width
     * @param height
     * @return
     */
    public ImageUtil resize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * 跳过内存缓存
     * @param skip
     * @return
     */
    public ImageUtil skipMemoryCache(boolean skip) {
        requestOptions.skipMemoryCache(skip);
        return this;
    }

    /**
     * 加载时的占位图片
     * @param drawableRes
     * @return
     */
    public ImageUtil placeholder(@DrawableRes int drawableRes) {
        requestOptions.placeholder(drawableRes);
        return this;
    }

    /**
     * 加载时的占位图片
     * @param drawable
     * @return
     */
    public ImageUtil placeholder(Drawable drawable) {
        requestOptions.placeholder(drawable);
        return this;
    }

    /**
     * 加载失败时显示图片
     * @param drawableRes
     * @return
     */
    public ImageUtil error(@DrawableRes int drawableRes) {
        requestOptions.error(drawableRes);
        return this;
    }

    /**
     * 加载失败时显示图片
     * @param drawable
     * @return
     */
    public ImageUtil error(Drawable drawable) {
        requestOptions.error(drawable);
        return this;
    }

    /**
     * 图片样式
     * @param transformation
     * @return
     */
    public ImageUtil transform(Transformation<Bitmap> transformation) {
        requestOptions.transform(transformation);
        return this;
    }

    /**
     * 多样式混合
     * @param transformation
     * @return
     */
    public ImageUtil transforms(Transformation<Bitmap>... transformation) {
        requestOptions.transforms(transformation);
        return this;
    }


    /**
     * 设置scaleType
     * @return
     */
    public ImageUtil centerCrop() {
        requestOptions.centerCrop();
        return this;
    }

    /**
     * 物理存储策略
     * @param strategy
     * @return
     */
    public ImageUtil diskCacheStrategy(int strategy) {
        switch (strategy) {
            case DISK_CACHE_STRATEGY_ALL:
                requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                break;
            case DISK_CACHE_STRATEGY_NONE:
                requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
                break;
            case DISK_CACHE_STRATEGY_DATA:
                requestOptions.diskCacheStrategy(DiskCacheStrategy.DATA);
                break;
            case DISK_CACHE_STRATEGY_RESOURCE:
                requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                break;
            case DISK_CACHE_STRATEGY_AUTOMATIC:
            default:
                requestOptions.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                break;
        }
        return this;
    }

    /**
     * 加载网络图片和本地图片
     * @param url
     * @param v
     */
    public void load(String url, ImageView v) {
        requestManager.load(url).apply(requestOptions).into(v);
    }

    /**
     * 加载图片
     * @param drawableRes
     * @param v
     */
    public void load(@DrawableRes int drawableRes, ImageView v) {
        requestManager.load(drawableRes).apply(requestOptions).into(v);
    }

    private void init() {
        requestOptions = RequestOptions
                .diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC);
    }

}
