package com.tzl.agriculture.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseRecyclerHolder> {
    private Context mContext;
    private boolean isLoading = false;
    private OnLoadMoreListener mOnLoadMoreListener;
    private OnItemClickListener mItemClickListener;
    private onLongItemClickListener mLongItemClickListener;

    private List<T> mDatas;

    private int mLayoutId;

    private View mHeadView;
    private View mFooterView;
    public static final int TYPE_HEADER = 0;  //说明是带有Header的
    public static final int TYPE_FOOTER = 1;  //说明是带有Footer的
    public static final int TYPE_NORMAL = 2;  //说明是不带有header和footer的

    public BaseAdapter(Context mContext, RecyclerView recyclerView, List<T> mDatas, int mLayoutId) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        this.mLayoutId = mLayoutId;
    }

    public void updateData(List<T> data) {
        mDatas.clear();
        mDatas.addAll(data);
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return mDatas;
    }

    public void addAll(List<T> data) {

        mDatas.addAll(data);
        notifyDataSetChanged();
    }
    public void addAll2(T data) {
        mDatas.add(0, data);
        notifyDataSetChanged();
    }

    public void addData(T data) {
        mDatas.add( data);
        notifyDataSetChanged();
    }

    public void setData(T data,int index) {
        mDatas.set(index, data);
        notifyDataSetChanged();
    }

    public void addAll3(T data, int index) {
        mDatas.add(index, data);
        notifyDataSetChanged();
    }

    public void addHeadView(View headView) {
        mHeadView = headView;
    }

    public void updateFooterView(View newFooterView) {
        mFooterView = newFooterView;
        notifyDataSetChanged();
    }

    public void addFooterView(View footerView) {
        mFooterView = footerView;
    }

    @Override
    public BaseRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (mHeadView != null && viewType == TYPE_HEADER) {
            return BaseRecyclerHolder.getRecyclerHolder(mContext, mHeadView);
        }
        if (mFooterView != null && viewType == TYPE_FOOTER) {
            return BaseRecyclerHolder.getRecyclerHolder(mContext, mFooterView);
        }
        View layout = LayoutInflater.from(parent.getContext()).
                inflate(mLayoutId, parent, false);
        return BaseRecyclerHolder.getRecyclerHolder(mContext, layout);
    }

    @Override
    public void onBindViewHolder(BaseRecyclerHolder holder, final int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            //这里加载数据的时候要注意，是从position-1开始，因为position==0已经被header占用了
            if (mHeadView != null) {
                convert(mContext, holder,position-1, mDatas.get(position - 1));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mItemClickListener != null) {
                            mItemClickListener.onItemClick(v, position - 1);
                        }
                    }
                });


                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (mLongItemClickListener != null){
                            mLongItemClickListener.onLongItemClick(view, position - 1);
                        }
                        return false;
                    }
                });
            } else {
                convert(mContext, holder,position, mDatas.get(position));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mItemClickListener != null) {
                            mItemClickListener.onItemClick(v, position);
                        }

                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (mLongItemClickListener != null){
                            mLongItemClickListener.onLongItemClick(view, position);
                        }
                        return false;
                    }
                });

            }

            return;
        } else if (getItemViewType(position) == TYPE_HEADER) {
            return;
        } else {
            return;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeadView == null && mFooterView == null) {
            return TYPE_NORMAL;
        }
        if (position == 0) {
            //第一个item应该加载Header
            return TYPE_HEADER;
        }
        if (position == getItemCount() - 1) {
            //最后一个,应该加载Footer
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public abstract void convert(Context mContext, BaseRecyclerHolder holder,int position, T t);

    @Override
    public int getItemCount() {
        if (mHeadView == null && mFooterView == null) {
            return mDatas.size();
        } else if (mHeadView == null && mFooterView != null) {
            return mDatas.size() + 1;
        } else if (mHeadView != null && mFooterView == null) {
            return mDatas.size() + 1;
        } else {
            return mDatas.size() + 2;
        }
    }

    public void setLoading(boolean b) {
        isLoading = b;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface onLongItemClickListener {
        void onLongItemClick(View view, int postion);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setonLongItemClickListener(onLongItemClickListener listener) {
        this.mLongItemClickListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mOnLoadMoreListener = listener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
