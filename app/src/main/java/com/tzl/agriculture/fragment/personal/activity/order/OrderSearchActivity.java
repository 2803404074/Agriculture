package com.tzl.agriculture.fragment.personal.activity.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.home.activity.SearchActivity;
import com.tzl.agriculture.mall.activity.OrderDetailsActivity;
import com.tzl.agriculture.mall.activity.StartCommentActivity;
import com.tzl.agriculture.model.OrderMo;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.view.BaseActivity;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseRecyclerHolder;

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

public class OrderSearchActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;

    @BindView(R.id.search_edit)
    EditText etEdit;

    @BindView(R.id.tv_start)
    TextView tvStart;

    @BindView(R.id.tv_tips)
    TextView tvTips;

    @BindView(R.id.search_recycler_view)
    RecyclerView recyclerView;

    private BaseAdapter adapter;

    private List<OrderMo> mData = new ArrayList<>();


    @Override
    public int setLayout() {
        return R.layout.activity_order_search;
    }

    @Override
    public void initView() {
        tvStart.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        etEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) etEdit.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(OrderSearchActivity.this
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    // 搜索，进行自己要的操作...
                    startSearch();
                    return true;
                }
                return false;
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BaseAdapter<OrderMo>(getContext(), recyclerView, mData, R.layout.item_my_order_dep) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, OrderMo o) {

                holder.setText(R.id.tv_depName, o.getShopName());
                TextView tvStatus = holder.getView(R.id.tv_status);

                holder.setText(R.id.tv_goodsSize, "共" + o.getGoodsListBo().size() + "件商品");

                holder.setText(R.id.tv_depPrice, o.getShopOrderAmount());

                //状态对应  需要显示的按钮
                //  0 联系卖家，取消订单，付款
                //  1 联系卖家，取消订单
                //  2 联系卖家，评价
                //  3 联系卖家，取消订单，查看物流
                switch (o.getOrderStatus()) {
                    case 0:
                        tvStatus.setText("等待买家付款");
                        holder.getView(R.id.tv_lxmj).setVisibility(View.VISIBLE);
                        holder.getView(R.id.tv_qxdd).setVisibility(View.VISIBLE);
                        holder.getView(R.id.tv_fk).setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        tvStatus.setText("等待卖家发货");
                        holder.getView(R.id.tv_lxmj).setVisibility(View.VISIBLE);
                        holder.getView(R.id.tv_qxdd).setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        tvStatus.setText("订单完成");
                        holder.getView(R.id.tv_lxmj).setVisibility(View.VISIBLE);
                        TextView tvComment = holder.getView(R.id.tv_pj);
                        tvComment.setVisibility(View.VISIBLE);
                        tvComment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), StartCommentActivity.class);
                                intent.putExtra("orderId",o.getOrderId());
                                startActivity(intent);
                            }
                        });

                        break;
                    case 3:
                        tvStatus.setText("等待买家收货");
                        holder.getView(R.id.tv_lxmj).setVisibility(View.VISIBLE);
                        holder.getView(R.id.tv_qxdd).setVisibility(View.VISIBLE);
                        holder.getView(R.id.tv_ckwl).setVisibility(View.VISIBLE);
                        break;
                }


                RecyclerView recy = holder.getView(R.id.recy);
                recy.setLayoutManager(new LinearLayoutManager(mContext));
                BaseAdapter adapterx = new BaseAdapter<OrderMo.GoodsThis>(mContext, recy, o.getGoodsListBo(), R.layout.item_my_order_goods) {
                    @Override
                    public void convert(Context mContext, BaseRecyclerHolder holder, OrderMo.GoodsThis o) {
                        holder.setImageByUrl(R.id.iv_img, o.getPicUrl());
                        holder.setText(R.id.tv_title, o.getGoodsName());
                        holder.setText(R.id.tv_price, getString(R.string.app_money) + o.getGoodsPrice());
                        holder.setText(R.id.tv_num, "X" + o.getGoodsNum());
                        holder.setText(R.id.tv_gg, TextUtil.checkStr2Str(o.getGoodsSpecs()));
                    }
                };
                recy.setAdapter(adapterx);

                adapterx.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        OrderMo.GoodsThis orderMo = (OrderMo.GoodsThis) adapterx.getData().get(position);
                        Intent intent = new Intent(getContext(), OrderDetailsActivity.class);
                        intent.putExtra("orderId", orderMo.getOrderId());
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                OrderMo orderMo = (OrderMo) adapter.getData().get(position);
                Intent intent = new Intent(getContext(), OrderDetailsActivity.class);
                intent.putExtra("orderId", orderMo.getOrderId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_start:
                if (StringUtils.isEmpty(etEdit.getText().toString())){
                    ToastUtil.showShort(this,"请输入订单关键字");
                    return;
                }
                startSearch();
                break;

            case R.id.iv_back:
                finish();
                break;
                default:break;
        }
    }

    private void startSearch(){
        Map<String, String> map = new HashMap<>();
        map.put("key", etEdit.getText().toString());
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.orderSearch, str, getToken(this), new GsonObjectCallback<String>(Mall.BASE) {

            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        JSONObject dataObj = object.optJSONObject("data");
                        String str = dataObj.optString("records");
                        mData = JsonUtil.string2Obj(str, List.class, OrderMo.class);
                        if (null != mData && mData.size() > 0) {
                            adapter.updateData(mData);
                            tvTips.setVisibility(View.GONE);
                        }else {
                            tvTips.setVisibility(View.VISIBLE);
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
