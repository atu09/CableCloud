package com.mtaj.mtaj_08.cableplus_new;

import android.view.View;

import java.util.HashMap;

/**
 * Created by MTAJ-08 on 11/28/2016.
 */
public class ViewHolder
{
    private HashMap<Integer, View> storedViews = new HashMap<Integer, View>();

    public ViewHolder()
    {
    }

    /**
     *
     * @param view
     *            The view to add; to reference this view later, simply refer to its id.
     * @return This instance to allow for chaining.
     */
    public ViewHolder addView(View view)
    {
        int id = view.getId();
        storedViews.put(id, view);
        return this;
    }

    public View getView(int id)
    {
        return storedViews.get(id);
    }
}
