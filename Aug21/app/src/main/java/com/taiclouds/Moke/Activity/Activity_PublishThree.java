package com.taiclouds.Moke.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.taiclouds.Moke.ExitApplication;
import com.taiclouds.Moke.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Activity_PublishThree extends Activity {
    private static String PATH = "http://120.25.166.18/courseNew";
    //static private String PATH = "http://10.0.2.2:8000/courseNew";

    private String cookie = "";
    private String _xsrf = "";

    //从第一页和第二页传来的数据
    private String className;
    private String subjectCode;
    private String courseCode;
    private String subjectString;
    private String courseString;
    private String provinceCode;
    private String cityCode;
    private String cityName;
    private String districtCode;
    private String districtName;
    private String lat;
    private String lng;
    private String picturePath;
    private String price;
    private String beginYear;
    private String beginMonth;
    private String beginDay;
    private String endYear;
    private String endMonth;
    private String endDay;
    private String morningWeekday;
    private String afternoonWeekday;
    private String morningBeginTime;
    private String morningEndTime;
    private String afternoonBeginTime;
    private String afternoonEndTime;
    private String locationDetail;
    private String enrollNumber;
    private ArrayList<String> agencyList;
    private ArrayList<String> agencyIDList;

    //第三页的数据
    private String agencyName;
    private String agencyID;
    private String teacherName;
    private String teacherIntro;
    private String contact;
    private String courseIntro;
    private String targetCustomer;
    private String[] contents;                          //课程目录

    private Spinner agencySpinner;
    private EditText teacherNameEditText;
    private EditText teacherIntroEditText;
    private EditText contactEditText;
    private EditText courseIntroEditText;
    private EditText targetCustomerEditText;

    private LinearLayout linearLayout;
    private ImageButton backButton;

    private Button plusButton;
    private Button minusButton;
    private Button nextButton;

    private static final int FAILED = -1;
    private static final int SUCCESS = 0;
    private static final int UNKNOWN_REASON = 100;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            AlertDialog.Builder builder;
            switch (msg.what){
                case FAILED:
                    builder = new AlertDialog.Builder(Activity_PublishThree.this);
                    builder.setMessage("网络有问题，请检查");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;

                case SUCCESS:
                    //显示“提交成功”，并跳转到首页
                    builder = new AlertDialog.Builder(Activity_PublishThree.this);
                    builder.setMessage("提交成功！");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setClass(Activity_PublishThree.this, Activity_Main.class);
                            startActivity(intent);

                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;

                case UNKNOWN_REASON:
                    //其他情况
                    builder = new AlertDialog.Builder(Activity_PublishThree.this);
                    builder.setMessage("好像哪里出了问题，请再试一次");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setClass(Activity_PublishThree.this, Activity_Main.class);
                            startActivity(intent);

                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //添加到activityList列表中
        ExitApplication.getInstance().addActivity(this);

        setContentView(R.layout.activity_publishthree);

        init();                     //1.初始化控件
        getData();                  //2.从上一个Activity传来的数据
        setSpinner();               //3.设置机构名称的下拉菜单
        setListeners();             //4.设置监听器
        getUserData();              //5.获取用户数据

        //添加第一条目录
        EditText et = new EditText(Activity_PublishThree.this);
        et.setBackgroundColor(Color.rgb(207, 206, 206));
        et.setTextSize(15);
        et.setGravity(Gravity.LEFT);
        et.setPadding(20, 20, 20, 20);                  //左、上、右、下
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80);
        layoutParams.setMargins(20, 20, 20, 0);         //左、上、右、下
        et.setLayoutParams(layoutParams);
        linearLayout.addView(et);
    }

    //1.找到各种控件
    public void init(){
        linearLayout = (LinearLayout)findViewById(R.id.PublishThreeCategoryLayout);
        backButton = (ImageButton)findViewById(R.id.PublishThreeBackgroundLayoutBackButton);
        plusButton = (Button)findViewById(R.id.PublishThreePlusButton);
        agencySpinner = (Spinner)findViewById(R.id.PublishThreeAgencySpinner);
        teacherNameEditText = (EditText)findViewById(R.id.PublishThreeTeacherNameEditText);
        teacherIntroEditText = (EditText)findViewById(R.id.PublishThreeTeacherIntroEditText);
        contactEditText = (EditText)findViewById(R.id.PublishThreeContactEditText);
        courseIntroEditText = (EditText)findViewById(R.id.PublishThreeCourseIntroEditText);
        targetCustomerEditText = (EditText)findViewById(R.id.PublishThreeTargetCustomerEditText);
        //categoryEditText1 = (EditText)findViewById(R.id.PublishThreeCategoryEditText);
        minusButton = (Button)findViewById(R.id.PublishThreeMinusButton);
        nextButton = (Button)findViewById(R.id.PublishThreeNextButton);
    }

    //2.从上一个Activity传来的数据
    public void getData(){
        Bundle bundle = this.getIntent().getExtras();
        provinceCode = bundle.getString("provinceCode");
        cityCode = bundle.getString("cityCode");
        cityName = bundle.getString("cityName");
        districtCode = bundle.getString("districtCode");
        districtName = bundle.getString("districtName");
        lat = bundle.getString("lat");
        lng = bundle.getString("lng");
        className = bundle.getString("classname");
        subjectCode = bundle.getString("subjectCode");
        courseCode = bundle.getString("courseCode");
        subjectString = bundle.getString("typeName");
        courseString = bundle.getString("subjectName");
        picturePath = bundle.getString("picturePath");
        price = bundle.getString("price");
        beginYear = bundle.getString("beginYear");
        beginMonth = bundle.getString("beginMonth");
        beginDay = bundle.getString("beginDay");
        endYear = bundle.getString("endYear");
        endMonth = bundle.getString("endMonth");
        endDay = bundle.getString("endDay");
        morningWeekday = bundle.getString("morningWeekday");
        afternoonWeekday = bundle.getString("afternoonWeekday");
        morningBeginTime = bundle.getString("morningBeginTime");
        morningEndTime = bundle.getString("morningEndTime");
        afternoonBeginTime = bundle.getString("afternoonBeginTime");
        afternoonEndTime = bundle.getString("afternoonEndTime");
        locationDetail = bundle.getString("locationDetail");
        enrollNumber = bundle.getString("enrollNumber");
        agencyList = bundle.getStringArrayList("agencyList");
        agencyIDList = bundle.getStringArrayList("agencyIDList");
    }

    //3.设置机构名称的下拉菜单
    public void setSpinner(){
        int length = agencyList.size();
        final String[] nameStrings = new String[length];
        final String[] IDStrings = new String[length];

        for (int i = 0; i < length; i++){
            nameStrings[i] = agencyList.get(i);
            IDStrings[i] = agencyIDList.get(i);
        }

        ArrayAdapter<String> agencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, nameStrings);
        agencySpinner.setAdapter(agencyAdapter);
        agencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                agencyName = nameStrings[position];
                agencyID = IDStrings[position];
                System.out.println("--"+agencyName);
                System.out.println("--"+agencyID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        agencySpinner.setSelection(0, true);
    }

    //4.设置监听器
    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_PublishThree.this.finish();
            }
        });

        plusButton.setOnClickListener(new PlusButtonListener());            //4-1.添加按钮的监听器
        minusButton.setOnClickListener(new MinusButtonListener());          //4-2.删除按钮的监听器
        nextButton.setOnClickListener(new SubmitListener());                //4-3.提交按钮的监听器
    }

    //5.获取用户数据
    public void getUserData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(PATH);
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

    //4-1.添加按钮的监听器
    public class PlusButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            EditText et = new EditText(Activity_PublishThree.this);
            et.setBackgroundColor(Color.rgb(207, 206, 206));
            et.setTextSize(15);
            et.setGravity(Gravity.LEFT);
            et.setPadding(20, 20, 20, 20);                  //左、上、右、下
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80);
            layoutParams.setMargins(20, 20, 20, 0);         //左、上、右、下
            et.setLayoutParams(layoutParams);
            linearLayout.addView(et);
        }
    }

    //4-2.删除按钮的监听器
    public class MinusButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            int i = linearLayout.getChildCount();

            if (i == 1){
                //如果只有一个EditText，不删除
            }
            else {
                linearLayout.removeViewAt(i - 1);
            }
        }
    }

    //4-3.提交按钮的监听器
    public class SubmitListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //讲师姓名
            if (teacherNameEditText.getText().toString().length() == 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PublishThree.this);
                builder.setMessage("请填写讲师姓名");
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
            else {
                    teacherName = teacherNameEditText.getText().toString();
            }

            //讲师简介
            if (teacherIntroEditText.getText().toString().length() == 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PublishThree.this);
                builder.setMessage("请填写讲师简介");
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
            else {
                teacherIntro = teacherIntroEditText.getText().toString();
            }

            //联系电话
            if (contactEditText.getText().toString().length() == 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PublishThree.this);
                builder.setMessage("请填写联系电话");
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
            else if (!contactEditText.getText().toString().startsWith("1") | contactEditText.getText().toString().length()!= 11){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PublishThree.this);
                builder.setMessage("请填写正确格式的电话");
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
            else {
                contact = contactEditText.getText().toString();
            }

            //课程简介
            if (courseIntroEditText.getText().toString().length() == 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PublishThree.this);
                builder.setMessage("请填写课程简介");
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
            else {
                courseIntro = courseIntroEditText.getText().toString();
            }

            //适用人群
            if (targetCustomerEditText.getText().toString().length() == 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PublishThree.this);
                builder.setMessage("请填写适用人群");
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
            else {
                targetCustomer = targetCustomerEditText.getText().toString();
            }

            //课程目录
            int i = linearLayout.getChildCount();
            contents = new String[i];
            for (int j = 0; j < i; j++){
                EditText e = (EditText)linearLayout.getChildAt(j);
                contents[j] = e.getText().toString();

                //第一条目录为空
                if (contents[j].length() == 0 && j ==0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PublishThree.this);
                    builder.setMessage("请填写目录");
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

                //某一条目录为空
                if (contents[j].length() == 0 && j != 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PublishThree.this);
                    builder.setMessage("请删除空白目录");
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
            }

            //检查无误后，发送数据
            uploadData();                           //4-4.利用OkHttp上传数据
        }
    }

    //4-4.利用OkHttp上传数据
    public void uploadData(){
        //目录
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < contents.length; i++){
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("lecture" + i, contents[i]);
                jsonArray.put(i, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String lectures = jsonArray.toString();             //将目录存储为字符串
        System.out.println(lectures);

        //用OkHttp发送POST数据
        OkHttpClient mOkHttpClient = new OkHttpClient();

        File file = new File(picturePath);

        String absence = "未知";

        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"_xsrf\""), RequestBody.create(null, _xsrf))

                //课程
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"courseName\""), RequestBody.create(null, className))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"typeID\""), RequestBody.create(null, subjectCode))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"typeName\""), RequestBody.create(null, subjectString))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"subjectID\""), RequestBody.create(null, courseCode))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"subjectName\""), RequestBody.create(null, courseString))

                //市区
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"cityID\""), RequestBody.create(null, provinceCode + cityCode))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"cityName\""), RequestBody.create(null, cityName))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"districtID\""), RequestBody.create(null, districtCode))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"districtName\""), RequestBody.create(null, districtName))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"address\""), RequestBody.create(null, locationDetail))

                //坐标
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"locationLat\""), RequestBody.create(null, lat))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"locationLng\""), RequestBody.create(null, lng))

                //课程日期
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"beginTime\""), RequestBody.create(null, beginYear + "-" + beginMonth + "-" + beginDay))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"endTime\""), RequestBody.create(null, endYear + "-" + endMonth + "-" + endDay))

                //课程时间
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"courseWeek1\""), RequestBody.create(null, transform(morningWeekday)))                                   //4-5.把中文数字改为阿拉伯数字
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"courseTime1\""), RequestBody.create(null, trim(morningBeginTime) + "-" + trim(morningEndTime)))         //4-6.修改时间格式
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"courseWeek2\""), RequestBody.create(null, transform(afternoonWeekday)))                                 //4-5.把中文数字改为阿拉伯数字
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"courseTime2\""), RequestBody.create(null, trim(afternoonBeginTime) + "-" + trim(afternoonEndTime)))     //4-6.修改时间格式

                //其它
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"description\""), RequestBody.create(null, courseIntro))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"price\""), RequestBody.create(null, price))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"TargetCustomer\""), RequestBody.create(null, targetCustomer))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"phoneNum\""), RequestBody.create(null, contact))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"addition\""), RequestBody.create(null, absence))

                //讲师信息
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"teacherName\""), RequestBody.create(null, teacherName))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"teacherTitle\""), RequestBody.create(null, teacherIntro))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"institutionID\""), RequestBody.create(null, agencyID))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"institutionName\""), RequestBody.create(null, agencyName))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"studentMax\""), RequestBody.create(null, enrollNumber))

                //目录
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"courseContents\""), RequestBody.create(null, lectures))

                //图片
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"" + file.getName() + "\""), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(PATH)
                .addHeader("Cookie", cookie)
                .post(requestBody)
                .build();

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                System.out.println("失败");
                //网络错误
                handler.sendEmptyMessage(FAILED);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                System.out.println(responseData);

                //获取返回状态
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    String feedback = jsonObject.getString("feedback");

                    //提交成功
                    if (feedback.equals("success")){
                        handler.sendEmptyMessage(SUCCESS);
                    }

                    else {
                        handler.sendEmptyMessage(UNKNOWN_REASON);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(UNKNOWN_REASON);
                    System.out.println("其他原因");
                }
            }
        });
    }

    //4-5.把中文数字改为阿拉伯数字
    public String transform(String s){
        s = s.replace("一", "1");
        s = s.replace("二", "2");
        s = s.replace("三", "3");
        s = s.replace("四", "4");
        s = s.replace("五", "5");
        s = s.replace("六", "6");
        s = s.replace("日", "7");
        return s;
    }

    //4-6.修改时间格式
    public String trim(String s){
        s = s.replace(":", "");
        s = s.trim();
        s = s + "00";
        return s;
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