package com.taiclouds.Moke.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.taiclouds.Moke.R;

/**
 * Created by MichaelLee826 on 2016-05-05-0005.
 */
public class Adapter_LocationCities extends BaseAdapter {
    private String[] cityStrings;
    private Context context;

    public class ViewHolder{
        TextView cityNameTextView = null;
    }

    public Adapter_LocationCities(Context context, String[] cityStrings) {
        this.context = context;
        this.cityStrings = cityStrings;
    }

    @Override
    public int getCount() {
        return cityStrings.length;
    }

    @Override
    public Object getItem(int position) {
        return cityStrings[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = cityStrings[position];
        ViewHolder viewHolder = null;

        if (viewHolder == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.main_locationcitiesitem, null);

            viewHolder = new ViewHolder();
            viewHolder.cityNameTextView = (TextView)convertView.findViewById(R.id.LocationCitiesItemNameTextView);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.cityNameTextView.setText(name);

        return convertView;
    }
}
