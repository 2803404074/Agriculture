package com.tzl.agriculture.mall.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.set.AddressActivity;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
import com.tzl.agriculture.model.AddressMo;
import com.tzl.agriculture.model.CommentMo;
import com.tzl.agriculture.model.GoodsDetailsMo;
import com.tzl.agriculture.model.OrderMo;
import com.tzl.agriculture.util.BottomShowUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.MyTextChangedListener;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
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

public class OrderActivity extends SetBaseActivity implements View.OnClickListener {

    @BindView(R.id.recy)
    RecyclerView recyclerView;

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
    public int setLayout() {
        return R.layout.activity_order;
    }

    @Override
    public void initView() {
        setTitle("确认订单");

        ivSetAddress.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BaseAdapter<OrderMo>(this, recyclerView, orderMo, R.layout.item_order_dep) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, OrderMo o) {

                holder.setText(R.id.tv_depName, o.getShopName());
                holder.setText(R.id.tv_kd, o.getDistribution());
                holder.setText(R.id.tv_fp, o.getInvoice());
                holder.setText(R.id.tv_depPrice, o.getShopOrderAmount());
                holder.setText(R.id.tv_goodsNum, "共" + o.getGoodsListBo().size() + "件");

                EditText editText = holder.getView(R.id.et_mess);
                editText.addTextChangedListener(new MyTextChangedListener(holder, o.getShopId(), mapMess));

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
                hasAddress = false;
                return;

            }
            tvAdName.setText(TextUtil.checkStr2Str(addressMo.getReceiptName()));
            tvAdPhone.setText(TextUtil.checkStr2Str(addressMo.getReceiptPhone()));
            tvAdAddress.setText(addressMo.getProvinceStr() + addressMo.getCityStr() + addressMo.getAreaStr());
            hasAddress = true;
        }

    }

    private boolean hasAddress = false;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.orther_ok:
                showBottomDialog(view);
                break;
            case R.id.orther_set_address:
                Intent intent = new Intent(OrderActivity.this, AddressActivity.class);
                intent.putExtra("type",1);
                startActivityForResult(intent,100);
                break;
            default:
                break;
        }
    }

    /**
     * 提交订单
     */
    private void commit() {
        if (!hasAddress) {
            showTwo();
            showUtil.des();
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("typeId", 0);//1购物车购买
        map.put("remarksUser", JsonUtil.obj2String(mapMess));
        map.put("harvestId", addressMo.getHarvestId());
        String token = (String) SPUtils.instance(this, 1).getkey("token", "");
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.save, str, token, new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        Intent intent = new Intent(OrderActivity.this, OrderPayStatusActivity.class);
                        intent.putExtra("price",tvTotalPrice.getText().toString());
                        startActivity(intent);
                        finish();
                    }
                    ToastUtil.showShort(OrderActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }

    //支付弹窗
    private void showBottomDialog(View view) {
        showUtil = new BottomShowUtil(this, this) {
            @Override
            public void convert(Context mContext, View holder) {
                TextView tvPrice = holder.findViewById(R.id.tv_price);
                tvPrice.setText(tvTotalPrice.getText().toString());
                holder.findViewById(R.id.tv_zf).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        commit();
                        showUtil.des();
                    }
                });
            }
        };
        showUtil.BottomShow(view, R.layout.dialog_zhif);
    }


    private BottomShowUtil showUtil;

    //添加地址提示
    private void showTwo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setIcon(R.mipmap.application).setTitle("趣乡服务")
                .setMessage("您未添加地址，是否前往添加？").setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(OrderActivity.this, AddressActivity.class);
                        intent.putExtra("type",1);
                        startActivityForResult(intent,100);
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
        if (resultCode == RESULT_OK){
            if (requestCode == 100){
                initData();
            }
        }
    }
}
