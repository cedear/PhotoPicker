package com.demo.photopicker.activity;

import android.Manifest;
import android.app.Activity;
import android.widget.Toast;

import com.baijiahulian.common.permission.AppPermissions;
import com.demo.photopicker.R;
import com.demo.photopicker.model.PhotoFolderInfo;

import java.util.ArrayList;

import rx.functions.Action1;

import static com.demo.photopicker.util.PhotoUtil.getAllPhotoFolder;

/**
 * Created by bjhl on 2018/6/11.
 */

public class PhotoShowPresenter implements PhotoShowContract.Presenter {
    private PhotoShowContract.View view;

    public PhotoShowPresenter(PhotoShowContract.View view) {
        this.view = view;
    }

    @Override
    public void getPhotoList(final Activity activity) {
        final ArrayList<PhotoFolderInfo> list = new ArrayList<>();
        if (AppPermissions.newPermissions(activity).isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            list.addAll(getAllPhotoFolder(activity));
        } else {
            AppPermissions.newPermissions(activity).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if (aBoolean) {
                                list.addAll(getAllPhotoFolder(activity));
                            } else {
                                Toast.makeText(activity, R.string.photo_picker_write_external_permission_failed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        view.onGetPhotoListSuccess(list);
    }
}
