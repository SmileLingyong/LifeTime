package com.example.smile.lifetime.activity;

import android.Manifest;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.smile.lifetime.R;
import com.example.smile.lifetime.db.NotesDB;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lly54 on 2017/4/6.
 */

public class NoteEditActivity extends AppCompatActivity {

    private final String DEFAULT_EDITTEXT = "  写点什么吧";
    private String MyPosition = "";
    private Button savebtn, cancalbtn;
    private TextView editText, toolbarTitle;
    private Toolbar toolbar;

    private NotesDB notesDB;            //创建一个数据库
    private SQLiteDatabase dbWriter;    //创建一个添加权限

    public LocationClient mLocationClient;  //创建一个LocationClient实例

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        initToolbar();
        initLocation();

        editText = (TextView) findViewById(R.id.edit_content);

        notesDB = new NotesDB(this);                //实例化数据库
        dbWriter = notesDB.getWritableDatabase();    //获取到当前可添加的权
    }

    //初始化Toolbar
    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_note_edit);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbar.setNavigationIcon(R.drawable.ic_action_note_create_cancel);//设置返回按钮

        //添加返回按钮事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                Intent upIntent = NavUtils.getParentActivityIntent(NoteEditActivity.this);
                if (NavUtils.shouldUpRecreateTask(NoteEditActivity.this, upIntent)) {
                    TaskStackBuilder.create(NoteEditActivity.this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    NavUtils.navigateUpTo(NoteEditActivity.this, upIntent);
                }
            }
        });

        toolbar.setTitle("");
        toolbarTitle.setText("新建日记");
        setSupportActionBar(toolbar);

    }

    //初始化定位功能
    public void initLocation() {
        // 创建了一个LocationClient的实例，
        // 调用LocationClient的registerLocationListerner()方法来注册一个定位监听器，当获取到位置信息的时候，就会回调这个定位监听器。
        // 创建一个空的List集合，然后依次判断申请的3个权限有没有被授权，如果没有被授权就添加到List集合中，最后将List转换成数组，
        // 再调用ActivityCompat.requestPermissions()方法一次性申请。
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(NoteEditActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(NoteEditActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(NoteEditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(NoteEditActivity.this, permissions, 1);
        } else {
            requestLocation();
        }
    }

    //设置地理位置定位
    private void requestLocation() {
        LocationClientOption option = new LocationClientOption(); //创建一个LocationClientOption()对象 option
        option.setIsNeedAddress(true);  //设置需要街道信息
        mLocationClient.setLocOption(option);
        mLocationClient.start();    //开始定位功能
    }



    //对权限申请结果的逻辑处理
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation.getCity() == null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(NoteEditActivity.this);
                MyPosition = prefs.getString("usual_location", null);
                Log.d("Tag", MyPosition);
            } else {
                MyPosition = bdLocation.getCity();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(NoteEditActivity.this).edit();
                editor.putString("usual_location", MyPosition);
                Log.d("Tag", MyPosition);
                editor.apply();
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }


    //隐藏键盘
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // 加载保存按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // 保存按钮点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_note_create_save:
                if (editText.getText().length() == 0) {
                    finish();
                } else {
                    addDB();
                    Log.d("NoteEditActivity.class", "addDB");
                    finish();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //将EditText中的数据保存至数据库
    public void addDB() {
        ContentValues cv = new ContentValues();
        cv.put(NotesDB.CONTENT, editText.getText().toString());
        cv.put(NotesDB.TIME, getTime());
        cv.put(NotesDB.LOCATION, MyPosition);
//        Log.d("Tag", MyPosition);
        dbWriter.insert(NotesDB.TABLE_NAME, null, cv);
    }

    //获取系统当前时间
    public String getTime() {
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("HH:mm  yyyy.MM.dd");
        Date date = new Date();
        String str = format.format(date);
        return str;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }
}
