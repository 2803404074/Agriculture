package com.tzl.agriculture.fragment.home.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shehuan.niv.NiceImageView;
import com.tzl.agriculture.R;
import com.tzl.agriculture.comment.activity.HtmlForStoryActivity;
import com.tzl.agriculture.comment.activity.HtmlForXcActivity;
import com.tzl.agriculture.fragment.home.activity.SearchActivity;
import com.tzl.agriculture.fragment.personal.activity.set.AddressActivity;
import com.tzl.agriculture.fragment.vip.activity.CouponActivity;
import com.tzl.agriculture.fragment.vip.activity.VipActivity;
import com.tzl.agriculture.fragment.xiangc.activity.BroadcastActivity;
import com.tzl.agriculture.mall.activity.ChinaGuActivity;
import com.tzl.agriculture.mall.activity.GoodsDetailsActivity;
import com.tzl.agriculture.mall.activity.LimitedTimeActivity;
import com.tzl.agriculture.fragment.xiangc.activity.StoryActivity;
import com.tzl.agriculture.fragment.home.util.HomeAdapter;
import com.tzl.agriculture.fragment.personal.login.activity.LoginActivity;
import com.tzl.agriculture.main.MainActivity;
import com.tzl.agriculture.mall.activity.OrderActivity;
import com.tzl.agriculture.model.BannerMo;
import com.tzl.agriculture.model.HomeMo;
import com.tzl.agriculture.model.UserInfo;
import com.tzl.agriculture.model.XiangcMo;
import com.tzl.agriculture.util.BannerUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.ScreenUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.util.UserData;
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
import butterknife.ButterKnife;
import config.App;
import config.Article;
import config.Base;
import okhttp3.Call;

public class HomeFragment extends BaseFragment {
    private RefreshLayout mRefreshLayout;
    private RecyclerView homeRecycler;
    private HomeAdapter homeAdapter;
    private Context mContext;

    @BindView(R.id.iv_tips_show)
    ImageView ivTips;

    @BindView(R.id.rl_search)
    RelativeLayout rlSearch;

    @BindView(R.id.spin_kit)
    SpinKitView spinKitView;

