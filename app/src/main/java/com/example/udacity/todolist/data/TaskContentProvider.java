package com.example.udacity.todolist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class TaskContentProvider extends ContentProvider {

    public static final Uri CONTENT_URI = TaskContract.ItemEntry.CONTENT_URI; //public to access in main


    //1. Define URIs - what information do you want to access/display?

    /* one for the whole table
    one for a row (in this case a row that contains data about one task)
    make a note: match Sunshine - these ints can be any value
    convention is: if you have a main table, yu call it 100,
    and for multiple uri's in that table, do 101, 102, 103...
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
            TaskContract.ItemEntry.TABLE_NAME+
                    "." + TaskContract.ItemEntry.COLUMN_PRIORITY + " = ? ";


    public TaskContentProvider() {
    }

    // 2. Build the URI matcher - based on the URI int's you declared above!

    private static UriMatcher buildUriMatcher() {

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // For each kind of uri you may want to access, add the corresponding match code
        // addUri(authority, path, int match code)
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS, TASKS);
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS+ "/#", ONE_TASK);
        return uriMatcher;
    }


    //3. Implement getType
    // using URI matching (not used; will provide this method to the student)
    @Override
    public String getType(Uri uri) {
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
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.

        String id;

        switch (sUriMatcher.match(uri)) {
            case TASKS: id = null;
                break;
            case ONE_TASK:
                //get(1) returns  the path segment of the uri with index = 1
                // in this case: content://com.example.udacity.todolist/tasks/#
                // tasks is at index 0, the # which is the row, is at index 1
                id = uri.getPathSegments().get(1);
                break;
            default:
                throw new UnsupportedOperationException("Uri match not recognized!");
        }
        //NOTIFICATION and return cursor
        Cursor c = mTaskDbHelper.getTasks(id, projection, selection, selectionArgs, sortOrder);
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

        switch (sUriMatcher.match(uri)) {
            case TASKS:
                long id = mTaskDbHelper.addNewTask(values);
                // sunshine uses a helper build uri method for the line below, hmm
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

        switch (sUriMatcher.match(uri)) {
            case TASKS: id = null;
                break;
            case ONE_TASK:
                //delete a single task by getting the id
                id = uri.getPathSegments().get(1);
                tasksDeleted = mTaskDbHelper.deleteTask(id);
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

        switch (sUriMatcher.match(uri)) {
            case TASKS: id = null;
                break;
            case ONE_TASK:
                //delete a single task b getting the id
                id = uri.getPathSegments().get(1);
                tasksUpdated = mTaskDbHelper.updateTasks(id, values);
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


    //9.1 Create helper method for building a selection query

    private Cursor getTasksByPriority(Uri uri, String[] projection, String sortOrder) {
        //change to priority
        //String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);

        // will select all tasks with Priority = 1
        // can change this to be selectable
        String prioritySetting = "1";

        String selection;
        String[] selectionArgs;

        selection = sPrioritySelection;
        selectionArgs = new String[]{prioritySetting};

        String id;

        switch (sUriMatcher.match(uri)) {
            case TASKS: id = null;
                break;
            case ONE_TASK:
                //get(1) returns  the path segment of the uri with index = 1
                // in this case: content://com.example.udacity.todolist/tasks/#
                // tasks is at index 0, the # which is the row, is at index 1
                id = uri.getPathSegments().get(1);
                break;
            default:
                throw new UnsupportedOperationException("Uri match not recognized!");
        }


        return mTaskDbHelper.getTasks(id,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
    }



    /*
    // create 1) selection clause and 2) corresponding args
        // this lets you choose rows based on a selection criteria

        // Defines a string to contain the selection clause
        String mSelectionClause = null;

        // Initializes an array to contain selection arguments
        // defines a one element String array to contain the selection argument
        String[] mSelectionArgs = {""};

        mSelectionClause = TaskContract.ItemEntry.COLUMN_PRIORITY + " = ?";

        // what row of priority do you want to select?

        //Could add this as an input parameter
        mSelectionArgs[0] = "1";

        //actually do something with those selection args (query then update)
        Cursor c = getContentResolver().query(TaskContentProvider.CONTENT_URI,
                null,
                mSelectionClause,
                mSelectionArgs,
                null);

        mAdapter.swapCursor(c);
     */
}
