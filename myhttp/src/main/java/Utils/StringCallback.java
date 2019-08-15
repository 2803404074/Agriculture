package Utils;

import android.os.Handler;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 类的用途 如果要将得到的json直接转化为集合 建议使用该类
 */

public abstract class StringCallback<T> implements Callback {
    private String BASE_PATH;

    public StringCallback(String BASE_PATH) {
        this.BASE_PATH = BASE_PATH;
    }

    private Handler handler = OkHttp3Utils.getInstance(BASE_PATH).getHandler();

    //主线程处理
    public abstract void onUi(T result);

    public abstract void onErrno(T result);

    //主线程处理
    public abstract void onFailed(Call call, IOException e);

    //请求失败
    @Override
    public void onFailure(final Call call, final IOException e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onFailed(call, e);
            }
        });
    }

    //请求json 并直接返回泛型的对象 主线程处理
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final String json = response.body().string();
//        Class<T> cls = null;
//
//        Class clz = this.getClass();
//        ParameterizedType type = (ParameterizedType) clz.getGenericSuperclass();
//        Type[] types = type.getActualTypeArguments();
//        cls = (Class<T>) types[0];
//        Gson gson = new Gson();
//        final T t = gson.fromJson(json, cls);
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject object = new JSONObject(json);
                    if (object.optInt("code") == 0){
                        JSONObject dataObj = object.optJSONObject("data");
                        onUi((T) dataObj);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}