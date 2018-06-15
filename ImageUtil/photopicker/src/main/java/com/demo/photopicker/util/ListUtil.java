package com.demo.photopicker.util;


import com.demo.photopicker.model.PhotoInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by bjhl on 2018/6/6.
 */

public class ListUtil {
    public static List<PhotoInfo> mapToList(LinkedHashMap<String ,PhotoInfo> selectedMap) {
        List<PhotoInfo> list = new ArrayList<>();
        Iterator iterator = selectedMap.keySet().iterator();
        while(iterator.hasNext()){
            String key =iterator.next().toString();
            list.add(selectedMap.get(key));// value 存入list
        }
        return list;
    }
}
