package com.tzl.agriculture.fragment.home.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.home.util.NewsAdapter;
import com.tzl.agriculture.model.NewsMo;
import com.tzl.agriculture.view.BaseRecyclerHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 播报页面
 */
public class BroadcastActivity extends AppCompatActivity {

    @BindView(R.id.recy)
    RecyclerView recyclerView;

    private NewsAdapter adapter;
    private List<NewsMo> mData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        //模拟数据
        initData();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        adapter = new NewsAdapter<NewsMo>(this,mData) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int mType) {
                if (mType == adapter.mOnePType){

                }else if (mType == adapter.mTowPType){

                }else {

                }
            }
        };

        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        NewsMo newsMo = new NewsMo();
        newsMo.setType(1);
        mData.add(newsMo);

        NewsMo newsMo1 = new NewsMo();
        newsMo1.setType(3);
        mData.add(newsMo1);

        NewsMo newsMo2 = new NewsMo();
        newsMo2.setType(2);
        mData.add(newsMo2);

        NewsMo newsMo3 = new NewsMo();
        newsMo3.setType(3);
        mData.add(newsMo3);

        NewsMo newsMo4 = new NewsMo();
        newsMo4.setType(2);
        mData.add(newsMo4);

        NewsMo newsMo5 = new NewsMo();
        newsMo5.setType(1);
        mData.add(newsMo5);

        NewsMo newsMo6 = new NewsMo();
        newsMo6.setType(2);
        mData.add(newsMo6);

        NewsMo newsMo7 = new NewsMo();
        newsMo7.setType(3);
        mData.add(newsMo7);

        NewsMo newsMo8 = new NewsMo();
        newsMo8.setType(1);
        mData.add(newsMo8);

        NewsMo newsMo9 = new NewsMo();
        newsMo9.setType(2);
        mData.add(newsMo9);

        NewsMo newsMo10 = new NewsMo();
        newsMo10.setType(3);
        mData.add(newsMo10);
    }
}
