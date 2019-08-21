package com.tzl.agriculture.fragment.vip.fragment;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.vip.activity.CouponActivity;
import com.tzl.agriculture.fragment.vip.model.CouponMo;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseFragment;
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

/**
 *
 */
public class CouponFragment extends BaseFragment {


    @BindView(R.id.recy)
    RecyclerView recyclerView;

    private BaseAdapter adapter;

    private List<CouponMo>mData = new ArrayList<>();
    @Override
    protected int initLayout() {
        return R.layout.recy_demo;
    }

    @Override
    protected void initView(View view) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BaseAdapter<CouponMo>(getContext(), recyclerView, mData, R.layout.item_coupon) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position,CouponMo o) {
                TextView tvPrice = holder.getView(R.id.tv_price);
                TextView tvTip = holder.getView(R.id.tv_tip);
                TextView tv_mess = holder.getView(R.id.tv_mess);
                TextView text_flag = holder.getView(R.id.text_flag);
                TextView tv_type = holder.getView(R.id.tv_type);
                ImageView image_top = holder.getView(R.id.image_top);

                int status = o.getCardState();

                tvPrice.setTextColor(getResources().getColor(R.color.colorCoupon));

                tvTip.setBackgroundResource(R.mipmap.pic_conpon_no_use_bg);
                tvTip.setClickable(false);
                tvTip.setText("未使用");

                switch (status) {
                    case 0:
                        break;
                    case 1:
                        tvPrice.setTextColor(getResources().getColor(R.color.colorGray));
                        tv_mess.setTextColor(getResources().getColor(R.color.colorGray));
                        text_flag.setTextColor(getResources().getColor(R.color.colorGray));
                        image_top.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.shape_coupon_top_used));

                        tvTip.setBackgroundResource(R.mipmap.pic_coupon_used_bg);
                        tv_type.setBackgroundResource(R.mipmap.pic_coupoe_used_quan);
                        tvTip.setText("已使用");
                        break;
                    case 2:
                        tvPrice.setTextColor(getResources().getColor(R.color.colorGray));
                        tv_mess.setTextColor(getResources().getColor(R.color.colorGray));
                        text_flag.setTextColor(getResources().getColor(R.color.colorGray));
                        image_top.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.shape_coupon_top_used));

                        tvTip.setBackgroundResource(R.mipmap.pic_coupon_used_bg);
                        tv_type.setBackgroundResource(R.mipmap.pic_coupoe_used_quan);
                        tvTip.setText("已过期");
                        break;
                    default:
                        break;
                }
                if (o.getConsumeType() == 1) {//满减
                    holder.setText(R.id.tv_price, o.getAmount());
                    holder.setText(R.id.tv_name, o.getCradName());
                    holder.setText(R.id.tv_endTime, o.getEndEffective());
                    holder.setText(R.id.tv_mess, o.getCradNote());
                } else {//折扣
                    holder.setText(R.id.tv_type, "折扣券\t" + TextUtil.checkStr2Str(o.getDiscount()));
                    holder.setText(R.id.tv_price, "0");
                    holder.setText(R.id.tv_name, o.getCradName());
                    holder.setText(R.id.tv_endTime, o.getEndEffective());
                    holder.setText(R.id.tv_mess, o.getCradNote());
                }
            }
        };
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new onLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                page += 1;
                initData();
            }
        });
    }

    private int page = 1;
    @Override
    protected void initData() {
        Map<String,String> map = new HashMap<>();
        map.put("pageNum",String.valueOf(page));
        map.put("pageSize","5");
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.minePage, str, getToken(), new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0){
                        JSONObject dataObj = object.optJSONObject("data");
                        String str = dataObj.optString("records");
                        mData = JsonUtil.string2Obj(str,List.class,CouponMo.class);
                        if (null != mData && mData.size()>0){
                            if (page > 1){
                                adapter.addAll(mData);
                            }else {
                                adapter.updateData(mData);
                            }
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
