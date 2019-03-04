package com.demo.photopicker.activity.preview;

import android.os.Bundle;

import com.demo.photopicker.model.PhotoInfo;

import java.util.List;

/**
 * Created by bjhl on 2018/6/12.
 */

public class PhotoPreviewContract {

    interface View {
        void onGetBigPicturePreviewData(List<PhotoInfo> bigPictureData, int selectedPosition);
        void onGetThumbnailPreviewData(List<PhotoInfo> thumbnailData, int selectedPosition);
    }

    interface Presenter {
        void getPhotoPreviewData(Bundle bundle);
    }

}
