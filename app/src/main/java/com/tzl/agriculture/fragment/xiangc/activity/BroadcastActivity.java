package com.tzl.agriculture.fragment.xiangc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.comment.activity.HtmlForStoryActivity;
import com.tzl.agriculture.fragment.home.util.NewsAdapter;
import com.tzl.agriculture.model.XiangcMo;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.TextUtil;
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
import butterknife.ButterKnife;
import config.Article;
import okhttp3.Call;

/**
 * 播报页面
 */
public class BroadcastActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.recy)
    RecyclerView recyclerView;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.tv_search)
    TextView tvSearch;

    private NewsAdapter adapter;
    private List<XiangcMo.Article> mData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        tvSearch.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        adapter = new NewsAdapter<XiangcMo.Article>(this,mData) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position,XiangcMo.Article o,int mType) {
                if (mType == adapter.mOnePType){
                    holder.setText(R.id.tv_title,o.getTitle());
                    holder.setImageByUrl(R.id.iv_img,o.getCoverImgurl());
                    holder.setText(R.id.tv_dz, TextUtil.checkStr2Num(o.getGoodNum()));
                    holder.setText(R.id.tv_date,TextUtil.checkStr2Str(o.getCreateTime()));
                }else if (mType == adapter.mTowPType){
                    holder.setText(R.id.tv_title,o.getTitle());
                    holder.setImageByUrl(R.id.iv_img,o.getCoverImgurl());
                    holder.setText(R.id.tv_dz, TextUtil.checkStr2Num(o.getGoodNum()));
                    holder.setText(R.id.tv_date,TextUtil.checkStr2Str(o.getCreateTime()));
                }else {
                    holder.setText(R.id.tv_title,o.getTitle());
                    holder.setImageByUrl(R.id.iv_img1,o.getCoverImgurl());
                    holder.setImageByUrl(R.id.iv_img2,o.getCoverImgurl2());
                    holder.setImageByUrl(R.id.iv_img3,o.getCoverImgurl3());
                    holder.setText(R.id.tv_dz, TextUtil.checkStr2Num(o.getGoodNum()));
                    holder.setText(R.id.tv_date,TextUtil.checkStr2Str(o.getCreateTime()));
                }
            }
        };

        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                XiangcMo.Article data = mData.get(position);
                Intent intent = new Intent(BroadcastActivity.this, HtmlForStoryActivity.class);
                intent.putExtra("title",data.getTitle());
                intent.putExtra("userName",data.getUserNickname());
                intent.putExtra("date",data.getCreateTime());
                intent.putExtra("content",data.getContent());
                intent.putExtra("goodNum",data.getGoodNum());
                intent.putExtra("collectNum",data.getCollectNum());
                intent.putExtra("articleId",data.getArticleId());
                intent.putExtra("alreadyGood",data.getAlreadyGood());
                intent.putExtra("alreadyCollect",data.getAlreadyCollect());
                startActivity(intent);
            }
        });
    }

    private void initData() {
        Map<String,String> map = new HashMap<>();
        map.put("typeId",getIntent().getStringExtra("typeId"));//播报类型 6
        map.put("findChildren","0");//1查子分类
        map.put("category","0");//1查子分类
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivBack:
                finish();
                break;
            case R.id.tv_search:
                Intent intent = new Intent(this, ArticelSearchActivity.class);
                startActivity(intent);
                break;
                default:break;
        }
    }
}
