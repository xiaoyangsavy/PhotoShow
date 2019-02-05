package com.savy.imageshow;

import android.app.Application;

import com.savy.imageshow.custom.ActivityLocal;

import java.util.HashMap;


/**自定义的Application
 * 使同线程下Intent不再需要进行对象的序列化
 * 通过HashMap进行值的存取，通过key进行类之间的传递
 */
public class Iapplication extends Application {

    private HashMap<String,ActivityLocal> localMap = new HashMap<String,ActivityLocal>();
    private HashMap<ActivityLocal,Object> activityMap = new HashMap<ActivityLocal,Object>();

    public void onCreate() {
        super.onCreate();
    }

    public ActivityLocal get(String key){
        return this.localMap.remove(key);
    }

    public void set(String key,ActivityLocal activityLocal){
        this.localMap.put(key, activityLocal);
    }

    public Object get(ActivityLocal activityLocal){
        return this.activityMap.remove(activityLocal);
    }

    public void set(ActivityLocal activityLocal,Object value){
        this.activityMap.put(activityLocal, value);
    }
}
