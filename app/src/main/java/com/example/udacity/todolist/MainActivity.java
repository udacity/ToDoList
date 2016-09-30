package com.example.udacity.todolist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.udacity.todolist.data.TaskContentProvider;
import com.example.udacity.todolist.data.TaskContract;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //need to create custom adapter class first
    private CustomCursorAdapter mAdapter;

    //

    //private boolean isSorted; //initialized as false

    //private int priority; // keep track of what priority is checked

    // to distinguish loader if you want to refer to it later
    private static final int TASK_LOADER_ID = 0;


    RecyclerView mRecyclerView;

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

                //build appropriate uri with row id appended
                Uri uri = TaskContract.ItemEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath("" + myId).build();

                //view uri for debug
                Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();

                getContentResolver().delete(uri, null, null);

                Cursor c;
                /*
                if (isSorted) {
                    c = getContentResolver().query(TaskContentProvider.CONTENT_URI, TaskContract.ALL_COLUMNS, null, null,
                            TaskContract.ItemEntry.COLUMN_PRIORITY);

                } else {
                */
                    c = getContentResolver().query(TaskContentProvider.CONTENT_URI, TaskContract.ALL_COLUMNS, null, null, TaskContract.ItemEntry.COLUMN_PRIORITY);
                //}
                mAdapter.swapCursor(c);
            }
        }).attachToRecyclerView(mRecyclerView);


        // initialize to Priority = 1
        /*
        ((RadioButton) findViewById(R.id.firstButton)).setChecked(true);
        priority = 1; // init to P1
        */

        // set up loader (OLD)
        //getSupportLoaderManager().restartLoader(0, null, this);

        //setup (NEW)
        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);



        // *new* FAB launch new activity
        // set an onClickListener

        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers category is clicked on.
            @Override
            public void onClick(View view) {
                // Create a new intent to open the {@link NumbersActivity}
                Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);

                // Start the new activity
                startActivity(addTaskIntent);
            }
        });
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

//    /*public void onClickAddTask(View view) {
//        ContentValues contentValues = new ContentValues();
//
//        // check if the input is empty (won't create an entry in this case)
//        String input = ((EditText) findViewById(R.id.editTextTaskName)).getText().toString();
//        if (input.length() == 0) {
//            return;
//        }
//
//        contentValues.put(TaskContract.ItemEntry.COLUMN_TASK_NAME,
//                ((EditText) findViewById(R.id.editTextTaskName)).getText().toString());
//
//
//        if (((RadioButton) findViewById(R.id.firstButton)).isChecked()) {
//            priority = 1;
//        } else if (((RadioButton) findViewById(R.id.secondButton)).isChecked()) {
//            priority = 2;
//        } else if (((RadioButton) findViewById(R.id.thirdButton)).isChecked()) {
//            priority = 3;
//        }
//
//        contentValues.put(TaskContract.ItemEntry.COLUMN_PRIORITY, priority);
//
//        // insert values through content resolver
//        Uri uri = getContentResolver().insert(TaskContentProvider.CONTENT_URI, contentValues);
//
//        //update cursor
//        Cursor c;
//
//        System.out.println("SORTED?? : " + isSorted);
//        if (isSorted) {
//            c = getContentResolver().query(TaskContentProvider.CONTENT_URI, TaskContract.ALL_COLUMNS, null, null,
//                    TaskContract.ItemEntry.COLUMN_PRIORITY);
//
//        } else {
//            c = getContentResolver().query(TaskContentProvider.CONTENT_URI, TaskContract.ALL_COLUMNS, null, null, null);
//        }
//        mAdapter.swapCursor(c);
//
//        // show the uri that the inserted entry is in
//        Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
//
//        //Set Edit text back to empty fields
//        //((EditText) findViewById(R.id.editTextTaskName)).getText().clear();
//
//        // reset priority to 1
//        //((RadioGroup) findViewById(R.id.priorityGroup)).clearCheck();
//        //((RadioButton) findViewById(R.id.firstButton)).setChecked(true);
//    }*/


    //radio button click code -- just to check it's working
    public void onRadioButtonClicked(View view) {
        //handle if a radio button is checked
        System.out.println("Clicked!");

    }

    // sorting button function

