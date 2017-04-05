package com.example.smile.lifetime.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lly54 on 2017/4/6.
 */

public class NotesDB extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "notes";
    public static final String ID = "_id";
    public static final String CONTENT = "content";
    public static final String LOCATION = "location";
    public static final String TIME = "time";


    public NotesDB(Context context) {
        super(context, "notes", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CONTENT + " TEXT NOT NULL,"
                + LOCATION + " TEXT NOT NULL,"
                + TIME + " TEXT NOT NULL)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
