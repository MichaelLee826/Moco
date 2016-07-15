package com.jishuli.Moco.ExpandedPopupWindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jishuli.Moco.R;

import java.util.List;

/**
 * Created by MichaelLee826 on 2016-07-05-0005.
 */
public class SecondClassAdapter extends BaseAdapter {
    private Context context;
    private List<SecondClassItem> list;
    private int selectedPosition = 0;

    public SecondClassAdapter(Context context, List<SecondClassItem> list){
        this.context = context;
        this.list = list;
    }

    private class ViewHolder{
        TextView nameTV;
    }

    @Override
    public int getCount() {
        return list == null?0:list.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.right_listview_item, null);
            holder = new ViewHolder();
            holder.nameTV = (TextView) convertView.findViewById(R.id.right_item_name);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        //选中和没选中时，设置不同的颜色
        if (position == selectedPosition){
            holder.nameTV.setBackgroundResource(R.color.popup_right_pressed);
        }
        else{
            holder.nameTV.setBackgroundResource(R.drawable.selector_left_listview);
        }

        holder.nameTV.setText(list.get(position).getName());

        return convertView;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
