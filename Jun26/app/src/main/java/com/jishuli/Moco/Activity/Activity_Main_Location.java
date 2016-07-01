package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jishuli.Moco.Adapter.Adapter_LocationCities;
import com.jishuli.Moco.BusinessLogicLayer.dao.CityDao;
import com.jishuli.Moco.BusinessLogicLayer.manager.DatabaseManager;
import com.jishuli.Moco.ExitApplication;
import com.jishuli.Moco.PersistenceLayer.City;
import com.jishuli.Moco.R;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Activity_Main_Location extends Activity {
    private View relativeLayout;
    private ImageButton backButton;
    private EditText searchEditText;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //添加到activityList列表中
        ExitApplication.getInstance().addActivity(this);

        setContentView(R.layout.activity_main_location);

        init();                 //1.初始化控件
        setListeners();         //2.设置监听器
        setListView("");        //3.设置城市列表
    }

    //1.初始化控件
    public void init(){
        relativeLayout = findViewById(R.id.Main_LocationLayout);
        backButton = (ImageButton)findViewById(R.id.Main_LocationBackgroundLayoutBackButton) ;
        searchEditText = (EditText)findViewById(R.id.Main_LocationSearchEditText);
        listView = (ListView) findViewById(R.id.Main_LocationListView);
    }

    //2.设置监听器
    public void setListeners(){
        //返回按钮的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_Main_Location.this.finish();
            }
        });

        //EditText输入文字时，按下换行键，则隐藏软键盘
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                    return true;
                }
                else {
                    return false;
                }
            }
        });

        //如果在搜索框中输入了文字
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setListView(s.toString());                                      //3.设置城市列表
            }
        });

        //判断虚拟键盘是显示还是关闭，从而设置搜索框中文字的位置
        relativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int delta = relativeLayout.getRootView().getHeight() - relativeLayout.getHeight();

                //可认为键盘显示
                if (delta > 100){
                    searchEditText.setGravity(Gravity.LEFT);            //居左显示
                }

                //键盘关闭且搜索框中无内容
                else if (delta < 100 && searchEditText.getText().toString().isEmpty()){
                    searchEditText.setGravity(Gravity.CENTER);          //居中显示
                    searchEditText.clearFocus();                        //取消光标
                }
            }
        });
    }

    //3.设置城市列表
    public void setListView(String keyWord){
        //获得城市列表
        final CityDao cityDao = DatabaseManager.getmInstance(Activity_Main_Location.this).getCityDao();
        List<City> cityList;

        //如果没有输入城市名称
        if (keyWord.isEmpty()){
            //显示所有城市名称
            cityList = cityDao.queryBuilder().list();
        }

        //如果输入了城市名称
        else {
            //显示出所有包含关键字的城市名称
            cityList = cityDao.queryBuilder().where(CityDao.Properties.Name.like("%" + keyWord + "%")).list();
        }

        //按照首字母拼音排序
        int size = cityList.size();
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            strings.add(cityList.get(i).getName());

            //特别地，如果首个字是多音字，则在排序前做一个替换
            if (strings.get(i).equals("沈阳市")){
                strings.set(i, "深阳市");
            }
            if (strings.get(i).equals("重庆市")){
                strings.set(i, "崇庆市");
            }
            if (strings.get(i).equals("长春市")){
                strings.set(i, "厂春市");
            }
            if (strings.get(i).equals("长沙市")){
                strings.set(i, "厂沙市");
            }
            if (strings.get(i).equals("长治市")){
                strings.set(i, "厂治市");
            }
        }
        Comparator comparator = Collator.getInstance(Locale.CHINA);
        Collections.sort(strings, comparator);

        for (int i = 0; i < size; i++) {
            if (strings.get(i).equals("崇庆市")){
                strings.set(i, "重庆市");
            }
            if (strings.get(i).equals("深阳市")){
                strings.set(i, "沈阳市");
            }
            if (strings.get(i).equals("厂春市")){
                strings.set(i, "长春市");
            }
            if (strings.get(i).equals("厂沙市")){
                strings.set(i, "长沙市");
            }
            if (strings.get(i).equals("厂治市")){
                strings.set(i, "长治市");
            }
        }

        cityList.clear();
        for (String s : strings) {
            City city = cityDao.queryBuilder().where(CityDao.Properties.Name.eq(s)).unique();
            cityList.add(city);
        }

        //将排好序的列表转换为字符串组
        final String[] cityStrings = new String[size];
        for (int i = 0; i < cityList.size(); i++) {
            cityStrings[i] = cityList.get(i).getName();
            //System.out.println(cityStrings[i]);
        }

        //如果没有匹配的城市，则提示
        if (cityStrings.length == 0){
            Toast.makeText(Activity_Main_Location.this, "无匹配结果，请重新输入", Toast.LENGTH_SHORT).show();
        }

        //设置ListView的适配器
        Adapter_LocationCities adapter_locationCities = new Adapter_LocationCities(Activity_Main_Location.this, cityStrings);
        listView.setAdapter(adapter_locationCities);

        //选择好某个城市后，将结果返回
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = cityStrings[position];
                Intent intent = new Intent();
                intent.putExtra("cityName", name);
                setResult(2, intent);
                finish();
            }
        });
    }
}