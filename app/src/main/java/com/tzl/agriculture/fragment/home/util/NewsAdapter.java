package com.tzl.agriculture.fragment.home.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.model.NewsMo;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseRecyclerHolder;

import java.util.List;

/**
 * 新闻类多类型适配
 * T 类型指定新闻公共类
 */
public abstract class NewsAdapter<T> extends RecyclerView.Adapter<BaseRecyclerHolder> {

    private Context mContext;
    private OnItemClickListener mItemClickListener;
    private List<T>mData;
    public final int mOnePType = 1;//一张图片
    public final int mTowPType = 2;//三张图片
    public final int mVideoType = 3;//视频

    public NewsAdapter(Context mContext,List<T> mDatas) {
        this.mContext = mContext;
        this.mData = mDatas;
    }

    public void updateData(List<T> data){
        this.mData = data;
        notifyDataSetChanged();
    }
    public void addData(List<T> data){
        this.mData.addAll(data);
        this.mData = data;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public BaseRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = null;
        if (viewType == mOnePType){
            layout = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_news_one, parent, false);
        }else if (viewType == mTowPType){
            layout = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_news_tow, parent, false);
        }else {
            layout = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_news_video, parent, false);
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
        NewsMo newsMo = (NewsMo)mData.get(position);
        if (newsMo.getType() == 1) {
            return mOnePType;
        }else if (newsMo.getType() == 2){
            return mTowPType;
        }else {
            return mVideoType;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public abstract void convert(Context mContext, BaseRecyclerHolder holder,int mType);

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

}
