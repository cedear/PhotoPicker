package com.demo.photopicker.activity;

import android.os.Bundle;

import com.demo.photopicker.PhotoPicker;
import com.demo.photopicker.model.PhotoInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import static com.demo.photopicker.PhotoPicker.THUMBNAIL_POSITION_CANT_REACH;
import static com.demo.photopicker.activity.PhotoPreviewActivity.BIG_PICTURE_CLICK_PATH;
import static com.demo.photopicker.activity.PhotoPreviewActivity.BIG_PICTURE_PREVIEW_DATA;
import static com.demo.photopicker.activity.PhotoPreviewActivity.PHOTO_PREVIEW_TYPE_SELECTED_ONLY;
import static com.demo.photopicker.activity.PhotoPreviewActivity.PHOTO_PREVIEW_TYPE_WHOLE_CATALOG;
import static com.demo.photopicker.activity.PhotoPreviewActivity.PREVIEW_DATA_TYPE;

/**
 * Created by bjhl on 2018/6/12.
 */

public class PhotoPreviewPresenter implements PhotoPreviewContract.Presenter {

    private PhotoPreviewContract.View view = null;
    private int previewDataType;
    private boolean isViewPagerItemInSelectedList;
    private int viewPagerItemPositionInSelectedList;
    private int currentPositionInViewPager;

    public PhotoPreviewPresenter(PhotoPreviewContract.View view) {
        this.view = view;
    }

    @Override
    public void getPhotoPreviewData(Bundle bundle) {
        previewDataType = bundle.getInt(PREVIEW_DATA_TYPE);
        //预览选中的图片集（大图预览和缩略图预览都是一样的，即选中的那些图片，目前最多限制9张）
        if (previewDataType == PHOTO_PREVIEW_TYPE_SELECTED_ONLY) {
            view.onGetBigPicturePreviewData(mapToList(PhotoPicker.PHOTO_SELECT_LIST), 0);
            view.onGetThumbnailPreviewData(mapToList(PhotoPicker.PHOTO_SELECT_LIST), 0);
        }
        //预览整个目录中的图片集
        else if (previewDataType == PHOTO_PREVIEW_TYPE_WHOLE_CATALOG) {
            List<PhotoInfo> catalogList = new ArrayList<>();
            String clickPhotoPath;
            catalogList = (List<PhotoInfo>) bundle.getSerializable(BIG_PICTURE_PREVIEW_DATA);
            clickPhotoPath = bundle.getString(BIG_PICTURE_CLICK_PATH);

            view.onGetBigPicturePreviewData(catalogList, findPositionInListByPhotopath(catalogList, clickPhotoPath));
            currentPositionInViewPager = findPositionInListByPhotopath(catalogList, clickPhotoPath);
            view.onGetThumbnailPreviewData(mapToList(PhotoPicker.PHOTO_SELECT_LIST), findPositionInListByPhotopath(mapToList(PhotoPicker.PHOTO_SELECT_LIST), clickPhotoPath));
        }
    }

    public List<PhotoInfo> mapToList(LinkedHashMap<String ,PhotoInfo> selectedMap) {
        List<PhotoInfo> list = new ArrayList<>();
        Iterator iterator = selectedMap.keySet().iterator();
        while(iterator.hasNext()){
            String key =iterator.next().toString();
            list.add(selectedMap.get(key));// value 存入list
        }
        return list;
    }

    private int findPositionInListByPhotopath(List<PhotoInfo> list, String photoPath) {
        for (int i = 0 ; i < list.size() ; i++) {
            if (list.get(i).getPhotoPath().equals(photoPath)) {
                return i;
            }
        }
        return THUMBNAIL_POSITION_CANT_REACH;
    }

    public void currentPageEqualsSelected(PhotoInfo currentPhotoInfo, int currentItemPosition) {
        boolean isInList = false;
        int indexInList = THUMBNAIL_POSITION_CANT_REACH;
        List<PhotoInfo> selectedList = new ArrayList<>();
        selectedList.addAll(mapToList(PhotoPicker.PHOTO_SELECT_LIST));
        if (PhotoPicker.PHOTO_SELECT_LIST != null && selectedList.size() != 0) {
            for (int i = 0 ; i < selectedList.size() ; i++) {
                if (currentPhotoInfo.getPhotoPath().equals(selectedList.get(i).getPhotoPath())) {
                    isInList = true;
                    indexInList = i;
                    break;
                }
            }
        }
        isViewPagerItemInSelectedList = isInList;
        viewPagerItemPositionInSelectedList = indexInList;
        currentPositionInViewPager = currentItemPosition;
    }

    public boolean getViewPagerItemInSelectedList() {
        return isViewPagerItemInSelectedList;
    }

    public int getViewPagerItemPositionInSelectedList() {
        return viewPagerItemPositionInSelectedList;
    }

    public int getCurrentPositionInViewPager() {
        return currentPositionInViewPager;
    }




}
