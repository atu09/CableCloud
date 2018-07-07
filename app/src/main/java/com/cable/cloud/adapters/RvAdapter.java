package com.cable.cloud.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by Admin on 12/6/2016.
 */

public class RvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface AdapterListener {
        RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

        void onBindViewHolder(RecyclerView.ViewHolder holder, int position);

        int getItemCount();

        int getItemViewType(int position);

        long getItemId(int position);
    }

    AdapterListener listener;

    public RvAdapter(AdapterListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return listener.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        listener.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return listener.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return listener.getItemViewType(position);
    }


    @Override
    public long getItemId(int position) {
        return listener.getItemId(position);
    }



}
