package com.airplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/9.
 */
public abstract class AirAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mLayoutInflater;

    private Context mContext;

    private List<T> mList;

    public AirAdapter(Context context, List<T> list) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mList = list;
    }

    public LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    public Context getContext() {
        return mContext;
    }

    public List<T> getList() {
        return mList;
    }

    @Override
    public abstract void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i);

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }
}
