package com.tzl.agriculture.mall.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.Html;
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
import com.tzl.agriculture.model.AddressMo;
import com.tzl.agriculture.model.OrderMo;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.MyTextChangedListener;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.util.WXPayUtils;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseRecyclerHolder;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
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


    @BindView(R.id.ll_coupon)
    LinearLayout mLinearLayoutCoupon;

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

    @BindView(R.id.or_is_address)
    LinearLayout ivSetAddress;

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
    //设置优惠券
    private void setTextCoupon(String ...stringsArp){
       TextView m1= (TextView) mLinearLayoutCoupon.getChildAt(0);
       TextView m2= (TextView) mLinearLayoutCoupon.getChildAt(1);
       TextView m3= (TextView) mLinearLayoutCoupon.getChildAt(2);
        m2.setVisibility(TextUtils.isEmpty(stringsArp[2])?View.GONE:View.VISIBLE);
        m1.setText(Html.fromHtml("店铺优惠\t\t\t\t\t"+"<font color='#8A8888'>"+stringsArp[0]+"张优惠券，共优惠"+stringsArp[1]+"元</font>"));
        m2.setText(Html.fromHtml("会员优惠\t\t\t\t\t"+"<font color='#8A8888'>折扣优惠"+stringsArp[2]+"元</font>"));
        m3.setText("优惠总额\t\t\t\t\t共计"+stringsArp[3]+"元");
    }

    //精确数据
    private String getTotalCoupon(String s1,String s2){
        if(TextUtils.isEmpty(s1)){
            s1="0";
        }   if(TextUtils.isEmpty(s2)){
            s2="0";
        }
        try {
            BigDecimal bigDecimal=new BigDecimal(s1);
           return bigDecimal.add(new BigDecimal(s2))+"";
        }catch (Throwable t){

        }
        return "0";
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
            public void convert(Context mContext, BaseRecyclerHolder holder,int position, OrderMo o) {

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
                    public void convert(Context mContext, BaseRecyclerHolder holder,int position, OrderMo.GoodsThis o) {
                        holder.setText(R.id.tv_title, o.getGoodsName());
                        holder.setText(R.id.tv_price, o.getGoodsPrice());
                        holder.setImageByUrl(R.id.iv_img, o.getPicUrl());
                        holder.setText(R.id.tv_num, "x" + o.getGoodsNum());
                        holder.setText(R.id.tv_gg, o.getGoodsSpecs());
                    }
                };
                recyclerViewSun.setAdapter(adapterx);
            }

        };
        recyclerView.setAdapter(adapter);
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
                        //设置地址
                        setAddress();

                        //店铺商品列表
                        String shopList = dataObj.optString("shopList");
                        orderMo = JsonUtil.string2Obj(shopList, List.class, OrderMo.class);

                        //优惠数据
                        JSONObject couponBosObj = dataObj.optJSONObject("couponBos");

                        //优惠券数量
                       String total=couponBosObj.optString("useCouponNum");
                        String deductedAmount= couponBosObj.optString("deductedAmount");
                        String member = couponBosObj.optString("member");
                        if (!TextUtils.isEmpty(member)){
                            if (member.equals("true")){//是会员
                                String memberAmount = couponBosObj.optString("memberAmount");
                                //订单已优惠（整个页面的）
                                setTextCoupon(total,deductedAmount,memberAmount,getTotalCoupon(memberAmount,deductedAmount));
                            }else{
                                //订单已优惠（整个页面的）
                                setTextCoupon(total,deductedAmount,"",getTotalCoupon("0",deductedAmount));
                            }
                        }else{
                            //订单已优惠（整个页面的）
                            setTextCoupon(total,deductedAmount,"",getTotalCoupon("0",deductedAmount));
                        }
                        //整个订单需要支付的金额
                        tvTotalPrice.setText(TextUtil.checkStr2Str(couponBosObj.optString("paymentAmount")));
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
                commit();
                break;
            case R.id.or_is_address:
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
        spinKitView.setVisibility(View.VISIBLE);
        Map<String, Object> map = new HashMap<>();
        map.put("typeId", 0);//1购物车购买
        map.put("remarksUser", JsonUtil.obj2String(mapMess));
        map.put("harvestId", addressId);
        String token = (String) SPUtils.instance(this, 1).getkey("token", "");
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.save, str, token, new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                spinKitView.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        JSONObject dataObj = object.optJSONObject("data");

                        //是否是兑换
                        boolean isExchange = dataObj.optBoolean("isExchange");
                        if (isExchange){//如果是兑换直接跳转订单详情
                            Intent intent = new Intent(OrderActivity.this,OrderDetailsActivity.class);
                            intent.putExtra("orderId",dataObj.optString("orderId"));
                            startActivity(intent);
                            spinKitView.setVisibility(View.GONE);
                            return;
                        }
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
                payEryWX(orderId);
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
    private void payEryWX(String orderId){
        Map<String,String>map = new HashMap<>();
        map.put("orderId",orderId);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJsonForObj(Mall.paySave, map, getToken(), new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                spinKitView.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        JSONObject dataObj = object.optJSONObject("data");
                        toWXPay(dataObj.optJSONObject("WXPay"));
                    }else {
                        ToastUtil.showShort(OrderActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
                    }

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
                        spinKitView.setVisibility(View.GONE);
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
