package com.tzl.agriculture.view;

/**
 * 需要分享的activity
 */

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
/*import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;*/

public class SharePlatform extends Activity implements PlatformActionListener {

    private Context context;

    public SharePlatform(Context context){
        this.context=context;
        //ShareSDK.initSDK(this.context);
    }

    /**
     * 分享回调
     */
    @Override
    public void onCancel(Platform arg0, int arg1) {
        //回调的地方是子线程，进行UI操作要用handle处理
        handler.sendEmptyMessage(4);
    }

    @Override
    public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
        //回调的地方是子线程，进行UI操作要用handle处理
//        if (arg0.getName().equals(Wechat.NAME)) {
//            handler.sendEmptyMessage(1);
//        } else if (arg0.getName().equals(WechatMoments.NAME)) {
//            handler.sendEmptyMessage(2);
//        } else if (arg0.getName().equals(QQ.NAME)) {
//            handler.sendEmptyMessage(3);
//        }
    }

    @Override
    public void onError(Platform arg0, int arg1, Throwable arg2) {
        //回调的地方是子线程，进行UI操作要用handle处理
        arg2.printStackTrace();
        Message msg = new Message();
        msg.what = 5;
        msg.obj = arg2.getMessage();
        handler.sendMessage(msg);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(context, "微信分享成功", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(context, "朋友圈分享成功", Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Toast.makeText(context, "QQ分享成功", Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    Toast.makeText(context, "取消分享", Toast.LENGTH_LONG).show();
                    break;
                case 5:
                    Toast.makeText(context, "分享失败", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 进行分享
     *
     * @param shareType
     * @param shareTitle
     * @param shareText
     * @param shareImageUrl
     * @param shareUrl
     */
    public void share(String shareType, String shareTitle, String shareText, String shareImageUrl, String shareUrl) {
        Platform.ShareParams sharePlatform = new Platform.ShareParams();
        Platform platform;
        /**
         * 如果是微信朋友圈分享
//         */
//        if(shareType.equalsIgnoreCase(ShareConfig.WeChatMomentsShare)){
//            sharePlatform.setShareType(Platform.SHARE_WEBPAGE);
//
//            setSharePlatform(sharePlatform,shareTitle, shareText, shareImageUrl);
//
//            if(shareUrl!=null && !shareUrl.equalsIgnoreCase("")) {
//                sharePlatform.setUrl(shareUrl);
//            }
//
//            //platform = ShareSDK.getPlatform(WechatMoments.NAME);
//            platform.setPlatformActionListener(this);
//            platform.share(sharePlatform);
//        }
//        /**
//         * 如果是微信分享
//         */
//        else if(shareType.equalsIgnoreCase(ShareConfig.WeChatShare)){
//            sharePlatform.setShareType(Platform.SHARE_WEBPAGE);
//
//            setSharePlatform(sharePlatform, shareTitle, shareText, shareImageUrl);
//
//            if(shareUrl!=null && !shareUrl.equalsIgnoreCase("")) {
//                sharePlatform.setUrl(shareUrl);
//            }
//            platform = ShareSDK.getPlatform(Wechat.NAME);
//            platform.setPlatformActionListener(this);
//            platform.share(sharePlatform);
//        }
//        /**
//         * 如果是QQ分享
//         */
//        else {
//            setSharePlatform(sharePlatform, shareTitle, shareText, shareImageUrl);
//
//            if(shareUrl!=null && !shareUrl.equalsIgnoreCase("")) {
//                sharePlatform.setTitleUrl(shareUrl);
//            }
//
//            //platform = ShareSDK.getPlatform(QQ.NAME);
//            platform.setPlatformActionListener(this);
//            platform.share(sharePlatform);
//        }
    }


    private void setSharePlatform(Platform.ShareParams sharePlatform,String shareTitle, String shareText, String shareImageUrl){
        sharePlatform.setTitle(shareTitle);
        if(shareText!=null && !shareText.equalsIgnoreCase("")) {
            sharePlatform.setText(shareText);
        }
        if(shareImageUrl!=null && !shareImageUrl.equalsIgnoreCase("")) {
            sharePlatform.setImageUrl(shareImageUrl);
            sharePlatform.setImagePath(shareImageUrl);
        }
    }
}
