package com.tzl.agriculture.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.function.activity.MyCommentActivity;
import com.tzl.agriculture.model.ProductMo;
import com.tzl.agriculture.model.Specifications;
import com.tzl.agriculture.util.ToastUtil;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/*
 * 描述:TODO 商品规格选择View
 */
public class ShoppingSelectView extends LinearLayout {

    private boolean hasGG = false;
    private boolean isFinish = false;
    private boolean OK;
    private int faterArr[];
    private String sunArr[];

    private TextView tvNum;//数量
    private TextView countPrice;//总价
    private TextView stock;//库存
    private TextView tvBuy;//购买


    //private TextView tvTips;//
//    private RecyclerView recyclerView;//已选提示
//    private BaseAdapter adapter;
//    private List<String> mData;

    private double lastPrice;//选完规格后的价钱
    private String productId;//选完规格后的id

    /**
     * 数据源
     */
    private List<Specifications> list; //规格
    private List<ProductMo> productInfo;//产品

    /**
     * 上下文
     */
    private Context context;

    /**
     * 已选提示的字符串
     */
    private String tips = "";
    /**
     * 规格标题栏的文本间距
     */
    private int titleMargin = 8;
    /**
     * 整个商品属性的左右间距
     */
    private int flowLayoutMargin = 16;
    /**
     * 属性按钮的高度
     */
    private int buttonHeight = 25;
    /**
     * 属性按钮之间的左边距
     */
    private int buttonLeftMargin = 10;
    /**
     * 属性按钮之间的上边距
     */
    private int buttonTopMargin = 8;
    /**
     * 文字与按钮的边距
     */
    private int textPadding = 10;

    /**
     * 选择后的回调监听
     */
    private OnSelectedListener listener;


    public ShoppingSelectView(Context context) {
        super(context);
        initView(context);
    }

    public ShoppingSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        this.context = context;
    }

    private  int index = 0;
    public void getView() {
        if (list.size() < 1) {
            return;
        }
        faterArr = new int[list.size()];//存放规格标题id
        sunArr = new String[list.size()];//存放需要匹配的规格id

        //规格id
        for (int i = 0; i < list.size(); i++) {
            faterArr[i] = list.get(i).getId();
        }

        for (Specifications attr : list) {
            //设置规格分类的标题
            TextView textView = new TextView(context);
            TextView line = new TextView(context);
            line.setBackgroundColor(getResources().getColor(R.color.colorGri2));
            int lineHeight = dip2px(context, titleMargin);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            LayoutParams lineParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, lineHeight);
            line.setLayoutParams(lineParams);

            int margin = dip2px(context, titleMargin);
            textView.setText(attr.getName());
            params.setMargins(margin, margin, margin, margin);
            textView.setLayoutParams(params);

            addView(textView);
            //设置一个大规格下的所有小规格
            FlowLayout layout = new FlowLayout(context, attr.getValueList(),index);
            index++;

            layout.setTitle(attr.getName());
            layout.setPadding(dip2px(context, flowLayoutMargin), 0, dip2px(context, flowLayoutMargin), 0);

            layout.setListener(new OnSelectedListener() {
                /**
                 *
                 * @param tagId 所属的父及id
                 * @param id 规格id,335   需要匹配的id
                 * @param name 规格名称
                 * @param index  点击的是几个大规格，用于展示“已选”
                 */
                @Override
                public void onSelected(int tagId, String id, String name,int index) {
                    for (int i = 0; i < faterArr.length; i++) {
                        if (tagId == faterArr[i]) {
                            sunArr[i] = id;
                        }
                    }
                    //开始计算
                    startJs();
                }
            });

            for (int k = 0; k < attr.getValueList().size(); k++) {
                Specifications.ValueList smallAttr = attr.getValueList().get(k);
                //属性按钮
                RadioButton button = new RadioButton(context);

                //默认选中第一个
                if (k == 0) {
                    button.setChecked(true);
                    for (int i = 0; i < faterArr.length; i++) {
                        if (attr.getId() == faterArr[i]) {
                            sunArr[i] = attr.getValueList().get(k).getSpecsId();
                        }
                    }
                }
                //设置按钮的参数
                LayoutParams buttonParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                        dip2px(context, buttonHeight));
                //设置文字的边距
                int padding = dip2px(context, textPadding);
                button.setPadding(padding, 0, padding, 0);
                //设置margin属性，需传入LayoutParams否则会丢失原有的布局参数
                MarginLayoutParams marginParams = new MarginLayoutParams(buttonParams);
                marginParams.rightMargin = dip2px(context, buttonLeftMargin);
                marginParams.topMargin = dip2px(context, buttonTopMargin);

                button.setLayoutParams(marginParams);
                button.setGravity(Gravity.CENTER);
                button.setBackgroundResource(R.drawable.tv_sel);

                button.setButtonDrawable(android.R.color.transparent);
                button.setText(smallAttr.getSpecsValue());//设置规格内容
                button.setId(Integer.parseInt(smallAttr.getSpecsId()));//设置规格ID
                button.setTag(attr.getId());
                layout.addView(button);
            }
            addView(layout);
        }

        startJs();
    }

    private String lastStr = "";
    private void startJs() {
        for (int i = 0; i < sunArr.length; i++) {
            //只有一个规格
            if (sunArr.length == 1) {
                lastStr = sunArr[0];
                break;
            }

            //最后一个规格
            if (i == sunArr.length - 1) {
                lastStr += sunArr[i];
                break;
            }

            lastStr += sunArr[i] + ",";
        }

        //循环规格对象，如果和lastStr一样则存在
        for (int i = 0; i < productInfo.size(); i++) {
            if (lastStr.equals(productInfo.get(i).getSpecsIds())) {
                lastPrice = Double.valueOf(productInfo.get(i).getPrice());
                //库存
                stock.setText("库存"+productInfo.get(i).getNumber()+"件");
                //设置productId
                productId = productInfo.get(i).getId();
                isFinish = true;

                //价格变化
                countPrice.setText(productInfo.get(i).getPrice());

                //设置数量
                if (Integer.parseInt(tvNum.getText().toString())> Integer.parseInt(productInfo.get(i).getNumber())){
                    tvNum.setText(productInfo.get(i).getNumber());
                }
                if (Integer.parseInt(productInfo.get(i).getNumber()) < 1) {
                    ToastUtil.showShort(context, "该规格的产品卖完啦。。");
                    isFinish = false;
                    productId = "";
                }
                break;
            } else {
            }

        }

    }

    public String getLastPrice() {
        return String.valueOf(lastPrice);
    }

    public String getProductId() {
        return productId;
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setData(List<Specifications> data) {
        list = data;
//        if (list == null)return;
//        mData =  new ArrayList<>();
        getView();
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * @param countPrice  总价控件
     * @param stock       库存控件
     * @param productInfo 规格对象
     */
    public void setTextViewAndGGproject(TextView tvNum,TextView countPrice, TextView stock, TextView tvBuy,
                                        List<ProductMo> productInfo) {
        this.tvNum = tvNum;
        this.countPrice = countPrice;
        this.stock = stock;
        this.tvBuy = tvBuy;
        this.productInfo = productInfo;

    }
}
