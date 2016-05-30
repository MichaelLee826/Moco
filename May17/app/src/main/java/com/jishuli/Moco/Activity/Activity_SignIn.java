package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jishuli.Moco.ExitApplication;
import com.jishuli.Moco.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Activity_SignIn extends AppCompatActivity {
    private static final String PATH = "http://120.25.166.18/login";
    //static private String PATH = "http://10.0.2.2:8000/login";

    private EditText accountEditText;
    private EditText passwordEditText;
    private ImageButton signInButton;
    private TextView forgetPasswordTextView;
    private TextView signUpTextView;

    private String userName;
    private String password;

    private static final int NO_SUCH_USER = 0;
    private static final int WRONG_PASSWORD = 1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            AlertDialog.Builder builder;
            switch (msg.what){
                case NO_SUCH_USER:
                    builder = new AlertDialog.Builder(Activity_SignIn.this);
                    builder.setMessage("该用户名或手机号未注册，请注册");
                    builder.setTitle("登录失败");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;

                case WRONG_PASSWORD:
                    builder = new AlertDialog.Builder(Activity_SignIn.this);
                    builder.setMessage("密码错误，请重新输入");
                    builder.setTitle("登录失败");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;

                default: break;
            }
        }
    };

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

        //加上下划线
        forgetPasswordTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        signUpTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    //2.设置监听器
    public void setListeners(){
        accountEditText.setKeyListener(new editTextListener());         //2-1.用户名输入框监听器

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = accountEditText.getText().toString();
                password = passwordEditText.getText().toString();

                if (userName.length() == 0 | userName.equals(null)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SignIn.this);
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

                if (password.length() == 0 | password.equals(null)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SignIn.this);
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

                signIn();                                               //2-2.登录
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

    //2-1.用户名输入框监听器
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

    //2-2.登录
    public void signIn(){
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
                    String content = "user=" + userName + "&_xsrf=" + _xsrf + "&password=" + password;

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
                    httpURLConnection.setRequestProperty("Charset", "utf-8");

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(content.getBytes());
                    outputStream.flush();
                    outputStream.close();

                    //获得返回代码
                    String responseCode = httpURLConnection.getResponseCode() + "";
                    System.out.println(responseCode);

                    //如果正常，则返回200
                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        String data;
                        String responseData = "";
                        while ((data = bufferedReader.readLine()) != null) {
                            responseData += data;
                        }
                        System.out.println("responseData:" + responseData);
                        bufferedReader.close();

                        //获取返回状态
                        JSONObject jsonObject = new JSONObject(responseData);
                        String feedback = jsonObject.getString("feedback");

                        //登录成功
                        if (feedback.equals("success")){
                            //将用户名、密码和cookie保存在SharedPreferences中
                            SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userName", userName);
                            editor.putString("password", password);
                            editor.putString("cookie", cookie);
                            editor.apply();
                            System.out.println("登录时的cookie：" + cookie);

                            Intent intent = new Intent();
                            intent.setClass(Activity_SignIn.this, Activity_Main.class);
                            startActivity(intent);
                        }

                        //用户名不存在
                        if (feedback.equals("no such user")){
                            handler.sendEmptyMessage(NO_SUCH_USER);
                        }

                        //密码错误
                        if (feedback.equals("wrong password")){
                            handler.sendEmptyMessage(WRONG_PASSWORD);
                        }
                    }
                    else {
                        System.out.println("提交信息不成功");
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