    private List<HomeMo> mData = new ArrayList<>();
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
        ButterKnife.bind(view);
        rlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        mContext = HomeFragment.this.getContext();
        mRefreshLayout = view.findViewById(R.id.h_refreshLayout);
        mRefreshLayout.setEnableHeaderTranslationContent(true);//内容偏移
        mRefreshLayout.setPrimaryColorsId(R.color.colorPrimaryDark, android.R.color.white);//主题颜色
        homeRecycler = view.findViewById(R.id.home_recycler);
        homeRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        //homeRecycler.addItemDecoration(new DividerItemDecoration(mContext, VERTICAL));
        homeRecycler.setItemAnimator(new DefaultItemAnimator());

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                mData.clear();
                setAdapter2();
                initData();
            }
        });

        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });
        setAdapter2();
    }

    private void setAdapter2(){
        homeAdapter = new HomeAdapter(mContext) {
            @Override
            public void convert(Context mContext, final BaseRecyclerHolder holder, int mType) {

                if (mType == homeAdapter.mTypeZero){
                    List<String> mTitle = new ArrayList<>();
                    List<String> mUrl = new ArrayList<>();
                    List<BannerMo> bannerMos = mData.get(mType).getAdvertiseList();
                    for (int i = 0; i <bannerMos.size() ; i++) {
                        mUrl.add(bannerMos.get(i).getUrl());
                        mTitle.add(bannerMos.get(i).getDescription());
                    }
                    Banner banner = holder.getView(R.id.banner);
                    BannerUtil.startBanner(banner,HomeFragment.this.getActivity(),mUrl,mTitle);
                }else if (mType == homeAdapter.mTypeOne){
                    TextView tvTitle = holder.getView(R.id.tv_title);
                    tvTitle.setText(mData.get(mType).getArticleList().getTypeName());
                    List<XiangcMo.Article> article =  mData.get(mType).getArticleList().getArticleInfoList();
                    if (null!=article && article.size()>0){
                        holder.setImageByUrl(R.id.iv_h_one,article.get(0).getCoverImgurl());
                        holder.setText(R.id.tv_story_title,article.get(0).getTitle());
                        //品牌故事跳转
                        tvTitle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(),StoryActivity.class);
                                intent.putExtra("typeId",mData.get(mType).getArticleList().getTypeId());
                                startActivity(intent);
                            }
                        });
                        //具体文章跳转
                        holder.getView(R.id.iv_h_one).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(),HtmlForStoryActivity.class);
                                intent.putExtra("articleId",article.get(0).getArticleId());
                                startActivity(intent);
                            }
                        });
                    }else {
                        //隐藏品牌故事
                        TextView textView = holder.getView(R.id.tv_title);
                        textView.setText("请添加该区域内容");
                        textView.setTextColor(getResources().getColor(R.color.colorAccent));
                    }

                }else if (mType == homeAdapter.mTypeTow){

                    //点击弹窗提示
                    holder.getView(R.id.ll_ptjx).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showTwo();
                        }
                    });
                    holder.getView(R.id.ll_tjyh).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showTwo();
                        }
                    });

                    //获取限时购模块数据（限时购、拼团、特价）
                    List<HomeMo.LimitGoods> goodsList = mData.get(mType).getGoodsTypeList();

                    //获取限时购数据
                    HomeMo.LimitGoods limitGoods = goodsList.get(0);
                    holder.setText(R.id.tv_xsg,limitGoods.getTypeName());
                    holder.setText(R.id.tv_xsg_tag,limitGoods.getTag());

                    if (limitGoods.getGoodsList() !=null && limitGoods.getGoodsList().size()>0){
                        holder.setImageByUrl(R.id.tv_xsg_01,limitGoods.getGoodsList().get(0).getPicUrl());
                        holder.getView(R.id.tv_xsg_01).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), GoodsDetailsActivity.class);
                                intent.putExtra("goodsId",limitGoods.getGoodsList().get(0).getGoodsId());
                                startActivity(intent);
                            }
                        });
                    }
                    if (limitGoods.getGoodsList() !=null && limitGoods.getGoodsList().size()>1){
                        holder.setImageByUrl(R.id.tv_xsg_02,limitGoods.getGoodsList().get(1).getPicUrl());
                        holder.getView(R.id.tv_xsg_02).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), GoodsDetailsActivity.class);
                                intent.putExtra("goodsId",limitGoods.getGoodsList().get(1).getGoodsId());
                                startActivity(intent);
                            }
                        });
                    }

                    //获取拼团数据
                    HomeMo.LimitGoods ptGoods = goodsList.get(1);
                    holder.setText(R.id.tv_pt_title,ptGoods.getTypeName());
                    holder.setText(R.id.tv_pt_tag,ptGoods.getTag());
                    if (null!=ptGoods.getGoodsList() && ptGoods.getGoodsList().size()>0){
                        holder.setImageByUrl(R.id.iv_pt_img,ptGoods.getGoodsList().get(0).getPicUrl());
                        holder.getView(R.id.iv_pt_img).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), GoodsDetailsActivity.class);
                                intent.putExtra("goodsId",ptGoods.getGoodsList().get(0).getGoodsId());
                                startActivity(intent);
                            }
                        });
                    }


                    //获取特价数据
                    HomeMo.LimitGoods tjGoods = goodsList.get(2);
                    holder.setText(R.id.tv_tj,tjGoods.getTypeName());
                    holder.setText(R.id.tv_tj_tag,tjGoods.getTag());
                    if (null!=tjGoods.getGoodsList() && tjGoods.getGoodsList().size()>0){
                        holder.setImageByUrl(R.id.iv_tj_img,tjGoods.getGoodsList().get(0).getPicUrl());
                        holder.getView(R.id.iv_tj_img).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), GoodsDetailsActivity.class);
                                intent.putExtra("goodsId",tjGoods.getGoodsList().get(0).getGoodsId());
                                startActivity(intent);
                            }
                        });
                    }

                    //限时购点击
                    holder.getView(R.id.ll_xsg).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(),LimitedTimeActivity.class);
                            startActivity(intent);
                        }
                    });


                }else  if (mType == homeAdapter.mTypeThree){
                    XiangcMo xiangcMo = mData.get(mType).getArticleList();
                    holder.setText(R.id.tv_title_bb, xiangcMo.getTypeName());
                    List<XiangcMo.Article> article = xiangcMo.getArticleInfoList();
                    TextView textView = holder.getView(R.id.tv_bro);
                    if (null!=article && article.size()>0){
                        textView.setText(article.get(0).getTitle());
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(),HtmlForXcActivity.class);
                                intent.putExtra("articleId",article.get(0).getArticleId());
                                startActivity(intent);
                            }
                        });
                    }else {
                        textView =holder.getView(R.id.tv_bro);
                        textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        textView.setText("请添加该区域的内容");
                    }

                    holder.getView(R.id.tv_news_more).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(),BroadcastActivity.class);
                            intent.putExtra("typeId",xiangcMo.getTypeId());
                            startActivity(intent);
                        }
                    });

                }else  if (mType == homeAdapter.mTypeFour){

                    holder.getView(R.id.iv_new).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showShareDialogNes();
                        }
                    });

                    holder.getView(R.id.iv_inv).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                           showShareDialog();
                        }
                    });

                }else  if (mType == homeAdapter.mTypeFive){//订单农业
                    holder.getView(R.id.iv_ddny).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showTwo();
                        }
                    });
                }else if (mType == homeAdapter.mTypeSix){//乡村好物模块

                    HomeMo.XchwGoods xchwModel = mData.get(mType).getXchwModel();

                   //模块总名称
                    holder.setText(R.id.tv_xchw_name,xchwModel.getModelName());
                    holder.setText(R.id.tv_xchw_tag,xchwModel.getModelTag());

                    holder.getView(R.id.tv_xchw_name).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(),LimitedTimeActivity.class);
                            intent.putExtra("mType",1);
                            startActivity(intent);
                        }
                    });


                    //发现好货
                    List<HomeMo.LimitGoods> goodsList = xchwModel.getGoodsTypeList();
                    holder.setText(R.id.tv_find,goodsList.get(0).getTypeName());
                    holder.setText(R.id.tv_mess01,goodsList.get(0).getTag());
                    if (null != goodsList.get(0).getGoodsList() && goodsList.get(0).getGoodsList().size()>0){
                        holder.setImageByUrl(R.id.iv_find_01,goodsList.get(0).getGoodsList().get(0).getPicUrl());
                        holder.getView(R.id.iv_find_01).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(),GoodsDetailsActivity.class);
                                intent.putExtra("goodsId",goodsList.get(0).getGoodsList().get(0).getGoodsId());
                                intent.putExtra("type",2);
                                startActivity(intent);
                            }
                        });
                    }

                    if (null != goodsList.get(0).getGoodsList() && goodsList.get(0).getGoodsList().size()>1){
                        holder.setImageByUrl(R.id.iv_find_02,goodsList.get(0).getGoodsList().get(1).getPicUrl());
                        holder.getView(R.id.iv_find_02).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(),GoodsDetailsActivity.class);
                                intent.putExtra("goodsId",goodsList.get(0).getGoodsList().get(1).getGoodsId());
                                intent.putExtra("type",2);
                                startActivity(intent);
                            }
                        });
                    }


                    //去旅游
                    List<HomeMo.LimitGoods> goodsList2 = xchwModel.getGoodsTypeList();
                    holder.setText(R.id.tv_find02,goodsList2.get(1).getTypeName());
                    holder.setText(R.id.tv_mess02,goodsList2.get(1).getTag());

                    List<HomeMo.LimitGoods.GoodsList> goo= goodsList2.get(1).getArticleList();
                    if (goo!=null && goo.size()>0){
                        holder.setImageByUrl(R.id.iv_qly_01,goo.get(0).getPicUrl());
                        ImageView iv01 = holder.getView(R.id.iv_qly_01);
                        iv01.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(),HtmlForXcActivity.class);
                                intent.putExtra("articleId",goo.get(0).getArticleId());
                                startActivity(intent);
                            }
                        });
                    }
                    if (goo!=null && goo.size()>1){
                        holder.setImageByUrl(R.id.iv_qly_02,goo.get(1).getPicUrl());
                        ImageView iv02 = holder.getView(R.id.iv_qly_02);
                        iv02.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(),HtmlForXcActivity.class);
                                intent.putExtra("articleId",goo.get(0).getArticleId());
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
                    holder.setText(R.id.tv_fpp_title,goodsList3.get(2).getTypeName());
                    holder.setText(R.id.tv_messfpt01,goodsList3.get(2).getTag());
                    List<HomeMo.LimitGoods.GoodsList> goo2= goodsList3.get(2).getGoodsList();
                    if (goo2!=null && goo2.size()>0){
                        holder.setImageByUrl(R.id.iv_fpp_01,goo2.get(0).getPicUrl());
                        holder.getView(R.id.iv_fpp_01).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(),GoodsDetailsActivity.class);
                                intent.putExtra("goodsId",goo2.get(0).getGoodsId());
                                startActivity(intent);
                            }
                        });
                    }
                    if (goo2!=null && goo2.size()>1){
                        holder.setImageByUrl(R.id.iv_fpp_02,goo2.get(1).getPicUrl());
                        holder.getView(R.id.iv_fpp_02).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(),GoodsDetailsActivity.class);
                                intent.putExtra("goodsId",goo2.get(1).getGoodsId());
                                startActivity(intent);
                            }
                        });
                    }


                    //中国馆---先屏蔽
                    holder.getView(R.id.rl_zgg).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showTwo();
                        }
                    });

