package com.tzl.agriculture.util;

import android.content.Context;

import com.tzl.agriculture.baseresult.SPTAG;
import com.tzl.agriculture.model.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.User;
import okhttp3.Call;

public class UserData {

    protected static UserData userData;

    private Context context;
    protected UserInfo userInfo;

    public UserData(Context context) {
        this.context = context;
        this.userInfo = getUsreInfo();
    }
    public static UserData instance(Context context){
        if (null == userData){
            return new UserData(context.getApplicationContext());
        }
        return userData;
    }

    /**
     *  修改用户信息
     * @param token
     * @param index 1头像 3昵称 4性别 5出生日期
     * @param str
     */
    public  void updateUserInfo(String token,int index, String str) {
        switch (index){
            case 1:
                userInfo.setHeadUrl(str);
                break;
            case 3:
                if (userInfo.getNickname().equals(str))return;
                userInfo.setNickname(str);
                break;
            case 4:
                if (userInfo.getSex() == (Integer.parseInt(str)))return;
                userInfo.setSex(Integer.parseInt(str));
                break;
            case 5:
                userInfo.setBirthday(str);
                break;
                default:break;

        }
        String use = JsonUtil.obj2String(userInfo);
        OkHttp3Utils.getInstance(User.BASE).doPostJson2(User.saveInfo, use, token, new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }


    //获取用户信息
    public UserInfo getUsreInfo(){
        String str = (String) SPUtils.instance(context,1).getkey(SPTAG.USER,"");
        return JsonUtil.string2Obj(str,UserInfo.class);
    }
}
