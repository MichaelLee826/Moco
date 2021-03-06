package com.taiclouds.Moke.ExpandedPopupWindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.taiclouds.Moke.R;

import java.util.List;

/**
 * Created by MichaelLee826 on 2016-07-05-0005.
 */
public class FirstClassAdapter extends BaseAdapter {
    private Context context;
    private List<FirstClassItem> list;
    private int selectedPosition = 0;

    public FirstClassAdapter(Context context, List<FirstClassItem> list) {
        this.context = context;
        this.list = list;
    }

    private class ViewHolder {
        TextView nameTV;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_left_item, null);
            holder = new ViewHolder();
            holder.nameTV = (TextView) convertView.findViewById(R.id.left_item_name);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        //选中和没选中时，设置不同的颜色
        if (position == selectedPosition){
            holder.nameTV.setBackgroundResource(R.color.popup_left_pressed);
        }
        else{
            holder.nameTV.setBackgroundResource(R.drawable.selector_left_listview);
        }

        holder.nameTV.setText(list.get(position).getName());

        //用于显示右箭头
        /*if (list.get(position).getSecondList() != null && list.get(position).getSecondList().size() > 0) {
            holder.nameTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_right, 0);
        } else {
            holder.nameTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }*/

        return convertView;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}