//    public void onClickSort(View view) {
//
//        // add button toggle behavior
//        isSorted = !isSorted; // toggles from true <-> false
//
//        Cursor c;
//
//        if (isSorted) {
//
//            //TODO: sorting by priority - can have them test out different ways to sort
//            String sortId = TaskContract.ItemEntry.COLUMN_PRIORITY;
//            //System.out.println("Sort id = " + sortId);
//
//            //query the content provider for a new sorted cursor
//            //should auto update bc of loader?
//            c = getContentResolver().query(TaskContentProvider.CONTENT_URI,
//                    null,
//                    null,
//                    null /* selection args need to test */,
//                    sortId);
//
//        } else {
//            // unsorted
//            c = getContentResolver().query(TaskContentProvider.CONTENT_URI, TaskContract.ALL_COLUMNS, null, null, null);
//        }
//        mAdapter.swapCursor(c);
//    }



    // playing around with cursor display and selection args
    // there is probably a more efficient way to do this, like a drop-down menu??
    public void onClickSelection1(View view) {

        // references private helper method onClickSelection
        int priority = 1;
        onClickSelection(view, priority);

    }


    private void onClickSelection(View view, int priority) {
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
        mSelectionArgs[0] = ""+priority;

        //actually do something with those selection args (query then update)
        Cursor c = getContentResolver().query(TaskContentProvider.CONTENT_URI,
                null,
                mSelectionClause,
                mSelectionArgs,
                TaskContract.ItemEntry.COLUMN_PRIORITY);

        mAdapter.swapCursor(c);

    }


    //NEW -- for re-qerying after an insert (after resuming this activity!!)
    @Override
    protected void onResume() {
        super.onResume();

        //actually do something with those selection args (query then update)
        Cursor c = getContentResolver().query(TaskContentProvider.CONTENT_URI,
                null,
                null,
                null,
                TaskContract.ItemEntry.COLUMN_PRIORITY);

        mAdapter.swapCursor(c);
    }




    //------ Loader code -----

    // Loaders will automatically update the view only if
    // notifications are set!
    /*
    Could show student an insert (with a Toast) and how a view changes/doesn't change
    based on including the cursor.setNotificationUri(getContext().getContentResolver(), uri);
     */
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//
//        //handle sorting
//
//        //loader sorts by priority
//        return new CursorLoader(getBaseContext(), TaskContentProvider.CONTENT_URI,
//                null, null, null, TaskContract.ItemEntry.COLUMN_PRIORITY);
//
//
//            /*
//            // previous; no sorting case
//            return new CursorLoader(getBaseContext(), TaskContentProvider.CONTENT_URI,
//                    null, null, null, null);
//                    */
//
//        //return null;
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        //adapter..
//        mAdapter.swapCursor(data);
//
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//
//        mAdapter.swapCursor(null);
//
//    }



    // NEW Loader code:

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {

            /* This String array will hold and help cache our weather data */
            Cursor mTaskData = null;

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    deliverResult(mTaskData);
                } else {
                    //mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from OpenWeatherMap in the background.
             *
             * @return Weather data from OpenWeatherMap as an array of Strings.
             * null if an error occurs
             */
            @Override
            public Cursor loadInBackground() {

                try {
                    Cursor retCursor = getContentResolver().query(TaskContract.ItemEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            TaskContract.ItemEntry.COLUMN_PRIORITY);

                    return retCursor;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };

    }

        //
        //


        /**
         * Called when a previously created loader has finished its load.
         *
         * @param loader The Loader that has finished.
         * @param data The data generated by the Loader.
         */
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            //mLoadingIndicator.setVisibility(View.INVISIBLE);
            mAdapter.swapCursor(data);
            if (null == data) {
                //showErrorMessage();
            } else {
                //showWeatherDataView();
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }

        /**
         * Called when a previously created loader is being reset, and thus
         * making its data unavailable.  The application should at this point
         * remove any references it has to the Loader's data.
         *
         * @param loader The Loader that is being reset.
         */
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        /*
         * We aren't using this method in our example application, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */

            mAdapter.swapCursor(null);
        }

}

