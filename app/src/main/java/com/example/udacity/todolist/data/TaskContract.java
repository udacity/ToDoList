package com.example.udacity.todolist.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public class TaskContract {

    // Clients need to know how to access this data
    // So... provide the content URI's for each table

    //1. Specify the authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.udacity.todolist";

    //2. Use the authority to create the Content URI for this whole contract
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    //3. Define the possible paths for accessing data in this contract
    // Possible paths usually point to different tables in here

    public static final String PATH_QUEUE = "tasks"; // it even matches the name of the table!


    //total projection for getting a complete Cursor (for RecyclerView later)

    public static final String[] ALL_COLUMNS = new String[]{
            ItemEntry._ID,
            ItemEntry.COLUMN_TASK_TITLE,
            ItemEntry.COLUMN_PRIORITY
    };



    // Inner class that defines the table contents of the task table

    public static final class ItemEntry implements BaseColumns {


        //4. create the URI that points to this table specifically
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        TaskContract.BASE_CONTENT_URI,
                        PATH_QUEUE
                );

        //Next specify the types of data that can be accessed --
        // There are generally two types of data we will be working with:
        // A single item (like a single String or int number)
        // OR a directory of items, which can be a column or a set of columns

        //public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_QUEUE).build();

        // might not use these mime types
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_QUEUE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_QUEUE;


        public static final String TABLE_NAME = "tasks";

        public static final String COLUMN_TASK_TITLE = "title";

        public static final String COLUMN_PRIORITY = "priority";

    }
}
