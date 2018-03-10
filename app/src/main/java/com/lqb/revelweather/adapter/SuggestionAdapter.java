package com.lqb.revelweather.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqb.revelweather.R;
import com.lqb.revelweather.bean.SuggestionBean;

import java.util.ArrayList;
import java.util.List;

public class SuggestionAdapter extends BaseAdapter {
    private Context mContext;
    private List<SuggestionBean> sBeans;

    public SuggestionAdapter(Context mContext, int[] images, String[] type, String[] intro, String[] detaile) {
        super();
        sBeans = new ArrayList<>();
        this.mContext = mContext;

        for (int i = 0; i < images.length; i++) {
            SuggestionBean sB = new SuggestionBean(images[i], type[i], intro[i], detaile[i]);
            sBeans.add(sB);
        }
    }

    @Override
    public int getCount() {
        return sBeans.size();
    }

    @Override
    public Object getItem(int i) {
        return sBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();

            view = LayoutInflater.from(mContext).inflate(R.layout.gridview_suggest, null);

            viewHolder.ivLifeStyle = view.findViewById(R.id.iv_lifeStyle);
            viewHolder.tvIndex = view.findViewById(R.id.tv_index);
            viewHolder.tvIntro = view.findViewById(R.id.tv_intro);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        SuggestionBean sBean = sBeans.get(i);
        viewHolder.ivLifeStyle.setImageResource(sBean.getImage());

        viewHolder.tvIndex.setText(sBean.getType());
        viewHolder.tvIntro.setText(sBean.getIntro());

        return view;
    }

    class ViewHolder {
        ImageView ivLifeStyle;
        TextView tvIndex;
        TextView tvIntro;
    }
}
