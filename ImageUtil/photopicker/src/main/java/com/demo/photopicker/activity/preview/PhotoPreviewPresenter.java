package com.demo.photopicker.activity.preview;

import android.os.Bundle;

import com.demo.photopicker.PhotoPicker;
import com.demo.photopicker.manager.PhotoListManager;
import com.demo.photopicker.model.PhotoInfo;

import java.util.ArrayList;
import java.util.List;

import static com.demo.photopicker.PhotoPicker.THUMBNAIL_POSITION_CANT_REACH;
import static com.demo.photopicker.activity.preview.PhotoPreviewActivity.BIG_PICTURE_CLICK_PATH;
import static com.demo.photopicker.activity.preview.PhotoPreviewActivity.BIG_PICTURE_DATA_FOLDER_ID;
import static com.demo.photopicker.activity.preview.PhotoPreviewActivity.PHOTO_PREVIEW_TYPE_SELECTED_ONLY;
import static com.demo.photopicker.activity.preview.PhotoPreviewActivity.PHOTO_PREVIEW_TYPE_WHOLE_CATALOG;
import static com.demo.photopicker.activity.preview.PhotoPreviewActivity.PREVIEW_DATA_TYPE;

/**
 * Created by bjhl on 2018/6/12.
 */

public class PhotoPreviewPresenter implements PhotoPreviewContract.Presenter {

    private PhotoPreviewContract.View view = null;
    private int previewDataType;
    private boolean isViewPagerItemInSelectedList;
    private int viewPagerItemPositionInSelectedList;
    private String cropPhotoPath;
    private int viewPageCurrentPosition;

    public PhotoPreviewPresenter(PhotoPreviewContract.View view) {
        this.view = view;
    }

    @Override
    public void getPhotoPreviewData(Bundle bundle) {
        previewDataType = bundle.getInt(PREVIEW_DATA_TYPE);
        //预览选中的图片集（大图预览和缩略图预览都是一样的，即选中的那些图片，目前最多限制9张）
        if (previewDataType == PHOTO_PREVIEW_TYPE_SELECTED_ONLY) {
            view.onGetBigPicturePreviewData(PhotoPicker.PHOTO_SELECT_LIST, 0);
            view.onGetThumbnailPreviewData(PhotoPicker.PHOTO_SELECT_LIST, 0);
        }
        //预览整个目录中的图片集
        else if (previewDataType == PHOTO_PREVIEW_TYPE_WHOLE_CATALOG) {
            List<PhotoInfo> catalogList = new ArrayList<>();
            String clickPhotoPath = bundle.getString(BIG_PICTURE_CLICK_PATH);
            int folderId = bundle.getInt(BIG_PICTURE_DATA_FOLDER_ID, 0);
            catalogList = PhotoListManager.getInstance().getCatalogListByFolderId(folderId);
            view.onGetBigPicturePreviewData(catalogList, findPositionInListByPhotopath(catalogList, clickPhotoPath));
            view.onGetThumbnailPreviewData(PhotoPicker.PHOTO_SELECT_LIST, findPositionInListByPhotopath(PhotoPicker.PHOTO_SELECT_LIST, clickPhotoPath));
        }
    }

    private int findPositionInListByPhotopath(List<PhotoInfo> list, String photoPath) {
        for (int i = 0 ; i < list.size() ; i++) {
            if (list.get(i).getPhotoPath().equals(photoPath)) {
                return i;
            }
        }
        return THUMBNAIL_POSITION_CANT_REACH;
    }

    public void currentPageEqualsSelected(PhotoInfo currentPhotoInfo) {
        boolean isInList = false;
        int indexInList = THUMBNAIL_POSITION_CANT_REACH;
        if (PhotoPicker.PHOTO_SELECT_LIST != null && PhotoPicker.PHOTO_SELECT_LIST.size() != 0) {
            for (int i = 0 ; i < PhotoPicker.PHOTO_SELECT_LIST.size() ; i++) {
                if (currentPhotoInfo.getPhotoPath().equals(PhotoPicker.PHOTO_SELECT_LIST.get(i).getPhotoPath())) {
                    isInList = true;
                    indexInList = i;
                    break;
                }
            }
        }
        isViewPagerItemInSelectedList = isInList;
        viewPagerItemPositionInSelectedList = indexInList;
    }

    public int getCurrentPhotoPositionInSelected(PhotoInfo curPhoto) {
        int position = 0;
        if (PhotoPicker.PHOTO_SELECT_LIST != null && PhotoPicker.PHOTO_SELECT_LIST.size() != 0) {
            for (int i = 0 ; i < PhotoPicker.PHOTO_SELECT_LIST.size() ; i++) {
                if (curPhoto.getPhotoPath().equals(PhotoPicker.PHOTO_SELECT_LIST.get(i).getPhotoPath())) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    public int getViewPageCurrentPosition() {
        return viewPageCurrentPosition;
    }

    public void setViewPageCurrentPosition(int viewPageCurrentPosition) {
        this.viewPageCurrentPosition = viewPageCurrentPosition;
    }

    public String getCropPhotoPath() {
        return cropPhotoPath;
    }

    public void setCropPhotoPath(String cropPhotoPath) {
        this.cropPhotoPath = cropPhotoPath;
    }

    public boolean getViewPagerItemInSelectedList() {
        return isViewPagerItemInSelectedList;
    }

    public int getViewPagerItemPositionInSelectedList() {
        return viewPagerItemPositionInSelectedList;
    }

}
