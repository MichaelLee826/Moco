package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jishuli.Moco.Adapter.Adapter_CourseIntro_ViewPager;
import com.jishuli.Moco.R;
import com.scrollablelayout.ScrollableLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Activity_CourseDetail extends Activity {
    private String PATH = "http://120.25.166.18/coursedetail?courseID=1&city=nanjing";

    private ScrollableLayout sl_root;
    private ViewPager viewPager;
    private List<View> viewList;

    private TextView courseNameTextView;
    private TextView coursePriceTextView;
    private TextView rateNumberTextView;
    private TextView enrollNumberTextView;
    private TextView showTimeTextView;
    private TextView showLocationTextView;
    private ImageView heartsImageViews[] = new ImageView[5];

    private Button saveButton;
    private Button enrollButton;
    private ImageButton backButton;
    private ImageButton shareButton;

    private TextView introductionTitile;
    private TextView categoryTitile;
    private TextView commentTitile;

    private View view1;
    private View view2;
    private View view3;

    private TextView introductionContents;
    private TextView introductionInstitutionName;
    private TextView introductionInstitutionDescripton;
    private TextView introductionTeacherName;
    private TextView introductionTeacherTitle;
    private TextView introductionTargetCustomers;

    private TextView categoryTextView;
    private TextView commentTextView;

    private int currentIndex = 0;
    private int offset = 0;
    private int width = 0;

    private String status;
    private String classDescription;
    private String img1;
    private String img2;
    private String districtID;
    private int studentIn;
    private String courseName;
    private String phoneNum;
    private String subjectID;
    private double price;
    private String beginTime;
    private String endTime;
    private String releaseTime;
    private String student;
    private String address;
    private String shareLink;
    private String teacherName;
    private String teacherTitle;
    private String typeID;
    private int studentMax;
    private String courseTime;
    private String addition;
    private String targetCustomer;
    private String courseContents;
    private String userID;
    private String comments;
    private String institutionID;
    private String institutionDesctiption;
    private String institutionName;
    private String cityID;
    private String courseID;
    private int score;
    private double lat;
    private double lng;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{
                    setData();          //4.设置详情数据
                    setViewPager();     //5.设置ViewPager
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coursedetail);

        findViews();            //1.找到各种控件
        getData();              //2.下载数据
        setListeners();         //3.设置监听器

        Bundle bundle = this.getIntent().getExtras();
        int id = bundle.getInt("id");

        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_CourseDetail.this.finish();
            }
        });
    }

    //1.找到各种控件
    public void findViews(){
        sl_root = (ScrollableLayout) findViewById(R.id.sl_root);            //开源项目ScrollableLayout的控件
        viewPager = (ViewPager)findViewById(R.id.coursedetail_viewpager);

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
        enrollButton = (Button)findViewById(R.id.coursedetail_signupButton);

        introductionTitile = (TextView)findViewById(R.id.viewpager_title1);
        categoryTitile = (TextView)findViewById(R.id.viewpager_title2);
        commentTitile = (TextView)findViewById(R.id.viewpager_title3);
    }

    //2.下载数据
    public void getData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(PATH);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setConnectTimeout(5000);      //超时时间，5秒
                    httpURLConnection.setRequestMethod("GET");      //方式为GET
                    httpURLConnection.setDoInput(true);

                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        InputStream inputStream = httpURLConnection.getInputStream();   //获得输入流
                        byte[] data = readStream(inputStream);                          //把输入流转换成字符串组，单独一个函数：2-1
                        String JSONString = new String(data);                           //把字符串组转换成字符串

                        JSONObject jsonObject = new JSONObject(JSONString);             //得到总的JSON数据

                        classDescription = jsonObject.getString("description");
                        districtID = jsonObject.getString("districtID");
                        studentIn = jsonObject.getInt("studentIn");
                        courseName = jsonObject.getString("courseName");
                        phoneNum = jsonObject.getString("phoneNum");
                        subjectID = jsonObject.getString("subjectID");
                        beginTime = jsonObject.getString("beginTime");
                        endTime = jsonObject.getString("endTime");
                        releaseTime = jsonObject.getString("releaseTime");
                        student = jsonObject.getString("student");
                        address = jsonObject.getString("address");
                        shareLink = jsonObject.getString("shareLink");
                        typeID = jsonObject.getString("typeID");
                        studentMax = jsonObject.getInt("studentMax");
                        courseTime = jsonObject.getString("courseTime");
                        addition = jsonObject.getString("addition");
                        targetCustomer = jsonObject.getString("TargetCustomer");
                        courseContents = jsonObject.getString("courseContents");
                        userID = jsonObject.getString("userID");
                        comments= jsonObject.getString("comments");
                        cityID = jsonObject.getString("cityID");
                        courseID = jsonObject.getString("courseID");
                        score = jsonObject.getInt("score");
                        price = jsonObject.getInt("price");

                        JSONObject jsonTeacherObj = jsonObject.getJSONObject("teacher");
                        teacherName = jsonTeacherObj.getString("name");
                        teacherTitle = jsonTeacherObj.getString("title");

                        JSONObject jsonInstitutionObj = jsonObject.getJSONObject("institution");
                        institutionID = jsonInstitutionObj.getString("institutionID");
                        institutionDesctiption = jsonInstitutionObj.getString("description");
                        institutionName = jsonInstitutionObj.getString("name");

                        JSONObject jsonLocationObj = jsonObject.getJSONObject("location");
                        lat = jsonLocationObj.getDouble("lat");
                        lng = jsonLocationObj.getDouble("lng");

                        //说明已下载完数据
                        handler.sendEmptyMessage(0);
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
        introductionTitile.setOnClickListener(new TitleTextViwOnClickListener(0));
        categoryTitile.setOnClickListener(new TitleTextViwOnClickListener(1));
        commentTitile.setOnClickListener(new TitleTextViwOnClickListener(2));
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

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String priceString = decimalFormat.format(price);
        coursePriceTextView.setText("￥" + priceString);

        enrollNumberTextView.setText(studentIn + "人报名");
        showTimeTextView.setText(beginTime + " ~ " + endTime);
        showLocationTextView.setText(address);

        //设置心形的数量
        switch (score){
            case 0:
            {
                for (int i = 0; i < 5; i++){
                    heartsImageViews[i].setImageResource(R.drawable.heart_gray);
                }
                break;
            }

            case 1:
            {
               heartsImageViews[0].setImageResource(R.drawable.heart_green);
                for (int i = 1; i < 4; i++){
                    heartsImageViews[i].setImageResource(R.drawable.heart_gray);
                }
                break;
            }

            case 2:
            {
                for (int i = 0; i < 2; i++){
                    heartsImageViews[i].setImageResource(R.drawable.heart_green);
                }
                for (int i = 2; i < 5; i++){
                    heartsImageViews[i].setImageResource(R.drawable.heart_gray);
                }
                break;
            }

            case 3:
            {
                for (int i = 0; i < 3; i++){
                    heartsImageViews[i].setImageResource(R.drawable.heart_green);
                }
                for (int i = 3; i < 5; i++){
                    heartsImageViews[i].setImageResource(R.drawable.heart_gray);
                }
                break;
            }

            case 4:
            {
                for (int i = 0; i < 4; i++){
                    heartsImageViews[i].setImageResource(R.drawable.heart_green);
                }
                heartsImageViews[4].setImageResource(R.drawable.heart_gray);
                break;
            }

            case 5:
            {
                for (int i = 0; i < 5; i++){
                    heartsImageViews[i].setImageResource(R.drawable.heart_green);
                }
                break;
            }
        }
    }

    //5.设置ViewPager
    public void setViewPager(){
        viewList = new ArrayList<View>();
        LayoutInflater layoutInflater = getLayoutInflater().from(this);

        view1 = layoutInflater.inflate(R.layout.viewpager1, null);
        view2 = layoutInflater.inflate(R.layout.viewpager2, null);
        view3 = layoutInflater.inflate(R.layout.viewpager3, null);

        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);

        sl_root.getHelper().setCurrentScrollableContainer(view1);       //开源项目ScrollableLayout，填view1后，功能就正常

        viewPager.setAdapter(new Adapter_CourseIntro_ViewPager(viewList));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new PageChangeListener());

        //简介部分
        introductionContents = (TextView)view1.findViewById(R.id.viewPager1_contents2);
        introductionInstitutionName = (TextView)view1.findViewById(R.id.viewPager1_institutionName);
        introductionInstitutionDescripton = (TextView)view1.findViewById(R.id.viewPager1_institutionDescription);
        introductionTeacherName = (TextView)view1.findViewById(R.id.viewPager1_teacherName);
        introductionTeacherTitle = (TextView)view1.findViewById(R.id.viewPager1_teacherTitle);
        introductionTargetCustomers = (TextView)view1.findViewById(R.id.viewPager1_targetCustomer);

        introductionContents.setText(courseContents);
        introductionInstitutionName.setText(institutionName);
        introductionInstitutionDescripton.setText(institutionDesctiption);
        introductionTeacherName.setText(teacherName);
        introductionTeacherTitle.setText(teacherTitle);
        introductionTargetCustomers.setText(targetCustomer);



        //目录部分
        categoryTextView = (TextView)view2.findViewById(R.id.viewPager2_TexView);
        categoryTextView.setText(courseContents);


        //评价部分
        commentTextView = (TextView)view3.findViewById(R.id.viewPager3_TexView);
        commentTextView.setText(comments);
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
                    introductionTitile.setTextColor(Color.rgb(71, 187, 150));
                    categoryTitile.setTextColor(Color.BLACK);
                    commentTitile.setTextColor(Color.BLACK);
                    break;
                }
                case 1: {
                    introductionTitile.setTextColor(Color.BLACK);
                    categoryTitile.setTextColor(Color.rgb(71, 187, 150));
                    commentTitile.setTextColor(Color.BLACK);
                    break;
                }
                case 2: {
                    introductionTitile.setTextColor(Color.BLACK);
                    categoryTitile.setTextColor(Color.BLACK);
                    commentTitile.setTextColor(Color.rgb(71, 187, 150));
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
}
