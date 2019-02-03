package com.savy.imageshow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.savy.imageshow.adapter.FileListViewAdapter;
import com.savy.imageshow.model.FileInfo;
import com.savy.imageshow.util.StaticProperty;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbSession;


public class MainActivity extends Activity {

    SharedPreferences share = null;     //本地缓存
    SharedPreferences.Editor sedit = null;
//    private ImageView myImageView;
    private ProgressDialog progressDialog;  //等待视图
    private Handler myHandler;
    private ListView listView;  //文件浏览列表
    private FileListViewAdapter fileListViewAdapter;
    private  String fileUrl=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent=getIntent();
        if(intent!=null)
        {
            this.fileUrl=intent.getStringExtra("fileUrl");
            Log.e("savy","新页面接收到数据："+ this.fileUrl);
        }
        // 保存本地信息
        share = MainActivity.this.getSharedPreferences(
                StaticProperty.SAVE_INFO, Activity.MODE_PRIVATE);
        sedit = share.edit();
        // 保存手机信息
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        sedit.putInt(StaticProperty.SCREEN_WIDTH, width);//手机宽度
        sedit.putInt(StaticProperty.SCREEN_HEIGHT, height);//手机高度
        //保存访问信息
        String ip = "192.168.3.34";//pc地址
        String username = "user";//账户密码
        String password = "546213";
        sedit.putString(StaticProperty.IP, ip);//访问地址
        sedit.putString(StaticProperty.USERNAME, username);//用户名
        sedit.putString(StaticProperty.PASSWORD, password);//密码
        sedit.commit();
        //访问连接配置
        System.setProperty("jcifs.smb.client.dfs.disabled", "true");//默认false
        System.setProperty("jcifs.smb.client.soTimeout", "1000000");//超时
        System.setProperty("jcifs.smb.client.responseTimeout", "30000");//超时

//        this.myImageView = (ImageView) this
//                .findViewById(R.id.myImageView); // 取得弹出界面中的组件
            this.listView = (ListView) this.findViewById(R.id.list_view);

        progressDialog = new ProgressDialog(MainActivity.this);
        // 设置进度条风格，风格为圆形，旋转的
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置ProgressDialog 提示信息
        progressDialog.setMessage("请稍候......");
        // 设置ProgressDialog 是否可以按退回按键取消
        progressDialog.setCancelable(false);
        MainActivity.this.progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                UniAddress mDomain = null;
                try {
                    //登录授权
                    String myIp = share.getString(StaticProperty.IP, null);
                    String myUsername = share.getString(StaticProperty.USERNAME, null);
                    String myPassword = share.getString(StaticProperty.PASSWORD, null);
                    mDomain = UniAddress.getByName(myIp);
                    NtlmPasswordAuthentication mAuthentication = new NtlmPasswordAuthentication(myIp, myUsername, myPassword);
                    SmbSession.logon(mDomain, mAuthentication);//访问共享服务器
                    //登录授权结束
                    if(MainActivity.this.fileUrl==null||"".equals(MainActivity.this.fileUrl)){
                        MainActivity.this.fileUrl = "smb://" + myIp + "/";//文件夹根目录
                    }
                    Log.e("savy","获取共享文件目录："+ MainActivity.this.fileUrl);
//                    String rootPath = "smb://" + myIp + "/";//文件夹根目录
                    SmbFile[] files = MainActivity.this.getFileList(MainActivity.this.fileUrl, mAuthentication);
                    List<FileInfo> fileList = MainActivity.this.toFileList(files);
                    Message locationMsg = MainActivity.this.myHandler
                            .obtainMessage(); // 创建消息
                    locationMsg.what = 1;
                    locationMsg.obj = fileList;
                    MainActivity.this.myHandler.sendMessage(locationMsg);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (SmbException e) {
                    e.printStackTrace();
                }
                MainActivity.this.progressDialog.dismiss();
            }
        }).start();


        // 将跳转之后的页面需要显示的信息传入
        this.myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        List<FileInfo> myList = (List<FileInfo>) msg.obj;
                        fileListViewAdapter = new FileListViewAdapter(MainActivity.this, myList);
                        MainActivity.this.listView.setAdapter(fileListViewAdapter);
                        break;
                    case 2:
                        Bitmap imageBitmap = (Bitmap) msg.obj;
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this,
                                PhotoShowActivity.class);
                        intent.putExtra("bitmap",
                                imageBitmap);
