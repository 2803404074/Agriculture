package com.tzl.agriculture.fragment.vip.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tzl.agriculture.R;
import com.tzl.agriculture.model.VipCommodity;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
import com.tzl.agriculture.mall.activity.GoodsDetailsActivity;
import com.tzl.agriculture.model.GoodsMo;
import com.tzl.agriculture.model.UserInfo;
import com.tzl.agriculture.util.DrawableSizeUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.ScreenUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.view.BaseAdapter;
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
import config.App;
import okhttp3.Call;

public class VipActivity extends SetBaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_share)
    TextView tvShare;

    @BindView(R.id.tv_yjsy)
    TextView tvYjsy;

    @BindView(R.id.tv_zszk)
    TextView tvZszk;

    @BindView(R.id.tv_jmlb)
    TextView tvJmlb;

    @BindView(R.id.drawee_img)
    SimpleDraweeView draweeView;

    @BindView(R.id.tv_phone)
    TextView tvPhone;

    @BindView(R.id.tv_openTips)
    TextView tvOpenTips;

    @BindView(R.id.tv_invNow)
    TextView tvInvNow;//立即邀请

    @BindView(R.id.tv_jf)
    TextView tvJf;

    @BindView(R.id.ll_myjf)
    LinearLayout llMyJf;

    @BindView(R.id.ll_my_jf_tips)
    LinearLayout llMyJfTips;

    @BindView(R.id.iv_open)
    ImageView ivOpen;

    @BindView(R.id.re_my_jl)
    RecyclerView reMyJl;//会员显示 我的奖励

    @BindView(R.id.tv_mingx)
    TextView tvMingx;//明细

    @BindView(R.id.tv_vip_no)
    ImageView tvBy;

    @BindView(R.id.tv_month)
    TextView tvMonth;

    @BindView(R.id.rl_vip_yes)
    RelativeLayout rlVipYes;

    @BindView(R.id.tv_myqi)
    TextView tvMyQi;//如果普通用户，则显示 我的权益，如果是会员则显示我的奖励

    @BindView(R.id.recy)
    RecyclerView recyclerView;

    @BindView(R.id.nsv_view)
    NestedScrollView nestedScrollView;

    private BaseAdapter adapter;

    private List<GoodsMo> mData = new ArrayList<>();

    private UserInfo userInfo;

    private List<VipCommodity> vipCommodityLists = new ArrayList<>();;


    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_vip;
    }

    @Override
    public void initView() {
        setTitle("员工中心");
        setStatusColor(R.color.colorW);
        setAndroidNativeLightStatusBar(true);


        DrawableSizeUtil sizeUtil = new DrawableSizeUtil(this);
        sizeUtil.setImgSize(100, 100, 1, tvShare, R.mipmap.icon_sha);
        sizeUtil.setImgSize(100, 100, 1, tvYjsy, R.mipmap.icon_shouyi);
        sizeUtil.setImgSize(100, 100, 1, tvZszk, R.mipmap.icon_zhekou);
        sizeUtil.setImgSize(100, 100, 1, tvJmlb, R.mipmap.icon_gift);

        tvShare.setOnClickListener(this);
        tvYjsy.setOnClickListener(this);
        tvZszk.setOnClickListener(this);
        tvJmlb.setOnClickListener(this);
        ivOpen.setOnClickListener(this);
        tvMingx.setOnClickListener(this);

        String user = (String) SPUtils.instance(this, 1).getkey("user", "");
        userInfo = JsonUtil.string2Obj(user, UserInfo.class);
        draweeView.setImageURI(userInfo.getHeadUrl());
        tvPhone.setText(userInfo.getPhone());

        if (userInfo.getUserType() == 3) {
            tvBy.setVisibility(View.GONE);
            //rlVipYes.setVisibility(View.VISIBLE);
            llMyJf.setVisibility(View.VISIBLE);
            llMyJfTips.setVisibility(View.GONE);
            ivOpen.setVisibility(View.GONE);
            tvInvNow.setText("提现");
            tvOpenTips.setText(userInfo.getUsername());

            //获取积分信息

            reMyJl.setLayoutManager(new GridLayoutManager(this, 3));


            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setNestedScrollingEnabled(false);
            adapter = new BaseAdapter<GoodsMo>(this, recyclerView, mData, R.layout.item_vip_goods_buy) {
                @Override
                public void convert(Context mContext, BaseRecyclerHolder holder,int position, GoodsMo o) {
                    holder.setImageByUrl(R.id.nick_img, o.getPicUrl());
                    holder.setText(R.id.tv_title, o.getGoodsName());
                    holder.getView(R.id.tv_gg).setVisibility(View.GONE);
                    holder.setText(R.id.tv_price, o.getPrice());
                    holder.getView(R.id.tv_payNum).setVisibility(View.GONE);
                }
            };
            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    GoodsMo goodsMo = (GoodsMo) adapter.getData().get(position);
                    Intent intent = new Intent(VipActivity.this, GoodsDetailsActivity.class);
                    intent.putExtra("goodsId", goodsMo.getGoodsId());
                    intent.putExtra("type", 1);
                    startActivity(intent);
                }
            });


        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            adapter = new BaseAdapter<VipCommodity>(this,recyclerView,vipCommodityLists,R.layout.item_vip_commodity_adapter) {
                @Override
                public void convert(Context mContext, BaseRecyclerHolder holder,int position, VipCommodity o) {
                    switch (o.getMonth()){
                        case 1:
                            holder.setImageResource(R.id.iv_month,R.mipmap.month_1);
                            break;
                        case 2:
                            holder.setImageResource(R.id.iv_month,R.mipmap.month_2);
                            break;
                        case 3:
                            holder.setImageResource(R.id.iv_month,R.mipmap.month_3);
                            break;
                        case 4:
                            holder.setImageResource(R.id.iv_month,R.mipmap.month_4);
                            break;
                        case 5:
                            holder.setImageResource(R.id.iv_month,R.mipmap.month_5);
                            break;
                        case 6:
                            holder.setImageResource(R.id.iv_month,R.mipmap.month_6);
                            break;
                        case 7:
                            holder.setImageResource(R.id.iv_month,R.mipmap.month_7);
                            break;
                        case 8:
                            holder.setImageResource(R.id.iv_month,R.mipmap.month_8);
                            break;
                        case 9:
                            holder.setImageResource(R.id.iv_month,R.mipmap.month_9);
                            break;
                        case 10:
                            holder.setImageResource(R.id.iv_month,R.mipmap.month_10);
                            break;
                        case 11:
                            holder.setImageResource(R.id.iv_month,R.mipmap.month_11);
                            break;
                        case 12:
                            holder.setImageResource(R.id.iv_month,R.mipmap.month_12);
                            break;

                    }

                    holder.setImageByUrl(R.id.iv_commodity_img,o.getPicUrl());

                }
            };
            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                   Intent intent = new Intent(VipActivity.this,MonthActivity.class);
                   intent.putExtra("monthNumber",vipCommodityLists.get(position).getMonth());
                   startActivity(intent);
                }
            });

            tvInvNow.setVisibility(View.VISIBLE);
            //tvBy.setVisibility(View.VISIBLE);
            //rlVipYes.setVisibility(View.GONE);

            llMyJf.setVisibility(View.GONE);
            llMyJfTips.setVisibility(View.VISIBLE);

            tvMyQi.setVisibility(View.GONE);
            tvOpenTips.setText("暂未开通");

        }

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (userInfo.getUserType() == 2){
                    return;
                }
                //判断是否滑到的底部
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    page++;
                    initData();
                }
            }
        });


    }

    public void tvYq(View view){
        if (userInfo.getUserType() == 3){
            //提现
            Intent intent = new Intent(VipActivity.this,CashActivity.class);
            intent.putExtra("money",tvJf.getText().toString());
            startActivity(intent);
        }else {
            //邀请
            Intent intent = new Intent(this, ApplyActivity.class);
            startActivity(intent);
        }
    }

    private int page = 1;

    @Override
    public void initData() {

        if (userInfo.getUserType() == 2) {

            String token = (String) SPUtils.instance(VipActivity.this, 1).getkey("token", "");

            Map<String,String>map = new HashMap<>();
            String str = JsonUtil.obj2String(map);
            OkHttp3Utils.getInstance(App.BASE).doPostJson2(App.sNoVipGoodsList, str,token, new GsonObjectCallback<String>(App.BASE) {
                @Override
                public void onUi(String result) {
                    try {

                        JSONObject object = new JSONObject(result);
                        String str = object.optString("data");
                        List<VipCommodity> vipCommodityList = JsonUtil.string2Obj(str, List.class, VipCommodity.class);
                        adapter.updateData(vipCommodityList);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(Call call, IOException e) {

                }
            });

        } else if (userInfo.getUserType() == 3) {
            Map<String, String> map = new HashMap<>();
            map.put("pageNum", String.valueOf(page));
            String str = JsonUtil.obj2String(map);
            String token = (String) SPUtils.instance(this, 1).getkey("token", "");
            OkHttp3Utils.getInstance(App.BASE).doPostJson2(App.vipGoodsList, str, token, new GsonObjectCallback<String>(App.BASE) {
                @Override
                public void onUi(String result) {
                    try {
                        JSONObject object = new JSONObject(result);

                        if (object.optInt("code") != 0) return;

                        JSONObject dataObj = object.optJSONObject("data");

                        //月份
                        String moth = dataObj.optString("month");
                        tvMonth.setText(moth);

                        //商品列表
                        JSONObject pageObj = dataObj.optJSONObject("goodsListApiVoIPage");
                        String str = pageObj.optString("records");
                        mData = JsonUtil.string2Obj(str, List.class, GoodsMo.class);
                        if (mData != null) {
                            if (page == 0) {
                                adapter.updateData(mData);
                            } else {
                                adapter.addAll(mData);
                            }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_open:
                Intent intent = new Intent(this, ApplyActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_share:
                showShareDialog(0);
                break;
            case R.id.tv_yjsy:
                showShareDialog(1);
                break;
            case R.id.tv_zszk:
                showShareDialog(2);
                break;
            case R.id.tv_jmlb:
                showShareDialog(3);
                break;
            case R.id.tv_mingx:
                Intent intent1 = new Intent(this,MoneyDetailedActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    /**
     * 分享弹窗
     */
    private void showShareDialog(int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.img_fragment_tips, null, false);
        ImageView imageView = view.findViewById(R.id.hxxq_img);
        initDialogImg(imageView, index);
        final AlertDialog dialog = new AlertDialog.Builder(VipActivity.this).setView(view).create();
        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this) / 4 * 3), LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    private void initDialogImg(ImageView imageView, int index) {
        String token = (String) SPUtils.instance(this, 1).getkey("token", "");
        OkHttp3Utils.getInstance(App.BASE).doPostJson2(App.interests, "", token, new GsonObjectCallback<String>(App.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    String data = object.optString("data");
                    List<QyTcMo> qyTcMos = JsonUtil.string2Obj(data, List.class, QyTcMo.class);
                    if (null != qyTcMos && index < qyTcMos.size()) {
                        Glide.with(VipActivity.this).load(TextUtil.checkStr2Str(qyTcMos.get(index).getContUrl())).into(imageView);
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
     * 如果是会员，获取奖励信息
     */
    private void getJl() {
        String token = (String) SPUtils.instance(this, 1).getkey("token", "");
        OkHttp3Utils.getInstance(App.BASE).doPostJson2(App.rewards, "", token, new GsonObjectCallback<String>(App.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    String str = object.optString("data");
                    List<Tips> list = JsonUtil.string2Obj(str, List.class, Tips.class);
                    if (list != null) {
                        Tips tips = list.get(list.size() - 1);
                        tvJf.setText(tips.getValue());
                        list.remove(list.size() - 1);
                    }
                    BaseAdapter adapter = new BaseAdapter<Tips>(VipActivity.this, reMyJl, list, R.layout.item_vip_tips) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder,int position, Tips o) {
                            holder.setText(R.id.tv_name, o.getName());
                            holder.setText(R.id.tv_value, o.getValue());
                        }
                    };
                    reMyJl.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }

    static class Tips {
        private String id;
        private String value;
        private String name;

        public Tips() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    /**
     * 权益弹窗数据
     */
    static class QyTcMo {
        private String id;
        private String iconUrl;
        private String contUrl;
        private String name;

        public QyTcMo() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public String getContUrl() {
            return contUrl;
        }

        public void setContUrl(String contUrl) {
            this.contUrl = contUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getJl();
    }
}
