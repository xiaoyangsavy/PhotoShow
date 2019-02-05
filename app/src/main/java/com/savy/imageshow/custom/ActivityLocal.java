package com.savy.imageshow.custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.savy.imageshow.Iapplication;

/**
 * 用于Application中进行值的存取，使同线程下Intent不再需要进行对象的序列化
 */
public class ActivityLocal {

  private Context context = null;


 public ActivityLocal(Context context){
 this.context = context;
 }

  public <T> T get(){
 Iapplication application = (Iapplication) ((Activity)this.context).getApplication();
 Intent intent = (Intent)((Activity)this.context).getIntent();
  String key = intent.getExtras().getString("activitylocal");
  ActivityLocal local = application.get(key);
 return (T) application.get(local);
  }

  public <T> void set(T value){
 Iapplication application = (Iapplication) ((Activity)this.context).getApplication();
 application.set(this, value);
 application.set(String.valueOf(this.hashCode()), this);
 }

}
