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

    public int getViewTypeForMyTask(int position){
        return mData.get(position).getPosition();
    }

    @NonNull
    @Override
    public BaseRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = 0;
        if (viewType == mTypeZero){
            layout = R.layout.new_item_home_zero;
        }else if (viewType == mTypeOne){
            layout = R.layout.new_item_home_one;
        }else if (viewType == mTypeTow){
            layout = R.layout.new_item_home_tow;
        }else if (viewType == mTypeThree){
            layout = R.layout.new_item_home_three;
        }else if (viewType == mTypeFour){
            layout = R.layout.new_item_home_four;
        }else if (viewType == mTypeFive){
            layout = R.layout.new_item_home_five;
        }else if (viewType == mTypeSix){
            layout = R.layout.new_item_home_six;
        }else if (viewType == mTypeSeven){
            layout = R.layout.new_item_home_seven;
        }

        if (layout == 0)return null;

        View view = LayoutInflater.from(parent.getContext()).
                inflate(layout, parent, false);

        return BaseRecyclerHolder.getRecyclerHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerHolder holder, int position) {
        if (holder == null)return;
        if (null == holder.itemView.getTag()){
            holder.itemView.setTag(position);
            convert(mContext, holder,position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        HomeMo homeMo = mData.get(position);
        return homeMo.getPosition();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public abstract void convert(Context mContext, BaseRecyclerHolder holder,int mType);
}
