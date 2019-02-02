package com.savy.imageshow.model;

//文件展示信息
public class FileInfo {
    private String name;        //文件名称
    private Integer type;           //文件类型
    private String fileUrl;     //文件地址

    public static int DIRECTORY = 1;//目录
    public static int PHOTO = 2;//图片
    public static int AUDIO = 3;//音频
    public static int VIDEO = 4;//视频
    public static int UNKNOWN = -1;//未知


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
