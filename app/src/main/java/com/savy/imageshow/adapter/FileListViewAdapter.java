package com.savy.imageshow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.savy.imageshow.R;
import com.savy.imageshow.model.FileInfo;

import java.util.List;

public class FileListViewAdapter extends BaseAdapter {
    private List<FileInfo> allValues;
    private Context context;

    public FileListViewAdapter(Context context,
                               List<FileInfo> allValues) {
        this.context = context;
        this.allValues = allValues;
    }

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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.adapter_file_list, null);
            viewHolder = new ViewHolder();
            viewHolder.fileImageView = (ImageView) convertView
                    .findViewById(R.id.file_icon);
            viewHolder.fileNameTextView = (TextView) convertView
                    .findViewById(R.id.file_name);
            viewHolder.fileDetailTextView = (TextView) convertView
                    .findViewById(R.id.file_detail);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.fileNameTextView.setText(allValues.get(position).getName());
        int fileType = allValues.get(position).getType();
        String detailString = "";
        if(fileType==FileInfo.DIRECTORY){
            detailString = "文件夹";
            viewHolder.fileImageView.setImageDrawable(context.getResources().getDrawable((R.drawable.ico_directory)));
        }else if(fileType==FileInfo.PHOTO){
            viewHolder.fileImageView.setImageDrawable(context.getResources().getDrawable((R.drawable.photo_default)));
            detailString = "图片";
        }else{
            viewHolder.fileImageView.setImageDrawable(context.getResources().getDrawable((R.drawable.ico_file)));
            detailString = "文件";
        }
        viewHolder.fileDetailTextView.setText(detailString);

        return convertView;
    }

    static class ViewHolder {
        ImageView fileImageView;
        TextView fileNameTextView, fileDetailTextView;
    }

}
