package com.tzl.agriculture.fragment.personal.activity.ccb.fragment;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseFragment;
import com.tzl.agriculture.view.BaseRecyclerHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 商品收藏fragment
 */
public class CollectionDepFragment extends BaseFragment {


    @BindView(R.id.recy)
    RecyclerView recyclerView;

    private BaseAdapter adapter;

    private List<String>mData = new ArrayList<>();
    @Override
    protected int initLayout() {
        return R.layout.recy_demo;
    }

    @Override
    protected void initView(View view) {
//        for (int i=0;i<10;i++){
//            mData.add("数据"+i);
//        }
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        adapter = new BaseAdapter<String>(getContext(),recyclerView,mData,R.layout.item_limited_time) {
//            @Override
//            public void convert(Context mContext, BaseRecyclerHolder holder, String o) {
//                holder.getView(R.id.tv_buy).setVisibility(View.GONE);
//            }
//        };
//        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {

    }
}
