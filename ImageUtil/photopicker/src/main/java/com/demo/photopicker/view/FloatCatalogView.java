package com.demo.photopicker.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.demo.photopicker.R;
import com.demo.photopicker.adapter.FloatCatalogAdapter;
import com.demo.photopicker.model.PhotoFolderInfo;

import java.util.List;

/**
 * Created by bjhl on 2018/6/4.
 */

public class FloatCatalogView extends FrameLayout {

    private Context context;
    private FrameLayout container;
    private RecyclerView cataloglist;
    private FloatCatalogAdapter adapter;
    private OnCatalogClickListener clickLlistener;

    public FloatCatalogView(@NonNull Context context) {
        this(context, null);
    }

    public FloatCatalogView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatCatalogView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        initListener();
    }

    public void setClickLlistener(OnCatalogClickListener clickLlistener) {
        this.clickLlistener = clickLlistener;
    }

    private void initListener() {
        //点击半透明位置也要隐藏
        container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFloatCatalog();
            }
        });
    }

    public void setCatalogList(List<PhotoFolderInfo> photoFolders) {
        if (adapter == null) {
            adapter = new FloatCatalogAdapter(context, photoFolders);
            adapter.setCatalogItemClickListener(new FloatCatalogAdapter.OnCatalogItemClickListener() {
                @Override
                public void onCatalogItemClick(int selectedPosition) {
                    if (clickLlistener != null) {
                        //关闭目录页
                        toggleFloatCatalog();
                        clickLlistener.onCatalogItemClick(selectedPosition);
                    }
                }
            });
        }
        cataloglist.setAdapter(adapter);
    }

    private void initView() {
        View view = View.inflate(context, R.layout.photo_picker_view_float_catalog, this);
        container = view.findViewById(R.id.photo_picker_float_catalog_container_fl);
        cataloglist = view.findViewById(R.id.photo_picker_float_catalog_recycler_view);
        cataloglist.setLayoutManager(new LinearLayoutManager(context));
    }


    public void toggleFloatCatalog() {
        if (container.getVisibility() == View.VISIBLE) {
            cataloglist.startAnimation(AnimationUtils.loadAnimation(context, R.anim.photo_picker_anim_float_up_to_bottom));
            ObjectAnimator animator = ObjectAnimator.ofFloat(container, "alpha",1, 0);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    container.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.start();
        } else {
            container.setVisibility(View.VISIBLE);
            cataloglist.startAnimation(AnimationUtils.loadAnimation(context, R.anim.photo_picker_anim_float_bottom_to_up));
            ObjectAnimator animator = ObjectAnimator.ofFloat(container, "alpha",0, 1);
            animator.start();
        }
    }

    public interface OnCatalogClickListener {
        void onCatalogItemClick(int selectedPosition);
    }

}
