package com.example.smile.lifetime.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.smile.lifetime.R;
import com.example.smile.lifetime.util.HttpUtil;
import com.r0adkll.slidr.Slidr;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by lly54 on 2017/3/27.
 */

public class NewsActivity extends AppCompatActivity {

    private static final int ANIMATION_DURATION = 4000;
    private static final float SCALE_END = 1.13F;
    private ImageView bingPicImg;
    private TextView daytv;
    private TextView weektv;
    private TextView monthtv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //隐藏状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Slidr.attach(this);

        //看缓存中是否有图片
        bingPicImg = (ImageView) findViewById(R.id.everyday_news);
        daytv = (TextView) findViewById(R.id.everyday_news_day);
        weektv = (TextView) findViewById(R.id.everyday_news_week);
        monthtv = (TextView) findViewById(R.id.everyday_news_month);

        //注意:这里要使用的是import java.util.Calendar; 而不是 import android.icu.util.Calendar;
        //注意：这里获取的月份要 + 1 才是正确的
        Calendar c = Calendar.getInstance();

        daytv.setText(setDay(c.get(Calendar.DAY_OF_MONTH)));
        weektv.setText(setWeek(c.get(Calendar.DAY_OF_WEEK)));     //注意：这里返回的数据1-7 表示 日-六
        monthtv.setText(setMonth(c.get(Calendar.MONTH) + 1));     //注意：这里获取的月份要 + 1 才是正确的

        //获取缓存中的图片
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic = prefs.getString("bing_pic", null);


        if (bingPic != null) {
            loadBingPic();
            Glide.with(this).load(bingPic).into(bingPicImg);
            animateImage();
            Log.d("not null URL", bingPic);
        } else {
            loadBingPic();
        }

    }

    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(NewsActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                Log.d("URL", "bing_pic");
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(NewsActivity.this).load(bingPic).into(bingPicImg);
                        animateImage();
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }


    //设置每日一图动画效果
    private void animateImage() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(bingPicImg, "scaleX", 1f, SCALE_END);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(bingPicImg, "scaleY", 1f, SCALE_END);

        AnimatorSet set = new AnimatorSet();
        //设置动画的长度：通过上面定义的animatorX，animatorY来渐变长度
        set.setDuration(ANIMATION_DURATION).play(animatorX).with(animatorY);
        set.start();

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                NewsActivity.this.finish();
            }
        });

    }

    //对应这个月第几天
    public String setDay(int day) {
        if (day >= 1 && day <= 9) {
            return "0" + day;
        } else {
            return String.valueOf(day);
        }
    }


    //对应 周几 英文
    public String setWeek(int week) {
        String weekNow = "";
        switch (week) {
            case 1:
                weekNow = "Sunday";
                break;
            case 2:
                weekNow = "Monday";
                break;
            case 3:
                weekNow = "Tuesday";
                break;
            case 4:
                weekNow = "Wednesday";
                break;
            case 5:
                weekNow = "Thursday";
                break;
            case 6:
                weekNow = "Friday";
                break;
            case 7:
                weekNow = "Saturday";
                break;
        }
        return weekNow;
    }

    //对应 月份 英文
    public String setMonth(int month) {
        String monthNow = "";
        switch (month) {
            case 1:
                monthNow = "January";
                break;
            case 2:
                monthNow = "February";
                break;
            case 3:
                monthNow = "March";
                break;
            case 4:
                monthNow = "April";
                break;
            case 5:
                monthNow = "May";
                break;
            case 6:
                monthNow = "June";
                break;
            case 7:
                monthNow = "July";
                break;
            case 8:
                monthNow = "August";
                break;
            case 9:
                monthNow = "September";
                break;
            case 10:
                monthNow = "October";
                break;
            case 11:
                monthNow = "November";
                break;
            case 12:
                monthNow = "December";
                break;
        }
        return monthNow;
    }


}
