package com.demo.photopicker.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.photopicker.PhotoPicker;
import com.demo.photopicker.R;
import com.demo.photopicker.adapter.PhotoPreviewViewPagerAdapter;
import com.demo.photopicker.adapter.ThumbnailAdapter;
import com.demo.photopicker.model.PhotoEvent;
import com.demo.photopicker.model.PhotoInfo;
import com.demo.photopicker.util.ScreenUtil;
import com.demo.photopicker.view.PhotoPreviewViewPager;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

import static com.demo.photopicker.PhotoPicker.THUMBNAIL_POSITION_CANT_REACH;

public class PhotoPreviewActivity extends AppCompatActivity implements PhotoPreviewContract.View{

    private PhotoPreviewPresenter presenter;

    private ImageButton imgBtBack;
    private TextView tvTitle;
    private TextView tvSend;
    private RelativeLayout titleBarContainer;
    private PhotoPreviewViewPager viewPager;
    private RelativeLayout previewContainer;
    private RelativeLayout chooseContainer;
    private ImageButton chooseCheckBox;
    private RelativeLayout thumbnailContainer;      //缩略图容器
    private RecyclerView thumbnailRecyclerView;                 //缩略图list
    public static final String PREVIEW_DATA_TYPE = "PREVIEW_DATA_TYPE";
    public static final int PHOTO_PREVIEW_TYPE_SELECTED_ONLY = 0;
    public static final int PHOTO_PREVIEW_TYPE_WHOLE_CATALOG = 1;
    public static final String BIG_PICTURE_PREVIEW_DATA = "BIG_PICTURE_PREVIEW_DATA";
    public static final String BIG_PICTURE_CLICK_PATH = "BIG_PICTURE_CLICK_PATH";

