package com.taiclouds.Moke.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.taiclouds.Moke.R;
import com.umeng.analytics.MobclickAgent;

public class Activity_Comment extends Activity {
    private ImageButton backButton;
    private ImageView heart1, heart2, heart3, heart4, heart5;
    private EditText commentEditText;
    private Button confirmButton;

    private int rate = 0;
    private String commentString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        init();                         //1.初始化
        setListeners();                 //2.设置监听器
    }

    //1.初始化
    public void init(){
        backButton = (ImageButton)findViewById(R.id.Comment_BackgroundLayout_BackButton);
        heart1 = (ImageView)findViewById(R.id.Comment_heart1_ImageView);
        heart2 = (ImageView)findViewById(R.id.Comment_heart2_ImageView);
        heart3 = (ImageView)findViewById(R.id.Comment_heart3_ImageView);
        heart4 = (ImageView)findViewById(R.id.Comment_heart4_ImageView);
        heart5 = (ImageView)findViewById(R.id.Comment_heart5_ImageView);
        commentEditText = (EditText)findViewById(R.id.Comment_Content_EditText);
        confirmButton = (Button)findViewById(R.id.Comment_Confirm_Button);

        //填上之前的内容
        SharedPreferences sharedPreferences = getSharedPreferences("courseIntro", Activity.MODE_PRIVATE);
        rate = sharedPreferences.getInt("rate", 0);
        commentString = sharedPreferences.getString("comment", null);

    }

    //2.设置监听器
    public void setListeners(){
        //返回按钮的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveUp();                                           //2-1.放弃修改
            }
        });

        //心形
        heart1.setOnClickListener(new heartListener());             //2-2.心形按钮监听器
        heart2.setOnClickListener(new heartListener());
        heart3.setOnClickListener(new heartListener());
        heart4.setOnClickListener(new heartListener());
        heart5.setOnClickListener(new heartListener());

        confirmButton.setOnClickListener(new confirmListener());   //2-3.确认按钮监听器
    }

    //2-1.放弃修改
    public void giveUp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Comment.this);
        builder.setMessage("放弃修改吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Activity_Comment.this.finish();
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

    //2-2.心形按钮监听器
    public class heartListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (v.getId() == heart1.getId()){
                rate = 1;
                heart1.setImageResource(R.drawable.heart_green);
                heart2.setImageResource(R.drawable.heart_gray);
                heart3.setImageResource(R.drawable.heart_gray);
                heart4.setImageResource(R.drawable.heart_gray);
                heart5.setImageResource(R.drawable.heart_gray);
            }
            if (v.getId() == heart2.getId()){
                rate = 2;
                heart1.setImageResource(R.drawable.heart_green);
                heart2.setImageResource(R.drawable.heart_green);
                heart3.setImageResource(R.drawable.heart_gray);
                heart4.setImageResource(R.drawable.heart_gray);
                heart5.setImageResource(R.drawable.heart_gray);
            }
            if (v.getId() == heart3.getId()){
                rate = 3;
                heart1.setImageResource(R.drawable.heart_green);
                heart2.setImageResource(R.drawable.heart_green);
                heart3.setImageResource(R.drawable.heart_green);
                heart4.setImageResource(R.drawable.heart_gray);
                heart5.setImageResource(R.drawable.heart_gray);
            }
            if (v.getId() == heart4.getId()){
                rate = 4;
                heart1.setImageResource(R.drawable.heart_green);
                heart2.setImageResource(R.drawable.heart_green);
                heart3.setImageResource(R.drawable.heart_green);
                heart4.setImageResource(R.drawable.heart_green);
                heart5.setImageResource(R.drawable.heart_gray);
            }
            if (v.getId() == heart5.getId()){
                rate = 5;
                heart1.setImageResource(R.drawable.heart_green);
                heart2.setImageResource(R.drawable.heart_green);
                heart3.setImageResource(R.drawable.heart_green);
                heart4.setImageResource(R.drawable.heart_green);
                heart5.setImageResource(R.drawable.heart_green);
            }
        }
    }

    //2-3.确认按钮监听器
    public class confirmListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            commentString = commentEditText.getText().toString();

            if (rate == 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Comment.this);
                builder.setMessage("请点击评分");
                builder.setTitle("提示");
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return;
            }

            if (commentString.length() == 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Comment.this);
                builder.setMessage("请填写评论");
                builder.setTitle("提示");
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return;
            }

            sendData();                             //2-4.发送数据
        }
    }

    //2-4.发送数据
    public void sendData(){
        Toast.makeText(Activity_Comment.this, "已发送", Toast.LENGTH_SHORT).show();

        //将所填的内容保存起来
        SharedPreferences sharedPreferences = getSharedPreferences("comment", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("rate", rate);
        editor.putString("comment", commentString);
        editor.apply();

        //finish();

    }

    //按下返回键
    @Override
    public void onBackPressed() {
        giveUp();                                   //2-1.放弃修改
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
