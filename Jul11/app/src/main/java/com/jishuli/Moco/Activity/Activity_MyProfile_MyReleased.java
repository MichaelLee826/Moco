package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.jishuli.Moco.Adapter.Adapter_MyClass;
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

public class Activity_MyProfile_MyReleased extends Activity {
    private static final String PATH = "http://120.25.166.18/releasedCourse";

    private ImageButton backButton;

    private ListAdapter listAdapter;
    private ListView listView;

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
        setContentView(R.layout.activity_myreleased);

        init();                                 //1.初始化
        setListeners();                         //2.设置监听器
        getData();                              //3.获取数据
    }

    //1.初始化
    public void init(){
        backButton = (ImageButton)findViewById(R.id.MyReleasedBackgroundLayoutBackButton);
        listView = (ListView)findViewById(R.id.MyReleasedListView);
    }

    //2.设置监听器
    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_MyProfile_MyReleased.this.finish();
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

    //4.设置ListView
    public void setListView(){
        //与“课表”共用同一个适配器和布局
        listAdapter = new Adapter_MyClass(this, statusStrings, courseIDStrings, courseNameStrings,
                                          cityNameStrings, districtNameStrings, addressStrings,
                                          beginTimeStrings, endTimeStrings, courseWeek1Strings,
                                          courseTime1Strings, courseWeek2Strings, courseTime2Strings,
                                          institutionNameStrings, teacherNameStrings);
        listView.setAdapter(listAdapter);
    }
}