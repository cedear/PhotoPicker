package com.demo.photopicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.demo.photopicker.PhotoPicker;
import com.demo.photopicker.R;
import com.demo.photopicker.model.PhotoFolderInfo;
import com.demo.photopicker.view.PhotoPickerCircleColorView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bjhl on 2018/6/4.
 */

public class FloatCatalogAdapter extends RecyclerView.Adapter<FloatCatalogAdapter.ViewHolder> {

    List<PhotoFolderInfo> mAllPhotoFolders;
    Context context;
    OnCatalogItemClickListener clickListener;
    int mSelectedPosition = 0;

    public FloatCatalogAdapter(Context context, List<PhotoFolderInfo> list) {
        this.context = context;
        if (mAllPhotoFolders == null) {
            mAllPhotoFolders = new ArrayList<>();
        }
        mAllPhotoFolders.addAll(list);
    }

    public void notifyData(List<PhotoFolderInfo> photoFolders) {
        if (mAllPhotoFolders != null && mAllPhotoFolders.size() != 0) {
            mAllPhotoFolders.clear();
        }
        mAllPhotoFolders.addAll(photoFolders);
        notifyDataSetChanged();
    }


    public void setCatalogItemClickListener(OnCatalogItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_picker_item_float_catalog, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        PhotoFolderInfo folderInfo = mAllPhotoFolders.get(position);
        Glide.with(context).load(folderInfo.getCoverPhoto().getPhotoPath()).into(holder.folderAvatar);

        holder.folderName.setText(folderInfo.getFolderName());
        holder.picNumber.setText(folderInfo.getPhotoList().size() + "张");

        //目录item点击，小圆点的显示
        holder.checkedView.setColor(PhotoPicker.getThemeColor(context));
        if (mSelectedPosition == position) {
            holder.checkedView.setVisibility(View.VISIBLE);
        } else {
            holder.checkedView.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSelectedPosition = position;
//                mSelectFolder = mAllPhotoFolders.get(position);
//                mViewModel.setCurrentFolder(mSelectFolder);
                notifyDataSetChanged();
                if (clickListener != null) {
                    clickListener.onCatalogItemClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mAllPhotoFolders == null)
            return 0;
        return mAllPhotoFolders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView folderAvatar;
        private TextView folderName;
        private TextView picNumber;
        private PhotoPickerCircleColorView checkedView;

        public ViewHolder(View itemView) {
            super(itemView);

            folderAvatar = (ImageView) itemView.findViewById(R.id.item_photo_folder_img_ci);
            folderName = (TextView) itemView.findViewById(R.id.item_photo_folder_name_tv);
            picNumber = (TextView) itemView.findViewById(R.id.item_photo_folder_num_tv);
            checkedView = (PhotoPickerCircleColorView) itemView.findViewById(R.id.item_photo_folder_checked_cc);
            checkedView.setSelected(true);
        }
    }

    public interface OnCatalogItemClickListener {
        void onCatalogItemClick(int selectedPosition);
    }
}
