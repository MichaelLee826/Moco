package com.jishuli.Moco.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jishuli.Moco.AppClass.ClassItem;
import com.jishuli.Moco.R;

import java.util.List;

public class Adapter_ClassItem extends BaseAdapter {
    public List<ClassItem> classes;
    Context context;

    public class ViewHolder{
        TextView classNumberTextView = null;
        ImageView backgroundImageView = null;
    }

    //构造函数
    public Adapter_ClassItem(Context context, List<ClassItem> classes) {
        this.classes = classes;
        this.context = context;
    }

    @Override
    public int getCount() {
        return classes.size();
    }

    @Override
    public Object getItem(int position) {
        return classes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ClassItem classItem = (ClassItem)getItem(position);
        ViewHolder viewHolder = null;

        if (viewHolder == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.main_classitem, null);

            viewHolder = new ViewHolder();
            viewHolder.classNumberTextView = (TextView)convertView.findViewById(R.id.classNumberTextView);
            viewHolder.backgroundImageView = (ImageView)convertView.findViewById(R.id.backgroundImageView);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.classNumberTextView.setText(classItem.classNumber + "\r\n" + "节课");
        viewHolder.backgroundImageView.setImageResource(classItem.classImage);

        return convertView;
    }
}