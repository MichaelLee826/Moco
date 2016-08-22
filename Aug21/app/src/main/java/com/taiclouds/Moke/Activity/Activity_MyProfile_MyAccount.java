package com.taiclouds.Moke.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.taiclouds.Moke.R;
import com.umeng.analytics.MobclickAgent;

public class Activity_MyProfile_MyAccount extends Activity {
    private ImageButton backButton;

    private TextView pointTextView;
    private TextView couponTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccount);

        init();                         //1.初始化
        setListeners();                 //2.设置监听器
    }

    //1.初始化
    public void init(){
        backButton = (ImageButton)findViewById(R.id.MyAccount_BackgroundLayout_BackButton);
        pointTextView = (TextView)findViewById(R.id.MyAccount_Points_TextView);
        couponTextView = (TextView)findViewById(R.id.MyAccount_Coupon_TextView);
    }

    //2.设置监听器
    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_MyProfile_MyAccount.this.finish();
            }
        });
    }

    //按下返回键，返回到首页
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Activity_MyProfile_MyAccount.this.finish();
        }
        return super.onKeyDown(keyCode, event);
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
