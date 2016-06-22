package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.jishuli.Moco.R;

public class Activity_MyFeedback extends Activity {
    private ImageButton backButton;
    private EditText editText;
    private Button sendButton;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfeedback);

        backButton = (ImageButton)findViewById(R.id.MyFeedbackBackgroundLayoutBackButton);
        editText = (EditText)findViewById(R.id.MyFeedbackEditText);
        sendButton = (Button)findViewById(R.id.MyFeedbackSendButton);

        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_MyFeedback.this.finish();
            }
        });

        //取消提示，左对齐
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setHint(null);
                editText.setGravity(Gravity.LEFT);
            }
        });

        //提交反馈内容
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().length() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_MyFeedback.this);
                    builder.setMessage("请输入反馈内容");
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
                else {
                    content = editText.getText().toString();
                    sendContent(content);
                }
            }
        });
    }

    public void sendContent(String content){

    }
}
