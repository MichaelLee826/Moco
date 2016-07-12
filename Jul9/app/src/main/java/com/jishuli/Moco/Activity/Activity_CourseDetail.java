package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jishuli.Moco.Adapter.Adapter_Category;
import com.jishuli.Moco.Adapter.Adapter_CourseIntro_ViewPager;
import com.jishuli.Moco.R;
import com.scrollablelayout.ScrollableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Activity_CourseDetail extends Activity {
    private String PATH = "http://120.25.166.18/courseDetail";
    private String courseID;
    private String cityID;

    private ScrollableLayout sl_root;
    private ViewPager viewPager;
    private List<View> viewList;

    private Button saveButton;
    private Button enrollButton;
    private ImageButton backButton;
    private ImageButton shareButton;

    private TextView introductionTitle;
    private TextView categoryTitle;
    private TextView commentTitle;

    private int currentIndex = 0;
    private int offset = 0;
    private int width = 0;

    //概况
    private ImageView pictureImageView;
    private TextView courseNameTextView;
    private TextView coursePriceTextView;
    private TextView rateNumberTextView;
    private TextView enrollNumberTextView;
    private TextView showTimeTextView;
    private TextView showLocationTextView;
    private ImageView heartsImageViews[] = new ImageView[5];

    private String pictureString;
    private Bitmap pictureBitmap;
    private String courseName;
    private double price;
    private int score;
    private int studentIn;
    private String beginTime;
    private String endTime;
    private String address;

    //简介
    private String description;
    private String courseWeek1;
    private String courseTime1;
    private String courseWeek2;
    private String courseTime2;
    private String institutionName;
    private String teacherName;
    private String teacherTitle;
    private String targetCustomer;

    //目录
    private String[] category;

    //评论
    private String comments;

    private final static int DONE = 0;
    private final static int ENROLL_SUCCESS = 1;
    private final static int SAVE_SUCCESS = 2;
    private final static int ENROLL_FAIL = 3;
    private final static int SAVE_FAIL = 4;
    private final static int ALREADY_ENROLL = 5;
    private final static int ALREADY_SAVE = 6;
    private final static int SET_PICTURE = 7;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            AlertDialog.Builder builder;
            switch (msg.what){
                case DONE:
                    setData();          //4.设置详情数据
                    setViewPager();     //5.设置ViewPager
                    break;

                case ENROLL_SUCCESS:
                    builder = new AlertDialog.Builder(Activity_CourseDetail.this);
                    builder.setMessage("报名成功！");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;

                case SAVE_SUCCESS:
                    builder = new AlertDialog.Builder(Activity_CourseDetail.this);
                    builder.setMessage("收藏成功！");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;

                case ALREADY_ENROLL:
                    builder = new AlertDialog.Builder(Activity_CourseDetail.this);
                    builder.setMessage("已经报名了");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;

                case ALREADY_SAVE:
                    builder = new AlertDialog.Builder(Activity_CourseDetail.this);
                    builder.setMessage("已经收藏了");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;

                case ENROLL_FAIL:
                    builder = new AlertDialog.Builder(Activity_CourseDetail.this);
                    builder.setMessage("报名失败！");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;

                case SAVE_FAIL:
                    builder = new AlertDialog.Builder(Activity_CourseDetail.this);
                    builder.setMessage("收藏失败！");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;

                case SET_PICTURE:
                    pictureImageView.setImageBitmap(pictureBitmap);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coursedetail);

        init();                 //1.初始化控件
        getData();              //2.下载数据
        setListeners();         //3.设置监听器
    }

    //1.初始化控件
    public void init(){
        sl_root = (ScrollableLayout) findViewById(R.id.sl_root);            //开源项目ScrollableLayout的控件
        viewPager = (ViewPager)findViewById(R.id.coursedetail_viewpager);

        pictureImageView = (ImageView)findViewById(R.id.coursedetailImageView);
        courseNameTextView = (TextView)findViewById(R.id.coursedetail_coursename);
        coursePriceTextView = (TextView)findViewById(R.id.coursedetail_price);
        rateNumberTextView = (TextView)findViewById(R.id.coursedetail_ratenumber);
        enrollNumberTextView = (TextView)findViewById(R.id.coursedetail_enrollnumber);
        showTimeTextView = (TextView)findViewById(R.id.coursedetail_showtime);
        showLocationTextView = (TextView)findViewById(R.id.coursedetail_showlocaton);
        heartsImageViews[0] = (ImageView)findViewById(R.id.courdetail_heart1);
        heartsImageViews[1] = (ImageView)findViewById(R.id.courdetail_heart2);
        heartsImageViews[2] = (ImageView)findViewById(R.id.courdetail_heart3);
        heartsImageViews[3] = (ImageView)findViewById(R.id.courdetail_heart4);
        heartsImageViews[4] = (ImageView)findViewById(R.id.courdetail_heart5);

        backButton = (ImageButton)findViewById(R.id.coursedetail_back);
        shareButton = (ImageButton)findViewById(R.id.coursedetail_share);
        saveButton = (Button)findViewById(R.id.coursedetail_saveButton);
        enrollButton = (Button)findViewById(R.id.coursedetail_enrollButton);

        introductionTitle = (TextView)findViewById(R.id.viewpager_title1);
        categoryTitle = (TextView)findViewById(R.id.viewpager_title2);
        commentTitle = (TextView)findViewById(R.id.viewpager_title3);
    }

    //2.下载数据
    public void getData(){
        Bundle bundle = this.getIntent().getExtras();
        courseID = bundle.getString("courseID");
        cityID = bundle.getString("cityID");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path = PATH + "?courseID=" + courseID + "&cityID=" + cityID;
                    System.out.println(path);
                    URL url = new URL(path);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setConnectTimeout(5000);      //超时时间，5秒
                    httpURLConnection.setRequestMethod("GET");      //方式为GET
                    httpURLConnection.setDoInput(true);

                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        InputStream inputStream = httpURLConnection.getInputStream();   //获得输入流
                        byte[] data = readStream(inputStream);                          //把输入流转换成字符串组，单独一个函数：2-1
                        String JSONString = new String(data);                           //把字符串组转换成字符串

                        JSONObject jsonObject = new JSONObject(JSONString);             //得到总的JSON数据
                        System.out.println(jsonObject.toString());

                        //概况
                        pictureString = jsonObject.getString("Img");
                        courseName = jsonObject.getString("courseName");
                        price = jsonObject.getInt("price");
                        score = jsonObject.getInt("score");
                        studentIn = jsonObject.getInt("studentIn");
                        beginTime = jsonObject.getString("beginTime");
                        endTime = jsonObject.getString("endTime");
                        address = jsonObject.get("cityName") + jsonObject.getString("districtName") + jsonObject.getString("address");

                        //简介
                        description = jsonObject.getString("description");
                        courseWeek1 = jsonObject.getString("courseWeek1");
                        courseWeek2 = jsonObject.getString("courseWeek2");
                        courseTime1 = jsonObject.getString("courseTime1");
                        courseTime2 = jsonObject.getString("courseTime2");
                        institutionName = jsonObject.getString("institutionName");
                        teacherName = jsonObject.getString("teacherName");
                        teacherTitle = jsonObject.getString("teacherTitle");
                        targetCustomer = jsonObject.getString("TargetCustomer");

                        //目录
                        String temp = jsonObject.getString("courseContents");
                        temp = temp.replace("\"", "");                          //需要把引号去掉才能生成JSONArray

                        JSONArray jsonArray = new JSONArray(temp);
                        category = new String[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++){
                            category[i] = jsonArray.getJSONObject(i).get("lecture" + i).toString();
                        }

                        //评论
                        comments= jsonObject.getString("comments");

                        //说明已下载完数据
                        handler.sendEmptyMessage(DONE);
                        getPicture();                           //6.下载图片
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

    //3.设置监听器
    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_CourseDetail.this.finish();
            }
        });

        introductionTitle.setOnClickListener(new TitleTextViwOnClickListener(0));       //3-1.点击标题时切换ViewPager
        categoryTitle.setOnClickListener(new TitleTextViwOnClickListener(1));           //3-1.点击标题时切换ViewPager
        commentTitle.setOnClickListener(new TitleTextViwOnClickListener(2));            //3-1.点击标题时切换ViewPager

        enrollButton.setOnClickListener(new enrollListener());                          //3-2.报名按钮监听器
        saveButton.setOnClickListener(new saveListener());                              //3-3.收藏按钮监听器
    }

    //4.设置详情数据
    public void setData(){
        beginTime = beginTime.replace(" ", "");
        beginTime = beginTime.trim();
        beginTime = beginTime.replace("-", ".");
        beginTime = beginTime.substring(0, 10);
        //不显示时间      + " " + beginTime.substring(10, 12) + ":" + beginTime.substring(12, 14);

        endTime = endTime.replace(" ", "");
        endTime = endTime.trim();
        endTime = endTime.replace("-", ".");
        endTime = endTime.substring(0, 10);
        //不显示时间      + "  " + endTime.substring(10, 12) + ":" + endTime.substring(12, 14);

        //courseTime = courseTime.substring(1, 3) + ":" + courseTime.substring(3, 5) + " -" + courseTime.substring(5, 7) + ":" + courseTime.substring(7, 9);

        courseNameTextView.setText(courseName);

        //价格
        if (price == 0){
            coursePriceTextView.setText("免费");
        }
        else {
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            String priceString = decimalFormat.format(price);
            coursePriceTextView.setText("￥" + priceString);
        }

        enrollNumberTextView.setText(studentIn + "人报名");
        showTimeTextView.setText(beginTime + " ~ " + endTime);
        showLocationTextView.setText(address);

        //设置心形的数量
        switch (score){
            case 0:
                for (int i = 0; i < 5; i++){
                    heartsImageViews[i].setImageResource(R.drawable.heart_gray);
                }
                break;

            case 1:
               heartsImageViews[0].setImageResource(R.drawable.heart_green);
                for (int i = 1; i < 4; i++){
                    heartsImageViews[i].setImageResource(R.drawable.heart_gray);
                }
                break;

            case 2:
                for (int i = 0; i < 2; i++){
                    heartsImageViews[i].setImageResource(R.drawable.heart_green);
                }
                for (int i = 2; i < 5; i++){
                    heartsImageViews[i].setImageResource(R.drawable.heart_gray);
                }
                break;

            case 3:
                for (int i = 0; i < 3; i++){
                    heartsImageViews[i].setImageResource(R.drawable.heart_green);
                }
                for (int i = 3; i < 5; i++){
                    heartsImageViews[i].setImageResource(R.drawable.heart_gray);
                }
                break;

            case 4:
                for (int i = 0; i < 4; i++){
                    heartsImageViews[i].setImageResource(R.drawable.heart_green);
                }
                heartsImageViews[4].setImageResource(R.drawable.heart_gray);
                break;

            case 5:
                for (int i = 0; i < 5; i++){
                    heartsImageViews[i].setImageResource(R.drawable.heart_green);
                }
                break;
        }
    }

    //5.设置ViewPager
    public void setViewPager(){
        //简介部分
        TextView introductionContents, introductionDate, introductionTime1, introductionTime2,
                 introductionInstitutionName, introductionInstitutionDescription,
                 introductionTeacherName, introductionTeacherTitle, introductionTargetCustomers;

        //目录部分
        ListView categoryListView;

        //评论部分
        ListView commentListView;

        viewList = new ArrayList<View>();
        LayoutInflater layoutInflater = getLayoutInflater().from(this);

        //三个tab
        View view1 = layoutInflater.inflate(R.layout.coursedetail_viewpager1, null);
        View view2 = layoutInflater.inflate(R.layout.coursedetail_viewpager2, null);
        View view3 = layoutInflater.inflate(R.layout.coursedetail_viewpager3, null);

        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);

        viewPager.setAdapter(new Adapter_CourseIntro_ViewPager(viewList));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new PageChangeListener());                    //5-1.切换ViewPager的监听器

        //简介部分
        introductionContents = (TextView)view1.findViewById(R.id.viewPager1_contents2);
        introductionDate = (TextView)view1.findViewById(R.id.viewPager1_date);
        introductionTime1 = (TextView)view1.findViewById(R.id.viewPager1_time1);
        introductionTime2 = (TextView)view1.findViewById(R.id.viewPager1_time2);
        introductionInstitutionName = (TextView)view1.findViewById(R.id.viewPager1_institutionName);
        introductionInstitutionDescription = (TextView)view1.findViewById(R.id.viewPager1_institutionDescription);
        introductionTeacherName = (TextView)view1.findViewById(R.id.viewPager1_teacherName);
        introductionTeacherTitle = (TextView)view1.findViewById(R.id.viewPager1_teacherTitle);
        introductionTargetCustomers = (TextView)view1.findViewById(R.id.viewPager1_targetCustomer);

        introductionContents.setText("\t" + "\t" + description);               //课程简介

        introductionDate.setText(beginTime + " 至 " + endTime);                //上课时间
        StringBuilder stringBuilder1 = new StringBuilder("每周");
        if (courseWeek1.contains("1"))  stringBuilder1.append("一、");
        if (courseWeek1.contains("2"))  stringBuilder1.append("二、");
        if (courseWeek1.contains("3"))  stringBuilder1.append("三、");
        if (courseWeek1.contains("4"))  stringBuilder1.append("四、");
        if (courseWeek1.contains("5"))  stringBuilder1.append("五、");
        if (courseWeek1.contains("6"))  stringBuilder1.append("六、");
        if (courseWeek1.contains("7"))  stringBuilder1.append("日、");
        String string1 = stringBuilder1.substring(0, stringBuilder1.length() - 1);
        courseTime1 = courseTime1.substring(0, 2) + ":" + courseTime1.substring(2, 4) + "-" + courseTime1.substring(7, 9) + ":" + courseTime1.substring(9, 11);
        introductionTime1.setText(string1 + "：  " + courseTime1);

        StringBuilder stringBuilder2 = new StringBuilder("每周");
        if (courseWeek2.contains("1"))  stringBuilder2.append("一、");
        if (courseWeek2.contains("2"))  stringBuilder2.append("二、");
        if (courseWeek2.contains("3"))  stringBuilder2.append("三、");
        if (courseWeek2.contains("4"))  stringBuilder2.append("四、");
        if (courseWeek2.contains("5"))  stringBuilder2.append("五、");
        if (courseWeek2.contains("6"))  stringBuilder2.append("六、");
        if (courseWeek2.contains("7"))  stringBuilder2.append("日、");
        String string2 = stringBuilder2.substring(0, stringBuilder2.length() - 1);
        courseTime2 = courseTime2.substring(0, 2) + ":" + courseTime2.substring(2, 4) + "-" + courseTime2.substring(7, 9) + ":" + courseTime2.substring(9, 11);
        introductionTime2.setText(string2 + "：  " + courseTime2);

        introductionInstitutionName.setText(institutionName);                       //机构名称
        //introductionInstitutionDescription.setText(institutionDescription);       //机构介绍
        introductionTeacherName.setText(teacherName);                               //讲师名称
        introductionTeacherTitle.setText(teacherTitle);                             //讲师介绍

        introductionTargetCustomers.setText(targetCustomer);                        //适用人群

        //目录部分
        categoryListView = (ListView) view2.findViewById(R.id.viewPager2_listView);
        ListAdapter categoryAdapter = new Adapter_Category(this, category);
        categoryListView.setAdapter(categoryAdapter);

        //评价部分
        commentListView = (ListView) view3.findViewById(R.id.viewPager3_listView);
        int size = 100;
        String[] stringArray = new String[size];
        for (int i = 0; i < size; ++i) {
            stringArray[i] = ""+i;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringArray);
        commentListView.setAdapter(adapter);

        sl_root.getHelper().setCurrentScrollableContainer(categoryListView);        //开源项目ScrollableLayout，填view1的话，滑动会有问题
    }

    //2-1.读数据中用到的函数
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

    //3-1.点击标题时切换ViewPager
    public class TitleTextViwOnClickListener implements View.OnClickListener{
        private int index = 0;

        public TitleTextViwOnClickListener(int i){
            index = i;
        }

        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }
    }

    //3-2.报名按钮监听器
    public class enrollListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //取出cookie
            SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
            String cookie = sharedPreferences.getString("cookie", null);

            //没有登录
            if (cookie == null){
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(Activity_CourseDetail.this);
                builder.setMessage("请登录");
                builder.setTitle("提示");
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClass(Activity_CourseDetail.this, Activity_SignIn.class);
                        startActivity(intent);

                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }

            //已登录
            else {
                String path = "http://120.25.166.18/courseJoinIn";
                path = path + "?courseID=" + courseID;
                System.out.println(path);

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

                        //获取返回状态
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String feedback = jsonObject.getString("feedback");

                            //提交成功
                            if (feedback.equals("success")){
                                handler.sendEmptyMessage(ENROLL_SUCCESS);
                            }
                            else if (feedback.equals("course exist")){
                                handler.sendEmptyMessage(ALREADY_ENROLL);
                            }
                            else {
                                handler.sendEmptyMessage(ENROLL_FAIL);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("其他原因");
                        }
                    }
                });
            }
        }
    }

    //3-3.收藏按钮监听器
    public class saveListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //取出cookie
            SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
            String cookie = sharedPreferences.getString("cookie", null);

            //没有登录
            if (cookie == null){
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(Activity_CourseDetail.this);
                builder.setMessage("请登录");
                builder.setTitle("提示");
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClass(Activity_CourseDetail.this, Activity_SignIn.class);
                        startActivity(intent);

                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }

            //已登录
            else {
                String path = "http://120.25.166.18/courseFollow";
                path = path + "?courseID=" + courseID;
                System.out.println(path);

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

                        //获取返回状态
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String feedback = jsonObject.getString("feedback");

                            //提交成功
                            if (feedback.equals("success")){
                                handler.sendEmptyMessage(SAVE_SUCCESS);
                            }
                            else if (feedback.equals("course exist")){
                                handler.sendEmptyMessage(ALREADY_SAVE);
                            }
                            else {
                                handler.sendEmptyMessage(SAVE_FAIL);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("其他原因");
                        }
                    }
                });
            }
        }
    }

    //5-1.切换ViewPager的监听器
    public class PageChangeListener implements ViewPager.OnPageChangeListener{
        int one = offset * 2 + width;           // 页卡1 -> 页卡2 偏移量
        int two = one * 2;                      // 页卡1 -> 页卡3 偏移量

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position){
                case 0: {
                    introductionTitle.setTextColor(Color.rgb(71, 187, 150));
                    categoryTitle.setTextColor(Color.BLACK);
                    commentTitle.setTextColor(Color.BLACK);
                    break;
                }
                case 1: {
                    introductionTitle.setTextColor(Color.BLACK);
                    categoryTitle.setTextColor(Color.rgb(71, 187, 150));
                    commentTitle.setTextColor(Color.BLACK);
                    break;
                }
                case 2: {
                    introductionTitle.setTextColor(Color.BLACK);
                    categoryTitle.setTextColor(Color.BLACK);
                    commentTitle.setTextColor(Color.rgb(71, 187, 150));
                    break;
                }
            }
            //构造函数：TranslateAnimation(float fromXDelta, float toXDelta, float fromYDelta, floattoYDelta)
            //Animation animation = new TranslateAnimation(one * currentIndex, one * position, 0, 0);

            currentIndex = position;

            //animation.setFillAfter(true);           // True:图片停在动画结束位置
            //animation.setDuration(300);
            //imageView.startAnimation(animation);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    //按下返回键，返回到首页
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Activity_CourseDetail.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    //6.下载图片
    public void getPicture(){
        String path = "http://120.25.166.18/" + pictureString;

        try {
            URL url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(5000);      //超时时间，5秒
            httpURLConnection.setRequestMethod("GET");      //方式为GET
            httpURLConnection.setDoInput(true);

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream inputStream = httpURLConnection.getInputStream();   //获得输入流
                byte[] data = readStream(inputStream);                          //把输入流转换成字符串组，单独一个函数：2-1
                if(data != null){
                    pictureBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                }
                inputStream.close();

                handler.sendEmptyMessage(SET_PICTURE);          //下载完成后，设置图片
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
}