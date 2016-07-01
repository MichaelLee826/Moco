package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.jishuli.Moco.ExitApplication;
import com.jishuli.Moco.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Activity_MyProfile extends Activity {
    private String cookie = "";
    private String _xsrf = "";

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

            getUserData();
        }
    }

    //1.初始化控件
    public void init(){
        messageImageButton = (ImageButton)findViewById(R.id.message);

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

    //2-1.“退出”功能
    public void logout(){
        String path = "http://120.25.166.18/logout";

        OkHttpClient mOkHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(path)
                .addHeader("Cookie", cookie)
                .build();

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                System.out.println("失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                System.out.println(responseData);

            }
        });
    }

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
