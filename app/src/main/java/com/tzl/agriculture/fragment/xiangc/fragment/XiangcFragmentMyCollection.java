package com.tzl.agriculture.fragment.xiangc.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.comment.activity.HtmlForXcActivity;
import com.tzl.agriculture.model.XiangcMo;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseFragmentFromType;
import com.tzl.agriculture.view.BaseRecyclerHolder;

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
 * 我的乡愁，点赞、收藏、分享 页面数据
 */
public class XiangcFragmentMyCollection extends BaseFragmentFromType {

    private RecyclerView recyclerView;
    private BaseAdapter adapter;
    private List<XiangcMo.Article> mData = new ArrayList<>();
    @Override
    protected int initLayout() {
        return R.layout.recy_demo_w;
    }

    @Override
    protected void initView(View view) {
        recyclerView = view.findViewById(R.id.recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BaseAdapter<XiangcMo.Article>(getContext(),recyclerView,mData,R.layout.item_xiangc_colleciton) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, XiangcMo.Article o) {
                holder.setImageByUrl(R.id.iv_img,o.getCoverImgurl());
                holder.setText(R.id.tv_title,o.getTitle());
                holder.setText(R.id.tv_date,o.getCreateTime());
                holder.setText(R.id.tv_seeNum,o.getBrowseNum()+"人阅读");
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                XiangcMo.Article data = mData.get(position);
                Intent intent = new Intent(getContext(), HtmlForXcActivity.class);
                intent.putExtra("articleId",data.getArticleId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void setDate(boolean isLoad) {
        Map<String,String>map = new HashMap<>();
        map.put("optionType",getCtype());//0 点按，1收藏，2转发
        String str = JsonUtil.obj2String(map);
        String token = (String) SPUtils.instance(getContext(),1).getkey("token","");
        OkHttp3Utils.getInstance(Article.BASE).doPostJson2(Article.getOptionArticleList, str,token, new GsonObjectCallback<String>(Article.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0){
                        JSONObject dataObj = object.optJSONObject("data");
                        String str = dataObj.optString("articleInfoList");
                        mData = JsonUtil.string2Obj(str,List.class,XiangcMo.Article.class);
                        adapter.updateData(mData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }
}
