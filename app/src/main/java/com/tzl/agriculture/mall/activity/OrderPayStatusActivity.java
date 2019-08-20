package com.tzl.agriculture.mall.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseRecyclerHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 订单支付状态页面
 */
public class OrderPayStatusActivity extends SetBaseActivity implements View.OnClickListener {

    @BindView(R.id.recy_status)
    RecyclerView recyclerView;

    @BindView(R.id.tv_price)
    TextView tvPrice;

    @BindView(R.id.tv_seeOrder)
    TextView tvSeeOrder;

    @BindView(R.id.tv_tips)
    TextView tvTips;
    private BaseAdapter adapter;

    private List<String> mData = new ArrayList<>();

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_order_pay_status;
    }

    @Override
    public void initView() {

        if (-1 == getIntent().getIntExtra("status",0)){
            setTitle("未支付");
            tvTips.setText("订单金额");
        }else {
            setTitle("交易成功");
        }
        tvPrice.setText(getIntent().getStringExtra("price"));
        for (int i=0;i<10;i++){
            mData.add("数据"+i);
        }
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new BaseAdapter<String>(this,recyclerView,mData,R.layout.img_recommend_goods) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position,String o) {

            }
        };
        recyclerView.setAdapter(adapter);

        tvSeeOrder.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_seeOrder:
                Intent intent = new Intent(this, OrderDetailsActivity.class);
                intent.putExtra("orderId",getIntent().getStringExtra("orderId"));
                startActivity(intent);
                finish();
                break;
                default:break;
        }
    }
}
