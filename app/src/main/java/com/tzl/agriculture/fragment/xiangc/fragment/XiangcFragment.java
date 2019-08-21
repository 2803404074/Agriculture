package com.tzl.agriculture.fragment.xiangc.fragment;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tzl.agriculture.R;
import com.tzl.agriculture.comment.activity.HtmlForXcActivity;
import com.tzl.agriculture.fragment.xiangc.activity.ArticelSearchActivity;
import com.tzl.agriculture.fragment.xiangc.activity.XiangcMyActivity;
import com.tzl.agriculture.model.BannerMo;
import com.tzl.agriculture.model.XiangcMo;
import com.tzl.agriculture.util.BannerUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseFragment;
import com.tzl.agriculture.view.BaseRecyclerHolder;
import com.youth.banner.Banner;

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
import cc.ibooker.zviewpagerlib.GeneralVpLayout;
import cc.ibooker.zviewpagerlib.HolderCreator;
import cc.ibooker.zviewpagerlib.OnItemClickListener;
import config.Article;
import okhttp3.Call;
import okhttp3.Response;

public class XiangcFragment extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.nsv_view)
    NestedScrollView nestedScrollView;

    @BindView(R.id.iv_my)
    ImageView ivMy;

    @BindView(R.id.banner_xc)
    GeneralVpLayout banner;

    @BindView(R.id.refreshLayout)
    RefreshLayout mRefreshLayout;

    @BindView(R.id.home_recycler)
    RecyclerView xRecycler;

    @BindView(R.id.spin_kit)
    SpinKitView spinKitView;

    private BaseAdapter adapter;
    private List<XiangcMo> mData = new ArrayList<>();

    @BindView(R.id.ll_search)
    LinearLayout llSearch;

