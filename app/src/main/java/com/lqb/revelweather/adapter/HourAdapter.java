package com.lqb.revelweather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqb.revelweather.R;

import java.util.List;

public class HourAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> list;

    public HourAdapter(List<String> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hour_item, parent, false);
        return new HourAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HourAdapter.ViewHolder viewHolder = (HourAdapter.ViewHolder) holder;
        viewHolder.time.setText(list.get(position * 4));
        viewHolder.cond_code.setImageResource(Integer.parseInt(list.get((position * 4) + 1)));
        viewHolder.cond_txt.setText(list.get((position * 4) + 2));
        viewHolder.tmp.setText(list.get((position * 4) + 3));
    }

    @Override
    public int getItemCount() {
        return list.size() / 4;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView time, cond_txt, tmp;
        ImageView cond_code;

        ViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.tv_time);
            cond_txt = itemView.findViewById(R.id.tv_txt);
            tmp = itemView.findViewById(R.id.hour_tmp);
            cond_code = itemView.findViewById(R.id.iv_code);
        }
    }
}
