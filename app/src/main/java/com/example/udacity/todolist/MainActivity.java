package com.example.udacity.todolist;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.udacity.todolist.data.TaskContentProvider;
import com.example.udacity.todolist.data.TaskContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Recycler view vars
    private RecyclerView recView;
    //need to create custom adapter class first
    private CustomCursorAdapter rAdapter;

    private boolean isSorted; //initialized as false


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rAdapter = new CustomCursorAdapter(this);

        //set up Recycler view
        recView = (RecyclerView) findViewById(R.id.recyclerViewTasks);

        recView.setLayoutManager(new LinearLayoutManager(this));

        recView.setAdapter(rAdapter);


        //TODO: implement swipe delete

        // Add a touch helper to the recyclerview to handle swiping items off the db
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int myId = (int) viewHolder.itemView.getTag();
                System.out.println("tag for itemView id:  " + myId);
                String sId = "" + myId;
                //build appropriate uri with row id appended
                Uri uri = TaskContract.ItemEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath("" + myId).build();

                //view uri for debug
                Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();

                getContentResolver().delete(uri, null, null);

                Cursor c;
                if(isSorted) {
                    c = getContentResolver().query(TaskContentProvider.CONTENT_URI, TaskContract.ALL_COLUMNS, null, null,
                            TaskContract.ItemEntry.COLUMN_PRIORITY);

                } else {
                    c = getContentResolver().query(TaskContentProvider.CONTENT_URI, TaskContract.ALL_COLUMNS, null, null, null);
                }
                rAdapter.swapCursor(c);
            }
        }).attachToRecyclerView(recView);

        // set up loader
        getSupportLoaderManager().restartLoader(0, null, this);
    }


    /*
    No created menu yet
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    */

    public void onClickAddTask(View view) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(TaskContract.ItemEntry.COLUMN_TASK_TITLE,
                ((EditText) findViewById(R.id.editTextTaskTitle)).getText().toString());

        int checked = 4; // init to 4, so it's lower priority than the rest of the tasks
        //1-3 = high to low priorities
        if(((RadioButton) findViewById(R.id.firstButton)).isChecked()){
            checked = 1;
        } else if(((RadioButton) findViewById(R.id.secondButton)).isChecked()){
            checked = 2;
        } else if(((RadioButton) findViewById(R.id.thirdButton)).isChecked()){
            checked = 3;
        }

        contentValues.put(TaskContract.ItemEntry.COLUMN_PRIORITY, checked);

        // insert values through content resolver
        Uri uri = getContentResolver().insert(TaskContentProvider.CONTENT_URI, contentValues);

        //update cursor
        Cursor c;
        if(isSorted) {
            c = getContentResolver().query(TaskContentProvider.CONTENT_URI, TaskContract.ALL_COLUMNS, null, null,
                    TaskContract.ItemEntry.COLUMN_PRIORITY);

        } else {
            c = getContentResolver().query(TaskContentProvider.CONTENT_URI, TaskContract.ALL_COLUMNS, null, null, null);
        }
        rAdapter.swapCursor(c);

        // show the uri that the inserted entry is in
        Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();

        //Set Edit text back to empty fields
        ((EditText) findViewById(R.id.editTextTaskTitle)).getText().clear();
        ((RadioGroup) findViewById(R.id.priorityGroup)).clearCheck();
    }




    //radio button click code -- just to check it's working
    public void onRadioButtonClicked(View view){
        //handle if a radio button is checked
        System.out.println("Clicked!");

    }

    // sorting button function
    public void onClickSort(View view) {

        isSorted = true;

        //TODO: sorting by priority - can have them test out different ways to sort
        String sortId = TaskContract.ItemEntry.COLUMN_PRIORITY;
        //System.out.println("Sort id = " + sortId);

        //query the content provider for a new sorted cursor
        //should auto update bc of loader?
        Cursor c = getContentResolver().query(TaskContentProvider.CONTENT_URI,
                null,
                null,
                null /* selection args need to test */,
                sortId);

        rAdapter.swapCursor(c);
    }


    // playing around with cursor display and selection args
    /*
    public void onClickSelection(View view) {

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

        rAdapter.swapCursor(c);


    }
    */



    //------ Loader code -----

    // Loaders will automatically update the view only if
    // notifications are set!
    /*
    Could show student an insert (with a Toast) and how a view changes/doesn't change
    based on including the cursor.setNotificationUri(getContext().getContentResolver(), uri);
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cursorLoader = new CursorLoader(getBaseContext(), TaskContentProvider.CONTENT_URI,
                null, null, null, null);

        return cursorLoader;
        //return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //adapter..
        rAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        rAdapter.swapCursor(null);

    }
}

