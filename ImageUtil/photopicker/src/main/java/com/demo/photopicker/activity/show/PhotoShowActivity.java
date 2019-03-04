package com.demo.photopicker.activity.show;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.demo.photopicker.PhotoPicker;
import com.demo.photopicker.R;
import com.demo.photopicker.activity.preview.PhotoPreviewActivity;
import com.demo.photopicker.adapter.PhotoShowAdapter;
import com.demo.photopicker.model.PhotoEvent;
import com.demo.photopicker.model.PhotoFolderInfo;
import com.demo.photopicker.model.PhotoInfo;
import com.demo.photopicker.view.FloatCatalogView;
import com.gyf.barlibrary.ImmersionBar;

import java.util.List;

import de.greenrobot.event.EventBus;

import static com.demo.photopicker.PhotoPicker.PHOTO_EVENT_REPLACE_CROP_KEY;
import static com.demo.photopicker.PhotoPicker.PHOTO_EVENT_REPLACE_OLD_PHOTO_PATH;

public class PhotoShowActivity extends AppCompatActivity implements PhotoShowContract.View {

    private RelativeLayout imgBtBack;
    private TextView tvTitle;
    private TextView tvSend;
    private RecyclerView cyPhotoList;
    private TextView tvCatalog;
    private TextView tvPreview;
    private FloatCatalogView floatCatalogView;
    private ProgressBar progressBar;

