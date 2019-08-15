package com.tzl.agriculture.fragment.vip.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.rey.material.app.BottomSheetDialog;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
import com.tzl.agriculture.fragment.vip.model.CatMo;
import com.tzl.agriculture.util.JsonUtil;
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
import config.User;
import okhttp3.Call;

/**
 * 提现
 */
public class CashActivity extends SetBaseActivity {

    @BindView(R.id.spin_kit)
    SpinKitView spinKitView;

    @BindView(R.id.tv_money)
    TextView tvMoney;

    @BindView(R.id.tv_allTx)
    TextView tvAllTx;

    @BindView(R.id.tv_cart)
    TextView tvCart;

    @BindView(R.id.tv_startCash)
    TextView tvStartCash;

    @BindView(R.id.et_cashNumber)
    EditText etCashNumber;

    private BottomSheetDialog dialog;

    protected int cId=-1;

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_cash;
    }

    @Override
    public void initView() {
        setTitle("提现");
        double dMoney = Double.valueOf(getIntent().getStringExtra("money"));
        tvMoney.setText(String.valueOf(dMoney));
        tvCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });
        tvStartCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cId == -1){
                    ToastUtil.showShort(CashActivity.this,"请选择或添加银行卡");
                    return;
                }

                if (StringUtils.isEmpty(etCashNumber.getText().toString())
                        || Integer.parseInt(etCashNumber.getText().toString())<=0
                        || Integer.parseInt(etCashNumber.getText().toString())<1) {
                    ToastUtil.showShort(CashActivity.this, "请输入正确的金额");
                    return;
                }

                if (Double.valueOf(etCashNumber.getText().toString())>dMoney){
                    ToastUtil.showShort(CashActivity.this, "余额不足");
                    return;
                }
                showQuit(etCashNumber.getText().toString());
            }
        });

        tvAllTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dMoney<100){
                    ToastUtil.showShort(CashActivity.this,"您的余额不足100");
                }else {
                    etCashNumber.setText(tvMoney.getText().toString());
                    tvAllTx.setClickable(false);
                    tvAllTx.setTextColor(getResources().getColor(R.color.colorGray));
                }
            }
        });

    }

    private AlertDialog alertDialog;
    private void showQuit(String price){
        alertDialog = new AlertDialog.Builder(this).setIcon(R.mipmap.application).setTitle("趣乡提现")
                .setMessage("您将提现"+price+"，是否继续？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        spinKitView.setVisibility(View.VISIBLE);
                        startCah();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                }).create();

        alertDialog.show();
    }


    private void startCah() {

        Map<String, String> map = new HashMap<>();
        map.put("userTiebankId", String.valueOf(cId));
        map.put("withdrawApplyTotal", etCashNumber.getText().toString());
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(User.BASE).doPostJson2(User.postCash, str, getToken(),
                new GsonObjectCallback<String>(User.BASE) {
                    @Override
                    public void onUi(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.optInt("code") == 0) {
                                Intent intent = new Intent(CashActivity.this, CashStartActivity.class);
                                startActivity(intent);
                            }else {
                                ToastUtil.showShort(CashActivity.this,TextUtil.checkStr2Str(object.optString("msg")));
                            }
                            spinKitView.setVisibility(View.GONE);
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
    public void initData() {

    }

    private BaseAdapter adapter;
    private List<CatMo> mData = new ArrayList<>();

    private void showBottomDialog() {
        dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_add_cart, null);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.tv_addCad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(CashActivity.this, AddCartActivity.class);
                startActivity(intent);
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        adapter = new BaseAdapter<CatMo>(CashActivity.this, recyclerView, mData, R.layout.item_card) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, CatMo o) {
                CheckBox checkBox = holder.getView(R.id.cb_check);
                holder.setImageByUrl(R.id.iv_img, o.getBankLogo());
                holder.setText(R.id.tv_cardName, o.getBankName() + "(" + TextUtil.checkStr2Str(o.getBankNum()) + ")");
                if (o.isDefault()) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }
                checkBox.setClickable(false);

            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //将其他都置为false
                for (int i = 0; i < mData.size(); i++) {
                    mData.get(i).setDefault(false);
                }
                //把当前点击的设置为默认
                mData.get(position).setDefault(true);
                adapter.updateData(mData);

                cId = mData.get(position).getUserTiebankId();
                tvCart.setText(mData.get(position).getBankName() + "(" + TextUtil.checkStr2Str(mData.get(position).getBankNum()) + ")");
            }
        });


        OkHttp3Utils.getInstance(User.BASE).doPostJson2(User.cartList, "", getToken(),
                new GsonObjectCallback<String>(User.BASE) {
                    @Override
                    public void onUi(String result) {
                        try {
                            JSONObject object = new JSONObject(result);

                            String str = object.optString("data");

                            mData = JsonUtil.string2Obj(str, List.class, CatMo.class);

                            if (mData != null && mData.size() > 0) {
                                adapter.updateData(mData);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(Call call, IOException e) {

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserBanKard();
    }

    private void getUserBanKard(){
        OkHttp3Utils.getInstance(User.BASE).doPostJson2(User.cartList, "", getToken(),
                new GsonObjectCallback<String>(User.BASE) {
                    @Override
                    public void onUi(String result) {
                        try {
                            JSONObject object = new JSONObject(result);

                            String str = object.optString("data");

                            mData = JsonUtil.string2Obj(str, List.class, CatMo.class);

                            if(null != mData && mData.size()>0){
                                tvCart.setText(mData.get(0).getBankName() + "(" + TextUtil.checkStr2Str(mData.get(0).getBankNum()) + ")");
                                cId = mData.get(0).getUserTiebankId();
                            }else {
                                tvCart.setText("未添加银行卡");
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
