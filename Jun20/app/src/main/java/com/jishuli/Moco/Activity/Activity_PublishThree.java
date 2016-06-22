package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
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

import com.jishuli.Moco.ExitApplication;
import com.jishuli.Moco.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Activity_PublishThree extends Activity {
    static private String PATH = "http://120.25.166.18/courseNew";
    //static private String PATH = "http://10.0.2.2:8000/courseNew";

    //从第一页和第二页传来的数据
    private String className;
    private String classCode;
    private String subjectString;
    private String courseString;
    private String provinceCode;
    private String cityCode;
    private String cityName;
    private String districtCode;
    private String districtName;
    private String lat;
    private String lng;
    private Bitmap pictureBitmap;
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

    //第三页的数据
    private String agencyName;
    private String teacherName;
    private String teacherIntro;
    private String contact;
    private String courseIntro;
    private String targetCustomer;


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

    private static final int SUCCESS = 0;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case SUCCESS:
                    //提交成功的对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PublishThree.this);
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
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //添加到activityList列表中
        ExitApplication.getInstance().addActivity(this);

        setContentView(R.layout.activity_publishthree);

        findViews();        //1.找到各种控件
        getData();          //2.从上一个Activity传来的数据
        setSpinner();       //3.设置机构名称的下拉菜单
        setListeners();     //4.设置监听器

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
    public void findViews(){
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
        classCode = bundle.getString("classcode");
        subjectString = bundle.getString("typeName");
        courseString = bundle.getString("subjectName");
        Intent intent = getIntent();
        pictureBitmap = intent.getParcelableExtra("bitmap");
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
    }

    //3.设置机构名称的下拉菜单
    public void setSpinner(){
        final String[] strings = new String[10];
        for (int i = 0; i < 10; i++){
            strings[i] = "机构名称" + i;
        }
        ArrayAdapter<String> agencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, strings);
        agencySpinner.setAdapter(agencyAdapter);
        agencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                agencyName = strings[position];
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

        plusButton.setOnClickListener(new PlusButtonListener());        //4-1.添加按钮的监听器
        minusButton.setOnClickListener(new MinusButtonListener());      //4-2.删除按钮的监听器
        nextButton.setOnClickListener(new NextListener());              //4-3.下一步按钮的监听器
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

    //4-3.下一步按钮的监听器
    public class NextListener implements View.OnClickListener{
        String[] contents;      //课程目录

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

            /*System.out.println("课程名称：" + className);
            System.out.println("课程编码：" + classCode);
            System.out.println("价格：" + price);
            System.out.println("开始时间：" + beginYear + "-" + beginMonth + "-" + beginDay);
            System.out.println("结束时间：" + endYear + "-" + endMonth + "-" + endDay);
            System.out.println("上午：" + morningWeekday);
            System.out.println("上午时间：" + morningBeginTime + "~" + morningEndTime);
            System.out.println("下午：" + afternoonWeekday);
            System.out.println("下午时间：" + afternoonBeginTime + "~" + afternoonEndTime);
            System.out.println("地址编码：" + provinceCode + cityCode + districtCode);
            System.out.println("详细地址：" + locationDetail);
            System.out.println("开课人数：" + enrollNumber);
            System.out.println("机构名称：" + agencyName);
            System.out.println("教师名称：" + teacherName);
            System.out.println("教师简介：" + teacherIntro);
            System.out.println("联系电话：" + contact);
            System.out.println("课程简介：" + courseIntro);
            System.out.println("适用人群：" + targetCustomer);
            for (String s : contents) {
                System.out.println(s);
            }*/

            SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
            String userName = sharedPreferences.getString("userName", null);
            String cookie = sharedPreferences.getString("cookie", null);

            StringBuilder contentStringBuilder = new StringBuilder(cookie);

            String absence = "未知";
            JSONObject jsonObject = new JSONObject();
            try {
                //用户名
                jsonObject.put("userID", userName);

                //课程
                jsonObject.put("courseName", className);
                jsonObject.put("typeID", classCode.substring(0,2));
                jsonObject.put("typeName", subjectString);
                jsonObject.put("subjectID", classCode.substring(2,4));
                jsonObject.put("subjectName", courseString);

                //市区
                jsonObject.put("cityID", provinceCode + cityCode);
                jsonObject.put("cityName", cityName);
                jsonObject.put("districtID", districtCode);
                jsonObject.put("districtName", districtName);
                jsonObject.put("address", locationDetail);

                //坐标
                jsonObject.put("locationLat", lat);
                jsonObject.put("locationLng", lng);

                jsonObject.put("beginTime", beginYear + "-" + beginMonth + "-" + beginDay);
                jsonObject.put("endTime", endYear + "-" + endMonth + "-" + endDay);

                //课程时间
                //4-4.把中文数字改为阿拉伯数字
                jsonObject.put("week1", transform(morningWeekday));
                //4-5.修改时间格式
                jsonObject.put("time1", trim(morningBeginTime) + "-" + trim(morningEndTime));
                //4-4.把中文数字改为阿拉伯数字
                jsonObject.put("week2", transform(afternoonWeekday));
                //4-5.修改时间格式
                jsonObject.put("time2", trim(afternoonBeginTime) + "-" + trim(afternoonEndTime));

                jsonObject.put("description", courseIntro);
                jsonObject.put("price", price);
                jsonObject.put("TargetCustomer", targetCustomer);

                //课程目录
                for (int j = 0; j < contents.length; j++){
                    jsonObject.put("lecture" + (j + 1), contents[j]);
                }
               /* JSONObject jsonContentsObj = new JSONObject();
                for (int j = 0; j < contents.length; j++){
                    jsonContentsObj.put("lecture" + (j + 1), contents[j]);
                }
                jsonObject.put("courseContents", jsonContentsObj);*/

                //课程图片  bitmap转为String
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                pictureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] coursePic = byteArrayOutputStream.toByteArray();
                String picString1 = Base64.encodeToString(coursePic, Base64.DEFAULT);

                jsonObject.put("Img", picString1);

                jsonObject.put("phoneNum", contact);
                jsonObject.put("addition", absence);

                //讲师信息
                jsonObject.put("teacherName", teacherName);
                jsonObject.put("teacherTitle", teacherIntro);

                jsonObject.put("institutionID", absence);
                jsonObject.put("studentMax", enrollNumber);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            contentStringBuilder.append("&userID=" + userName);
            contentStringBuilder.append("&courseName=" + className);
            contentStringBuilder.append("&typeId=" + classCode.substring(0, 2));
            contentStringBuilder.append("&typeName=" + subjectString);
            contentStringBuilder.append("&subjectID=" + classCode.substring(2,4));
            contentStringBuilder.append("&subjectName=" + courseString);
            contentStringBuilder.append("&cityID=" + provinceCode + cityCode);
            contentStringBuilder.append("&cityName=" + cityName);
            contentStringBuilder.append("&districtID=" + districtCode);
            contentStringBuilder.append("&districtName=" + districtName);
            contentStringBuilder.append("&address=" + locationDetail);
            contentStringBuilder.append("&locationLat=" + lat);
            contentStringBuilder.append("&locationLng=" + lng);
            contentStringBuilder.append("&beginTime=" + beginYear + "-" + beginMonth + "-" + beginDay);
            contentStringBuilder.append("&endTime=" + endYear + "-" + endMonth + "-" + endDay);
            contentStringBuilder.append("&week1=" + transform(morningWeekday));
            contentStringBuilder.append("&time1=" + trim(morningBeginTime) + "-" + trim(morningEndTime));
            contentStringBuilder.append("&week2=" + transform(afternoonWeekday));
            contentStringBuilder.append("&time2=" + trim(afternoonBeginTime) + "-" + trim(afternoonEndTime));
            for (int j = 0; j < contents.length; j++){
                contentStringBuilder.append("&lecture" + (j + 1) + "=" + contents[j]);
            }
            contentStringBuilder.append("&phoneNum=" + contact);
            contentStringBuilder.append("&addition=" + absence);
            contentStringBuilder.append("&phoneNum=" + contact);
            contentStringBuilder.append("&addition=" + absence);
            contentStringBuilder.append("&teacherName=" + teacherName);
            contentStringBuilder.append("&teacherTitle=" + teacherIntro);
            contentStringBuilder.append("&Img=" + "abc");

            //System.out.println(jsonObject);
            sendData(jsonObject, contentStringBuilder);                   //4-6.发送数据
        }
    }

    //4-4.把中文数字改为阿拉伯数字
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

    //4-5.修改时间格式
    public String trim(String s){
        s = s.replace(":", "");
        s = s.trim();
        s = s + "00";
        return s;
    }

    //4-6.发送数据
    public void sendData(final JSONObject jsonObject, final StringBuilder contentStringBuilder){
        new Thread(new Runnable() {
            //String content = String.valueOf(jsonObject);
            String content = contentStringBuilder.toString();

            @Override
            public void run() {
                try {
                    URL url = new URL(PATH);

                    //取出cookie
                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
                    String cookie = sharedPreferences.getString("cookie", null);

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setRequestProperty("Cookie", cookie);
                    httpURLConnection.setRequestProperty("Connection", "keep-alive");
                    httpURLConnection.setRequestProperty("User-Agent", "Fiddler");
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    httpURLConnection.setRequestProperty("Charset", "utf-8");

                    System.out.println("取出的cookie：" + cookie);
                    System.out.println("提交的内容：" + content);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(content.getBytes());
                    outputStream.close();

                    //获得返回代码
                    String responseCode = httpURLConnection.getResponseCode() + "";
                    System.out.println(responseCode);

                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        String data;
                        String responseData = "";
                        while((data = bufferedReader.readLine()) != null)
                        {
                            responseData += data;
                        }
                        JSONObject jsonObject = new JSONObject(responseData);
                        String feedback = jsonObject.getString("feedback");
                        System.out.println(feedback);

                        bufferedReader.close();

                        //根据返回的提示，显示相应的对话框
                        //注册成功
                        if (feedback.equals("success")){
                            handler.sendEmptyMessage(SUCCESS);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}