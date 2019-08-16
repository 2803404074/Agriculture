package com.tzl.agriculture.fragment.personal.activity.order.page;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.set.AddressActivity;
import com.tzl.agriculture.mall.activity.GoodsDetailsActivity;
import com.tzl.agriculture.mall.activity.OrderActivity;
import com.tzl.agriculture.mall.activity.OrderDetailsActivity;
import com.tzl.agriculture.mall.activity.StartCommentActivity;
import com.tzl.agriculture.model.OrderMo;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.util.WXPayUtils;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseFragment;
import com.tzl.agriculture.view.BaseFragmentFromType;
import com.tzl.agriculture.view.BaseRecyclerHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import config.Article;
import config.Mall;
import okhttp3.Call;

/**
 *
 */
public class OrderFragmentPage extends BaseFragmentFromType {

    @BindView(R.id.iv_tips)
    ImageView ivTips;

    @BindView(R.id.spin_kit)
    SpinKitView spinKitView;

    private RecyclerView recyclerView;

    private BaseAdapter adapter;
    private List<OrderMo> mData = new ArrayList<>();

    @Override
    protected int initLayout() {
        return R.layout.recy_demo;
    }

    public OrderFragmentPage() {
    }
    public OrderFragmentPage(int position) {
        this.mSerial = position;
    }


    @Override
    protected void initView(View view) {
        recyclerView = view.findViewById(R.id.recy);
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
                //                //  1 联系卖家，取消订单
                //                //  2 联系卖家，评价
                //                //  3 联系卖家，取消订单，查看物流
                //                //  4 订单已评价
                //  5 订单已取消
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
                        //holder.getView(R.id.tv_qxdd).setVisibility(View.VISIBLE);
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
                        holder.getView(R.id.tv_ckwl).setVisibility(View.VISIBLE);
                        TextView tvQrsh = holder.getView(R.id.tv_qrsh);
                        tvQrsh.setVisibility(View.VISIBLE);
                        tvQrsh.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showTwo(o,1,"是否确认收货？");
                            }
                        });

                        break;
                    case 4:
                        tvStatus.setText("订单已评价");
                        break;
                    case 5:
                        tvStatus.setText("订单已取消");
                        break;
                }

                //订单操作-- 取消订单
                holder.getView(R.id.tv_qxdd).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showTwo(o,0,"是否取消订单？");
                    }
                });

                //支付
                holder.getView(R.id.tv_fk).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      payEryWX(o.getOrderId());
                    }
                });



                //订单商品
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
    protected void setDate(boolean isLoad) {
        Map<String, String> map = new HashMap<>();
        map.put("orderStatus", getCtype());
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.orderList, str, getToken(), new GsonObjectCallback<String>(Mall.BASE) {

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
                        }
                        if (adapter.getData() == null || adapter.getData().size() == 0){
                            ivTips.setVisibility(View.VISIBLE);
                        }
                    } else {
                        ToastUtil.showShort(getContext(), "错误代码:"+object.optInt("code"));
                        getActivity().finish();
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
                        if (adapter.getData() == null || adapter.getData().size() == 0){
                            ivTips.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
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



    /**
     * 取消订单
     * @param orderMo
     */
    private void cancelOrder(OrderMo orderMo){
        Map<String,String>map = new HashMap<>();
        map.put("orderId",orderMo.getOrderId());
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.cancelOrder, str, getToken(), new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0){
                        ToastUtil.showShort(getContext(),"取消成功");
                        mData.remove(orderMo);
                        adapter.updateData(mData);
                        sendMessage();
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


    /**
     *  提示
     * @param orderMo
     * @param index 0 取消订单，1确认收货
     */
    private void showTwo(OrderMo orderMo , int index,String tips) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setIcon(R.mipmap.application).setTitle("趣乡服务")
                .setMessage(tips).setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (index == 0){
                            cancelOrder(orderMo);
                        }
                        if (index == 1){
                            qrsh(orderMo);
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ToDo: 你想做的事情
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    /**
     * 确认收货
     * @param orderMo
     */
    private void qrsh(OrderMo orderMo){
        Map<String,String>map = new HashMap<>();
        map.put("orderId",orderMo.getOrderId());
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.receivingOrder, str, getToken(), new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0){
                        ToastUtil.showShort(getContext(),"收货成功");
                        mData.remove(orderMo);
                        adapter.updateData(mData);
                        sendMessage();
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

    private void payEryWX(String orderId){
        spinKitView.setVisibility(View.VISIBLE);
        Map<String,String>map = new HashMap<>();
        map.put("orderId",orderId);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJsonForObj(Mall.paySave, map, getToken(), new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        JSONObject dataObj = object.optJSONObject("data");
                        toWXPay(dataObj.optJSONObject("WXPay"));
                    }else {
                        ToastUtil.showShort(getContext(), TextUtil.checkStr2Str(object.optString("msg")));
                    }
                    spinKitView.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinKitView.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinKitView.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void toWXPay(JSONObject object) {
        WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
        builder.setAppId(object.optString("appid"))
                .setPartnerId(object.optString("partnerid"))
                .setPrepayId(object.optString("prepayid"))
                .setPackageValue(object.optString("package"))
                .setNonceStr(object.optString("noncestr"))
                .setTimeStamp(object.optString("timestamp"))
                .setSign(object.optString("sign"))
                .build().toWXPayNotSign(getContext());
        ToastUtil.showShort(getContext(), TextUtil.checkStr2Str(object.optString("returnMsg"))+"正在打开微信");
    }



}
