package com.tzl.agriculture.mall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.model.AddressCode;

import java.util.List;

public class ChinaGuSpannerAdapter extends BaseAdapter {
    private List<AddressCode> mList;
    private Context mContext;

    private OnclickTi onclickTi;


    public interface OnclickTi{
       void clickData(String code);
    }

    public void setOnclickTi(OnclickTi onclickTi){
        this.onclickTi = onclickTi;
    }

    public ChinaGuSpannerAdapter(Context pContext, List<AddressCode> pList) {
        this.mContext = pContext;
        this.mList = pList;
    }

    public void update(List<AddressCode> pList){
        this.mList = pList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    /**
     * 下面是重要代码
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater _LayoutInflater=LayoutInflater.from(mContext);
        convertView=_LayoutInflater.inflate(R.layout.item_text, null);
        if(convertView!=null)
        {
            TextView textView=(TextView)convertView.findViewById(R.id.tv_tips);
            textView.setText(mList.get(position).getName());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onclickTi.clickData(mList.get(position).getCode());
                }
            });
        }
        return convertView;
    }
}