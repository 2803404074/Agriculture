package com.tzl.agriculture.fragment.personal.activity.ccb.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.mall.activity.GoodsDetailsActivity;
import com.tzl.agriculture.model.GoodsMin;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseFragment;
import com.tzl.agriculture.view.BaseRecyclerHolder;

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

/**
 * 商品收藏fragment
 */
public class CollectionGoodsFragment extends BaseFragment {


    @BindView(R.id.recy)
    RecyclerView recyclerView;

    @BindView(R.id.iv_tips)
    ImageView ivTips;

    private BaseAdapter adapter;

    private List<GoodsMin> mData = new ArrayList<>();

    @Override
    protected int initLayout() {
        return R.layout.recy_demo;
    }

    @Override
    protected void initView(View view) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BaseAdapter<GoodsMin>(getContext(), recyclerView, mData, R.layout.item_limited_time) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, GoodsMin o) {

                holder.getView(R.id.ll_price).setVisibility(View.GONE);//隐藏价钱
                holder.getView(R.id.ll_gg).setVisibility(View.GONE);//隐藏规格

                holder.setImageByUrl(R.id.iv_img, o.getPicUrl());
                holder.setText(R.id.tv_name, o.getGoodsName());
                TextView tvCollectionNum = holder.getView(R.id.tv_collection_num);
                tvCollectionNum.setVisibility(View.VISIBLE);
                tvCollectionNum.setText("收藏数：" + o.getCollectionNum());
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GoodsMin goodsMin = (GoodsMin) adapter.getData().get(position);
                Intent intent = new Intent(getContext(), GoodsDetailsActivity.class);
                intent.putExtra("goodsId",goodsMin.getGoodsId());
                intent.putExtra("type", 2);
                startActivity(intent);
            }
        });
    }

    private int page = 1;


    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    @Override
    protected void initData() {

    }


    private void getData(){
        Map<String, String> map = new HashMap<>();
        map.put("pageNum", String.valueOf(page));
        map.put("searchType", "goods");// "goods",  "shop"
        map.put("type", "1");//0浏览，1收藏
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.getGoodsOptionList,
                str, getToken(), new GsonObjectCallback<String>(Mall.BASE) {
                    @Override
                    public void onUi(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.optInt("code") == 0) {
                                JSONObject dataObj = object.optJSONObject("data");
                                String str = dataObj.optString("goodsList");
                                mData = JsonUtil.string2Obj(str, List.class, GoodsMin.class);
                                if (null != mData && mData.size() > 0) {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    ivTips.setVisibility(View.GONE);
                                    if (page == 1) {
                                        adapter.updateData(mData);
                                    } else {
                                        adapter.addAll(mData);
                                    }
                                } else {
                                    recyclerView.setVisibility(View.GONE);
                                    ivTips.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ivTips.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ivTips.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
    }
}
