package com.demo.photopicker.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import com.demo.photopicker.PhotoPicker;
import com.demo.photopicker.R;

/**
 * Created by bjhl on 2018/6/4.
 */

public class PhotoPreviewViewPager extends ViewPager {
    public PhotoPreviewViewPager(Context context) {
        super(context);
    }

    public PhotoPreviewViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private boolean mIsDisallowIntercept = false;
    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // keep the info about if the innerViews do
        // requestDisallowInterceptTouchEvent
        mIsDisallowIntercept = disallowIntercept;
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // the incorrect array size will only happen in the multi-touch
        // scenario.
        if (ev.getPointerCount() > 1 && mIsDisallowIntercept) {
            requestDisallowInterceptTouchEvent(false);
            boolean handled = super.dispatchTouchEvent(ev);
            requestDisallowInterceptTouchEvent(true);
            return handled;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    // we some the listner
    protected OnPageChangeListener listener;

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        super.addOnPageChangeListener(listener);
        this.listener = listener;
    }

    @Override
    public void setCurrentItem(int item) {
        // when you pass set current item to 0,
        // the listener won't be called so we call it on our own
        boolean invokeMeLater = false;

        if(super.getCurrentItem() == 0 && item == 0)
            invokeMeLater = true;

        super.setCurrentItem(item);

        if(invokeMeLater && listener != null)
            listener.onPageSelected(0);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (item == PhotoPicker.THUMBNAIL_POSITION_CANT_REACH) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.photo_picker_not_in_this_folder), Toast.LENGTH_LONG).show();
            return;
        }
        super.setCurrentItem(item, smoothScroll);

    }
}
