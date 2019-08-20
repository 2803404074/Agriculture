package com.tzl.agriculture.fragment.vip.activity;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.TimePickerView;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
import com.tzl.agriculture.fragment.vip.model.MingxMo;
import com.tzl.agriculture.util.DateUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseRecyclerHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import config.User;
import okhttp3.Call;

public class MoneyDetailedActivity extends SetBaseActivity implements View.OnClickListener {


    @BindView(R.id.iv_back)
    ImageView imgBack;

    @BindView(R.id.tv_date)
    TextView tvDate;

    //收入
    @BindView(R.id.tv_incomeNum)
    TextView tvIncomeNum;

    //支出
    @BindView(R.id.tv_payNum)
    TextView tvPayNum;

    @BindView(R.id.iv_tips)
    ImageView ivTips;

    //选择月份
    @BindView(R.id.iv_selectMonth)
    ImageView ivSelectMonth;

    @BindView(R.id.mx_recy)
    RecyclerView recyclerView;


    private TimePickerView pvTime1;

    private BaseAdapter adapter;

    private List<MingxMo.Option> mData = new ArrayList<>();

    private String dateStr;//参数的时间字符串


    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_money_detailed;
    }

    @Override
    public void initView() {
        setTitle("明细");
        dateStr = DateUtil.getDateForNow("yyyy-MM");

        imgBack.setImageResource(R.drawable.smssdk_ic_popup_dialog_close);
        ivSelectMonth.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseAdapter<MingxMo.Option>(this,recyclerView,mData,R.layout.item_mingx) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder,int position, MingxMo.Option o) {

                TextView textView = holder.getView(R.id.tv_money);
                if (o.getOptionType() == 0){
                    holder.setImageResource(R.id.iv_type,R.mipmap.add);
                    textView.setText("+"+o.getIntegral());
                    textView.setTextColor(getResources().getColor(R.color.colorOrange));
                }else {
                    holder.setImageResource(R.id.iv_type,R.mipmap.reduce);
                    textView.setText("-"+o.getIntegral());
                    textView.setTextColor(getResources().getColor(R.color.colorGri2));
                }
                holder.setText(R.id.tv_tvSName, TextUtil.checkStr2Str(o.getNotes()));//来源
                holder.setText(R.id.tv_date,o.getCreateTime());

                TextView tvSee = holder.getView(R.id.tv_see);
                ImageView ivSee = holder.getView(R.id.iv_see);
                tvSee.setText(TextUtil.checkStr2Str(o.getNotes()));
                ivSee.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tvSee.getVisibility() == View.GONE){
                            tvSee.setVisibility(View.VISIBLE);
                            ivSee.setRotation(180);
                        }else {
                            tvSee.setVisibility(View.GONE);
                            ivSee.setRotation(0);
                        }
                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new BaseAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
                initData();
            }
        });
    }

    private int page = 1;

    @Override
    public void initData() {
        Map<String,String>map = new HashMap<>();
        map.put("dateFormat",dateStr);
        //map.put("pageNum",String.valueOf(page));
        //map.put("userId","56");
        OkHttp3Utils.getInstance(User.BASE).doPostJsonForObj(User.moneyDetail, map, getToken(),
                new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0){
                        String str = object.optString("data");
                        MingxMo mingxMo = JsonUtil.string2Obj(str,MingxMo.class);

                        if (mingxMo != null){
                            tvIncomeNum.setText(mingxMo.getIncome());//收入
                            tvPayNum.setText(mingxMo.getExpend());//支出
                            mData = mingxMo.getOptions();
                        }

                        if (mData != null && mData.size()>0){
                            if (page == 1){
                                adapter.updateData(mData);
                            }else {
                                adapter.addAll(mData);
                            }

                            ivTips.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }else {
                            ivTips.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
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
        switch (view.getId()){
            case R.id.iv_selectMonth:
                showSelectDateView();
                break;
                default:break;
        }
    }

    private void showSelectDateView() {
        //时间选择器
        pvTime1 = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                dateStr = DateUtil.dateToStr(date,"yyyy-MM");
                tvDate.setText(DateUtil.dateToStr(date,"yyyy-MM"));
                page = 1;
                initData();
            }
        }).setType(new boolean[]{true, true, false, false, false, false}) //年月日时分秒 的显示与否，不设置则默认全部显示
                .isCenterLabel(false)
                .setDividerColor(Color.RED)
                .setTextColorCenter(getResources().getColor(R.color.colorPrimaryDark))//设置选中项的颜色
                .setTextColorOut(getResources().getColor(R.color.colorGri2))//设置没有被选中项的颜色
                .setContentSize(21)
                .setLineSpacingMultiplier(1.2f)
                .setTextXOffset(0, 0,0, 0, 0, 0)//设置X轴倾斜角度[ -90 , 90°]
                .setDecorView(null)
                .build();

        pvTime1.show();
    }


}
