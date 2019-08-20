package com.tzl.agriculture.fragment.personal.activity.ccb.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.ccb.fragment.CollectionDepFragment;
import com.tzl.agriculture.fragment.personal.activity.ccb.fragment.CollectionGoodsFragment;
import com.tzl.agriculture.view.SimpleFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 商城模块的收藏
 * 更新
 */
public class CollectionGoodsActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView ivBack;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private SimpleFragmentPagerAdapter adapter;
    private CollectionGoodsFragment goodsFragment1;
    private CollectionDepFragment goodsFragment2;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private List<String> tabTitle = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_goods);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tabTitle.add("宝贝");
        tabTitle.add("店铺");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(goodsFragment1!=null){
                    boolean isFinish = goodsFragment1.onKeyDown(KeyEvent.KEYCODE_BACK, null);
                    goodsFragment1.isEdit = false;
                    if (isFinish){
                        finish();
                    }
                }else {
                    finish();
                }
            }
        });
        goodsFragment1 = new CollectionGoodsFragment();
        mFragments.add(goodsFragment1);
        goodsFragment2 = new CollectionDepFragment();
        mFragments.add(goodsFragment2);

        adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), mFragments, tabTitle);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//超过长度可滑动
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(goodsFragment1!=null){
            if (goodsFragment1.isEdit){
                boolean isFinish = goodsFragment1.onKeyDown(keyCode, event);
                goodsFragment1.isEdit = false;
                return !isFinish;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
