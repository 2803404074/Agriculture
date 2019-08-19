package com.tzl.agriculture.mall.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.rey.material.app.BottomSheetDialog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.home.activity.SearchActivity;
import com.tzl.agriculture.fragment.personal.activity.order.OrderSearchActivity;
import com.tzl.agriculture.mall.adapter.ChinaGuSpannerAdapter;
import com.tzl.agriculture.model.AddressCode;
import com.tzl.agriculture.model.GoodsDetailsMo;
import com.tzl.agriculture.model.GoodsMo;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.ScreenUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.view.BaseActivity;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseRecyclerHolder;
import com.tzl.agriculture.view.onLoadMoreListener;

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
import config.Mall;
import okhttp3.Call;

/*中国馆*/
public class ChinaGuActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.h_refreshLayout)
    RefreshLayout mRefreshLayout;

    @BindView(R.id.tv_startSearch)
    LinearLayout tvSearch;

    @BindView(R.id.tv_sheng)
    TextView tvProvince;

    @BindView(R.id.tv_city)
    TextView tvCity;

    @BindView(R.id.tv_xian)
    TextView tvXian;

    @BindView(R.id.back)
    ImageView ivBack;

    @BindView(R.id.recy_zgg)
    RecyclerView recyclerView;

    private List<GoodsMo> mData = new ArrayList<>();


    private List<AddressCode> addressCodeList = new ArrayList<>();

    private BaseAdapter adapter;


    private BaseAdapter addressAdapter;

    @Override
    public int setLayout() {
        return R.layout.activity_china_gu;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void initView() {
        ivBack.setOnClickListener(this);
        tvProvince.setOnClickListener(this);
        tvCity.setOnClickListener(this);
        tvXian.setOnClickListener(this);

        tvSearch.setOnClickListener(this);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                mData.clear();
                page = 1;
                getGooods();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BaseAdapter<GoodsMo>(getContext(), recyclerView, mData, R.layout.item_limited_time) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, GoodsMo o) {
                holder.setImageByUrl(R.id.iv_img, o.getPicUrl());
                holder.setText(R.id.tv_name, o.getGoodsName());
                holder.setText(R.id.tv_price, o.getPrice());
                holder.getView(R.id.ll_gg).setVisibility(View.INVISIBLE);

                TextView tvMarketPrice = holder.getView(R.id.tv_marketPrice);
                tvMarketPrice.setText(o.getOriginalPrice());
                tvMarketPrice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GoodsMo goodsMo = (GoodsMo) adapter.getData().get(position);
                Intent intent = new Intent(getContext(), GoodsDetailsActivity.class);
                intent.putExtra("goodsId", goodsMo.getGoodsId());
                intent.putExtra("type", 2);
                startActivity(intent);
            }
        });

        recyclerView.addOnScrollListener(new onLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                page++;
                getGooods();
            }
        });
    }

    private int page = 1;

    //初始化省份
    @Override
    public void initData() {
        Map<String,String>map = new HashMap<>();
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.zggList, str, getToken(this), new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        JSONObject dataObj = object.optJSONObject("data");
                        String str = dataObj.optString("goodsList");
                        mData = JsonUtil.string2Obj(str, List.class, GoodsMo.class);
                        if (mData != null) {
                            if (page == 1) {
                                adapter.updateData(mData);
                            } else {
                                adapter.addAll(mData);
                            }
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_startSearch:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_sheng:
                showDiaLog(1);
                break;
            case R.id.tv_city:
                if (StringUtils.isEmpty(provinceCode)) {
                    ToastUtil.showShort(this, "请选择省份");
                    return;
                }
                showDiaLog(2);
                break;
            case R.id.tv_xian:
                if (StringUtils.isEmpty(cityCode)) {
                    ToastUtil.showShort(this, "请选择城市");
                    return;
                }
                showDiaLog(3);
                break;
            default:
                break;
        }
    }

    /**
     * @param index 1 2 3省市县弹窗
     */
    private void showDiaLog(int index) {
        dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_goods_cs, null);
        TextView tvTitle = view.findViewById(R.id.tv_show_title);
        tvTitle.setText("请选择区域");
        RecyclerView recyclerView = view.findViewById(R.id.recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        view.findViewById(R.id.tv_ok).setVisibility(View.GONE);

        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        addressAdapter = new BaseAdapter<AddressCode>(this, recyclerView, addressCodeList, R.layout.item_zgg_address_text) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, AddressCode o) {
                holder.setText(R.id.tv_tips, o.getName());
            }
        };
        recyclerView.setAdapter(addressAdapter);

        addressAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (index == 1) {
                    tvProvince.setText(addressCodeList.get(position).getName());
                    provinceCode = addressCodeList.get(position).getCode();
                } else if (index == 2) {
                    tvCity.setText(addressCodeList.get(position).getName());
                    cityCode = addressCodeList.get(position).getCode();
                } else {
                    tvXian.setText(addressCodeList.get(position).getName());
                    xianCode = addressCodeList.get(position).getCode();
                }
                getGooods();
                dialog.dismiss();
            }
        });

        if (index == 1) {
            setAddressMet("0", "");//查省
        } else if (index == 2) {
            setAddressMet("1", provinceCode);//查市
        } else {
            setAddressMet("2", cityCode);//查县
        }

        int hight = (int) (Double.valueOf(ScreenUtils.getScreenHeight(this)) / 1.5);
        dialog.contentView(view)
                .heightParam(hight)
                .inDuration(200)
                .outDuration(200)
                .cancelable(true)
                .show();
    }

    private BottomSheetDialog dialog;


    private String provinceCode = "";
    private String cityCode = "";
    private String xianCode = "";

    /**
     * @param code
     */
    private void setAddressMet(String type, String code) {
        Map<String, String> map = new HashMap<>();
        map.put("type", type);
        map.put("code", code);
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.getProvinceList, str, getToken(this),
                new GsonObjectCallback<String>(Mall.BASE) {
                    @Override
                    public void onUi(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            JSONObject dataObj = object.optJSONObject("data");
                            String str = dataObj.optString("dataList");
                            addressCodeList = JsonUtil.string2Obj(str, List.class, AddressCode.class);
                            if (addressCodeList != null) {
                                addressAdapter.updateData(addressCodeList);
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

    private void getGooods() {
        Map<String, String> map = new HashMap<>();
        map.put("provinceCode", provinceCode);
        map.put("cityCode", cityCode);
        map.put("areaCode", xianCode);
        map.put("pageNum", String.valueOf(page));

        String str = JsonUtil.obj2String(map);

        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.zggList, str, getToken(this), new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        JSONObject dataObj = object.optJSONObject("data");
                        String str = dataObj.optString("goodsList");
                        mData = JsonUtil.string2Obj(str, List.class, GoodsMo.class);
                        if (mData != null) {
                            if (page == 1) {
                                if (mData.size() == 0)ToastUtil.showShort(ChinaGuActivity.this,"该区域暂未上架内容哦");
                                adapter.updateData(mData);
                            } else {
                                adapter.addAll(mData);
                            }
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
