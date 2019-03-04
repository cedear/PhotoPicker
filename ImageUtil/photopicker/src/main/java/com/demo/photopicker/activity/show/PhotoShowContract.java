package com.demo.photopicker.activity.show;

import android.app.Activity;
import android.content.Context;

import com.demo.photopicker.model.PhotoFolderInfo;
import com.demo.photopicker.model.PhotoInfo;

import java.util.List;

/**
 * Created by bjhl on 2018/6/11.
 */

public class PhotoShowContract {

    interface View {
        void onGetPhotoListSuccess(List<PhotoFolderInfo> photoList);
        void onHandleCropPhotoSuccess(List<PhotoFolderInfo> photoList);
    }

    interface Presenter {
        void getPhotoList(Activity activity);
        void handleCropPhoto(Context context, String oldPhotoPath, PhotoInfo replacePhoto);
    }

}
