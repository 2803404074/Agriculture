package com.tzl.agriculture.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.tzl.agriculture.R;
import com.tzl.agriculture.main.WellComePageActivity;

import java.util.ArrayList;
import java.util.List;

public class CommentImageCroDialog extends Dialog implements ViewPager.OnPageChangeListener {
    private Context mContext;
    private ViewPager view_pager;
    private TextView text_page;
    private List<String> mStrings=new ArrayList<>();
    private PagerAdapter mPagerAdapter;

    public CommentImageCroDialog(Context context) {
        super(context);
        this.mContext=context;
        View view= LayoutInflater.from(context).inflate(R.layout.comment_cro_dialog,null,false);
        setContentView(view);
        view_pager=findViewById(R.id.view_pager);
        text_page=findViewById(R.id.text_page);
        view_pager.setAdapter(mPagerAdapter=new PagerAdapter() {
            @Override
            public int getCount() {
                return mStrings.size();
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View view= LayoutInflater.from(mContext).inflate(R.layout.img_fragment,null,false);
                ImageView imageView = view.findViewById(R.id.hxxq_img);
                Glide.with(context.getApplicationContext()).load(mStrings.get(position)).apply(
                        RequestOptions.bitmapTransform(
                                new RoundedCorners(10))).into(imageView);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
                super.destroyItem(container, position, object);

            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }
        });
        view_pager.addOnPageChangeListener(this);

        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width=WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height=WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.gravity= Gravity.CENTER;
        window.setAttributes(attributes);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        text_page.setText((position+1)+"/"+mStrings.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public void setData(int index,List<String> e){
        if(e==null||e.size()==0){
            return;
        }
        mStrings.clear();
        mStrings.addAll(e);
        text_page.setText((index+1)+"/"+mStrings.size());
        mPagerAdapter.notifyDataSetChanged();
        show();
    }
}
