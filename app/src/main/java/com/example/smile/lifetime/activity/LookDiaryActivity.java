package com.example.smile.lifetime.activity;

import android.app.AlertDialog;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.smile.lifetime.R;
import com.example.smile.lifetime.db.NotesDB;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lly54 on 2017/4/6.
 */

public class LookDiaryActivity extends AppCompatActivity {

    private int flag = 0;
    private int DEFAULT_INT_ID;
    private String DEFAULT_STRING_ID;       //记录未修改前的 ID
    private String DEFAULT_CONTENT;         //记录未修改前的 CONTENT

    private EditText detail_editText;
    private TextView toolbarTitle, detail_time;
    private Toolbar toolbar;

    private NotesDB notesDB;    //创建一个数据库
    private SQLiteDatabase dbWriter;    //创建一个添加权限

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        notesDB = new NotesDB(this);                //实例化数据库
        dbWriter = notesDB.getWritableDatabase();    //获取到当前可添加的权

        DEFAULT_INT_ID = getIntent().getIntExtra(NotesDB.ID, 0);       //记录所查看的日记 id 号
        DEFAULT_STRING_ID = Integer.toString(DEFAULT_INT_ID);           //记录未修改前的 ID
        DEFAULT_CONTENT = getIntent().getStringExtra(NotesDB.CONTENT);  //记录未修改前的 CONTENT

        initToolbar();  //初始化 Toolbar
        initView();     //显示具体日记

        /*//调试信息
        Log.d("Tag", getIntent().getStringExtra(NotesDB.CONTENT));
        Log.d("Tag", getIntent().getStringExtra(NotesDB.TIME));
*/

        //判断文本编辑框是否是否被点击，点击后就将返回按钮  换成  保存按钮。
        detail_editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detail_editText.setCursorVisible(true);    //显示光标
                toolbar.setNavigationIcon(R.drawable.ic_action_note_detail_modify_save);//修改返回按钮为保存按钮
                toolbar.setTitle("");
                toolbarTitle.setText("修改日记");
                flag = 1;

                Log.d("Tag1", DEFAULT_STRING_ID);
            }
        });


        //添加返回按钮事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                Log.d("TagAfterClick", detail_editText.getText().toString());
                //点击返回按钮 或 点击保存按钮，就保存最后修改后的 EditText

                Intent upIntent = NavUtils.getParentActivityIntent(LookDiaryActivity.this);
                if (NavUtils.shouldUpRecreateTask(LookDiaryActivity.this, upIntent)) {
                    modify();
                    TaskStackBuilder.create(LookDiaryActivity.this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    modify();
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    NavUtils.navigateUpTo(LookDiaryActivity.this, upIntent);
                }
            }
        });

    }


    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_note_detail);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        toolbarTitle.setText("查看日记");
        toolbar.setNavigationIcon(R.drawable.ic_action_note_detail_return);     //设置返回按钮
        setSupportActionBar(toolbar);
    }

    public void initView() {
        detail_editText = (EditText) findViewById(R.id.note_detail_extext);
        detail_time = (TextView) findViewById(R.id.note_detail_time);

        detail_editText.clearFocus();   //清除EditText的焦点
        detail_editText.setCursorVisible(false);    //隐藏光标

        detail_editText.setText(getIntent().getStringExtra(NotesDB.CONTENT));   //显示日记内容
        detail_time.setText(getIntent().getStringExtra(NotesDB.TIME));          //显示日记时间
    }


    //加载删除布局按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //实现删除按钮事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.note_detail_delete:
                AlertDialog.Builder dialog = new AlertDialog.Builder(LookDiaryActivity.this);
                dialog.setTitle("删除日记");
                dialog.setMessage("真的要删除我么(＞﹏＜)");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDate();
                        finish();
                    }
                });

                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteDate() {
        dbWriter.delete(NotesDB.TABLE_NAME, "_id=" + getIntent().getIntExtra(NotesDB.ID, 0), null);
    }


    public void modify() {
        Log.d("TagAfterClickModify", detail_editText.getText().toString());

        ContentValues cv = new ContentValues();
        cv.put(NotesDB.CONTENT, detail_editText.getText().toString());
//        cv.put(NotesDB.TIME, getTime().toString());   //这里点击查看日记后，不需要重新修改时间

        dbWriter.update(NotesDB.TABLE_NAME, cv, "_id=?", new String[]{Integer.toString(DEFAULT_INT_ID)});
    }

//    public String getTime() {
//        SimpleDateFormat format = new SimpleDateFormat("HH:mm  yyyy.MM.dd");
//        Date date = new Date();
//        String str = format.format(date);
//        return str;
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        modify();
        finish();
    }

}
