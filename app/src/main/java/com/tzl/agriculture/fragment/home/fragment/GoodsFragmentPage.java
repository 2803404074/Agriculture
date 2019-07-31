package com.tzl.agriculture.fragment.home.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.mall.activity.GoodsDetailsActivity;
import com.tzl.agriculture.mall.activity.OrderDetailsActivity;
import com.tzl.agriculture.model.GoodsMo;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseFragmentFromType;
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
import config.Mall;
import okhttp3.Call;

/**
 *
 */
public class GoodsFragmentPage extends BaseFragmentFromType {

    private RecyclerView recyclerView;

    private BaseAdapter adapter;
    private List<GoodsMo> mData = new ArrayList<>();

    @Override
    protected int initLayout() {
        return R.layout.recy_demo;
    }

    public GoodsFragmentPage() {
    }
    public GoodsFragmentPage(int type) {
        this.type = type;
    }

    private int type;
    @Override
    protected void initView(View view) {
        recyclerView = view.findViewById(R.id.recy);
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
                setDate(true);
            }
        });
        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GoodsMo goodsMo = (GoodsMo) adapter.getData().get(position);
                Intent intent = new Intent(getContext(), GoodsDetailsActivity.class);
                intent.putExtra("goodsId", goodsMo.getGoodsId());
                if (type == 1){
                    intent.putExtra("type",2);
                }

                startActivity(intent);
            }
        });
    }

    private int page = 1;

    @Override
    protected void setDate(boolean isLoad) {
        String url = "";
        if (type == 1){//发现好物
            url = Mall.fxhwList;
        }else {
            url = Mall.xsgList;
        }

        Map<String, String> map = new HashMap<>();
        map.put("shopTypeId", getCtype());
        map.put("pageNum", String.valueOf(page));
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(url, str, getToken(), new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("code")) {
                        JSONObject dataObj = object.optJSONObject("data");
                        String str = dataObj.optString("records");
                        mData = JsonUtil.string2Obj(str, List.class, GoodsMo.class);
                        if (mData != null && mData.size() > 0) {
                            if (isLoad) {
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
}
