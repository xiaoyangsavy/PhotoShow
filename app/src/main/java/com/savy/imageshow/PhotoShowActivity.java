package com.savy.imageshow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class PhotoShowActivity extends Activity {

    private Bitmap myBitmap;
    private ImageView myImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_show);

        Intent intent=getIntent();
        this.myImageView=super.findViewById(R.id.photo_show_image_view);

        if(intent!=null)
        {
            Bitmap bitmap=intent.getParcelableExtra("bitmap");
            this.myImageView.setImageBitmap(bitmap);
            Log.e("savy","新页面接收到数据："+ bitmap.getByteCount());
        }
    }

}
