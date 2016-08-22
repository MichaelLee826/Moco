package com.taiclouds.Moke.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.taiclouds.Moke.Adapter.Adapter_CourseTime;
import com.taiclouds.Moke.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Activity_CourseTime extends Activity {
    private static final int DONE = 0;
    private static final int COURSE_TIME = 4;

    private String content;
    private String weekDay, time;
    private int beginHour, beginMinute, endHour, endMinute;

    private ImageButton backButton;
    private ImageButton plusButton;
    private ListView listView;
    private ArrayList<HashMap<String, String>> arrayList = null;
    private Adapter_CourseTime adapter;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coursetime);

        init();                                 //1.初始化
        setListeners();                         //2.设置监听器
        setListView();                          //3.设置ListView
    }

    //1.初始化
    public void init(){
        backButton = (ImageButton)findViewById(R.id.CourseTime_BackgroundLayout_BackButton);
        plusButton = (ImageButton)findViewById(R.id.CourseTime_BackgroundLayout_PlusButton);
        listView = (ListView)findViewById(R.id.CourseTime_ListView);
        confirmButton = (Button)findViewById(R.id.CourseTime_Confirm_Button);
    }

    //2.设置监听器
    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveUp();                           //2-1.放弃修改
            }
        });

        //添加时段
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = View.inflate(getApplicationContext(), R.layout.dialog_coursetime, null);

                Spinner spinner = (Spinner)view.findViewById(R.id.Dialog_CourseTIme_Spinner);
                final TimePicker beginTimePicker = (TimePicker)view.findViewById(R.id.Dialog_CourseTime_BeginTimePicker);
                final TimePicker endTimePicker = (TimePicker)view.findViewById(R.id.Dialog_CourseTime_EndTimePicker);

                //设置下拉菜单
                final String[] strings = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日",};
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(Activity_CourseTime.this, android.R.layout.simple_spinner_dropdown_item, strings);
                spinner.setAdapter(spinnerAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        weekDay = strings[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                final Calendar calendar = Calendar.getInstance();
                int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
                spinner.setSelection(day_of_week, true);

                //默认设置为当前时间
                beginHour = endHour = calendar.get(Calendar.HOUR_OF_DAY);
                beginMinute = endMinute = calendar.get(Calendar.MINUTE);

                beginTimePicker.setIs24HourView(true);
                endTimePicker.setIs24HourView(true);

                beginTimePicker.setCurrentHour(beginHour);
                beginTimePicker.setCurrentMinute(beginMinute);

                endTimePicker.setCurrentHour(endHour);
                endTimePicker.setCurrentMinute(endMinute);

                //显示对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_CourseTime.this);
                builder.setView(view);
                builder.setTitle("请选择开课时段");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String beginHourString, beginMinuteString, endHourString, endMinuteString;

                        beginHour = beginTimePicker.getCurrentHour();
                        beginMinute = beginTimePicker.getCurrentMinute();

                        if (beginHour < 10){
                            beginHourString = "0" + beginHour;
                        }
                        else {
                            beginHourString = String.valueOf(beginHour);
                        }

                        if (beginMinute < 10){
                            beginMinuteString = "0" + beginMinute;
                        }
                        else {
                            beginMinuteString = String.valueOf(beginMinute);
                        }

                        endHour = endTimePicker.getCurrentHour();
                        endMinute = endTimePicker.getCurrentMinute();


                        if (endHour < 10){
                            endHourString = "0" + endHour;
                        }
                        else {
                            endHourString = String.valueOf(endHour);
                        }

                        if (endMinute < 10){
                            endMinuteString = "0" + endMinute;
                        }
                        else {
                            endMinuteString = String.valueOf(endMinute);
                        }

                        time = beginHourString + ":" + beginMinuteString + " -- " + endHourString + ":" + endMinuteString;

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("weekDay", weekDay);
                        map.put("time", time);
                        arrayList.add(map);
                        Collections.sort(arrayList, new SortByTime());          //2-2.先根据日期排序
                        Collections.sort(arrayList, new SortByDate());          //2-3.再根据日期排序
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.show();
            }
        });

        //提交
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTimeList(arrayList);                                //2-4.保存临时数据

                if (adapter.getCount() != 0){
                    content = "OK";
                }
                else {
                    content = "empty";
                }

                Intent intent = new Intent();

                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < arrayList.size(); i++){
                    String weekDay = arrayList.get(i).get("weekDay");
                    String time = arrayList.get(i).get("time");

                    try {
                        jsonObject.put("weekDay" + i, weekDay);
                        jsonObject.put("time" + i, time);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                intent.putExtra("result", content);
                intent.putExtra("courseTime", jsonObject.toString());

                setResult(COURSE_TIME, intent);
                finish();
            }
        });
    }

    //3.设置ListView
    public void setListView(){
        arrayList = getTimeList();                          //3-1.取出数据

        adapter = new Adapter_CourseTime(this, arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String currentWeekDay = arrayList.get(position).get("weekDay");
                final String currentTime = arrayList.get(position).get("time");

                View tempView = View.inflate(getApplicationContext(), R.layout.dialog_coursetime, null);

                Spinner spinner = (Spinner) tempView.findViewById(R.id.Dialog_CourseTIme_Spinner);
                final TimePicker beginTimePicker = (TimePicker) tempView.findViewById(R.id.Dialog_CourseTime_BeginTimePicker);
                final TimePicker endTimePicker = (TimePicker) tempView.findViewById(R.id.Dialog_CourseTime_EndTimePicker);

                //设置下拉菜单
                final String[] strings = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日",};
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(Activity_CourseTime.this, android.R.layout.simple_spinner_dropdown_item, strings);
                spinner.setAdapter(spinnerAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        weekDay = strings[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                //设置为已选的日期
                int num = 0;
                for (int i = 0; i < strings.length; i++){
                    if (strings[i].equals(currentWeekDay)){
                        num = i;
                    }
                }
                spinner.setSelection(num, true);

                //设置为已选的时间
                beginHour = Integer.valueOf(currentTime.substring(0, 2));
                beginMinute = Integer.valueOf(currentTime.substring(3, 5));

                endHour = Integer.valueOf(currentTime.substring(9, 11));
                endMinute = Integer.valueOf(currentTime.substring(12, 14));

                beginTimePicker.setIs24HourView(true);
                endTimePicker.setIs24HourView(true);

                beginTimePicker.setCurrentHour(beginHour);
                beginTimePicker.setCurrentMinute(beginMinute);

                endTimePicker.setCurrentHour(endHour);
                endTimePicker.setCurrentMinute(endMinute);

                //显示对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_CourseTime.this);
                builder.setView(tempView);
                builder.setTitle("请选择开课时段");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String beginHourString, beginMinuteString, endHourString, endMinuteString;

                        beginHour = beginTimePicker.getCurrentHour();
                        beginMinute = beginTimePicker.getCurrentMinute();

                        if (beginHour < 10){
                            beginHourString = "0" + beginHour;
                        }
                        else {
                            beginHourString = String.valueOf(beginHour);
                        }

                        if (beginMinute < 10){
                            beginMinuteString = "0" + beginMinute;
                        }
                        else {
                            beginMinuteString = String.valueOf(beginMinute);
                        }

                        endHour = endTimePicker.getCurrentHour();
                        endMinute = endTimePicker.getCurrentMinute();


                        if (endHour < 10){
                            endHourString = "0" + endHour;
                        }
                        else {
                            endHourString = String.valueOf(endHour);
                        }

                        if (endMinute < 10){
                            endMinuteString = "0" + endMinute;
                        }
                        else {
                            endMinuteString = String.valueOf(endMinute);
                        }

                        time = beginHourString + ":" + beginMinuteString + " -- " + endHourString + ":" + endMinuteString;

                        if (weekDay.equals(currentWeekDay) && time.equals(currentTime)){
                            //与之前日期、时间完全相同，则什么也不做
                        }
                        else {
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("weekDay", weekDay);
                            map.put("time", time);
                            arrayList.add(map);
                            Collections.sort(arrayList, new SortByTime());          //2-2.先根据日期排序
                            Collections.sort(arrayList, new SortByDate());          //2-3.再根据日期排序
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

                builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        arrayList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });

                builder.show();
            }
        });
    }

    //2-1.放弃修改
    public void giveUp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_CourseTime.this);
        builder.setMessage("放弃修改吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Activity_CourseTime.this.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //什么也不做
            }
        });
        builder.create().show();
    }

    //2-2.根据时间排序
    public class SortByTime implements Comparator{
        @Override
        public int compare(Object lhs, Object rhs) {
            HashMap<String, String> map1 = (HashMap<String, String>) lhs;
            HashMap<String, String> map2 = (HashMap<String, String>) rhs;

            return map1.get("time").compareTo(map2.get("time"));
        }
    }

    //2-3.根据日期排序
    public class SortByDate implements Comparator{
        @Override
        public int compare(Object lhs, Object rhs) {
            HashMap<String, String> map1 = (HashMap<String, String>) lhs;
            HashMap<String, String> map2 = (HashMap<String, String>) rhs;

            int temp1 = 0;
            int temp2 = 1;

            if (map1.get("weekDay").equals("周一")){
                temp1 = 1;
            }
            if (map1.get("weekDay").equals("周二")){
                temp1 = 2;
            }
            if (map1.get("weekDay").equals("周三")){
                temp1 = 3;
            }
            if (map1.get("weekDay").equals("周四")){
                temp1 = 4;
            }
            if (map1.get("weekDay").equals("周五")){
                temp1 = 5;
            }
            if (map1.get("weekDay").equals("周六")){
                temp1 = 6;
            }
            if (map1.get("weekDay").equals("周日")){
                temp1 = 7;
            }

            if (map2.get("weekDay").equals("周一")){
                temp2 = 1;
            }
            if (map2.get("weekDay").equals("周二")){
                temp2 = 2;
            }
            if (map2.get("weekDay").equals("周三")){
                temp2 = 3;
            }
            if (map2.get("weekDay").equals("周四")){
                temp2 = 4;
            }
            if (map2.get("weekDay").equals("周五")){
                temp2 = 5;
            }
            if (map2.get("weekDay").equals("周六")){
                temp2 = 6;
            }
            if (map2.get("weekDay").equals("周日")){
                temp2 = 7;
            }

            int result = temp1 - temp2;
            return result;
        }
    }

    //2-4.保存临时数据
    public void saveTimeList(List<HashMap<String, String>> arrayList) {
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < arrayList.size(); i++) {
            HashMap<String, String> itemMap = arrayList.get(i);
            Iterator<Map.Entry<String, String>> iterator = itemMap.entrySet().iterator();
            JSONObject jsonObject = new JSONObject();

            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                try {
                    jsonObject.put(entry.getKey(), entry.getValue());
                }
                catch (JSONException e) {
                }
            }
            jsonArray.put(jsonObject);
        }

        SharedPreferences sharedPreferences = getSharedPreferences("courseTimeList", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("timeList", jsonArray.toString());
        editor.apply();
    }

    //3-1.取出数据
    public ArrayList<HashMap<String, String>> getTimeList() {
        arrayList = new ArrayList<HashMap<String, String>>();

        SharedPreferences sharedPreferences = getSharedPreferences("courseTimeList", Activity.MODE_PRIVATE);
        String result = sharedPreferences.getString("timeList", "");
        try {
            JSONArray jsonArray = new JSONArray(result);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> itemMap = new HashMap<String, String>();
                JSONArray names = jsonObject.names();
                if (names != null) {
                    for (int j = 0; j < names.length(); j++) {
                        String name = names.getString(j);
                        String value = jsonObject.getString(name);
                        itemMap.put(name, value);
                    }
                }
                arrayList.add(itemMap);
            }
        }
        catch (JSONException e) {
        }
        return arrayList;
    }

    //按下返回键
    @Override
    public void onBackPressed() {
        giveUp();                       //2-1.放弃修改
    }

    @Override
    protected void onResume() {
        //友盟数据统计
        MobclickAgent.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        //友盟数据统计
        MobclickAgent.onPause(this);
        super.onPause();
    }
}