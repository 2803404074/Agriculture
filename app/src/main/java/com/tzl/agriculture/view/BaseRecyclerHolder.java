package com.tzl.agriculture.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.tzl.agriculture.R;

import org.apache.commons.lang.StringUtils;

/**
 * 万能RecyclerHolder
 */
public class BaseRecyclerHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> views;
    private Context context;
    private RequestOptions options;


    private BaseRecyclerHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        //指定一个初始为8
        views = new SparseArray<>(8);
    }

    /**
     * 取得一个RecyclerHolder对象
     *
     * @param context  上下文
     * @param itemView 子项
     * @return 返回一个RecyclerHolder对象
     */
    public static BaseRecyclerHolder getRecyclerHolder(Context context, View itemView) {
        return new BaseRecyclerHolder(context, itemView);
    }

    public SparseArray<View> getViews() {
        return this.views;
    }

    /**
     * 通过view的id获取对应的控件，如果没有则加入views中
     *
     * @param viewId 控件的id
     * @return 返回一个控件
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 设置字符串
     */
    public BaseRecyclerHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        if (!StringUtils.isEmpty(text) && !text.equals("null")) {
            tv.setText(text);
        }
        return this;
    }

    /**
     * 设置图片
     */
    public BaseRecyclerHolder setImageResource(int viewId, int drawableId) {
        ImageView iv = getView(viewId);
        iv.setImageResource(drawableId);
        return this;
    }

    /**
     * 设置图片
     */
    public BaseRecyclerHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView iv = getView(viewId);
        iv.setImageBitmap(bitmap);
        return this;
    }

    /**
     * 设置图片
     */
    public BaseRecyclerHolder setImageByUrl(int viewId, String url) {
        Glide.with(context.getApplicationContext()).load(url + "").apply(
                RequestOptions.bitmapTransform(
                        new RoundedCorners(10))).into((ImageView) getView(viewId));
        return this;
    }


    private RequestOptions getOptions() {
        RoundedCorners roundedCorners = new RoundedCorners(10);
        if (options != null) {
            return options;
        } else {
            options = new RequestOptions();
            options.placeholder(R.mipmap.application)//图片加载出来前，显示的图片
                    .fallback(R.mipmap.errno) //url为空的时候,显示的图片
                    .error(R.mipmap.errno);//图片加载失败后，显示的图片
            options.bitmapTransform(roundedCorners);
            return options;
        }
    }

}
