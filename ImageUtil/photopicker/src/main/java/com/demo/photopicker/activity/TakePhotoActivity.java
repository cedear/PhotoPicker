package com.demo.photopicker.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.demo.photopicker.PhotoPicker;
import com.demo.photopicker.R;
import com.demo.photopicker.model.PhotoInfo;
import com.demo.photopicker.provider.PhotoProvider;
import com.demo.photopicker.util.DeviceUtils;
import com.demo.photopicker.util.MediaScanner;
import com.demo.photopicker.util.PhotoUtil;
import com.demo.photopicker.util.RandomUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class TakePhotoActivity extends AppCompatActivity {


    protected static String mPhotoTargetFolder;
    private String photoPath;
    private Uri mTakePhotoUri;
    private MediaScanner mMediaScanner;
    private RxPermissions rxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.photo_picker_activity_take_photo);
        mMediaScanner = new MediaScanner(this);
        permissionRequest();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, TakePhotoActivity.class);
        context.startActivity(intent);
    }

    private void permissionRequest() {

        if (rxPermissions == null) {
            rxPermissions = new RxPermissions(this);
        }
        if (rxPermissions.isGranted(Manifest.permission.CAMERA)) {
            takePhoto();
        } else {
            rxPermissions
                    .request(Manifest.permission.CAMERA)
                    .subscribe(granted -> {
                        if (granted) {
                            takePhoto();
                        } else {
                            Toast.makeText(TakePhotoActivity.this, R.string.photo_picker_take_photo_permission_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void takePhoto() {
        if (!DeviceUtils.existSDCard()) {
            toast("没有SD卡不能拍照呢~");
            return;
        }

        File takePhotoFolder = null;
        if (TextUtils.isEmpty(mPhotoTargetFolder)) {
            takePhotoFolder = PhotoUtil.getTakePhotoDir();
        } else {
            takePhotoFolder = new File(mPhotoTargetFolder);
        }

        SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String name = f.format(new Date());
        photoPath = takePhotoFolder.getPath() + File.separator + "IMG" + name + ".jpeg";
        File toFile = new File(photoPath);
        boolean suc = reuqestPermissionWriteFile(toFile);
        if (suc) {
            try {
                if (Build.VERSION.SDK_INT >= 24) {
                    mTakePhotoUri = PhotoProvider.getUriForFile(this,
                            getApplicationContext().getPackageName() + ".fileprovider",
                            toFile);
                } else {
                    mTakePhotoUri = Uri.fromFile(toFile);
                }
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mTakePhotoUri);
                startActivityForResult(captureIntent, PhotoPicker.REQUEST_CODE_TAKE_PHOTO);
            } catch (Exception e) {
                e.printStackTrace();
                toast(getString(R.string.photo_picker_take_photo_error));
            }
        } else {
//            toast(getString(R.string.photo_picker_take_photo_fail));
        }


    }

    /**
     * 更新相册
     */
    private void updateGallery(String filePath) {
        if (mMediaScanner != null) {
            mMediaScanner.scanFile(filePath, "image/jpeg");
        }
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PhotoPicker.REQUEST_CODE_TAKE_PHOTO) {
            if (resultCode == RESULT_OK && mTakePhotoUri != null) {
                int width = 0;
                int height = 0;
                try {
                    ExifInterface exifInterface = new ExifInterface(photoPath);
                    width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
                    height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final PhotoInfo info = new PhotoInfo();
                info.setWidth(width);
                info.setHeight(height);
                info.setPhotoId(RandomUtil.getRandom(10000, 99999));
                info.setPhotoPath(photoPath);
                PhotoPicker.PHOTO_SELECT_LIST.put(photoPath, info);
                updateGallery(photoPath);
                PhotoPicker.sendPhoto();
//                takeResult(info);
            } else {
//                toast(getString(R.string.photo_picker_take_photo_cancel));
            }
        }
        finish();
    }

    private boolean reuqestPermissionWriteFile(File file) {

        if (rxPermissions == null) {
            rxPermissions = new RxPermissions(this);
        }

        if (rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
           rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE);
           return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaScanner != null) {
            mMediaScanner.unScanFile();
        }
    }
}
