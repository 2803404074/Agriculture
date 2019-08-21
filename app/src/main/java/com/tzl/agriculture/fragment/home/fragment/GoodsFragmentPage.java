package com.tzl.agriculture.fragment.home.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.mall.activity.GoodsDetailsActivity;
import com.tzl.agriculture.model.GoodsMo;
import com.tzl.agriculture.util.CountDownUtil;
import com.tzl.agriculture.util.DateUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.ShowButtonLayoutData;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseFragmentFromType;
import com.tzl.agriculture.view.BaseRecyclerHolder;
import com.tzl.agriculture.view.ShowButtonLayout;
import com.tzl.agriculture.view.onLoadMoreListener;

import org.apache.commons.lang.StringUtils;
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
 *
 */
public class GoodsFragmentPage extends BaseFragmentFromType {

    @BindView(R.id.iv_tips)
    ImageView ivTips;

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
            public void convert(Context mContext, BaseRecyclerHolder holder, int position,GoodsMo o) {

                //发现好物，不需要倒计时
                if (type == 1){
                    holder.getView(R.id.ll_gg).setVisibility(View.GONE);
                }
                //标签
                ShowButtonLayout labelLayout = holder.getView(R.id.labelLayout);
                ShowButtonLayoutData showButtonLayoutData = new ShowButtonLayoutData<String>(getContext(), labelLayout, o.getGoodsLabelList(),null);
                showButtonLayoutData.setView(R.layout.text_view_red);
                showButtonLayoutData.setData();


                holder.setImageByUrl(R.id.iv_img, o.getPicUrl());
                holder.setText(R.id.tv_name, o.getGoodsName());
                holder.setText(R.id.tv_price, o.getPrice());

                TextView tvMarketPrice = holder.getView(R.id.tv_marketPrice);

                tvMarketPrice.setText(o.getOriginalPrice());
                tvMarketPrice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );

                TextView tvDate = holder.getView(R.id.tv_date);
                dowTime(o.getSpikeEndTime(),tvDate);
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
        Map<String, String> map = new HashMap<>();
        String url = "";
        if (type == 1){//发现好物
            url = Mall.fxhwList;
            map.put("shopTypeId", getCtype());
        }else {
            url = Mall.xsgList;
        }
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
                        }else {
                            if (adapter.getData() == null || adapter.getData().size() == 0){
                                ivTips.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        if (adapter.getData() == null || adapter.getData().size() == 0){
                            ivTips.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailed(Call call, IOException e) {
                if (getActivity() == null)return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter.getData() == null || adapter.getData().size() == 0){
                            ivTips.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                if (getActivity() == null)return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter.getData() == null || adapter.getData().size() == 0){
                            ivTips.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }

    private void dowTime(String data,TextView tvDate) {
        if (StringUtils.isEmpty(data) || data.equals("null")){
            tvDate.setText("活动已结束");
            return;
        }
        CountDownUtil downUtil = new CountDownUtil();
        downUtil.start(DateUtil.timeToStamp(data), new CountDownUtil.OnCountDownCallBack() {
            @Override
            public void onProcess(int day, int hour, int minute, int second) {
                String strDay = "";
                String strHour= "";
                String strMinute = "";
                String strSecond = "";

                if (day<10){
                    strDay = 0+String.valueOf(day);
                }else {
                    strDay = String.valueOf(day);
                }

                if (hour<10){
                    strHour = 0+String.valueOf(hour);
                }else {
                    strHour = String.valueOf(hour);
                }

                if (minute<10){
                    strMinute = 0+String.valueOf(minute);
                }else {
                    strMinute = String.valueOf(minute);
                }

                if (second<10){
                    strSecond = 0+String.valueOf(second);
                }else {
                    strSecond = String.valueOf(second);
                }
                tvDate.setText(strDay + "天 " + strHour + "时 " + strMinute + "分 " + strSecond + "秒");
            }
            @Override
            public void onFinish() {
                tvDate.setText("活动已结束");
            }
        });
    }
}
