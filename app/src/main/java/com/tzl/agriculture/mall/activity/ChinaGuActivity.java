package com.tzl.agriculture.mall.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.order.OrderSearchActivity;
import com.tzl.agriculture.model.GoodsDetailsMo;
import com.tzl.agriculture.model.GoodsMo;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.view.BaseActivity;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseRecyclerHolder;
import com.tzl.agriculture.view.onLoadMoreListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import config.Mall;
import okhttp3.Call;

public class ChinaGuActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.sp1)
    Spinner spinner1;
    @BindView(R.id.sp2)
    Spinner spinner2;
    @BindView(R.id.sp3)
    Spinner spinner3;
    @BindView(R.id.sp4)
    Spinner spinner4;

    @BindView(R.id.back)
    ImageView ivBack;

    @BindView(R.id.recy_zgg)
    RecyclerView recyclerView;

    private List<GoodsMo> mData = new ArrayList<>();

    private BaseAdapter adapter;
    @Override
    public int setLayout() {
        return R.layout.activity_china_gu;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void initView() {
        ivBack.setOnClickListener(this);
        changeSpinner(spinner1);
        changeSpinner(spinner2);
        changeSpinner(spinner3);
        changeSpinner(spinner4);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BaseAdapter<GoodsMo>(getContext(), recyclerView, mData, R.layout.item_limited_time) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, GoodsMo o) {
                holder.setImageByUrl(R.id.iv_img, o.getPicUrl());
                holder.setText(R.id.tv_name, o.getGoodsName());
                holder.setText(R.id.tv_price, o.getPrice());
                holder.setText(R.id.tv_marketPrice,o.getOriginalPrice());
            }
        };
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new onLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                page++;
                initData();
            }
        });
        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GoodsMo goodsMo = (GoodsMo) adapter.getData().get(position);
                Intent intent = new Intent(getContext(), GoodsDetailsActivity.class);
                intent.putExtra("goodsId", goodsMo.getGoodsId());
                intent.putExtra("type",2);
                startActivity(intent);
            }
        });


    }

    private int page = 1;
    @Override
    public void initData() {
        Map<String, String> map = new HashMap<>();
        map.put("pageNum", String.valueOf(page));
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.zggList, str, getToken(this), new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("code")) {
                        JSONObject dataObj = object.optJSONObject("data");
                        String str = dataObj.optString("records");
                        mData = JsonUtil.string2Obj(str, List.class, GoodsMo.class);
                        if (mData != null && mData.size() > 0) {
                            if (page>1) {
                                adapter.addAll(mData);
                            } else {
                                adapter.updateData(mData);
                            }
                        }
                    } else {
                        ToastUtil.showShort(getContext(), TextUtil.checkStr2Str(object.optString("msg")));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void changeSpinner(Spinner spinner){
        String[] spinnerItems = {"北京","南宁","重庆","南宁","重庆","南宁","重庆","南宁","重庆","南宁","重庆"};
        //自定义选择填充后的字体样式
        //只能是textview样式，否则报错：ArrayAdapter requires the resource ID to be a TextView
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,R.layout.text_spanner, spinnerItems);
        //自定义下拉的字体样式
        spinnerAdapter.setDropDownViewResource(R.layout.text_spanner_drop);
        //这个在不同的Theme下，显示的效果是不同的
        //spinnerAdapter.setDropDownViewTheme(Theme.LIGHT);
        spinner.setAdapter(spinnerAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
                default:break;
        }
    }
}
