package com.tzl.agriculture.fragment.home.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tzl.agriculture.R;
import com.tzl.agriculture.comment.activity.HtmlForStoryActivity;
import com.tzl.agriculture.comment.activity.HtmlForXcActivity;
import com.tzl.agriculture.comment.activity.OrderHtmlActivity;
import com.tzl.agriculture.fragment.home.activity.SearchActivity;
import com.tzl.agriculture.fragment.home.util.HomeAdapter;
import com.tzl.agriculture.fragment.personal.login.activity.LoginActivity;
import com.tzl.agriculture.fragment.vip.activity.VipActivity;
import com.tzl.agriculture.fragment.xiangc.activity.BroadcastActivity;
import com.tzl.agriculture.fragment.xiangc.activity.StoryActivity;
import com.tzl.agriculture.main.MainActivity;
import com.tzl.agriculture.mall.activity.ChinaGuActivity;
import com.tzl.agriculture.mall.activity.GoodsDetailsActivity;
import com.tzl.agriculture.mall.activity.LimitedTimeActivity;
import com.tzl.agriculture.model.BannerMo;
import com.tzl.agriculture.model.HomeMo;
import com.tzl.agriculture.model.XiangcMo;
import com.tzl.agriculture.util.BannerUtil;
import com.tzl.agriculture.util.DialogUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseFragment;
import com.tzl.agriculture.view.BaseRecyclerHolder;
import com.tzl.agriculture.view.VerticalTextview;

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
import config.Article;
import okhttp3.Call;

public class HomeFragment extends BaseFragment {

    private HomeAdapter homeAdapter;

    @BindView(R.id.h_refreshLayout)
    RefreshLayout mRefreshLayout;

    @BindView(R.id.home_recycler)
    RecyclerView homeRecycler;

    @BindView(R.id.iv_tips_show)
    ImageView ivTips;

    @BindView(R.id.rl_search)
    RelativeLayout rlSearch;

    @BindView(R.id.spin_kit)
    SpinKitView spinKitView;

    private List<HomeMo> mData = new ArrayList<>();

    private MessageBroadcastReceiver receiver = new MessageBroadcastReceiver();

    public static HomeFragment getInstance() {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View view) {
        //注册广播
        registerBroadcast();

        //搜索
        rlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        mRefreshLayout.setEnableHeaderTranslationContent(true);//内容偏移
        mRefreshLayout.setPrimaryColorsId(R.color.colorPrimaryDark, android.R.color.white);//主题颜色
        homeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        homeRecycler.setItemAnimator(new DefaultItemAnimator());

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mData.clear();
                initData();
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                //refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });

        setAdapter();

        //跳转
        toJump();
    }

    private void toJump() {
        String main_type = (String) SPUtils.instance(getContext(), 1).getkey("main_type", "");
        String main_link = (String) SPUtils.instance(getContext(), 1).getkey("main_link", "");
        if (!TextUtils.isEmpty(main_type) && !TextUtils.isEmpty(main_link)) {
            ///0：商品，1：文章，2：外链
            switch (main_type) {
                case "0":
                    Intent intent = new Intent(getContext(), GoodsDetailsActivity.class);
                    intent.putExtra("goodsId",main_link);
                    intent.putExtra("type",2);
                    startActivity(intent);
                    break;
                case "1":
                    Intent intent1 = new Intent(getContext(), HtmlForXcActivity.class);
                    intent1.putExtra("articleId",main_link);
                    startActivity(intent1);
                    break;
                case "2":
                    Intent intent2=new Intent(getContext(), OrderHtmlActivity.class);
                    intent2.putExtra("html",main_link);
                    startActivity(intent2);
                    break;
            }
        }
    }

    //（播报）滚动文字
    private VerticalTextview tvBro;


