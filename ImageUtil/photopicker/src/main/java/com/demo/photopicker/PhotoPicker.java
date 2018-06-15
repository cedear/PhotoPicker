package com.demo.photopicker;

import android.content.Context;

import com.demo.photopicker.activity.PhotoShowActivity;
import com.demo.photopicker.activity.TakePhotoActivity;
import com.demo.photopicker.model.PhotoInfo;
import com.demo.photopicker.util.ListUtil;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by bjhl on 2018/6/6.
 */

public class PhotoPicker {
    private static OnGetPhotoPickerCallBack callBack;
    private static int LIMIT_PHOTO_COUNT = 9;
    private static int THEME_COLOR = R.color.photo_picker_color_FF6C00;
    private static boolean IS_MUTIL_SELECT_TYPE = true;
    private static int PHOTO_LIST_SPAN_COUNT = 4;
    private static boolean isPhotoPreviewWithCamera = false;
    public static LinkedHashMap<String, PhotoInfo> PHOTO_SELECT_LIST = new LinkedHashMap<>();

    public static final int PHOTO_EVENT_NOTIFY_DATA = 0;
    public static final int PHOTO_EVENT_SEND_PHOTO = 1;
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

    public PhotoPicker setGetPhotoPickerCallBack(OnGetPhotoPickerCallBack photoPickerCallBack) {
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

    public PhotoPicker setIsMutilSelectType(boolean isMutilSelectType) {
        IS_MUTIL_SELECT_TYPE = isMutilSelectType;
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
        isPhotoPreviewWithCamera = isWithCamera;
        return this;
    }

    public static boolean getIsPhotoPreviewWithCamera() {
        return isPhotoPreviewWithCamera;
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
        if (IS_MUTIL_SELECT_TYPE)
            return LIMIT_PHOTO_COUNT;
        else
            return 1;
    }

    public static int getThemeColor() {
        return THEME_COLOR;
    }

    public static boolean isMutilSelectType() {
        return IS_MUTIL_SELECT_TYPE;
    }

    public static int getPhotoListSpanCount() {
        return PHOTO_LIST_SPAN_COUNT;
    }

    public static void sendPhoto() {
        if (PhotoPicker.PHOTO_SELECT_LIST != null && PhotoPicker.PHOTO_SELECT_LIST.size() != 0) {
            if (PhotoPicker.getCallBack() != null) {
                PhotoPicker.getCallBack().onGetPhotoPickerSuccess(ListUtil.mapToList(PhotoPicker.PHOTO_SELECT_LIST));
            } else {
                PhotoPicker.getCallBack().onGetPhotoPickerFail();
            }
        }
        freeResource();
    }

    public static void freeResource() {
        PHOTO_SELECT_LIST.clear();
        callBack = null;
        LIMIT_PHOTO_COUNT = 9;
        THEME_COLOR = R.color.photo_picker_color_FF6C00;
        IS_MUTIL_SELECT_TYPE = true;
        PHOTO_LIST_SPAN_COUNT = 4;
        isPhotoPreviewWithCamera = false;
    }
}
