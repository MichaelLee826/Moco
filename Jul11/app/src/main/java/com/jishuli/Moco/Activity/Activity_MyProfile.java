package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jishuli.Moco.ExitApplication;
import com.jishuli.Moco.R;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Activity_MyProfile extends Activity {
    private String cookie = "";
    private String _xsrf = "";

    private ImageView avatarImageView;
    private TextView usernameTextView;
    private ImageButton messageImageButton;

    private ImageButton frontPageImageButton;
    private ImageButton myClassImageButton;
    private ImageButton myProfileImageButton;

    private ImageButton myReleasedImageButton;
    private ImageButton myAgencyImageButton;
    private ImageButton myFavouriteImageButton;
    private ImageButton aboutImageButton;

    //程序退出时的时间
    private long exitTime = 0;

    private final static int SUCCESS = 1;
    private final static int FAIL = 2;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            AlertDialog.Builder builder;
            switch (msg.what){
                case SUCCESS:
                    builder = new AlertDialog.Builder(Activity_MyProfile.this);
                    builder.setMessage("已退出");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setClass(Activity_MyProfile.this, Activity_Main.class);
                            startActivity(intent);

                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;

                case FAIL:
                    builder = new AlertDialog.Builder(Activity_MyProfile.this);
                    builder.setMessage("退出不成功");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //添加到activityList列表中
        ExitApplication.getInstance().addActivity(this);

        //检查用户名和密码
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", null);
        String password = sharedPreferences.getString("password", null);

        if (userName == null | password == null){
            Intent intent = new Intent();
            intent.setClass(Activity_MyProfile.this, Activity_SignIn.class);
            startActivity(intent);
        }
        else {
            setContentView(R.layout.activity_myprofile);

            init();                 //1.初始化控件
            setListeners();         //2.设置按钮监听器
            getUserData();          //3.获取用户信息
        }
    }

    //1.初始化控件
    public void init(){
        avatarImageView = (ImageView)findViewById(R.id.MyProfile_Avatar_ImageView);
        usernameTextView = (TextView)findViewById(R.id.MyProfile_Username_TextView);
        messageImageButton = (ImageButton)findViewById(R.id.MyProfile_Message_ImageButton);

        frontPageImageButton = (ImageButton)findViewById(R.id.frontPage);
        myClassImageButton = (ImageButton)findViewById(R.id.schedule);
        myProfileImageButton = (ImageButton)findViewById(R.id.profile);

        myReleasedImageButton = (ImageButton)findViewById(R.id.MyProfileMyReleasedImageButton);
        myAgencyImageButton = (ImageButton)findViewById(R.id.MyProfileMyAgencyImageButton);
        myFavouriteImageButton = (ImageButton)findViewById(R.id.MyProfileMyFavouriteImageButton);
        aboutImageButton = (ImageButton)findViewById(R.id.MyProfileAboutImageButton);

        frontPageImageButton.setBackgroundResource(R.drawable.bt_frontpage);
        myClassImageButton.setBackgroundResource(R.drawable.bt_schedule);
        myProfileImageButton.setBackgroundResource(R.drawable.bt_myprofile_pressed);
    }

    //2.设置按钮监听器
    public void setListeners(){
        //点击头像，跳转到编辑资料页面
        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity_MyProfile.this, Activity_EditProfile.class);
                startActivity(intent);
            }
        });

        //点击用户名，跳转到编辑资料页面
        usernameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity_MyProfile.this, Activity_EditProfile.class);
                startActivity(intent);
            }
        });

        //暂时用于“退出”
        messageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        //设置导航按钮的监听器（只需设置第一和第二个）
        frontPageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(Activity_MyProfile.this, Activity_Main.class);
                startActivity(intent);
            }
        });

        myClassImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(Activity_MyProfile.this, Activity_MyClass.class);
                startActivity(intent);
            }
        });

        //我的发布
        myReleasedImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity_MyProfile.this, Activity_MyProfile_MyReleased.class);
                startActivity(intent);
            }
        });

        //我的机构
        myAgencyImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity_MyProfile.this, Activity_MyProfile_MyAgency.class);
                startActivity(intent);
            }
        });

        //我的收藏
        myFavouriteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(Activity_MyProfile.this, Activity_MyProfile_MyFavourite.class);
                startActivity(intent);
            }
        });

        //关于
        aboutImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(Activity_MyProfile.this, Activity_MyProfile_About.class);
                startActivity(intent);
            }
        });
    }

    //3.获取用户信息
    public void getUserData() {
        final String path = "http://120.25.166.18/logout";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(path);
                    Document document = Jsoup.parse(url, 5000);             //第三方库Jsoup，用于解析HTML
                    //System.out.println(document.html());
                    _xsrf = document.select("input").attr("value");         //从HTML中获得_xsrf

                    //取出cookie
                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
                    cookie = sharedPreferences.getString("cookie", null);
                    cookie = cookie + "; _xsrf = " + _xsrf;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //2-1.“退出”功能
    public void logout(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path = "http://120.25.166.18/logout";

                    URL url = new URL(path);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setConnectTimeout(5000);      //超时时间，5秒
                    httpURLConnection.setRequestMethod("GET");      //方式为GET
                    httpURLConnection.setDoInput(true);

                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        InputStream inputStream = httpURLConnection.getInputStream();   //获得输入流
                        byte[] data = readStream(inputStream);                          //2-2.把输入流转换成字符串组，单独一个函数：2-1
                        String responseString = new String(data);

                        //成功退出，删除本地用户数据
                        if (responseString.contains("Please Log In")){
                            SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove("userName");
                            editor.remove("password");
                            editor.remove("cookie");
                            editor.remove("_xsrf");
                            editor.apply();

                            handler.sendEmptyMessage(SUCCESS);
                        }
                        else {
                            handler.sendEmptyMessage(FAIL);
                        }

                    }
                    else {
                        System.out.println("网络有问题");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //2-2.读数据中用到的函数
    private static byte[] readStream(InputStream inputStream) throws Exception{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1){
            byteArrayOutputStream.write(buffer, 0, length);
        }
        byteArrayOutputStream.close();
        inputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    //按两下返回键，退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis() - exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
            else {
                ExitApplication.getInstance().exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
