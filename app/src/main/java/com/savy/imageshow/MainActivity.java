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
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.savy.imageshow.adapter.FileListViewAdapter;
import com.savy.imageshow.custom.ActivityLocal;
import com.savy.imageshow.model.FileInfo;
import com.savy.imageshow.util.FileUtil;
import com.savy.imageshow.util.StaticProperty;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String fileName = null;
    private List<FileInfo> fileList = new ArrayList<FileInfo>();
    private  Intent intent = null;
    private RelativeLayout activityBack=null;//导航栏
    private TextView titleInfo=null;
    private RelativeLayout activitySetting=null;

    private long exitTime = 0;// 返回键响应时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.intent=getIntent();
        if(this.intent!=null)
        {
            this.fileUrl=this.intent.getStringExtra("fileUrl");
            Log.e("savy","新页面接收到数据："+ this.fileUrl);
            this.fileName = this.intent.getStringExtra("fileName");
        }
        // 本地缓存信息
        share = this.getSharedPreferences(
                StaticProperty.SAVE_INFO, Activity.MODE_PRIVATE);
        sedit = share.edit();
        // 保存手机信息
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        sedit.putInt(StaticProperty.SCREEN_WIDTH, width);//手机宽度
        sedit.putInt(StaticProperty.SCREEN_HEIGHT, height);//手机高度
        sedit.commit();
        //访问连接配置
        System.setProperty("jcifs.smb.client.dfs.disabled", "true");//默认false
        System.setProperty("jcifs.smb.client.soTimeout", "1000000");//超时
        System.setProperty("jcifs.smb.client.responseTimeout", "30000");//超时

//        this.myImageView = (ImageView) super
//                .findViewById(R.id.myImageView); // 取得弹出界面中的组件
            this.listView = (ListView) super.findViewById(R.id.list_view);

        this.progressDialog = new ProgressDialog(this);
        // 设置进度条风格，风格为圆形，旋转的
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置ProgressDialog 提示信息
        this.progressDialog.setMessage("请稍候......");
        // 设置ProgressDialog 是否可以按退回按键取消
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();

        //设置导航栏
        this.activityBack = (RelativeLayout) super
                .findViewById(R.id.activityBack);
        this.activityBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.this.intent!=null) {
                    MainActivity.this.finish();
                }
            }
        });

        this.activitySetting = (RelativeLayout) super
                .findViewById(R.id.activitySetting);
        this.activitySetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到配置页面
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,
                        SettingActivity.class);
                intent.putExtra("fromClass","mainActivity");
                Log.e("savvy","即将跳转页面SettingActivity");
                startActivity(intent);
            }
        });

        this.titleInfo = (TextView) super.findViewById(R.id.titleInfo);
        if(fileName==null||"".equals(fileName)){
            this.activityBack.setVisibility(View.GONE);//隐藏返回按钮
            this.activitySetting.setVisibility(View.VISIBLE);//显示配置按钮
            this.fileName = "文件列表";
        }
        this.titleInfo.setText(this.fileName);

        new Thread(new Runnable() {
            @Override
            public void run() {
                UniAddress mDomain = null;
                try {
                    //登录授权
                    String myIp = share.getString(StaticProperty.IP, "");
                    String myUsername = share.getString(StaticProperty.USERNAME, "");
                    String myPassword = share.getString(StaticProperty.PASSWORD, "");
                    Log.i("savvy","ip为："+myIp);
                    Log.i("savvy","myUsername为："+myUsername);
                    Log.i("savvy","myPassword为："+myPassword);
                    mDomain = UniAddress.getByName(myIp);
                    NtlmPasswordAuthentication mAuthentication = new NtlmPasswordAuthentication(myIp, myUsername, myPassword);
                    SmbSession.logon(mDomain, mAuthentication);//访问共享服务器
                    //登录授权结束
                    if(MainActivity.this.fileUrl==null||"".equals(MainActivity.this.fileUrl)){
                        MainActivity.this.fileUrl = "smb://" + myIp + "/";//文件夹根目录
                    }
                    Log.e("savy","获取共享文件目录："+ MainActivity.this.fileUrl);
//                    String rootPath = "smb://" + myIp + "/";//文件夹根目录
                    SmbFile[] files = FileUtil.getFileList(MainActivity.this.fileUrl, mAuthentication);
                    MainActivity.this.fileList = FileUtil.toFileList(files);
                    Message locationMsg = MainActivity.this.myHandler
                            .obtainMessage(); // 创建消息
                    locationMsg.what = 1;
                    locationMsg.obj =  MainActivity.this.fileList;
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
                        Map<String,Object> myMap = (Map<String,Object>)msg.obj;
//                        List<FileInfo> allValues = (List<FileInfo>) msg.obj;
                        List<FileInfo> allValues = (List<FileInfo>)myMap.get("data");
                        int position = (int)myMap.get("position");
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this,
                                PhotoShowActivity.class);
                        ActivityLocal al = new ActivityLocal(MainActivity.this);
                        al.set(allValues );
                        Bundle bundle = new Bundle();
                        bundle.putString("activitylocal",String.valueOf(al.hashCode()));
                        bundle.putInt("position",position);
                        intent.putExtras(bundle);
//                                                intent.putExtra("AttitudeDesign",
//                                                        (Serializable) attitudeDesign4);
                        Log.e("savvy","即将跳转页面PhotoShowActivity");
                        startActivity(intent);
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
                                                final int positon = arg2;
                                                FileInfo fileInfo = (FileInfo) arg0.getAdapter().getItem(positon);
                                                Integer type = fileInfo.getType();
                                                Log.e("savvy","文件类型为："+type);
                                               if(type==FileInfo.DIRECTORY){
                                                   Intent intent = new Intent();
                                                   intent.setClass(MainActivity.this,
                                                           MainActivity.class);
                                                   intent.putExtra("fileUrl",
                                                           fileInfo.getFileUrl());
                                                   intent.putExtra("fileName",
                                                           fileInfo.getName());
                                                   Log.e("savvy","跳转页面："+fileInfo.getFileUrl());
                                                   startActivityForResult(intent, 1);
                                               }else if (type==FileInfo.PHOTO){
                                                  final  SmbFile file = fileInfo.getFile();
                                                   new Thread(new Runnable() {
                                                       @Override
                                                       public void run() {
//                                                           Bitmap imageBitmap = FileUtil.smbFileToBitmap(file);
                                                           Message locationMsg = MainActivity.this.myHandler
                                                                   .obtainMessage(); // 创建消息
                                                           locationMsg.what = 2;
                                                           Map<String,Object> myMap = new HashMap<String,Object>();
                                                           myMap.put("data",MainActivity.this.fileList);
                                                           myMap.put("position",positon);
                                                           locationMsg.obj = myMap;
                                                           MainActivity.this.myHandler.sendMessage(locationMsg);
                                                       }
                                                   }).start();
//                                                   Intent intent = new Intent();
//                                                   intent.setClass(MainActivity.this,
//                                                           PhotoShowActivity.class);
//                                                intent.putExtra("fileUrl",
//                                                        fileUrl);
//                                                   Log.e("savvy","即将跳转页面PhotoShowActivity");
//                                                   startActivityForResult(intent, 1);
                                               }


                                            }
                                        });
    }


    //按键时间监听
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (exitTime == 0
                    || (System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次，退出APP", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        } else {// 防止菜单键不能显示
            return false;
        }
    }

}
