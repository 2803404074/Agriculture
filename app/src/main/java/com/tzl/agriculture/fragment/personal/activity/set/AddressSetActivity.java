package com.tzl.agriculture.fragment.personal.activity.set;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import com.lljjcoder.citypickerview.widget.CityPicker;
import com.tzl.agriculture.R;
import com.tzl.agriculture.model.AddressMo;
import com.tzl.agriculture.util.DrawableSizeUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import config.Article;
import config.User;
import okhttp3.Call;

public class AddressSetActivity extends SetBaseActivity {

    private AddressMo addressMo;

    @BindView(R.id.et_name)
    EditText etName;

    @BindView(R.id.et_phone)
    EditText etPhone;

    @BindView(R.id.et_pcs)
    TextView tvPcs;//省市县

    @BindView(R.id.et_address)
    EditText etAddress;

    @BindView(R.id.ll_addressTag)
    LinearLayout llAddressTag;

    @BindView(R.id.sw)
    Switch aSwitch;

    @BindView(R.id.tv_save)
    TextView tvSave;

    @BindView(R.id.tv_tag)
    TextView tvTag;

    @BindView(R.id.et_jd)
    EditText etJd;

    private String labelName;
    private String province;//省
    private String city;//市
    private String district;//县

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_address_set;
    }

    @Override
    public void initView() {
        setTitle("编辑收货地址");
        String str = getIntent().getStringExtra("mess");
        addressMo = JsonUtil.string2Obj(str, AddressMo.class);

        if (null != addressMo) {

            etName.setText(addressMo.getReceiptName());
            etAddress.setText(addressMo.getReceiptName());
            etPhone.setText(addressMo.getReceiptPhone());
            etJd.setText(addressMo.getStreetStr());

            //省市县 初始化赋值
            province = addressMo.getProvinceStr();
            city = addressMo.getCityStr();
            district  =addressMo.getAreaStr();

            //省市县 展示字符串
            String addressStr = addressMo.getProvinceStr() + addressMo.getCityStr() + addressMo.getAreaStr();
            tvPcs.setText(addressStr);

            etAddress.setText(addressMo.getStreetStr() + addressMo.getAddress());

            if (addressMo.getIsDefault() == 1) {//默认
                aSwitch.setChecked(true);
            } else {
                aSwitch.setChecked(false);
            }

            tvTag.setText(TextUtil.checkStr2Str(addressMo.getLabel()));
        }


        llAddressTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog(view);
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAddress();
            }
        });
        tvPcs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAddress();
            }
        });
    }

    @Override
    public void initData() {

    }

    //省市县三级联动
    private void selectAddress() {
        CityPicker cityPicker = new CityPicker.Builder(this)
                .textSize(14)
                .title("地址选择")
                .titleBackgroundColor("#FFFFFF")
                .confirTextColor("#696969")
                .cancelTextColor("#696969")
                .province("四川省")
                .city("成都市")
                .district("青羊区")
                .textColor(Color.parseColor("#000000"))
                .provinceCyclic(true)
                .cityCyclic(false)
                .districtCyclic(false)
                .visibleItemsCount(7)
                .itemPadding(10)
                .onlyShowProvinceAndCity(false)
                .build();
        cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                //省份
                province = citySelected[0];
                //城市
                city = citySelected[1];
                //区县（如果设定了两级联动，那么该项返回空）
                district = citySelected[2];
                //邮编
                String code = citySelected[3];
                //为TextView赋值
                tvPcs.setText(TextUtil.checkStr2Str(province.trim()) + "" + TextUtil.checkStr2Str(city.trim()) + "" + TextUtil.checkStr2Str(district.trim()));
            }
        });

        cityPicker.show();
    }

    //设置标签
    private void showBottomDialog(View parent) {
        final View popView = View.inflate(this, R.layout.dialog_address_tag, null);
        showAnimation(popView);//开启动画

        TextView tvHome = popView.findViewById(R.id.tv_home);
        TextView tvGs = popView.findViewById(R.id.tv_gs);
        TextView tvXx = popView.findViewById(R.id.tv_xx);

        TextView tvQuit = popView.findViewById(R.id.tv_quit);
        EditText editText = popView.findViewById(R.id.et_input);

        DrawableSizeUtil util = new DrawableSizeUtil(AddressSetActivity.this);
        util.setImgSize(80, 80, 0, tvHome, R.mipmap.set_home);
        util.setImgSize(80, 80, 0, tvGs, R.mipmap.set_gs);
        util.setImgSize(80, 80, 0, tvXx, R.mipmap.set_school);

        PopupWindow mPopWindow = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mPopWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopWindow.showAtLocation(parent,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setFocusable(true);
        mPopWindow.update();
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = 0.7f;
        this.getWindow().setAttributes(lp);
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                tvTag.setText(labelName);
                WindowManager.LayoutParams lp = AddressSetActivity.this.getWindow().getAttributes();
                lp.alpha = 1f;
                AddressSetActivity.this.getWindow().setAttributes(lp);
            }
        });

        popView.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopWindow.dismiss();
            }
        });


        tvHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                labelName = tvHome.getText().toString();
                mPopWindow.dismiss();
            }
        });
        tvGs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                labelName = tvGs.getText().toString();
                mPopWindow.dismiss();
            }
        });
        tvXx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                labelName = tvXx.getText().toString();
                mPopWindow.dismiss();
            }
        });
        tvQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                labelName = editText.getText().toString();
                mPopWindow.dismiss();

            }
        });

    }

    private void showAnimation(View popView) {
        AnimationSet animationSet = new AnimationSet(false);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1.0f);
        alphaAnimation.setDuration(300);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        );
        translateAnimation.setDuration(300);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        popView.startAnimation(animationSet);
    }

    /**
     * 保存地址
     */
    private void saveAddress() {
        Map<String, String> map = new HashMap<>();
        map.put("provinceStr", province);//省
        map.put("cityStr", city);//市
        map.put("areaStr", district);//县/区
        map.put("streetStr", etJd.getText().toString());//道路
        map.put("address", etAddress.getText().toString());//门牌号
        map.put("receiptName", etName.getText().toString());
        map.put("receiptPhone", etPhone.getText().toString());
        map.put("harvestId", addressMo == null ? "" : addressMo.getHarvestId());
        map.put("isDefault", aSwitch.isChecked() ? "1" : "0");
        map.put("label", StringUtils.isEmpty(labelName) ? "" : labelName);


        String token = (String) SPUtils.instance(this, 1).getkey("token", "");
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(User.BASE).doPostJson2(User.saveAddress, str, token, new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0){
                        ToastUtil.showShort(AddressSetActivity.this,TextUtil.checkStr2Str(object.optString("msg")));
                        Intent intent = new Intent(AddressSetActivity.this, AddressActivity.class);
                        setResult(RESULT_OK, intent);
                        finish();
                    }else {
                        ToastUtil.showShort(AddressSetActivity.this,TextUtil.checkStr2Str(object.optString("msg")));
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
