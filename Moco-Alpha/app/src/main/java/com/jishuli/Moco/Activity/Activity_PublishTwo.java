package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.jishuli.Moco.BusinessLogicLayer.dao.CityDao;
import com.jishuli.Moco.BusinessLogicLayer.dao.CountyDao;
import com.jishuli.Moco.BusinessLogicLayer.dao.ProvinceDao;
import com.jishuli.Moco.BusinessLogicLayer.manager.DatabaseManager;
import com.jishuli.Moco.ExitApplication;
import com.jishuli.Moco.PersistenceLayer.City;
import com.jishuli.Moco.PersistenceLayer.County;
import com.jishuli.Moco.PersistenceLayer.Province;
import com.jishuli.Moco.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class Activity_PublishTwo extends Activity {
    private String locationProvince;
    private String locationCity;
    private String locationDistrict;
    private String className;
    private String classCode;
    private String provinceCode;
    private String cityCode;
    private String districtCode;

    private ImageButton backButton;

    private boolean isFree = true;
    private RadioGroup radioGroup;
    private RadioButton freeRadioButton;
    private RadioButton chargeRadioButton;
    private EditText priceEditText;

    private Spinner beginYearSpinner;
    private Spinner beginMonthSpinner;
    private Spinner beginDaySpinner;
    private Spinner endYearSpinner;
    private Spinner endMonthSpinner;
    private Spinner endDaySpinner;
    private Spinner provinceSpinner;
    private Spinner citySpinner;
    private Spinner disrictSpinner;

    private String beginYear;
    private String beginMonth;
    private String beginDay;
    private String endYear;
    private String endMonth;
    private String endDay;

    private EditText locationEditText;
    private EditText agentNameEditText;
    private EditText teacherNameEditText;
    private EditText teacherIntroEditText;

    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //添加到activityList列表中
        ExitApplication.getInstance().addActivity(this);

        setContentView(R.layout.activity_publishtwo);

        findViews();        //1.找到各种控件
        //getData();
        setListeners();     //3.设置监听器
        setSpinners();      //4.设置各个下拉菜单

        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_PublishTwo.this.finish();
            }
        });
    }

    //1.找到各种控件
    public void findViews(){
        nextButton = (Button)findViewById(R.id.PublishTwoNextButton);
        radioGroup = (RadioGroup)findViewById(R.id.PublishTwoRadioGroup);
        freeRadioButton = (RadioButton)findViewById(R.id.PublishTwoFreeRadioButton);
        chargeRadioButton = (RadioButton)findViewById(R.id.PublishTwoChargeRadioButton);
        priceEditText = (EditText)findViewById(R.id.PublishTwoChargeEditText);
        beginYearSpinner = (Spinner)findViewById(R.id.PublishTwoBeginTimeYear);
        beginMonthSpinner = (Spinner)findViewById(R.id.PublishTwoBeginTimeMonth);
        beginDaySpinner = (Spinner)findViewById(R.id.PublishTwoBeginTimeDay);
        endYearSpinner = (Spinner)findViewById(R.id.PublishTwoEndTimeYear);
        endMonthSpinner = (Spinner)findViewById(R.id.PublishTwoEndTimeMonth);
        endDaySpinner = (Spinner)findViewById(R.id.PublishTwoEndTimeDay);
        provinceSpinner = (Spinner)findViewById(R.id.PublishTwoProvinceSpinner);
        citySpinner = (Spinner)findViewById(R.id.PublishTwoCitySpinner);
        disrictSpinner = (Spinner)findViewById(R.id.PublishTwoDistrictSpinner);
        locationEditText = (EditText)findViewById(R.id.PublishTwoLocationEditText);
        agentNameEditText = (EditText)findViewById(R.id.PublishTwoAgentNameEditText);
        teacherNameEditText = (EditText)findViewById(R.id.PublishTwoTeacherNameEditText);
        teacherIntroEditText = (EditText)findViewById(R.id.PublishTwoTeacherIntroEditText);
        backButton = (ImageButton)findViewById(R.id.PublishTwoBackgroundLayoutBackButton);
    }

    //2.从上一个Activity传来的数据
    public void getData(){
        Bundle bundle = this.getIntent().getExtras();
        locationProvince = bundle.getString("province");       //目前定位的省
        locationCity = bundle.getString("city");               //目前定位的市
        locationDistrict = bundle.getString("district");       //目前定位的区
        className = bundle.getString("classname");           //课程名称
        classCode = bundle.getString("classcode");           //课程分类编码
    }

    //3.设置监听器
    public void setListeners(){
        radioGroup.setOnCheckedChangeListener(new RadioListener());         //3-1.是否收费





        nextButton.setOnClickListener(new NextListener());                  //3-3.下一步按钮的监听器
    }

    //4.设置各个下拉菜单
    public void setSpinners(){
        //获取今天日期
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = sDateFormat.format(new java.util.Date());
        String currentYear = date.substring(0, 4);

        //年份
        final String[] years = new String[50];
        int y = Integer.valueOf(currentYear);
        for (int i = 0; i < 50; i++){
            years[i] = String.valueOf(y);
            y++;
        }

        //共用的适配器
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, years);

        //初始年份
        beginYearSpinner.setAdapter(yearAdapter);
        beginYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                beginYear = years[position];
                setMonthSpinner("begin", beginYear);           //4-1.设置初始月份下拉菜单
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        beginYearSpinner.setSelection(0, true);               //初始设为当前年份，此时会调用OnItemSelectedListener
        beginYear = currentYear;


        //结束年份
        endYearSpinner.setAdapter(yearAdapter);
        endYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endYear = years[position];
                setMonthSpinner("end", endYear);              //4-1.设置结束月份下拉菜单
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        endYearSpinner.setSelection(0, true);                 //结束设为当前年份，此时会调用OnItemSelectedListener
        endYear = currentYear;

        //省份
        final ProvinceDao provinceDao = DatabaseManager.getmInstance(Activity_PublishTwo.this).getProvinceDao();
        List<Province> provinceList = provinceDao.queryBuilder().list();
        final String[] provinces = new String[provinceList.size()];
        for (int i = 0; i < provinceList.size(); i++){
            provinces[i] = provinceList.get(i).getName().toString();        //获得省份列表
        }

        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, provinces);
        provinceSpinner.setAdapter(provinceAdapter);
        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedProvince = provinces[position];
                locationProvince = selectedProvince;
                Province tempProvince = provinceDao.queryBuilder().where(ProvinceDao.Properties.Name.eq(selectedProvince)).unique();
                provinceCode = tempProvince.getIdentifier().toString().substring(0, 2);      //省份的编码
                setCitySpinner(tempProvince);                                                //4-3.设置城市列表下拉菜单
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //默认显示定位的省份
        int provinceNum = 0;
        for (int i = 0; i < provinceList.size(); i++){
            if (provinceList.get(i).getName().toString().equals(locationProvince)){
                provinceNum = i;
                break;
            }
        }
        provinceSpinner.setSelection(provinceNum, true);
    }

    //3-1.是否收费
    public class RadioListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            //免费
            if (checkedId == freeRadioButton.getId()){
                isFree = true;
                priceEditText.setEnabled(false);
            }
            //收费
            if (checkedId == chargeRadioButton.getId()){
                isFree = false;
                priceEditText.setEnabled(true);
            }
        }
    }


    //3-3.下一步按钮的监听器
    public class NextListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(Activity_PublishTwo.this, Activity_PublishThree.class);

            /*Bundle bundle = new Bundle();
            bundle.putString("province", locationProvince);               //目前定位的省
            bundle.putString("city", locationCity);                       //目前定位的市
            bundle.putString("district", locationDistrict);               //目前定位的区
            bundle.putString("classname", className);                     //课程名称
            bundle.putString("classcode", classCode);                     //课程分类编码

            intent.putExtras(bundle);*/

            startActivity(intent);
        }
    }

    //4-1.设置月份下拉菜单
    public void setMonthSpinner(String flag, String tempYear){
        final String localYear = tempYear;

        //月份
        final String[] months = new String[12];
        for (int i = 0; i < 12; i++){
            months[i] = String.valueOf(i + 1);
        }

        //共用的适配器
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, months);

        //初始月份
        if (flag.equals("begin")){
            beginMonthSpinner.setAdapter(monthAdapter);
            beginMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    beginMonth = months[position];
                    setDaySpinner("begin", localYear, beginMonth);                      //4-2.设置开始日期下拉菜单
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            beginMonthSpinner.setSelection(0, true);                                //初始设为1月份
            beginMonth = "1";
        }

        //结束月份
        if (flag.equals("end")){
            endMonthSpinner.setAdapter(monthAdapter);
            endMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    endMonth = months[position];
                    setDaySpinner("end", localYear, endMonth);                      //4-2.设置结束日期下拉菜单
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            endMonthSpinner.setSelection(0, true);                                //结束设为1月份
            endMonth = "1";
        }
    }

    //4-2.设置初始日期下拉菜单
    public void setDaySpinner(String flag, String tempYear, String tempMonth){
        //四种日期
        String[] days28 =new String[28];
        for (int i = 0; i < 28; i++){
            days28[i] = String.valueOf(i + 1);
        }

        String[] days29 =new String[29];
        for (int i = 0; i < 29; i++){
            days29[i] = String.valueOf(i + 1);
        }

        String[] days30 =new String[30];
        for (int i = 0; i < 30; i++){
            days30[i] = String.valueOf(i + 1);
        }

        String[] days31 =new String[31];
        for (int i = 0; i < 31; i++){
            days31[i] = String.valueOf(i + 1);
        }

        final String[] days;
        switch (Integer.valueOf(tempMonth)){
            //2月份需要判断是否闰年
            case 2:{
                int y = Integer.valueOf(tempYear);
                if(y % 4 == 0 && y % 100 != 0 || y % 400 == 0){
                    days = days29;      //是闰年
                }else{
                    days = days28;      //不是闰年
                }
                break;
            }
            //30天的月份
            case 4:case 6:case 9:case 11:{
                days = days30;
                break;
            }
            //31天的月份
            default:{
                days = days31;
                break;
            }
        }

        //共用的适配器
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, days);

        //初始日期
        if (flag.equals("begin")){
            beginDaySpinner.setAdapter(dayAdapter);
            beginDaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    beginDay = days[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            beginDaySpinner.setSelection(0, true);              //初始设为1号
            beginDay = "1";
        }

        //结束日期
        if (flag.equals("end")){
            endDaySpinner.setAdapter(dayAdapter);
            endDaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    endDay = days[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            endDaySpinner.setSelection(0, true);                //初始设为1号
            endDay = "1";
        }
    }

    //4-3.设置城市列表下拉菜单
    public void setCitySpinner(final Province province){
        List<City> cityList = province.getCities();
        final String[] cities = new String[cityList.size()];
        for (int i = 0; i < cityList.size(); i++){
            cities[i] = cityList.get(i).getName().toString();       //获得城市列表
        }

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, cities);
        citySpinner.setAdapter(cityAdapter);
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = cities[position];
                locationCity = selectedCity;
                City tempCity = null;

                //防止重名
                CityDao cityDao = DatabaseManager.getmInstance(Activity_PublishTwo.this).getCityDao();
                List<City> cl = cityDao.queryBuilder().where(CityDao.Properties.Name.eq(selectedCity)).list();
                for (City c : cl) {
                    if (c.getIdentifier().toString().substring(0, 2).equals(province.getIdentifier().toString().substring(0, 2))){
                        tempCity = c;
                        break;
                    }
                }
                cityCode = tempCity.getIdentifier().toString().substring(2, 4);     //城市的编码
                setDistrictSpinner(tempCity);                                       //4-4.设置区下拉菜单
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int cityNum = 0;
        for (int i = 0; i < cityList.size(); i ++){
            if (cityList.get(i).getName().toString().equals(locationCity)){
                cityNum = i;
            }
        }
        citySpinner.setSelection(cityNum, true);
    }

    //4-4.设置区下拉菜单
    public void setDistrictSpinner(final City city){
        List<County> countyList = city.getCounties();
        final String[] counties = new String[countyList.size()];
        for (int i = 0; i < countyList.size(); i++){
            counties[i] = countyList.get(i).getName().toString();           //获得区列表
        }

        ArrayAdapter<String> countyAdapter = new ArrayAdapter<String>(Activity_PublishTwo.this, android.R.layout.simple_spinner_dropdown_item, counties);
        disrictSpinner.setAdapter(countyAdapter);
        disrictSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCounty = counties[position];
                locationDistrict = selectedCounty;
                County tempCounty = null;

                //防止重名
                CountyDao countyDao = DatabaseManager.getmInstance(Activity_PublishTwo.this).getCountyDao();
                List<County> cl = countyDao.queryBuilder().where(CountyDao.Properties.Name.eq(selectedCounty)).list();
                for (County c : cl) {
                    if (c.getIdentifier().toString().substring(0, 4).equals(city.getIdentifier().toString().substring(0, 4))){
                        tempCounty = c;
                        break;
                    }
                }
                districtCode = tempCounty.getIdentifier().toString().substring(4, 6);       //区的编码
                System.out.println(provinceCode+cityCode+districtCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int countyNum = 0;
        for (int i = 0; i < countyList.size(); i ++){
            if (countyList.get(i).getName().toString().equals(locationDistrict)){
                countyNum = i;
            }
        }
        disrictSpinner.setSelection(countyNum, true);
    }
}
