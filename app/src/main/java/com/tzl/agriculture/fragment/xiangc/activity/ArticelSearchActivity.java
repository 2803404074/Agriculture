package com.tzl.agriculture.fragment.xiangc.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.comment.activity.HtmlForXcActivity;
import com.tzl.agriculture.model.XiangcMo;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.ShowButtonLayoutData;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseRecyclerHolder;
import com.tzl.agriculture.view.ShowButtonLayout;

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
import butterknife.ButterKnife;
import config.Article;
import okhttp3.Call;

public class ArticelSearchActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.back)
    ImageView ivBack;
    @BindView(R.id.search_edit)
    EditText editText;

    @BindView(R.id.tv_start)
    TextView tvStart;

    @BindView(R.id.hot_layout)
    ShowButtonLayout hotLayout;

    @BindView(R.id.history_layout)
    ShowButtonLayout historyLayout;

    private List<String> HistList;//历史
    private List<String> hostList;//热门

    @BindView(R.id.search_recycler_view)
    RecyclerView recyclerView;

    private BaseAdapter adapter;
    private List<XiangcMo.Article> mData = new ArrayList<>();

    @BindView(R.id.search_title)
    LinearLayout llSearch;

    @BindView(R.id.tv_tips)
    TextView tvTips;

    @BindView(R.id.tv_clea)
    TextView tvClea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articel_search);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        tvClea.setOnClickListener(this);

        //热门搜索
        hostList = new ArrayList<>();
        hostList.add("桂林山水");
        hostList.add("锦绣山河");
        hostList.add("南宁趣乡");
        hostList.add("农业规划");
        hostList.add("新政策");
        hostList.add("农业产量");

        //历史数据
        String history = (String) SPUtils.instance(this,1).getkey("history","");
        HistList = JsonUtil.string2Obj(history,List.class,String.class);
        if(null == HistList){
            HistList = new ArrayList<>();
        }

        //热门搜索
        ShowButtonLayoutData data1 = new ShowButtonLayoutData<String>(this, hotLayout, hostList, new ShowButtonLayoutData.MyClickListener() {
            @Override
            public void clickListener(View v, double arg1, double arg2) {
                String tag = (String) v.getTag();
                getHttp(tag);
            }
        });

        //历史搜索
        ShowButtonLayoutData data2 = new ShowButtonLayoutData<String>(this, historyLayout, HistList, new ShowButtonLayoutData.MyClickListener() {
            @Override
            public void clickListener(View v,  double arg1,double arg2) {
                String tag = (String) v.getTag();
                getHttp(tag);
            }
        });
        data1.setData();
        data2.setData();

        tvStart.setOnClickListener(this);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) editText.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(ArticelSearchActivity.this
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    // 搜索，进行自己要的操作...
                    getHttp(editText.getText().toString());
                    return true;
                }
                return false;
            }
        });


        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new BaseAdapter<XiangcMo.Article>(this,recyclerView,mData,R.layout.item_xiangc_children) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, XiangcMo.Article o) {
                holder.setImageByUrl(R.id.iv_img,o.getCoverImgurl());
                holder.setText(R.id.tv_title,""+o.getTitle());
               // holder.setText(R.id.tv_address,o.getArticleLocation().getProvinceStr()+o.getArticleLocation().getCityStr());
            }
        };
        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(ArticelSearchActivity.this, HtmlForXcActivity.class);
                XiangcMo.Article xiangcMo = (XiangcMo.Article) adapter.getData().get(position);
                intent.putExtra("title",xiangcMo.getTitle());
                intent.putExtra("content",xiangcMo.getContent());
                intent.putExtra("articleId",xiangcMo.getArticleId());
                intent.putExtra("date",xiangcMo.getCreateTime());
                intent.putExtra("goodNum",xiangcMo.getGoodNum());
                intent.putExtra("collectNum",xiangcMo.getCollectNum());

                intent.putExtra("alreadyGood",xiangcMo.getAlreadyGood());

                intent.putExtra("alreadyCollect",xiangcMo.getAlreadyCollect());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                /*判断是否是“搜索”键*/
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    String key = editText.getText().toString().trim();
                    if(StringUtils.isEmpty(key)){
                        ToastUtil.showShort(ArticelSearchActivity.this,"请输入您要搜索的文章");
                        return true;
                    }
                    //  下面就是大家的业务逻辑
                    getHttp(key);
                    //  这里记得一定要将键盘隐藏了
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 开始搜索
     * @param tag 关键字
     */
    private void getHttp(String tag) {
        Map<String,String>map = new HashMap<>();
        map.put("title",tag);
        map.put("typeId","1");
        map.put("findChildren","1");
        map.put("category","1");
        String str = JsonUtil.obj2String(map);
        String token = (String) SPUtils.instance(this,1).getkey("token","");
        OkHttp3Utils.getInstance(Article.BASE).doPostJson2(Article.getArticles, str,token, new GsonObjectCallback<String>(Article.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject dataObj = object.optJSONObject("data");
                    String str = dataObj.optString("articleInfoList");
                    mData = JsonUtil.string2Obj(str,List.class,XiangcMo.Article.class);

                    if (mData.size()>0){
                        llSearch.setVisibility(View.GONE);
                        tvTips.setVisibility(View.GONE);
                        adapter.updateData(mData);
                    }else {
                        mData.clear();
                        adapter.updateData(mData);
                        llSearch.setVisibility(View.VISIBLE);
                        tvTips.setVisibility(View.VISIBLE);
                    }

                    //添加历史
                    for (int i = 0; i <HistList.size() ; i++) {
                        if (HistList.get(i).equals(tag)){
                            return;
                        }
                        if (HistList.size()>10){
                            return;
                        }
                    }
                    //添加历史
                    HistList.add(tag);
                    String strHistList = JsonUtil.obj2String(HistList);
                    SPUtils.instance(ArticelSearchActivity.this,1).put("history",strHistList);
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
            case R.id.tv_start:
                if (StringUtils.isEmpty(editText.getText().toString()))return;
                getHttp(editText.getText().toString());
                break;
            case R.id.tv_clea:
                SPUtils.instance(this,1).remove("historyGoods");
                break;
                default:break;
        }
    }
}
