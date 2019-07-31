package com.tzl.agriculture.fragment.vip.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.home.activity.SearchActivity;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
import com.tzl.agriculture.mall.activity.GoodsDetailsActivity;
import com.tzl.agriculture.model.GoodsMo;
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
import config.Mall;
import okhttp3.Call;

public class ApplyActivity extends SetBaseActivity implements View.OnClickListener {

    @BindView(R.id.recy_vip)
    RecyclerView recyclerView;

    @BindView(R.id.tv_share)
    TextView tvShare;

    @BindView(R.id.tv_yjsy)
    TextView tvYjsy;

    @BindView(R.id.tv_zszk)
    TextView tvZszk;

    @BindView(R.id.tv_jmlb)
    TextView tvJmlb;

    private BaseAdapter adapter;

    private List<GoodsMo> mData = new ArrayList<>();
    @Override
    public int setLayout() {
        return R.layout.activity_apply;
    }

    @Override
    public void initView() {
        setTitle("成为顾问");
        setAndroidNativeLightStatusBar(true);
        setStatusColor(R.color.colorW);


        DrawableSizeUtil sizeUtil = new DrawableSizeUtil(this);
        sizeUtil.setImgSize(100,100,1,tvShare,R.mipmap.icon_sha);
        sizeUtil.setImgSize(100,100,1,tvYjsy,R.mipmap.icon_shouyi);
        sizeUtil.setImgSize(100,100,1,tvZszk,R.mipmap.icon_zhekou);
        sizeUtil.setImgSize(100,100,1,tvJmlb,R.mipmap.icon_gift);

        tvShare.setOnClickListener(this);
        tvYjsy.setOnClickListener(this);
        tvZszk.setOnClickListener(this);
        tvJmlb.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new BaseAdapter<GoodsMo>(this,recyclerView,mData,R.layout.item_vip_goods_open) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, GoodsMo o) {
                holder.setImageByUrl(R.id.nice_img,TextUtil.checkStr2Str(o.getPicUrl()));
                holder.setText(R.id.tv_title,""+o.getGoodsName());
                holder.setText(R.id.tv_gg, TextUtil.checkStr2Str(o.getCategoryName()));
                holder.setText(R.id.tv_price,o.getPrice());
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GoodsMo goodsMo = (GoodsMo) adapter.getData().get(position);
                Intent intent = new Intent(ApplyActivity.this, GoodsDetailsActivity.class);
                intent.putExtra("goodsId",goodsMo.getGoodsId());
                intent.putExtra("type",2);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        String token = (String) SPUtils.instance(this,1).getkey("token","");
        Map<String,String>map = new HashMap<>();
        map.put("isEquity","1");
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.vipGoods, str, token, new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject dataObj = object.optJSONObject("data");
                    String str = dataObj.optString("records");
                    mData = JsonUtil.string2Obj(str,List.class,GoodsMo.class);
                    if (mData!=null){
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

    /**
     * 分享弹窗
     */
    private void showShareDialog(int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.img_fragment,null,false);
        ImageView imageView = view.findViewById(R.id.hxxq_img);
        initDialogImg(imageView,index);
        final AlertDialog dialog = new AlertDialog.Builder(ApplyActivity.this).setView(view).create();
        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this)/4*3), LinearLayout.LayoutParams.WRAP_CONTENT);
    }
    private void initDialogImg(ImageView imageView, int index) {
        String token = (String) SPUtils.instance(this, 1).getkey("token", "");
        OkHttp3Utils.getInstance(App.BASE).doPostJson2(App.interests, "", token, new GsonObjectCallback<String>(App.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    String data = object.optString("data");
                    List<VipActivity.QyTcMo> qyTcMos = JsonUtil.string2Obj(data, List.class, VipActivity.QyTcMo.class);
                    if (null != qyTcMos && index < qyTcMos.size()) {
                        Glide.with(ApplyActivity.this).load(TextUtil.checkStr2Str(qyTcMos.get(index).getContUrl())).into(imageView);
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
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
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
                default:break;
        }
    }
}
