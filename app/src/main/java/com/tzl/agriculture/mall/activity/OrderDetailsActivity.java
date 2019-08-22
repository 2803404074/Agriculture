package com.tzl.agriculture.mall.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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
import com.tzl.agriculture.model.WlMo;
import com.tzl.agriculture.util.BottomShowUtil;
import com.tzl.agriculture.util.DateUtil;
import com.tzl.agriculture.util.DialogUtilT;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.StatusBarUtil;
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
import config.User;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public void initView() {


        //本activity需要沉浸式
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);

        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.colorW));
        }

        setTitle("订单详情");

        llLickDep.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new BaseAdapter<OrderMo.GoodsThis>(this, recyclerView, list, R.layout.item_my_order_goods) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder,int position, OrderMo.GoodsThis o) {
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
        tvQrsh.setOnClickListener(this);
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
                tvPj.setVisibility(View.GONE);
                tvQrsh.setVisibility(View.GONE);
                tvCkwl.setVisibility(View.GONE);
                break;
            case 1:
                tvPj.setVisibility(View.GONE);
                tvQrsh.setVisibility(View.GONE);
                tvCkwl.setVisibility(View.GONE);
                tvQxdd.setVisibility(View.GONE);
                tvFk.setVisibility(View.GONE);
                break;
            case 2:
                tvPj.setVisibility(View.VISIBLE);
                tvQrsh.setVisibility(View.GONE);
                tvCkwl.setVisibility(View.GONE);
                tvQxdd.setVisibility(View.GONE);
                tvFk.setVisibility(View.GONE);
                break;
            case 3:
                tvQrsh.setVisibility(View.VISIBLE);
                tvCkwl.setVisibility(View.VISIBLE);
                tvQxdd.setVisibility(View.GONE);
                tvFk.setVisibility(View.GONE);
                tvPj.setVisibility(View.GONE);
                break;
            case 4:
                tvPj.setVisibility(View.GONE);
                tvQrsh.setVisibility(View.GONE);
                tvCkwl.setVisibility(View.GONE);
                tvQxdd.setVisibility(View.GONE);
                tvFk.setVisibility(View.GONE);
                break;
            case 5:
                tvPj.setVisibility(View.GONE);
                tvQrsh.setVisibility(View.GONE);
                tvCkwl.setVisibility(View.GONE);
                tvQxdd.setVisibility(View.GONE);
                tvFk.setVisibility(View.GONE);
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
                showTwo(0,"是否取消订单？");
                break;
            case R.id.tv_pj:
                Intent intent = new Intent(this, StartCommentActivity.class);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
                break;
            case R.id.tv_ckwl:
                showLogistics(orderId);
                break;
            case R.id.tv_qrsh:
                showTwo(1, "是否确认收货？");
                break;
            default:
                break;
        }
    }

    private void showTwo(int index, String tips) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setIcon(R.mipmap.application).setTitle("趣乡服务")
                .setMessage(tips).setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (index == 0) {
                            //取消订单
                            cancelOrder();
                        }
                        if (index == 1) {
                            qrsh();
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
    private void qrsh() {
        Map<String, String> map = new HashMap<>();
        map.put("orderId", orderId);
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.receivingOrder, str, getToken(), new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        ToastUtil.showShort(OrderDetailsActivity.this, "收货成功");
                        getOrderMess();
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

    private void cancelOrder() {
        Map<String, String> map = new HashMap<>();
        map.put("orderId", orderId);
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.cancelOrder, str, getToken(), new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        ToastUtil.showShort(OrderDetailsActivity.this, "取消成功");
                        getOrderMess();
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



    private DialogUtilT dialogUtilT;
    private void showLogistics(String orderId) {
        spinKitView.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("orderId", orderId);
        OkHttp3Utils.getInstance(User.BASE).doPostJsonForObj(User.wlFind, map, getToken(),
                new GsonObjectCallback<String>(User.BASE) {
                    @Override
                    public void onUi(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.optInt("code") == 0) {
                                JSONObject dataObj = object.optJSONObject("data");

                                //图片
                                String picUrl = dataObj.optString("picUrl");

                                //状态
                                String stateName = dataObj.optString("stateName");

                                //商品名称
                                String goodsName = dataObj.optString("goodsName");

                                //快递名称
                                String dtName = dataObj.optString("dtName");

                                //快递号
                                String dtNum = dataObj.optString("dtNum");

                                //详情
                                JSONObject kdInfoObj = dataObj.optJSONObject("kdInfo");
                                String str = kdInfoObj.optString("detail");
                                List<WlMo> mData = JsonUtil.string2Obj(str, List.class, WlMo.class);
                                if (mData !=null && mData.size()>0){
                                    mData.get(0).setFist(true);
                                }
                                dialogUtilT = new DialogUtilT<String>(OrderDetailsActivity.this) {
                                    @Override
                                    public void convert(BaseRecyclerHolder holder, String data) {
                                        holder.setText(R.id.tv_status, stateName);
                                        holder.setImageByUrl(R.id.iv_goods, picUrl);
                                        holder.setText(R.id.tv_name, goodsName);
                                        holder.setText(R.id.tv_wl, dtName + ":" + dtNum);
                                        //列表
                                        RecyclerView recyclerView = holder.getView(R.id.recy);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                        BaseAdapter adapter = new BaseAdapter<WlMo>(getContext(), recyclerView, mData, R.layout.item_logistics) {
                                            @Override
                                            public void convert(Context mContext, BaseRecyclerHolder holder,int position, WlMo o) {
                                                if (o.isFist()) {
                                                    //holder.getView(R.id.v_line_top).setVisibility(View.INVISIBLE);
                                                    holder.setImageResource(R.id.iv_status, R.drawable.round_check_active);
                                                } else {
                                                    //holder.getView(R.id.v_line_top).setVisibility(View.VISIBLE);
                                                    holder.setImageResource(R.id.iv_status, R.drawable.round_check_selected);
                                                }
                                                holder.setText(R.id.tv_date, DateUtil.stampToDateMoth(o.getTimeFormat()));
                                                holder.setText(R.id.tv_details, o.getRemark());
                                            }
                                        };
                                        recyclerView.setAdapter(adapter);
                                    }
                                };
                                dialogUtilT.show2(R.layout.dialog_logistics, "",6,5);
                            }else {
                                showTwo(3,"暂无物流信息，请稍后再试~");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        spinKitView.setVisibility(View.GONE);
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
        map.put("orderId", orderId == null?getIntent().getStringExtra("orderId"):orderId);
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

