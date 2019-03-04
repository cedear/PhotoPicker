package com.demo.photopicker.activity.preview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.photopicker.PhotoPicker;
import com.demo.photopicker.R;
import com.demo.photopicker.activity.crop.imaging.IMGEditActivity;
import com.demo.photopicker.adapter.PhotoPreviewViewPagerAdapter;
import com.demo.photopicker.adapter.ThumbnailAdapter;
import com.demo.photopicker.model.PhotoEvent;
import com.demo.photopicker.model.PhotoInfo;
import com.demo.photopicker.util.MediaScanner;
import com.demo.photopicker.util.PhotoUtil;
import com.demo.photopicker.view.PhotoPreviewViewPager;
import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;

import static com.demo.photopicker.PhotoPicker.PHOTO_EVENT_REPLACE_CROP;
import static com.demo.photopicker.PhotoPicker.PHOTO_EVENT_REPLACE_CROP_KEY;
import static com.demo.photopicker.PhotoPicker.PHOTO_EVENT_REPLACE_OLD_PHOTO_PATH;
import static com.demo.photopicker.PhotoPicker.THUMBNAIL_POSITION_CANT_REACH;

public class PhotoPreviewActivity extends AppCompatActivity implements PhotoPreviewContract.View {

    private PhotoPreviewPresenter presenter;

    private RelativeLayout imgBtBack;
    private TextView tvTitle;
    private TextView tvSend;
    private RelativeLayout titleBarContainer;
    private PhotoPreviewViewPager viewPager;
    private RelativeLayout previewContainer;
    private TextView tvCrop;
    private RelativeLayout chooseContainer;
    private ImageButton chooseCheckBox;
    private RelativeLayout thumbnailContainer;      //缩略图容器
    private RecyclerView thumbnailRecyclerView;                 //缩略图list
    public static final String PREVIEW_DATA_TYPE = "PREVIEW_DATA_TYPE";
    public static final int PHOTO_PREVIEW_TYPE_SELECTED_ONLY = 0;
    public static final int PHOTO_PREVIEW_TYPE_WHOLE_CATALOG = 1;
    public static final String BIG_PICTURE_DATA_FOLDER_ID = "BIG_PICTURE_DATA_FOLDER_ID";
    public static final String BIG_PICTURE_CLICK_PATH = "BIG_PICTURE_CLICK_PATH";
    public static final int REQUEST_CODE_CROP = 10000;
    private MediaScanner mMediaScanner;
    protected ImmersionBar mImmersionBar;

