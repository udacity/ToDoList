package com.example.udacity.todolist;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.udacity.todolist.data.TaskContract;


public class AddTaskActivity extends AppCompatActivity {

    // Declare a class variable to keep track of a task's selected priority
    private int priority;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        //initialize to default priority (priority = 1)
        ((RadioButton) findViewById(R.id.radButton1)).setChecked(true);
        priority = 1;

    }


    // TODO: Retrieve user input and insert new task data
    public void onClickAddTask(View view) {

        // Create new empty ContentValues
        ContentValues contentValues = new ContentValues();

        // Check if the EditText input is empty - (you won't create an entry if there is no input)
        String input = ((EditText) findViewById(R.id.editTextTaskDescription)).getText().toString();
        if (input.length() == 0) {
            return;
        }

        // Put the task description input into the ContentValues
        contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, input);

        // Put the selected task priority into the ContentValues
        contentValues.put(TaskContract.TaskEntry.COLUMN_PRIORITY, priority);

        // Insert values through a content resolver
        Uri uri = getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues);

        // Use a Toast to show the uri that the inserted entry is in
        if(uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }

        // Finish activity (this returns back to MainActivity)
        finish();
    }


    // Changes priority based on the selected button
    public void onPrioritySelected(View view) {
        if (((RadioButton) findViewById(R.id.radButton1)).isChecked()) {
            priority = 1;
        } else if (((RadioButton) findViewById(R.id.radButton2)).isChecked()) {
            priority = 2;
        } else if (((RadioButton) findViewById(R.id.radButton3)).isChecked()) {
            priority = 3;
        }
    }

}
