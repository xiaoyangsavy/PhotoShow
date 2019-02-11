package com.savy.imageshow;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.savy.imageshow.util.StaticProperty;

//配置页面
public class SettingActivity extends Activity {

    SharedPreferences share = null;     //本地缓存
    SharedPreferences.Editor sedit = null;

    private RelativeLayout activityBack=null;//导航栏
    private TextView titleInfo=null;
    private RelativeLayout activitySetting=null;

    private EditText ipEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 本地缓存信息
        share = this.getSharedPreferences(
                StaticProperty.SAVE_INFO, Activity.MODE_PRIVATE);
        sedit = share.edit();
//        sedit.putInt(StaticProperty.SCREEN_HEIGHT, height);

        //设置导航栏
        this.activityBack = (RelativeLayout) super
                .findViewById(R.id.activityBack);
        this.activityBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    SettingActivity.this.finish();
            }
        });
        this.titleInfo = (TextView) super.findViewById(R.id.titleInfo);
        this.titleInfo.setText("配置");

        this.ipEditText = (EditText)super.findViewById(R.id.ipEditText);
        this.usernameEditText = (EditText)super.findViewById(R.id.usernameEditText);
        this.passwordEditText = (EditText)super.findViewById(R.id.passwordEditText);
        sedit.putString(StaticProperty.IP, this.ipEditText.getText().toString());

        //本地保存连接信息
        sedit.putString(StaticProperty.USERNAME, this.usernameEditText.getText().toString());
        sedit.putString(StaticProperty.PASSWORD, this.passwordEditText.getText().toString());
        sedit.commit();
    }
}
