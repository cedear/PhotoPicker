package com.demo.photopicker.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;


import com.demo.photopicker.model.PhotoInfo;
import com.demo.photopicker.view.PhotoPreviewGalleryView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bjhl on 2018/6/5.
 */

public class PhotoPreviewViewPagerAdapter extends PagerAdapter {

    private List<PhotoPreviewGalleryView> viewList;
    private Context context;
    private List<PhotoInfo> dataList;
    private OnGalleryViewClickListener galleryViewClickListener;

    public void setGalleryViewClickListener(OnGalleryViewClickListener listener) {
        galleryViewClickListener = listener;
    }

    public PhotoPreviewViewPagerAdapter(Context context, List<PhotoInfo> list) {
        this.context = context;
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        this.dataList.addAll(list);
        dataListToViewList(list);
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        viewList.get(position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (galleryViewClickListener != null) {
                    galleryViewClickListener.onGalleryViewClick();
                }
            }
        });
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }

    public interface OnGalleryViewClickListener{
        void onGalleryViewClick();
    }

    public int getPhotoInfoPosition(PhotoInfo photoInfo) {
        for (int i = 0 ; i < dataList.size() ; i++) {
            if (photoInfo.getPhotoPath().equals(dataList.get(i).getPhotoPath())) {
                return i;
            }
        }
        return 0;
    }


    private List<PhotoPreviewGalleryView> dataListToViewList(List<PhotoInfo> dataList) {
        if (viewList == null)
            viewList = new ArrayList<>();
        for (int i = 0 ; i < dataList.size() ; i++) {
            PhotoPreviewGalleryView galleryView = new PhotoPreviewGalleryView(context, dataList.get(i));
            viewList.add(galleryView);
        }
        return viewList;
    }

    public PhotoInfo getCurrentItemPhotoInfo(int currentItemPosition) {
        return dataList.get(currentItemPosition);
    }
}
