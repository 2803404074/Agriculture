package com.tzl.agriculture.fragment.personal.activity.function.activity;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
import com.tzl.agriculture.model.CommentMo;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseRecyclerHolder;
import com.tzl.agriculture.view.onLoadMoreListener;

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
import config.Mall;
import okhttp3.Call;

public class MyCommentActivity extends SetBaseActivity {

    private int type;//1商品评论，0我的评论

    @BindView(R.id.iv_tips)
    ImageView ivTips;

    @BindView(R.id.recy_comment)
    RecyclerView recyclerView;


    private BaseAdapter adapter;
    private List<CommentMo> mData = new ArrayList<>();


    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_my_comment;
    }

    @Override
    public void initView() {
        type = getIntent().getIntExtra("type",0);
        //1 商品的评论列表    0 我的评论列表
        if (type == 1){
            setTitle("评论");
        }else {
            setTitle("我的评论");
        }

        setAndroidNativeLightStatusBar(true);
        setStatusColor(R.color.colorGri);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseAdapter<CommentMo>(this,recyclerView,mData,R.layout.item_my_comment) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder,int position, CommentMo o) {

                SimpleDraweeView draweeView = holder.getView(R.id.drawee_img);
                draweeView.setImageURI(o.getHeadUrl());

                holder.setText(R.id.tv_name,o.getNickname());
                holder.setText(R.id.tv_content,o.getContent());
                holder.setText(R.id.tv_date,o.getCreateTime());

                if (type == 1){
                    holder.getView(R.id.rl_gooods).setVisibility(View.GONE);
                }else {
                    holder.getView(R.id.rl_gooods).setVisibility(View.VISIBLE);
                    holder.setImageByUrl(R.id.iv_goods_img,o.getGoods().getPicUrl());
                    holder.setText(R.id.tv_goodsName,o.getGoods().getGoodsName());
                    holder.setText(R.id.tv_price,o.getGoods().getFinalAmount());
                }

                RecyclerView recySun = holder.getView(R.id.recy_sun);
                recySun.setLayoutManager(new GridLayoutManager(MyCommentActivity.this,3));
                BaseAdapter adapterx = new BaseAdapter<String>(MyCommentActivity.this,recySun,o.getImgList(),R.layout.img_fragment) {
                    @Override
                    public void convert(Context mContext, BaseRecyclerHolder holder, int position,String o) {
                        holder.setImageByUrl(R.id.hxxq_img,o);
                    }
                };
                recySun.setAdapter(adapterx);
            }
        };
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new onLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                page++;
                initData();
            }
        });
    }

    private int page = 1;
    @Override
    public void initData() {
        String utl = "";
        Map<String,String>map = new HashMap<>();
        if (type == 1){
            utl = Mall.appCommentList;
            map.put("goodsId",getIntent().getStringExtra("goodsId"));
        }else {
            utl = Mall.myCommentList;
        }
        map.put("pageNum",String.valueOf(page));
        String str = JsonUtil.obj2String(map);
        String token = (String) SPUtils.instance(this,1).getkey("token","");
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(utl, str, token, new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                System.out.println("result = [" + result + "]");
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0){
                        JSONObject dataObj = object.optJSONObject("data");
                        String str = dataObj.optString("commentList");
                        List<CommentMo>list = JsonUtil.string2Obj(str,List.class,CommentMo.class);
                        if (list!=null && list.size()>0){
                            if (page == 1){
                                mData = list;
                                adapter.updateData(mData);
                            }else {
                                adapter.addAll(list);
                            }
                        }else {
                            if (adapter.getData() == null || adapter.getData().size() == 0){
                                ivTips.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } catch (JSONException e) {
                    System.out.println("result = [" + result + "]");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                System.out.println("call = [" + call + "], e = [" + e + "]");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivTips.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("call = [" + call + "], e = [" + e + "]");
                super.onFailure(call, e);runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivTips.setVisibility(View.VISIBLE);
                    }
                });

            }
        });
    }
}
