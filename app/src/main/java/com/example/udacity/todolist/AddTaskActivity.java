package com.example.udacity.todolist;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.udacity.todolist.data.TaskContentProvider;
import com.example.udacity.todolist.data.TaskContract;

/**
 * Created by cezannec on 9/8/16.
 */

public class AddTaskActivity extends AppCompatActivity {

    private int priority;

    //private boolean isSorted = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        //initialize P1
        ((RadioButton) findViewById(R.id.firstButton)).setChecked(true);
        priority = 1;

    }


    public void onClickAddTask(View view) {
        ContentValues contentValues = new ContentValues();

        // check if the input is empty (won't create an entry in this case)
        String input = ((EditText) findViewById(R.id.editTextTaskName)).getText().toString();
        if (input.length() == 0) {
            return;
        }

        contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION,
                input);


        contentValues.put(TaskContract.TaskEntry.COLUMN_PRIORITY, priority);

        // insert values through content resolver
        Uri uri = getContentResolver().insert(TaskContentProvider.CONTENT_URI, contentValues);

        // show the uri that the inserted entry is in
        Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();

        finish();

    }

    public void onPrioritySelected(View view) {

        if (((RadioButton) findViewById(R.id.firstButton)).isChecked()) {
            priority = 1;
        } else if (((RadioButton) findViewById(R.id.secondButton)).isChecked()) {
            priority = 2;
        } else if (((RadioButton) findViewById(R.id.thirdButton)).isChecked()) {
            priority = 3;
        }

    }

}
