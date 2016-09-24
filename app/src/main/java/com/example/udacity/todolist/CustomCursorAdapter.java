package com.example.udacity.todolist;

import android.content.Context;
import android.database.Cursor;
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
        int idIndex = mCursor.getColumnIndex(TaskContract.ItemEntry._ID);
        int titleIndex = mCursor.getColumnIndex(TaskContract.ItemEntry.COLUMN_TASK_NAME);
        int priorityIndex = mCursor.getColumnIndex(TaskContract.ItemEntry.COLUMN_PRIORITY);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        //determine values
        int id = mCursor.getInt(idIndex);
        String title = mCursor.getString(titleIndex);
        int priority = mCursor.getInt(priorityIndex);

        //set values
        holder.itemView.setTag(id);
        holder.nameView.setText(title);

        //handle visibility and priority markers
        /*
        if (priority == 1 || priority == 2 || priority == 3) {
            holder.priorityView.setVisibility(View.VISIBLE);
            holder.priorityView.setText(mContext.getString(R.string.boxed_priority, priority));
        } else {
            holder.priorityView.setVisibility(View.GONE);
        }
        */

        // takes in context and ~instanceNum~ which in this case will be the priority

        /*
        int backgroundColorForViewHolder = ColorUtils
                .getViewHolderBackgroundColorFromInstance(mContext, priority);

        holder.pMarker.setBackgroundColor(backgroundColorForViewHolder);
        */

        holder.pMarker.setText(""+priority);


        switch(priority) {
            case 1: holder.pMarker.setBackgroundResource(R.drawable.circle1);
                break;
            case 2: holder.pMarker.setBackgroundResource(R.drawable.circle2);
                break;
            case 3: holder.pMarker.setBackgroundResource(R.drawable.circle3);
                break;
            default: holder.pMarker.setVisibility(View.INVISIBLE);
        }


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

        public TextView nameView;

        // priority drawable marker
        public TextView pMarker;

        //public TextView priorityView;

        public ViewHolder(View itemView) {
            super(itemView);
            // create constructor and initialize views
            nameView = (TextView) itemView.findViewById(R.id.taskName);
            pMarker = (TextView) itemView.findViewById(R.id.priorityMarker);
            //priorityView = (TextView) itemView.findViewById(R.id.androidPriorityText);


        }

    }


}