package com.tzl.agriculture.util;

import android.content.Context;

import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class ShareUtils {
    private static ShareUtils shareUtils;
    private Context mContext;

    public ShareUtils(Context mContext) {
        this.mContext = mContext;
    }

    public static ShareUtils getInstance(Context mContext) {
        if (shareUtils == null) {
            shareUtils = new ShareUtils(mContext.getApplicationContext());
        }
        return shareUtils;
    }

    /**  mob自带的分享页面
     * @param title   标题
     * @param text    内容
     * @param imgUrl  图片
     * @param linkUrl 连接
     */
    public void startShare(String title, String text, String imgUrl, String linkUrl, PlatformActionListener listener) {
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

        PlatformActionListener platformActionListener = oks.getCallback();

        oks.setCallback(platformActionListener);

    }

    /**
     * @param title   标题
     * @param text    内容
     * @param imgUrl  图片
     * @param linkUrl 连接   微信分享
     */
    public void startShareWX(Context contextArp,String title, String text, String imgUrl, String linkUrl, PlatformActionListener listener) {
        OnekeyShare oks = new OnekeyShare();
        //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
        oks.setPlatform("Wechat");
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl(imgUrl);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(linkUrl);
        //启动分享
        oks.show(contextArp);
        oks.setCallback(listener);
    }

    /**
     * @param title   标题
     * @param text    内容
     * @param imgUrl  图片
     * @param linkUrl 连接   朋友圈分享
     */
    public void startShareWXcomtent(Context contextArp,String title, String text, String imgUrl, String linkUrl, PlatformActionListener listener) {
        OnekeyShare oks = new OnekeyShare();
        //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
        oks.setPlatform("WechatMoments");
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl(imgUrl);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(linkUrl);
        //启动分享
        oks.show(contextArp);
        oks.setCallback(listener);
    }
}
