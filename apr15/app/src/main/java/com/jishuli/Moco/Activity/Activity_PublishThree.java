package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.jishuli.Moco.ExitApplication;
import com.jishuli.Moco.R;

public class Activity_PublishThree extends Activity {
    private LinearLayout linearLayout;
    private ImageButton backButton;

    private Button plusButton;
    private Button minusButton;
    private Button nextButton;

    int editNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //添加到activityList列表中
        ExitApplication.getInstance().addActivity(this);

        setContentView(R.layout.activity_publishthree);

        findViews();        //1.找到各种控件
        setListeners();     //2.设置监听器

        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_PublishThree.this.finish();
            }
        });
    }

    //1.找到各种控件
    public void findViews(){
        linearLayout = (LinearLayout)findViewById(R.id.PublishThreeCategoryLayout);
        backButton = (ImageButton)findViewById(R.id.PublishThreeBackgroundLayoutBackButton);
        plusButton = (Button)findViewById(R.id.PublishThreePlusButton);
        minusButton = (Button)findViewById(R.id.PublishThreeMinusButton);
        nextButton = (Button)findViewById(R.id.PublishThreeNextButton);
    }

    //2.设置监听器
    public void setListeners(){
        plusButton.setOnClickListener(new PlusButtonListener());        //2-1.添加按钮的监听器
        minusButton.setOnClickListener(new MinusButtonListener());      //2-2.删除按钮的监听器
        nextButton.setOnClickListener(new NextListener());              //2-3.下一步按钮的监听器
    }

    //2-1.添加按钮的监听器
    public class PlusButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            EditText et = new EditText(Activity_PublishThree.this);
            et.setBackgroundColor(Color.rgb(207, 206, 206));
            et.setTextSize(15);
            et.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20, 20, 20, 0);     //左、上、右、下
            et.setLayoutParams(layoutParams);
            linearLayout.addView(et);
        }
    }

    //2-2.删除按钮的监听器
    public class MinusButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            int i = linearLayout.getChildCount();

            if (i == 1){
                //如果只有一EditText，不删除
            }
            else {
                linearLayout.removeViewAt(i - 1);
            }
        }
    }

    //2-3.下一步按钮的监听器
    public class NextListener implements View.OnClickListener{
        String[] contents;

        @Override
        public void onClick(View v) {
            int i = linearLayout.getChildCount();
            contents = new String[i];
            for (int j = 0; j < i; j++){
                EditText e = (EditText)linearLayout.getChildAt(j);
                contents[j] = e.getText().toString();
            }

            Intent intent = new Intent();
            intent.setClass(Activity_PublishThree.this, Activity_Main.class);
            startActivity(intent);
        }
    }
}
