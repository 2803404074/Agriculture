package com.tzl.agriculture.fragment.xiangc.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.tabs.TabLayout;
import com.shehuan.niv.NiceImageView;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.xiangc.fragment.XiangcFragmentMyCollection;
import com.tzl.agriculture.model.UserInfo;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.view.SimpleFragmentPagerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import config.Article;
import config.Base;
import okhttp3.Call;

public class XiangcMyActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.back)
    ImageView ivBack;

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.drawee_img)
    NiceImageView draweeView;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private List<String> tabTitle = new ArrayList<>();
    private SimpleFragmentPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiangc_my);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        ivBack.setOnClickListener(this);
        initData();
    }


    private void initData(){
        String token = (String) SPUtils.instance(XiangcMyActivity.this,1).getkey("token","");
        OkHttp3Utils.getInstance(Article.BASE).doPostJson2(Article.getOptionTotalNum, "",token, new GsonObjectCallback<String>(Article.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject dataObj = object.optJSONObject("data");
                    String str = dataObj.optString("optionNumList");
                    String head = dataObj.optString("headUrl");
                    String username = dataObj.optString("username");
                    Glide.with(XiangcMyActivity.this).load(head).into(draweeView);
                    tvName.setText(TextUtil.checkStr2Str(username));

                    List<DzColSh> mData = JsonUtil.string2Obj(str,List.class,DzColSh.class);
                    if (null !=mData && mData.size()>0){
                        for (int i=0;i<mData.size();i++){
                            XiangcFragmentMyCollection goodsFragment1 = new XiangcFragmentMyCollection();
                            goodsFragment1.setTabPos(i,mData.get(i).getType());
                            mFragments.add(goodsFragment1);
                            tabTitle.add(mData.get(i).getName()+"\t"+mData.get(i).getNum());
                        }

                        adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), mFragments, tabTitle);
                        viewPager.setAdapter(adapter);
                        tabLayout.setupWithViewPager(viewPager);
                        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            }

                            @Override
                            public void onPageSelected(int position) {
                                //滑动监听加载数据，一次只加载一个标签页
                                ((XiangcFragmentMyCollection) adapter.getItem(position)).sendMessage();
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });

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
            case R.id.back:
                finish();
                break;
                default:break;
        }
    }

    //点赞收藏分享---标题及数量及id模型
    static class DzColSh{
        private String name;
        private String num;
        private String type;

        public DzColSh() {
        }

        public DzColSh(String name, String num, String type) {
            this.name = name;
            this.num = num;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
