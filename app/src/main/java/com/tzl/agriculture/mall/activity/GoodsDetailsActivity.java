package com.tzl.agriculture.mall.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.ybq.android.spinkit.SpinKitView;
import com.rey.material.app.BottomSheetDialog;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.function.activity.MyCommentActivity;
import com.tzl.agriculture.fragment.personal.login.activity.LoginActivity;
import com.tzl.agriculture.model.GoodsDetailsMo;
import com.tzl.agriculture.util.CountDownUtil;
import com.tzl.agriculture.util.DateUtil;
import com.tzl.agriculture.util.HtmlStyleUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.MyWebViewClient;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.ScreenUtils;
import com.tzl.agriculture.util.ShareUtils;
import com.tzl.agriculture.util.ShowButtonLayoutData;
import com.tzl.agriculture.util.StatusBarUtil;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.view.BaseActivity;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseRecyclerHolder;
import com.tzl.agriculture.view.ShoppingSelectView;
import com.tzl.agriculture.view.ShowButtonLayout;
import com.tzl.agriculture.view.ViewPagerTransform;

import org.apache.commons.lang.StringUtils;
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
import butterknife.OnClick;
import config.Article;
import config.Mall;
import config.ShopCart;
import okhttp3.Call;

/**
 * 商品详情统一页面
 */
public class GoodsDetailsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_share)
    ImageView ivShare;

    @BindView(R.id.viewpagerTransform)
    ViewPagerTransform viewPager;

    @BindView(R.id.iv_back)
    ImageView ivBack;

    @BindView(R.id.tv_price)
    TextView tvPrice;

    @BindView(R.id.tv_marketPrice)
    TextView tvMarketPrice;

    @BindView(R.id.tv_date)
    TextView tvDate;

    @BindView(R.id.tv_name)
    TextView tvGoodsName;


    @BindView(R.id.tv_address)
    TextView tvAddress;

    //保障简述
    @BindView(R.id.tv_bz)
    TextView tvBz;

    //保障按键区域
    @BindView(R.id.ll_server)
    LinearLayout llServer;

    //参数
    @BindView(R.id.ll_cs)
    LinearLayout llCs;

    @BindView(R.id.tv_commentNum)
    TextView tvCommentNum;

    //------商家
    @BindView(R.id.iv_dep_head)
    ImageView ivDepHead;

    //进店逛逛
