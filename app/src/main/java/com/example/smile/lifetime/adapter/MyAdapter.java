package com.example.smile.lifetime.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smile.lifetime.R;

/**
 * Created by lly54 on 2017/4/6.
 */

public class MyAdapter  extends BaseAdapter {

    private Context context;//承接上下文的context
    private Cursor cursor; //数据库查询出来的结果，相当于结果集ResultSet,
    // Cursor是一个游标接口，提供了遍历查询结果的方法，如移动指针方法move()，获得列值方法getString()等
    private LayoutInflater inflater;

    public MyAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return cursor.getPosition();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            //动态加载布局文件
            inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.card_item, null);
            //然后再关联相应的布局文件
            holder = new ViewHolder();
            holder.contenttv = (TextView) view.findViewById(R.id.list_content);
            holder.timetv = (TextView) view.findViewById(R.id.list_time);
            holder.localtv = (TextView) view.findViewById(R.id.note_list_card_location_text);
            holder.imageTime = (ImageView) view.findViewById(R.id.pic_time);
            holder.imageLocal = (ImageView) view.findViewById(R.id.pic_local);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        cursor.moveToPosition(position);
        String content = cursor.getString(cursor.getColumnIndex("content"));
        String location = cursor.getString(cursor.getColumnIndex("location"));
        String time = cursor.getString(cursor.getColumnIndex("time"));

        holder.contenttv.setText(content);
        holder.timetv.setText(time);
        holder.localtv.setText(location);
        return view;
    }


    class ViewHolder {
        TextView contenttv;
        TextView timetv;
        TextView localtv;
        ImageView imageTime;
        ImageView imageLocal;
    }


}
