package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.jishuli.Moco.Adapter.Adapter_MyClass;
import com.jishuli.Moco.ExitApplication;
import com.jishuli.Moco.R;

import java.util.ArrayList;
import java.util.List;

public class Activity_MyClass extends Activity {
    private List<String> classList = new ArrayList<>();
    private ListAdapter listAdapter;
    private ListView myClassListView;

    private ImageButton frontPageImageButton = null;
    private ImageButton myClassImageButton = null;
    private ImageButton myProfileImageButton = null;

    //程序退出时的时间
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //添加到activityList列表中
        ExitApplication.getInstance().addActivity(this);

        //检查用户名和密码
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", null);
        String password = sharedPreferences.getString("password", null);

        if (userName == null | password == null){
            Intent intent = new Intent();
            intent.setClass(Activity_MyClass.this, Activity_SignIn.class);
            startActivity(intent);
        }
        else {
            setContentView(R.layout.activity_myclass);

            findViews();                    //1.找到各种控件
            setListeners();                 //2.设置按钮的监听器
            setListView();                  //3.设置课程列表
        }
    }

    //1.找到各种控件
    public void findViews(){
        myClassListView = (ListView)findViewById(R.id.MyClassListView);

        frontPageImageButton = (ImageButton)findViewById(R.id.frontPage);
        myClassImageButton = (ImageButton)findViewById(R.id.schedule);
        myProfileImageButton = (ImageButton)findViewById(R.id.profile);

        frontPageImageButton.setBackgroundResource(R.drawable.bt_frontpage);
        myClassImageButton.setBackgroundResource(R.drawable.bt_schedule_pressed);
        myProfileImageButton.setBackgroundResource(R.drawable.bt_myprofile);
    }

    //2.设置底部导航按钮的监听器（只需设置第一和第三个）
    public void setListeners(){
        frontPageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(Activity_MyClass.this, Activity_Main.class);
                startActivity(intent);

            }
        });

        myProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(Activity_MyClass.this, Activity_MyProfile.class);
                startActivity(intent);
            }
        });
    }

    //3.设置课程列表
    public void setListView(){
        for (int i = 0; i < 10; i++){
            classList.add("育儿课堂");
        }
        listAdapter = new Adapter_MyClass(this, classList);
        myClassListView.setAdapter(listAdapter);
    }

    //按两下返回键，退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis() - exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
            else {
                ExitApplication.getInstance().exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
