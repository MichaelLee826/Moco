package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.jishuli.Moco.Adapter.Adapter_MyClass;
import com.jishuli.Moco.ExitApplication;
import com.jishuli.Moco.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Activity_MyClass extends Activity {
    private static final String PATH = "http://120.25.166.18/joinedCourse";

    private ListView myClassListView;
    private ListAdapter listAdapter;

    private String[] statusStrings;
    private String[] courseIDStrings;
    private String[] courseNameStrings;
    private String[] cityNameStrings;
    private String[] districtNameStrings;
    private String[] addressStrings;
    private String[] beginTimeStrings;
    private String[] endTimeStrings;
    private String[] courseWeek1Strings;
    private String[] courseTime1Strings;
    private String[] courseWeek2Strings;
    private String[] courseTime2Strings;
    private String[] institutionNameStrings;
    private String[] teacherNameStrings;

    private ImageButton frontPageImageButton = null;
    private ImageButton myClassImageButton = null;
    private ImageButton myProfileImageButton = null;

    //程序退出时的时间
    private long exitTime = 0;

    private final static int DONE = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DONE: {
                    setListView();                  //4.设置课程列表
                    break;
                }
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
            intent.setClass(Activity_MyClass.this, Activity_SignIn.class);
            startActivity(intent);
        }
        else {
            setContentView(R.layout.activity_myclass);

            init();                         //1.初始化控件
            setListeners();                 //2.设置按钮的监听器
            getData();                      //3.获取数据
        }
    }

    //1.初始化控件
    public void init(){
        myClassListView = (ListView)findViewById(R.id.MyClassListView);

        frontPageImageButton = (ImageButton)findViewById(R.id.frontPage);
        myClassImageButton = (ImageButton)findViewById(R.id.schedule);
        myProfileImageButton = (ImageButton)findViewById(R.id.profile);

        frontPageImageButton.setBackgroundResource(R.drawable.bt_frontpage);
        myClassImageButton.setBackgroundResource(R.drawable.bt_schedule_pressed);
        myProfileImageButton.setBackgroundResource(R.drawable.bt_myprofile);
    }

    //2.设置底部导航按钮的监听器（只需设置第一和第三个）
    public void setListeners(){
        frontPageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(Activity_MyClass.this, Activity_Main.class);
                startActivity(intent);

            }
        });

        myProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(Activity_MyClass.this, Activity_MyProfile.class);
                startActivity(intent);
            }
        });
    }

    //3.获取数据
    public void getData(){
        OkHttpClient mOkHttpClient = new OkHttpClient();

        //取出cookie
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        String cookie = sharedPreferences.getString("cookie", null);

        Request request = new Request.Builder()
                .url(PATH)
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

                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray jsonArray = jsonObject.getJSONArray("detail");

                    int length = jsonObject.getInt("length");

                    statusStrings = new String[length];
                    courseIDStrings = new String[length];
                    courseNameStrings = new String[length];
                    cityNameStrings = new String[length];
                    districtNameStrings = new String[length];
                    addressStrings = new String[length];
                    beginTimeStrings = new String[length];
                    endTimeStrings = new String[length];
                    courseWeek1Strings = new String[length];
                    courseTime1Strings = new String[length];
                    courseWeek2Strings = new String[length];
                    courseTime2Strings = new String[length];
                    institutionNameStrings = new String[length];
                    teacherNameStrings = new String[length];

                    for (int i = 0; i < length; i++){
                        JSONObject temp = (JSONObject)jsonArray.get(i);
                        statusStrings[i] = temp.getString("status");
                        courseIDStrings[i] = temp.getString("courseID");
                        courseNameStrings[i] = temp.getString("courseName");
                        cityNameStrings[i] = temp.getString("cityName");
                        districtNameStrings[i] = temp.getString("districtName");
                        addressStrings[i] = temp.getString("address");
                        beginTimeStrings[i] = temp.getString("beginTime");
                        endTimeStrings[i] = temp.getString("endTime");
                        courseWeek1Strings[i] = temp.getString("courseWeek1");
                        courseTime1Strings[i] = temp.getString("courseTime1");
                        courseWeek2Strings[i] = temp.getString("courseWeek2");
                        courseTime2Strings[i] = temp.getString("courseTime2");
                        institutionNameStrings[i] = temp.getString("institutionName");
                        teacherNameStrings[i] = temp.getString("teacherName");
                    }
                    //已下载完数据
                    handler.sendEmptyMessage(DONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //4.设置课程列表
    public void setListView() {
        listAdapter = new Adapter_MyClass(this, statusStrings, courseIDStrings, courseNameStrings, cityNameStrings,
                                          districtNameStrings, addressStrings, beginTimeStrings, endTimeStrings,
                                          courseWeek1Strings, courseTime1Strings, courseWeek2Strings, courseTime2Strings,
                                          institutionNameStrings, teacherNameStrings);
        myClassListView.setAdapter(listAdapter);
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