package com.jishuli.Moco.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jishuli.Moco.AppClass.ClassDetailItem;
import com.jishuli.Moco.R;

import java.util.List;

public class Adapter_ClassDetailItem extends BaseAdapter {
    private List<ClassDetailItem> classDetails;
    private Context context;

    public class ViewHolder{
        TextView statusTextView = null;
        TextView dateTextView = null;
        TextView timeTextView = null;
        TextView loginNumberTextView = null;
        TextView classNameTextView = null;
        TextView agentNameTextView = null;
        TextView teacherNameTextView = null;
        TextView distanceTextView = null;
        TextView rateTextView = null;
        ImageView heartsImageViews[] = new ImageView[5];
    }

    //构造函数
    public Adapter_ClassDetailItem(Context context, List<ClassDetailItem> classDetails){
        this.classDetails = classDetails;
        this.context = context;
    }

    @Override
    public int getCount() {
        int i;
        if (classDetails.size() == 0){
            i = 0;
        }
        else {
            i = classDetails.size();
        }
        return i;
    }

    @Override
    public Object getItem(int position) {
        return classDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ClassDetailItem classDetailItem = (ClassDetailItem)getItem(position);
        ViewHolder viewHolder = null;

        if (viewHolder == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.classdetailitem, null);

            viewHolder = new ViewHolder();
            viewHolder.statusTextView = (TextView)convertView.findViewById(R.id.statusTextView);
            viewHolder.dateTextView = (TextView)convertView.findViewById(R.id.dateTextView);
            //viewHolder.timeTextView = (TextView)convertView.findViewById(R.id.timeTextView);
            viewHolder.loginNumberTextView = (TextView)convertView.findViewById(R.id.loginNumberTextView);
            viewHolder.classNameTextView = (TextView)convertView.findViewById(R.id.classNameTextView);
            viewHolder.agentNameTextView = (TextView)convertView.findViewById(R.id.agentNameTextView);
            viewHolder.teacherNameTextView = (TextView)convertView.findViewById(R.id.teacherNameTextView);
            viewHolder.distanceTextView = (TextView)convertView.findViewById(R.id.distanceTextView);
            viewHolder.rateTextView = (TextView)convertView.findViewById(R.id.rateTextView);
            viewHolder.heartsImageViews[0] = (ImageView)convertView.findViewById(R.id.rateImageView1);
            viewHolder.heartsImageViews[1] = (ImageView)convertView.findViewById(R.id.rateImageView2);
            viewHolder.heartsImageViews[2] = (ImageView)convertView.findViewById(R.id.rateImageView3);
            viewHolder.heartsImageViews[3] = (ImageView)convertView.findViewById(R.id.rateImageView4);
            viewHolder.heartsImageViews[4] = (ImageView)convertView.findViewById(R.id.rateImageView5);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }


        if (classDetailItem.status == "已开课"){
            viewHolder.statusTextView.setText("已开课");
        }
        else if (classDetailItem.status == "未开课"){
            viewHolder.statusTextView.setText("未开课");
        }
        else {
            viewHolder.statusTextView.setText("正在进行");
        }

        //格式化时间
        String beginTime = classDetailItem.beginTime.replace(" ","");
        beginTime = beginTime.replace(" ", "");
        beginTime = beginTime.trim();
        beginTime = beginTime.replace("-", ".");
        beginTime = beginTime.substring(0, 10);
        //不显示时间      + " " + beginTime.substring(10, 12) + ":" + beginTime.substring(12, 14);

        String endTime = classDetailItem.endTime.replace(" ","");
        endTime = endTime.replace(" ", "");
        endTime = endTime.trim();
        endTime = endTime.replace("-", ".");
        endTime = endTime.substring(0, 10);
        //不显示时间      + "  " + endTime.substring(10, 12) + ":" + endTime.substring(12, 14);

        viewHolder.dateTextView.setText(beginTime + "\n" + endTime);
        viewHolder.loginNumberTextView.setText(classDetailItem.studentIn + "人报名");
        viewHolder.classNameTextView.setText(classDetailItem.courseName);
        viewHolder.agentNameTextView.setText(classDetailItem.institutionName);
        viewHolder.teacherNameTextView.setText(classDetailItem.teacherName);
        viewHolder.rateTextView.setText(classDetailItem.score + "分");
        //viewHolder.distanceTextView.setText(classDetailItem.distance + "KM");

        //设置心形的数量
        switch (classDetailItem.score){
            case 0:
            {
                for (int i = 0; i < 5; i++){
                    viewHolder.heartsImageViews[i].setImageResource(R.drawable.heart_gray);
                }
                break;
            }

            case 1:
            {
                viewHolder.heartsImageViews[0].setImageResource(R.drawable.heart_green);
                for (int i = 1; i < 4; i++){
                    viewHolder.heartsImageViews[i].setImageResource(R.drawable.heart_gray);
                }
                break;
            }

            case 2:
            {
                for (int i = 0; i < 2; i++){
                    viewHolder.heartsImageViews[i].setImageResource(R.drawable.heart_green);
                }
                for (int i = 2; i < 5; i++){
                    viewHolder.heartsImageViews[i].setImageResource(R.drawable.heart_gray);
                }
                break;
            }

            case 3:
            {
                for (int i = 0; i < 3; i++){
                    viewHolder.heartsImageViews[i].setImageResource(R.drawable.heart_green);
                }
                for (int i = 3; i < 5; i++){
                    viewHolder.heartsImageViews[i].setImageResource(R.drawable.heart_gray);
                }
                break;
            }

            case 4:
            {
                for (int i = 0; i < 4; i++){
                    viewHolder.heartsImageViews[i].setImageResource(R.drawable.heart_green);
                }
                viewHolder.heartsImageViews[4].setImageResource(R.drawable.heart_gray);
                break;
            }

            case 5:
            {
                for (int i = 0; i < 5; i++){
                    viewHolder.heartsImageViews[i].setImageResource(R.drawable.heart_green);
                }
                break;
            }
        }
        return convertView;
    }
}