    private PhotoShowPresenter presenter;
    private PhotoShowAdapter adapter;
    private int floatCatalogSelectPosition = 0;
    private boolean isCameraClick = false;
    protected ImmersionBar mImmersionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.photo_picker_activity_photo_show);
        EventBus.getDefault().register(this);
        initView();
        initListener();
        initPresenter();
    }

    private void fullScreen() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.init();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, PhotoShowActivity.class);
        context.startActivity(intent);
    }

    private void initListener() {
        //返回按钮
        imgBtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //目录按钮
        tvCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatCatalogView.toggleFloatCatalog();
            }
        });
        //预览按钮
        tvPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPreviewActivity.startWithSelectPhoto(PhotoShowActivity.this);
            }
        });

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvSend.isEnabled()) {
                    PhotoPicker.sendPhoto(PhotoShowActivity.this);
                    PhotoShowActivity.super.finish();
                }
            }
        });

    }

    private void initPresenter() {
        if (presenter == null) {
            presenter = new PhotoShowPresenter(this);
        }
        progressBar.setVisibility(View.VISIBLE);
        presenter.getPhotoList(this);
    }

    @SuppressLint("WrongViewCast")
    private void initView() {
        imgBtBack = findViewById(R.id.view_title_bar_back_button);
        tvTitle = (TextView) findViewById(R.id.view_title_bar_title_tv);
        tvSend = (TextView) findViewById(R.id.view_title_bar_select_button);
        GradientDrawable gradientDrawable = (GradientDrawable) tvSend.getBackground();
        gradientDrawable.setColor(PhotoPicker.getThemeColor(this));
        tvSend.setAlpha(0.5f);
        tvSend.setEnabled(false);
        cyPhotoList = (RecyclerView) findViewById(R.id.photo_picker_activity_image_show_recycler_view);
        cyPhotoList.setLayoutManager(new GridLayoutManager(this, PhotoPicker.getPhotoListSpanCount()));
        floatCatalogView = (FloatCatalogView) findViewById(R.id.photo_picker_activity_image_show_float_catalog_view);
        tvCatalog = (TextView) findViewById(R.id.photo_picker_activity_image_show_catalog);
        tvPreview = (TextView) findViewById(R.id.photo_picker_activity_image_show_preview);
        progressBar = findViewById(R.id.photo_picker_activity_image_show_progressBar);
    }

    private void initTopBottomTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
            tvCatalog.setText(title);
        }
    }

    private void dealBtStatesBySelectedPhoto(boolean hasSelectedPhoto) {
        if (hasSelectedPhoto) {
            tvSend.setAlpha(1);
            tvSend.setEnabled(true);
            tvPreview.setAlpha(1);
            tvPreview.setEnabled(true);
            if (PhotoPicker.isMultipleSelectType()) {
                tvSend.setText(getResources().getString(R.string.photo_picker_send) + "（" + PhotoPicker.PHOTO_SELECT_LIST.size() + "/" + PhotoPicker.getLimitPhotoCount() + "）");
                tvPreview.setText(getResources().getString(R.string.photo_picker_preview) + "（" + PhotoPicker.PHOTO_SELECT_LIST.size() + "）");
            } else {
                tvSend.setText(getResources().getString(R.string.photo_picker_send));
                tvPreview.setText(getResources().getString(R.string.photo_picker_preview));
            }
        } else {
            tvSend.setAlpha(0.5f);
            tvSend.setEnabled(false);
            tvSend.setText(getResources().getString(R.string.photo_picker_send));
            tvPreview.setAlpha(0.5f);
            tvPreview.setEnabled(false);
            tvPreview.setText(getResources().getString(R.string.photo_picker_preview));
        }
    }

    @Override
    public void onGetPhotoListSuccess(final List<PhotoFolderInfo> photoList) {
        progressBar.setVisibility(View.GONE);
        if (photoList != null && photoList.size() != 0) {
            initTopBottomTitle(photoList.get(0).getFolderName());
            if (adapter == null) {
                adapter = new PhotoShowAdapter(this, photoList.get(0).getPhotoList(), photoList.get(0).getFolderId());
                adapter.setCheckboxSelectedListener(new PhotoShowAdapter.OnPhotoCheckboxSelectedListener() {
                    @Override
                    public void onCheckboxSelected() {
                        //证明勾选了图片
                        if (PhotoPicker.PHOTO_SELECT_LIST.size() != 0) {
                            dealBtStatesBySelectedPhoto(true);
                        } else {
                            dealBtStatesBySelectedPhoto(false);
                        }
                    }

                    @Override
                    public void onCameraClick() {
                        isCameraClick = true;
                        finish();
                    }
                });
            }
            cyPhotoList.setAdapter(adapter);

            floatCatalogView.setCatalogList(photoList);
            floatCatalogView.setClickLlistener(new FloatCatalogView.OnCatalogClickLlistener() {
                @Override
                public void onCatalogItemClick(int selectedPosition) {
                    if (adapter != null) {
                        floatCatalogSelectPosition = selectedPosition;
                        adapter.notifyData(photoList.get(selectedPosition).getPhotoList(), photoList.get(selectedPosition).getFolderId());
                        initTopBottomTitle(photoList.get(selectedPosition).getFolderName());
                    }
                }
            });
        }
    }

    @Override
    public void onHandleCropPhotoSuccess(List<PhotoFolderInfo> photoList) {
        if (adapter != null) {
            adapter.notifyData(photoList.get(floatCatalogSelectPosition).getPhotoList(), photoList.get(floatCatalogSelectPosition).getFolderId());
        }
        if (floatCatalogView != null) {
            floatCatalogView.notifyData(photoList);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (!isCameraClick) {
            PhotoPicker.freeResource();
        } else {
            PhotoPicker.PHOTO_SELECT_LIST.clear();
        }
    }

    public void onEventMainThread(PhotoEvent event) {
        if (event.eventType == PhotoPicker.PHOTO_EVENT_NOTIFY_DATA) {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            if (PhotoPicker.PHOTO_SELECT_LIST.size() != 0) {
                dealBtStatesBySelectedPhoto(true);
            } else {
                dealBtStatesBySelectedPhoto(false);
            }
        } else if (event.eventType == PhotoPicker.PHOTO_EVENT_SEND_PHOTO) {
            super.finish();
        } else if (event.eventType == PhotoPicker.PHOTO_EVENT_REPLACE_CROP) {
            String oldPhotoPath = event.getData().getString(PHOTO_EVENT_REPLACE_OLD_PHOTO_PATH);
            PhotoInfo replacePhoto = (PhotoInfo) event.getData().getSerializable(PHOTO_EVENT_REPLACE_CROP_KEY);
            if (presenter != null) {
                presenter.handleCropPhoto(this, oldPhotoPath, replacePhoto);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mImmersionBar != null)
            mImmersionBar.destroy();
    }
}
