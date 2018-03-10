package com.lqb.revelweather.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lqb.revelweather.R;
import com.lqb.revelweather.bean.ManageBean;
import com.lqb.revelweather.bean.WeatherData.WeatherBean;
import com.lqb.revelweather.util.SPUtil;

import java.util.ArrayList;
import java.util.List;

public class ManageAdapter extends BaseAdapter {
    private Context context;
    private List<ManageBean> cityList;
    private boolean isShowDelete;   //删除图标，true是显示，false是不显示

    public ManageAdapter(Context context, List<WeatherBean> cities) {
        super();
        cityList = new ArrayList<>();
        this.context = context;

        for (WeatherBean city : cities) {
            int code = context.getResources().getIdentifier("pic_" + city.now.cond.cond_code,
                    "drawable", context.getPackageName());
            ManageBean manageBean = new ManageBean(city.basic.cityName, code,
                        city.forecastList.get(0).temperature.tmp_min, city.forecastList.get(0).temperature.tmp_max);
            cityList.add(manageBean);
        }
    }

    public void setIsShowDelete(boolean isShowDelete){
        this.isShowDelete = isShowDelete;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cityList.size();
    }

    @Override
    public Object getItem(int i) {
        return cityList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ManageAdapter.ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ManageAdapter.ViewHolder();

            view = LayoutInflater.from(context).inflate(R.layout.gridview_manage, null);

            viewHolder.cityName = view.findViewById(R.id.city_name);
            viewHolder.cityCond = view.findViewById(R.id.city_cond);
            viewHolder.tmpMin = view.findViewById(R.id.tmp_min);
            viewHolder.tmpMax = view.findViewById(R.id.tmp_max);
            viewHolder.delete = view.findViewById(R.id.delete_manage);
            viewHolder.gridView = view.findViewById(R.id.gridview_manage);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ManageAdapter.ViewHolder) view.getTag();
        }
        final ManageBean mBean = cityList.get(i);

        viewHolder.cityName.setText(mBean.getCityName());
        viewHolder.cityCond.setImageResource(mBean.getCityCode());
        viewHolder.tmpMin.setText(mBean.getTmpMin() + "°");
        viewHolder.tmpMax.setText(mBean.getTmpMax() + "°");

        viewHolder.delete.setVisibility(isShowDelete ? View.VISIBLE : View.GONE);//设置删除按钮是否显示

        viewHolder.delete.setOnClickListener(new MyClick(i));

        return view;
    }

    class MyClick implements View.OnClickListener {
        private int position;

        private MyClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            new  AlertDialog.Builder(context)
                    .setTitle("确认" )
                    .setMessage("确定删除该城市吗？")
                    .setPositiveButton("是" ,  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 执行删除选中城市操作
                            deleteCity(position);
                            cityList.remove(position);
                            String cityName = cityList.get(position).getCityName();
                            Toast.makeText(context, "刪除城市(" + cityName + ")成功", Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }
                    } )
                    .setNegativeButton("否" , null)
                    .show();

        }
    }

    class ViewHolder {
        TextView cityName;
        ImageView cityCond;
        TextView tmpMin;
        TextView tmpMax;
        ImageView delete;
        GridView gridView;
    }

    private void deleteCity(int item) {
        String[] cities = SPUtil.getSharedPreference(context, "cityNames");
        String[] str = new String[cities.length - 1];

        if (item == cities.length - 1) {
            System.arraycopy(cities, 0, str, 0, str.length);
        } else {
            System.arraycopy(cities, 0, str, 0, item);
            System.arraycopy(cities, item + 1, str, item, str.length - item);
        }
        SPUtil.setSharedPreference(context, "cityNames", str);
    }
}
