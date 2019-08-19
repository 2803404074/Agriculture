package com.tzl.agriculture.application;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.tzl.agriculture.R;
import com.tzl.agriculture.baseresult.SPTAG;
import com.tzl.agriculture.comment.activity.HtmlForXcActivity;
import com.tzl.agriculture.fragment.vip.activity.VipActivity;
import com.tzl.agriculture.main.MainActivity;
import com.tzl.agriculture.mall.activity.GoodsDetailsActivity;
import com.tzl.agriculture.mall.activity.OrderDetailsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.tzl.agriculture.baseresult.SPTAG.articleType;
import static com.tzl.agriculture.baseresult.SPTAG.goodsType;
import static com.tzl.agriculture.baseresult.SPTAG.orderType;

public class JPushReceiver extends JPushMessageReceiver {
    private static final String TAG = "JIGUANG";

    /**
     * TODO 连接极光服务器
     */
    @Override
    public void onConnected(Context context, boolean b) {
        super.onConnected(context, b);
        Log.e(TAG, "onConnected");
    }

    /**
     * TODO 注册极光时的回调
     */
    @Override
    public void onRegister(Context context, String s) {
        super.onRegister(context, s);
        Log.e(TAG, "onRegister" + s);
    }

    /**
     * TODO 注册以及解除注册别名时回调
     */
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onAliasOperatorResult(context, jPushMessage);
        Log.e(TAG, jPushMessage.toString());
    }

    /**
     * TODO 接收到推送下来的通知
     *      可以利用附加字段（notificationMessage.notificationExtras）来区别Notication,指定不同的动作,附加字段是个json字符串
     *      通知（Notification），指在手机的通知栏（状态栏）上会显示的一条通知信息
     */
    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageArrived(context, notificationMessage);


        Log.e(TAG, notificationMessage.toString());
    }

    /**
     * TODO 打开了通知
     *      notificationMessage.notificationExtras(附加字段)的内容处理代码
     *      打开新的Activity， 打开一个网页等..
     */
    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage);
        try {
            JSONObject object = new JSONObject(notificationMessage.notificationExtras);
            int code = object.optInt("code");

            //是否在前台
            //是：直接跳转到相应的activity
            //否：判断应用是否
            if (isAppForeground(context)){
                //直接跳转到相应的activity
                Intent intent = null;
                if (code == articleType){
                    intent = new Intent(context, HtmlForXcActivity.class);
                    intent.putExtra("articleId",object.optString("typeId"));
                }else if (code == goodsType){
                    intent = new Intent(context, GoodsDetailsActivity.class);
                    intent.putExtra("goodsId",object.optString("typeId"));
                }else if (code == orderType){
                    intent = new Intent(context, OrderDetailsActivity.class);
                    intent.putExtra("orderId",object.optString("typeId"));
                }else {
                    intent = new Intent(context, VipActivity.class);
                }
                context.startActivity(intent);
            }else{
                Intent intent = new Intent(context,MainActivity.class);
                intent.putExtra("typeId",object.optString("typeId"));
                intent.putExtra("code",code);
                context.startActivity(intent);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * TODO 接收到推送下来的自定义消息
     *      自定义消息不是通知，默认不会被SDK展示到通知栏上，极光推送仅负责透传给SDK。其内容和展示形式完全由开发者自己定义。
     *      自定义消息主要用于应用的内部业务逻辑和特殊展示需求
     */
    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        super.onMessage(context, customMessage);
        Log.e(TAG, "onMessage");
        processCustomMessage(context, customMessage.extra);
    }

    /**
     *
     * @param context
     * @param message 消息内容
     */
    private void processCustomMessage(Context context, String message) {

        String channelID = "1";
        String channelName = "channel_name";

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        //适配安卓8.0的消息渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(context, channelID);

        notification.setAutoCancel(true)
                .setContentText(message)
                .setContentTitle("趣味乡村推送")
                .setSmallIcon(R.mipmap.application)
                .setDefaults(Notification.DEFAULT_ALL);

        notificationManager.notify((int)(System.currentTimeMillis()/1000), notification.build());
    }


    /**
     * 判断是否在前台
     * @return
     */
    private boolean isAppForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals("com.tzl.agriculture")) {
            return true;
        }
        return false;
    }
}
