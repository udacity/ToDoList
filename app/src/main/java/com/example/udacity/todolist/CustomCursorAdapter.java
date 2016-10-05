package com.example.udacity.todolist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.udacity.todolist.data.TaskContract;

public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.ViewHolder> {

    private Cursor mCursor; // create swapCursor method with this
    private Context mContext;


    public CustomCursorAdapter(Context mContext) {
        this.mContext = mContext;
    }

    //here is where you use the context passed in in the ^constructor
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.task_layout, parent, false);

        return new ViewHolder(view);
        //return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // index of the ID column, title, author (0, 1, 2 -- autoincrement)
        int idIndex = mCursor.getColumnIndex(TaskContract.TaskEntry._ID);
        int titleIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION);
        int priorityIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_PRIORITY);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        //determine values
        int id = mCursor.getInt(idIndex);
        String title = mCursor.getString(titleIndex);
        int priority = mCursor.getInt(priorityIndex);

        //set values
        holder.itemView.setTag(id);
        holder.taskDescriptionView.setText(title);


        // Programmatically set text and color of priority marker
        String priorityString = "" + priority; // converts int to String
        holder.pMarker.setText(priorityString);

        GradientDrawable priorityCircle = (GradientDrawable) holder.pMarker.getBackground();

        // Get the appropriate background color based on the priority
        int priorityColor = getPriorityColor(priority);
        priorityCircle.setColor(priorityColor);

    }

    // Helper methods for selecting correct priority circle color
    private int getPriorityColor(int priority) {
        int priorityColor = 0;

        switch(priority) {
            case 1: priorityColor = ContextCompat.getColor(mContext, R.color.materialRed);
                break;
            case 2: priorityColor = ContextCompat.getColor(mContext, R.color.materialYellow);
                break;
            case 3: priorityColor = ContextCompat.getColor(mContext, R.color.materialGreen);
                break;
            default: break;
        }
        return priorityColor;
    }


    @Override
    public int getItemCount() {

        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    // helper method for changeCursor -- when there is no loader - to close the cursor!
    /* In the non-loader case, this should..
    1) be private
    2)return the Cursor temp
    3) changeCursor should be available
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged(); // notify bc of change!
        }
        return temp;
    }

    //The real swapCursor - swaps AND closes the cursor
    // uses private swap cursor and then closes the cursor, is this necessary??

    //change and close
    /*
    public void changeCursor(Cursor c) {
        //swap!
        Cursor temp = swapCursor(c);
        if (temp != null) {
            temp.close(); // then close the old cursor if it exists (unnecessary clean up?)
        }
    }
    */

    // inner class for view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView taskDescriptionView;

        // priority drawable marker
        public TextView pMarker;


        public ViewHolder(View itemView) {
            super(itemView);
            // create constructor and initialize views
            taskDescriptionView = (TextView) itemView.findViewById(R.id.taskDescription);
            pMarker = (TextView) itemView.findViewById(R.id.priorityMarker);


        }

    }


}