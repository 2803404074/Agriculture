package com.tzl.agriculture.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

/**
 * 保存信息配置类
 * @author
 */
public class SPUtils {

    protected static SPUtils spUtils;

    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor editor;


    /**
     *
     * @param context
     * @param type 0 用户信息  ，1 其他信息
     */
    private SPUtils(Context context, int type) {
        String fileName = "";
        switch (type){
            case 0:fileName = "user_info";break;
            case 1:fileName = "other_info";break;
            default:fileName = "user_info";break;
        }
        sharedPreferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SPUtils instance(Context context, int type){
        if (null == spUtils){
            return new SPUtils(context.getApplicationContext(),type);
        }
        return spUtils;
    }

    /**
     * 存储
     */
    public synchronized void put(String key, Object object) {
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.commit();
    }

    /**
     * 获取保存的数据
     */
    public synchronized Object getkey(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return sharedPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sharedPreferences.getLong(key, (Long) defaultObject);
        } else {
            return sharedPreferences.getString(key, null);
        }
    }

    /**
     * 移除某个key值已经对应的值
     */
    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        editor.clear();
        editor.commit();
    }

    /**
     * 查询某个key是否存在
     */
    public Boolean contain(String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }


    /**
     * 存储对象----序列化 --私密数据
     */
    public String putObjectByInput(String key, Object obj) {
        if (obj == null) {//判断对象是否为空
            return "";
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = null;
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            // 将对象放到OutputStream中
            // 将对象转换成byte数组，并将其进行base64编码
            String objectStr = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            baos.close();
            oos.close();
            put(key, objectStr);
            return objectStr;
        } catch (Throwable t) {

        }
        return "";
    }

    /**
     * 获取对象---序列化---私密数据
     */
    public Object getObjectByInput(String key) {
        String wordBase64 = sharedPreferences.getString(key,"");
        // 将base64格式字符串还原成byte数组
        if (TextUtils.isEmpty(wordBase64)) {
            return null;
        }
        try {
            byte[] objBytes = Base64.decode(wordBase64.getBytes(), Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(objBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            // 将byte数组转换成product对象
            Object obj = ois.readObject();
            bais.close();
            ois.close();
            return obj;
        } catch (Throwable t) {

        }
        return null;
    }

}