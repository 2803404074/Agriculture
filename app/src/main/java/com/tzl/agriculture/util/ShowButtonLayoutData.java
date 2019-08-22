package com.tzl.agriculture.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.view.ShowButtonLayout;

import java.util.List;

public class ShowButtonLayoutData<T> {
    private Context context;
    private ShowButtonLayout layout;
    private List<T> data;
    private MyClickListener mListener;

    public ShowButtonLayoutData(Context context, ShowButtonLayout layout, List<T> data, MyClickListener mListener) {
        this.context = context;
        this.layout = layout;
        this.data = data;
        this.mListener = mListener;
    }

    //自定义接口，用于回调按钮点击事件到Activity
    public interface MyClickListener{
        public void clickListener(View v, double lot, double lat);
    }

    public void updata(){
        this.data.clear();
        layout.removeAllViews();
    }

    public void setView(int layout){
        this.layoutView = layout;
    }

    private int layoutView = -1;

    public void setData() {
        if (null == data)return;
        TextView views[] = new TextView[data.size()];
        //热门数据源
        for (int i = 0; i < data.size(); i++) {
            final TextView view = (TextView) LayoutInflater.from(context).inflate(layoutView == -1?R.layout.hot_search_tv :layoutView, layout, false);
            if (data.get(i) instanceof String){
                view.setText((String)data.get(i));
                view.setTag(data.get(i));
            }

            views[i] = view;
            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //String tag = (String) v.getTag();
                    if(data.get(finalI) instanceof String){
                        if (mListener != null){
                            mListener.clickListener(v,0,0);
                        }
                    }
                }
            });
            layout.addView(view);

        }
    }
}
