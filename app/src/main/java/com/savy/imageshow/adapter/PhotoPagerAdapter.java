package com.savy.imageshow.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
       final Context context = container.getContext();
        View convertView = LayoutInflater.from(container.getContext()).inflate(
                R.layout.adapter_image_show, null);
        final PhotoView photoView =  convertView.findViewById(R.id.photo_show_imageview);
        Button deleteButton = convertView.findViewById(R.id.photo_delete_button);
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
                        case 2:
                            String errorInfo = (String) msg.obj;
                            if(errorInfo==null||"".equals(errorInfo)){
                                Toast.makeText(context,"删除成功！",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context,errorInfo,Toast.LENGTH_SHORT).show();
                            }
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



            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context).setTitle("提示")//设置对话框标题

                    .setMessage("是否删除此图片")//设置显示的内容

                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                            Log.e("savvy","确认删除此文件！");


                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String errorInfo = null;
                                            try {
                                                if (!fileInfo.getFile().exists()){
                                                    errorInfo = "删除失败，文件不存在！";
                                                }else if(!fileInfo.getFile().isFile()){
                                                    errorInfo = "删除失败，不是有效的文件！";
                                                }else if(!fileInfo.getFile().canWrite()){
                                                    errorInfo = "删除失败，无操作权限！";
                                                }else{
                                                    fileInfo.getFile().delete();
                                                }
                                            }catch (Exception e){
                                                errorInfo = "删除失败，错误类型为："+e.toString();
                                                e.printStackTrace();
                                            }
                                            Message locationMsg = myHandler.obtainMessage(); // 创建消息
                                            locationMsg.what = 2;
                                            locationMsg.obj = errorInfo;
                                            myHandler.sendMessage(locationMsg);
                                        }
                                    }).start();



                        }
                    }).setNegativeButton("返回", new DialogInterface.OnClickListener() {//添加返回按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//响应事件
                        }
                    });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.show();
                }
            });

        }else{
            photoView.setImageResource(R.drawable.photo_default);
        }


            // Now just add PhotoView to ViewPager and return it
            container.addView(convertView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return convertView;
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