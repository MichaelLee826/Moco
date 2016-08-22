package com.taiclouds.Moke.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.taiclouds.Moke.Adapter.Adapter_MyAgency;
import com.taiclouds.Moke.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Activity_JoinAgency extends Activity {
    static private String PATH = "http://120.25.166.18/institutionJoinIn";

    private ImageButton backButton;

    private EditText searchBarEditText;
    private ImageButton searchBarDeleteImageButton;

    private List<String> institutionIDList = new ArrayList<>();             //机构ID
    private List<String> institutionNameList = new ArrayList<>();           //机构名称
    private List<String> legalPersonNameList = new ArrayList<>();           //创建者姓名
    private List<String> statusList = new ArrayList<>();                    //显示“已加入”或“已注册”
    private List<String> textList = new ArrayList<>();                      //显示“加入时间”或“建立时间”
    private List<String> dateList = new ArrayList<>();                      //时间
    private ListView listView;
    private ListAdapter listAdapter;

    private Button registerButton;

    private OkHttpClient mOkHttpClient = new OkHttpClient();
    private String cookie;

    private final static int DONE = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DONE: {
                    setListView();                          //5.设置ListView
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joinagency);

        init();                         //1.初始化
        getData();                      //2.获得数据
        setListeners();                 //3.设置监听器
        setListViewListener();          //4.设置ListView的监听器
    }

    //1.初始化
    public void init(){
        backButton = (ImageButton)findViewById(R.id.JoinAgency_BackButton);
        searchBarEditText = (EditText)findViewById(R.id.JoinAgency_SearchBar_EditText);
        searchBarDeleteImageButton = (ImageButton)findViewById(R.id.JoinAgency_SearchBar_Delete_ImageButton);
        listView = (ListView)findViewById(R.id.JoinAgency_ListView);
        registerButton = (Button)findViewById(R.id.JoinAgency_SignUpButton);
    }

    //2.获得数据
    public void getData(){
        SharedPreferences geoSharedPreferences = getSharedPreferences("geographyInfo", Activity.MODE_PRIVATE);
        String province = geoSharedPreferences.getString("province", null);
        String city = geoSharedPreferences.getString("city", null);

        //取出cookie
        SharedPreferences userSharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        cookie = userSharedPreferences.getString("cookie", null);
    }

    //3.设置监听器
    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_JoinAgency.this.finish();
            }
        });

        //搜索框
        searchBarEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //根据有无文字，显示或隐藏删除按钮
                if (s.length() == 0) {
                    searchBarEditText.setHint("请输入要搜索的机构");
                    searchBarDeleteImageButton.setVisibility(View.GONE);

                    //显示出键盘
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(searchBarEditText, InputMethodManager.SHOW_FORCED);
                }
                else {
                    searchBarEditText.setHint(null);
                    searchBarDeleteImageButton.setVisibility(View.VISIBLE);
                }
            }
        });

        //搜索框的删除按钮
        searchBarDeleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将搜索框内文字清空
                searchBarEditText.setText("");
                //searchBarEditText.requestFocus();
            }
        });

        //按下回车后搜索
        searchBarEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String content = searchBarEditText.getText().toString();
                    if (content.length() == 0){
                        //如果搜索内容为空，什么也不做
                    }
                    else {
                        /*//强制隐藏键盘
                        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(searchBarEditText.getWindowToken(), 0);*/

                        //将中文转换编码格式
                        String message = "";
                        try {
                            message = java.net.URLEncoder.encode(content,"utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        String path = PATH + "?name=" + message;
                        downloadData(path);                             //3-1.下载数据
                    }
                }
                return false;
            }
        });

        //我要注册机构的按钮
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity_JoinAgency.this, Activity_SignUpAgency.class);
                startActivity(intent);
            }
        });
    }

    //4.设置ListView的监听器
    public void setListViewListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("点击了第" + position + "个");
                /*Intent intent = new Intent();
                intent.setClass(Activity_JoinAgency.this, Activity_CourseDetail.class);   //跳转到课程详情列表

                Bundle bundle = new Bundle();
                bundle.putString("courseID", courseID[position]);                     //点击了第几个课程
                bundle.putString("cityID", cityId);

                intent.putExtras(bundle);

                startActivity(intent);*/
            }
        });
    }

    //5.设置ListView
    public void setListView(){
        listAdapter = new Adapter_MyAgency(this, institutionNameList, dateList, textList, statusList);
        listView.setAdapter(listAdapter);
    }

    //3-1.下载数据
    public void downloadData(String path){
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

                try {
                    //清空数组，防止数据叠加
                    institutionNameList.clear();
                    dateList.clear();
                    textList.clear();
                    statusList.clear();

                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray jsonArray = jsonObject.getJSONArray("detail");
                    int length = jsonObject.getInt("count");
                    for (int i = 0; i < length; i++){
                        JSONObject temp = (JSONObject)jsonArray.get(i);
                        String ID = temp.getString("institutionID");
                        String name = temp.getString("name");
                        String ownerName = temp.getString("legalPersonName");
                        String date = temp.getString("releaseTime");
                        date = date.substring(0, 11);
                        institutionIDList.add(ID);
                        institutionNameList.add(name);
                        legalPersonNameList.add(ownerName);
                        dateList.add(date);
                        textList.add("建立时间：");
                        statusList.add("已注册");
                    }
                    handler.sendEmptyMessage(DONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //按下返回键，返回到首页
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Activity_JoinAgency.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        //友盟数据统计
        MobclickAgent.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        //友盟数据统计
        MobclickAgent.onPause(this);
        super.onPause();
    }
}
