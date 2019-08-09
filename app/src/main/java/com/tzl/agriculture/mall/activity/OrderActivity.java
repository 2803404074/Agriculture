package com.tzl.agriculture.mall.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.rey.material.app.BottomSheetDialog;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.set.AddressActivity;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
import com.tzl.agriculture.fragment.vip.model.CouponMo;
import com.tzl.agriculture.main.MainActivity;
import com.tzl.agriculture.model.AddressMo;
import com.tzl.agriculture.model.CommentMo;
import com.tzl.agriculture.model.GoodsDetailsMo;
import com.tzl.agriculture.model.OrderMo;
import com.tzl.agriculture.util.BottomShowUtil;
import com.tzl.agriculture.util.DateUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.MyTextChangedListener;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.util.WXPayUtils;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseRecyclerHolder;
import com.tzl.agriculture.wxapi.WXPayEntryActivity;

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

public class OrderActivity extends SetBaseActivity implements View.OnClickListener {

    public static OrderActivity instance;
    @BindView(R.id.spin_kit)
    SpinKitView spinKitView;

    @BindView(R.id.recy)
    RecyclerView recyclerView;

    @BindView(R.id.recy_yhq)
    RecyclerView recyclerYhq;

    private BaseAdapter adapterYhq;
    private List<CouponMo> couponMos = new ArrayList<>();


    @BindView(R.id.tv_yhq_status)
    TextView tvYhqSize;

    @BindView(R.id.tv_youh)
    TextView tvYouh;

    private BaseAdapter adapter;

    private AddressMo addressMo;

    private List<OrderMo> orderMo = new ArrayList<>();

    private Map<String, String> mapMess = new HashMap<>();

    @BindView(R.id.orther_ok)
    TextView tvCommit;

    @BindView(R.id.tv_adName)
    TextView tvAdName;

    @BindView(R.id.tv_ad_phone)
    TextView tvAdPhone;


    @BindView(R.id.tv_adAddress)
    TextView tvAdAddress;

    @BindView(R.id.or_money_count)
    TextView tvTotalPrice;

