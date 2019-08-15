package com.tzl.agriculture.fragment.personal.framgent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shehuan.niv.NiceImageView;
import com.tzl.agriculture.R;
import com.tzl.agriculture.baseresult.SPTAG;
import com.tzl.agriculture.fragment.personal.activity.ccb.activity.BrowseActivity;
import com.tzl.agriculture.fragment.personal.activity.ccb.activity.CartActivity;
import com.tzl.agriculture.fragment.personal.activity.ccb.activity.CollectionGoodsActivity;
import com.tzl.agriculture.fragment.personal.activity.function.activity.MyCommentActivity;
import com.tzl.agriculture.fragment.personal.activity.order.MyOrderActivity;
import com.tzl.agriculture.fragment.personal.activity.set.SettingActivity;
import com.tzl.agriculture.fragment.personal.activity.set.UserMessActivity;
import com.tzl.agriculture.fragment.personal.login.activity.LoginActivity;
import com.tzl.agriculture.fragment.vip.activity.CouponActivity;
import com.tzl.agriculture.fragment.vip.activity.VipActivity;
import com.tzl.agriculture.main.MainActivity;
import com.tzl.agriculture.model.ServerMo;
import com.tzl.agriculture.model.UserInfo;
import com.tzl.agriculture.util.DialogUtil;
import com.tzl.agriculture.util.DialogUtilT;
import com.tzl.agriculture.util.DrawableSizeUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.ScreenUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.util.UserData;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseFragment;
import com.tzl.agriculture.view.BaseRecyclerHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import config.App;
import config.Article;
import config.Base;
import config.User;
import okhttp3.Call;

/**
 * 个人中心
 */

