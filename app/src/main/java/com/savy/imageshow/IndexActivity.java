package com.savy.imageshow;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.savy.imageshow.util.StaticProperty;

import java.util.ArrayList;

//引导页面
public class IndexActivity extends Activity {
    private static final int START_ACTIVITY = 0;        //跳转首页
    private static final int START_LOADVIEWPAGER = 1;   //显示引导页

    SharedPreferences share = null;
    SharedPreferences.Editor sedit = null;

    private ViewPager mViewPager;
    LinearLayout mLinearLayout;
    private int[] indexImages = { R.drawable.index0,
            R.drawable.index1, R.drawable.index2, R.drawable.index3 };
    private String[] indexCaptions = {"第一步，开启windows的网络共享","第二步，设置需要共享的文件夹",
            "第三步，给共享的文件夹添加可操作的用户及其操作权限","第四步，查看电脑的ip地址，用于本APP的配置"};
    private ArrayList<View> views = new ArrayList<View>();
    private View indexView = null;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_LOADVIEWPAGER:// 加载viewpager
                    initViewPager();
                    break;
                case START_ACTIVITY:
                    Intent intent = new Intent();
                    intent.setClass(IndexActivity.this, SettingActivity.class);
                    startActivity(intent);
//                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    finish();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        // 保存本地信息
        share = IndexActivity.this.getSharedPreferences(
                StaticProperty.SAVE_INFO, Activity.MODE_PRIVATE);
        sedit = share.edit();

// 验证是否为第一次登录
        boolean flag = share.getBoolean(StaticProperty.IS_FIRST_LOGIN, true);
        flag = true;    //测试代码，每次都进入引导页
        if (flag) {
            sedit = share.edit();
            sedit.putBoolean(StaticProperty.IS_FIRST_LOGIN, false);
            sedit.commit();
            handler.sendEmptyMessage(START_LOADVIEWPAGER);
        } else {
            handler.sendEmptyMessage(START_ACTIVITY);
//            handler.sendEmptyMessageDelayed(START_ACTIVITY, 1500);
        }
        sedit.commit();

    }


    private void initViewPager() {
        mViewPager = (ViewPager) this.findViewById(R.id.vPager);

        mViewPager.setVisibility(View.VISIBLE);
        for (int i = 0; i < this.indexImages.length; i++) {

            LayoutInflater mLi = LayoutInflater.from(this);
            indexView = mLi.inflate(R.layout.layout_index, null);
            ImageView indexImageView = indexView.findViewById(R.id.indexImageview);
            indexImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),this.indexImages[i]));
            TextView indexCaptionTextView = indexView.findViewById(R.id.indexCaptionTextView);
            indexCaptionTextView.setText(this.indexCaptions[i]);
            Button indexCommitButton = indexView.findViewById(R.id.indexCommitButton);
            indexCommitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(IndexActivity.this, SettingActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
             if (i == this.indexImages.length - 1) {
//                 indexImageView.setOnClickListener(new OnViewPagerClickImpl());
                 indexCaptionTextView.setVisibility(View.GONE);
                 indexCommitButton.setVisibility(View.VISIBLE);
             }



            views.add(indexView);
        }
        mViewPager.setAdapter(new MyPagerAdapter());

    }

//    class OnViewPagerClickImpl implements View.OnClickListener {
//
//        @Override
//        public void onClick(View v) {
//            Intent intent = new Intent();
//            intent.setClass(IndexActivity.this, SettingActivity.class);
//            startActivity(intent);
//            finish();
//        }
//
//    }

    class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return views.size();
        }

        // 初始化position位置的界面
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);

        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

    }
}
