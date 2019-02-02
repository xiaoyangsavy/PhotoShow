package com.savy.imageshow.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.savy.imageshow.model.FileInfo;

import java.util.List;

public class FileListViewAdapter extends BaseAdapter {
    private List<FileInfo> allValues;

    @Override
    public int getCount() {
        return allValues.size();
    }

    @Override
    public Object getItem(int position) {
        return allValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
