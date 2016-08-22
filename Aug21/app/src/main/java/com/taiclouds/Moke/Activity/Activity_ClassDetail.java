package com.taiclouds.Moke.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.taiclouds.Moke.Adapter.Adapter_ClassDetailItem;
import com.taiclouds.Moke.Adapter.Adapter_PopupSpinner;
import com.taiclouds.Moke.AppClass.ClassDetailItem;
import com.taiclouds.Moke.BusinessLogicLayer.dao.CityDao;
import com.taiclouds.Moke.BusinessLogicLayer.dao.CountyDao;
import com.taiclouds.Moke.BusinessLogicLayer.dao.SubjectDao;
import com.taiclouds.Moke.BusinessLogicLayer.manager.DatabaseManager;
import com.taiclouds.Moke.ExpandedPopupWindow.FirstClassAdapter;
import com.taiclouds.Moke.ExpandedPopupWindow.FirstClassItem;
import com.taiclouds.Moke.ExpandedPopupWindow.SecondClassAdapter;
import com.taiclouds.Moke.ExpandedPopupWindow.SecondClassItem;
import com.taiclouds.Moke.PersistenceLayer.City;
import com.taiclouds.Moke.PersistenceLayer.County;
import com.taiclouds.Moke.PersistenceLayer.Course;
import com.taiclouds.Moke.PersistenceLayer.Subject;
import com.taiclouds.Moke.R;
import com.umeng.analytics.MobclickAgent;

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
    private String subjectId = "10";    //默认值
    private String districtId;

    private TextView detailNameTextView;
    private TextView detailNumberTextView;
    private TextView spinnerTextView1;
    private TextView spinnerTextView2;
    private TextView spinnerTextView3;
    private TextView spinnerTextView4;

    private ImageButton backButton;
    private ImageButton locationButton;

    private PopupWindow popupWindow;                 //只有一级的下拉菜单
    private Adapter_PopupSpinner adapter;

    private PopupWindow popupWindow_withSecond;      //有两级的下拉菜单
    private int firstClassPosition = 0;              //一级分类的位置
    private int secondClassPosition = -1;            //一级分类的位置

    //第二项筛选菜单
    private List<FirstClassItem> firstList;
    private List<SecondClassItem> secondList;
    private FirstClassAdapter firstAdapter;
    private SecondClassAdapter secondAdapter;

    //4个下拉菜单
    private String[] spinner1String;
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

    private final static int DONE = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DONE: {
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

        init();                     //1.初始化控件
        getData();                  //2.从服务器获得数据
        setListeners();             //3.设置监听器
        setListViewListener();      //4.设置ListView的监听器
    }

    //1.初始化控件
    public void init(){
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
        String cityString = bundle.getString("city");               //城市名
        latitude = bundle.getDouble("lat");                         //纬度
        longtitude = bundle.getDouble("lng");                       //经度
        int id = bundle.getInt("id");                               //课程分类的序号

        if (cityString == null){
            cityString = "南京市";
        }

        //本市的各个区，填充第一个下拉菜单
        CityDao cityDao = DatabaseManager.getmInstance(Activity_ClassDetail.this).getCityDao();
        City city = cityDao.queryBuilder().where(CityDao.Properties.Name.eq(cityString)).unique();
        cityId = String.valueOf(city.getIdentifier());          //获得城市编码
        cityId = cityId.substring(0, 4);                        //只取省市四位编码
        List<County> countyList = city.getCounties();           //市下各区的列表
        spinner1String = new String[countyList.size()];
        for (int i = 0; i < countyList.size(); i++){
            spinner1String[i] = countyList.get(i).getName();
        }

        String subjectString;
        firstClassPosition = id - 1;                            //第二个筛选框默认的一级分类

        //获得相应的课程分类名称
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

        //获得课程分类编码
        SubjectDao subjectDao = DatabaseManager.getmInstance(Activity_ClassDetail.this).getSubjectDao();
        final Subject subject = subjectDao.queryBuilder().where(SubjectDao.Properties.Name.eq(subjectString)).unique();
        typeId = String.valueOf(subject.getIdentifier());       //获得课程分类编码
        typeId = typeId.substring(0, 2);                        //只取一级分类两位编码

        String path = PATH + "?typeID=" + typeId + "&city=" + cityId;
        downloadData(path);                                     //2-2.下载数据
    }

    //3.设置监听器
    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_ClassDetail.this.finish();
            }
        });

        Listener listener = new Listener();
        Listener_withSecond listener_withSecond = new Listener_withSecond();

        spinnerTextView1.setOnClickListener(listener);
        spinnerTextView2.setOnClickListener(listener_withSecond);
        spinnerTextView3.setOnClickListener(listener);
        spinnerTextView4.setOnClickListener(listener);
    }

    public class Listener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            setSpinnerTextView(v.getId());                  //3-1.设置三个下拉菜单
        }
    }

    public class Listener_withSecond implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            prepData();                                     //3-2.从数据库读出第二个筛选框的数据
            initPopup();                                    //3-3.
            showPopup();                                    //3-4.
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
                bundle.putString("courseID", courseID[position]);                          //点击了第几个课程分类
                bundle.putString("cityID", cityId);

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
                    institutionName[i], courseName[i], score[i], beginTime[i], endTime[i]));
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
                        System.out.println(jsonObject);

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
                            teacherName[i] = temp.getString("teacherName");
                            institutionName[i] = temp.getString("institutionName");
                        }
                        //说明已下载完数据
                        handler.sendEmptyMessage(DONE);
                    }
                    else {
                        int hint = httpURLConnection.getResponseCode();
                        System.out.println(hint);
                        System.out.println("网络有问题" + Activity_ClassDetail.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //3-1.设置三个下拉菜单
    public void setSpinnerTextView(int id){
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_window, null);
        ListView listView = (ListView)view.findViewById(R.id.popup_window_listView);

        if (id == spinnerTextView1.getId()){
            adapter = new Adapter_PopupSpinner(this, spinner1String);
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
        popupWindow.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_spinner));
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
                            //2.中cityId已经截取为前四位
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

            case R.id.spinnerTextView3:{
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String sort = spinner3String[position];
                        spinnerTextView3.setText(sort);

                        if (sort.equals("离我最近")){
                            sort = "distance";
                        }
                        else if (sort.equals("评价最好")){
                            sort = "score";
                        }

                        popupWindow.dismiss();
                        popupWindow = null;

                        String path = PATH + "?typeID=" + typeId + "&city=" + cityId + "&sort=" + sort;
                        downloadData(path);     //2-2.下载数据
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

    //3-2.从数据库读出第二个筛选框的数据
    public void prepData(){
        firstList = new ArrayList<FirstClassItem>();

        SubjectDao subjectDao = DatabaseManager.getmInstance(Activity_ClassDetail.this).getSubjectDao();
        List<Subject> subjectList = subjectDao.queryBuilder().list();

        for (int i = 0; i < subjectList.size(); i++){
            ArrayList<SecondClassItem> secondList = new ArrayList<>();
            List<Course> courseList = subjectList.get(i).getCourses();
            for (int j = 0; j < courseList.size(); j++){
                secondList.add(new SecondClassItem(courseList.get(j).getIdentifier(), courseList.get(j).getName()));
            }

            //把"其它"放到最后
            SecondClassItem temp = secondList.get(0);
            for (int k = 0; k < secondList.size() - 1; k++){
                secondList.set(k, secondList.get(k + 1));
            }
            secondList.set(secondList.size() - 1, temp);

            firstList.add(new FirstClassItem(subjectList.get(i).getIdentifier(), subjectList.get(i).getName(), secondList));
        }
    }

    //3-3.初始化下拉菜单并设置监听器
    private void initPopup() {
        popupWindow_withSecond = new PopupWindow(this);
        View view = LayoutInflater.from(this).inflate(R.layout.popup_layout, null);
        ListView firstListView = (ListView) view.findViewById(R.id.pop_listview_left);
        ListView secondListView = (ListView) view.findViewById(R.id.pop_listview_right);

        popupWindow_withSecond.setContentView(view);
        popupWindow_withSecond.setFocusable(true);

        popupWindow_withSecond.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow_withSecond.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        //不设置背景的话下面的代码不管用
        popupWindow_withSecond.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_spinner));
        popupWindow_withSecond.setOutsideTouchable(true);

        //一级分类
        firstAdapter = new FirstClassAdapter(this, firstList);
        firstAdapter.setSelectedPosition(firstClassPosition);                   //设置为上次的选择
        firstListView.setAdapter(firstAdapter);

        //二级分类
        secondList = new ArrayList<SecondClassItem>();
        secondList.addAll(firstList.get(firstClassPosition).getSecondList());
        secondAdapter = new SecondClassAdapter(this, secondList);
        secondAdapter.setSelectedPosition(secondClassPosition);                 //设置为上次的选择
        secondListView.setAdapter(secondAdapter);

        //左侧ListView点击事件
        firstListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //二级数据
                List<SecondClassItem> list2 = firstList.get(position).getSecondList();

                //如果没有二级类，则直接跳转
                if (list2 == null || list2.size() == 0) {
                    popupWindow_withSecond.dismiss();

                    long firstId = firstList.get(position).getId();
                    String selectedName = firstList.get(position).getName();
                    handleResult(firstId, -1, selectedName);        //3-6.重新获取数据
                    return;
                }

                FirstClassAdapter adapter = (FirstClassAdapter) (parent.getAdapter());
                //如果上次点击的就是这一个item，则不进行任何操作
                if (adapter.getSelectedPosition() == position){
                    return;
                }

                //根据一级分类选中情况，更新背景色
                adapter.setSelectedPosition(position);
                adapter.notifyDataSetChanged();

                updateSecondListView(list2, secondAdapter);         //3-5.刷新右侧二级分类

                firstClassPosition = position;                      //记录当前点击的一级分类序号
            }
        });

        //右侧ListView点击事件
        secondListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int firstPosition = firstAdapter.getSelectedPosition();
                long firstId = firstList.get(firstPosition).getId();
                long secondId = firstList.get(firstPosition).getSecondList().get(position).getId();
                String selectedName = firstList.get(firstPosition).getSecondList().get(position).getName();

                //根据二级分类选中情况，更新背景色
                SecondClassAdapter adapter = (SecondClassAdapter) (parent.getAdapter());
                adapter.setSelectedPosition(position);
                adapter.notifyDataSetChanged();

                handleResult(firstId, secondId, selectedName);          //3-6.重新获取数据

                secondClassPosition = position;                         //记录当前点击的二级分类序号

                //关闭popupWindow，显示用户选择的分类
                popupWindow_withSecond.dismiss();
            }
        });

        popupWindow_withSecond.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    //3-4.显示PopupWindow
    private void showPopup() {
        if (popupWindow_withSecond.isShowing()) {
            popupWindow_withSecond.dismiss();
        } else {
            popupWindow_withSecond.showAsDropDown(spinnerTextView2, 0, 0);
            popupWindow_withSecond.setAnimationStyle(-1);
        }
    }

    //3-5.刷新右侧ListView
    private void updateSecondListView(List<SecondClassItem> list2, SecondClassAdapter secondAdapter) {
        secondList.clear();
        secondList.addAll(list2);
        secondAdapter.setSelectedPosition(-1);
        secondAdapter.notifyDataSetChanged();
    }

    //3-6.重新获取数据
    private void handleResult(long firstId, long secondId, String selectedName){
        typeId = String.valueOf(firstId).substring(0, 2);
        subjectId = String.valueOf(secondId).substring(2, 4);

        String path = PATH + "?typeID=" + typeId + "&city=" + cityId + "&subjectID="
                + subjectId + "&districtID=" + districtId;
        downloadData(path);     //2-2.下载数据

        spinnerTextView2.setText(selectedName);
    }

    //按下返回键，返回到首页
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Activity_ClassDetail.this.finish();
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