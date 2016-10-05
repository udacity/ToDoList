package com.example.udacity.todolist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import static com.example.udacity.todolist.data.TaskContract.TaskEntry.TABLE_NAME;



//TODO: 1. Create TaskContentProvider class that extends from ContentProvider
//1.2. Add all necessary empty methods

public class TaskContentProvider extends ContentProvider {

    //1.3. create a DbHelper class variable, so you can access it throughout your code
    // this is initialized in the onCreate() method
    private TaskDbHelper mTaskDbHelper;


    //3.1. Define the URIs and ints for them to match to

    public static final Uri CONTENT_URI = TaskContract.TaskEntry.CONTENT_URI; //public to access in main

    /* Define one final int for the directory of all tasks and one for a single item.
    It is convention to use 100, 200, 300, etc for directories
    and related ints (101, 102, ..) for items in that directory.
     */
    private static final int TASKS = 100;
    private static final int TASK_WITH_ID = 101;

    //3.2. Declare a class variable for the Uri matcher that you construct
    private static final UriMatcher sUriMatcher = buildUriMatcher();


    //TODO: 3. Build the URI matcher - based on the URI int's you declared above
    private static UriMatcher buildUriMatcher() {

        /* 3.3. Initialize a UriMatcher with no initially set matches by passing in
        the argument NO_MATCH to the constructor.
         */
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //All paths added to the UriMatcher have a corresponding int

        //3.4. For each kind of uri you may want to access, add the corresponding match code
        //by using the function: addUri(authority, path, int match code)
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS, TASKS);
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS + "/#", TASK_WITH_ID);
        return uriMatcher;
    }


    //TODO: 1.1. Implement onCreate() to initialize your content provider on startup

    /* onCreate() is where you should initialize anything you’ll need to setup
    your underlying data source.
    In this case, you’re working with a SQLite database, so you’ll need to
    initialize a DbHelper to gain access to it.
     */
    @Override
    public boolean onCreate() {
        Context context = getContext();
        mTaskDbHelper = new TaskDbHelper(context);
        return true;
        //return false;
    }



    //TODO: 4. Implement insert to handle requests to insert a new row of data

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        //4.1. get access to our database (to write new data to)
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();

        Uri returnUri; // to be returned

        //4.2. Matching code
        int match = sUriMatcher.match(uri);

        switch (match) {
            case TASKS:
                //4.3. Insert into tasks table
                long id = db.insert(TABLE_NAME, null, values);
                if ( id > 0 )
                    returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            //4.4. code default UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        //4.5. Notify the resolver that the uri has been changed
        getContext().getContentResolver().notifyChange(uri, null);

        //4.6. return constructed uri (this points to the newly inserted row of data)
        return returnUri;

    }


    //TODO: 5. Implement query to handle requests for data by URI.

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        //5.1. Get access to underlying database (read-only)
        final SQLiteDatabase db = mTaskDbHelper.getReadableDatabase();

        Cursor retCursor;

        //5.2. URI match code
        int match = sUriMatcher.match(uri);

        switch (match) {
            case TASKS:
                // 5.3. query for the tasks directory
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            // 5.4. default case should throw an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        //5.5. set a notification on the queried URI
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        //5.6. return the desired Cursor
        return retCursor;
    }


    //TODO: 6. Implement delete to delete a single row of data.

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        //6.1. get access to our database
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();

        // Keep track of the number of deleted tasks
        int tasksDeleted = 0; // init as 0
        String id;

        //6.2. Match code
        int match = sUriMatcher.match(uri);

        switch (match) {
            //6.3. handle single item case
            case TASK_WITH_ID:
                //delete a single task by getting the id
                id = uri.getPathSegments().get(1);
                //using selections
                tasksDeleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksDeleted != 0) {
            //6.4. a task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //6.5. Return the number of tasks deleted
        return tasksDeleted;
    }


    //TODO: 7. [Optional] Implement update to handle requests for updating a single row
    // This function won't be used n our final app but is included for completeness

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        //Keep track of if an update occurs
        int tasksUpdated = 0;
        String id;

        // match code
        int match = sUriMatcher.match(uri);

        switch (match) {
            case TASK_WITH_ID:
                //update a single task by getting the id
                id = uri.getPathSegments().get(1);
                //using selections
                tasksUpdated = mTaskDbHelper.getWritableDatabase().update(TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksUpdated != 0) {
            //set notifications if a task was updated
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // return number of tasks updated
        return tasksUpdated;
    }


    //TODO: [Optional] 8. Implement getType
    /* getType() handles requests for the MIME type of data
    We are working with two types of data:
    1) a directory and 2) a single row of data.
    This method will not be used in our app, but gives a way to standardize the data formats
    that your provider accesses, and this can be useful for data organization.
    For now, this method will not be used but will be provided for completeness.
     */
    @Override
    public String getType(Uri uri) {

        int match = sUriMatcher.match(uri);

        switch (match) {
            case TASKS:
                // directory
                return TaskContract.TaskEntry.CONTENT_TYPE;
            case TASK_WITH_ID:
                // single item type
                return TaskContract.TaskEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

}
