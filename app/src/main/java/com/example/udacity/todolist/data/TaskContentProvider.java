package com.example.udacity.todolist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.example.udacity.todolist.data.TaskContract.ItemEntry.TABLE_NAME;

public class TaskContentProvider extends ContentProvider {

    public static final Uri CONTENT_URI = TaskContract.ItemEntry.CONTENT_URI; //public to access in main


    //1. Define URIs - what information do you want to access/display/insert/delete?

    /* one for the whole table
    one for a row (in this case a row that contains data about one task)
    make a note: match Sunshine - these ints can be any value
    convention is: if you have a main table, you call it 100,
    and for multiple uri's in that table, do 101, 102, 103...
    for a separate table, 200, 201, 202...
     */
    private static final int TASKS = 100;
    private static final int ONE_TASK = 101;

    //Uri matcher that we construct
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    //4.1 declare database so you can access it throughout (initialized in onCreate() )
    private TaskDbHelper mTaskDbHelper;


    //9. Latest step - add selections! -
    //priority = ?
    private static final String sPrioritySelection =
            TABLE_NAME+
                    "." + TaskContract.ItemEntry.COLUMN_PRIORITY + " = ? ";



    // 2. Build the URI matcher - based on the URI int's you declared above!

    private static UriMatcher buildUriMatcher() {

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // For each kind of uri you may want to access, add the corresponding match code
        // addUri(authority, path, int match code)
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS, TASKS);
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS + "/#", ONE_TASK);
        return uriMatcher;
    }


    //3. Implement getType
    // using URI matching (not used; will provide this method to the student)
    @Override
    public String getType(@NonNull Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.

        switch (sUriMatcher.match(uri)) {
            case TASKS:
                return TaskContract.ItemEntry.CONTENT_TYPE;
            case ONE_TASK:
                return TaskContract.ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Uri match not recognized!");
                //throw new UnsupportedOperationException("Not yet implemented");
        }
    }


    //4. Initialize provider and link it to database helper

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        Context context = getContext();
        mTaskDbHelper = new TaskDbHelper(context);
        return true;
        //return false;
    }


    //5. Implement query

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.

        //1. get access to db

        final SQLiteDatabase db = mTaskDbHelper.getReadableDatabase();

        Cursor retCursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case TASKS:
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //NOTIFICATION and return cursor

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

        //throw new UnsupportedOperationException("Not yet implemented");
    }


    //6. Write insert - show what happens with and w/o setting notifications!

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        //throw new UnsupportedOperationException("Not yet implemented");
        // check uri for validity

        //1. get access to our database (to write new data to)
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();

        Uri returnUri; // to be returned

        //2. Matching code
        int match = sUriMatcher.match(uri);

        switch (match) {
            case TASKS:
                long id = db.insert(TABLE_NAME, null, values);
                if ( id > 0 )
                    returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //Notify
        getContext().getContentResolver().notifyChange(uri, null);

        // return constructed uri
        return returnUri;

    }

    // 7. Implement delete (if you want?)
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        //throw new UnsupportedOperationException("Not yet implemented");

        // keep track of if tasks are indeed deleted
        int tasksDeleted = 0; // init as 0

        String id;

        switch (sUriMatcher.match(uri)) {
            case TASKS:
                break;
            case ONE_TASK:
                //delete a single task by getting the id
                id = uri.getPathSegments().get(1);
                //using selections
                tasksDeleted = mTaskDbHelper.getWritableDatabase().delete(TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksDeleted != 0) {
            //a task was deleted
            //Notify
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksDeleted;
    }

    //8. Update

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        //throw new UnsupportedOperationException("Not yet implemented");

        //keep track of if an update occurs
        int tasksUpdated = 0;

        String id;

        switch (sUriMatcher.match(uri)) {
            case TASKS:
                break;
            case ONE_TASK:
                //delete a single task b getting the id
                id = uri.getPathSegments().get(1);
                //using selections
                tasksUpdated = mTaskDbHelper.getWritableDatabase().update(TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksUpdated != 0) {
            //Notify
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksUpdated;
    }

}
