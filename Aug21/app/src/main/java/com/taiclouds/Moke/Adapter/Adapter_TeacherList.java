package com.taiclouds.Moke.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.taiclouds.Moke.AppClass.Teacher;
import com.taiclouds.Moke.R;

import java.util.List;

/**
 * Created by MichaelLee826 on 2016-08-10-0010.
 */
public class Adapter_TeacherList extends BaseAdapter {
    private Context context;
    private List<Teacher> teacherList;

    public class ViewHolder{
        ImageView avatarImageView;
        TextView nameTextView;
    }

    public Adapter_TeacherList(Context context, List<Teacher> teacherList) {
        this.context = context;
        this.teacherList = teacherList;
    }

    @Override
    public int getCount() {
        int i;
        if (teacherList.size() == 0){
            i = 0;
        }
        else {
            i = teacherList.size();
        }
        return i;
    }

    @Override
    public Object getItem(int position) {
        return teacherList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Teacher teacher = teacherList.get(position);
        ViewHolder viewHolder = null;

        convertView = LayoutInflater.from(context).inflate(R.layout.teacherlist_item, null);
        viewHolder = new ViewHolder();
        viewHolder.avatarImageView = (ImageView)convertView.findViewById(R.id.TeacherList_Item_avatarImageView);
        viewHolder.nameTextView = (TextView)convertView.findViewById(R.id.TeacherList_Item_nameTextView);

        convertView.setTag(viewHolder);

        viewHolder.avatarImageView.setImageBitmap(teacher.getAvatar());
        viewHolder.nameTextView.setText(teacher.getName());

        return convertView;
    }
}
