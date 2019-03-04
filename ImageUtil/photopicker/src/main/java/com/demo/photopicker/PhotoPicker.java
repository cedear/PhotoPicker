package com.demo.photopicker;

import android.content.Context;

import com.demo.photopicker.activity.TakePhotoActivity;
import com.demo.photopicker.activity.show.PhotoShowActivity;
import com.demo.photopicker.manager.PhotoListManager;
import com.demo.photopicker.model.PhotoInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by bjhl on 2018/6/6.
 */

public class PhotoPicker {
    private static OnGetPhotoPickerCallBack callBack;
    private static int LIMIT_PHOTO_COUNT = 9;
    private static int THEME_COLOR = 0;
    private static boolean IS_MULTIPLE_SELECT_TYPE = true;
    private static int PHOTO_LIST_SPAN_COUNT = 4;
    private static boolean IS_PHOTO_PREVIEW_WITH_CAMERA = false;
    private static boolean IS_OPEN_CROP_TYPE = false;
    private static int COMPRESS_VALUE = 0;          //压缩数值（KB）
    public static List<PhotoInfo> PHOTO_SELECT_LIST = new ArrayList();

    public static final int PHOTO_EVENT_NOTIFY_DATA = 0;
    public static final int PHOTO_EVENT_SEND_PHOTO = 1;
    public static final int PHOTO_EVENT_REPLACE_CROP = 2;
    public static final String PHOTO_EVENT_REPLACE_OLD_PHOTO_PATH = "PHOTO_EVENT_REPLACE_OLD_PHOTO_PATH";
    public static final String PHOTO_EVENT_REPLACE_CROP_KEY = "PHOTO_EVENT_REPLACE_CROP_KEY";
    public static final int REQUEST_CODE_TAKE_PHOTO = 1001;
    public static final int THUMBNAIL_POSITION_CANT_REACH = 100000;


    private static PhotoPicker photoPicker;

    public interface OnGetPhotoPickerCallBack {
        void onGetPhotoPickerSuccess(List<PhotoInfo> photoList);

        void onGetPhotoPickerFail();
    }

    public static PhotoPicker getPhotoPicker() {
        if (photoPicker == null) {
            photoPicker = new PhotoPicker();
        }
        return photoPicker;
    }

    public PhotoPicker setPhotoPickerCallBack(OnGetPhotoPickerCallBack photoPickerCallBack) {
        callBack = photoPickerCallBack;
        return this;
    }

    public PhotoPicker setMaxPhotoCounts(int photoCount) {
        LIMIT_PHOTO_COUNT = photoCount;
        return this;
    }

    public PhotoPicker setThemeColor(int colorId) {
        THEME_COLOR = colorId;
        return this;
    }

    public PhotoPicker setIsMultiSelectType(boolean isMutilSelectType) {
        IS_MULTIPLE_SELECT_TYPE = isMutilSelectType;
        if (!isMutilSelectType) {
            setMaxPhotoCounts(1);
        }
        return this;
    }

    public PhotoPicker setPhotoSpanCount(int spanCount) {
        PHOTO_LIST_SPAN_COUNT = spanCount;
        return this;
    }

    public PhotoPicker setIsPhotoPreviewWithCamera(boolean isWithCamera) {
        IS_PHOTO_PREVIEW_WITH_CAMERA = isWithCamera;
        return this;
    }

    public static boolean getIsPhotoPreviewWithCamera() {
        return IS_PHOTO_PREVIEW_WITH_CAMERA;
    }

    public void startSelectPhoto(Context context) {
        PhotoShowActivity.start(context);
    }

    public void startTakePhoto(Context context) {
        TakePhotoActivity.start(context);
    }

    public static OnGetPhotoPickerCallBack getCallBack() {
        return callBack;
    }

    public static int getLimitPhotoCount() {
        if (IS_MULTIPLE_SELECT_TYPE)
            return LIMIT_PHOTO_COUNT;
        else
            return 1;
    }

