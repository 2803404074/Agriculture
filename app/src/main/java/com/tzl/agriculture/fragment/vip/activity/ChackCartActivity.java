package com.tzl.agriculture.fragment.vip.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rey.material.app.BottomSheetDialog;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
import com.tzl.agriculture.fragment.vip.model.BankMo;
import com.tzl.agriculture.fragment.vip.model.CatMo;
import com.tzl.agriculture.util.DialogUtilT;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.ScreenUtils;
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
public class ChackCartActivity extends SetBaseActivity {

    @BindView(R.id.tv_card)
    TextView tvCard;//点击底部弹窗，选择银行

    @BindView(R.id.et_phone)
    EditText etPhone;

    @BindView(R.id.cb_check)
    CheckBox checkBox;

    @BindView(R.id.tv_next)
    TextView tvNext;

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_check_cart;
    }

    @Override
    public void initView() {
        setTitle("添加银行卡");

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInput();
            }
        });


        tvCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottom();
            }
        });
    }

    private void checkInput() {
        if (StringUtils.isEmpty(cId)){
            ToastUtil.showShort(this,"请选择银行卡");
            return;
        }
        if (StringUtils.isEmpty(etPhone.getText().toString())){
            ToastUtil.showShort(this,"请输入手机号");
            return;
        }
        if (!checkBox.isChecked()){
            ToastUtil.showShort(this,"未同意用户协议");
            return;
        }

        String number = getIntent().getStringExtra("number");
        String name = getIntent().getStringExtra("name");

        Map<String,Object>map = new HashMap<>();
        map.put("userPhone",etPhone.getText().toString());//手机号
        map.put("bankNumber",number);//银行卡号
        map.put("bankId",cId);//银行卡id
        map.put("cnname",name);//持卡人
        map.put("cardType",0);//银行卡类型（储蓄卡）

        String str = JsonUtil.obj2String(map);

        OkHttp3Utils.getInstance(User.BASE).doPostJson2(User.addCard, str, getToken(),
                new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0){

                        DialogUtilT dialogUtilT = new DialogUtilT<String>(ChackCartActivity.this) {
                            @Override
                            public void convert(BaseRecyclerHolder holder, String data) {
                                holder.setText(R.id.tv_status,data);

                                holder.getView(R.id.tv_next).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        finish();
                                    }
                                });
                            }
                        };
                        dialogUtilT.show2(R.layout.dialog_ok,object.optString("msg"));
                    }
                    ToastUtil.showShort(ChackCartActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
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

    private BottomSheetDialog dialog;
    private BaseAdapter adapter;
    private List<BankMo> mDate = new ArrayList<>();
    private String cId;

    /**
     * 选择银行
     */
    private void showBottom(){
        dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.recy_demo_w, null);

        RecyclerView recyclerView = view.findViewById(R.id.recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BaseAdapter<BankMo>(this,recyclerView,mDate,R.layout.item_img_text) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, BankMo o) {
                holder.setImageByUrl(R.id.iv_img,o.getBankLogo());
                holder.setText(R.id.tv_tips,o.getBankName());
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                cId = mDate.get(position).getBankId();
                tvCard.setText(mDate.get(position).getBankName());
                dialog.dismiss();
            }
        });

        Map<String,String>map = new HashMap<>();
        String str= JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(User.BASE).doPostJson2(User.allCardList, str, getToken(),
                new GsonObjectCallback<String>(User.BASE) {
                    @Override
                    public void onUi(String result) {
                        try {
                            JSONObject object = new JSONObject(result);

                            String str = object.optString("data");
                            mDate = JsonUtil.string2Obj(str,List.class,BankMo.class);

                            adapter.updateData(mDate);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(Call call, IOException e) {

                    }
                });

        int hight = (int) (Double.valueOf(ScreenUtils.getScreenHeight(this))/1.3);
        dialog.contentView(view)/*加载视图*/
                .heightParam(hight)
                /*动画设置*/
                .inDuration(200)
                .outDuration(200)
                .cancelable(true)
                .show();
    }
}
