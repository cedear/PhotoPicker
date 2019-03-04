package com.demo.photopicker.manager;

import android.os.AsyncTask;

/**
 * Created by bjhl on 2018/8/7.
 */

public class CommonAsyncTask {

    public interface DisPatchRunnable {
        /**
         * 后台执行操作
         */
        void runInBackground();

        /**
         * 主线程执行操作
         */
        void runInMain();
    }


    public static void dispatchAsync(DisPatchRunnable runnable) {
        new DispatchAsyncTask().execute(runnable);
    }

    private final static class DispatchAsyncTask extends AsyncTask<DisPatchRunnable, Void, DisPatchRunnable> {

        @Override
        protected DisPatchRunnable doInBackground(DisPatchRunnable... params) {
            if (params == null || params.length == 0)
                return null;
            DisPatchRunnable runnable = params[0];

            runnable.runInBackground();

            return runnable;
        }

        @Override
        protected void onPostExecute(DisPatchRunnable txDisPatchRunnable) {
            if (txDisPatchRunnable != null) {
                txDisPatchRunnable.runInMain();
            }
        }
    }
    
}
