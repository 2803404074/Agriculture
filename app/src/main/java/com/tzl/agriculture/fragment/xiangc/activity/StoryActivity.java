package com.tzl.agriculture.fragment.xiangc.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.comment.activity.HtmlForStoryActivity;
import com.tzl.agriculture.fragment.home.util.StoryAdapter;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
import com.tzl.agriculture.model.XiangcMo;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
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
import butterknife.BindView;
import config.Article;
import okhttp3.Call;

/**
 * 品牌故事页面
 */
public class StoryActivity extends SetBaseActivity {

    @BindView(R.id.recy)
    RecyclerView recyclerView;

    @BindView(R.id.tv_search)
    TextView tvSearch;
    private StoryAdapter adapter;
    private List<XiangcMo.Article> mData = new ArrayList<>();

    @Override
    public int setLayout() {
        return R.layout.activity_story;
    }

    @Override
    public void initView() {
        setTitle("品牌故事");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StoryAdapter<XiangcMo.Article>(this, mData) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int mType, XiangcMo.Article storyMo) {
                if (storyMo.getCoverImgurlSize() == adapter.mOnePType) {
                    holder.setText(R.id.tv_tag,storyMo.getUserNickname());
                    holder.setText(R.id.tv_date,storyMo.getCreateTime());
                    holder.setText(R.id.tv_title, storyMo.getTitle());
                    holder.setImageByUrl(R.id.iv_img, storyMo.getCoverImgurl());
                } else if (storyMo.getCoverImgurlSize() == adapter.mTowPType) {
                    holder.setText(R.id.tv_title, storyMo.getTitle());
                    holder.setImageByUrl(R.id.iv_img1, storyMo.getCoverImgurl());
                    holder.setImageByUrl(R.id.iv_img2, storyMo.getCoverImgurl2());
                } else {
                    holder.setText(R.id.tv_tag,storyMo.getUserNickname());
                    holder.setText(R.id.tv_date,storyMo.getCreateTime());
                    holder.setText(R.id.tv_title, storyMo.getTitle());
                    holder.setImageByUrl(R.id.iv_img1, storyMo.getCoverImgurl());
                    holder.setImageByUrl(R.id.iv_img2, storyMo.getCoverImgurl2());
                    holder.setImageByUrl(R.id.iv_img3, storyMo.getCoverImgurl3());
                }
            }
        };
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new StoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                XiangcMo.Article data = mData.get(position);
                Intent intent = new Intent(StoryActivity.this, HtmlForStoryActivity.class);
                intent.putExtra("articleId",data.getArticleId());
                startActivity(intent);
            }
        });

        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoryActivity.this, ArticelSearchActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        Map<String,String>map = new HashMap<>();
        map.put("typeId",getIntent().getStringExtra("typeId"));//品牌故事类型 4
        map.put("category","0");
        map.put("findChildren","0");
        String str  = JsonUtil.obj2String(map);
        String token = (String) SPUtils.instance(this,1).getkey("token","");
        OkHttp3Utils.getInstance(Article.BASE).doPostJson2(Article.getArticles, str,token, new GsonObjectCallback<String>(Article.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code")==0){
                        JSONObject dataObject = object.optJSONObject("data");
                        String str = dataObject.optString("articleTypeList");
                        List<XiangcMo> list = JsonUtil.string2Obj(str,List.class, XiangcMo.class);
                        XiangcMo xiangcMo = list.get(0);
                        mData = xiangcMo.getArticleInfoList();
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
