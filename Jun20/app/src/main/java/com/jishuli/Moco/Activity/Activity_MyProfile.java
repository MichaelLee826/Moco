package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.jishuli.Moco.ExitApplication;
import com.jishuli.Moco.R;

public class Activity_MyProfile extends Activity {
    private ImageButton frontPageImageButton = null;
    private ImageButton myClassImageButton = null;
    private ImageButton myProfileImageButton = null;

    private ImageButton myAgencyImageButton;
    private ImageButton myFeedbackImageButton;

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
            intent.setClass(Activity_MyProfile.this, Activity_SignIn.class);
            startActivity(intent);
        }
        else {
            setContentView(R.layout.activity_myprofile);
            findViews();            //1.找到各种控件
            setListeners();         //2.设置按钮监听器
        }
    }

    //1.找到各种控件
    public void findViews(){
        frontPageImageButton = (ImageButton)findViewById(R.id.frontPage);
        myClassImageButton = (ImageButton)findViewById(R.id.schedule);
        myProfileImageButton = (ImageButton)findViewById(R.id.profile);

        myAgencyImageButton = (ImageButton)findViewById(R.id.MyProfileMyAgencyImageButton);
        myFeedbackImageButton = (ImageButton)findViewById(R.id.MyProfileMyFeedbackImageButton);

        frontPageImageButton.setBackgroundResource(R.drawable.bt_frontpage);
        myClassImageButton.setBackgroundResource(R.drawable.bt_schedule);
        myProfileImageButton.setBackgroundResource(R.drawable.bt_myprofile_pressed);
    }

    //2.设置按钮监听器
    public void setListeners(){
        //我的机构
        myAgencyImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity_MyProfile.this, Activity_MyAgency.class);
                startActivity(intent);
            }
        });

        //我的反馈
        myFeedbackImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity_MyProfile.this, Activity_MyFeedback.class);
                startActivity(intent);
            }
        });

        //设置导航按钮的监听器（只需设置第一和第二个）
        frontPageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(Activity_MyProfile.this, Activity_Main.class);
                startActivity(intent);
            }
        });

        myClassImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(Activity_MyProfile.this, Activity_MyClass.class);
                startActivity(intent);
            }
        });
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
