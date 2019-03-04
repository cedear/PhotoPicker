## PhotoPicker使用说明书  
PhotoPicker是一款高仿微信的图片选取基础库，逼真程度高达99.9%。主要功能包括：从相册选取图片和调用相机拍照。 (其中图片编辑参考了kareluo的方案，这是大佬的blog:https://github.com/kareluo/Imaging) 
### 如何使用  
#### 图片选取功能  
 **1. 获取PhotoPicker实例 ** 

```
            PhotoPicker.getPhotoPicker()
```  
**2. 配置相关属性**  

```
            PhotoPicker
                        .getPhotoPicker()
                        .setMaxPhotoCounts(3)
                        .setIsPhotoPreviewWithCamera(true)
                        .setIsOpenCropType(true)
                        .setPhotoPickerCallBack(new PhotoPicker.OnGetPhotoPickerCallBack() {
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
```    
**目前支持属性：**  
>**setIsMutilSelectType:** 是否开启图片多选模式（默认是开启的）  
**setMaxPhotoCounts：**   选取图片的张数（前提：setIsMutilSelectType不能设置为false，否则会失效。默认为9张）  
**setThemeColor：** 主题颜色，主要是相关按钮、背景、边框的颜色（默认颜色为FF6C00）  
**setPhotoSpanCount：** 图片展示时，采用几列展示方式展示。  
**setIsPhotoPreviewWithCamera:** 在图片展示页面的第一个位置是否为照相机（默认是不展示）  
**setIsOpenCropType：** 是否开启图片编辑选项（裁剪、马赛克、文字、涂鸦。）  
**setCompressValue：** 是否开启图片压缩（当value > 0则认为开启压缩，返回的则为压缩过的图片）
**setGetPhotoPickerCallBack：** 设置选择图片和拍照后的回调，返回从相册选择的或拍的图片。  

```
public interface OnGetPhotoPickerCallBack {
        void onGetPhotoPickerSuccess(List<PhotoInfo> photoList);

        void onGetPhotoPickerFail();
    }
```


**3. 执行图片选取功能**

```
                .startSelectPhoto(MainActivity.this);
```  
#### 拍照功能  
**1. 获取PhotoPicker实例**  

```
            PhotoPicker.getPhotoPicker()
```  
**2. 设置回调**  

```
            PhotoPicker.getPhotoPicker()
                        .setPhotoPickerCallBack(new PhotoPicker.OnGetPhotoPickerCallBack() {
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
```  
**3. 执行拍照**  

```
            startTakePhoto(MainActivity.this);
```  
### 图片缩放预览  
![image](https://github.com/cedear/Cedear.github.io/blob/master/%E5%9B%BE%E7%89%87%E6%96%87%E4%BB%B6%E5%A4%B9/%E4%B8%8B%E8%BD%BD%20(2).gif?raw=true)  
#### 使用及参数说明   

```
            /**
         * 
         * @param context           上下文
         * @param photoList         需要展示的图片的集合
         * @param position          点击的图片所在集合中的index值
         * @param clickImage        点击的图片view（要求ImageView）
         * @param isFullScreen      是否全屏展示
         */
     
        GalleryViewActivity.launch(context, datalist, position, imageView, true);
```    
同时提供图片保存功能。
