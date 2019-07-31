package com.tzl.agriculture.fragment.home.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.model.HomeMo;
import com.tzl.agriculture.model.XiangcMo;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseRecyclerHolder;

import java.util.List;


public abstract class HomeAdapter extends RecyclerView.Adapter<BaseRecyclerHolder> {

    private Context mContext;
    private List<HomeMo> mData;

    public final int mTypeZero = 0;
    public final int mTypeOne = 1;
    public final int mTypeTow = 2;
    public final int mTypeThree = 3;
    public final int mTypeFour = 4;
    public final int mTypeFive = 5;
    public final int mTypeSix = 6;
    public final int mTypeSeven = 7;
    public HomeAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setmData(List<HomeMo> list) {
        this.mData = list;
        notifyDataSetChanged();
    }
    public void update(List<HomeMo> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = null;
        if (viewType == mTypeZero){
            layout = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.new_item_home_zero, parent, false);
        }else if (viewType == mTypeOne){
            layout = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.new_item_home_one, parent, false);
        }else if (viewType == mTypeTow){
            layout = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.new_item_home_tow, parent, false);
        }else if (viewType == mTypeThree){
            layout = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.new_item_home_three, parent, false);
        }else if (viewType == mTypeFour){
            layout = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.new_item_home_four, parent, false);
        }else if (viewType == mTypeFive){
            layout = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.new_item_home_five, parent, false);
        }else if (viewType == mTypeSix){
            layout = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.new_item_home_six, parent, false);
        }else if (viewType == mTypeSeven){
            layout = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.new_item_home_seven, parent, false);
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
        HomeMo homeMo = mData.get(position);
        if (0 == homeMo.getPosition()) {
            return mTypeZero;
        }else if (1 == homeMo.getPosition()){
            return mTypeOne;
        }else if (2 == homeMo.getPosition()){
            return mTypeTow;
        }else if (3 == homeMo.getPosition()){
            return mTypeThree;
        }else if (4 == homeMo.getPosition()){
            return mTypeFour;
        }else if (5 == homeMo.getPosition()){
            return mTypeFive;
        }else if (6 == homeMo.getPosition()){
            return mTypeSix;
        }else {//if (7 == homeMo.getPosition())
            return mTypeSeven;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public abstract void convert(Context mContext, BaseRecyclerHolder holder,int mType);
}