    private void setAdapter() {
        homeAdapter = new HomeAdapter(getContext()) {
            @Override
            public void convert(Context mContext, final BaseRecyclerHolder holder, int mType) {

                if (homeAdapter.getViewTypeForMyTask(mType) == homeAdapter.mTypeZero) {
                    List<String> mTitle = new ArrayList<>();
                    List<String> mUrl = new ArrayList<>();
                    List<BannerMo> bannerMos = mData.get(mType).getAdvertiseList();
                    for (int i = 0; i < bannerMos.size(); i++) {
                        mUrl.add(bannerMos.get(i).getUrl());
                        mTitle.add(bannerMos.get(i).getDescription());
                    }
                    GeneralVpLayout<BannerMo> banner = holder.getView(R.id.banner);
                    BannerUtil util = new BannerUtil(context);
                    util.banner3(banner, bannerMos);

                } else if (homeAdapter.getViewTypeForMyTask(mType) == homeAdapter.mTypeOne) {
                    TextView tvTitle = holder.getView(R.id.tv_title);
                    tvTitle.setText(mData.get(mType).getArticleList().getTypeName());
                    TextUtil.setTextViewStyles(tvTitle);

                    List<XiangcMo.Article> article = mData.get(mType).getArticleList().getArticleInfoList();
                    if (null != article && article.size() > 0) {
                        holder.setImageByUrl(R.id.iv_h_one, article.get(0).getCoverImgurl());
                        holder.setText(R.id.tv_story_title, article.get(0).getTitle());
                        //品牌故事跳转
                        tvTitle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), StoryActivity.class);
                                intent.putExtra("typeId", mData.get(mType).getArticleList().getTypeId());
                                startActivity(intent);
                            }
                        });
                        //具体文章跳转
                        holder.getView(R.id.iv_h_one).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                Intent intent = new Intent(getContext(), HtmlForStoryActivity.class);
//                                intent.putExtra("articleId", article.get(0).getArticleId());
//                                startActivity(intent);
                                Intent intent = new Intent(getContext(), StoryActivity.class);
                                intent.putExtra("typeId", mData.get(mType).getArticleList().getTypeId());
                                startActivity(intent);
                            }
                        });
                    } else {
                        //隐藏品牌故事
                        TextView textView = holder.getView(R.id.tv_title);
                        textView.setText("请添加该区域内容");
                        textView.setTextColor(getResources().getColor(R.color.colorAccent));
                    }

                } else if (homeAdapter.getViewTypeForMyTask(mType) == homeAdapter.mTypeTow) {

                    //获取限时购模块数据（限时购、拼团、特价）
                    List<HomeMo.LimitGoods> goodsList = mData.get(mType).getGoodsTypeList();
                    //获取限时购数据
                    HomeMo.LimitGoods limitGoods = goodsList.get(0);
                    holder.setText(R.id.tv_xsg, limitGoods.getTypeName());
                    holder.setText(R.id.tv_xsg_tag, limitGoods.getTag());

                    RecyclerView recyclerView = holder.getView(R.id.recy_limit);
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
                    BaseAdapter adapter = new BaseAdapter<HomeMo.LimitGoods.GoodsList>(getContext(),
                            recyclerView, limitGoods.getGoodsList(), R.layout.img_goods_text) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder, HomeMo.LimitGoods.GoodsList o) {
                            holder.setImageByUrl(R.id.iv_img, o.getPicUrl());
                            holder.setText(R.id.tv_price, getResources().getString(R.string.app_money_home, o.getPrice()));
                        }
                    };
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(getContext(), LimitedTimeActivity.class);
                            startActivity(intent);
                        }
                    });

                    //限时购点击
                    holder.getView(R.id.ll_xsg).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), LimitedTimeActivity.class);
                            startActivity(intent);
                        }
                    });
                } else if (homeAdapter.getViewTypeForMyTask(mType) == homeAdapter.mTypeThree) {//播报
                    XiangcMo xiangcMo = mData.get(mType).getArticleList();
                    holder.setText(R.id.tv_title_bb, xiangcMo.getTypeName());
                    tvBro = holder.getView(R.id.tv_bro);

                    List<XiangcMo.Article> article = xiangcMo.getArticleInfoList();

                    ArrayList<String> textList = new ArrayList<>();
                    for (int i = 0; i < article.size(); i++) {
                        textList.add(article.get(i).getTitle());
                    }
                    tvBro.setTextList(textList);
                    tvBro.setText(15, 5, getResources().getColor(R.color.colorGri2));//设置属性
                    tvBro.setTextStillTime(5000);//设置停留时长间隔
                    tvBro.setAnimTime(500);//设置进入和退出的时间间隔
                    tvBro.startAutoScroll();

                    tvBro.setOnItemClickListener(new VerticalTextview.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Intent intent = new Intent(getContext(), HtmlForStoryActivity.class);
                            intent.putExtra("articleId", article.get(position).getArticleId());
                            startActivity(intent);
                        }
                    });

                    holder.getView(R.id.tv_news_more).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), BroadcastActivity.class);
                            intent.putExtra("typeId", xiangcMo.getTypeId());
                            startActivity(intent);
                        }
                    });

                } else if (homeAdapter.getViewTypeForMyTask(mType) == homeAdapter.mTypeFour) {

                    holder.getView(R.id.iv_new).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showShareDialogNes();
                        }
                    });

                    holder.getView(R.id.iv_inv).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), VipActivity.class);
                            startActivity(intent);
                        }
                    });

                } else if (homeAdapter.getViewTypeForMyTask(mType) == homeAdapter.mTypeFive) {//订单农业
                    holder.getView(R.id.iv_ddny).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showTwo();
                        }
                    });
                } else if (homeAdapter.getViewTypeForMyTask(mType) == homeAdapter.mTypeSix) {//乡村好物模块

                    HomeMo.XchwGoods xchwModel = mData.get(mType).getXchwModel();

                    //模块总名称
                    holder.setText(R.id.tv_xchw_name, xchwModel.getModelName());
                    holder.setText(R.id.tv_xchw_tag, xchwModel.getModelTag());

                    holder.getView(R.id.tv_xchw_name).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), LimitedTimeActivity.class);
                            intent.putExtra("mType", 1);
                            startActivity(intent);
                        }
                    });

                    holder.getView(R.id.rl_fxhw).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), LimitedTimeActivity.class);
                            intent.putExtra("mType", 1);
                            startActivity(intent);
                        }
                    });

                    //发现好货
                    List<HomeMo.LimitGoods> goodsList = xchwModel.getGoodsTypeList();
                    holder.setText(R.id.tv_find, goodsList.get(0).getTypeName());
                    holder.setText(R.id.tv_mess01, goodsList.get(0).getTag());
                    if (null != goodsList.get(0).getGoodsList() && goodsList.get(0).getGoodsList().size() > 0) {
                        holder.setImageByUrl(R.id.iv_find_01, goodsList.get(0).getGoodsList().get(0).getPicUrl());
                    }

                    if (null != goodsList.get(0).getGoodsList() && goodsList.get(0).getGoodsList().size() > 1) {
                        holder.setImageByUrl(R.id.iv_find_02, goodsList.get(0).getGoodsList().get(1).getPicUrl());
                    }

                    //去旅游
                    List<HomeMo.LimitGoods> goodsList2 = xchwModel.getGoodsTypeList();
                    holder.setText(R.id.tv_find02, goodsList2.get(1).getTypeName());
                    holder.setText(R.id.tv_mess02, goodsList2.get(1).getTag());

                    List<HomeMo.LimitGoods.GoodsList> goo = goodsList2.get(1).getArticleList();
                    if (goo != null && goo.size() > 0) {
                        holder.setImageByUrl(R.id.iv_qly_01, goo.get(0).getPicUrl());
                        ImageView iv01 = holder.getView(R.id.iv_qly_01);
                        iv01.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), HtmlForXcActivity.class);
                                intent.putExtra("articleId", goo.get(0).getArticleId());
                                startActivity(intent);
                            }
                        });
                    }
                    if (goo != null && goo.size() > 1) {
                        holder.setImageByUrl(R.id.iv_qly_02, goo.get(1).getPicUrl());
                        ImageView iv02 = holder.getView(R.id.iv_qly_02);
                        iv02.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), HtmlForXcActivity.class);
                                intent.putExtra("articleId", goo.get(1).getArticleId());
                                startActivity(intent);
                            }
                        });
                    }

                    //扶贫拼
                    holder.getView(R.id.rl_fpt).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showTwo();
                        }
                    });
                    List<HomeMo.LimitGoods> goodsList3 = xchwModel.getGoodsTypeList();
                    holder.setText(R.id.tv_fpp_title, goodsList3.get(2).getTypeName());
                    holder.setText(R.id.tv_messfpt01, goodsList3.get(2).getTag());
                    List<HomeMo.LimitGoods.GoodsList> goo2 = goodsList3.get(2).getGoodsList();
                    if (goo2 != null && goo2.size() > 0) {
                        holder.setImageByUrl(R.id.iv_fpp_01, goo2.get(0).getPicUrl());
                        holder.getView(R.id.iv_fpp_01).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), GoodsDetailsActivity.class);
                                intent.putExtra("goodsId", goo2.get(0).getGoodsId());
                                startActivity(intent);
                            }
                        });
                    }
                    if (goo2 != null && goo2.size() > 1) {
                        holder.setImageByUrl(R.id.iv_fpp_02, goo2.get(1).getPicUrl());
                        holder.getView(R.id.iv_fpp_02).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), GoodsDetailsActivity.class);
                                intent.putExtra("goodsId", goo2.get(1).getGoodsId());
                                startActivity(intent);
                            }
                        });
                    }

                    //中国馆
                    holder.getView(R.id.rl_zgg).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), ChinaGuActivity.class);
                            startActivity(intent);
                        }
                    });
                    List<HomeMo.LimitGoods> goodsList4 = xchwModel.getGoodsTypeList();
                    holder.setText(R.id.tv_f2_title, goodsList4.get(3).getTypeName());
                    holder.setText(R.id.tv_f2_mess, goodsList4.get(3).getTag());
                    List<HomeMo.LimitGoods.GoodsList> goo3 = goodsList4.get(3).getGoodsList();
                    if (goo3 != null && goo3.size() > 0) {
                        holder.setImageByUrl(R.id.iv_zgg_01, goo3.get(0).getPicUrl());
                    }
                    if (goo3 != null && goo3.size() > 1) {
                        holder.setImageByUrl(R.id.iv_zgg_02, goo3.get(1).getPicUrl());
                    }

                } else if (homeAdapter.getViewTypeForMyTask(mType) == homeAdapter.mTypeSeven) {//乡愁模块
                    recyclerViewXc = holder.getView(R.id.recycler_seven);
                    recyclerViewXc.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapterXc = new BaseAdapter<XiangcMo>(getContext(), recyclerViewXc, mData.get(mType).getArticleListXc(), R.layout.item_xiangc_title) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder, XiangcMo o) {
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
                                    public void convert(Context mContext, BaseRecyclerHolder holder, XiangcMo.Article o) {
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
                                        intent.putExtra("articleId", xiangcMo.getArticleId());
                                        startActivity(intent);
                                    }
                                });
                                recyclerView.setAdapter(adapterx);
                            }
                        }
                    };
                    recyclerViewXc.setAdapter(adapterXc);
                }
            }
        };
        List<HomeMo> dt = (List<HomeMo>) SPUtils.instance(getContext(), 1).getObjectByInput("home_data_index");
        if (dt != null && dt.size() > 0) {
            mData.addAll(dt);
        }
        homeAdapter.setmData(mData);
        homeRecycler.setAdapter(homeAdapter);
    }

    //乡愁模块的列表
    private RecyclerView recyclerViewXc;
    private BaseAdapter adapterXc;

    @Override
    protected void initData() {
        Map<String, String> map = new HashMap<>();
        map.put("typeId", "4");
        map.put("positionCode", "home_head");
        map.put("category", "0");
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Article.BASE).doPostJson2(Article.home, str, getToken(), new GsonObjectCallback<String>(Article.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        ivTips.setVisibility(View.GONE);
                        String str = object.optString("data");
                        mData = JsonUtil.string2Obj(str, List.class, HomeMo.class);

                        if (mData != null && mData.size() > 0) {
                            homeAdapter.update(mData);
                        }
                    } else if (object.optInt("code") == 502 || object.optInt("code") == 501) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        if (null != MainActivity.instance) {
                            MainActivity.instance.finish();
                        }
                    } else {
                        ToastUtil.showShort(getContext(), TextUtil.checkStr2Str(object.optString("msg")));
                        ivTips.setVisibility(View.VISIBLE);
                    }
                    spinKitView.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    spinKitView.setVisibility(View.GONE);
                }

                mRefreshLayout.finishRefresh();
            }

            @Override
            public void onFailed(Call call, IOException e) {
                Log.e("httpOnFailed", e.getMessage());
                HomeFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.finishRefresh();
                        spinKitView.setVisibility(View.GONE);
                        ivTips.setImageResource(R.mipmap.null_network);
                        ivTips.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("onFailure", e.getMessage());
                HomeFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.finishRefresh();
                        spinKitView.setVisibility(View.GONE);
                        ivTips.setImageResource(R.mipmap.null_network);
                        ivTips.setVisibility(View.VISIBLE);
                    }
                });

            }
        });
    }

    //订单农业，扶贫厅 弹窗
    private void showTwo() {
        DialogUtil.init(getContext()).showTips();
    }

    /**
     * 新人礼包
     */
    private void showShareDialogNes() {
        DialogUtil.init(getContext()).show(R.layout.dialog_img);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tvBro != null) {
            tvBro.startAutoScroll();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (tvBro != null) {
            tvBro.stopAutoScroll();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (tvBro != null) {
            tvBro.startAutoScroll();
        }
    }

    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter("android.intent.action.DOUBLE_ACTION");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, filter);
    }

    private void desBroadcast() {
        if (null == receiver) return;
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    @Override
    public void onDestroy() {
        desBroadcast();
        super.onDestroy();
    }

    private class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.DOUBLE_ACTION")) {
                homeRecycler.scrollToPosition(0);
                mRefreshLayout.setEnableRefresh(true);
            }
        }
    }

}