//                    holder.getView(R.id.ll_zgg).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent intent = new Intent(getContext(), ChinaGuActivity.class);
//                            startActivity(intent);
//                        }
//                    });
                    List<HomeMo.LimitGoods> goodsList4 = xchwModel.getGoodsTypeList();
                    holder.setText(R.id.tv_f2_title,goodsList4.get(3).getTypeName());
                    holder.setText(R.id.tv_f2_mess,goodsList4.get(3).getTag());
                    List<HomeMo.LimitGoods.GoodsList> goo3= goodsList4.get(3).getGoodsList();
                    if (goo3!=null && goo3.size()>0){
                        holder.setImageByUrl(R.id.iv_zgg_01,goo3.get(0).getPicUrl());
                        holder.getView(R.id.iv_zgg_01).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                Intent intent = new Intent(getContext(),GoodsDetailsActivity.class);
//                                intent.putExtra("goodsId",goo3.get(0).getGoodsId());
//                                intent.putExtra("type",2);
//                                startActivity(intent);
                            }
                        });
                    }
                    if (goo3!=null && goo3.size()>1){
                        holder.setImageByUrl(R.id.iv_zgg_02,goo3.get(1).getPicUrl());
                        holder.getView(R.id.iv_zgg_02).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                Intent intent = new Intent(getContext(),GoodsDetailsActivity.class);
//                                intent.putExtra("goodsId",goo3.get(1).getGoodsId());
//                                intent.putExtra("type",2);
//                                startActivity(intent);
                            }
                        });
                    }

                }else if (mType == homeAdapter.mTypeSeven){//乡愁模块
                    recyclerViewXc = holder.getView(R.id.recycler_seven);
                    recyclerViewXc.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapterXc = new BaseAdapter<XiangcMo>(getContext(),recyclerViewXc,mData.get(mType).getArticleListXc(),R.layout.item_xiangc_title) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder, XiangcMo o) {
                            //文章类型标题
                            holder.setText(R.id.tv_title, o.getTypeName());
                            //文章类型简述
                            holder.setText(R.id.tv_mess, "" + o.getTypeDesc());

                            if (null == o.getArticleInfoList() || o.getArticleInfoList().size()==0){
                                holder.getView(R.id.tv_tips).setVisibility(View.VISIBLE);
                                holder.getView(R.id.recy_children).setVisibility(View.GONE);
                            }else {
                                holder.getView(R.id.tv_tips).setVisibility(View.GONE);
                                //文章类型下的文章列表
                                RecyclerView recyclerView = holder.getView(R.id.recy_children);
                                if (recyclerView.getVisibility() == View.GONE)recyclerView.setVisibility(View.VISIBLE);
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
        homeAdapter.setmData(mData);
        homeRecycler.setAdapter(homeAdapter);
    }

    private RecyclerView recyclerViewXc;
    private BaseAdapter adapterXc;
    @Override
    protected void initData() {
        Map<String,String>map = new HashMap<>();
        map.put("typeId","4");
        map.put("positionCode","home_head");
        map.put("category","0");
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Article.BASE).doPostJson2(Article.home, str, getToken(), new GsonObjectCallback<String>(Article.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code")==0){
                        ivTips.setVisibility(View.GONE);
                        String str = object.optString("data");
                        mData = JsonUtil.string2Obj(str,List.class, HomeMo.class);
                        if (mData!=null && mData.size()>0){
                            homeAdapter.update(mData);
                        }
                    }else if (object.optInt("code") == 502 || object.optInt("code") == 501 ){
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        if (null !=MainActivity.instance){
                            MainActivity.instance.finish();
                        }
                    }else {
                        ToastUtil.showShort(getContext(),TextUtil.checkStr2Str(object.optString("msg")));
                        ivTips.setVisibility(View.VISIBLE);
                    }
                    spinKitView.setVisibility(View.GONE);
                }catch (JSONException e) {
                    e.printStackTrace();
                    spinKitView.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailed(Call call, IOException e) {
                Log.e("httpOnFailed",e.getMessage());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("httpOnFailure",e.getMessage());
                HomeFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinKitView.setVisibility(View.GONE);
                        ivTips.setImageResource(R.mipmap.null_data);
                        ivTips.setVisibility(View.VISIBLE);
                    }
                });

            }
        });
    }


    //订单农业，扶贫厅 弹窗
    private void showTwo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setIcon(R.mipmap.application).setTitle("趣乡服务")
                .setMessage("敬请期待").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    /**
     * 新人礼包
     */
    private void showShareDialogNes() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.img_fragment,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).create();
        ImageView img = view.findViewById(R.id.hxxq_img);
        img.setImageResource(R.mipmap.share_news_lb);
        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(getContext())/4*3), LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 分享弹窗
     */
    private void showShareDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_share_app,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).create();
        UserInfo info = UserData.instance(getContext()).getUsreInfo();
        //背景
        RelativeLayout rlBg = view.findViewById(R.id.rl_bg);
        rlBg.setBackgroundResource(R.mipmap.share_news);
        //头像
        NiceImageView imageView = view.findViewById(R.id.nice_img);
        Glide.with(getContext()).load(info.getHeadUrl()).into(imageView);

        //昵称
        TextView tvName = view.findViewById(R.id.tv_name);
        tvName.setText(info.getNickname());

        //二维码
        ImageView img = view.findViewById(R.id.iv_img);

        //邀请码
        TextView tvCode = view.findViewById(R.id.tv_code);


        initDialogImg(img,tvCode);

        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.showShort(getContext(),"图片已保存至本地：c:/app/article/2019/07/25/agriculterapp.jpg");
            }
        });
        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(getContext())/4*3), LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void initDialogImg(ImageView imageView,TextView tvCode) {
        OkHttp3Utils.getInstance(App.BASE).doPostJson2(App.shareApp, "", getToken(), new GsonObjectCallback<String>(App.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0){
                        JSONObject dataObj = object.optJSONObject("data");
                        JSONObject shareObj = dataObj.optJSONObject("shareInfo");
                        Glide.with(getContext()).load(shareObj.optString("qrCodeUrl")).into(imageView);
                        tvCode.setText(shareObj.optString("inviteCode"));
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
