package com.tzl.agriculture.fragment.home.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.model.XiangcMo;
import com.tzl.agriculture.view.BaseRecyclerHolder;

import java.util.List;

/**
 * 播报页多类型适配
 * T 类型指定新闻公共类
 */
public abstract class NewsAdapter<T> extends RecyclerView.Adapter<BaseRecyclerHolder> {

    private Context mContext;
    private OnItemClickListener mItemClickListener;
    private List<T>mData;
    public final int mOnePType = 1;//一张图片
    public final int mTowPType = 2;//视频
    public final int mThreeType = 3;//三张图片

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
                    inflate(R.layout.item_news_video, parent, false);
        }else {
            layout = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_news_tow, parent, false);
        }
        return BaseRecyclerHolder.getRecyclerHolder(mContext, layout);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerHolder holder, int position) {
        if (null == holder.itemView.getTag()){
            holder.itemView.setTag(position);
            XiangcMo.Article article = (XiangcMo.Article) mData.get(position);
            convert(mContext, holder,position,mData.get(position),article.getCoverImgurlSize());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener!=null){
                        mItemClickListener.onItemClick(v, position);
                    }

                }
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        XiangcMo.Article newsMo = (XiangcMo.Article)mData.get(position);
        if (newsMo.getCoverImgurlSize() == 1) {
            return mOnePType;
        }else if (newsMo.getCoverImgurlSize() == 2){
            return mTowPType;
        }else {
            return mThreeType;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public abstract void convert(Context mContext, BaseRecyclerHolder holder,int mType,T t,int type);

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

}
