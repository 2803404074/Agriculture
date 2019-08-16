package com.tzl.agriculture.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.tzl.agriculture.R;
import com.tzl.agriculture.comment.activity.HtmlForXcActivity;
import com.tzl.agriculture.comment.activity.OrderHtmlActivity;
import com.tzl.agriculture.mall.activity.GoodsDetailsActivity;
import com.tzl.agriculture.model.BannerMo;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import cc.ibooker.zviewpagerlib.GeneralVpLayout;
import cc.ibooker.zviewpagerlib.Holder;
import cc.ibooker.zviewpagerlib.HolderCreator;
import cc.ibooker.zviewpagerlib.OnItemClickListener;
import config.Article;
import okhttp3.Call;

/**
 * 轮播图工具类
 */
public class BannerUtil<T> {

    private Context context;

    public BannerUtil() {
    }

    public BannerUtil(Context context) {
        this.context = context;
    }

    public void banner3(GeneralVpLayout generalVpLayout, List<T> datasx) {
        generalVpLayout.init(new HolderCreator<ImageViewHolder>() {
            @Override
            public ImageViewHolder createHolder() {
                return new ImageViewHolder();
            }
        }, datasx)
                // 设置指示器，第一个代表选中，第二个代表未选中
                /*.setPageIndicator(R.mipmap.test1, R.mipmap.test2)*/
                // 设置轮播停顿时间
                .setDuration(1500)
                // 指示器位置，居左、居中、居右
                .setPageIndicatorAlign(GeneralVpLayout.PageIndicatorAlign.CENTER_HORIZONTAL)
                // 设置指示器是否可见
//                .setPointViewVisible(true)
                // 设置ViewPager是否可以滚动
//                .setScrollble(true)
                // 点击事件监听
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClickListener(int position) {
                        BannerMo bannerMo = (BannerMo) datasx.get(position);

                        if (StringUtils.isEmpty(bannerMo.getLink())){
                            return;
                        }

                        if (bannerMo.getLinkType() == 2){
                            Intent intent = new Intent(context, OrderHtmlActivity.class);
                            intent.putExtra("html",bannerMo.getLink());
                            context.startActivity(intent);
                        }else if (bannerMo.getLinkType() == 0){
                            Intent intent = new Intent(context,GoodsDetailsActivity.class);
                            intent.putExtra("goodsId",bannerMo.getLink());
                            intent.putExtra("type",2);
                            context.startActivity(intent);
                        }else {//bannerMo.getLinkType() == 2
                            Intent intent = new Intent(context,HtmlForXcActivity.class);
                            intent.putExtra("articleId",bannerMo.getLink());
                            context.startActivity(intent);
                        }
                    }
                })
                // 是否开启无线 (true)

                // 开启轮播
                .start();
        // ViewPager状态改变监听
        generalVpLayout.setOnViewPagerChangeListener(new GeneralVpLayout.OnViewPagerChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //Toast.makeText(GeneralActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class ImageViewHolder implements Holder<BannerMo> {
        private ImageView imageView;
        private TextView tvTitle;

        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.banner_view, null, false);
            // 创建数据
            imageView = view.findViewById(R.id.iv_img);
            tvTitle = view.findViewById(R.id.tv_title);

            return view;
        }

        @Override
        public void UpdateUI(Context context, int position, BannerMo data) {
            // 加载数据
            Glide.with(context.getApplicationContext()).load(data.getUrl()).apply(
                    RequestOptions.bitmapTransform(
                            new RoundedCorners(20))).into(imageView);

            tvTitle.setText(data.getDescription());
        }
    }
}
