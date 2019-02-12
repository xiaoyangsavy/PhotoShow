package com.savy.imageshow;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.savy.imageshow.util.StaticProperty;

import java.util.Set;

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
    private Button commitButton;         //提交按钮
    private Button clearButton;          //清空按钮

    private String ip = null;           //ip
    private String username = null;     //用户名
    private String password = null;     //密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 本地缓存信息
        share = this.getSharedPreferences(
                StaticProperty.SAVE_INFO, Activity.MODE_PRIVATE);
        sedit = share.edit();
//        sedit.putInt(StaticProperty.SCREEN_HEIGHT, height);

        this.ip = share.getString(StaticProperty.IP, null);
        this.username = share.getString(StaticProperty.USERNAME, null);
        this.password = share.getString(StaticProperty.PASSWORD, null);

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

        this.commitButton = (Button)super.findViewById(R.id.commitButton);
        //提交按钮点击事件
        this.commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("savvy","点击提交按钮！");
               Editable ipEditTextContent = SettingActivity.this.ipEditText.getText();
                Editable usernameEditTextContent = SettingActivity.this.usernameEditText.getText();
                Editable passwordEditTextContent = SettingActivity.this.passwordEditText.getText();
               if(ipEditTextContent == null||"".equals(ipEditTextContent.toString())) {
                   Log.i("savvy","ip为空");
                   Toast.makeText(SettingActivity.this, "ip不能为空!", Toast.LENGTH_SHORT).show();
               }else   if(usernameEditTextContent == null||"".equals(usernameEditTextContent.toString())) {
                   Log.i("savvy","用户名为空");
                           Toast.makeText(SettingActivity.this,"用户名不能为空!", Toast.LENGTH_SHORT).show();
               }else   if(passwordEditTextContent == null||"".equals(passwordEditTextContent.toString())) {
                   Log.i("savvy","登录密码为空");
                   Toast.makeText(SettingActivity.this,"登录密码不能为空!", Toast.LENGTH_SHORT).show();
               }else {
                   Log.i("savvy","保存连接信息");
                   //本地保存连接信息
                   SettingActivity.this.sedit.putString(StaticProperty.IP, ipEditTextContent.toString());
                   SettingActivity.this.sedit.putString(StaticProperty.USERNAME, usernameEditTextContent.toString());
                   SettingActivity.this.sedit.putString(StaticProperty.PASSWORD, passwordEditTextContent.toString());
                   SettingActivity.this.sedit.commit();
                   //跳转到首页
                   Intent goIntent = new Intent();
                   goIntent.setClass(SettingActivity.this, MainActivity.class);
                   startActivity(goIntent);
                   finish();
               }
            }
        });

        this.clearButton = (Button)super.findViewById(R.id.clearButton);
        this.clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingActivity.this.ipEditText.setText(null);
                SettingActivity.this.usernameEditText.setText(null);
                SettingActivity.this.passwordEditText.setText(null);
                SettingActivity.this.sedit.putString(StaticProperty.IP, null);
                SettingActivity.this.sedit.putString(StaticProperty.USERNAME, null);
                SettingActivity.this.sedit.putString(StaticProperty.PASSWORD, null);
                SettingActivity.this.sedit.commit();
            }
        });

        //从首页跳转过来时，显示返回按钮
        boolean isFromMain = true;//是否从首页跳转进来的
        Intent intent=getIntent();
        if(intent!=null) {
            String fromClass = intent.getStringExtra("fromClass");
            if(fromClass!=null&&"mainActivity".equals(fromClass)){
                isFromMain = true;

            }else{
                isFromMain = false;
            }
        }else{
            isFromMain = false;
        }
        if(isFromMain){     //从首页跳转进入
            this.activityBack.setVisibility(View.VISIBLE);
            //登录授权
            String myIp = share.getString(StaticProperty.IP, null);
            String myUsername = share.getString(StaticProperty.USERNAME, null);
            String myPassword = share.getString(StaticProperty.PASSWORD, null);
            if(myIp!=null&&!"".equals(myIp)){
                this.ipEditText.setText(myIp);
            }
            if(myUsername!=null&&!"".equals(myUsername)) {
                this.usernameEditText.setText(myUsername);
            }
            if(myPassword!=null&&!"".equals(myPassword)) {
                this.passwordEditText.setText(myPassword);
            }
        }else{  //直接启动进入
            this.activityBack.setVisibility(View.GONE);
            //如果已保存了连接信息，则直接进入首页
            if(this.ip!=null&&!"".equals(this.ip)&&this.username!=null&&!"".equals(this.username)&&this.password!=null&&!"".equals(this.password)){
                Intent goIntent = new Intent();
                goIntent.setClass(SettingActivity.this, MainActivity.class);
                startActivity(goIntent);
                finish();
            }
        }



    }
}
