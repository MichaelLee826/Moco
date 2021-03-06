package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.jishuli.Moco.BusinessLogicLayer.dao.SubjectDao;
import com.jishuli.Moco.BusinessLogicLayer.manager.DatabaseManager;
import com.jishuli.Moco.ExpandedPopupWindow.FirstClassAdapter;
import com.jishuli.Moco.ExpandedPopupWindow.FirstClassItem;
import com.jishuli.Moco.ExpandedPopupWindow.SecondClassAdapter;
import com.jishuli.Moco.ExpandedPopupWindow.SecondClassItem;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Activity_Search extends Activity {
    static private String PATH = "http://120.25.166.18/course";

    //PATH后面的参数
    private String cityId;
    private double latitude;
    private double longtitude;
    private String typeId;
    private String subjectId = "10";    //默认值
    private String districtId;

    private TextView spinnerTextView1;
    private TextView spinnerTextView2;
    private TextView spinnerTextView3;
    private TextView spinnerTextView4;

    private View relativeLayout;
    private EditText searchBarEditText;
    private ImageButton searchBarDeleteImageButton;
    private ImageButton backButton;
    private ImageButton locationButton;

    private PopupWindow popupWindow;                 //只有一级的下拉菜单
    private Adapter_PopupSpinner adapter;

    private PopupWindow popupWindow_withSecond;      //有两级的下拉菜单
    private int firstClassPosition = 0;              //一级分类的位置
    private int secondClassPosition = -1;             //一级分类的位置

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
        setContentView(R.layout.activity_search);

        init();                     //1.初始化控件
        getData();                  //2.从前一个Activity传来的数据
        setListeners();             //3.设置监听器
        setListViewListener();      //4.设置ListView的监听器
    }

    //1.初始化控件
    public void init(){
        spinnerTextView1 = (TextView)findViewById(R.id.SearchSpinnerTextView1);
        spinnerTextView2 = (TextView)findViewById(R.id.SearchSpinnerTextView2);
        spinnerTextView3 = (TextView)findViewById(R.id.SearchSpinnerTextView3);
        spinnerTextView4 = (TextView)findViewById(R.id.SearchSpinnerTextView4);

        relativeLayout = findViewById(R.id.Search_Layout);
        searchBarEditText = (EditText)findViewById(R.id.Search_SearchBar_EditText);
        searchBarDeleteImageButton = (ImageButton)findViewById(R.id.Search_SearchBar_Delete_ImageButton);

        backButton = (ImageButton)findViewById(R.id.SearchBackButton);
        locationButton = (ImageButton)findViewById(R.id.SearchLocationButton);
        classDetailListView = (ListView)findViewById(R.id.SearchListViewLayout);
    }

    //2.获得数据
    public void getData(){
        //从前一个Activity传来的数据
        Bundle bundle = this.getIntent().getExtras();
        String cityString = bundle.getString("city");               //城市名
        latitude = bundle.getDouble("lat");                         //纬度
        longtitude = bundle.getDouble("lng");                       //经度

        if (cityString == null){
            cityString = "南京市";
        }

        //本市的各个区，填充第一个下拉菜单
        CityDao cityDao = DatabaseManager.getmInstance(Activity_Search.this).getCityDao();
        City city = cityDao.queryBuilder().where(CityDao.Properties.Name.eq(cityString)).unique();
        cityId = String.valueOf(city.getIdentifier());          //获得城市编码
        cityId = cityId.substring(0, 4);                        //只取省市四位编码
        List<County> countyList = city.getCounties();           //市下各区的列表
        spinner1String = new String[countyList.size()];
        for (int i = 0; i < countyList.size(); i++){
            spinner1String[i] = countyList.get(i).getName();
        }
    }

    //3.设置监听器
    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_Search.this.finish();
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
                    searchBarEditText.setHint("请输入要搜索的课程");
                    searchBarDeleteImageButton.setVisibility(View.GONE);

                    //显示出键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(searchBarEditText, InputMethodManager.SHOW_FORCED);
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
            }
        });

        //按下回车后搜索
        searchBarEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String content = searchBarEditText.getText().toString();
                    if (content.length() == 0){
                        //如果搜索内容为空，什么也不做
                    }
                    else {
                        //将中文转换编码格式
                        String message = "";
                        try {
                            message = java.net.URLEncoder.encode(content,"utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        String path = PATH + "?city=" + cityId + "&courseName=" + message;
                        downloadData(path);                                                  //2-2.下载数据
                    }
                }
                return false;
            }
        });

        //下拉菜单的监听器
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
                intent.setClass(Activity_Search.this, Activity_CourseDetail.class);   //跳转到课程详情列表

                Bundle bundle = new Bundle();
                bundle.putString("courseID", courseID[position]);                     //点击了第几个课程
                bundle.putString("cityID", cityId);

                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }

    //5.设置ListView的适配器
    public void setListViewAdapter(){
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
                        System.out.println("网络有问题" + Activity_Search.this);
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
                        CountyDao countyDao = DatabaseManager.getmInstance(Activity_Search.this).getCountyDao();
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

        SubjectDao subjectDao = DatabaseManager.getmInstance(Activity_Search.this).getSubjectDao();
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
            Activity_Search.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}