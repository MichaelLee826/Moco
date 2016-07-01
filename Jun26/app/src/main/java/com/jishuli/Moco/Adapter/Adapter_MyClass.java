package com.jishuli.Moco.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jishuli.Moco.R;

/**
 * Created by MichaelLee826 on 2016-04-23-0023.
 */
public class Adapter_MyClass extends BaseAdapter {
    private Context context;
    private String[] statusStrings;
    private String[] courseIDStrings;
    private String[] courseNameStrings;
    private String[] cityNameStrings;
    private String[] districtNameStrings;
    private String[] addressStrings;
    private String[] beginTimeStrings;
    private String[] endTimeStrings;
    private String[] courseWeek1Strings;
    private String[] courseTime1Strings;
    private String[] courseWeek2Strings;
    private String[] courseTime2Strings;
    private String[] institutionNameStrings;
    private String[] teacherNameStrings;

    public class ViewHolder{
        TextView courseNameTextView, institutionNameTextView, teacherNameTextView, addressTextView;
        TextView dateTextView, courseWeek1TextView, courseTime1TextView, courseWeek2TextView, courseTime2TextView = null;
        TextView statusTextView;
    }

    public Adapter_MyClass(Context context, String[] statusStrings, String[] courseIDStrings,
                           String[] courseNameStrings, String[] cityNameStrings, String[] districtNameStrings,
                           String[] addressStrings, String[] beginTimeStrings, String[] endTimeStrings,
                           String[] courseWeek1Strings, String[] courseTime1Strings, String[] courseWeek2Strings,
                           String[] courseTime2Strings, String[] institutionNameStrings, String[] teacherNameStrings) {

        this.context = context;
        this.statusStrings = statusStrings;
        this.courseIDStrings = courseIDStrings;
        this.courseNameStrings = courseNameStrings;
        this.cityNameStrings = cityNameStrings;
        this.districtNameStrings = districtNameStrings;
        this.addressStrings = addressStrings;
        this.beginTimeStrings = beginTimeStrings;
        this.endTimeStrings = endTimeStrings;
        this.courseWeek1Strings = courseWeek1Strings;
        this.courseTime1Strings = courseTime1Strings;
        this.courseWeek2Strings = courseWeek2Strings;
        this.courseTime2Strings = courseTime2Strings;
        this.institutionNameStrings = institutionNameStrings;
        this.teacherNameStrings = teacherNameStrings;
    }

    @Override
    public int getCount() {
        return courseIDStrings.length;
    }

    @Override
    public Object getItem(int position) {
        return courseIDStrings[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String courseName = courseNameStrings[position];
        String institutionName = "机构：" + institutionNameStrings[position];
        String teacherName = "讲师：" + teacherNameStrings[position];
        String address = "上课地点：" + cityNameStrings[position] + districtNameStrings[position] + addressStrings[position];
        String date = beginTimeStrings[position] + "至" + endTimeStrings[position];
        String courseWeek1 = courseWeek1Strings[position];
        String courseTime1 = courseTime1Strings[position];
        String status = statusStrings[position];

        StringBuilder stringBuilder1 = new StringBuilder("每周");
        if (courseWeek1.contains("1"))  stringBuilder1.append("一、");
        if (courseWeek1.contains("2"))  stringBuilder1.append("二、");
        if (courseWeek1.contains("3"))  stringBuilder1.append("三、");
        if (courseWeek1.contains("4"))  stringBuilder1.append("四、");
        if (courseWeek1.contains("5"))  stringBuilder1.append("五、");
        if (courseWeek1.contains("6"))  stringBuilder1.append("六、");
        if (courseWeek1.contains("7"))  stringBuilder1.append("日、");
        courseWeek1 = stringBuilder1.substring(0, stringBuilder1.length() - 1);
        courseTime1 = courseTime1.substring(0, 2) + ":" + courseTime1.substring(2, 4) + "-" + courseTime1.substring(7, 9) + ":" + courseTime1.substring(9, 11);

        ViewHolder viewHolder = null;

        if (viewHolder == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.myclass_item, null);

            viewHolder = new ViewHolder();
            viewHolder.courseNameTextView = (TextView)convertView.findViewById(R.id.MyClassItemClassNameTextView);
            viewHolder.institutionNameTextView = (TextView)convertView.findViewById(R.id.MyClassItemAgencyNameTextView);
            viewHolder.teacherNameTextView = (TextView)convertView.findViewById(R.id.MyClassItemTeacherNameTextView);
            viewHolder.addressTextView = (TextView)convertView.findViewById(R.id.MyClassItemAddressTextView);
            viewHolder.dateTextView = (TextView)convertView.findViewById(R.id.MyClassItemDateTextView);
            viewHolder.courseWeek1TextView = (TextView)convertView.findViewById(R.id.MyClassItemCourseWeek1TextView);
            viewHolder.courseTime1TextView = (TextView)convertView.findViewById(R.id.MyClassItemCourseTime1TextView);
            viewHolder.statusTextView = (TextView)convertView.findViewById(R.id.MyClassItemStatusTextView);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.courseNameTextView.setText(courseName);
        viewHolder.institutionNameTextView.setText(institutionName);
        viewHolder.teacherNameTextView.setText(teacherName);
        viewHolder.addressTextView.setText(address);
        viewHolder.dateTextView.setText(date);
        viewHolder.courseWeek1TextView.setText(courseWeek1);
        viewHolder.courseTime1TextView.setText(courseTime1);
        viewHolder.statusTextView.setText(status);

        return convertView;
    }
}
