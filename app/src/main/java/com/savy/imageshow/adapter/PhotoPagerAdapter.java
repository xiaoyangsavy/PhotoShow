package com.savy.imageshow.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.savy.imageshow.MainActivity;
import com.savy.imageshow.PhotoShowActivity;
import com.savy.imageshow.R;
import com.savy.imageshow.custom.photoview.PhotoView;
import com.savy.imageshow.model.FileInfo;
import com.savy.imageshow.util.FileUtil;

import java.util.List;

public class PhotoPagerAdapter extends PagerAdapter {

    private List<FileInfo> allValues;

    public PhotoPagerAdapter(List<FileInfo> allValues){
    this.allValues = allValues;
    }

    @Override
    public int getCount() {
        return allValues.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        final PhotoView photoView = new PhotoView(container.getContext());

        final FileInfo fileInfo = allValues.get(position);
        if(fileInfo.getType()==FileInfo.PHOTO) {

            // 将跳转之后的页面需要显示的信息传入
            final Handler myHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case 1:
                            Bitmap imageBitmap = (Bitmap) msg.obj;
                            photoView.setImageBitmap(imageBitmap);
                            break;

                    }
                }
            };


            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e("savy_image","当前图片显示的内容为："+ fileInfo.getName()+";"+fileInfo.getFileUrl());
                    Bitmap imageBitmap = FileUtil.smbFileToBitmap(fileInfo.getFile());
                    Log.e("savy_image","大小为："+ (imageBitmap!=null?imageBitmap.getByteCount():"content is null!"));
                    Message locationMsg = myHandler.obtainMessage(); // 创建消息
                                                           locationMsg.what = 1;
                                                           locationMsg.obj = imageBitmap;
                                                          myHandler.sendMessage(locationMsg);
                }
            }).start();


        }else{
            photoView.setImageResource(R.drawable.photo_default);
        }
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}