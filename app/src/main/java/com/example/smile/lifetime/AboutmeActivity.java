package com.example.smile.lifetime;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by lly54 on 2017/3/28.
 */

public class AboutmeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_aboutme);

        ImageView userHead = (ImageView) findViewById(R.id.user_head);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        TextView tvThanks = (TextView) findViewById(R.id.tv_thanks);
        TextView tvBlog = (TextView) findViewById(R.id.tv_blog);

        collapsingToolbar.setTitle("生活点滴");
        AboutmeActivity.this.setSupportActionBar(toolbar);

    }
}
