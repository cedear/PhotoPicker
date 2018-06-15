package com.demo.photopicker.activity;

import android.app.Activity;

import com.demo.photopicker.model.PhotoFolderInfo;

import java.util.List;

/**
 * Created by bjhl on 2018/6/11.
 */

public class PhotoShowContract {

    interface View {
        void onGetPhotoListSuccess(List<PhotoFolderInfo> photoList);
    }

    interface Presenter {
        void getPhotoList(Activity activity);
    }

}
