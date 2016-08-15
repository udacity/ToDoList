package com.example.cezannec.todolist;

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

import com.example.cezannec.todolist.data.TaskContentProvider;
import com.example.cezannec.todolist.data.TaskContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Recycler view vars
    private RecyclerView recView;
    //need to create custom adapter class first
    private CustomCursorAdapter rAdapter;


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

                //update cursor
                Cursor c = getContentResolver().query(TaskContentProvider.CONTENT_URI, TaskContract.ALL_COLUMNS, null, null, null);
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

        contentValues.put(TaskContract.ItemEntry.COLUMN_TASKTITLE,
                ((EditText) findViewById(R.id.editTextTaskTitle)).getText().toString());

        int checked = 0; // init to 0 -- 3 to mark priority of tasks
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

