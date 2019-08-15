package com.tzl.agriculture.mall.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.home.activity.SearchActivity;
import com.tzl.agriculture.fragment.home.fragment.GoodsFragmentPage;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.view.BaseActivity;
import com.tzl.agriculture.view.SimpleFragmentPagerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import config.Mall;
import okhttp3.Call;

/**
 * 限时购/发现好物 两个页面一样
 * 区别于限时购多一个布局
 * 因此根据跳转类型来确定是否添加head
 * 商品列表页面
 */
public class LimitedTimeActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.h_refreshLayout)
    RefreshLayout mRefreshLayout;

    @BindView(R.id.back)
    ImageView ivBack;

    @BindView(R.id.tv_startSearch)
    ImageView tvSearch;

    private SimpleFragmentPagerAdapter adapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private List<String> tabTitle = new ArrayList<String>();


    private  int mType;
    @Override
    public void initView() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mRefreshLayout.setEnableHeaderTranslationContent(true);//内容偏移
        mRefreshLayout.setPrimaryColorsId(R.color.colorPrimaryDark, android.R.color.white);//主题颜色
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });

        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });

        //获取跳转类型
        //0表示限时购(默认)，1表示发现好物，2其他
        mType = getIntent().getIntExtra("mType", 0);
        if (mType == 1) {
            tvTitle.setText("发现好物");
        }


        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LimitedTimeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.typeTopList, "",
                getToken(this), new GsonObjectCallback<String>(Mall.BASE) {
                    @Override
                    public void onUi(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.optInt("code") == 0) {

                                String str = object.optString("data");
                                List<GoodsTypeMo> mList = JsonUtil.string2Obj(str,List.class,GoodsTypeMo.class);

                                if (mList == null)return;

                                for (int i = 0; i < mList.size(); i++) {
                                    GoodsFragmentPage fragmentPage = new GoodsFragmentPage(mType);
                                    fragmentPage.setTabPos(i, mList.get(i).getShopTypeId());//第几页，和第几页的id
                                    mFragments.add(fragmentPage);
                                    tabTitle.add(mList.get(i).getName());
                                }
                                adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), mFragments, tabTitle);
                                viewPager.setAdapter(adapter);
                                ((GoodsFragmentPage) adapter.getItem(0)).sendMessage();
                                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                    @Override
                                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                    }

                                    @Override
                                    public void onPageSelected(int position) {
                                        //滑动监听加载数据，一次只加载一个标签页
                                        ((GoodsFragmentPage) adapter.getItem(position)).sendMessage();
                                    }

                                    @Override
                                    public void onPageScrollStateChanged(int state) {

                                    }
                                });

                                tabLayout.setupWithViewPager(viewPager);
                                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//超过长度可滑动

                            } else {
                                ToastUtil.showShort(LimitedTimeActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
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

    @Override
    public int setLayout() {
        return R.layout.activity_limited_time;
    }


    static class GoodsTypeMo{
        private String shopTypeId;
        private String name;

        public GoodsTypeMo() {
        }

        public String getShopTypeId() {
            return shopTypeId;
        }

        public String getName() {
            return name;
        }
    }
}
