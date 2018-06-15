package com.demo.photopicker.activity;

import android.Manifest;
import android.app.Activity;
import android.widget.Toast;

import com.demo.photopicker.R;
import com.demo.photopicker.model.PhotoFolderInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;

import static com.demo.photopicker.util.PhotoUtil.getAllPhotoFolder;

/**
 * Created by bjhl on 2018/6/11.
 */

public class PhotoShowPresenter implements PhotoShowContract.Presenter {
    private PhotoShowContract.View view;
    private RxPermissions rxPermissions;

    public PhotoShowPresenter(PhotoShowContract.View view) {
        this.view = view;
    }

    @Override
    public void getPhotoList(final Activity activity) {
        final ArrayList<PhotoFolderInfo> list = new ArrayList<>();

        if (rxPermissions == null) {
            rxPermissions = new RxPermissions(activity);
        }
        if (rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            list.addAll(getAllPhotoFolder(activity));
            view.onGetPhotoListSuccess(list);
        } else {
            rxPermissions
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) {
                            list.addAll(getAllPhotoFolder(activity));
                            view.onGetPhotoListSuccess(list);
                        } else {
                            Toast.makeText(activity, R.string.photo_picker_write_external_permission_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
