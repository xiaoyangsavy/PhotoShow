package com.savy.imageshow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.savy.imageshow.adapter.FileListViewAdapter;
import com.savy.imageshow.adapter.PhotoPagerAdapter;
import com.savy.imageshow.custom.ActivityLocal;
import com.savy.imageshow.custom.photoview.PhotoView;
import com.savy.imageshow.model.FileInfo;
import com.savy.imageshow.util.FileUtil;
import com.savy.imageshow.util.StaticProperty;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbSession;


public class PhotoShowActivity extends Activity {
    SharedPreferences share = null;     //本地缓存
    private Bitmap myBitmap;
//    private ImageView myImageView;
private List<FileInfo> allValues = new ArrayList<FileInfo>();
private  String fileUrl;//共享目录地址
    private Handler myHandler;
    private ProgressDialog progressDialog;  //等待视图
    private ViewPager viewPager;
    private int position = 0;//跳转的图片位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_show);


        Intent intent=getIntent();
//        this.myImageView=super.findViewById(R.id.photo_show_image_view);
        // 本地缓存信息
        share = PhotoShowActivity.this.getSharedPreferences(
                StaticProperty.SAVE_INFO, Activity.MODE_PRIVATE);
       this.viewPager = findViewById(R.id.view_pager);


        if(intent!=null)
        {

           Bundle myBundle = intent.getExtras();
           this.position =  (int)myBundle.get("position");//图片位置
           Log.e("savvy","位置："+String.valueOf(position));
//            this.myImageView.setImageBitmap(bitmap);
            ActivityLocal local = new ActivityLocal(this);
            this.allValues = (List<FileInfo>)local.get();
            Log.e("savy","新页面接收到数据："+ fileUrl);
        }

        this.progressDialog = new ProgressDialog(this);
        // 设置进度条风格，风格为圆形，旋转的
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置ProgressDialog 提示信息
        this.progressDialog.setMessage("请稍候......");
        // 设置ProgressDialog 是否可以按退回按键取消
        this.progressDialog.setCancelable(false);
//        this.progressDialog.show();

        PhotoPagerAdapter samplePagerAdapter = new PhotoPagerAdapter(this.allValues);
        this.viewPager.setAdapter(samplePagerAdapter);
        this.viewPager.setCurrentItem(position);//跳转到指定图片

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                UniAddress mDomain = null;
//                try {
//                    //登录授权
//                    String myIp = share.getString(StaticProperty.IP, null);
//                    String myUsername = share.getString(StaticProperty.USERNAME, null);
//                    String myPassword = share.getString(StaticProperty.PASSWORD, null);
//                    mDomain = UniAddress.getByName(myIp);
//                    NtlmPasswordAuthentication mAuthentication = new NtlmPasswordAuthentication(myIp, myUsername, myPassword);
//                    SmbSession.logon(mDomain, mAuthentication);//访问共享服务器
//                    //登录授权结束
//                    if(PhotoShowActivity.this.fileUrl==null||"".equals(PhotoShowActivity.this.fileUrl)){
//                        PhotoShowActivity.this.fileUrl = "smb://" + myIp + "/";//文件夹根目录
//                    }
//                    Log.e("savvy","获取共享文件目录："+ PhotoShowActivity.this.fileUrl);
////                    String rootPath = "smb://" + myIp + "/";//文件夹根目录
//                    SmbFile[] files = FileUtil.getFileList(PhotoShowActivity.this.fileUrl, mAuthentication);
//                    PhotoShowActivity.this.allValues = FileUtil.toFileList(files);
//                    Message locationMsg = PhotoShowActivity.this.myHandler
//                            .obtainMessage(); // 创建消息
//                    locationMsg.what = 1;
//                    locationMsg.obj =  PhotoShowActivity.this.allValues;
//                    PhotoShowActivity.this.myHandler.sendMessage(locationMsg);
//                } catch (UnknownHostException e) {
//                    e.printStackTrace();
//                } catch (SmbException e) {
//                    e.printStackTrace();
//                }
//                PhotoShowActivity.this.progressDialog.dismiss();
//            }
//        }).start();




        // 将跳转之后的页面需要显示的信息传入
        this.myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        List<FileInfo> myList = (List<FileInfo>) msg.obj;
                        PhotoPagerAdapter samplePagerAdapter = new PhotoPagerAdapter(myList);
                        PhotoShowActivity.this.viewPager.setAdapter(samplePagerAdapter);
                        break;
                }
            }
        };
    }

}
