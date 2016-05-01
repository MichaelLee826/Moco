package com.jishuli.Moco.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jishuli.Moco.ExitApplication;
import com.jishuli.Moco.R;

public class Activity_SignIn extends AppCompatActivity {
    private EditText accountEditText;
    private EditText passwordEditText;
    private ImageButton signInButton;
    private TextView forgetPasswordTextView;
    private TextView signUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        //添加到activityList列表中
        ExitApplication.getInstance().addActivity(this);

        findViews();            //1.找到各种控件
        setListeners();         //2.设置监听器
    }

    //1.找到各种控件
    public void findViews(){
        accountEditText = (EditText)findViewById(R.id.SignInAccountEditText);
        passwordEditText = (EditText)findViewById(R.id.SignInPasswordEditText);
        signInButton = (ImageButton)findViewById(R.id.SignInImageButton);
        forgetPasswordTextView = (TextView)findViewById(R.id.SignInForgetPasswordTextView);
        signUpTextView = (TextView)findViewById(R.id.SignInRegisterTextView);
    }

    //2.设置监听器
    public void setListeners(){
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        forgetPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //跳转到注册页面
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity_SignIn.this, Activity_SignUp.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            Intent intent = new Intent();
            intent.setClass(Activity_SignIn.this, Activity_Main.class);
            startActivity(intent);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
