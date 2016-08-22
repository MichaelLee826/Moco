package com.taiclouds.Moke.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.taiclouds.Moke.R;
import com.umeng.analytics.MobclickAgent;

public class Activity_ChangePassword extends AppCompatActivity {
    private ImageButton backButton;
    private EditText originalPasswordEditText;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);

        init();                         //1.初始化
        setListeners();                 //2.设置监听器
    }

    //1.初始化
    public void init(){
        backButton = (ImageButton)findViewById(R.id.ChangePassword_BackgroundLayout_BackButton);
        originalPasswordEditText = (EditText)findViewById(R.id.ChangePassword_OriginalPassword_EditText);
        newPasswordEditText = (EditText)findViewById(R.id.ChangePassword_NewPassword_EditText);
        confirmPasswordEditText = (EditText)findViewById(R.id.ChangePassword_ConfirmPassword_EditText);
        confirmButton = (Button)findViewById(R.id.ChangePassword_Confirm_Button);
    }

    //2.设置监听器
    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_ChangePassword.this.finish();
            }
        });

        //确认按钮
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //2-1.检查是否有输入
                //2-2.检查原始密码是否正确
                //2-3.检查新密码和原始密码是否相同
                //2-4.检查两次输入的密码是否一样
                if (checkInput() && checkValid() && checkIdentity() && checkConsistency()){
                    updatePassword();               //2-5.提交新密码
                }
                else {
                    System.out.println("不能修改");
                }
            }
        });
    }

    //2-1.检查是否有输入
    public boolean checkInput(){
        if (originalPasswordEditText.getText().toString().length() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_ChangePassword.this);
            builder.setTitle("请输入原始密码");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return false;
        }

        if (newPasswordEditText.getText().toString().length() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_ChangePassword.this);
            builder.setTitle("请输入新密码");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return false;
        }

        if (confirmPasswordEditText.getText().toString().length() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_ChangePassword.this);
            builder.setTitle("请再次输入新密码");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return false;
        }
        return true;
    }

    //2-2.检查原始密码是否正确
    public boolean checkValid(){
        //获得原始密码
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        String password = sharedPreferences.getString("password", null);

        if (!originalPasswordEditText.getText().toString().equals(password)){
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_ChangePassword.this);
            builder.setTitle("原始密码不正确");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return false;
        }
        return true;
    }

    //2-3.检查新密码和原始密码是否相同
    public boolean checkIdentity(){
        //获得原始密码
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        String password = sharedPreferences.getString("password", null);

        if (newPasswordEditText.getText().toString().equals(password)){
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_ChangePassword.this);
            builder.setTitle("新密码与原密码相同");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return false;
        }
        return true;
    }

    //2-4.检查两次输入的密码是否一样
    public boolean checkConsistency(){
        String s1 = newPasswordEditText.getText().toString();
        String s2 = confirmPasswordEditText.getText().toString();

        if (!s1.equals(s2)){
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_ChangePassword.this);
            builder.setTitle("两次输入的密码不同");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return false;
        }
        return true;
    }

    //2-5.提交新密码
    public void updatePassword(){
        System.out.println("修改成功");

        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_ChangePassword.this);
        builder.setTitle("修改成功");
        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setClass(Activity_ChangePassword.this, Activity_EditProfile.class);
                startActivity(intent);
            }
        });
        builder.create().show();
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
