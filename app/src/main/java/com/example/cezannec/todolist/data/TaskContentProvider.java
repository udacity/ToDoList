package com.example.cezannec.todolist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class TaskContentProvider extends ContentProvider {
    //authority
    private static final String AUTHORITY = TaskContract.AUTHORITY;


    //1. Define URIs - what information do you want to access/display?
    //content uri, should this be in Contract?
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/tasks"); // public -- to access in Main
    // one for the whole table
    // one for a row (in this case data about one task)
    private static final int TASKS = 1;
    private static final int ONE_TASK = 2;
    private static final UriMatcher uriMatcher = getUriMatcher();


    //2. Build the URI matcher - based on the URI int's above!
    //4.1 declare database so you can access it throughout (initialized in onCreate() )
    private TaskDbHelper taskDb = null;

    public TaskContentProvider() {
    }

    // 2 (continued)
    private static UriMatcher getUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "tasks", TASKS);
        uriMatcher.addURI(AUTHORITY, "tasks/#", ONE_TASK);
        return uriMatcher;
    }


    //(optional step --pre-3) getType
    // using URI matching (THIS IS NOT GOING TO BE USED IN SUNSHINE, so I can leave this method empty)
    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.

        switch (uriMatcher.match(uri)) {
            case TASKS:
                return "vnd.android.cursor.dir/vnd.com.example.cezannec.todolist.tasks";
            case ONE_TASK:
                return "vnd.android.cursor.item/vnd.com.example.cezannec.todolist.tasks";
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
        taskDb = new TaskDbHelper(context);
        return true;
        //return false;
    }


    //5. Implement query

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        String id = null;
        if (uriMatcher.match(uri) == ONE_TASK) {
            //this is for the data for one book; get that row data from the URI's ID!
            id = uri.getPathSegments().get(1);
        }

        //NOTIFICATION and return cursor
        Cursor c = taskDb.getTasks(id, projection, selection, selectionArgs, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;

        //throw new UnsupportedOperationException("Not yet implemented");
    }


    //6. Write insert - show what happens with and w/o setting notifications!

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        //throw new UnsupportedOperationException("Not yet implemented");

        try {
            long id = taskDb.addNewTask(values);
            Uri returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
            //Notify
            getContext().getContentResolver().notifyChange(uri, null);
            return returnUri;
        } catch (Exception e) {
            return null;
        }
    }

    // 7. Implement delete (if you want?)
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        //throw new UnsupportedOperationException("Not yet implemented");
        String id = null;

        // keep track of if tasks are indeed deleted
        int tasksDeleted; // init as 0

        if (uriMatcher.match(uri) == ONE_TASK) {
            //Delete is for one single task. Get the ID from the URI. -- one row!
            id = uri.getPathSegments().get(1);
            System.out.println("ID path segment in provider = " + id);
        }

        tasksDeleted = taskDb.deleteTask(id);
        if(tasksDeleted != 0){
            //a task was deleted
            //Notify
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksDeleted;
    }

    //8. Update

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        //throw new UnsupportedOperationException("Not yet implemented");
        String id = null;

        //keep track of if an update occurs
        int tasksUpdated;

        if (uriMatcher.match(uri) == ONE_TASK) {
            //Update is for one single task. Get the ID from the URI.
            id = uri.getPathSegments().get(1);
        }

        tasksUpdated = taskDb.updateTasks(id, values);
        if(tasksUpdated != 0){
            //Notify
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksUpdated;
    }
}
