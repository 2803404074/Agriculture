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
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 购物车页面
 */
public class CartActivity extends AppCompatActivity {

    @BindView(R.id.recy)
    RecyclerView recyclerView;

    //管理按钮
    @BindView(R.id.tv_manage)
    TextView btnDelete;//tvManage

    //全选按钮
    @BindView(R.id.cbx_quanx_check)
    CheckBox checkBox;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartShoppMoular.setOnClickListener(new OnClickListener());
        checkBox.setOnClickListener(new OnClickListener());
        btnDelete.setOnClickListener(new OnClickListener());

        showData();
//        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.line_divider_inset));
//        recyclerView.addItemDecoration(itemDecoration);
        cartAdapter = new CartAdapter(this, cartInfo.getData());
        recyclerView.setAdapter(cartAdapter);
        showExpandData();
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
                showCommodityCalculation();
            }
        });

        /**
         * 计算价钱
         */
        cartAdapter.setOnItemMoneyClickListener(new OnItemMoneyClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showCommodityCalculation();
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

    private void showData() {
        Gson gson = new Gson();
        cartInfo = gson.fromJson(JSONDATA(),CartInfo.class);
    }

    private class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                //全选和不全选
                case R.id.cbx_quanx_check:
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
                    cartAdapter.removeChecked();
                    showCommodityCalculation();
                    break;
                case R.id.cart_shopp_moular:
                    if (num == 0){
                        ToastUtil.showShort(CartActivity.this,"您还没选宝贝呢~~~~");
                    }else {
                        List<CartMo> mCartMo=new ArrayList<>();
                        for (int i=0;i<cartInfo.getData().size();i++){
                            if (cartInfo.getData().get(i).ischeck()){
                                for (int j=0;j<cartInfo.getData().get(i).getItems().size();j++){
                                    mCartMo.add(new CartMo(cartInfo.getData().get(i).getShop_id(),String.valueOf(cartInfo.getData().get(i).getItems().get(j).getNum())));
                                }
                            }
                        }

                        ToastUtil.showShort(CartActivity.this,"提交"+ JsonUtil.obj2String(mCartMo));
                    }

                    //Toast.makeText(CartActivity.this,"提交订单:  "+cartMoney.getText().toString()+"元",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    public static final String JSONDATA() {
        return "{\"errcode\":0,\"errmsg\":\"success\",\"data\":[" +
                "{\"shop_id\":\"1636\",\"shop_name\":\"水城县阳光佳美食材经营部\",\"items\":[{\"itemid\":\"100189\",\"quantity\":\"1\",\"thumb\":\"https:\\/\\/cg.liaidi.com\\/data\\/attachment\\/image\\/thumb\\/2017\\/06\\/20170609105502145359.jpg\",\"image\":\"https:\\/\\/cg.liaidi.com\\/data\\/attachment\\/image\\/photo\\/2017\\/06\\/20170609105502145359.jpg\",\"price\":\"3.00\",\"title\":\"油菜一斤\"}]}," +
                "{\"shop_id\":\"1666\",\"shop_name\":\"水城县美食材经营部\",\"items\":[{\"itemid\":\"100189\",\"quantity\":\"1\",\"thumb\":\"https:\\/\\/cg.liaidi.com\\/data\\/attachment\\/image\\/thumb\\/2017\\/06\\/20170609105502145359.jpg\",\"image\":\"https:\\/\\/cg.liaidi.com\\/data\\/attachment\\/image\\/photo\\/2017\\/06\\/20170609105502145359.jpg\",\"price\":\"33.00\",\"title\":\"羊肉一斤\"}]}," +
                "{\"shop_id\":\"1669\",\"shop_name\":\"美食材经营部\",\"items\":[{\"itemid\":\"100189\",\"quantity\":\"1\",\"thumb\":\"https:\\/\\/cg.liaidi.com\\/data\\/attachment\\/image\\/thumb\\/2017\\/06\\/20170609105502145359.jpg\",\"image\":\"https:\\/\\/cg.liaidi.com\\/data\\/attachment\\/image\\/photo\\/2017\\/06\\/20170609105502145359.jpg\",\"price\":\"32.00\",\"title\":\"狗肉一斤\"}]}," +
                "{\"shop_id\":\"1778\",\"shop_name\":\"商品测试专卖店\",\"items\":[{\"itemid\":\"103677\",\"quantity\":\"2\",\"thumb\":\"https:\\/\\/cg.liaidi.com\\/data\\/attachment\\/image\\/thumb\\/2017\\/09\\/20170926150650687701.jpg\",\"image\":\"https:\\/\\/cg.liaidi.com\\/data\\/attachment\\/image\\/photo\\/2017\\/09\\/20170926150650687701.jpg\",\"price\":\"0.10\",\"title\":\"测试商品1\"},{\"itemid\":\"103629\",\"quantity\":\"1\",\"thumb\":\"https:\\/\\/cg.liaidi.com\\/data\\/attachment\\/image\\/thumb\\/2017\\/10\\/20171016134627837135.jpg\",\"image\":\"https:\\/\\/cg.liaidi.com\\/data\\/attachment\\/image\\/photo\\/2017\\/10\\/20171016134627837135.jpg\",\"price\":\"2.50\",\"title\":\"测试商品2\"},{\"itemid\":\"104015\",\"quantity\":\"1\",\"thumb\":\"https:\\/\\/cg.liaidi.com\\/data\\/attachment\\/image\\/thumb\\/2017\\/10\\/20171016135318646327.jpg\",\"image\":\"https:\\/\\/cg.liaidi.com\\/data\\/attachment\\/image\\/photo\\/2017\\/10\\/20171016135318646327.jpg\",\"price\":\"1.00\",\"title\":\"测试商品3\"}]}]}";
    }

    public static void main(String[] args) {
        System.out.println(JSONDATA());
    }

    class CartMo{
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
}
