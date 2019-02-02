package com.savy.imageshow;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.UnknownHostException;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbSession;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.setProperty("jcifs.smb.client.dfs.disabled", "true");//默认false
        System.setProperty("jcifs.smb.client.soTimeout", "1000000");//超时
        System.setProperty("jcifs.smb.client.responseTimeout", "30000");//超时

        new Thread(new Runnable() {
            @Override
            public void run() {
                String ip = "192.168.3.34";//pc地址
                String username = "user";//账户密码
                String password = "546213";
                UniAddress mDomain = null;
                try {
                    //登录授权
                    mDomain = UniAddress.getByName(ip);
                    NtlmPasswordAuthentication mAuthentication = new NtlmPasswordAuthentication(ip, username, password);
                    SmbSession.logon(mDomain, mAuthentication);//访问pc
                    //登录授权结束
                    String rootPath = "smb://" + ip + "/";
                    SmbFile mRootFolder;
                    try {
                        mRootFolder = new SmbFile(rootPath, mAuthentication);//共享文件列表
                        try {
                            SmbFile[] files;
                            files = mRootFolder.listFiles();
                            for (SmbFile smbfile : files) {//遍历文件列表内容
                                String fileUrl = smbfile.getCanonicalPath();
                                String contentType =  smbfile.getContentType();
                                int type = smbfile.getType();
                                String fileName =  smbfile.getName();
                                Log.e("文件----","文件地址:"+fileUrl);
                                Log.e("文件----","文件名称:"+fileName);
                                Log.e("文件----","文件类型:"+type);
                                Log.e("文件----","内容类型:"+contentType);
                                if("GoogleCloudDisk/".equals(fileName)){
                                    Log.e("文件----","发现文件！");
                                    SmbFile itemSmbfile=new SmbFile(fileUrl, mAuthentication);
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
                                    }

                                }
                            }
                        } catch (SmbException e) {
                            // ...
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (SmbException e) {
                    e.printStackTrace();
                }

    }
}).start();

    }}
