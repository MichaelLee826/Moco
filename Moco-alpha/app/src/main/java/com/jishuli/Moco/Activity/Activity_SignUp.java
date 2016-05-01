package com.jishuli.Moco.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.jishuli.Moco.ExitApplication;
import com.jishuli.Moco.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Activity_SignUp extends AppCompatActivity {
    static private String PATH = "http://120.25.166.18/register";

    private EditText accountEditText;
    private EditText phoneNumberEditText;
    private EditText passwordEditText;
    private EditText codeEditText;
    private ImageButton signUpButton;

    private String userName;
    private String phoneNumber;
    private String password;
    private String code;

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
        signUpButton.setOnClickListener(new SignUpListener());      //2-1.注册按钮的监听器
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
                    /**得到的HTML
                     * <html>
                    <head>
                     <title>Register</title>
                    </head>
                    <body>
                     <form action="/register" method="POST">
                      <input type="hidden" name="_xsrf" value="2|d72b2cbb|de43b96fb778c9494e5727f48be52604|1462117340">
                      <ul>
                       <li>Username: <input type="text" name="username"></li>
                       <li>Phone: <input type="text" name="phone"></li>
                       <li>Password: <input type="password" name="password"></li>
                       <ul>
                        <input type="submit" value="Register">
                       </ul>
                      </ul>
                     </form>
                    </body>
                   </html>*/
                    String _xsrf = document.select("input").attr("value");

                    //获得_xsrf的值后，通过POST方式，连同其它数据提交给服务器
                    final JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("_xsrf", _xsrf);
                        jsonObject.put("username", userName);
                        jsonObject.put("phone", phoneNumber);
                        jsonObject.put("password", password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //将JSON对象转换为String形式
                    String content = String.valueOf(jsonObject);
                    //String content = "_xsrf=" + _xsrf + "&username=" + userName + "&phone=" + phoneNumber + "&password=" + password;

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setRequestProperty("connection", "keep-alive");
                    httpURLConnection.setRequestProperty("User-Agent", "Fiddler");
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    httpURLConnection.setRequestProperty("Content-Length", String.valueOf(content.getBytes().length));
                    httpURLConnection.setRequestProperty("Charset", "utf-8");

                    System.out.println(content);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(content.getBytes());
                    outputStream.flush();
                    outputStream.close();

                    String s = httpURLConnection.getResponseCode() + "";
                    System.out.println(s);

                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        String retData;
                        String responseData = "";
                        while((retData = in.readLine()) != null)
                        {
                            responseData += retData;
                        }
                        JSONObject jo = new JSONObject(responseData);
                        String status = jo.getString("status");
                        System.out.println(status);

                        in.close();
                    }else {
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