public class PersonFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.ll_my_all)
    LinearLayout llMyAll;//没有网络的时候隐藏

    @BindView(R.id.h_refreshLayout)
    RefreshLayout mRefreshLayout;

    //设置
    @BindView(R.id.iv_set)
    ImageView ivSet;

    //浏览记录局域控件---用于点击跳转
    @BindView(R.id.ll_see)
    LinearLayout llSee;

    //购物车局域控件---用于点击跳转
    @BindView(R.id.ll_cart)
    LinearLayout llCart;

    //收藏局域控件---用于点击跳转
    @BindView(R.id.ll_sc)
    LinearLayout llSc;

    //功能与服务模块
    @BindView(R.id.rv_menu)
    RecyclerView recyclerView;

    //圆角头像
    @BindView(R.id.drawee_img)
    SimpleDraweeView headView;

    //昵称
    @BindView(R.id.tv_nickName)
    TextView tvNickName;

    //收藏的数量
    @BindView(R.id.tv_scNum)
    TextView tvScNum;

    //购物车数量
    @BindView(R.id.tv_cartNum)
    TextView tvCartNum;

    //浏览数量
    @BindView(R.id.tv_seeNum)
    TextView tvSeeNum;

    //待付款
    @BindView(R.id.tv_dfk)
    TextView tvDfk;

    //待收货
    @BindView(R.id.tv_dsh)
    TextView tvDsh;

    //待评价
    @BindView(R.id.tv_dpj)
    TextView tvDpj;

    //售后
    @BindView(R.id.tv_sh)
    TextView tvSh;

    //我的订单
    @BindView(R.id.tv_myOrder)
    TextView tvMyOrder;

    @BindView(R.id.tv_share)
    TextView tvShare;

    private List<ServerMo> mData = new ArrayList<>();

    public static PersonFragment getInstance(){
        PersonFragment homeFragment = new PersonFragment();
        return homeFragment;
    }
    @Override
    protected int initLayout() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initView(View view) {

        headView.setOnClickListener(this);
        tvShare.setOnClickListener(this);

        mRefreshLayout = view.findViewById(R.id.h_refreshLayout);
        mRefreshLayout.setEnableHeaderTranslationContent(true);//内容偏移
        mRefreshLayout.setPrimaryColorsId(R.color.colorPrimaryDark, android.R.color.white);//主题颜色

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                getUserInfo();
            }
        });

        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });

        DrawableSizeUtil sizeUtil = new DrawableSizeUtil(getContext());
        sizeUtil.setImgSize(69,69,1,tvDfk,R.mipmap.my_dfk);
        sizeUtil.setImgSize(69,69,1,tvDsh,R.mipmap.my_dsh);
        sizeUtil.setImgSize(69,69,1,tvDpj,R.mipmap.my_dpj);
        sizeUtil.setImgSize(69,69,1,tvSh,R.mipmap.my_sh);
        sizeUtil.setImgSize(69,69,1,tvMyOrder,R.mipmap.my_wddd);

        ivSet.setOnClickListener(this);
        llSc.setOnClickListener(this);
        llCart.setOnClickListener(this);
        llSee.setOnClickListener(this);
        tvDfk.setOnClickListener(this);
        tvDsh.setOnClickListener(this);
        tvDpj.setOnClickListener(this);
        tvSh.setOnClickListener(this);
        tvMyOrder.setOnClickListener(this);

        mData.add(new ServerMo("员工中心",R.mipmap.gn_ygzx));
        mData.add(new ServerMo("新人福利",R.mipmap.gn_fl));
        mData.add(new ServerMo("我的评价",R.mipmap.gn_wdpl));
        mData.add(new ServerMo("分享APP",R.mipmap.gn_fx));

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),4));
        BaseAdapter adapter = new BaseAdapter<ServerMo>(getContext(),
                recyclerView,mData,R.layout.share_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, ServerMo o) {
                holder.setText(R.id.tv_name,o.getTitle());
                TextView textView = holder.getView(R.id.tv_name);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                holder.setImageResource(R.id.iv_img,o.getResources());
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0){
                    Intent intent = new Intent(getContext(), VipActivity.class);
                    startActivity(intent);
                }
                if (position == 1){
                    Intent intent = new Intent(getContext(), CouponActivity.class);
                    startActivity(intent);
                }
                if (position == 2){
                    Intent intent = new Intent(getContext(), MyCommentActivity.class);
                    startActivity(intent);
                }
                if (position == 3){
                    if (userInfo == null){
                        ToastUtil.showShort(getContext(),"获取信息失败");
                        return;
                    }
                   showShareDialog();
                }

            }
        });
    }

    /**
     * 分享弹窗
     */
    private void showShareDialog() {
        DialogUtilT dialogUtilT = new DialogUtilT<UserInfo>(getContext()) {
            @Override
            public void convert(BaseRecyclerHolder holder, UserInfo data) {
                holder.getView(R.id.rl_bg).setBackgroundResource(R.mipmap.share_news);
                SimpleDraweeView draweeView = holder.getView(R.id.nice_img);
                draweeView.setImageURI(data.getHeadUrl());
                holder.setText(R.id.tv_name,data.getNickname());

                //保存图片
                holder.getView(R.id.tv_save).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                //二维码
                ImageView img = holder.getView(R.id.iv_img);
                //邀请码
                TextView tvCode = holder.getView(R.id.tv_code);

                //网络获取
                initDialogImg(img,tvCode);
            }
        };
        dialogUtilT.show2(R.layout.dialog_share_app,userInfo);
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

    private UserInfo userInfo;

    @Override
    protected void initData() {

    }

    private void getUserInfo(){
        //获取用户信息
        OkHttp3Utils.getInstance(User.BASE).doGet(User.getUserInfo2, getToken(), new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {

                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0){
                        JSONObject dataObj = object.optJSONObject("data");

                        String str = dataObj.optString("user");
                        userInfo = JsonUtil.string2Obj(str, UserInfo.class);

                        SPUtils.instance(getContext(),1).put("user",str);

                        JSONObject numObj = dataObj.optJSONObject("goodsOptionNum");
                        if (null != numObj){
                            tvScNum.setText(numObj.optString("goodsCollectNum"));
                            tvCartNum.setText(numObj.optString("buyCarNum"));
                            tvSeeNum.setText(numObj.optString("goodsBrowseNum"));
                        }
                        setViewData();
                    }else if (object.optInt("code") == -1){
                        ToastUtil.showShort(getContext(),TextUtil.checkStr2Str(object.optString("msg")));
                    }else {
                        DialogUtil.init(getContext()).showTipsForControl("登陆失败，请重新登陆");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                llMyAll.setVisibility(View.VISIBLE);
            }
            @Override
            public void onFailed(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        llMyAll.setVisibility(View.GONE);
                        ToastUtil.showShort(getContext(),"请求超时");
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        llMyAll.setVisibility(View.GONE);
                        ToastUtil.showShort(getContext(),"无法连接服务，请稍后再试");
                    }
                });
            }
        });
    }

    /**
     * 绑定用户数据
     */
    private void setViewData() {
        if (null!=userInfo){
            tvNickName.setText(userInfo.getNickname());
            headView.setImageURI(userInfo.getHeadUrl());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //头像点击
            case R.id.drawee_img:
                Intent intentU = new Intent(getContext(), UserMessActivity.class);
                startActivity(intentU);
                break;
            case R.id.tv_share://分享
                showShareDialog();
                break;
            case R.id.iv_set:
                if (userInfo == null){
                    ToastUtil.showShort(getContext(),"获取用户信息异常，请检查您的网络");
                    return;
                }
                Intent intent = new Intent(getContext(), SettingActivity.class);
                intent.putExtra("head",userInfo.getHeadUrl());
                intent.putExtra("name",userInfo.getNickname());
                startActivity(intent);
                break;
            case R.id.ll_sc:
                Intent intent1 = new Intent(getContext(), CollectionGoodsActivity.class);
                startActivity(intent1);
                break;
            case R.id.ll_cart:
                DialogUtil.init(getContext()).showTips();
//                Intent intent2 = new Intent(getContext(), CartActivity.class);
//                startActivity(intent2);
                break;
            case R.id.ll_see:
                Intent intent3 = new Intent(getContext(), BrowseActivity.class);
                startActivity(intent3);
                break;
            case R.id.tv_dfk:
                Intent intent4 = new Intent(getContext(), MyOrderActivity.class);
                intent4.putExtra("position",1);
                startActivity(intent4);
                break;
            case R.id.tv_dsh:
                Intent intent5 = new Intent(getContext(), MyOrderActivity.class);
                intent5.putExtra("position",2);
                startActivity(intent5);
                break;
            case R.id.tv_dpj:
                Intent intent6 = new Intent(getContext(), MyOrderActivity.class);
                intent6.putExtra("position",3);
                startActivity(intent6);
                break;
            case R.id.tv_sh:
                break;
            case R.id.tv_myOrder:
                Intent intent8 = new Intent(getContext(), MyOrderActivity.class);
                intent8.putExtra("position",0);
                startActivity(intent8);
                break;
            default:break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserInfo();
    }
}
