package com.example.smile.lifetime.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.smile.lifetime.R;
import com.example.smile.lifetime.adapter.MyAdapter;
import com.example.smile.lifetime.db.NotesDB;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private FloatingActionButton addButton;
    private ListView listView;      //ListView项
    private DrawerLayout mDrawerLayout;

    private MyAdapter adapter;
    private NotesDB notesDB;        //创建一个数据库对象
    private SQLiteDatabase dbReader;    //创建一个读取权限
    private Cursor cursor;

    //结束启动界面,转换到主页
    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }


    public void initView() {
        listView = (ListView) findViewById(R.id.list);

        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle("生活点滴");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        navView.setCheckedItem(R.id.nav_home);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        mDrawerLayout.closeDrawers();

                        break;
                    case R.id.nav_news:
                        mDrawerLayout.closeDrawers();
                        Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_theme:
                        ChangeTheme();
//                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_aboutme:
                        Intent intent_aboutme = new Intent(MainActivity.this, AboutmeActivity.class);
                        startActivity(intent_aboutme);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_setting:
                        mDrawerLayout.closeDrawers();
                        break;
                    default:
                        mDrawerLayout.closeDrawers();
                        break;
                }

                return true;
            }
        });

        addButton = (FloatingActionButton) findViewById(R.id.add_button);
        addButton.setOnClickListener(this);

        notesDB = new NotesDB(this);    //实例化数据库
        dbReader = notesDB.getReadableDatabase();   //获取读取的权限

        //获取点击ListView的事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                Intent i = new Intent(MainActivity.this, LookDiaryActivity.class);
                i.putExtra(NotesDB.ID, cursor.getInt(cursor.getColumnIndex(NotesDB.ID)));
                i.putExtra(NotesDB.CONTENT, cursor.getString(cursor.getColumnIndex(NotesDB.CONTENT)));
                i.putExtra(NotesDB.TIME, cursor.getString(cursor.getColumnIndex(NotesDB.TIME)));
                startActivity(i);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_button:
                Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
                startActivity(intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_note_main_search, menu);
        final MenuItem item_search = menu.findItem(R.id.action_note_main_search);
        final MenuItem item_change_theme = menu.findItem(R.id.action_note_main_change_theme);
        // 注意这里导报要 导入 android.widget.SearchView 而不是  android.support.v7.widget.SearchView;
        SearchView searchView = (SearchView) item_search.getActionView();      //通过以上几句 获取SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //点击搜索按钮后的功能
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("Tag", query);
                searchDB(query);
                return false;
            }

            //每当监控到EditText中有变化就执行该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                //用try catch 处理输入 单引号的异常（因为这样SQL语句会不完整，有错，程序会崩掉）
                try {
                    searchDB(newText);
                } catch (Exception e) {

                }
                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_note_main_change_theme:
                ChangeTheme();
                break;
        }

        return true;
    }

    public void ChangeTheme() {
        SharedPreferences sp = getSharedPreferences("user_settings", MODE_PRIVATE);
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES) {
            sp.edit().putInt("theme", 0).apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            sp.edit().putInt("theme", 1).apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
        recreate();
    }

    public void selectDB() {
        cursor = dbReader.query(NotesDB.TABLE_NAME, null, null, null, null, null, NotesDB.ID + " DESC"); //通过id降序排列
        adapter = new MyAdapter(this, cursor);   //实例化adapter，通过adapter进行适配
        listView.setAdapter(adapter);
    }

    //查询功能
    public void searchDB(String mSearchText) {
        //注意：这里不能输入单引号！！！（因为这样SQL语句会不完整，出错）
        String sql = "select * from notes where content like '%" + mSearchText + "%' order by _id DESC";
        cursor = dbReader.rawQuery(sql, null); //通过id降序排列
        adapter = new MyAdapter(this, cursor);   //实例化adapter，通过adapter进行适配
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectDB();
    }
}
