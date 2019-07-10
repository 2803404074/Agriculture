package com.tzl.agriculture.fragment.home.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.home.activity.GoodsDetailsActivity;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseFragmentFromType;
import com.tzl.agriculture.view.BaseRecyclerHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import config.DyUrl;
import okhttp3.Call;

/**
 *
 */
public class GoodsFragmentPage extends BaseFragmentFromType {


    private RecyclerView recyclerView;

    private BaseAdapter adapter;
    private List<String> mData = new ArrayList<>();

    @Override
    protected int initLayout() {
        return R.layout.recy_demo;
    }

    @Override
    protected void initView(View view) {
        for (int i=0;i<20;i++){
            mData.add("数据"+i);
        }
        recyclerView = view.findViewById(R.id.recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BaseAdapter<String>(getContext(),recyclerView,mData,R.layout.item_limited_time) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, String o) {

            }
        };
        View headView = getLayoutInflater().inflate(R.layout.limited_time_head, null);
        headView.setLeftTopRightBottom(R.dimen.dp_0,10,10,10);
        adapter.addHeadView(headView);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String a = (String) adapter.getData().get(position);
                Intent intent = new Intent(getContext(), GoodsDetailsActivity.class);
                intent.putExtra("goodsId",a);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void setDate(boolean isLoad) {



        ToastUtil.showShort(GoodsFragmentPage.this.getContext(),"加载了"+getCtype());
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost("", null, new GsonObjectCallback<String>(DyUrl.BASE) {

            @Override
            public void onUi(String result) {

            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }
}
