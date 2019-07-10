package com.tzl.agriculture.fragment.home.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.home.activity.BroadcastActivity;
import com.tzl.agriculture.fragment.home.activity.LimitedTimeActivity;
import com.tzl.agriculture.fragment.home.util.HomeAdapter;
import com.tzl.agriculture.view.BaseFragment;
import com.tzl.agriculture.view.BaseRecyclerHolder;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class HomeFragment extends BaseFragment {
    private RefreshLayout mRefreshLayout;
    private RecyclerView homeRecycler;
    private HomeAdapter homeAdapter;
    private Context mContext;

    public static HomeFragment getInstance() {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(view);
        mContext = HomeFragment.this.getContext();
        mRefreshLayout = view.findViewById(R.id.h_refreshLayout);
        mRefreshLayout.setEnableHeaderTranslationContent(true);//内容偏移
        mRefreshLayout.setPrimaryColorsId(R.color.colorPrimaryDark, android.R.color.white);//主题颜色
        homeRecycler = view.findViewById(R.id.home_recycler);
        homeRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        homeRecycler.addItemDecoration(new DividerItemDecoration(mContext, VERTICAL));
        homeRecycler.setItemAnimator(new DefaultItemAnimator());

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

        homeAdapter = new HomeAdapter(mContext) {
            @Override
            public void convert(Context mContext, final BaseRecyclerHolder holder, int mType) {
                Map<String,String>map = new HashMap<>();
                map.put("mess","哈哈");
                if (mType == homeAdapter.mTypeOne){
                    List<String> images = new ArrayList<>();
                    List<String> titles = new ArrayList<>();
                    images.add("http://img3.fengniao.com/forum/attachpics/913/114/36502745.jpg");
                    titles.add("凯道1");
                    images.add("http://imageprocess.yitos.net/images/public/20160910/99381473502384338.jpg");
                    titles.add("凯道2");
                    images.add("http://imageprocess.yitos.net/images/public/20160910/77991473496077677.jpg");
                    titles.add("凯道3");
                    images.add("http://imageprocess.yitos.net/images/public/20160906/1291473163104906.jpg");
                    titles.add("凯道4");

                    Banner banner = holder.getView(R.id.banner);
                    banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
                    banner.setIndicatorGravity(BannerConfig.CENTER);
                    banner.setImageLoader(new ImageLoader() {
                        @Override
                        public void displayImage(Context context, Object path, ImageView imageView) {
                            //设置图片圆角角度
                            //RoundedCorners roundedCorners= new RoundedCorners(6);
                            //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
                            //RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override(300, 300);

                            Glide.with(context)
                                    .load((String) path)
                                    .apply(RequestOptions.bitmapTransform(new RoundedCorners( 20)))
                                    .into(imageView);
                        }
                    });
                    banner.isAutoPlay(true);
                    banner.setDelayTime(3000);
                    banner.setImages(images);
                    banner.setBannerTitles(titles);
                    banner.setBannerAnimation(Transformer.DepthPage);
                    banner.start();

                    holder.getView(R.id.tv_xsg).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, LimitedTimeActivity.class);
                            startActivity(intent);
                        }
                    });

                    holder.getView(R.id.tv_news_more).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, BroadcastActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        };
        homeRecycler.setAdapter(homeAdapter);
    }

    @Override
    protected void initData(Context mContext) {

    }
}