//    @BindView(R.id.tv_more)
//    TextView tvMore;

    private boolean isLoad = false;

    public static XiangcFragment getInstance() {
        XiangcFragment homeFragment = new XiangcFragment();
        return homeFragment;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_xiangc;
    }


    @Override
    protected void initView(View view) {
        ivMy.setOnClickListener(this);
        llSearch.setOnClickListener(this);
        //tvMore.setOnClickListener(this);

        mRefreshLayout.setEnableHeaderTranslationContent(true);//内容偏移
        mRefreshLayout.setPrimaryColorsId(android.R.color.transparent, android.R.color.white);//主题颜色
        xRecycler.setNestedScrollingEnabled(false);
        xRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        xRecycler.setItemAnimator(new DefaultItemAnimator());

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //refreshLayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                pageNum = 1;
                isLoad = false;
                initData();
            }
        });

        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                //refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });

        adapter = new BaseAdapter<XiangcMo>(getContext(), xRecycler, mData, R.layout.item_xiangc_title) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder,int position, XiangcMo o) {

                //文章类型标题
                holder.setText(R.id.tv_title, o.getTypeName());
                //文章类型简述
                holder.setText(R.id.tv_mess, "" + o.getTypeDesc());

                if (null == o.getArticleInfoList() || o.getArticleInfoList().size() == 0) {
                    holder.getView(R.id.tv_tips).setVisibility(View.VISIBLE);
                    holder.getView(R.id.recy_children).setVisibility(View.GONE);
                } else {
                    holder.getView(R.id.tv_tips).setVisibility(View.GONE);
                    //文章类型下的文章列表
                    RecyclerView recyclerView = holder.getView(R.id.recy_children);
                    if (recyclerView.getVisibility() == View.GONE)
                        recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setNestedScrollingEnabled(false);
                    GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
                    recyclerView.setLayoutManager(manager);
                    BaseAdapter adapterx = new BaseAdapter<XiangcMo.Article>(getContext(), recyclerView, o.getArticleInfoList(), R.layout.item_xiangc_children) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder,int position, XiangcMo.Article o) {
                            holder.setImageByUrl(R.id.iv_img, o.getCoverImgurl());
                            holder.setText(R.id.tv_title, "" + o.getTitle());
                            if (null != o.getArticleLocation()) {
                                holder.setText(R.id.tv_address, TextUtil.checkStr2Str(o.getArticleLocation().getProvinceStr()) + TextUtil.checkStr2Str(o.getArticleLocation().getCityStr()));
                            }
                        }
                    };
                    adapterx.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(getContext(), HtmlForXcActivity.class);
                            XiangcMo.Article xiangcMo = (XiangcMo.Article) adapterx.getData().get(position);
                            intent.putExtra("title", xiangcMo.getTitle());
                            intent.putExtra("content", xiangcMo.getContent());
                            intent.putExtra("articleId", xiangcMo.getArticleId());
                            intent.putExtra("date", xiangcMo.getCreateTime());
                            intent.putExtra("goodNum", xiangcMo.getGoodNum());
                            intent.putExtra("collectNum", xiangcMo.getCollectNum());

                            intent.putExtra("alreadyGood", xiangcMo.getAlreadyGood());

                            intent.putExtra("alreadyCollect", xiangcMo.getAlreadyCollect());
                            startActivity(intent);
                        }
                    });
                    recyclerView.setAdapter(adapterx);
                }

            }
        };
        xRecycler.setAdapter(adapter);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //判断是否滑到的底部
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    pageNum++;
                    initData();
                }
            }
        });

    }

    private void initBanner() {
        Map<String, String> map = new HashMap<>();
        map.put("positionCode", "xc_head");
        map.put("category", String.valueOf(1));
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Article.BASE).doPostJson(Article.banner, str,
                new GsonObjectCallback<String>(Article.BASE) {
                    //主线程处理
                    @Override
                    public void onUi(String msg) {
                        try {
                            JSONObject object = new JSONObject(msg);
                            int code = object.optInt("code");
                            if (code == 0) {//成功
                                JSONObject data = object.optJSONObject("data");
                                String array = data.optString("advertiseList");
                                List<BannerMo> mDate = JsonUtil.string2Obj(array, List.class, BannerMo.class);
                                BannerUtil bannerUtil = new BannerUtil(getActivity());
                                bannerUtil.banner3(banner,mDate);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //请求失败
                    @Override
                    public void onFailed(Call call, IOException e) {

                    }
                });


    }

    private int pageNum = 1;

    @Override
    protected void initData() {
        initBanner();

        Map<String, String> map = new HashMap<>();
        map.put("typeId", "1");
        map.put("findChildren", "1");
        map.put("pageNum", String.valueOf(pageNum));
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Article.BASE).doPostJson2(Article.getArticles, str, getToken(), new GsonObjectCallback<String>(Article.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    spinKitView.setVisibility(View.GONE);
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        JSONObject dataObject = object.optJSONObject("data");
                        String str = dataObject.optString("articleTypeList");
                        mData = JsonUtil.string2Obj(str, List.class, XiangcMo.class);

                        if (pageNum == 1) {
                            adapter.updateData(mData);
                        } else {
                            adapter.addAll(mData);
                        }
                    } else {
                        ToastUtil.showShort(getContext(), TextUtil.checkStr2Str(object.optString("msg")));
                    }
                    mRefreshLayout.finishRefresh();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                Log.e("AA", "异常" + e.getMessage());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                XiangcFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.finishRefresh();
                        spinKitView.setVisibility(View.GONE);
                        ToastUtil.showShort(getContext(), "无法连接服务器，请检查您的网络");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                super.onResponse(call, response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.finishRefresh();
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_my:
                Intent intent = new Intent(getContext(), XiangcMyActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_search:
                Intent intent2 = new Intent(getContext(), ArticelSearchActivity.class);
                startActivity(intent2);
                break;
//            case R.id.tv_more:
//                pageNum++;
//                isLoad = true;
//                initData();
//                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        spinKitView = null;
    }
}
