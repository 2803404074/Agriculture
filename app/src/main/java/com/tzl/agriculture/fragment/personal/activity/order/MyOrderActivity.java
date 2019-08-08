package com.tzl.agriculture.fragment.personal.activity.order;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.order.page.OrderFragmentPage;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
import com.tzl.agriculture.model.OrderStatusTitle;
import com.tzl.agriculture.view.SimpleFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 我的订单
 */
public class MyOrderActivity extends SetBaseActivity implements View.OnClickListener {

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.tv_search)
    TextView tvSearch;

    private SimpleFragmentPagerAdapter adapter;

    private List<Fragment> mFragments = new ArrayList<>();

    private List<String> tabTitle = new ArrayList<>();

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_my_order;
    }

    @Override
    public void initView() {
        setTitle("我的订单");
        tvSearch.setOnClickListener(this);

        for (int i = 0; i < OrderStatusTitle.getOrderStatus().size(); i++) {
            OrderFragmentPage fragmentPage = new OrderFragmentPage(getIntent().getIntExtra("position",0));
            fragmentPage.setTabPos(i, OrderStatusTitle.getOrderStatus().get(i).getId());//第几页，和第几页的id
            tabTitle.add(OrderStatusTitle.getOrderStatus().get(i).getName());
            mFragments.add(fragmentPage);

        }
        adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), mFragments, tabTitle);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(getIntent().getIntExtra("position",0));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //滑动监听加载数据，一次只加载一个标签页
                ((OrderFragmentPage) adapter.getItem(position)).sendMessage();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_search:
                Intent intent = new Intent(this,OrderSearchActivity.class);
                startActivity(intent);
                break;
                default:break;
        }
    }
}
