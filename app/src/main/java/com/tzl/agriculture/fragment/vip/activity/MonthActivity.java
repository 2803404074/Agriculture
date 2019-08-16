package com.tzl.agriculture.fragment.vip.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
import com.tzl.agriculture.mall.activity.GoodsDetailsActivity;
import com.tzl.agriculture.model.GoodsMin;
import com.tzl.agriculture.model.GoodsMo;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.view.BaseAdapter;
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
import config.App;
import okhttp3.Call;

public class MonthActivity extends SetBaseActivity {

    @BindView(R.id.month_recy)
    RecyclerView recyclerView;

    private BaseAdapter adapter;

    private List<GoodsMo> mData = new ArrayList<>();

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_month;
    }

    @Override
    public void initView() {
        int month = getIntent().getIntExtra("monthNumber",1);
        setTitle(month+"月水果");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseAdapter<GoodsMo>(this,recyclerView,mData,R.layout.item_vip_goods_open) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, GoodsMo o) {

                holder.getView(R.id.tv_kt).setVisibility(View.GONE);

                holder.setImageByUrl(R.id.nice_img,o.getGoodsPic());

                holder.setText(R.id.tv_title,TextUtil.checkStr2Str(o.getGoodsName()));

                TextView tvGG = holder.getView(R.id.tv_gg);

                tvGG.setText("包邮");

                tvGG.setBackgroundResource(R.drawable.shape_login_blue_no);

                holder.setText(R.id.tv_price,o.getOriginalPrice());
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GoodsMo goodsMo = (GoodsMo) adapter.getData().get(position);
                Intent intent = new Intent(MonthActivity.this, GoodsDetailsActivity.class);
                intent.putExtra("goodsId",goodsMo.getGoodsId());
                intent.putExtra("type",2);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        Map<String,String>map = new HashMap<>();
        map.put("month",String.valueOf(getIntent().getIntExtra("monthNumber",1)));
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(App.BASE).doPostJson2(App.monthInfo, str, getToken(), new GsonObjectCallback<String>(App.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (0==object.optInt("code")){
                        String str = object.optString("data");
                        mData = JsonUtil.string2Obj(str,List.class,GoodsMo.class);
                        if (mData != null && mData.size()>0){
                            adapter.updateData(mData);
                        }
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
