package com.tzl.agriculture.fragment.vip.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.TimePickerView;
import com.rey.material.app.BottomSheetDialog;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
import com.tzl.agriculture.fragment.vip.model.MingxMo;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseRecyclerHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class MoneyDetailedActivity extends SetBaseActivity implements View.OnClickListener {


    @BindView(R.id.iv_back)
    ImageView imgBack;

    @BindView(R.id.tv_date)
    TextView tvDate;

    //收入
    @BindView(R.id.tv_incomeNum)
    TextView tvIncomeNum;

    //支出
    @BindView(R.id.tv_payNum)
    TextView tvPayNum;

    //选择月份
    @BindView(R.id.iv_selectMonth)
    ImageView ivSelectMonth;

    @BindView(R.id.mx_recy)
    RecyclerView recyclerView;

    private BottomSheetDialog dialog;

    private BaseAdapter adapter;

    private List<MingxMo> mData = new ArrayList<>();

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_money_detailed;
    }

    @Override
    public void initView() {
        setTitle("明细");
        imgBack.setImageResource(R.drawable.smssdk_ic_popup_dialog_close);
        ivSelectMonth.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseAdapter<MingxMo>(this,recyclerView,mData,R.layout.item_mingx) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, MingxMo o) {

            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new BaseAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
                initData();
            }
        });
    }

    private int page = 1;

    @Override
    public void initData() {
        Map<String,String>map = new HashMap<>();
        map.put("","");
        map.put("","");
        map.put("","");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_selectMonth:
                showSelectDateView();
                break;
                default:break;
        }
    }

    private void showSelectDateView() {
        dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_add_cart, null);

        dialog.contentView(view)/*加载视图*/
                /*.heightParam(height/2),显示的高度*/
                /*动画设置*/
                .inDuration(200)
                .outDuration(200)
                /*.inInterpolator(new BounceInterpolator())
                .outInterpolator(new AnticipateInterpolator())*/
                .cancelable(true)
                .show();
    }
}
