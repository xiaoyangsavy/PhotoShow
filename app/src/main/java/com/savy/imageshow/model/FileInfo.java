package com.savy.imageshow.model;

//文件展示信息
public class FileInfo {
    private String name;        //文件名称
    private int type;           //文件类型
    private String fileUrl;     //文件地址

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
