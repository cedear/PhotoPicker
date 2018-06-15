package com.demo.photopicker.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.demo.photopicker.R;
import com.demo.photopicker.model.PhotoFolderInfo;
import com.demo.photopicker.model.PhotoInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bjhl on 2018/5/31.
 */

public class PhotoUtil {

    private static final long ALL_PHOTO_FOLDER_IMAGE_SIZE_LIMIT = 1024 * 10;

    /**
     * 获取所有图片
     *
     * @param context
     * @return
     */
    public static List<PhotoFolderInfo> getAllPhotoFolder(Context context) {
        List<PhotoFolderInfo> allFolderList = new ArrayList<>();
        final String[] projectionPhotos = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Thumbnails.DATA,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT
        };
        final ArrayList<PhotoFolderInfo> allPhotoFolderList = new ArrayList<>();
        HashMap<Integer, PhotoFolderInfo> bucketMap = new HashMap<>();
        Cursor cursor = null;
        //所有图片
        PhotoFolderInfo allPhotoFolderInfo = new PhotoFolderInfo();
        allPhotoFolderInfo.setFolderId(0);
        allPhotoFolderInfo.setFolderName(context.getResources().getString(R.string.photo_picker_all_photo));
        allPhotoFolderInfo.setPhotoList(new ArrayList<PhotoInfo>());
        allPhotoFolderList.add(0, allPhotoFolderInfo);
//        List<String> selectedList = BJCommonImageCropHelper.getFunctionConfig().getSelectedList();
//        List<String> filterList = GalleryFinal.getFunctionConfig().getFilterList();
        try {
            cursor = MediaStore.Images.Media.query(context.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    , projectionPhotos, "", null, MediaStore.Images.Media.DATE_TAKEN + " DESC");
            if (cursor != null) {
                int bucketNameColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                final int bucketIdColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                while (cursor.moveToNext()) {
                    int bucketId = cursor.getInt(bucketIdColumn);
                    String bucketName = cursor.getString(bucketNameColumn);
                    final int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    final int imageIdColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    //int thumbImageColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
                    final int imageId = cursor.getInt(imageIdColumn);
                    final String path = cursor.getString(dataColumn);
                    //final String thumb = cursor.getString(thumbImageColumn);
                    //初步检测gif
                    if (path.endsWith("gif")) {
                        continue;
                    }
                    //再次检测gif
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path, options);
                    String type = options.outMimeType;
                    if (TextUtils.isEmpty(type)) {
                        continue;
                    } else {
                        type = type.substring(6, type.length());
                    }
                    if ("gif".equals(type)) {
                        continue;
                    }

                    File file = new File(path);
                    final int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                    final int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));

                    if (/* (filterList == null || !filterList.contains(path)) && */file.exists() && file.length() > 0) {
                        final PhotoInfo photoInfo = new PhotoInfo();
                        photoInfo.setPhotoId(imageId);
                        photoInfo.setPhotoPath(path);
                        photoInfo.setWidth(width);
                        photoInfo.setHeight(height);
                        //photoInfo.setThumbPath(thumb);
                        if (allPhotoFolderInfo.getCoverPhoto() == null) {
                            allPhotoFolderInfo.setCoverPhoto(photoInfo);
                        }
                        //添加到所有图片
                        if (file.length() > ALL_PHOTO_FOLDER_IMAGE_SIZE_LIMIT) {
                            // 尺寸小于 10K 的图片,不加到所有图片目录下
                            allPhotoFolderInfo.getPhotoList().add(photoInfo);
                        }

                        //通过bucketId获取文件夹
                        PhotoFolderInfo photoFolderInfo = bucketMap.get(bucketId);

                        if (photoFolderInfo == null) {
                            photoFolderInfo = new PhotoFolderInfo();
                            photoFolderInfo.setPhotoList(new ArrayList<PhotoInfo>());
                            photoFolderInfo.setFolderId(bucketId);
                            photoFolderInfo.setFolderName(bucketName);
                            photoFolderInfo.setCoverPhoto(photoInfo);
                            bucketMap.put(bucketId, photoFolderInfo);
                            allPhotoFolderList.add(photoFolderInfo);
                        }
                        photoFolderInfo.getPhotoList().add(photoInfo);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        allFolderList.addAll(allPhotoFolderList);
//        if (selectedList != null) {
//            selectedList.clear();
//        }
        return allFolderList;
    }


    /**
     * 返回拍照后照片存储目录
     * @return
     */
    public static File getTakePhotoDir() {
        File takePhotoFile = new File(Environment.getExternalStorageDirectory(), "/DCIM/");
        if (! takePhotoFile.exists()) {
            takePhotoFile.mkdirs();
        }
        return takePhotoFile;
    }

}
