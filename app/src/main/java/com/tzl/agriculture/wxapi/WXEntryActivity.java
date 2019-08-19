package com.tzl.agriculture.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tzl.agriculture.application.AgricultureApplication;
import com.tzl.agriculture.fragment.personal.login.activity.LoginActivity;
import com.tzl.agriculture.fragment.personal.login.activity.PhoneRegistActivity;
import com.tzl.agriculture.main.MainActivity;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.ToastUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.User;
import okhttp3.Call;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI mWeixinAPI;
    private static final String APP_SECRET = "47710a0111e37c8b361539d790678b6e";
    public static final String WEIXIN_APP_ID = "wx9253e5b4ad426487";
    public static WXEntryActivity instants;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instants = this;
        mWeixinAPI = AgricultureApplication.api;
        mWeixinAPI.handleIntent(this.getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mWeixinAPI.handleIntent(intent, this);//必须调用此句话
    }

    //微信发送的请求将回调到onReq方法
    @Override
    public void onReq(BaseReq req) {
        Log.d("wechat", "onReq");
    }

    //发送到微信请求的响应结果
    @Override
    public void onResp(BaseResp resp) {
        Log.d("wechat", "onResp");
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if(resp.getType()== ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX){
                    finish();
                    return;
                }
                //ToastUtil.showShort(WXEntryActivity.this, "请求成功");
                String code = ((SendAuth.Resp) resp).code;
                getAccessToken(code);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                ToastUtil.showShort(WXEntryActivity.this, "取消");
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                ToastUtil.showShort(WXEntryActivity.this, "请求拒绝");
                finish();
                break;
            default:
                //发送返回
                finish();
                break;
        }
    }

    //66f9d1f7e2328f65339cdec1d993bd79
    //获取授权信息，即获取access_token 和 openId
    private void getAccessToken(String code) {
        String loginUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WEIXIN_APP_ID + "&secret=" + APP_SECRET + "&code=" + code + "&grant_type=authorization_code";
        OkHttp3Utils.getInstance("").doGet(loginUrl, new GsonObjectCallback<String>("") {
            @Override
            public void onUi(String result) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    String accessToken = jsonObject.optString("access_token");
                    String openId = jsonObject.getString("openid");
                    //获取用户信息
                    getUserInfo(accessToken, openId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                finish();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                finish();
            }
        });
    }

    /**
     * 获取用户信息
     *
     * @param acc
     * @param openId
     */
    private void getUserInfo(String acc, String openId) {
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + acc + "&openid=" + openId + "";
        OkHttp3Utils.getInstance("").doGet(url, new GsonObjectCallback<String>("") {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (StringUtils.isEmpty(result)) {
                        return ;
                    }
                    String openId = object.optString("unionid");
                    String nickname = object.optString("nickname");
                    int sex = object.optInt("sex");
                    String headimgurl = object.optString("headimgurl");

                    //检测用户
                    checkUser(openId, nickname,sex, headimgurl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                finish();
            }
        });
    }

    private void checkUser(final String openID, final String uName, int sex, final String headimgurl){
        OkHttp3Utils.desInstance();
        OkHttp3Utils.getInstance(User.BASE).doGet2(User.checkOpenId + openID, new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0){
                        JSONObject dataObj = object.optJSONObject("data");
                        boolean hasUser = dataObj.optBoolean("isRegister");{
                            if (hasUser){//有该用户
                                SPUtils.instance(WXEntryActivity.this,1).put("token",dataObj.optString("token"));
                                Intent intent = new Intent(WXEntryActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                LoginActivity.instance.finish();
                            }else {//没有该用户,去绑定手机号
                                Intent intent = new Intent(WXEntryActivity.this, PhoneRegistActivity.class);
                                intent.putExtra("openId",openID);
                                intent.putExtra("userName",uName);
                                intent.putExtra("sex",sex);
                                intent.putExtra("imgUrl",headimgurl);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                finish();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                finish();
            }
        });
    }
}
