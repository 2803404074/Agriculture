package com.tzl.agriculture.mall.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.rey.material.app.BottomSheetDialog;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
import com.tzl.agriculture.fragment.personal.login.activity.LoginActivity;
import com.tzl.agriculture.main.MainActivity;
import com.tzl.agriculture.model.AddressMo;
import com.tzl.agriculture.model.GoodsDetailsMo;
import com.tzl.agriculture.model.OrderMo;
import com.tzl.agriculture.util.BottomShowUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.util.WXPayUtils;
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
import config.Mall;
import okhttp3.Call;

public class OrderDetailsActivity extends SetBaseActivity implements View.OnClickListener {

    @BindView(R.id.recy_sun)
    RecyclerView recyclerView;

    @BindView(R.id.spin_kit)
    SpinKitView spinKitView;

    private BaseAdapter adapter;

    private List<OrderMo.GoodsThis> list = new ArrayList<>();

    @BindView(R.id.tv_adName)
    TextView tvAdName;

    @BindView(R.id.tv_phone)
    TextView tvPhone;

    @BindView(R.id.tv_address)
    TextView tvAddress;

    @BindView(R.id.tv_depName)
    TextView tvDepName;

    @BindView(R.id.tv_mess)
    TextView tvMess;//留言,不可编辑

    @BindView(R.id.tv_goodsNum)
    TextView tvGoodsNum;//订单商品数量

    @BindView(R.id.tv_price)
    TextView tvPrice;//订单总价

    @BindView(R.id.tv_orderNumber)
    TextView tvOrderNumber;

    @BindView(R.id.tv_date)
    TextView tvDate;

    @BindView(R.id.tv_deName)
    TextView tvDtName;

    @BindView(R.id.ll_lickDep)
    LinearLayout llLickDep;

    @BindView(R.id.tv_tips)
    TextView tvTips;

    @BindView(R.id.tv_times)
    TextView tvTimes;

    @BindView(R.id.tv_fp)
    TextView tvFp;

    @BindView(R.id.tv_wlmc)
    TextView tvWlmc;

    @BindView(R.id.tv_kddh)
    TextView tvKddh;

    //订单控制
    @BindView(R.id.tv_qxdd)
    TextView tvQxdd;
    @BindView(R.id.tv_fk)
    TextView tvFk;
    @BindView(R.id.tv_pj)
    TextView tvPj;
    @BindView(R.id.tv_ckwl)
    TextView tvCkwl;
    @BindView(R.id.tv_qrsh)
    TextView tvQrsh;

