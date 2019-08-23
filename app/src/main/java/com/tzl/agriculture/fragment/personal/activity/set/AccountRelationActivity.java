package com.tzl.agriculture.fragment.personal.activity.set;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tzl.agriculture.R;
import com.tzl.agriculture.application.AgricultureApplication;
import com.tzl.agriculture.model.UserInfo;
import com.tzl.agriculture.util.DialogUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import config.User;
import okhttp3.Call;

/**
 * 账号关联
 */
public class AccountRelationActivity extends SetBaseActivity implements View.OnClickListener {

    @BindView(R.id.rl_wx)
    RelativeLayout mRelativeLayoutWX;


    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_accont_relation;
    }

    @Override
    public void initView() {
        setTitle("账号关联");
    }

    @Override
    public void initData() {
        mRelativeLayoutWX.setOnClickListener(this);
        String str = (String) SPUtils.instance(this, 1).getkey("user", "");
        if(str!=null){
            UserInfo userInfo = JsonUtil.string2Obj(str, UserInfo.class);
            if(userInfo!=null){
                if(!TextUtils.isEmpty(userInfo.getOpenId())){
                    ((TextView)mRelativeLayoutWX.getChildAt(1)).setText("已关联");
                    ((ImageView)mRelativeLayoutWX.getChildAt(2)).setVisibility(View.INVISIBLE);
                    mRelativeLayoutWX.setEnabled(false);
                    ((TextView)mRelativeLayoutWX.getChildAt(1)).setTextColor(ContextCompat.getColor(AccountRelationActivity.this,R.color.colorPrimaryDark));
                }
            }
        }
    }


    @Override
    public void onClick(View viewArp) {
        wechatLogin();
    }


    /**
     * 微信登陆
     */
    private void wechatLogin() {
        if (AgricultureApplication.api == null) {
            AgricultureApplication.api = WXAPIFactory.createWXAPI(this, AgricultureApplication.APP_ID, true);
        }
        if (!AgricultureApplication.api.isWXAppInstalled()) {
            ToastUtil.showShort(this, "您手机尚未安装微信，请安装后再登录");
            return;
        }
        AgricultureApplication.api.registerApp(AgricultureApplication.APP_ID);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_xb_live_state";//官方说明：用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
        SPUtils.instance(this,1).put("wx_login_bind","bind");
        AgricultureApplication.api.sendReq(req);
    }


    @Override
    protected void onResume() {
        getUserInfo();
        super.onResume();
    }

    private void getUserInfo(){
        //获取用户信息
        OkHttp3Utils.getInstance(User.BASE).doGet(User.getUserInfo2, getToken(), new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {

                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0){
                        JSONObject dataObj = object.optJSONObject("data");

                        String str = dataObj.optString("user");
                        UserInfo userInfo = JsonUtil.string2Obj(str, UserInfo.class);
                        SPUtils.instance(AccountRelationActivity.this,1).put("user",str);
                        if(userInfo!=null){
                            if(!TextUtils.isEmpty(userInfo.getOpenId())){
                                ((TextView)mRelativeLayoutWX.getChildAt(1)).setText("已关联");
                                ((ImageView)mRelativeLayoutWX.getChildAt(2)).setVisibility(View.INVISIBLE);
                                mRelativeLayoutWX.setEnabled(false);
                                ((TextView)mRelativeLayoutWX.getChildAt(1)).setTextColor(ContextCompat.getColor(AccountRelationActivity.this,R.color.colorPrimaryDark));
                            }
                        }

                    }else if (object.optInt("code") == -1){
                        ToastUtil.showShort(AccountRelationActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
                    }else {
                        new DialogUtil(AccountRelationActivity.this).showTipsForControl("登陆失败，请重新登陆");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailed(Call call, IOException e) {

            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);

            }
        });
    }

}
