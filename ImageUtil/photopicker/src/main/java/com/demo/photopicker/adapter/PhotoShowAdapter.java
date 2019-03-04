package com.demo.photopicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.demo.photopicker.PhotoPicker;
import com.demo.photopicker.R;
import com.demo.photopicker.activity.preview.PhotoPreviewActivity;
import com.demo.photopicker.activity.TakePhotoActivity;
import com.demo.photopicker.model.PhotoInfo;
import com.demo.photopicker.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bjhl on 2018/6/11.
 */

public class PhotoShowAdapter extends RecyclerView.Adapter<PhotoShowAdapter.ViewHolder> {
    private List<PhotoInfo> photoList;
    private Context context;
    private int folderId;
    private OnPhotoCheckboxSelectedListener checkboxSelectedListener;
    private static final int PHOTO_ADAPTER_POSITION_CAMERA = 0;
    private static final int PHOTO_ADAPTER_POSITION_PHOTO = 1;

    public void notifyData(List<PhotoInfo> list, int folderId) {
        if (photoList == null) {
            photoList = new ArrayList<>();
        }
        if (photoList.size() != 0) {
            photoList.clear();
        }
        this.folderId = folderId;
        photoList.addAll(list);
        notifyDataSetChanged();
    }

    public PhotoShowAdapter(Context context, List<PhotoInfo> list, int folderId) {
        this.context = context;
        this.folderId = folderId;
        if (photoList == null) {
            photoList = new ArrayList<>();
        }
        this.photoList.addAll(list);
    }

    public void setCheckboxSelectedListener(OnPhotoCheckboxSelectedListener listener) {
        this.checkboxSelectedListener = listener;
    }


    @Override
    public int getItemCount() {
        if (PhotoPicker.getIsPhotoPreviewWithCamera())
            return photoList.size() + 1;
        return photoList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder;
        if (viewType == PHOTO_ADAPTER_POSITION_CAMERA) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_picker_item_photo_list_camera, parent, false);
            holder = new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_picker_item_photo_list_photo, parent, false);
            holder = new ViewHolder(view);
        }
        setHeightEqulsWidth(holder.itemView);
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        if (PhotoPicker.getIsPhotoPreviewWithCamera()) {
            if (position == 0) {
                return PHOTO_ADAPTER_POSITION_CAMERA;
            } else {
                return PHOTO_ADAPTER_POSITION_PHOTO;
            }
        } else {
            return PHOTO_ADAPTER_POSITION_PHOTO;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position == 0 && PhotoPicker.getIsPhotoPreviewWithCamera()) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TakePhotoActivity.start(context);
                    if (checkboxSelectedListener != null) {
                        checkboxSelectedListener.onCameraClick();
                    }
                }
            });
        } else {
            ImageView imageView = holder.itemView.findViewById(R.id.item_photo_list_photo_ci);
            final ImageButton checkBox = holder.itemView.findViewById(R.id.item_photo_list_photo_checkbox);
            final PhotoInfo photoInfo;
            if (PhotoPicker.getIsPhotoPreviewWithCamera()) {
                photoInfo = photoList.get(position - 1);
            } else {
                photoInfo = photoList.get(position);
            }

            if (PhotoPicker.getLimitPhotoCount() != 0) {
                checkBox.setVisibility(View.VISIBLE);
                boolean checked = PhotoPicker.PHOTO_SELECT_LIST.contains(photoInfo);
                if (checked) {
                    checkBox.setSelected(true);
                } else {
                    checkBox.setSelected(false);
                }
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (PhotoPicker.PHOTO_SELECT_LIST.size() < PhotoPicker.getLimitPhotoCount()) {
                            if (!checkBox.isSelected()) {
                                PhotoPicker.PHOTO_SELECT_LIST.add(photoInfo);
                            } else {
                                PhotoPicker.PHOTO_SELECT_LIST.remove(photoInfo);
                            }
                            checkBox.setSelected(!checkBox.isSelected());
                        } else {
                            if (checkBox.isSelected()) {
                                PhotoPicker.PHOTO_SELECT_LIST.remove(photoInfo);
                                checkBox.setSelected(false);
                            } else {
                                Toast.makeText(context, "最多只能选择" + PhotoPicker.getLimitPhotoCount() + "张图片", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (checkboxSelectedListener != null) {
                            checkboxSelectedListener.onCheckboxSelected();
                        }
                    }
                });
            } else {
                checkBox.setVisibility(View.GONE);
            }
            ImageUtil.with(context).placeholder(R.drawable.photo_picker_ic_placeholder).load(photoInfo.getPhotoPath(), imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PhotoPreviewActivity.startWithWholeCatalog(context, folderId, photoInfo.getPhotoPath());
                }
            });
        }
    }

    private void setHeightEqulsWidth(final View view) {
        view.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = view.getWidth();
                view.setLayoutParams(params);
            }
        });
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnPhotoCheckboxSelectedListener {
        void onCheckboxSelected();

        void onCameraClick();
    }
}