    private String orderId;
    private int orderStatus;
    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_order_details;
    }

    @Override
    public void initView() {
        setTitle("订单详情");

        llLickDep.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new BaseAdapter<OrderMo.GoodsThis>(this, recyclerView, list, R.layout.item_my_order_goods) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, OrderMo.GoodsThis o) {
                holder.setText(R.id.tv_title, o.getGoodsName());
                holder.setImageByUrl(R.id.iv_img, o.getPicUrl());
                holder.setText(R.id.tv_gg, o.getGoodsSpecs());
                holder.getView(R.id.tv_price).setVisibility(View.GONE);

                holder.setText(R.id.tv_num, "X" + o.getGoodsNum());
            }
        };
        recyclerView.setAdapter(adapter);

        tvFk.setOnClickListener(this);
        tvQxdd.setOnClickListener(this);
        tvPj.setOnClickListener(this);
        tvCkwl.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    //提示
    private void errTips(int code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setIcon(R.mipmap.application).setTitle("趣乡服务")
                .setMessage("错误代码:" + code).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                });
        builder.setCancelable(false);
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return false;
            }
        });
        builder.create().show();
    }

    private void setUi(AddressMo addressMo, String shopName, String shopOrderAmount, String orderNum, String createTime, String remarksUser) {
        if (null != addressMo) {
            tvAdName.setText(TextUtil.checkStr2Str(addressMo.getReceiptName()));
            tvPhone.setText(TextUtil.checkStr2Str(addressMo.getReceiptPhone()));
            String address = TextUtil.checkStr2Str(addressMo.getProvinceStr())
                    + TextUtil.checkStr2Str(addressMo.getCityStr())
                    + TextUtil.checkStr2Str(addressMo.getAreaStr())
                    + TextUtil.checkStr2Str(addressMo.getStreetStr())
                    + TextUtil.checkStr2Str(addressMo.getAddress());
            tvAddress.setText(address);
        }

        tvDepName.setText(shopName);

        tvGoodsNum.setText("共" + String.valueOf(list.size()) + "件商品");
        tvPrice.setText(shopOrderAmount);

        tvMess.setText(TextUtil.checkStr2Str(remarksUser));


        tvOrderNumber.setText(orderNum);
        tvDate.setText(createTime);

        //订单状态控制
        //  0 联系卖家，取消订单，付款
        //  1 联系卖家，取消订单
        //  2 联系卖家，评价
        //  3 联系卖家，取消订单，查看物流
        //  4 订单已评价
        //  5 订单已取消
        //订单状态，显示控制
        switch (orderStatus) {
            case 0:
                tvQxdd.setVisibility(View.VISIBLE);
                tvFk.setVisibility(View.VISIBLE);
                break;
            case 1:
                tvQxdd.setVisibility(View.VISIBLE);
                break;
            case 2:
                tvPj.setVisibility(View.VISIBLE);
                break;
            case 3:
                tvQxdd.setVisibility(View.VISIBLE);
                tvCkwl.setVisibility(View.VISIBLE);
                break;
            case 4:
                break;
            case 5:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_lickDep:
                showBottomDialog();
                break;
            case R.id.tv_fk:
                payEryWX(orderId);
                break;
            case R.id.tv_qxdd:
                break;
            case R.id.tv_pj:
                break;
            case R.id.tv_ckwl:
                break;
            default:
                break;
        }
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
                        ToastUtil.showShort(OrderDetailsActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
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

    private void toWXPay(JSONObject object) {
        WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
        builder.setAppId(object.optString("appid"))
                .setPartnerId(object.optString("partnerid"))
                .setPrepayId(object.optString("prepayid"))
                .setPackageValue(object.optString("package"))
                .setNonceStr(object.optString("noncestr"))
                .setTimeStamp(object.optString("timestamp"))
                .setSign(object.optString("sign"))
                .build().toWXPayNotSign(this);
        ToastUtil.showShort(this, TextUtil.checkStr2Str(object.optString("returnMsg"))+"正在打开微信");
    }

    protected String phone = "";

    private BottomSheetDialog dialog;

    //电话弹窗
    private void showBottomDialog() {
        dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_phone, null);
        TextView tvPhone = view.findViewById(R.id.tv_phone);
        tvPhone.setText(TextUtil.checkStr2Str(phone));
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_dial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(phone)) {

                    if (ContextCompat.checkSelfPermission(OrderDetailsActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        //点击拨打电话
                        Intent intent = new Intent(android.content.Intent.ACTION_CALL, Uri.parse("tel:" + phone.trim()));
                        startActivity(intent);
                    } else {
                        ActivityCompat.requestPermissions(OrderDetailsActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE}, 100);
                    }

                }else{
                    Toast.makeText(OrderDetailsActivity.this,"号码不能为空！",Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.contentView(view)
                .inDuration(200)
                .outDuration(200)
                .cancelable(true)
                .show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getOrderMess();
    }

    private void getOrderMess(){
        Map<String, String> map = new HashMap<>();
        map.put("orderId", getIntent().getStringExtra("orderId"));
        String str = JsonUtil.obj2String(map);
        String token = (String) SPUtils.instance(this, 1).getkey("token", "");
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.orderInfo, str, token, new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        JSONObject dataObj = object.optJSONObject("data");

                        //订单id
                        orderId = dataObj.optString("orderId");

                        //订单状态
                        orderStatus = dataObj.optInt("orderStatus");

                        //地址信息
                        String harvest = dataObj.optString("harvest");
                        AddressMo addressMo = JsonUtil.string2Obj(harvest, AddressMo.class);

                        //订单提示
                        String tips = dataObj.optString("tip");
                        String time = dataObj.optString("time");
                        tvTips.setText(tips);
                        tvTimes.setText(time);

                        //配送方式
                        String dtName = dataObj.optString("distribution");
                        tvDtName.setText(TextUtil.checkStr2Str(dtName));

                        //发票
                        tvFp.setText(TextUtil.checkStr2Str(dataObj.optString("invoice")));

                        //订单商品列表
                        JSONObject shopObj = dataObj.optJSONObject("shopList");
                        String goodsListBo = shopObj.optString("goodsListBo");
                        list = JsonUtil.string2Obj(goodsListBo, List.class, OrderMo.GoodsThis.class);
                        if (list == null) return;

                        if (list != null && list.size() > 0) {
                            adapter.updateData(list);
                        }

                        phone = shopObj.optString("shopTel");

                        //物流名称
                        tvWlmc.setText(TextUtil.checkStrNull(shopObj.optString("dtName")) ? "---" : shopObj.optString("dtName"));
                        //快递单号
                        tvKddh.setText(TextUtil.checkStrNull(shopObj.optString("dtNum")) ? "---" : shopObj.optString("dtNum"));

                        String shopName = shopObj.optString("shopName");
                        String shopOrderAmount = shopObj.optString("shopOrderAmount");
                        String orderNum = shopObj.optString("orderNum");
                        String createTime = shopObj.optString("createTime");
                        String remarksUser = shopObj.optString("remarksUser");

                        setUi(addressMo, shopName, shopOrderAmount, orderNum, createTime, remarksUser);
                    } else {
                        errTips(object.optInt("code"));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            //点击拨打电话
//            Intent intent = new Intent(android.content.Intent.ACTION_CALL, Uri.parse("tel:" + phone.trim()));
//            startActivity(intent);
        }
    }


}

