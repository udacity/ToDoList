package com.example.cezannec.todolist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

/**
 * Created by cezannec on 8/13/16.
 */

public class TaskDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TasksDb.db";
    private static final String TABLE_NAME = TaskContract.ItemEntry.TABLE_NAME;

    private static final int VERSION = 1;


    // create SQL table (careful about formatting)
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    TaskContract.ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TaskContract.ItemEntry.COLUMN_TASKTITLE + " TEXT NOT NULL, " +
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

    public Cursor getTasks(String id, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(TABLE_NAME);

        if (id != null) {
            sqliteQueryBuilder.appendWhere("_id" + " = " + id);
        }

        Cursor cursor = sqliteQueryBuilder.query(getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        return cursor;
    }

    //NEW for inserting, etc

    public long addNewTask(ContentValues values) throws SQLException {

        final SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_NAME, null, values);
        if (id <= 0) {
            throw new SQLException("Failed to add task");
        }
        return id;
    }

    public int deleteTask(String id) {
        if (id == null) {
            return getWritableDatabase().delete(TABLE_NAME, null, null);
        } else {
            return getWritableDatabase().delete(TABLE_NAME, "_id=?", new String[]{id});
        }
    }

    public int updateTasks(String id, ContentValues values) {
        if (id == null) {
            return getWritableDatabase().update(TABLE_NAME, values, null, null);
        } else {
            return getWritableDatabase().update(TABLE_NAME, values, "_id=?", new String[]{id});
        }
    }

}
