package com.tzl.agriculture.fragment.home.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.tzl.agriculture.R;
import com.tzl.agriculture.databinding.ActivityGoodsDetailsBinding;

/**
 * 商品详情统一页面
 */
public class GoodsDetailsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_goods_details);
    }
}
