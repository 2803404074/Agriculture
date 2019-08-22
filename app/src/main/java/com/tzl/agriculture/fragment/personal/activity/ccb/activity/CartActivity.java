package com.tzl.agriculture.fragment.personal.activity.ccb.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.ccb.adapter.CartAdapter;
import com.tzl.agriculture.fragment.personal.activity.ccb.cartinter.OnItemMoneyClickListener;
import com.tzl.agriculture.fragment.personal.activity.ccb.cartinter.OnViewItemClickListener;
import com.tzl.agriculture.fragment.personal.activity.ccb.entity.CartInfo;
import com.tzl.agriculture.mall.activity.GoodsDetailsActivity;
import com.tzl.agriculture.model.HomeMo;
import com.tzl.agriculture.util.DialogUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.view.BaseActivity;

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
import butterknife.ButterKnife;
import butterknife.OnClick;
import config.Mall;
import config.ShopCart;
import okhttp3.Call;

/**
 * 购物车页面
 */
public class CartActivity extends BaseActivity {

    @BindView(R.id.recy)
    RecyclerView recyclerView;

    //管理按钮
    @BindView(R.id.tv_manage)
    TextView btnDelete;//tvManage

    //全选按钮
    @BindView(R.id.cbx_quanx_check)
    CheckBox checkBox;

    @BindView(R.id.text_quanx_check)
    TextView text_quanx_check;

    //结算
    @BindView(R.id.cart_shopp_moular)
    TextView cartShoppMoular;

    //总价
    @BindView(R.id.cart_money)
    TextView cartMoney;

    //数量
    @BindView(R.id.cart_num)
    TextView cartNum;

    //显示“合计”两个字的TextView  ，声明是为了 管理购物车显示删除按钮的时候隐藏这两个子
    @BindView(R.id.tv_hj)
    TextView tvHj;

    private CartAdapter cartAdapter;
    CartInfo cartInfo;
    double price;
    int num;



