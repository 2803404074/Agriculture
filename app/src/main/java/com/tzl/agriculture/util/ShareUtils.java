package com.tzl.agriculture.util;

import android.app.Activity;
import android.content.Context;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class ShareUtils {
    private static ShareUtils shareUtils;
    private Context mContext;

    public ShareUtils(Context mContext) {
        this.mContext = mContext;
    }

    public static ShareUtils getInstance(Context mContext){
        if (shareUtils == null){
            shareUtils = new ShareUtils(mContext);
        }
        return shareUtils;
    }

    /**
     *
     * @param title  标题
     * @param text 内容
     * @param imgUrl 图片
     * @param linkUrl 连接
     */
    public void startShare(String title,String text,String imgUrl,String linkUrl){
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle(title);
        // titleUrl QQ和QQ空间跳转链接
        //oks.setTitleUrl("www.xcxky.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImageUrl(imgUrl);//确保SDcard下面存在此张图片
        // url在微信、微博，Facebook等平台中使用
        oks.setUrl(linkUrl);
        // comment是我对这条分享的评论，仅在人人网使用
        //oks.setComment("第一次分享文本");
        // 启动分享GUI
        oks.show(mContext);
    }
}
