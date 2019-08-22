package com.tzl.agriculture.fragment.personal.activity.ccb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.ccb.cartinter.OnClickAddCloseListenter;
import com.tzl.agriculture.fragment.personal.activity.ccb.cartinter.OnClickListenterModel;
import com.tzl.agriculture.fragment.personal.activity.ccb.entity.CartInfo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Describe:
 */
public class ListBaseAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<CartInfo.DataBean.ItemsBean> list=new ArrayList<>();
    private int positionO;

    public ListBaseAdapter(Context context, int position, List<CartInfo.DataBean.ItemsBean> list) {
        this.positionO = position;
        this.layoutInflater = LayoutInflater.from(context);
        this.list.clear();
        this.list.addAll(list);
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (null == convertView) {
            convertView = layoutInflater.inflate(R.layout.cart_chlid, null);
            viewHolder = new ViewHolder(convertView,position);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.cbChild.setChecked(list.get(position).ischeck());
        viewHolder.tvChild.setText(list.get(position).getTitle());
        viewHolder.textView.setText("¥ " + list.get(position).getPrice());
        viewHolder.btnNum.setText(list.get(position).getNum()+"");
        Glide.with(context.getApplicationContext()).load(list.get(position).getImage()).into(viewHolder.imageView);
        viewHolder.cbChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListenterModel.onItemClick(viewHolder.cbChild.isChecked(), v, positionO, position);
            }
        });
        return convertView;
    }
    // CheckBox2接口的方法
    private OnClickListenterModel onClickListenterModel = null;

    public void setOnClickListenterModel(OnClickListenterModel listener) {
        this.onClickListenterModel = listener;
    }
    // 数量接口的方法
    private OnClickAddCloseListenter onClickAddCloseListenter = null;
    public void setOnClickAddCloseListenter(OnClickAddCloseListenter listener) {
        this.onClickAddCloseListenter = listener;
    }


    class ViewHolder implements View.OnClickListener{

        public TextView tvChild;
        public CheckBox cbChild;
        public TextView textView;
        public ImageView imageView;
        private Button btnAdd;
        private Button btnNum;
        private Button btnClose;
        private int position;

        public ViewHolder(View view, int position) {
            this.position = position;
            tvChild = (TextView) view.findViewById(R.id.tv_child);
            cbChild = (CheckBox) view.findViewById(R.id.cb_child);
            textView = view.findViewById(R.id.item_chlid_money);
            imageView =view.findViewById(R.id.item_chlid_image);
            btnAdd= (Button) view.findViewById(R.id.item_chlid_add);
            btnAdd.setOnClickListener(this);
            btnNum= (Button) view.findViewById(R.id.item_chlid_num);
            btnClose= (Button) view.findViewById(R.id.item_chlid_close);
            btnClose.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.item_chlid_add:
                    onClickAddCloseListenter.onItemClick(v,2,positionO,position, Integer.valueOf(btnNum.getText().toString()));
                    break;
                case R.id.item_chlid_close:
                    onClickAddCloseListenter.onItemClick(v,1,positionO,position, Integer.valueOf(btnNum.getText().toString()));
                    break;
            }
        }
    }
}
