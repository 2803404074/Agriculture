package com.tzl.agriculture.fragment.personal.activity.ccb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.ccb.cartinter.OnClickAddCloseListenter;
import com.tzl.agriculture.fragment.personal.activity.ccb.cartinter.OnClickListenterModel;
import com.tzl.agriculture.fragment.personal.activity.ccb.cartinter.OnItemMoneyClickListener;
import com.tzl.agriculture.fragment.personal.activity.ccb.cartinter.OnViewItemClickListener;
import com.tzl.agriculture.fragment.personal.activity.ccb.entity.CartInfo;
import com.tzl.agriculture.fragment.personal.activity.ccb.widget.LinearLayoutForListView;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<CartInfo.DataBean> list=new ArrayList<>();
    public ListBaseAdapter listBaseAdapter;
    public boolean isCheck = false;

    public CartAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.cart_dep, parent, false));
    }


    public void updateData(List<CartInfo.DataBean> e1){
        list.clear();
        if(e1!=null&&e1.size()!=0){
            list.addAll(e1);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvChild.setText(list.get(position).getShop_name());
        holder.cbChild.setChecked(list.get(position).ischeck());
        listBaseAdapter = new ListBaseAdapter(context, position, list.get(position).getItems());
        holder.listView.setAdapter(listBaseAdapter);
        holder.cbChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(holder.cbChild.isChecked(), v, position);
            }
        });

        //店铺下的checkbox
        listBaseAdapter.setOnClickListenterModel(new OnClickListenterModel() {
            @Override
            public void onItemClick(boolean isFlang, View view, int onePosition, int position) {
                list.get(onePosition).getItems().get(position).setIscheck(isFlang);
                int length = list.get(onePosition).getItems().size();
                if (length == 1) {
                    mOnItemClickListener.onItemClick(isFlang, view, onePosition);
                } else {
                    for (int i = 0; i < length; i++) {
                        if (list.get(onePosition).getItems().get(i).ischeck()) {//true,true,true
                            isCheck = true;
                        } else {
                            isCheck = false;
                            break;
                        }
                    }
                    list.get(onePosition).setIscheck(isCheck);
                    onItemMoneyClickListener.onItemClick(view,3 ,onePosition,position);
                    notifyDataSetChanged();
                }
            }
        });


        /***
         * 数量增加和减少
         */
        listBaseAdapter.setOnClickAddCloseListenter(new OnClickAddCloseListenter() {
            @Override
            public void onItemClick(View view, int index, int onePosition, int position, int num) {
//                if (index==1){
//                    if (num>1) {
//                        list.get(onePosition).getItems().get(position).setNum((num - 1));
//                        notifyDataSetChanged();
//                    }
//                }else {
//                    list.get(onePosition).getItems().get(position).setNum((num + 1));
//                    notifyDataSetChanged();
//                }
                onItemMoneyClickListener.onItemClick(view,index, onePosition,position);
            }
        });

    }

    // CheckBox全选的方法
    private OnViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    // 计算价钱
    private OnItemMoneyClickListener onItemMoneyClickListener = null;

    public void setOnItemMoneyClickListener(OnItemMoneyClickListener listener) {
        this.onItemMoneyClickListener = listener;
    }




    /**
     * 删除选中item
     */
    public void removeChecked() {
        int iMax = list.size() - 1;
        //这里要倒序，因为要删除mDatas中的数据，mDatas的长度会变化
        for (int i = iMax; i >= 0; i--) {
            if (list.get(i).ischeck()) {
                list.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, list.size());
            } else {
                int length = list.get(i).getItems().size() - 1;
                for (int j = length; j >= 0; j--) {
                    if (list.get(i).getItems().get(j).ischeck()) {
                        list.get(i).getItems().remove(j);
                        notifyItemRemoved(i);
                        notifyItemRangeChanged(i, list.size());
                    }
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvChild;
        public CheckBox cbChild;
        public LinearLayoutForListView listView;

        public ViewHolder(View view) {
            super(view);
            tvChild = (TextView) view.findViewById(R.id.tv_group);
            cbChild = (CheckBox) view.findViewById(R.id.cb_group);
            listView = view.findViewById(R.id.listview_cart);

        }
    }

}
