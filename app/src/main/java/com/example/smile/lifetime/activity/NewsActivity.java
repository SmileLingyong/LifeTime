package com.example.smile.lifetime.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.smile.lifetime.R;
import com.example.smile.lifetime.backup.BackupTask;
import com.example.smile.lifetime.util.HttpUtil;
import com.r0adkll.slidr.Slidr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import rebus.bottomdialog.BottomDialog;

/**
 * Created by lly54 on 2017/3/27.
 */

public class NewsActivity extends AppCompatActivity {

    private static final int ANIMATION_DURATION = 4000;
    private static final float SCALE_END = 1.13F;
    private String bingPic;     //用于保存每天图片的地址
    private ImageView bingPicImg;
    private TextView daytv;
    private TextView weektv;
    private TextView monthtv;
    private BottomDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //隐藏状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Slidr.attach(this);     //手势滑动切换Activity库

        initView();  //初始化整个页面
        saveImage(); //点击图片实现保存图片
    }

    private void initView() {
        //绑定布局资源
        bingPicImg = (ImageView) findViewById(R.id.everyday_news);
        daytv = (TextView) findViewById(R.id.everyday_news_day);
        weektv = (TextView) findViewById(R.id.everyday_news_week);
        monthtv = (TextView) findViewById(R.id.everyday_news_month);

        //初始化 当前日期
        //注意:这里要使用的是import java.util.Calendar; 而不是 import android.icu.util.Calendar;
        //注意：这里获取的月份要 + 1 才是正确的
        Calendar c = Calendar.getInstance();
        daytv.setText(setDay(c.get(Calendar.DAY_OF_MONTH)));
        weektv.setText(setWeek(c.get(Calendar.DAY_OF_WEEK)));     //注意：这里返回的数据1-7 表示 日-六
        monthtv.setText(setMonth(c.get(Calendar.MONTH) + 1));     //注意：这里获取的月份要 + 1 才是正确的

        //使用缓存来存储访问的每日一图资源
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

    private void saveImage() {
        bingPicImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dialog = new BottomDialog(NewsActivity.this);
//                dialog.title(R.string.news_save);
                dialog.canceledOnTouchOutside(true);
                dialog.cancelable(true);
                dialog.inflateMenu(R.menu.menu_news_save);
                dialog.setOnItemSelectedListener(new BottomDialog.OnItemSelectedListener() {
                    @Override
                    public boolean onItemSelected(int id) {
                        switch (id) {
                            case R.id.note_news_action_save:
                                new BackupTask(NewsActivity.this, bingPicImg, bingPic).execute("savePic");
                                Toast.makeText(NewsActivity.this, "图片保存于 Life_Time_Down_Pic 目录下", Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                dialog.show();
            }
        });

    }

    private void loadBingPic() {    //使用OKHttp访问图片源
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                bingPic = response.body().string();
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
            public void onAnimationEnd(Animator animation) {    //设置动画结束事件

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