//                                                intent.putExtra("AttitudeDesign",
//                                                        (Serializable) attitudeDesign4);
                        Log.e("savvy","即将跳转页面PhotoShowActivity");
                        startActivityForResult(intent, 1);
                        break;


                    case -99:
//                        Bitmap bitmap = (Bitmap) msg.obj;
//                        MainActivity.this.myImageView.setImageBitmap(bitmap);
                        break;
                }
            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                                                    long arg3) {
                                            Log.e("savvy","点击列表，即将 跳转页面："+arg2);
                                                View view = arg1;
                                                int positon = arg2;
                                                FileInfo fileInfo = (FileInfo) arg0.getAdapter().getItem(positon);
                                                Integer type = fileInfo.getType();
                                                Log.e("savvy","文件类型为："+type);
                                               if(type==FileInfo.DIRECTORY){
                                                   Intent intent = new Intent();
                                                   intent.setClass(MainActivity.this,
                                                           MainActivity.class);
                                                   intent.putExtra("fileUrl",
                                                           fileInfo.getFileUrl());
                                                   Log.e("savvy","跳转页面："+fileInfo.getFileUrl());
                                                   startActivityForResult(intent, 1);
                                               }else if (type==FileInfo.PHOTO){
                                                  final  SmbFile file = fileInfo.getFile();
                                                   new Thread(new Runnable() {
                                                       @Override
                                                       public void run() {
                                                           Bitmap imageBitmap = MainActivity.this.smbFileToBitmap(file);
                                                           Message locationMsg = MainActivity.this.myHandler
                                                                   .obtainMessage(); // 创建消息
                                                           locationMsg.what = 2;
                                                           locationMsg.obj = imageBitmap;
                                                           MainActivity.this.myHandler.sendMessage(locationMsg);
                                                       }
                                                   }).start();

                                               }


                                            }
                                        });
    }

    //获取文件目录
    public final SmbFile[] getFileList(String myFileUrl,NtlmPasswordAuthentication myAuthentication){
        SmbFile[] files = null;
        SmbFile mRootFolder;
        try {
            mRootFolder = new SmbFile(myFileUrl, myAuthentication);//共享文件列表
            files = mRootFolder.listFiles();

            //DEBUG
            for (SmbFile smbfile : files) {//遍历文件列表内容
                String fileUrl = smbfile.getCanonicalPath();
                String contentType = smbfile.getContentType();
                int type = smbfile.getType();
                String fileName = smbfile.getName();
                Log.e("文件----", "文件地址:" + fileUrl);
                Log.e("文件----", "文件名称:" + fileName);
                Log.e("文件----", "文件类型:" + type);
                Log.e("文件----", "内容类型:" + contentType);
                if("GoogleCloudDisk/".equals(fileName)){
                    Log.e("文件----","发现文件！");
                    SmbFile itemSmbfile=new SmbFile(fileUrl, myAuthentication);
                    SmbFile[] itemFiles = itemSmbfile.listFiles();
                    for (SmbFile itemFile : itemFiles) {
                        String itemfileUrl = itemFile.getCanonicalPath();
                        String itemcontentType =  itemFile.getContentType();
                        int itemtype = itemFile.getType();
                        String itemfileName =  itemFile.getName();
                        Log.e("文件----","item文件地址:"+itemfileUrl);
                        Log.e("文件----","item文件名称:"+itemfileName);
                        Log.e("文件----","item文件类型:"+itemtype);
                        Log.e("文件----","item内容类型:"+itemcontentType);


                        if("3.jpg".equals(itemfileName)){
                            Log.e("文件----","发现文件！");
                            SmbFile itemSmbfile2=new SmbFile(itemfileUrl, myAuthentication);
                            if(itemSmbfile2.isDirectory()){
                                SmbFile[] itemFiles2 = itemSmbfile2.listFiles();
                                for (SmbFile itemFile2 : itemFiles2) {
                                    String itemfileUrl2 = itemFile2.getCanonicalPath();
                                    String itemcontentType2 =  itemFile2.getContentType();
                                    int itemtype2 = itemFile2.getType();
                                    String itemfileName2 =  itemFile2.getName();
                                    Log.e("文件----","item2文件地址:"+itemfileUrl2);
                                    Log.e("文件----","item2文件名称:"+itemfileName2);
                                    Log.e("文件----","item2文件类型:"+itemtype2);
                                    Log.e("文件----","item2内容类型:"+itemcontentType2);
                                }
                            }else{
//                                Bitmap imageBitmap = MainActivity.this.smbFileToBitmap(itemSmbfile2);

//                                Message locationMsg = MainActivity.this.myHandler
//                                        .obtainMessage(); // 创建消息
//                                locationMsg.what = -99;
//                                locationMsg.obj = imageBitmap;
//                                // System.out.println(bannerList.size()+"bannerList**********");
//                                MainActivity.this.myHandler.sendMessage(locationMsg);
                            }


                        }
                    }
                }
            }


            }catch (Exception e){
            e.printStackTrace();
            }
        return files;
    }


    //文件的原始接口数据转换为列表数据
    public List<FileInfo> toFileList(SmbFile[] myFiles){
        List<FileInfo> myList = new ArrayList<FileInfo>();
        for (SmbFile smbfile : myFiles) {//遍历文件列表内容
            try {
            FileInfo fileInfo = new FileInfo();

            String fileUrl = smbfile.getCanonicalPath();
            String fileName = smbfile.getName();
            fileInfo.setFile(smbfile);
            fileInfo.setFileUrl(fileUrl);//文件访问地址

            //识别文件类型
                if(smbfile.isDirectory()){//文件类型为文件夹
                    Log.e("savvy","为文件夹类型");
                    fileInfo.setName(fileName.substring(0,fileName.length()-1));//去除最后的/
                    fileInfo.setType(FileInfo.DIRECTORY);
                }else{//文件类型为文件
                    Log.e("savvy","为文件类型");
                   String[] fileNames =  fileName.split("\\.");
                    if(fileNames.length>1){//文件名包含类型
                        fileInfo.setName(fileNames[0]);
                       String suffix =  fileNames[fileNames.length-1];
                       Log.e("savvy","type:"+suffix);
                       if("jpg".equals(suffix)){
                           fileInfo.setType(FileInfo.PHOTO);
                       }
                    }else{//文件名不包含类型
                        fileInfo.setName(fileName);
                        fileInfo.setType(FileInfo.UNKNOWN);
                    }
                }
                if(fileInfo.getType()==null||fileInfo.getType()==0){//都不符合，则为未知类型的文件
                    fileInfo.setType(FileInfo.UNKNOWN);
                }
                myList.add(fileInfo);
            } catch (SmbException e) {
                e.printStackTrace();
            }

        }
        return myList;
    }


    //decodes image and scales it to reduce memory consumption
    private Bitmap smbFileToBitmap(SmbFile file){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new SmbFileInputStream(file),null,o);

            //The new size we want to scale to
            final int REQUIRED_SIZE=70;

            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;

            System.gc();

            return BitmapFactory.decodeStream(new SmbFileInputStream(file), null, o2);
        } catch (Exception e) {}
        return null;
    }
}
