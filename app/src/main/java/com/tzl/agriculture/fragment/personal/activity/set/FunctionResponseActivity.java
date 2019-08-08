package com.tzl.agriculture.fragment.personal.activity.set;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseRecyclerHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import config.Article;
import config.User;
import okhttp3.Call;

/**
 * 功能反馈
 */
public class FunctionResponseActivity extends SetBaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_send)
    TextView tvSend;

    @BindView(R.id.et_content)
    EditText etContent;

    @BindView(R.id.recy_response)
    RecyclerView recyclerView;

    private BaseAdapter adapter;

    private List<String> mDate = new ArrayList<>();

    private String mType;

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_function_response;
    }

    @Override
    public void initView() {
        setTitle("功能反馈");
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));


        tvSend.setOnClickListener(this);
    }

    @Override
    public void initData() {
        String token = (String) SPUtils.instance(this,1).getkey("token","");
        OkHttp3Utils.getInstance(User.BASE).doPostJson2(User.getType, "", token, new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray array = object.optJSONArray("data");
                    for (int i=0;i<array.length();i++){
                        JSONObject object1 = (JSONObject) array.get(i);
                        mDate.add(object1.optString("dictValue"));
                    }
                    adapter = new BaseAdapter<String>(FunctionResponseActivity.this,recyclerView,mDate,R.layout.item_checkbox) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder, String o) {
                            holder.setText(R.id.cb_name,o);
                            holder.getView(R.id.cb_name).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mType = o;
                                }
                            });
                        }
                    };
                    recyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_send:
                send();
                break;
                default:break;
        }
    }

    private void send() {
        Map<String,String>map = new HashMap<>();
        map.put("content", TextUtil.checkStr2Str(etContent.getText().toString()));//内容
        //map.put("fullname","");//姓名
        //map.put("phone","");//手机号
        map.put("type",mType);//类型
        String str = JsonUtil.obj2String(map);
        String token = (String) SPUtils.instance(this,1).getkey("token","");
        OkHttp3Utils.getInstance(Article.BASE).doPostJson2(User.response, str, token, new GsonObjectCallback<String>(Article.BASE) {
            @Override
            public void onUi(String result) {
                ToastUtil.showShort(FunctionResponseActivity.this,"成功");
                finish();
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }
}
