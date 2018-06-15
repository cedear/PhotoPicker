package com.demo.photopicker.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.demo.photopicker.R;
import com.demo.photopicker.model.PhotoInfo;

import java.util.ArrayList;
import java.util.List;

import static com.demo.photopicker.PhotoPicker.THUMBNAIL_POSITION_CANT_REACH;


/**
 * Created by bjhl on 2018/6/5.
 */

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder>{

    private List<PhotoInfo> thumbnailList;
    private Context context;
    private int clickPosition = THUMBNAIL_POSITION_CANT_REACH;
    private OnThumbnailClickListener thumbnailClickListener;

    public void setThumbnailClickListener(OnThumbnailClickListener listener) {
        this.thumbnailClickListener = listener;
    }

    public void setCurrentThumbnail(int currentPosition) {
        clickPosition = currentPosition;
        notifyDataSetChanged();
    }

    public void notifyData(List<PhotoInfo> list, int clickPosition) {
        if (thumbnailList == null) {
            thumbnailList = new ArrayList<>();
        }
        if (thumbnailList.size() != 0) {
            thumbnailList.clear();
        }
        thumbnailList.addAll(list);
        this.clickPosition = clickPosition;
        notifyDataSetChanged();
    }

    public ThumbnailAdapter(Context context, List<PhotoInfo> thumbnailData, int defaultClickPosition) {
        this.context = context;
        if (thumbnailList == null)
            thumbnailList = new ArrayList<>();
        thumbnailList.addAll(thumbnailData);
        clickPosition = defaultClickPosition;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_picker_item_preview_thumbnail, parent, false);
        holder = new ViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ImageView imageView = holder.itemView.findViewById(R.id.preview_thumbnail_image_view);
        Glide.with(context).load(thumbnailList.get(position).getPhotoPath()).into(imageView);
        FrameLayout maskFrameLayout = holder.itemView.findViewById(R.id.preview_thumbnail_mask);
        if (clickPosition == position) {
            maskFrameLayout.setVisibility(View.VISIBLE);
        } else {
            maskFrameLayout.setVisibility(View.GONE);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thumbnailClickListener != null) {
                    thumbnailClickListener.onThumbnailClick(thumbnailList.get(position));
                }
                clickPosition = position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return thumbnailList == null ? 0 : thumbnailList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnThumbnailClickListener {
        void onThumbnailClick(PhotoInfo photoInfo);
    }
}