    public void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartShoppMoular.setOnClickListener(new OnClickListener());
        checkBox.setOnClickListener(new OnClickListener());
        btnDelete.setOnClickListener(new OnClickListener());
        text_quanx_check.setOnClickListener(new OnClickListener());
        findViewById(R.id.back).setOnClickListener(new OnClickListener());

//        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.line_divider_inset));
//        recyclerView.addItemDecoration(itemDecoration);
        cartAdapter = new CartAdapter(this);
        recyclerView.setAdapter(cartAdapter);
    }

    @Override
    public void initData() {
        getDatalist();
        showExpandData();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_cart;
    }

    private void setAllUi(){
        List<CartInfo.DataBean> data = cartInfo.getData();
        if(data!=null){
            boolean isC=true;
            for(CartInfo.DataBean dataBean:data){
                if(!dataBean.ischeck()){
                    isC=false;
                    break;
                }
            }
            if(isC)
                checkBox.setChecked(true);
        }
    }

    private void showExpandData() {
        /**
         * 全选
         */
        cartAdapter.setOnItemClickListener(new OnViewItemClickListener() {
            @Override
            public void onItemClick(boolean isFlang, View view, int position) {
                cartInfo.getData().get(position).setIscheck(isFlang);
                int length = cartInfo.getData().get(position).getItems().size();
                for (int i = 0; i < length; i++) {
                    cartInfo.getData().get(position).getItems().get(i).setIscheck(isFlang);
                }
                cartAdapter.notifyDataSetChanged();
                setAllUi();
                showCommodityCalculation();
            }
        });

        /**
         * 计算价钱
         */
        cartAdapter.setOnItemMoneyClickListener(new OnItemMoneyClickListener() {
            @Override
            public void onItemClick(View view,int index, int parentPosition, int position) {
                setAllUi();
                if(index==3){
                    showCommodityCalculation();
                }else {
                    if(index==1){
                        if(cartInfo.getData().get(parentPosition).getItems().get(position).getNum()<=1){
                            ToastUtil.showShort(CartActivity.this,"购买数量不能小于1");
                            return;
                        }
                    }else{
                        if(cartInfo.getData().get(parentPosition).getItems().get(position).getNum()>=cartInfo.getData().get(parentPosition).getItems().get(position).getStock()){
                            ToastUtil.showShort(CartActivity.this,"购买数量不能大于库存数量");
                            return;
                        }
                    }
                    getUpdateNum(index==2,parentPosition,  position);
                }
            }

        });
    }

    /***
     * 计算商品的数量和价格
     */
    private void showCommodityCalculation() {
        price = 0;
        num = 0;
        for (int i = 0; i < cartInfo.getData().size(); i++) {
            for (int j = 0; j < cartInfo.getData().get(i).getItems().size(); j++) {
                if (cartInfo.getData().get(i).getItems().get(j).ischeck()) {
                    price += Double.valueOf((cartInfo.getData().get(i).getItems().get(j).getNum() * Double.valueOf(cartInfo.getData().get(i).getItems().get(j).getPrice())));
                    num++;
                } else {
                    checkBox.setChecked(false);
                }
            }
        }
        if (price == 0.0) {
            cartNum.setText("共0件商品");
            cartMoney.setText("¥ 0.0");
            return;
        }
        try {
            String money = String.valueOf(price);
            cartNum.setText("共" + num + "件商品");
            if (money.substring(money.indexOf("."), money.length()).length() > 2) {
                cartMoney.setText("¥ " + money.substring(0, (money.indexOf(".") + 3)));
                return;
            }
            cartMoney.setText("¥ " + money.substring(0, (money.indexOf(".") + 2)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                //全选和不全选
                case R.id.cbx_quanx_check:
                case R.id.text_quanx_check:
                    if (checkBox.isChecked()) {
                        int length = cartInfo.getData().size();
                        for (int i = 0; i < length; i++) {
                            cartInfo.getData().get(i).setIscheck(true);
                            int lengthn = cartInfo.getData().get(i).getItems().size();
                            for (int j = 0; j < lengthn; j++) {
                                cartInfo.getData().get(i).getItems().get(j).setIscheck(true);
                            }
                        }

                    } else {
                        int length = cartInfo.getData().size();
                        for (int i = 0; i < length; i++) {
                            cartInfo.getData().get(i).setIscheck(false);
                            int lengthn = cartInfo.getData().get(i).getItems().size();
                            for (int j = 0; j < lengthn; j++) {
                                cartInfo.getData().get(i).getItems().get(j).setIscheck(false);
                            }
                        }
                    }
                    cartAdapter.notifyDataSetChanged();
                    showCommodityCalculation();
                    break;
                case R.id.tv_manage:
                    //删除选中商品
                    del();
//                    cartAdapter.removeChecked();
//                    showCommodityCalculation();
                    break;
                case R.id.cart_shopp_moular:
                    if(true){
                        DialogUtil.init(getContext()).showTips();
                        return;
                    }
                    if (num == 0) {
                        ToastUtil.showShort(CartActivity.this, "您还没选宝贝呢~~~~");
                    } else {
                        List<CartMo> mCartMo = new ArrayList<>();
                        for (int i = 0; i < cartInfo.getData().size(); i++) {
                            if (cartInfo.getData().get(i).ischeck()) {
                                for (int j = 0; j < cartInfo.getData().get(i).getItems().size(); j++) {
                                    mCartMo.add(new CartMo(cartInfo.getData().get(i).getShop_id(), String.valueOf(cartInfo.getData().get(i).getItems().get(j).getNum())));
                                }
                            }
                        }

                        ToastUtil.showShort(CartActivity.this, "提交" + JsonUtil.obj2String(mCartMo));
                    }

                    //Toast.makeText(CartActivity.this,"提交订单:  "+cartMoney.getText().toString()+"元",Toast.LENGTH_LONG).show();
                    break;
                case R.id.back:
                    finish();
                    break;
            }
        }
    }

    //移除购物车
    List<Integer> ids=new ArrayList<>();
    private void del() {
        ids.clear();
        for(CartInfo.DataBean cartInfo:cartInfo.getData()){
            if(cartInfo!=null){
                for(CartInfo.DataBean.ItemsBean itemsBean:cartInfo.getItems()){
                    if(itemsBean!=null){
                        if(itemsBean.ischeck()){
                            ids.add(itemsBean.getCartId());
                        }
                    }
                }
            }
        }
        if(ids.size()==0){
            ToastUtil.showShort(this,"请选中所移除的商品");
            return;
        }
        setLoaddingView(true);
        Map<String, Object> map = new HashMap<>();
        map.put("idList", ids);
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(ShopCart.BASE).doPostJson2(ShopCart.delGoods, str, (String) SPUtils.instance(this, 1).getkey("token", ""), new GsonObjectCallback<String>(ShopCart.BASE) {
            @Override
            public void onUi(String result) {
                setLoaddingView(false);
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        getDatalist();
                    } else {
                        ToastUtil.showShort(CartActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
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
                        setLoaddingView(false);
                    }
                });
            }
        });
    }


    class CartMo {
        private String cartId;
        private String cartNum;

        public CartMo(String cartId, String cartNum) {
            this.cartId = cartId;
            this.cartNum = cartNum;
        }

        public String getCartId() {
            return cartId;
        }

        public void setCartId(String cartId) {
            this.cartId = cartId;
        }

        public String getCartNum() {
            return cartNum;
        }

        public void setCartNum(String cartNum) {
            this.cartNum = cartNum;
        }
    }


    //获取购物数据列表
    private void getDatalist() {
        setLoaddingView(true);
        Map<String, String> map = new HashMap<>();
        map.put("pageNum", "1");
        map.put("pageSize", "20");
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(ShopCart.BASE).doPostJson2(ShopCart.getGoods, str, (String) SPUtils.instance(this, 1).getkey("token", ""), new GsonObjectCallback<String>(ShopCart.BASE) {
            @Override
            public void onUi(String result) {
                setLoaddingView(false);
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        cartNum.setText("共0件商品");
                        cartMoney.setText("¥ 0.0");
                        JSONObject data = object.getJSONObject("data");
                        cartInfo=JsonUtil.string2Obj(data.toString(), CartInfo.class);
                        if(cartInfo==null||cartInfo.getData()==null||cartInfo.getData().size()==0){
                            btnDelete.setVisibility(View.GONE);
                            text_quanx_check.setVisibility(View.GONE);
                            checkBox.setVisibility(View.GONE);
                            cartShoppMoular.setBackgroundResource(R.drawable.shape_login_blue_hide);
                            cartShoppMoular.setEnabled(false);
                            cartAdapter.updateData(null);
                        }else{
                            btnDelete.setVisibility(View.VISIBLE);
                            checkBox.setVisibility(View.VISIBLE);
                            text_quanx_check.setVisibility(View.VISIBLE);
                            cartShoppMoular.setBackgroundResource(R.drawable.shape_login_blue);
                            cartShoppMoular.setEnabled(true);
                            cartAdapter.updateData(cartInfo.getData());
                        }
                    } else {
                        ToastUtil.showShort(CartActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
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
                       setLoaddingView(false);
                   }
               });
            }
        });

    }

    //更改数量
    private void getUpdateNum(boolean isdd,int  p,int  pp) {
        setLoaddingView(true);
        Map<String, String> map = new HashMap<>();
        map.put("buyCartId", cartInfo.getData().get(p).getItems().get(pp).getCartId()+"");
        int nums = cartInfo.getData().get(p).getItems().get(pp).getNum();
        nums=isdd?nums+1:nums-1;
        map.put("number", nums+"");
        String str = JsonUtil.obj2String(map);
        String finalNum = nums+"";
        OkHttp3Utils.getInstance(ShopCart.BASE).doPostJson2(ShopCart.updateGoodsNum, str, (String) SPUtils.instance(this, 1).getkey("token", ""), new GsonObjectCallback<String>(ShopCart.BASE) {
            @Override
            public void onUi(String result) {
                setLoaddingView(false);
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        cartInfo.getData().get(p).getItems().get(pp).setNum(finalNum);
                        cartAdapter.notifyDataSetChanged();
                        showCommodityCalculation();
                    } else {
                        ToastUtil.showShort(CartActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
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
                       setLoaddingView(false);
                   }
               });
            }
        });

    }
}
