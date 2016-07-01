package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.jishuli.Moco.Adapter.Adapter_MyAgency;
import com.jishuli.Moco.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Activity_MyProfile_MyAgency extends Activity {
    private static final String RELEASED_PATH = "http://120.25.166.18/releasedInstitution";
    private static final String JOINED_PATH = "http://120.25.166.18/joinedInstitution";

    private ImageButton backButton;
    private Button signUpButton;
    private Button joinButton;

    private List<String> agencyList = new ArrayList<>();        //机构名称
    private List<String> statusList = new ArrayList<>();        //显示“已加入”或“已注册”
    private List<String> textList = new ArrayList<>();          //显示“加入时间”或“建立时间”
    private List<String> dateList = new ArrayList<>();          //时间
    private ListView listView;

    private OkHttpClient mOkHttpClient = new OkHttpClient();
    private String cookie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myagency);

        init();                                 //1.初始化
        setListeners();                         //2.设置监听器
        getJoinedData();                        //3.获取已加入的机构数据
        getReleasedData();                      //4.获取创建的机构数据
        setListView();                          //5.设置ListView
    }

    //1.初始化
    public void init(){
        //取出cookie
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        cookie = sharedPreferences.getString("cookie", null);

        listView = (ListView)findViewById(R.id.MyAgencyListView);

        //我要加入机构的按钮作为ListView的FootView
        View footView = getLayoutInflater().inflate(R.layout.myagency_footview, null);
        listView.addFooterView(footView);

        joinButton = (Button)footView.findViewById(R.id.MyAgency_FootView);

        backButton = (ImageButton)findViewById(R.id.MyAgencyBackgroundLayoutBackButton);
        signUpButton = (Button)findViewById(R.id.MyAgencySignUpButton);
    }

    //2.设置监听器
    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_MyProfile_MyAgency.this.finish();
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity_MyProfile_MyAgency.this, Activity_JoinAgency.class);
                startActivity(intent);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity_MyProfile_MyAgency.this, Activity_SignUpAgency.class);
                startActivity(intent);
            }
        });
    }

    //3.获取已加入的机构数据
    public void getJoinedData(){
        Request request = new Request.Builder()
                .url(JOINED_PATH)
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
                    for (int i = 0; i < length; i++){
                        JSONObject temp = (JSONObject)jsonArray.get(i);
                        String name = temp.getString("name");
                        String date = temp.getString("releaseTime");
                        date = date.substring(0, 11);
                        agencyList.add(name);
                        dateList.add(date);
                        textList.add("加入时间：");
                        statusList.add("已加入");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //4.获取创建的机构数据
    public void getReleasedData(){
        Request request = new Request.Builder()
                .url(RELEASED_PATH)
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
                    for (int i = 0; i < length; i++){
                        JSONObject temp = (JSONObject)jsonArray.get(i);
                        String name = temp.getString("name");
                        String date = temp.getString("releaseTime");
                        date = date.substring(0, 11);
                        agencyList.add(name);
                        dateList.add(date);
                        textList.add("建立时间：");
                        statusList.add("已注册");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //5.设置ListView
    public void setListView(){
        ListAdapter listAdapter;
        listAdapter = new Adapter_MyAgency(this, agencyList, dateList, textList, statusList);
        listView.setAdapter(listAdapter);
    }
}
