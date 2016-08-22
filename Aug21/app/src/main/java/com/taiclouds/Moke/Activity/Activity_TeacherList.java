package com.taiclouds.Moke.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.taiclouds.Moke.Adapter.Adapter_TeacherList;
import com.taiclouds.Moke.AppClass.Teacher;
import com.taiclouds.Moke.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class Activity_TeacherList extends Activity {
    private static final int TEACHER_LIST = 3;

    private ImageButton backButton;

    private String content;
    private List<Teacher> teacherList = new ArrayList<>();
    private ListAdapter listAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacherlist);

        init();                                 //1.初始化
        setListeners();                         //2.设置监听器
        getData();                              //3.获得数据
        setListView();                          //4.设置ListView
    }

    //1.初始化
    public void init(){
        backButton = (ImageButton)findViewById(R.id.TeacherList_BackgroundLayout_BackButton);
        listView = (ListView)findViewById(R.id.TeacherList_ListView);
    }

    //2.设置监听器
    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_TeacherList.this.finish();
            }
        });
    }

    //3.获得数据
    public void getData(){

    }

    //4.设置ListView
    public void setListView(){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar_female1);

        for (int i = 0; i < 10; i++ ){
            teacherList.add(new Teacher(bitmap, "王大雷", "教授", "男"));
        }
        listAdapter = new Adapter_TeacherList(this, teacherList);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                content = teacherList.get(position).getName();

                Intent intent = new Intent();
                intent.putExtra("result", content);
                setResult(TEACHER_LIST, intent);
                finish();
            }
        });
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
