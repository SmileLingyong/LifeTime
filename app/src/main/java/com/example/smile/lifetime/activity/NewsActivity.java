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

import com.bumptech.glide.Glide;
import com.example.smile.lifetime.R;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import com.example.smile.lifetime.util.HttpUtil;

/**
 * Created by lly54 on 2017/3/27.
 */

public class NewsActivity extends AppCompatActivity {

    private static final int ANIMATION_DURATION = 4000;
    private static final float SCALE_END = 1.13F;
    private ImageView bingPicImg;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        //隐藏状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        //看缓存中是否有图片
        bingPicImg = (ImageView) findViewById(R.id.everyday_news);

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
}
