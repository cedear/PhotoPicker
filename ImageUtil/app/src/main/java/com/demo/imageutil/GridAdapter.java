package com.demo.imageutil;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.demo.photopicker.activity.GalleryViewActivity;
import com.demo.photopicker.model.PhotoInfo;
import com.demo.photopicker.util.GalleryViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bjhl on 2018/6/7.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    private List<PhotoInfo> datalist;
    private Context context;

    public GridAdapter(Context context, List<PhotoInfo> list) {
        this.context = context;
        if (datalist == null) {
            datalist = new ArrayList<>();
        }
        datalist.addAll(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_grid_view, parent, false);
        viewHolder = new ViewHolder(view);
//        setHeightEqulsWidth(viewHolder.itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ImageView imageView = holder.itemView.findViewById(R.id.item_photo_list_photo_ci);
        Glide.with(context).load(datalist.get(position).getPhotoResource()).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryViewActivity.launch(context, datalist, GalleryViewUtil.getGalleryPhotoParameterModel(position, datalist.get(position).getPhotoResource(), imageView), true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datalist == null ? 0 : datalist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
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
}
