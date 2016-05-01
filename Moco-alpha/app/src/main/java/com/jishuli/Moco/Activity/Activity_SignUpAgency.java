package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.jishuli.Moco.R;

public class Activity_SignUpAgency extends Activity {
    private ImageButton backButton;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupagency);

        findViews();
        setListeners();
    }

    public void findViews(){
        backButton = (ImageButton)findViewById(R.id.SignUpAgencyBackgroundLayoutBackButton);
        signUpButton = (Button)findViewById(R.id.SignUpAgencySignUpButton);
    }

    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_SignUpAgency.this.finish();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SignUpAgency.this);
                builder.setMessage("注册机构成功，请等待审核");
                builder.setTitle("提示");
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Activity_SignUpAgency.this.finish();
                    }
                });
                builder.create().show();
            }
        });
    }
}
