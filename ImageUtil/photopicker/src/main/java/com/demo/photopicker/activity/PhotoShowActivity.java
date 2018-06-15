package com.demo.photopicker.activity;

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
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.demo.photopicker.PhotoPicker;
import com.demo.photopicker.R;
import com.demo.photopicker.adapter.PhotoShowAdapter;
import com.demo.photopicker.model.PhotoEvent;
import com.demo.photopicker.model.PhotoFolderInfo;
import com.demo.photopicker.view.FloatCatalogView;

import java.util.List;

import de.greenrobot.event.EventBus;

public class PhotoShowActivity extends AppCompatActivity implements PhotoShowContract.View{

    private RelativeLayout imgBtBack;
    private TextView tvTitle;
    private TextView tvSend;
    private RecyclerView cyPhotoList;
    private TextView tvCatalog;
    private TextView tvPreview;
    private FloatCatalogView floatCatalogView;

    private PhotoShowPresenter presenter;
    private PhotoShowAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_picker_activity_photo_show);
        EventBus.getDefault().register(this);
        initView();
        initListener();
        initPresenter();
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
                    PhotoPicker.sendPhoto();
                    finish();
                }
            }
        });

    }

    private void initPresenter() {
        if (presenter == null) {
            presenter = new PhotoShowPresenter(this);
        }
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
            if (PhotoPicker.isMutilSelectType()) {
                tvSend.setText("发送" + "（" + PhotoPicker.PHOTO_SELECT_LIST.size() + "/" + PhotoPicker.getLimitPhotoCount() + "）");
                tvPreview.setText("预览" + "（" + PhotoPicker.PHOTO_SELECT_LIST.size() + "）");
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
        if (photoList != null && photoList.size() != 0) {
            initTopBottomTitle(photoList.get(0).getFolderName());
            if (adapter == null) {
                adapter = new PhotoShowAdapter(this, photoList.get(0).getPhotoList());
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
                        adapter.notifyData(photoList.get(selectedPosition).getPhotoList());
                        initTopBottomTitle(photoList.get(selectedPosition).getFolderName());
                    }
                }
            });
        }
    }

    @Override
    public void finish() {
        super.finish();
        PhotoPicker.freeResource();
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
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
