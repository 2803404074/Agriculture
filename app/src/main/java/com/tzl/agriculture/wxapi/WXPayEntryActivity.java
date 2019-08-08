package com.tzl.agriculture.wxapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tzl.agriculture.mall.activity.OrderPayStatusActivity;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.ToastUtil;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{

    private IWXAPI api;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.pay_result);
        api = WXAPIFactory.createWXAPI(this, "wx9253e5b4ad426487");
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if(resp.errCode == 0){
                Intent intent = new Intent(WXPayEntryActivity.this, OrderPayStatusActivity.class);
                intent.putExtra("price", (String) SPUtils.instance(WXPayEntryActivity.this,1).getkey("price",""));
                intent.putExtra("orderId",(String) SPUtils.instance(WXPayEntryActivity.this,1).getkey("orderId",""));
                startActivity(intent);
                finish();
            }else {
                //支付失败
                finish();
                ToastUtil.showShort(this,"支付失败,查看一下重复的订单参数是否一致");
            }
        }
    }
}