    @BindView(R.id.orther_set_address)
    ImageView ivSetAddress;

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_order;
    }
    /**
     * 权限申请
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "申请失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void initView() {
        instance = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(OrderActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                //没有权限则申请权限
                ActivityCompat.requestPermissions(OrderActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }
        setTitle("确认订单");

        ivSetAddress.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BaseAdapter<OrderMo>(this, recyclerView, orderMo, R.layout.item_order_dep) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, OrderMo o) {

                holder.setText(R.id.tv_depName, o.getShopName());
                holder.setText(R.id.tv_kd, o.getDistribution());
                holder.setText(R.id.tv_fp, o.getInvoice());

                holder.setText(R.id.tv_goodsNum, "共" + o.getGoodsListBo().size() + "件");

                EditText editText = holder.getView(R.id.et_mess);
                editText.addTextChangedListener(new MyTextChangedListener(holder, o.getShopId(), mapMess));

                //小计
                holder.setText(R.id.tv_depPrice, o.getShopOrderAmount());


                //商品
                RecyclerView recyclerViewSun = holder.getView(R.id.recy_sun);
                recyclerViewSun.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
                BaseAdapter adapterx = new BaseAdapter<OrderMo.GoodsThis>(OrderActivity.this, recyclerViewSun, o.getGoodsListBo(), R.layout.item_my_order_goods) {
                    @Override
                    public void convert(Context mContext, BaseRecyclerHolder holder, OrderMo.GoodsThis o) {
                        holder.setText(R.id.tv_title, o.getGoodsName());
                        holder.setText(R.id.tv_price, o.getGoodsPrice());
                        holder.setImageByUrl(R.id.iv_img, o.getPicUrl());
                        holder.setText(R.id.tv_num, "X" + o.getGoodsNum());
                        holder.setText(R.id.tv_gg, o.getGoodsSpecs());
                    }
                };
                recyclerViewSun.setAdapter(adapterx);
            }

        };
        recyclerView.setAdapter(adapter);


        //优惠券
        LinearLayoutManager manager = new LinearLayoutManager(OrderActivity.this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerYhq.setLayoutManager(manager);

        adapterYhq = new BaseAdapter<CouponMo>(OrderActivity.this,
                recyclerYhq,couponMos,R.layout.item_coupon_limit) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, CouponMo o) {

                if (o.getConsumeType() == 1) {//1抵扣，price显示价钱
                    holder.setText(R.id.tv_type, "满减券");
                    holder.setText(R.id.tv_price, o.getAmount());
                    holder.setText(R.id.tv_name, o.getCradName());
                    holder.setText(R.id.tv_endTime, o.getEndEffective());
                    holder.setText(R.id.tv_mess, o.getCradNote());
                } else {//折扣  price显示折扣
                    holder.setText(R.id.tv_type, "折扣券\t" + TextUtil.checkStr2Str(o.getDiscount()));
                    holder.setText(R.id.tv_price, "0");
                    holder.setText(R.id.tv_name, o.getCradName());
                    holder.setText(R.id.tv_endTime, o.getEndEffective());
                    holder.setText(R.id.tv_mess, o.getCradNote());
                }
            }
        };
        recyclerYhq.setAdapter(adapterYhq);
        tvCommit.setOnClickListener(this);
    }

    @Override
    public void initData() {
        String token = (String) SPUtils.instance(this, 1).getkey("token", "");
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.getConfirm, "", token, new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {

                        JSONObject dataObj = object.optJSONObject("data");

                        //订单总价
                        String str = dataObj.optString("totalPrice");
                        tvTotalPrice.setText(TextUtil.checkStr2Num(str));

                        //地址
                        String harvest = dataObj.optString("harvest");
                        addressMo = JsonUtil.string2Obj(harvest, AddressMo.class);

                        //店铺商品列表
                        String shopList = dataObj.optString("shopList");
                        orderMo = JsonUtil.string2Obj(shopList, List.class, OrderMo.class);

                        //优惠数据
                        JSONObject couponBosObj = dataObj.optJSONObject("couponBos");

                        //优惠券数量
                        tvYhqSize.setText(TextUtil.checkStr2Str(couponBosObj.optString("useCouponNum")));

                        String member = couponBosObj.optString("member");
                        if (!TextUtils.isEmpty(member)){
                            if (member.equals("true")){//是会员
                                String memberAmount = couponBosObj.optString("memberAmount");
                                //订单已优惠（整个页面的）
                                tvYouh.setText(TextUtil.checkStr2Str("原价："+
                                        couponBosObj.optString("totalAmount")+"，已优惠"+couponBosObj.optString("deductedAmount")+"元 ，会员折扣优惠" + memberAmount +"元。"));
                            }else{
                                //订单已优惠（整个页面的）
                                tvYouh.setText(TextUtil.checkStr2Str("原价："+
                                        couponBosObj.optString("totalAmount")+"，已优惠"+couponBosObj.optString("deductedAmount")+"元"));
                            }
                        }else{
                            //订单已优惠（整个页面的）
                            tvYouh.setText(TextUtil.checkStr2Str("原价："+
                                    couponBosObj.optString("totalAmount")+"，已优惠"+couponBosObj.optString("deductedAmount")+"元"));
                        }

                        //订单已优惠（整个页面的）
//                        tvYouh.setText(TextUtil.checkStr2Str("原价："+
//                                couponBosObj.optString("totalAmount")+"，已优惠"+couponBosObj.optString("deductedAmount")+"元"));

                        //整个订单需要支付的金额
                        tvTotalPrice.setText(TextUtil.checkStr2Str(couponBosObj.optString("paymentAmount")));

                        //优惠券列表
                        String couponsStr = couponBosObj.optString("coupons");
                        couponMos = JsonUtil.string2Obj(couponsStr, List.class, CouponMo.class);
                        if (couponMos !=null){
                            adapterYhq.updateData(couponMos);
                        }

                        //设置地址
                        setAddress();

                        //设置商品
                        adapter.updateData(orderMo);

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

    private void setAddress() {
        if (addressMo != null) {
            if (TextUtil.checkStrNull(addressMo.getHarvestId())) {
                return;
            }
            tvAdName.setText(TextUtil.checkStr2Str(addressMo.getReceiptName()));
            tvAdPhone.setText(TextUtil.checkStr2Str(addressMo.getReceiptPhone()));
            tvAdAddress.setText(addressMo.getProvinceStr() + addressMo.getCityStr() + addressMo.getAreaStr());
            addressId = addressMo.getHarvestId();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.orther_ok:
                spinKitView.setVisibility(View.VISIBLE);
                commit();
                break;
            case R.id.orther_set_address:
                Intent intent = new Intent(OrderActivity.this, AddressActivity.class);
                intent.putExtra("type", 1);
                startActivityForResult(intent, 100);
                break;
            default:
                break;
        }
    }

    /**
     * 提交订单
     */
    private void commit() {
        if (StringUtils.isEmpty(addressId)) {
            showTwo();
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("typeId", 0);//1购物车购买
        map.put("remarksUser", JsonUtil.obj2String(mapMess));
        map.put("harvestId", addressId);
        String token = (String) SPUtils.instance(this, 1).getkey("token", "");
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.save, str, token, new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        JSONObject dataObj = object.optJSONObject("data");


                        if (dataObj.optBoolean("status")){

                            showBottomDialog(dataObj.optString("orderId"));

                            //存当前订单的价钱
                            SPUtils.instance(OrderActivity.this,1).put("price",tvTotalPrice.getText().toString());

                            //存当前的订单id
                            SPUtils.instance(OrderActivity.this,1).put("orderId",dataObj.optString("orderId"));
                        }
                    }else {
                        ToastUtil.showShort(OrderActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
                    }

                    spinKitView.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinKitView.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinKitView.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private BottomSheetDialog dialog;

    //支付弹窗
    private void showBottomDialog(String orderId) {
        dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_zhif, null);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        TextView tvPrice = view.findViewById(R.id.tv_price);
        tvPrice.setText(tvTotalPrice.getText().toString());
        view.findViewById(R.id.tv_zf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinKitView.setVisibility(View.VISIBLE);
                payEryWX();
            }
        });
        dialog.contentView(view)/*加载视图*/
                /*.heightParam(height/2),显示的高度*/
                /*动画设置*/
                .inDuration(200)
                .outDuration(200)
                /*.inInterpolator(new BounceInterpolator())
                .outInterpolator(new AnticipateInterpolator())*/
                .cancelable(true)
                .show();

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Intent intent = new Intent(OrderActivity.this, OrderPayStatusActivity.class);
                        intent.putExtra("price",tvTotalPrice.getText().toString() );
                        intent.putExtra("orderId",orderId);
                        intent.putExtra("status",-1);
                        startActivity(intent);
                        finish();
                    }
                });
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Intent intent = new Intent(OrderActivity.this, OrderPayStatusActivity.class);
                        intent.putExtra("price",tvTotalPrice.getText().toString() );
                        intent.putExtra("orderId",orderId);
                        startActivity(intent);
                        finish();
                    }
                });

    }

    /**
     * 支付
     */
    private void payEryWX(){
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.paySave, "", getToken(), new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        JSONObject dataObj = object.optJSONObject("data");
                        toWXPay(dataObj.optJSONObject("WXPay"));
                    }else {
                        ToastUtil.showShort(OrderActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
                    }
                    spinKitView.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinKitView.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinKitView.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    //添加地址提示
    private void showTwo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setIcon(R.mipmap.application).setTitle("趣乡服务")
                .setMessage("您未添加地址，是否前往添加？").setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(OrderActivity.this, AddressActivity.class);
                        intent.putExtra("type", 1);
                        startActivityForResult(intent, 100);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                tvAdName.setText(TextUtil.checkStr2Str(data.getStringExtra("name")));
                tvAdPhone.setText(TextUtil.checkStr2Str(data.getStringExtra("phone")));
                tvAdAddress.setText(TextUtil.checkStr2Str(data.getStringExtra("address")));
                addressId = data.getStringExtra("addressId");
            }
        }
    }

    private String addressId;


    private void toWXPay(JSONObject object) {
        WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
        builder.setAppId(object.optString("appid"))
                .setPartnerId(object.optString("partnerid"))
                .setPrepayId(object.optString("prepayid"))
                .setPackageValue(object.optString("package"))
                .setNonceStr(object.optString("noncestr"))
                .setTimeStamp(object.optString("timestamp"))
                .setSign(object.optString("sign"))
                .build().toWXPayNotSign(OrderActivity.this);
        ToastUtil.showShort(this, TextUtil.checkStr2Str(object.optString("returnMsg"))+"正在打开微信");
    }


}