//    @BindView(R.id.tv_dep_go)
//    TextView tvDepGo;

    @BindView(R.id.tv_dep_name)
    TextView tvDepName;

    @BindView(R.id.web_view)
    WebView webView;

    //屏蔽购物车
    @BindView(R.id.tv_addcart)
    TextView tvAddCart;

    @BindView(R.id.tv_buy)
    TextView tvBuy;

    @BindView(R.id.bt_collect)
    ShineButton btCollect;

    private boolean isCollect;  //是否已经收藏

    @BindView(R.id.ll_comment)
    LinearLayout llComment;//评论数量区域内容

    @BindView(R.id.rl_comment)
    RelativeLayout rlComment;//评论内容区域控件

    @BindView(R.id.tv_commentTips)
    TextView tvCommentTips;//提示暂无评论

    @BindView(R.id.iv_head)
    ImageView ivHead;

    @BindView(R.id.tv_comName)
    TextView tvComName;

    @BindView(R.id.tv_comMess)
    TextView tvComMess;

    @BindView(R.id.tv_dh)
    TextView tvDh;

    @BindView(R.id.ll_deatils_menu)
    LinearLayout llDeatilsMenu;

    @BindView(R.id.ll_dateDown)
    LinearLayout llDateDown;//倒计时视图，只有限时购显示

    @BindView(R.id.tv_yf)
    TextView tvYf;//邮费

    @BindView(R.id.text_collect)
    TextView mTextViewCollect;


    @BindView(R.id.tv_salesNum)
    TextView tvSalesNum;

    private boolean isAddCart; //true 加入  false 立即购买

    @BindView(R.id.labelLayout)
    ShowButtonLayout labelLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SPUtils.instance(this, 1).remove("main_type");
        SPUtils.instance(this, 1).remove("main_link");
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }


    @Override
    public int setLayout() {
        return R.layout.activity_goods_details;
    }

    @Override
    public void initView() {

        //本activity需要沉浸式
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);

        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.colorW));
        }

        if (getIntent().getIntExtra("type", 0) == 1) {
            tvDh.setVisibility(View.VISIBLE);
            llDeatilsMenu.setVisibility(View.GONE);
            tvDh.setOnClickListener(this);
        } else {
            tvDh.setVisibility(View.GONE);
            llDeatilsMenu.setVisibility(View.VISIBLE);

        }


        btCollect.init(this); //收藏
        btCollect.setEnabled(false);
        btCollect.setClickable(false);
        ivBack.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        tvAddCart.setOnClickListener(this);
        llCs.setOnClickListener(this);
        tvBuy.setOnClickListener(this);
        llServer.setOnClickListener(this);
        llComment.setOnClickListener(this);

    }


    @OnClick({R.id.ll_collect})
    public void onTextClick(View view) {
        collection();
    }


    @Override
    public void initData() {

        Map<String, String> map = new HashMap<>();
        map.put("goodsId", getIntent().getStringExtra("goodsId"));
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.goodsInfo,
                str, getToken(this), new GsonObjectCallback<String>(Mall.BASE) {
                    @Override
                    public void onUi(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.optInt("code") == 0) {
                                String str = object.optString("data");
                                goodsDetailsMo = JsonUtil.string2Obj(str, GoodsDetailsMo.class);
                                setMess();
                            } else if (object.optInt("code") == 0) {
                                Intent intent = new Intent(GoodsDetailsActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                ToastUtil.showShort(GoodsDetailsActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
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

    private GoodsDetailsMo goodsDetailsMo;

    private void setMess() {
        //0限时购入口
        //1会员兑换入口
        //2普通商品和开通会员入口
        int type = getIntent().getIntExtra("type", 0);

        if (type == 0 || !StringUtils.isEmpty(goodsDetailsMo.getGoods().getSpikeEndTime())) {//限时购  -- 隐藏兑换，显示倒计时,显示购买
            int isSpike = goodsDetailsMo.getGoods().getIsSpike();
            if (isSpike == 0) {
                llDateDown.setVisibility(View.VISIBLE);
            } else {
                llDateDown.setVisibility(View.GONE);
            }
            //倒计时
            dowTime(goodsDetailsMo.getGoods().getSpikeEndTime());
            tvDh.setVisibility(View.GONE);
            llDeatilsMenu.setVisibility(View.VISIBLE);

        } else if (type == 1) {//会员兑换  -- 隐藏倒计时，显示兑换,隐藏购买
            tvDh.setVisibility(View.VISIBLE);//兑换视图
            //llDeatilsMenu.setVisibility(View.GONE);//购买视图
            btCollect.setVisibility(View.GONE);
            tvBuy.setVisibility(View.GONE);
            llDateDown.setVisibility(View.GONE);//倒计时视图
            tvDh.setOnClickListener(this);
        } else {//普通、开会员     -- 隐藏倒计时，隐藏兑换，显示购买
            tvDh.setVisibility(View.GONE);
            llDateDown.setVisibility(View.GONE);
            llDeatilsMenu.setVisibility(View.VISIBLE);
        }

        //是否已收藏该商品
        isCollect = !StringUtils.isEmpty(goodsDetailsMo.getGoods().getAlreadyCollect()) &&
                goodsDetailsMo.getGoods().getAlreadyCollect().equals("true");
        setCollect(isCollect, false);

        tvMarketPrice.setText(TextUtil.checkStr2Str(goodsDetailsMo.getGoods().getOriginalPrice()));
        tvMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        tvPrice.setText(TextUtil.checkStr2Str(goodsDetailsMo.getGoods().getPrice()));
        tvGoodsName.setText(TextUtil.checkStr2Str(goodsDetailsMo.getGoods().getGoodsName()));
        tvAddress.setText(TextUtil.checkStr2Str(goodsDetailsMo.getGoods().getShipAddress()));
        tvBz.setText(TextUtil.checkStr2Str(goodsDetailsMo.getGoodsServicesStr()));

        //标签
        ShowButtonLayoutData showButtonLayoutData = new ShowButtonLayoutData<String>(getContext(), labelLayout, goodsDetailsMo.getGoods().getGoodsLabelList(), null);
        showButtonLayoutData.setView(R.layout.text_view_red);
        showButtonLayoutData.setData();

        //销量
        tvSalesNum.setText("月销量" + TextUtil.checkStr2Num(goodsDetailsMo.getGoods().getSalesVolume()));

        //邮费
        String yfStr = TextUtil.checkStr2Str(goodsDetailsMo.getGoods().getFreeShipping());
        tvYf.setText(yfStr.equals("0") ? "包邮" : getString(R.string.app_money) + yfStr);

        //评论数量
        tvCommentNum.setText(TextUtil.checkStr2Num(goodsDetailsMo.getGoods().getCommentNum()));

        //评论头像
        if (goodsDetailsMo.getGoodsCommentList() != null && goodsDetailsMo.getGoodsCommentList().size() > 0) {
            Glide.with(getApplicationContext()).load(goodsDetailsMo.getGoodsCommentList().get(0).getHeadUrl()).into(ivHead);
            tvComName.setText(goodsDetailsMo.getGoodsCommentList().get(0).getNickname());
            tvComMess.setText(goodsDetailsMo.getGoodsCommentList().get(0).getContent());

            rlComment.setVisibility(View.VISIBLE);
            tvCommentTips.setVisibility(View.GONE);
        } else {
            rlComment.setVisibility(View.GONE);
            tvCommentTips.setVisibility(View.VISIBLE);
        }

        //评论用户名
        //评论内容
        //店铺logo
        if (!GoodsDetailsActivity.this.isFinishing()) {
            Glide.with(getApplicationContext()).load(goodsDetailsMo.getGoods().getLogoIcon()).into(ivDepHead);

        }
        //店铺名称
        tvDepName.setText(goodsDetailsMo.getGoods().getName());

        //商品详情
        setWebView(goodsDetailsMo.getGoods().getDetail());

        //轮播图
        if (goodsDetailsMo.getGoods().getGallerys() != null && goodsDetailsMo.getGoods().getGallerys().size() > 0) {
            setViewPagerAdapter(goodsDetailsMo.getGoods().getGallerys());
        }
    }

    //修改收藏颜色大小
    private void setCollect(boolean isCollect, boolean isClick) {
        System.out.println("isCollect = [" + isCollect + "]");
        btCollect.setChecked(isCollect, isClick);
        mTextViewCollect.setTextColor(ContextCompat.getColor(GoodsDetailsActivity.this, isCollect ? R.color.colorOrange : R.color.colorHs2));
    }

    private volatile List<ImageView> imgList = new ArrayList<>();

    //图片轮播适配
    private void setViewPagerAdapter(final List<String> listStr) {

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return listStr.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                View view = null;
                String img = listStr.get(position);
                if (!StringUtils.isEmpty(img)) {
                    view = View.inflate(container.getContext(), R.layout.img_fragment, null);
                    final ImageView iv = view.findViewById(R.id.hxxq_img);
                    imgList.add((ImageView) view.findViewById(R.id.hxxq_img));
                    Glide.with(getApplicationContext()).asBitmap()
                            .load(img)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap bitmap,
                                                            @Nullable Transition<? super Bitmap> transition) {
                                    iv.setImageBitmap(bitmap);
                                }
                            });
                    container.addView(view);
                }
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
    }

    //商品详情
    public void setWebView(String html) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 不使用缓存
        webView.getSettings().setUserAgentString(System.getProperty("http.agent"));
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//把html中的内容放大webview等宽的一列中
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new MyWebViewClient(webView));
        //webView.getSettings().setDefaultZoom(HtmlStyleUtil.getZoomDensity(this));
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadDataWithBaseURL(null, HtmlStyleUtil.pingHtml(html), "text/html", "utf-8", null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_share:
                String imgUrl = "";
                if (goodsDetailsMo.getGoods().getGallerys() != null && goodsDetailsMo.getGoods().getGallerys().size() > 0) {
                    imgUrl = goodsDetailsMo.getGoods().getGallerys().get(0);
                }
                ShareUtils.getInstance(this).startShare(
                        goodsDetailsMo.getGoods().getName(),
                        goodsDetailsMo.getGoods().getGoodsName(),
                        imgUrl, goodsDetailsMo.getGoods().getGoodsShareUrl(), null);
                break;
            case R.id.tv_buy:
                isAddCart = false;
                showBottomDialog(view);
                break;
            case R.id.tv_addcart:
                isAddCart = true;
                showBottomDialog(view);
                break;
            case R.id.ll_cs:
                showBottomDialogCs(view, goodsDetailsMo.getGoodsAttributes());
                break;
            case R.id.ll_server:
                showBottomDialogServer(view, goodsDetailsMo.getGoodsServices());
                break;
            case R.id.ll_comment:
                Intent intent = new Intent(this, MyCommentActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("goodsId", goodsDetailsMo.getGoods().getGoodsId());
                startActivity(intent);
                break;
            case R.id.tv_dh://兑换
                showBottomDialog(view);
                break;
            default:
                break;
        }
    }

    private BottomSheetDialog dialog;

    private ShoppingSelectView selectView;

    //购买弹窗
    private void showBottomDialog(View parent) {
        dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_goods_gg, null);

        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ImageView imageView = view.findViewById(R.id.iv_img);
        if (goodsDetailsMo == null) return;
        if (goodsDetailsMo.getGoods() == null) return;
        if (goodsDetailsMo.getGoods().getGallerys() != null && goodsDetailsMo.getGoods().getGallerys().size() > 0) {
            Glide.with(GoodsDetailsActivity.this).load(goodsDetailsMo.getGoods().getGallerys().get(0)).into(imageView);
        }

        TextView tvPrice = view.findViewById(R.id.tv_price);
        tvPrice.setText(goodsDetailsMo.getGoods().getPrice());

        //库存
        TextView tvKc = view.findViewById(R.id.tv_kc);
        tvKc.setText("库存" + goodsDetailsMo.getGoods().getNumber() + "件");

        TextView tvReduce = view.findViewById(R.id.tv_reduce);

        TextView tvAdd = view.findViewById(R.id.tv_add);

        TextView tvNum = view.findViewById(R.id.tv_num);

        TextView tvBuy = view.findViewById(R.id.tv_buy);

//        TextView tvTips = view.findViewById(R.id.tv_tips);
//
//        RecyclerView recyclerView = view.findViewById(R.id.recy_tips);

        //规格控件
        selectView = view.findViewById(R.id.shopping_selectView);

        selectView.setTextViewAndGGproject(tvNum, tvPrice, tvKc, tvBuy, goodsDetailsMo.getGoodsSpecifications());
        selectView.setData(goodsDetailsMo.getGoodsSpecs());//规格数组

        //加
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int limitSize = goodsDetailsMo.getGoods().getPurchaseLimit();
                if (limitSize > 0 && limitSize <= Integer.parseInt(tvNum.getText().toString())) {
                    ToastUtil.showShort(GoodsDetailsActivity.this, "该商品每人只限购" + limitSize + "件哦");
                    return;
                }

                int n = Integer.parseInt(tvNum.getText().toString());
                n++;
                if (n > goodsDetailsMo.getGoods().getNumber()) {
                    ToastUtil.showShort(GoodsDetailsActivity.this, "数量超出范围");
                    return;
                }
                tvNum.setText(String.valueOf(n));
            }
        });

        //减
        tvReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int n = Integer.parseInt(tvNum.getText().toString());
                if (n > 1) {
                    n--;
                }
                tvNum.setText(String.valueOf(n));
            }
        });

        //确认订单
        tvBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productId = "";
                if (!StringUtils.isEmpty(selectView.getProductId())) {
                    productId = selectView.getProductId();
                }
                if (StringUtils.isEmpty(productId)) {
                    ToastUtil.showShort(GoodsDetailsActivity.this, "产品已售完或超过购买库存数量");
                    return;
                }
                if (isAddCart) {
                    addOrder(productId,tvNum.getText().toString());
                } else {
                    quitOrder(productId, tvNum.getText().toString());
                }

            }
        });

        int hight = (int) (Double.valueOf(ScreenUtils.getScreenHeight(this)) / 1.3);
        dialog.contentView(view)/*加载视图*/
                .heightParam(hight)
                /*动画设置*/
                .inDuration(200)
                .outDuration(200)
                .cancelable(true)
                .show();

    }

    //参数弹窗
    private void showBottomDialogCs(View parent, List<GoodsDetailsMo.GoodsAttributes> mDate) {
        dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_goods_cs, null);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(GoodsDetailsActivity.this));
        BaseAdapter adapter = new BaseAdapter<GoodsDetailsMo.GoodsAttributes>(
                GoodsDetailsActivity.this, recyclerView, mDate, R.layout.item_text_cs) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position, GoodsDetailsMo.GoodsAttributes o) {
                holder.setText(R.id.tv_key, o.getAttribute());
                holder.setText(R.id.tv_value, o.getValue());
            }
        };
        recyclerView.setAdapter(adapter);
        dialog.contentView(view)/*加载视图*/
                /*.heightParam(height/2),显示的高度*/
                /*动画设置*/
                .inDuration(200)
                .outDuration(200)
                /*.inInterpolator(new BounceInterpolator())
                .outInterpolator(new AnticipateInterpolator())*/
                .cancelable(true)
                .show();
    }

    //服务弹窗
    private void showBottomDialogServer(View parent, List<GoodsDetailsMo.GoodsServices> mDate) {
        dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_goods_cs, null);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        TextView textView = view.findViewById(R.id.tv_show_title);
        textView.setText("服务支持");
        RecyclerView recyclerView = view.findViewById(R.id.recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(GoodsDetailsActivity.this));
        BaseAdapter adapter = new BaseAdapter<GoodsDetailsMo.GoodsServices>(
                GoodsDetailsActivity.this, recyclerView, mDate, R.layout.item_home_goods_server) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position, GoodsDetailsMo.GoodsServices o) {
                holder.setText(R.id.tv_serverName, o.getGoodServiceName());
                holder.setText(R.id.tv_serverDesc, o.getGoodServiceDesc());
            }
        };
        recyclerView.setAdapter(adapter);
        dialog.contentView(view)/*加载视图*/
                .inDuration(200)
                .outDuration(200)
                .cancelable(true)
                .show();
    }

    //确认订单
    private void quitOrder(String productId, String num) {
        if (selectView != null) {
            if (StringUtils.isEmpty(selectView.getProductId())) {
                ToastUtil.showShort(this, "请选择规格");
                return;
            }
        }
        setLoaddingView(true);
        Map<String, String> map = new HashMap<>();
        map.put("specsIds", productId);
        map.put("goodsNum", num);
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.saveConfirm, str, getToken(this), new GsonObjectCallback<String>(Article.BASE) {
            @Override
            public void onUi(String result) {
                setLoaddingView(false);
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        Intent intent = new Intent(GoodsDetailsActivity.this, OrderActivity.class);
                        startActivity(intent);
                    } else {
                        ToastUtil.showShort(GoodsDetailsActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
                    }
                    dialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoaddingView(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoaddingView(false);
                    }
                });
            }
        });
    }


    //加入购物车
    private void addOrder(String productId, String num) {
        if (selectView != null) {
            if (StringUtils.isEmpty(selectView.getProductId())) {
                ToastUtil.showShort(this, "请选择规格");
                return;
            }
        }
        setLoaddingView(true);
        Map<String, String> map = new HashMap<>();
        map.put("goodsId", goodsDetailsMo.getGoods().getGoodsId());
        map.put("goodsSpecificationId", productId);
        map.put("number", num);
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(ShopCart.BASE).doPostJson2(ShopCart.addGoods, str, getToken(this), new GsonObjectCallback<String>(ShopCart.BASE) {
            @Override
            public void onUi(String result) {
                setLoaddingView(false);
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        ToastUtil.showShort(GoodsDetailsActivity.this, "加入购物车成功");
                    } else {
                        ToastUtil.showShort(GoodsDetailsActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
                    }
                    dialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoaddingView(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoaddingView(false);
                    }
                });
            }
        });
    }

    //收藏
    private void collection() {
        Map<String, String> map = new HashMap<>();
        map.put("goodsId", goodsDetailsMo.getGoods().getGoodsId());
        map.put("type", "1");
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.toOptionGoods, str, getToken(this), new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                System.out.println("result = [" + result + "]");
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        isCollect = !isCollect;
                        setCollect(isCollect, true);
                    } else {
                        ToastUtil.showShort(GoodsDetailsActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
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


    /**
     * 倒计时
     *
     * @param data
     */
    private void dowTime(String data) {
        if (StringUtils.isEmpty(data) || data.equals("null")) {
            tvDate.setText("活动已结束");
            llDeatilsMenu.setVisibility(View.GONE);
            return;
        }
        CountDownUtil downUtil = new CountDownUtil();
        downUtil.start(DateUtil.timeToStamp(data), new CountDownUtil.OnCountDownCallBack() {
            @Override
            public void onProcess(int day, int hour, int minute, int second) {
                String strDay = "";
                String strHour = "";
                String strMinute = "";
                String strSecond = "";

                if (day < 10) {
                    strDay = 0 + String.valueOf(day);
                } else {
                    strDay = String.valueOf(day);
                }

                if (hour < 10) {
                    strHour = 0 + String.valueOf(hour);
                } else {
                    strHour = String.valueOf(hour);
                }

                if (minute < 10) {
                    strMinute = 0 + String.valueOf(minute);
                } else {
                    strMinute = String.valueOf(minute);
                }

                if (second < 10) {
                    strSecond = 0 + String.valueOf(second);
                } else {
                    strSecond = String.valueOf(second);
                }
                tvDate.setText(strDay + "天 " + strHour + "时 " + strMinute + "分 " + strSecond + "秒");
            }

            @Override
            public void onFinish() {
                tvDate.setText("活动已结束");
                llDeatilsMenu.setVisibility(View.GONE);
            }
        });
    }
}
