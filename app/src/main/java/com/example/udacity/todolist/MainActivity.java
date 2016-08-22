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
    private RecyclerView mRecyclerView;
    //need to create custom adapter class first
    private CustomCursorAdapter mAdapter;

    private boolean isSorted; //initialized as false

    private int priority; // keep track of what priority is checked


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new CustomCursorAdapter(this);

        //set up Recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewTasks);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setAdapter(mAdapter);


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
                mAdapter.swapCursor(c);
            }
        }).attachToRecyclerView(mRecyclerView);


        // initialize to Priority = 1
        ((RadioButton) findViewById(R.id.firstButton)).setChecked(true);
        priority = 1; // init to P1

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

        // check if the input is empty (won't create an entry in this case)
        String input = ((EditText) findViewById(R.id.editTextTaskTitle)).getText().toString();
        if(input.length()== 0){
            return;
        }

        contentValues.put(TaskContract.ItemEntry.COLUMN_TASK_TITLE,
                ((EditText) findViewById(R.id.editTextTaskTitle)).getText().toString());


        if(((RadioButton) findViewById(R.id.firstButton)).isChecked()){
            priority = 1;
        } else if(((RadioButton) findViewById(R.id.secondButton)).isChecked()){
            priority = 2;
        } else if(((RadioButton) findViewById(R.id.thirdButton)).isChecked()){
            priority = 3;
        }

        contentValues.put(TaskContract.ItemEntry.COLUMN_PRIORITY, priority);

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
        mAdapter.swapCursor(c);

        // show the uri that the inserted entry is in
        Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();

        //Set Edit text back to empty fields
        ((EditText) findViewById(R.id.editTextTaskTitle)).getText().clear();

        // reset priority to 1
        //((RadioGroup) findViewById(R.id.priorityGroup)).clearCheck();
        ((RadioButton) findViewById(R.id.firstButton)).setChecked(true);
    }




    //radio button click code -- just to check it's working
    public void onRadioButtonClicked(View view){
        //handle if a radio button is checked
        System.out.println("Clicked!");

    }

    // sorting button function
    public void onClickSort(View view) {

        // add button toggle behavior
        isSorted = !isSorted; // toggles from true <-> false

        Cursor c;

        if(isSorted) {

            //TODO: sorting by priority - can have them test out different ways to sort
            String sortId = TaskContract.ItemEntry.COLUMN_PRIORITY;
            //System.out.println("Sort id = " + sortId);

            //query the content provider for a new sorted cursor
            //should auto update bc of loader?
            c = getContentResolver().query(TaskContentProvider.CONTENT_URI,
                    null,
                    null,
                    null /* selection args need to test */,
                    sortId);

        } else {
            // unsorted
            c = getContentResolver().query(TaskContentProvider.CONTENT_URI, TaskContract.ALL_COLUMNS, null, null, null);
        }
        mAdapter.swapCursor(c);
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

        mAdapter.swapCursor(c);


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
        mAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);

    }
}

