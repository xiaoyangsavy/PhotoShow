package com.savy.imageshow.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.savy.imageshow.model.FileInfo;

import java.util.ArrayList;
import java.util.List;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class FileUtil {


    //获取文件目录
    public static final SmbFile[] getFileList(String myFileUrl,NtlmPasswordAuthentication myAuthentication){
        SmbFile[] files = null;
        SmbFile mRootFolder;
        try {
            mRootFolder = new SmbFile(myFileUrl, myAuthentication);//共享文件列表
            files = mRootFolder.listFiles();

            //DEBUG
//            for (SmbFile smbfile : files) {//遍历文件列表内容
//                String fileUrl = smbfile.getCanonicalPath();
//                String contentType = smbfile.getContentType();
//                int type = smbfile.getType();
//                String fileName = smbfile.getName();
//                Log.e("文件----", "文件地址:" + fileUrl);
//                Log.e("文件----", "文件名称:" + fileName);
//                Log.e("文件----", "文件类型:" + type);
//                Log.e("文件----", "内容类型:" + contentType);
//                if("GoogleCloudDisk/".equals(fileName)){
//                    Log.e("文件----","发现文件！");
//                    SmbFile itemSmbfile=new SmbFile(fileUrl, myAuthentication);
//                    SmbFile[] itemFiles = itemSmbfile.listFiles();
//                    for (SmbFile itemFile : itemFiles) {
//                        String itemfileUrl = itemFile.getCanonicalPath();
//                        String itemcontentType =  itemFile.getContentType();
//                        int itemtype = itemFile.getType();
//                        String itemfileName =  itemFile.getName();
//                        Log.e("文件----","item文件地址:"+itemfileUrl);
//                        Log.e("文件----","item文件名称:"+itemfileName);
//                        Log.e("文件----","item文件类型:"+itemtype);
//                        Log.e("文件----","item内容类型:"+itemcontentType);
//                    }
//                }
//            }


        }catch (Exception e){
            e.printStackTrace();
        }
        return files;
    }


    //文件的原始接口数据转换为列表数据
    public static List<FileInfo> toFileList(SmbFile[] myFiles){
        List<FileInfo> myList = new ArrayList<FileInfo>();
        if(myFiles!=null&&myFiles.length>0) {//无权限时，文件列表返回为null
            for (SmbFile smbfile : myFiles) {//遍历文件列表内容
                try {
                    FileInfo fileInfo = new FileInfo();

                    String fileUrl = smbfile.getCanonicalPath();
                    String fileName = smbfile.getName();
                    fileInfo.setFile(smbfile);
                    fileInfo.setFileUrl(fileUrl);//文件访问地址

                    //识别文件类型
                    if (smbfile.isDirectory()) {//文件类型为文件夹
//                    Log.e("savvy","为文件夹类型");
                        fileInfo.setName(fileName.substring(0, fileName.length() - 1));//去除最后的/
                        fileInfo.setType(FileInfo.DIRECTORY);
                    } else {//文件类型为文件
//                    Log.e("savvy","为文件类型");
                        String[] fileNames = fileName.split("\\.");
                        if (fileNames.length > 1) {//文件名包含类型
                            fileInfo.setName(fileNames[0]);
                            String suffix = fileNames[fileNames.length - 1];
//                        Log.e("savvy","type:"+suffix);
                            if ("jpg".equals(suffix) || "png".equals(suffix)) {
                                fileInfo.setType(FileInfo.PHOTO);
                            }
//                            if ("mp3".equals(suffix)) {
//                                fileInfo.setType(FileInfo.AUDIO);
//                            }
//                            if ("mp4".equals(suffix)) {
//                                fileInfo.setType(FileInfo.VIDEO);
//                            }
                        } else {//文件名不包含类型
                            fileInfo.setName(fileName);
                            fileInfo.setType(FileInfo.UNKNOWN);
                        }
                    }
                    if (fileInfo.getType() == null || fileInfo.getType() == 0) {//都不符合，则为未知类型的文件
                        fileInfo.setType(FileInfo.UNKNOWN);
                    }
                    myList.add(fileInfo);
                } catch (SmbException e) {
                    e.printStackTrace();
                }

            }
        }
        return myList;
    }

    //decodes image and scales it to reduce memory consumption
    public static Bitmap smbFileToBitmap(SmbFile file){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            SmbFileInputStream smbFileInputStream = new SmbFileInputStream(file);
            BitmapFactory.decodeStream(smbFileInputStream,null,o);

            //The new size we want to scale to
            final int REQUIRED_SIZE=1000;

            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;

            System.gc();
            Bitmap  bitmap = BitmapFactory.decodeStream(new SmbFileInputStream(file), null, o2);
            smbFileInputStream.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
