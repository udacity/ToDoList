package com.example.cezannec.todolist;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cezannec.todolist.data.TaskContract;

/**
 * Created by cezannec on 8/13/16.
 */


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
        int idIndex = mCursor.getColumnIndex(TaskContract.ItemEntry._ID);
        int titleIndex = mCursor.getColumnIndex(TaskContract.ItemEntry.COLUMN_TASKTITLE);
        int priorityIndex = mCursor.getColumnIndex(TaskContract.ItemEntry.COLUMN_PRIORITY);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        //determine values
        int id = mCursor.getInt(idIndex);
        String title = mCursor.getString(titleIndex);
        int priority = mCursor.getInt(priorityIndex);

        //set values
        holder.itemView.setTag(id);
        holder.titleView.setText(title);

        //handle visibility and priority markers
        if(priority == 1){
            //p1
            holder.priorityView.setVisibility(View.VISIBLE);
            holder.priorityView.setText("!!");
            holder.priorityView.setTextColor(0xffff0000); // red
        } else if(priority == 2) {
            //p2
            holder.priorityView.setVisibility(View.VISIBLE);
            holder.priorityView.setText("!!");
            holder.priorityView.setTextColor(0xffffff00); //yellow
        } else if(priority == 3) {
        //p3
            holder.priorityView.setVisibility(View.VISIBLE);
            holder.priorityView.setText("!!");
            holder.priorityView.setTextColor(0xff00ff00); //green
        } else {
            //priority == 0, none was selected
            holder.priorityView.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {

        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    // helper method for swapCursor
    private Cursor privateSwapCursor(Cursor c) {
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
    public void swapCursor(Cursor c) {
        //swap!
        Cursor temp = privateSwapCursor(c);
        if (temp != null) {
            temp.close(); // then close the old cursor if it exists (unnecessary clean up?)
        }
    }

    // inner class for view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleView;
        public TextView priorityView;

        public ViewHolder(View itemView) {
            super(itemView);
            // create constructor and initialize views
            titleView = (TextView) itemView.findViewById(R.id.taskTitle);
            priorityView = (TextView) itemView.findViewById(R.id.androidPriorityText);
        }

    }


}