    private PhotoPreviewViewPagerAdapter viewPagerAdapter;
    private ThumbnailAdapter thumbnailAdapter;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏颜色
        fullScreen();
//        getWindow().setStatusBarColor(getResources().getColor(R.color.photo_picker_color_3D3D3E));
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        setContentView(R.layout.photo_picker_activity_photo_preview);
        initView();
        initListener();
        initPresenter();
    }

    private void fullScreen() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.init();
    }

    private void initListener() {
        //返回按钮点击
        imgBtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //选择按钮点击
        chooseContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPagerAdapter != null && presenter != null) {
                    PhotoInfo currentPhoto = viewPagerAdapter.getCurrentItemPhotoInfo(presenter.getViewPageCurrentPosition());
                    if (PhotoPicker.PHOTO_SELECT_LIST.size() < PhotoPicker.getLimitPhotoCount()) {
                        if (!chooseCheckBox.isSelected()) {
                            PhotoPicker.PHOTO_SELECT_LIST.add(currentPhoto);
                            if (thumbnailAdapter == null) {
                                thumbnailAdapter = new ThumbnailAdapter(PhotoPreviewActivity.this, PhotoPicker.PHOTO_SELECT_LIST, PhotoPicker.PHOTO_SELECT_LIST.size() - 1);
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
                                thumbnailAdapter.notifyData(PhotoPicker.PHOTO_SELECT_LIST, PhotoPicker.PHOTO_SELECT_LIST.size() - 1);
                            }
                            thumbnailRecyclerView.scrollToPosition(PhotoPicker.PHOTO_SELECT_LIST.size() - 1);
                        } else {
                            if (PhotoPicker.PHOTO_SELECT_LIST.remove(currentPhoto)) {
                                if (thumbnailAdapter != null) {
                                    thumbnailAdapter.notifyData(PhotoPicker.PHOTO_SELECT_LIST, THUMBNAIL_POSITION_CANT_REACH);
                                }
                            }
                        }
                        chooseCheckBox.setSelected(!chooseCheckBox.isSelected());
                    } else {
                        if (chooseCheckBox.isSelected()) {
                            PhotoPicker.PHOTO_SELECT_LIST.remove(currentPhoto);
                            chooseCheckBox.setSelected(false);
                            if (thumbnailAdapter != null) {
                                thumbnailAdapter.notifyData(PhotoPicker.PHOTO_SELECT_LIST, THUMBNAIL_POSITION_CANT_REACH);
                            }
                        } else {
                            Toast.makeText(PhotoPreviewActivity.this, "最多只能选择" + PhotoPicker.getLimitPhotoCount() + "张图片", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                EventBus.getDefault().post(new PhotoEvent(PhotoPicker.PHOTO_EVENT_NOTIFY_DATA));
                //todo 发送按钮状态更改（√）
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

        //裁剪按钮点击
        tvCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPagerAdapter != null && presenter != null) {
                    SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                    String name = f.format(new Date());
                    presenter.setCropPhotoPath(PhotoUtil.getTakePhotoDir().getPath() + File.separator + "CROP" + name + ".jpeg");
                    PhotoInfo currentPhoto = viewPagerAdapter.getCurrentItemPhotoInfo(presenter.getViewPageCurrentPosition());
                    Intent action;
                    action = new Intent(PhotoPreviewActivity.this, IMGEditActivity.class);
                    action.putExtra(IMGEditActivity.EXTRA_IMAGE_URI, currentPhoto.getUri());
                    action.putExtra(IMGEditActivity.EXTRA_IMAGE_SAVE_PATH, presenter.getCropPhotoPath());
                    PhotoPreviewActivity.this.startActivityForResult(action, REQUEST_CODE_CROP);
                }
            }
        });

        //发送按钮点击
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvSend.isEnabled()) {
                    //直接发送当前图片
                    if (PhotoPicker.PHOTO_SELECT_LIST.size() == 0) {
                        if (viewPagerAdapter != null && presenter != null) {
                            PhotoInfo currentPhoto = viewPagerAdapter.getCurrentItemPhotoInfo(presenter.getViewPageCurrentPosition());
                            PhotoPicker.PHOTO_SELECT_LIST.add(currentPhoto);
                        }
                    }
                    PhotoPicker.sendPhoto(PhotoPreviewActivity.this);
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

    public static void startWithWholeCatalog(Context context, int folderId, String clickPhotoPath) {
        Intent intent = new Intent(context, PhotoPreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(PREVIEW_DATA_TYPE, PHOTO_PREVIEW_TYPE_WHOLE_CATALOG);
        bundle.putInt(BIG_PICTURE_DATA_FOLDER_ID, folderId);
        bundle.putString(BIG_PICTURE_CLICK_PATH, clickPhotoPath);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private void initView() {
        imgBtBack = findViewById(R.id.view_title_bar_back_button);
        tvTitle = (TextView) findViewById(R.id.view_title_bar_title_tv);
        tvSend = (TextView) findViewById(R.id.view_title_bar_select_button);
        GradientDrawable gradientDrawable = (GradientDrawable) tvSend.getBackground();
        gradientDrawable.setColor(PhotoPicker.getThemeColor(this));
        titleBarContainer = (RelativeLayout) findViewById(R.id.photo_picker_title_bar);
        viewPager = (PhotoPreviewViewPager) findViewById(R.id.activity_photo_preview_view_pager);
        previewContainer = (RelativeLayout) findViewById(R.id.activity_photo_preview_container);
        tvCrop = findViewById(R.id.photo_preview_activity_crop);
        if (PhotoPicker.getIsOpenCropType()) {
            tvCrop.setVisibility(View.VISIBLE);
        } else {
            tvCrop.setVisibility(View.GONE);
        }
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
            final int totalSize = bigPictureData.size();
            if (viewPagerAdapter == null) {
                viewPagerAdapter = new PhotoPreviewViewPagerAdapter(this, bigPictureData);
                viewPagerAdapter.setGalleryViewClickListener(new PhotoPreviewViewPagerAdapter.OnGalleryViewClickListener() {
                    @Override
                    public void onGalleryViewClick() {
                        //todo 显示隐藏上下状态栏（√）
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

                    //todo 更改页数指示（√）
                    tvTitle.setText(position + 1 + "/" + totalSize);
                    //todo 选择按钮动态更改状态（√）
                    if (presenter != null) {
                        presenter.setViewPageCurrentPosition(position);
                        //查看当前viewpager的项是否已在所选的list中
                        presenter.currentPageEqualsSelected(viewPagerAdapter.getDataList().get(position));
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
                        //todo 缩略图更改状态（√）
                        if (thumbnailAdapter != null) {
                            int selectedPosition = presenter.getViewPagerItemPositionInSelectedList();
                            thumbnailAdapter.setCurrentThumbnail(selectedPosition);
                            thumbnailRecyclerView.scrollToPosition(selectedPosition);
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
                        // todo viewPager跳转到当前页（√）
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
            if (PhotoPicker.isMultipleSelectType()) {
                tvSend.setText("发送" + "（" + PhotoPicker.PHOTO_SELECT_LIST.size() + "/" + PhotoPicker.getLimitPhotoCount() + "）");
            } else {
                tvSend.setText(getResources().getString(R.string.photo_picker_send));
            }
        } else {
            tvSend.setText(getResources().getString(R.string.photo_picker_send));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CROP) {
                //更新相册
                updateSystemGallery();

                PhotoInfo cropPhoto = PhotoUtil.getPhotoInfoByPath(presenter.getCropPhotoPath());
                if (cropPhoto != null && viewPagerAdapter != null && presenter != null) {
                    //查看裁剪之前的图是否被选中，选中的话替换成裁剪之后的图（缩略图中）
                    PhotoInfo oldPhoto = viewPagerAdapter.getCurrentItemPhotoInfo(presenter.getViewPageCurrentPosition());
                    if (PhotoPicker.PHOTO_SELECT_LIST.contains(oldPhoto)) {
                        int deleteIndex = presenter.getCurrentPhotoPositionInSelected(oldPhoto);
                        PhotoPicker.PHOTO_SELECT_LIST.remove(deleteIndex);
                        PhotoPicker.PHOTO_SELECT_LIST.add(deleteIndex, cropPhoto);
                        thumbnailAdapter.notifyData(PhotoPicker.PHOTO_SELECT_LIST, deleteIndex);
                    }
                    //处理截图（viewpager中的大图）
                    onPhotoCropDone(cropPhoto);
                    //把PhotoShowActivity中的图替换成新图
                    onReplaceCropInPhotoShowActivity(oldPhoto.getPhotoPath(), cropPhoto);
                }
            }
        }
    }

    private void onReplaceCropInPhotoShowActivity(String oldPhotoPath, PhotoInfo cropPhoto) {
        PhotoEvent replaceEvent = new PhotoEvent(PHOTO_EVENT_REPLACE_CROP);
        replaceEvent.writeString(PHOTO_EVENT_REPLACE_OLD_PHOTO_PATH, oldPhotoPath);
        replaceEvent.writeSerializable(PHOTO_EVENT_REPLACE_CROP_KEY, cropPhoto);
        EventBus.getDefault().post(replaceEvent);
    }

    private void updateSystemGallery() {
        if (mMediaScanner == null) {
            mMediaScanner = new MediaScanner(this);
        }
        if (presenter != null) {
            mMediaScanner.scanFile(presenter.getCropPhotoPath(), "image/jpeg");
        }
    }

    private void onPhotoCropDone(PhotoInfo cropPhoto) {
        if (viewPagerAdapter != null && presenter != null) {
            viewPagerAdapter.updateViewPagerItem(cropPhoto, presenter.getViewPageCurrentPosition());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null)
            mImmersionBar.destroy();
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
                    hideStatusBar();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            titleBarContainer.setVisibility(View.GONE);
            titleBarContainer.startAnimation(animation);
            previewContainer.setVisibility(View.GONE);

        } else {
            showStatusBar();
            titleBarContainer.setVisibility(View.VISIBLE);
            titleBarContainer.startAnimation(AnimationUtils.loadAnimation(this, R.anim.photo_picker_anim_titlebar_up_to_bottom));
            previewContainer.setVisibility(View.VISIBLE);
        }
    }

    private void hideStatusBar(){
        mImmersionBar.hideBar(BarHide.FLAG_HIDE_BAR).init();
    }

    private void showStatusBar(){
        mImmersionBar.hideBar(BarHide.FLAG_SHOW_BAR).init();
    }

}
