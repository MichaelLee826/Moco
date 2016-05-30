package com.jishuli.Moco.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.jishuli.Moco.ExitApplication;
import com.jishuli.Moco.R;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Activity_SignUp extends AppCompatActivity {
    private static final String PATH = "http://120.25.166.18/register";
    //static private String PATH = "http://10.0.2.2:8000/register";

    private EditText accountEditText;
    private EditText phoneNumberEditText;
    private EditText passwordEditText;
    private EditText codeEditText;
    private ImageButton signUpButton;

    private String userName;
    private String phoneNumber;
    private String password;
    private String code;

    private static final int SUCCESS = 0;
    private static final int USERNAME_EXIST = 1;
    private static final int PHONE_NUMBER_EXIST = 2;
    private static final int USERNAME_FORMAT_ERROR = 3;
    private static final int PHONE_NUMBER_FORMAT_ERROR = 4;
    private static final int CODE_WRONG = 5;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AlertDialog.Builder builder;
            switch (msg.what){
                case SUCCESS:
                    //显示“注册成功”，并跳转到登陆页面
                    builder = new AlertDialog.Builder(Activity_SignUp.this);
                    builder.setMessage("注册成功！");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setClass(Activity_SignUp.this, Activity_SignIn.class);
                            startActivity(intent);

                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;

                case USERNAME_EXIST:
                    builder = new AlertDialog.Builder(Activity_SignUp.this);
                    builder.setMessage("用户名已存在，请修改");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;

                case PHONE_NUMBER_EXIST:
                case CODE_WRONG:
                default: break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //添加到activityList列表中
        ExitApplication.getInstance().addActivity(this);

        findViews();            //1.找到各种控件
        setListeners();         //2.设置监听器
    }

    //1.找到各种控件
    public void findViews(){
        accountEditText = (EditText)findViewById(R.id.SignUpAccountEditText);
        phoneNumberEditText = (EditText)findViewById(R.id.SignUpPhoneNumberEditText);
        passwordEditText = (EditText)findViewById(R.id.SignUpPasswordEditText);
        codeEditText = (EditText)findViewById(R.id.SignUpCodeEditText);
        signUpButton = (ImageButton)findViewById(R.id.SignUpImageButton);
    }

    //2.设置监听器
    public void setListeners(){
        signUpButton.setOnClickListener(new SignUpListener());       //2-1.注册按钮的监听器
        accountEditText.setKeyListener(new editTextListener());      //2-3.用户名输入框监听器
    }

    //2-1.注册按钮的监听器
    private class SignUpListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            userName = accountEditText.getText().toString();
            phoneNumber = phoneNumberEditText.getText().toString();
            password = passwordEditText.getText().toString();
            code = codeEditText.getText().toString();

            if (userName.length() == 0 | userName.equals(null)){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SignUp.this);
                builder.setMessage("请填写用户名");
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

            if (userName.length() < 5 | userName.length() > 10){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SignUp.this);
                builder.setMessage("用户名为5~10位字母或数字");
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

            if (phoneNumber.length() == 0 | phoneNumber.equals(null)){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SignUp.this);
                builder.setMessage("请填写手机号");
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

            if (!phoneNumber.startsWith("1") | phoneNumber.length() != 11){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SignUp.this);
                builder.setMessage("请输入正确格式的手机号");
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

            if (password.length() == 0 | password.equals(null)){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SignUp.this);
                builder.setMessage("请设置密码");
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

            if (code.length() == 0 | code.equals(null)){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SignUp.this);
                builder.setMessage("请填写验证码");
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

            signUp();       //2-2.注册
        }
    }

    //2-2.注册
    public void signUp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(PATH);

                    //第三方库Jsoup，用于解析HTML
                    Document document = Jsoup.parse(url, 5000);
                    //System.out.println(document.html());

                    //从HTML中获得_xsrf
                    String _xsrf = document.select("input").attr("value");

                    //POST的内容
                    String content = "username=" + userName + "&_xsrf=" + _xsrf + "&phone=" + phoneNumber + "&password=" + password;

                    //Cookie的内容
                    String cookie = "_xsrf=" + _xsrf;

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setRequestProperty("Cookie", cookie);
                    httpURLConnection.setRequestProperty("Connection", "keep-alive");
                    httpURLConnection.setRequestProperty("User-Agent", "Fiddler");
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    httpURLConnection.setRequestProperty("Content-Length", String.valueOf(content.getBytes().length));
                    httpURLConnection.setRequestProperty("Charset", "utf-8");

                    System.out.println(cookie);
                    System.out.println(content);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(content.getBytes());
                    outputStream.flush();
                    outputStream.close();

                    //获得返回代码
                    String responseCode = httpURLConnection.getResponseCode() + "";
                    System.out.println(responseCode);

                    //如果正常，则返回200
                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        String data;
                        String responseData = "";
                        while((data = bufferedReader.readLine()) != null)
                        {
                            responseData += data;
                        }
                        System.out.println("responseData:" + responseData);
                        bufferedReader.close();

                        //根据返回的提示，显示相应的对话框
                        JSONObject jsonObject = new JSONObject(responseData);
                        String feedback = jsonObject.getString("feedback");

                        //注册成功
                        if (feedback.equals("success")){
                            handler.sendEmptyMessage(SUCCESS);
                        }

                        //用户名重复
                        else if (feedback.equals("username exist")){
                            handler.sendEmptyMessage(USERNAME_EXIST);
                        }

                        //手机号码重复
                        else if (feedback.equals("phonenumber exist")){
                            handler.sendEmptyMessage(PHONE_NUMBER_EXIST);
                        }

                        //用户名格式错误
                        else if (feedback.equals("username format error")){
                            handler.sendEmptyMessage(USERNAME_FORMAT_ERROR);
                        }

                        //手机号格式错误
                        else if (feedback.equals("userphone format error")){
                            handler.sendEmptyMessage(PHONE_NUMBER_FORMAT_ERROR);
                        }
                    }
                    else {
                        System.out.println("提交信息不成功");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //2-3.用户名输入框监听器
    public class editTextListener extends DigitsKeyListener {
        @Override
        public int getInputType() {
            return InputType.TYPE_TEXT_VARIATION_PASSWORD;
        }

        @Override
        protected char[] getAcceptedChars() {
            //允许输入的字符
            String input = "qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM1234567890";
            char[] data = input.toCharArray();
            return data;
        }
    }
}