    private PhotoPreviewViewPagerAdapter viewPagerAdapter;
    private ThumbnailAdapter thumbnailAdapter;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏颜色
        getWindow().setStatusBarColor(getResources().getColor(R.color.photo_picker_color_3D3D3E));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        setContentView(R.layout.photo_picker_activity_photo_preview);
        initView();
        initListener();
        initPresenter();
    }

    private void initListener() {
        imgBtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        chooseContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPagerAdapter != null && presenter != null) {
                    PhotoInfo currentPhoto = viewPagerAdapter.getCurrentItemPhotoInfo(presenter.getCurrentPositionInViewPager());
                    if (PhotoPicker.PHOTO_SELECT_LIST.size() < PhotoPicker.getLimitPhotoCount()) {
                        if (!chooseCheckBox.isSelected()) {
                            PhotoPicker.PHOTO_SELECT_LIST.put(currentPhoto.getPhotoPath(), currentPhoto);
                            if (thumbnailAdapter == null) {
                                thumbnailAdapter = new ThumbnailAdapter(PhotoPreviewActivity.this, presenter.mapToList(PhotoPicker.PHOTO_SELECT_LIST), PhotoPicker.PHOTO_SELECT_LIST.size() - 1);
                                thumbnailAdapter.setThumbnailClickListener(new ThumbnailAdapter.OnThumbnailClickListener() {
                                    @Override
                                    public void onThumbnailClick(PhotoInfo photoInfo) {
                                        if (viewPagerAdapter != null) {
                                            viewPager.setCurrentItem(viewPagerAdapter.getPhotoInfoPosition(photoInfo), false);
                                        }
                                    }
                                });
                                thumbnailRecyclerView.setAdapter(thumbnailAdapter);
                            } else {
                                thumbnailAdapter.notifyData(presenter.mapToList(PhotoPicker.PHOTO_SELECT_LIST), PhotoPicker.PHOTO_SELECT_LIST.size() - 1);
                            }
                        } else {
                            PhotoPicker.PHOTO_SELECT_LIST.remove(currentPhoto.getPhotoPath());
                            if (thumbnailAdapter != null) {
                                thumbnailAdapter.notifyData(presenter.mapToList(PhotoPicker.PHOTO_SELECT_LIST), THUMBNAIL_POSITION_CANT_REACH);
                            }
                        }
                        chooseCheckBox.setSelected(!chooseCheckBox.isSelected());
                    } else {
                        if (chooseCheckBox.isSelected()) {
                            PhotoPicker.PHOTO_SELECT_LIST.remove(currentPhoto.getPhotoPath());
                            chooseCheckBox.setSelected(false);
                            if (thumbnailAdapter != null) {
                                thumbnailAdapter.notifyData(presenter.mapToList(PhotoPicker.PHOTO_SELECT_LIST), THUMBNAIL_POSITION_CANT_REACH);
                            }
                        } else {
                            Toast.makeText(PhotoPreviewActivity.this, "最多只能选择" + PhotoPicker.getLimitPhotoCount() + "张图片", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                EventBus.getDefault().post(new PhotoEvent(PhotoPicker.PHOTO_EVENT_NOTIFY_DATA));
                //todo 发送按钮状态更改
                if (PhotoPicker.PHOTO_SELECT_LIST.size() != 0) {
                    if (thumbnailContainer.getVisibility() == View.GONE)
                        thumbnailContainer.setVisibility(View.VISIBLE);
                    initBtSendState(true);
                } else {
                    thumbnailContainer.setVisibility(View.GONE);
                    initBtSendState(false);
                }
            }
        });

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvSend.isEnabled()) {
                    //直接发送当前图片
                    if (PhotoPicker.PHOTO_SELECT_LIST.size() == 0) {
                        if (viewPagerAdapter != null && presenter != null) {
                            PhotoInfo currentPhoto = viewPagerAdapter.getCurrentItemPhotoInfo(presenter.getCurrentPositionInViewPager());
                            PhotoPicker.PHOTO_SELECT_LIST.put(currentPhoto.getPhotoPath(), currentPhoto);
                        }
                    }
                    PhotoPicker.sendPhoto();
                    EventBus.getDefault().post(new PhotoEvent(PhotoPicker.PHOTO_EVENT_SEND_PHOTO));
                    finish();
                }
            }
        });
    }

    private void initPresenter() {
        if (presenter == null) {
            presenter = new PhotoPreviewPresenter(this);
        }
        presenter.getPhotoPreviewData(getIntent().getExtras());
    }

    public static void startWithSelectPhoto(Context context) {
        Intent intent = new Intent(context, PhotoPreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(PREVIEW_DATA_TYPE, PHOTO_PREVIEW_TYPE_SELECTED_ONLY);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void startWithWholeCatalog(Context context, ArrayList<PhotoInfo> catalogList, String clickPhotoPath) {
        Intent intent = new Intent(context, PhotoPreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(PREVIEW_DATA_TYPE, PHOTO_PREVIEW_TYPE_WHOLE_CATALOG);
        bundle.putSerializable(BIG_PICTURE_PREVIEW_DATA, catalogList);
        bundle.putString(BIG_PICTURE_CLICK_PATH, clickPhotoPath);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private void initView() {
        imgBtBack = (ImageButton) findViewById(R.id.view_title_bar_back_button);
        tvTitle = (TextView) findViewById(R.id.view_title_bar_title_tv);
        tvSend = (TextView) findViewById(R.id.view_title_bar_select_button);
        tvSend.setBackgroundColor(PhotoPicker.getThemeColor());
        titleBarContainer = (RelativeLayout) findViewById(R.id.photo_picker_title_bar);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) titleBarContainer.getLayoutParams();
        params.setMargins(0, ScreenUtil.getStatusBarHeight(this), 0, 0);
        titleBarContainer.setLayoutParams(params);
        viewPager = (PhotoPreviewViewPager) findViewById(R.id.activity_photo_preview_view_pager);
        previewContainer = (RelativeLayout) findViewById(R.id.activity_photo_preview_container);
        chooseContainer = (RelativeLayout) findViewById(R.id.activity_photo_preview_choose_container);
        chooseCheckBox = (ImageButton) findViewById(R.id.activity_photo_preview_checkbox);
        thumbnailContainer = (RelativeLayout) findViewById(R.id.activity_photo_preview_thumbnail_container);
        thumbnailRecyclerView = (RecyclerView) findViewById(R.id.activity_photo_preview_thumbnail_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        thumbnailRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onGetBigPicturePreviewData(final List<PhotoInfo> bigPictureData, int selectedPosition) {
        if (bigPictureData != null && bigPictureData.size() != 0) {
            if (viewPagerAdapter == null) {
                viewPagerAdapter = new PhotoPreviewViewPagerAdapter(this, bigPictureData);
                viewPagerAdapter.setGalleryViewClickListener(new PhotoPreviewViewPagerAdapter.OnGalleryViewClickListener() {
                    @Override
                    public void onGalleryViewClick() {
                        //todo 显示隐藏上下状态栏
                        toggleFloatView();
                    }
                });
            }
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    Log.e("y_tag","dsdfasdfsdfsdfs");
                    //todo 更改页数指示
                    tvTitle.setText(position + 1 + "/" + bigPictureData.size());
                    //todo 选择按钮动态更改状态
                    if (presenter != null) {
                        //查看当前viewpager的项是否已在所选的list中
                        presenter.currentPageEqualsSelected(bigPictureData.get(position), position);
                        //当前viewpager的项已在所选的list中
                        if (presenter.getViewPagerItemInSelectedList()) {
                            // 改变选择按钮的状态
                            if (!chooseCheckBox.isSelected()) {
                                chooseCheckBox.setSelected(true);
                            }
                        } else {
                            if (chooseCheckBox.isSelected()) {
                                chooseCheckBox.setSelected(false);
                            }
                        }
                        //todo 缩略图更改状态
                        if (thumbnailAdapter != null) {
                            thumbnailAdapter.setCurrentThumbnail(presenter.getViewPagerItemPositionInSelectedList());
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.setCurrentItem(selectedPosition);
        }
    }

    @Override
    public void onGetThumbnailPreviewData(List<PhotoInfo> thumbnailData, int selectedPosition) {
        if (thumbnailData != null && thumbnailData.size() != 0) {
            initBtSendState(true);
            thumbnailContainer.setVisibility(View.VISIBLE);
            if (thumbnailAdapter == null) {
                thumbnailAdapter = new ThumbnailAdapter(this, thumbnailData, selectedPosition);
                thumbnailAdapter.setThumbnailClickListener(new ThumbnailAdapter.OnThumbnailClickListener() {
                    @Override
                    public void onThumbnailClick(PhotoInfo photoInfo) {
                        // todo viewPager跳转到当前页
                        if (viewPagerAdapter != null) {
                            viewPager.setCurrentItem(viewPagerAdapter.getPhotoInfoPosition(photoInfo), false);
                        }
                    }
                });
            }
            thumbnailRecyclerView.setAdapter(thumbnailAdapter);

        } else {
            initBtSendState(false);
            thumbnailContainer.setVisibility(View.GONE);
        }
    }

    private void initBtSendState(boolean hasSelectedPhoto) {
        if (hasSelectedPhoto) {
            if (PhotoPicker.isMutilSelectType()) {
                tvSend.setText("发送" + "（" + PhotoPicker.PHOTO_SELECT_LIST.size() + "/" + PhotoPicker.getLimitPhotoCount() + "）");
            } else {
                tvSend.setText(getResources().getString(R.string.photo_picker_send));
            }
        } else {
            tvSend.setText(getResources().getString(R.string.photo_picker_send));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void toggleFloatView(){
        Animation animation;
        if (titleBarContainer.getVisibility() == View.VISIBLE || previewContainer.getVisibility() == View.VISIBLE) {
            animation = AnimationUtils.loadAnimation(this, R.anim.photo_picker_anim_titlebar_bottom_to_up);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    hideStatusBar(getWindow());
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            titleBarContainer.setVisibility(View.GONE);
            titleBarContainer.startAnimation(animation);
            previewContainer.setVisibility(View.GONE);

        } else {
            showStatusBar(getWindow());
            titleBarContainer.setVisibility(View.VISIBLE);
            titleBarContainer.startAnimation(AnimationUtils.loadAnimation(this, R.anim.photo_picker_anim_titlebar_up_to_bottom));
            previewContainer.setVisibility(View.VISIBLE);
        }
    }

    private static void hideStatusBar(Window window){

//        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE| //保持布局状态
//                View.SYSTEM_UI_FLAG_FULLSCREEN;
//        window.getDecorView().setSystemUiVisibility(uiOptions);
//        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private static void showStatusBar(Window window){
//        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
