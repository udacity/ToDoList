package com.example.udacity.todolist.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


//TODO: 2. Add Provider URI's to the Contract

/* Clients need to know how to access the task data,
and it's your job to provide the content URI's for each path to that data.
 */

public class TaskContract {

    //2.1. Specify the authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.udacity.todolist";

    //2.2. Define the base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    //2.3. Define the possible paths for accessing data in this contract
    // Each path should correspond to a single directory

    public static final String PATH_TASKS = "tasks"; // has the same name as the "tasks" table



    // TaskEntry is an inner class that defines the contents of the task table
    public static final class TaskEntry implements BaseColumns {

        //2.4. Create the content URI that points to this directory
        // content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();


        //(Included) MIME types for a directory and a single item of data

        /* There are generally two MIME types of data we will be working with:
        1) a directory of items, which can be a row or set of rows
        OR 2) a single item, which is a single row of data
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_TASKS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_TASKS;


        //Table and column names

        public static final String TABLE_NAME = "tasks";

        public static final String COLUMN_DESCRIPTION = "description";

        public static final String COLUMN_PRIORITY = "priority";


        /*
        The above table structure looks something like the sample table below.
        With the name of the table and columns on top, and potential contents in rows

        Note: Because this implements BaseColumns, the _id column is generated automatically

        tasks
         - - - - - - - - - - - - - - - - - - - - - -
        | _id  |    description     |    priority   |
         - - - - - - - - - - - - - - - - - - - - - -
        |  1   |  Complete lesson   |       1       |
         - - - - - - - - - - - - - - - - - - - - - -
        |  2   |    Go shopping     |       3       |
         - - - - - - - - - - - - - - - - - - - - - -
        .
        .
        .
         - - - - - - - - - - - - - - - - - - - - - -
        | 43   |   Learn guitar     |       2       |
         - - - - - - - - - - - - - - - - - - - - - -

         */

    }
}
