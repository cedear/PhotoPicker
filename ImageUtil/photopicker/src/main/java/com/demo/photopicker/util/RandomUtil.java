package com.demo.photopicker.util;

import java.util.Random;

/**
 * Created by bjhl on 2018/6/6.
 */

public class RandomUtil {
    /**
     * 取某个范围的任意数
     * @param min
     * @param max
     * @return
     */
    public static int getRandom(int min, int max){
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;
    }
}
