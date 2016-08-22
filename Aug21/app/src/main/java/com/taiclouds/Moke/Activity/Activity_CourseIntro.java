package com.taiclouds.Moke.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.taiclouds.Moke.R;
import com.umeng.analytics.MobclickAgent;

public class Activity_CourseIntro extends Activity {
    private static final int TEACHER_INTRO = 2;

    private String content;
    private ImageButton backButton;
    private EditText contentEditText;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courseintro);

        init();                                 //1.初始化
        setListeners();                         //2.设置监听器
    }

    //1.初始化
    public void init(){
        backButton = (ImageButton)findViewById(R.id.CourseIntro_BackgroundLayout_BackButton);
        contentEditText = (EditText)findViewById(R.id.CourseIntro_Content_EditText);
        confirmButton = (Button)findViewById(R.id.CourseIntro_Confirm_Button);

        //填上之前的内容
        SharedPreferences sharedPreferences = getSharedPreferences("courseIntro", Activity.MODE_PRIVATE);
        content = sharedPreferences.getString("content", null);
        if (content != null){
            contentEditText.setText(content);
        }
    }

    //2.设置监听器
    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveUp();                   //2-1.放弃修改
            }
        });

        //确认
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = contentEditText.getText().toString();

                //将所填的内容保存起来
                SharedPreferences sharedPreferences = getSharedPreferences("courseIntro", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("content", content);
                editor.apply();

                Intent intent = new Intent();
                intent.putExtra("result", content);
                setResult(TEACHER_INTRO, intent);
                finish();
            }
        });
    }

    //2-1.放弃修改
    public void giveUp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_CourseIntro.this);
        builder.setMessage("放弃修改吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Activity_CourseIntro.this.finish();
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
