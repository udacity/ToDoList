/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.udacity.todolist;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

/**
 * ColorUtils is a class with one method, used to color the ViewHolders in
 * the RecyclerView. I put in a separate class in an attempt to keep the
 * code organized.
 * <p>
 * We aren't going to go into detail about how this method works, but feel
 * free to explore!
 */
public class ColorUtils {

    /**
     * This method returns the appropriate shade of green to form the gradient
     * seen in the list, based off of the order in which the
     * {com.example.android.recyclerview.GreenAdapter.NumberViewHolder}
     * instance was created.
     * <p>
     * This method is used to show how ViewHolders are recycled in a RecyclerView.
     * At first, the colors will form a nice, consistent gradient. As the
     * RecyclerView is scrolled, the
     * { com.example.android.recyclerview.GreenAdapter.NumberViewHolder}'s will be
     * recycled and the list will no longer appear as a consistent gradient.
     *
     * @param context  Context for getting colors
     * @param priority Priority selected for the task in the list
     * @return A shade of green based off of when the calling ViewHolder
     * was created.
     */
    // handles priorities 1 - 3 (lightest = P1)
    public static int getViewHolderBackgroundColorFromInstance(Context context, int priority) {
        switch (priority) {
            case 1:
                return ContextCompat.getColor(context, R.color.material100Green);
            case 2:
                return ContextCompat.getColor(context, R.color.material350Green);
            case 3:
                return ContextCompat.getColor(context, R.color.material600Green);
            case 4:
                return ContextCompat.getColor(context, R.color.material850Green);
            default:
                return Color.WHITE;
        }
    }
}
