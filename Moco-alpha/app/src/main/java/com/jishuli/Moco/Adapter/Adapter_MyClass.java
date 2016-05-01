package com.jishuli.Moco.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jishuli.Moco.R;

import java.util.List;

/**
 * Created by MichaelLee826 on 2016-04-23-0023.
 */
public class Adapter_MyClass extends BaseAdapter {
    public List<String> classList;
    Context context;

    public class ViewHolder{
        TextView classNameTextView = null;
    }

    public Adapter_MyClass(Context context, List<String> classList) {
        this.context = context;
        this.classList = classList;
    }

    @Override
    public int getCount() {
        return classList.size();
    }

    @Override
    public Object getItem(int position) {
        return classList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = classList.get(position).toString();
        ViewHolder viewHolder = null;

        if (viewHolder == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.myclassitem, null);

            viewHolder = new ViewHolder();
            viewHolder.classNameTextView = (TextView)convertView.findViewById(R.id.MyClassItemClassNameTextView);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.classNameTextView.setText(name);

        return convertView;
    }
}
