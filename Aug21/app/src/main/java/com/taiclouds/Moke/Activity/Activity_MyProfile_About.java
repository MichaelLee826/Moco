package com.taiclouds.Moke.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.taiclouds.Moke.R;
import com.umeng.analytics.MobclickAgent;

public class Activity_MyProfile_About extends Activity {
    private ImageButton backButton;
    private TextView versionTextView;
    private Button checkUpdateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        init();                 //1.初始化
        setListeners();         //2.设置监听器
    }

    //1.初始化
    public void init(){
        backButton = (ImageButton)findViewById(R.id.AboutBackgroundLayoutBackButton);
        versionTextView = (TextView)findViewById(R.id.AboutVersionTextView) ;
        checkUpdateButton = (Button)findViewById(R.id.AboutCheckUpdateButton);
    }

    //2.设置监听器
    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_MyProfile_About.this.finish();
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