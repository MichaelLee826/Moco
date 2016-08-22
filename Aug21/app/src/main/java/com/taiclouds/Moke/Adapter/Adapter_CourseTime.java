package com.taiclouds.Moke.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.taiclouds.Moke.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MichaelLee826 on 2016-08-04-0004.
 */
public class Adapter_CourseTime extends BaseAdapter{
    Context context;
    ArrayList<HashMap<String, String>> arrayList;
    String weekDay, time;

    public class ViewHolder{
        TextView weekDayTextView = null;
        TextView timeTextView = null;
    }

    public Adapter_CourseTime(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (viewHolder == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.coursetime_item, null);

            viewHolder = new ViewHolder();
            viewHolder.weekDayTextView= (TextView)convertView.findViewById(R.id.CourseTime_Item_WeekDay_TextView);
            viewHolder.timeTextView = (TextView)convertView.findViewById(R.id.CourseTime_Item_Time_TextView);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        weekDay = arrayList.get(position).get("weekDay");
        time = arrayList.get(position).get("time");

        viewHolder.weekDayTextView.setText(weekDay);
        viewHolder.timeTextView.setText(time);

        return convertView;
    }
}
