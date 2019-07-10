package com.tzl.agriculture.fragment.xiangc.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.home.util.HomeAdapter;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseFragment;
import com.tzl.agriculture.view.BaseRecyclerHolder;
import com.tzl.agriculture.view.RecyclerViewItemDecoration;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class XiangcFragment extends BaseFragment {
    private RefreshLayout mRefreshLayout;
    private RecyclerView xRecycler;

    private BaseAdapter adapter;
    private Context mContext;

    public static XiangcFragment getInstance() {
        XiangcFragment homeFragment = new XiangcFragment();
        return homeFragment;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_xiangc;
    }

    @Override
    protected void initView(View view) {
        mContext = XiangcFragment.this.getContext();
        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        mRefreshLayout.setEnableHeaderTranslationContent(true);//内容偏移
        mRefreshLayout.setPrimaryColorsId(R.color.colorPrimaryDark, android.R.color.white);//主题颜色
        xRecycler = view.findViewById(R.id.home_recycler);
        xRecycler.setNestedScrollingEnabled(false);
        GridLayoutManager manager = new GridLayoutManager(mContext,2);
        xRecycler.setLayoutManager(manager);
        xRecycler.setItemAnimator(new DefaultItemAnimator());

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });

        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });


        List<String>mData = new ArrayList<>();
        for (int i=0;i<10;i++){
            mData.add("数据"+i);
        }

        adapter = new BaseAdapter<String>(mContext,xRecycler,mData,R.layout.item_xiangc) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, String o) {

            }
        };
        xRecycler.setAdapter(adapter);

    }

    @Override
    protected void initData(Context mContext) {

    }
}