    public static int getThemeColor(Context context) {
        if (THEME_COLOR == 0) {
            return context.getResources().getColor(R.color.photo_picker_color_FF6C00);
        }
        return THEME_COLOR;
    }

    public static boolean isMultipleSelectType() {
        return IS_MULTIPLE_SELECT_TYPE;
    }

    public static int getPhotoListSpanCount() {
        return PHOTO_LIST_SPAN_COUNT;
    }

    public PhotoPicker setIsOpenCropType(boolean isOpenCropType) {
        IS_OPEN_CROP_TYPE = isOpenCropType;
        return this;
    }

    public static boolean getIsOpenCropType() {
        return IS_OPEN_CROP_TYPE;
    }

    public PhotoPicker setCompressValue(int compressValue) {
        COMPRESS_VALUE = compressValue;
        return this;
    }

    public static int getCompressValue() {
        return COMPRESS_VALUE;
    }


    public static void sendPhoto(final Context context) {
        if (PhotoPicker.PHOTO_SELECT_LIST != null && PhotoPicker.PHOTO_SELECT_LIST.size() != 0) {
            if (PhotoPicker.getCallBack() != null) {
                //压缩图片
                if (PhotoPicker.getCompressValue() > 0) {
                    final List<String> list = new ArrayList<>();
                    Iterator var = PhotoPicker.PHOTO_SELECT_LIST.iterator();
                    while (var.hasNext()) {
                        PhotoInfo item = (PhotoInfo) var.next();
                        list.add(item.getPhotoPath());
                    }
                    Luban.with(context)
                            .load(list)                                   // 传人要压缩的图片列表
                            .ignoreBy(PhotoPicker.getCompressValue())                                  // 忽略不压缩图片的大小
                            .setCompressListener(new OnCompressListener() { //设置回调
                                @Override
                                public void onStart() {
                                    // TODO 压缩开始前调用，可以在方法内启动 loading UI
                                }

                                @Override
                                public void onSuccess(File file) {
                                    // TODO 压缩成功后调用，返回压缩后的图片

                                    //返回第一张压缩成功的图
                                    if (PhotoPicker.PHOTO_SELECT_LIST.size() == list.size()) {
                                        PhotoPicker.PHOTO_SELECT_LIST.clear();
                                    }

                                    //新生成PhotoInfo 重新加入列表中
                                    if (file.exists()) {
                                        PhotoPicker.PHOTO_SELECT_LIST.add(new PhotoInfo(file.getPath()));
                                    }

                                    //已经全部压缩成功了
                                    if (PhotoPicker.PHOTO_SELECT_LIST.size() == list.size()) {
                                        PhotoPicker.getCallBack().onGetPhotoPickerSuccess(PhotoPicker.PHOTO_SELECT_LIST);
                                        freeResource();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    // TODO 当压缩过程出现问题时, 则不采用压缩方案
                                    PhotoPicker.getCallBack().onGetPhotoPickerSuccess(PHOTO_SELECT_LIST);
                                    freeResource();
                                }
                            }).launch();    //启动压缩
                } else {
                    //不压缩图片
                    PhotoPicker.getCallBack().onGetPhotoPickerSuccess(PHOTO_SELECT_LIST);
                    freeResource();
                }
            } else {
                PhotoPicker.getCallBack().onGetPhotoPickerFail();
                freeResource();
            }
        }
    }

    public static void freeResource() {
        PHOTO_SELECT_LIST.clear();
        callBack = null;
        LIMIT_PHOTO_COUNT = 9;
        THEME_COLOR = 0;
        IS_MULTIPLE_SELECT_TYPE = true;
        PHOTO_LIST_SPAN_COUNT = 4;
        IS_PHOTO_PREVIEW_WITH_CAMERA = false;
        IS_OPEN_CROP_TYPE = false;
        COMPRESS_VALUE = 0;
        PhotoListManager.release();
    }
}
