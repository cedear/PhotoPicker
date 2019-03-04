package com.demo.photopicker.manager;

import android.content.Context;


import com.demo.photopicker.model.PhotoFolderInfo;
import com.demo.photopicker.model.PhotoInfo;
import com.demo.photopicker.util.PhotoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bjhl on 2018/8/7.
 */

public class PhotoListManager {

    private static PhotoListManager manager;
    private List<PhotoFolderInfo> photoFolderInfoList;

    public static PhotoListManager getInstance() {
        if (manager == null) {
            synchronized (PhotoListManager.class) {
                if (manager == null) {
                    manager = new PhotoListManager();
                }
            }
        }
        return manager;
    }

    private PhotoListManager() {
    }

    public List<PhotoFolderInfo> getPhotoFolderInfoList(Context context) {
        if (photoFolderInfoList == null || photoFolderInfoList.size() == 0) {
            photoFolderInfoList = new ArrayList<>();
            photoFolderInfoList.addAll(PhotoUtil.getAllPhotoFolder(context));
        }
        return photoFolderInfoList;
    }

    public List<PhotoInfo> getCatalogListByFolderId(int folderId) {
        if (photoFolderInfoList != null && photoFolderInfoList.size() != 0) {
            if (folderId == 0) {
                return photoFolderInfoList.get(0).getPhotoList();
            }
            for (int i = 0 ; i < photoFolderInfoList.size() ; i++) {
                if (photoFolderInfoList.get(i).getFolderId() == folderId) {
                    return photoFolderInfoList.get(i).getPhotoList();
                }
            }
        }
        return null;
    }

    public static void release () {
        manager = null;
    }
}
