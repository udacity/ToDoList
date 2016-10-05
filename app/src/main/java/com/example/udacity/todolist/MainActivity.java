package com.example.udacity.todolist;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.example.udacity.todolist.data.TaskContentProvider;
import com.example.udacity.todolist.data.TaskContract;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //need to create custom adapter class first
    private CustomCursorAdapter mAdapter;

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


        //TODO: Implement swipe delete
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
                Uri uri = TaskContract.TaskEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath("" + myId).build();

                //view uri for debug
                Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();

                getContentResolver().delete(uri, null, null);

                Cursor c = getContentResolver().query(TaskContentProvider.CONTENT_URI,
                        null,
                        null,
                        null,
                        TaskContract.TaskEntry.COLUMN_PRIORITY);

                mAdapter.swapCursor(c);
            }
        }).attachToRecyclerView(mRecyclerView);



        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);

        /*
        FAB onClick listener that launches a new AddTaskActivity
         */
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


    public void selectAll(View view) {

        // re-queries for all tasks
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);

    }


    //TODO: Select tasks by priority
    public void selectPriority1(View view) {

        // references private helper method selectByPriority
        int priority = 1;
        selectByPriority(priority);

    }


    private void selectByPriority(int priority) {
        // create 1) selection clause and 2) corresponding args
        // this lets you choose rows based on a selection criteria

        // Defines a string to contain the selection clause
        String mSelectionClause = TaskContract.TaskEntry.COLUMN_PRIORITY + " = ?";

        // Initializes an array to contain selection arguments
        // defines a one element String array to contain the selection argument
        String priorityString = "" + priority;
        String[] mSelectionArgs = {priorityString};

        //Use these selection args in a query to update the display
        Cursor c = getContentResolver().query(TaskContentProvider.CONTENT_URI,
                null,
                mSelectionClause,
                mSelectionArgs,
                TaskContract.TaskEntry.COLUMN_PRIORITY);

        mAdapter.swapCursor(c);

    }


    // Re-query after an insert, where this activity is always resumed
    @Override
    protected void onResume() {
        super.onResume();

        // re-queries for all tasks
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {

            // This Cursor holds all our task data
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
             * TODO: Query and load all task data in the background
             * TODO: Sort data by priority
             *
             */
            @Override
            public Cursor loadInBackground() {

                try {
                    return getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            TaskContract.TaskEntry.COLUMN_PRIORITY);

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
            mAdapter.swapCursor(null);
        }

}

