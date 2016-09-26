package com.example.udacity.todolist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;


public class TaskDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TasksDb.db";
    private static final String TABLE_NAME = TaskContract.ItemEntry.TABLE_NAME;

    private static final int VERSION = 6;


    // create SQL table (careful about formatting)
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    TaskContract.ItemEntry._ID + " INTEGER PRIMARY KEY, " +
                    TaskContract.ItemEntry.COLUMN_TASK_NAME + " TEXT NOT NULL, " +
                    TaskContract.ItemEntry.COLUMN_PRIORITY + " INTEGER NOT NULL);";


    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;


    TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

}
