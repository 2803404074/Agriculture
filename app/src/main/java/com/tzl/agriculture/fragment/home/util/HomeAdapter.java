package com.tzl.agriculture.fragment.home.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.view.BaseRecyclerHolder;


public abstract class HomeAdapter extends RecyclerView.Adapter<BaseRecyclerHolder> {

    private Context mContext;

    public final int mTypeOne = 0;
    public final int mTypeTow = 1;


    public HomeAdapter(Context mContext) {
        this.mContext = mContext;
    }
    @NonNull
    @Override
    public BaseRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = null;
        if (viewType == mTypeOne){
            layout = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_home_one, parent, false);
        }else if (viewType == mTypeTow){
            layout = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_home_tow, parent, false);
        }
        return BaseRecyclerHolder.getRecyclerHolder(mContext, layout);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerHolder holder, int position) {
        if (null == holder.itemView.getTag()){
            holder.itemView.setTag(position);
            convert(mContext, holder,position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mTypeOne) {
            return mTypeOne;
        }else {
            return mTypeTow;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public abstract void convert(Context mContext, BaseRecyclerHolder holder,int mType);
}
