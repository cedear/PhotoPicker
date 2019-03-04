package com.demo.photopicker.activity.show;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.demo.photopicker.R;
import com.demo.photopicker.manager.CommonAsyncTask;
import com.demo.photopicker.manager.PhotoListManager;
import com.demo.photopicker.model.PhotoInfo;
import com.demo.photopicker.util.PermissionsUtil;

/**
 * Created by bjhl on 2018/6/11.
 */

public class PhotoShowPresenter implements PhotoShowContract.Presenter {
    private PhotoShowContract.View view;
    public PhotoShowPresenter(PhotoShowContract.View view) {
        this.view = view;
    }
//    ArrayList<PhotoFolderInfo> list = new ArrayList<>();

    @Override
    public void getPhotoList(final Activity activity) {

        if (PermissionsUtil.checkPermission((FragmentActivity) activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            onGetPhotoList(activity);
        } else {
            PermissionsUtil.request((FragmentActivity) activity, new PermissionsUtil.OnRequestPermissionListener() {
                @Override
                public void onAllow() {
                    onGetPhotoList(activity);
                }

                @Override
                public void onRefuse(boolean shouldShowRequestPermissionRationale) {
                    Toast.makeText(activity, R.string.photo_picker_write_external_permission_failed, Toast.LENGTH_SHORT).show();
                }
            }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void handleCropPhoto(Context context, String oldPhotoPath, PhotoInfo replacePhoto) {
        //相同的图片有两张 "全部图片" "自己所在的目录中"
        int limitCount = 2;
        if (PhotoListManager.getInstance().getPhotoFolderInfoList(context) != null && PhotoListManager.getInstance().getPhotoFolderInfoList(context).size() != 0) {
            for (int i = 0 ; i < PhotoListManager.getInstance().getPhotoFolderInfoList(context).size() ; i++) {
                if (PhotoListManager.getInstance().getPhotoFolderInfoList(context).get(i).getPhotoList() != null && PhotoListManager.getInstance().getPhotoFolderInfoList(context).get(i).getPhotoList().size() != 0) {
                    for (int j = 0 ; j < PhotoListManager.getInstance().getPhotoFolderInfoList(context).get(i).getPhotoList().size() ; j++) {
                        //把老的替换成新的
                        if (limitCount != 0) {
                            if (PhotoListManager.getInstance().getPhotoFolderInfoList(context).get(i).getPhotoList().get(j).getPhotoPath().equals(oldPhotoPath)) {
                                PhotoListManager.getInstance().getPhotoFolderInfoList(context).get(i).getPhotoList().remove(j);
                                PhotoListManager.getInstance().getPhotoFolderInfoList(context).get(i).getPhotoList().add(j, replacePhoto);
                                limitCount--;
                            }
                        } else {
                            view.onHandleCropPhotoSuccess(PhotoListManager.getInstance().getPhotoFolderInfoList(context));
                            break;
                        }
                    }
                }
            }
        }
    }

    private void onGetPhotoList(final Context context) {
        CommonAsyncTask.dispatchAsync(new CommonAsyncTask.DisPatchRunnable() {
            @Override
            public void runInBackground() {
                PhotoListManager.getInstance().getPhotoFolderInfoList(context);
            }

            @Override
            public void runInMain() {
                view.onGetPhotoListSuccess(PhotoListManager.getInstance().getPhotoFolderInfoList(context));
            }
        });
    }


}
