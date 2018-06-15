package com.demo.imageutil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.demo.photopicker.PhotoPicker;
import com.demo.photopicker.model.PhotoInfo;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private GridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        findViewById(R.id.bt_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPicker
                        .getPhotoPicker()
                        .setPhotoSpanCount(4)
                        .setMaxPhotoCounts(3)
                        .setGetPhotoPickerCallBack(new PhotoPicker.OnGetPhotoPickerCallBack() {
                            @Override
                            public void onGetPhotoPickerSuccess(List<PhotoInfo> photoList) {
                                if (adapter == null) {
                                    adapter = new GridAdapter(MainActivity.this, photoList);
                                    recyclerView.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onGetPhotoPickerFail() {

                            }
                        })
                        .startSelectPhoto(MainActivity.this);
            }
        });
        findViewById(R.id.bt_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPicker.getPhotoPicker()
                        .setGetPhotoPickerCallBack(new PhotoPicker.OnGetPhotoPickerCallBack() {
                            @Override
                            public void onGetPhotoPickerSuccess(List<PhotoInfo> photoList) {
                                if (adapter == null) {
                                    adapter = new GridAdapter(MainActivity.this, photoList);
                                    recyclerView.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onGetPhotoPickerFail() {

                            }
                        })
                        .startTakePhoto(MainActivity.this);
            }
        });
    }
}
