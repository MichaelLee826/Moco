package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jishuli.Moco.Adapter.Adapter_ClassDetailItem;
import com.jishuli.Moco.Adapter.Adapter_PopupSpinner;
import com.jishuli.Moco.AppClass.ClassDetailItem;
import com.jishuli.Moco.BusinessLogicLayer.dao.CityDao;
import com.jishuli.Moco.BusinessLogicLayer.dao.CountyDao;
import com.jishuli.Moco.BusinessLogicLayer.dao.CourseDao;
import com.jishuli.Moco.BusinessLogicLayer.dao.SubjectDao;
import com.jishuli.Moco.BusinessLogicLayer.manager.DatabaseManager;
import com.jishuli.Moco.PersistenceLayer.City;
import com.jishuli.Moco.PersistenceLayer.County;
import com.jishuli.Moco.PersistenceLayer.Course;
import com.jishuli.Moco.PersistenceLayer.Subject;
import com.jishuli.Moco.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Activity_ClassDetail extends Activity {
    static private String PATH = "http://120.25.166.18/course";

    //PATH后面的参数
    private String cityId;
    private double latitude;
    private double longtitude;
    private String typeId;
    private String subjectId = "12";    //默认值
    private String districtId = "02";   //默认值

    private TextView detailNameTextView;
    private TextView detailNumberTextView;
    private TextView spinnerTextView1;
    private TextView spinnerTextView2;
    private TextView spinnerTextView3;
    private TextView spinnerTextView4;

    private ImageButton backButton;
    private ImageButton locationButton;

    private PopupWindow popupWindow;
    private Adapter_PopupSpinner adapter;

    //4个下拉菜单
    private String[] spinner1String;
    private String[] spinner2String;
    private String[] spinner3String = {"离我最近", "评价最好"};
    private String[] spinner4String = {"正在进行", "即将开课", "已结束"};

    //ListView的控件
    private List<ClassDetailItem> classDetailItems = new ArrayList<>();
    private ListAdapter listAdapter;
    private ListView classDetailListView = null;

    private String className;           //标题的课程分类名称
    private int classNumber = 0;        //标题的课程数量
    private String[] status;
    private String[] typeID;
    private String[] courseID;
    private int[] studentIn;
    public String[] teacherName;
    private String[] institutionName;
    private String[] courseName;
    private int[] score;
    private String[] beginTime;
    private String[] endTime;
    private double[] lat;
    private double[] lng;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0: {
                    setListViewAdapter();       //5.设置ListView的适配器
                    break;
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classdetail);

        findViews();                //1.找到各种控件
        getData();                  //2.获得数据
        setListeners();             //3.设置监听器
        setListViewListener();      //4.设置ListView的监听器
        //setListViewAdapter();       //5.设置ListView的适配器

        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("点击了");
                Activity_ClassDetail.this.finish();

                /*方法一
                try{
                    Runtime runtime=Runtime.getRuntime();
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                }catch(IOException e){
                    Log.e("Exception when doBack", e.toString());
                }
                */

                //方法二
                //onBack();

            }
        });
    }

    public void onBack(){
        new Thread(){
            public void run() {
                try{
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                    System.out.println("线程");
                }
                catch (Exception e) {
                    Log.e("Exception when onBack", e.toString());
                }
            }
        }.start();

    }

    //1.找到各种控件
    public void findViews(){
        detailNameTextView = (TextView)findViewById(R.id.detailNameTextView);
        detailNumberTextView = (TextView)findViewById(R.id.detailNumberTextView);
        spinnerTextView1 = (TextView)findViewById(R.id.spinnerTextView1);
        spinnerTextView2 = (TextView)findViewById(R.id.spinnerTextView2);
        spinnerTextView3 = (TextView)findViewById(R.id.spinnerTextView3);
        spinnerTextView4 = (TextView)findViewById(R.id.spinnerTextView4);
        backButton = (ImageButton)findViewById(R.id.detailBackButton);
        locationButton = (ImageButton)findViewById(R.id.detailLocationButton);
        classDetailListView = (ListView)findViewById(R.id.detailListViewLayout);
    }

    //2.获得数据
    public void getData(){
        //从前一个Activity传来的数据
        Bundle bundle = this.getIntent().getExtras();
        String cityString = bundle.getString("city");       //城市名
        latitude = bundle.getDouble("lat");                 //纬度
        longtitude = bundle.getDouble("lng");               //经度
        int id = bundle.getInt("id");                       //课程分类的序号

        //获得相应的课程分类名称
        String subjectString;
        switch (id){
            case 1: className = subjectString = "学前教育";
                    break;
            case 2: className = subjectString = "小学教育";
                break;
            case 3: className = subjectString = "初中教育";
                break;
            case 4: className = subjectString = "高中教育";
                break;
            case 5: className = subjectString = "考研培训";
                break;
            case 6: className = subjectString = "职业培训";
                break;
            case 7: className = subjectString = "运动健康";
                break;
            case 8: className = subjectString = "其它课程";
                break;
            default: className = subjectString = "学前教育";
                break;
        }

        if (cityString == null){
            cityString = "南京市";
        }

        //本市的各个区
        CityDao cityDao = DatabaseManager.getmInstance(Activity_ClassDetail.this).getCityDao();
        City city = cityDao.queryBuilder().where(CityDao.Properties.Name.eq(cityString)).unique();
        cityId = String.valueOf(city.getIdentifier());          //获得城市编码
        cityId = cityId.substring(0, 4);                        //只取省市四位编码
        List<County> countyList = city.getCounties();           //市下各区的列表
        spinner1String = new String[countyList.size()];
        for (int i = 0; i < countyList.size(); i++){
            spinner1String[i] = countyList.get(i).getName();
        }

        //课程分类的下拉菜单
        SubjectDao subjectDao = DatabaseManager.getmInstance(Activity_ClassDetail.this).getSubjectDao();
        final Subject subject = subjectDao.queryBuilder().where(SubjectDao.Properties.Name.eq(subjectString)).unique();
        typeId = String.valueOf(subject.getIdentifier());       //获得课程分类编码
        typeId = typeId.substring(0, 2);                        //只取一级分类两位编码
        List<Course> courseList = subject.getCourses();         //课程分类下的二级分类列表
        spinner2String = new String[courseList.size()];
        for (int i = 0; i < courseList.size(); i++){
            spinner2String[i] = courseList.get(i).getName();
        }

        String path = PATH + "?typeID=" + typeId + "&city=" + cityId + "&subjectID="
                + subjectId + "&districtID=" + districtId;
        downloadData(path);     //2-2.下载数据
    }

    //3.设置监听器
    public void setListeners(){
        Listener listener = new Listener();

        spinnerTextView1.setOnClickListener(listener);
        spinnerTextView2.setOnClickListener(listener);
        spinnerTextView3.setOnClickListener(listener);
        spinnerTextView4.setOnClickListener(listener);
    }

    public class Listener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            setSpinnerTextView(v.getId());
        }
    }

    //4.设置ListView的监听器
    public void setListViewListener(){
        classDetailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(Activity_ClassDetail.this, Activity_CourseDetail.class);   //跳转到课程详情列表

                Bundle bundle = new Bundle();
                bundle.putInt("id", position);                                              //点击了第几个课程分类

                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }

    //5.设置ListView的适配器
    public void setListViewAdapter(){
        detailNameTextView.setText(className);                        //标题的课程分类名称
        detailNumberTextView.setText(classNumber + "节课程");          //标题的课程数量

        classDetailItems.clear();                                     //清空数组，防止数据叠加
        for (int i = 0; i < classNumber; i++){
            classDetailItems.add(new ClassDetailItem(status[i], typeID[i], courseID[i], studentIn[i], teacherName[i],
                    institutionName[i], courseName[i], score[i], beginTime[i], endTime[i], lat[i], lng[i]));
        }
        listAdapter = new Adapter_ClassDetailItem(this, classDetailItems);
        classDetailListView.setAdapter(listAdapter);
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

    //2-2.下载数据
    public void downloadData(String pathString){
        final String path = pathString;

        //从服务器上下载数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
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

                        classNumber = jsonObject.getInt("count");

                        JSONArray listArray = jsonObject.getJSONArray("detail");
                        int length = listArray.length();
                        status = new String[length];
                        typeID = new String[length];
                        courseID = new String[length];
                        studentIn = new int[length];
                        teacherName = new String[length];
                        institutionName = new String[length];
                        courseName = new String[length];
                        score = new int[length];
                        beginTime = new String[length];
                        endTime = new String[length];
                        lat = new double[length];
                        lng = new double[length];

                        for (int i = 0; i < length; i++){
                            JSONObject temp = (JSONObject)listArray.get(i);
                            status[i] = temp.getString("status");
                            typeID[i] = temp.getString("typeID");
                            courseID[i] = temp.getString("courseID");
                            studentIn[i] = temp.getInt("studentIn");
                            courseName[i] = temp.getString("courseName");
                            score[i] = temp.getInt("score");
                            beginTime[i] = temp.getString("beginTime");
                            endTime[i] = temp.getString("endTime");

                            JSONObject jsonObject1 = temp.getJSONObject("location");
                            lat[i] = jsonObject1.getDouble("lat");
                            lng[i] = jsonObject1.getDouble("lng");

                            JSONObject jsonObject2 = temp.getJSONObject("teacher");
                            teacherName[i] = jsonObject2.getString("name");

                            JSONObject jsonObject3 = temp.getJSONObject("institution");
                            institutionName[i] = jsonObject3.getString("name");
                        }
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

    //3-1.设置四个下拉菜单
    public void setSpinnerTextView(int id){
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_window, null);
        ListView listView = (ListView)view.findViewById(R.id.popup_window_listView);

        if (id == spinnerTextView1.getId()){
            adapter = new Adapter_PopupSpinner(this, spinner1String);
        }
        if (id == spinnerTextView2.getId()){
            adapter = new Adapter_PopupSpinner(this, spinner2String);
        }
        if (id == spinnerTextView3.getId()){
            adapter = new Adapter_PopupSpinner(this, spinner3String);
        }
        if (id == spinnerTextView4.getId()){
            adapter = new Adapter_PopupSpinner(this, spinner4String);
        }

        listView.setAdapter(adapter);

        popupWindow = new PopupWindow(findViewById(R.id.layout_classdetail));
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);

        //listView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        //设置PopupWindow的宽和高
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        //不设置背景的话下面的代码不管用
        popupWindow.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bt_myaccount));
        popupWindow.setOutsideTouchable(true);

        popupWindow.showAsDropDown(spinnerTextView1, 0, 0);

        //下拉菜单每一项的监听器
        switch (id){
            case R.id.spinnerTextView1:{
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String districtString = spinner1String[position];
                        spinnerTextView1.setText(districtString);

                        //通过查表获得区的编码
                        CountyDao countyDao = DatabaseManager.getmInstance(Activity_ClassDetail.this).getCountyDao();
                        //此处是处理区名字中有重名的问题
                        String countyId = null;
                        List<County> counties = countyDao.queryBuilder().where(CountyDao.Properties.Name.eq(districtString)).list();
                        for (County c : counties) {
                            String identifier = String.valueOf(c.getIdentifier());
                            //2中cityId已经截取为前四位
                            if (identifier.substring(0, 4).equals(cityId)){
                                countyId = identifier;                          //获得区编码
                                break;
                            }
                        }
                        districtId = countyId.substring(4, 6);                  //只取区的最后两位编码

                        popupWindow.dismiss();
                        popupWindow = null;

                        String path = PATH + "?typeID=" + typeId + "&city=" + cityId + "&subjectID="
                                + subjectId + "&districtID=" + districtId;
                        downloadData(path);     //2-2.下载数据
                    }
                });
                break;
            }

            case R.id.spinnerTextView2:{
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String courseString = spinner2String[position];
                        spinnerTextView2.setText(courseString);

                        //通过查表获得二级分类的编码
                        CourseDao courseDao = DatabaseManager.getmInstance(Activity_ClassDetail.this).getCourseDao();
                        //此处是处理二级分类中有重名的问题
                        String courseId = null;
                        List<Course> courses = courseDao.queryBuilder().where(CourseDao.Properties.Name.eq(courseString)).list();
                        for (Course c : courses) {
                            String identifier = String.valueOf(c.getIdentifier());
                            //2中typeId已经截取为前两位
                            if (identifier.substring(0, 2).equals(typeId)){
                                courseId = identifier;                  //获得二级分类编码
                                break;
                            }
                        }
                        subjectId = courseId.substring(2, 4);            //只取二级分类的最后两位编码

                        popupWindow.dismiss();
                        popupWindow = null;

                        String path = PATH + "?typeID=" + typeId + "&city=" + cityId + "&subjectID="
                                + subjectId + "&districtID=" + districtId;
                        downloadData(path);     //2-2.下载数据
                    }
                });
                break;
            }

            case R.id.spinnerTextView3:{
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        spinnerTextView3.setText(spinner3String[position]);
                        popupWindow.dismiss();
                        popupWindow = null;
                    }
                });
                break;
            }

            case R.id.spinnerTextView4:{
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        spinnerTextView4.setText(spinner4String[position]);
                        popupWindow.dismiss();
                        popupWindow = null;
                    }
                });
                break;
            }
        }
    }

    //按下返回键，返回到首页
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Activity_ClassDetail.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}