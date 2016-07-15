package com.jishuli.Moco.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jishuli.Moco.R;

/**
 * Created by MichaelLee826 on 2016-06-30-0030.
 */
public class Adapter_Category extends BaseAdapter {
    Context context;
    String[] category;

    public class ViewHolder{
        TextView itemTextView = null;
    }

    public Adapter_Category(Context context, String[] category) {
        this.context = context;
        this.category = category;
    }

    @Override
    public int getCount() {
        return category.length;
    }

    @Override
    public Object getItem(int position) {
        return category[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String item = category[position];

        ViewHolder viewHolder = null;

        if (viewHolder == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.category_item, null);

            viewHolder = new ViewHolder();
            viewHolder.itemTextView = (TextView)convertView.findViewById(R.id.categoryItemTextView) ;

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.itemTextView.setText(item);

        return convertView;
    }
}
