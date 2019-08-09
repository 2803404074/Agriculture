package com.tzl.agriculture.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.hanks.htextview.rainbow.RainbowTextView;
import com.rey.material.app.BottomSheetDialog;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.home.fragment.HomeFragment;
import com.tzl.agriculture.fragment.personal.framgent.PersonFragment;
import com.tzl.agriculture.fragment.xiangc.fragment.XiangcFragment;
import com.tzl.agriculture.model.UserInfo;
import com.tzl.agriculture.util.DrawableSizeUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.view.DoubleClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import config.Base;
import config.User;
import okhttp3.Call;

import static com.mob.MobSDK.getContext;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    public static MainActivity instance;

    //测试环境
    @BindView(R.id.htext)
    RainbowTextView tvSetAddress;


    @BindView(R.id.main_radiogrop)
    RadioGroup radioGroup;

    @BindView(R.id.main_home)
    RadioButton radioButton_01;

    @BindView(R.id.main_video)
    RadioButton radioButton_02;

    @BindView(R.id.main_my)
    RadioButton radioButton_03;

    private FragmentManager fragmentManager;
    private FragmentTransaction beginTransaction;

    private HomeFragment fg_00;
    private XiangcFragment fg_01;
    private PersonFragment fg_02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        getUserInfo();

        tvSetAddress.animateText("设置服务地址");

        instance = MainActivity.this;
        tvSetAddress.setOnClickListener(this);
        fragmentManager = getSupportFragmentManager();
        radioGroup.setOnCheckedChangeListener(this);
        radioButton_01.setChecked(true);
        radioButton_01.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        DrawableSizeUtil drawableSizeUtil = new DrawableSizeUtil(this);
        drawableSizeUtil.setImgSize(60, 60, 1, radioButton_01, R.drawable.select_home);
        drawableSizeUtil.setImgSize(60, 60, 1, radioButton_02, R.drawable.select_xc);
        drawableSizeUtil.setImgSize(60, 60, 1, radioButton_03, R.drawable.select_my);


        radioButton_01.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                Intent intent = new Intent("android.intent.action.DOUBLE_ACTION");
                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.main_home: {
                radioButton_01.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                radioButton_02.setTextColor(getResources().getColor(R.color.smssdk_tv_light_gray));
                radioButton_03.setTextColor(getResources().getColor(R.color.smssdk_tv_light_gray));
                changeFragment(0);
                break;
            }
            case R.id.main_video: {
                radioButton_01.setTextColor(getResources().getColor(R.color.smssdk_tv_light_gray));
                radioButton_02.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                radioButton_03.setTextColor(getResources().getColor(R.color.smssdk_tv_light_gray));
                changeFragment(1);
                //show(1,"该功能正在努力升级中，敬请期待!");
                break;
            }
            case R.id.main_my: {
                radioButton_01.setTextColor(getResources().getColor(R.color.smssdk_tv_light_gray));
                radioButton_02.setTextColor(getResources().getColor(R.color.smssdk_tv_light_gray));
                radioButton_03.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                changeFragment(2);
                break;
            }
        }
    }

    public void changeFragment(int index) {

        // 调用FragmentTransaction中的方法来处理这个transaction
        beginTransaction = fragmentManager.beginTransaction();
        hideFragments(beginTransaction);

        // 根据不同的标签页执行不同的操作
        switch (index) {
            case 0:
                /*****/
                if (fg_00 == null) {
                    fg_00 = HomeFragment.getInstance();
                    beginTransaction.add(R.id.fl_container, fg_00);
                } else {
                    beginTransaction.show(fg_00);
                }
                break;
            case 1:
                /*****/
                if (fg_01 == null) {
                    fg_01 = XiangcFragment.getInstance();
                    beginTransaction.add(R.id.fl_container, fg_01);
                } else {
                    beginTransaction.show(fg_01);
                }
                break;
            case 2:
                /*****/
                if (fg_02 == null) {
                    fg_02 = PersonFragment.getInstance();
                    beginTransaction.add(R.id.fl_container, fg_02);
                } else {
                    beginTransaction.show(fg_02);
                }
                break;
            default:
                break;
        }

        //需要提交事务
        beginTransaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (fg_00 != null) {
            transaction.hide(fg_00);
        }
        if (fg_01 != null) {
            transaction.hide(fg_01);
        }
        if (fg_02 != null) {
            transaction.hide(fg_02);
        }
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.htext:
                showBottomDialog();
                break;
            default:
                break;
        }
    }


    private BottomSheetDialog dialog;

    private void showBottomDialog() {
        dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_server_address, null);


        //选择
        CheckBox cb1 = view.findViewById(R.id.cb1);

        CheckBox cb2 = view.findViewById(R.id.cb2);

        CheckBox cb3 = view.findViewById(R.id.cb3);

        CheckBox cb4 = view.findViewById(R.id.cb4);

        CheckBox cb5 = view.findViewById(R.id.cb5);

        //地址
        EditText etAdd1 = view.findViewById(R.id.et1);

        EditText etAdd2 = view.findViewById(R.id.et2);

        EditText etAdd3 = view.findViewById(R.id.et3);

        EditText etAdd4 = view.findViewById(R.id.et4);

        EditText etAdd5 = view.findViewById(R.id.et5);

        //端口
        EditText etDk1 = view.findViewById(R.id.et_dk_1);

        EditText etDk2 = view.findViewById(R.id.et_dk_2);

        EditText etDk3 = view.findViewById(R.id.et_dk_3);

        EditText etDk4 = view.findViewById(R.id.et_dk_4);

        EditText etDk5 = view.findViewById(R.id.et_dk_5);

        TextView tvCheckIp = view.findViewById(R.id.tv_checkIp);


        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                    cb5.setChecked(false);
                    url = etAdd1.getText().toString();
                    dk = etDk1.getText().toString();
                    base = url+":"+dk;
                    tvCheckIp.setText(base);
                }
            }
        });

        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    cb1.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                    cb5.setChecked(false);
                    url = etAdd2.getText().toString();
                    dk = etDk2.getText().toString();

                    base = url+":"+dk;
                    tvCheckIp.setText(base);
                }
            }
        });


        cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    cb2.setChecked(false);
                    cb1.setChecked(false);
                    cb4.setChecked(false);
                    cb5.setChecked(false);
                    url = etAdd3.getText().toString();
                    dk = etDk3.getText().toString();

                    base = url+":"+dk;
                    tvCheckIp.setText(base);
                }
            }
        });


        cb4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb1.setChecked(false);
                    cb5.setChecked(false);
                    url = etAdd4.getText().toString();
                    dk = etDk4.getText().toString();

                    base = url+":"+dk;
                    tvCheckIp.setText(base);
                }
            }
        });

        cb5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                    cb1.setChecked(false);
                    url = etAdd5.getText().toString();
                    dk = etDk5.getText().toString();

                    base = url+":"+dk;
                    tvCheckIp.setText(base);
                }
            }
        });

        view.findViewById(R.id.tv_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttp3Utils.desInstance();
                Base.BASE = base;
                dialog.dismiss();
                ToastUtil.showShort(MainActivity.this,"设置成功");
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

    private String base;
    private String url="";
    private String dk="";



    private void getUserInfo(){
        String token = (String) SPUtils.instance(this,1).getkey("token","");
        //获取用户信息
        OkHttp3Utils.getInstance(User.BASE).doGet(User.getUserInfo2, token, new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {

                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0){
                        JSONObject dataObj = object.optJSONObject("data");
                        String str = dataObj.optString("user");
                        SPUtils.instance(getContext(),1).put("user",str);
                    }else if (object.optInt("code") == -1){
                        ToastUtil.showShort(getContext(), TextUtil.checkStr2Str(object.optString("msg")));
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
                        ToastUtil.showShort(getContext(),"请求超时");
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort(getContext(),"无法连接服务，请稍后再试");
                    }
                });
            }
        });
    }
}







