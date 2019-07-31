package com.tzl.agriculture.util;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.tzl.agriculture.model.BannerMo;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.Article;
import okhttp3.Call;

/**
 * 轮播图工具类
 */
public class BannerUtil {

    /**
     * @param tag home_head首页，xc_head乡愁
     * @param cId 0首页，1乡愁
     */
    public void starBanner(Activity activity ,Banner banner,String tag,int cId) {
        final List<String> title = new ArrayList<>();
        final List<String> img = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("positionCode", tag);
        map.put("category",String.valueOf(cId));
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Article.BASE).doPostJson(Article.banner, str,
                new GsonObjectCallback<String>(Article.BASE) {
                    //主线程处理
                    @Override
                    public void onUi(String msg) {
                        try {
                            JSONObject object = new JSONObject(msg);
                            int code = object.optInt("code");
                            if (code == 0) {//成功
                                JSONObject data = object.optJSONObject("data");
                                String array = data.optString("advertiseList");
                                List<BannerMo> mDate = JsonUtil.string2Obj(array, List.class, BannerMo.class);
                                if (null == mDate || mDate.size() == 0){
                                    title.add("");
                                    img.add("");
                                }else {
                                    for (int i=0;i<mDate.size();i++){
                                        title.add(mDate.get(i).getDescription());
                                        img.add(mDate.get(i).getUrl());
                                    }
                                }
                                banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
                                banner.setIndicatorGravity(BannerConfig.CENTER);
                                banner.setImageLoader(new ImageLoader() {
                                    @Override
                                    public void displayImage(Context context, Object path, ImageView imageView) {
                                        //设置图片圆角角度
                                        if (activity != null && !activity.isFinishing()){
                                            Glide.with(context)
                                                    .load((String) path)
                                                    .apply(RequestOptions.bitmapTransform(new RoundedCorners( 20)))
                                                    .into(imageView);
                                        }
                                    }
                                });
                                banner.isAutoPlay(true);
                                banner.setDelayTime(3000);
                                banner.setImages(img);
                                banner.setBannerTitles(title);
                                banner.setBannerAnimation(Transformer.DepthPage);
                                banner.start();

                            }//失败
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //请求失败
                    @Override
                    public void onFailed(Call call, IOException e) {

                    }
                });
    }


    public static void startBanner(Banner banner,Activity activity,List<String> img,List<String> title){
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                //设置图片圆角角度
                if (activity != null && !activity.isFinishing()){
                    Glide.with(context)
                            .load((String) path)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners( 20)))
                            .into(imageView);
                }
            }
        });
        banner.isAutoPlay(true);
        banner.setDelayTime(3000);
        banner.setImages(img);
        banner.setBannerTitles(title);
        banner.setBannerAnimation(Transformer.DepthPage);
        banner.start();
    }
}
