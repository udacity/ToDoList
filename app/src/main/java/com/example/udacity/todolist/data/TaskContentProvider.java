package com.example.udacity.todolist.data;

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
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TaskContract.PATH_TASKS); // public -- to access in Main
    // one for the whole table
    // one for a row (in this case data about one task)
    // make a note: match Sunshine
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
        uriMatcher.addURI(AUTHORITY, TaskContract.PATH_TASKS, TASKS);
        uriMatcher.addURI(AUTHORITY, TaskContract.PATH_TASKS+ "/#", ONE_TASK);
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
        taskDb = new TaskDbHelper(context);
        return true;
        //return false;
    }


    //5. Implement query

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.

        String id;

        switch (uriMatcher.match(uri)) {
            case TASKS: id = null;
                break;
            case ONE_TASK:
                //this is for the data for one task; get that row data from the URI's ID!
                id = uri.getPathSegments().get(1);
                break;
            default:
                throw new UnsupportedOperationException("Uri match not recognized!");
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
        // check uri for validity

        Uri returnUri; // to be returned

        switch (uriMatcher.match(uri)) {
            case TASKS:
                long id = taskDb.addNewTask(values);
                returnUri = ContentUris.withAppendedId(CONTENT_URI, id);

                break;
            default:
                throw new UnsupportedOperationException("Uri match not recognized!");
        }
        //Notify
        getContext().getContentResolver().notifyChange(uri, null);

        // return constructed uri
        return returnUri;

    }

    // 7. Implement delete (if you want?)
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        //throw new UnsupportedOperationException("Not yet implemented");

        // keep track of if tasks are indeed deleted
        int tasksDeleted = 0; // init as 0

        String id;

        switch (uriMatcher.match(uri)) {
            case TASKS: id = null;
                break;
            case ONE_TASK:
                //delete a single task b getting the id
                id = uri.getPathSegments().get(1);
                tasksDeleted = taskDb.deleteTask(id);
                break;
            default:
                throw new UnsupportedOperationException("Uri match not recognized!");
        }

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

        //keep track of if an update occurs
        int tasksUpdated = 0;

        String id;

        switch (uriMatcher.match(uri)) {
            case TASKS: id = null;
                break;
            case ONE_TASK:
                //delete a single task b getting the id
                id = uri.getPathSegments().get(1);
                tasksUpdated = taskDb.updateTasks(id, values);
                break;
            default:
                throw new UnsupportedOperationException("Uri match not recognized!");
        }

        if(tasksUpdated != 0){
            //Notify
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksUpdated;
    }
}
