package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.jishuli.Moco.BusinessLogicLayer.dao.CityDao;
import com.jishuli.Moco.BusinessLogicLayer.dao.CountyDao;
import com.jishuli.Moco.BusinessLogicLayer.dao.ProvinceDao;
import com.jishuli.Moco.BusinessLogicLayer.manager.DatabaseManager;
import com.jishuli.Moco.ExitApplication;
import com.jishuli.Moco.PersistenceLayer.City;
import com.jishuli.Moco.PersistenceLayer.County;
import com.jishuli.Moco.PersistenceLayer.Province;
import com.jishuli.Moco.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Activity_PublishTwo extends Activity {
    //从第一页传来的数据
    private String locationProvince;
    private String locationCity;
    private String locationDistrict;
    private String className;
    private String classCode;
    private String provinceCode;
    private String cityCode;
    private String districtCode;
    private Bitmap pictureBitmap;

    //第二页的数据
    private String price;
    private String beginYear;
    private String beginMonth;
    private String beginDay;
    private String endYear;
    private String endMonth;
    private String endDay;
    private String morningWeekday = "";
    private String afternoonWeekday = "";
    private String morningBeginTime;
    private String morningEndTime;
    private String afternoonBeginTime;
    private String afternoonEndTime;
    private String locationDetail;
    private String enrollNumber;

    private ImageButton backButton;

    //价格
    private boolean isFree = true;
    private RadioGroup radioGroup;
    private RadioButton freeRadioButton;
    private RadioButton chargeRadioButton;
    private EditText priceEditText;

    //开始和结束年月日、省市区下拉菜单
    private Spinner beginYearSpinner, beginMonthSpinner, beginDaySpinner;
    private Spinner endYearSpinner, endMonthSpinner, endDaySpinner;
    private Spinner provinceSpinner, citySpinner, districtSpinner;

    //上午时段的开课时间多选框
    private CheckBox MMondayCheckBox, MTuesdayCheckBox, MWednesdayCheckBox, MThursdayCheckBox, MFridayCheckBox, MSaturdayCheckBox, MSundayCheckBox;
    private List<CheckBox> beginCheckBoxs = new ArrayList<CheckBox>();

    //下午时段的开课时间多选框
    private CheckBox AMondayCheckBox, ATuesdayCheckBox, AWednesdayCheckBox, AThursdayCheckBox, AFridayCheckBox, ASaturdayCheckBox, ASundayCheckBox;
    private List<CheckBox> endCheckBoxs = new ArrayList<CheckBox>();

    //日历对象
    private Calendar calendar = Calendar.getInstance(Locale.CHINA);

    private EditText morningBeginTimeEditText;
    private EditText morningEndTimeEditText;

    private EditText afternoonBeginTimeEditText;
    private EditText afternoonEndTimeEditText;

    private EditText locationEditText;
    private EditText enrollNumberEditText;

    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //添加到activityList列表中
        ExitApplication.getInstance().addActivity(this);

        setContentView(R.layout.activity_publishtwo);

        findViews();        //1.找到各种控件
        getData();          //2.从第一页传来的数据
        setListeners();     //3.设置监听器
        setSpinners();      //4.设置各个下拉菜单
    }

    //1.找到各种控件
    public void findViews(){
        backButton = (ImageButton)findViewById(R.id.PublishTwoBackgroundLayoutBackButton);
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
        districtSpinner = (Spinner)findViewById(R.id.PublishTwoDistrictSpinner);
        locationEditText = (EditText)findViewById(R.id.PublishTwoLocationEditText);

        enrollNumberEditText = (EditText)findViewById(R.id.PublishTwoEnrollNumber);

        MMondayCheckBox = (CheckBox)findViewById(R.id.PublishTwoMorningCheckBoxMonday);
        MTuesdayCheckBox = (CheckBox)findViewById(R.id.PublishTwoMorningCheckBoxTuesday);
        MWednesdayCheckBox = (CheckBox)findViewById(R.id.PublishTwoMorningCheckBoxWednesday);
        MThursdayCheckBox = (CheckBox)findViewById(R.id.PublishTwoMorningCheckBoxThursday);
        MFridayCheckBox = (CheckBox)findViewById(R.id.PublishTwoMorningCheckBoxFriday);
        MSaturdayCheckBox = (CheckBox)findViewById(R.id.PublishTwoMorningCheckBoxSaturday);
        MSundayCheckBox = (CheckBox)findViewById(R.id.PublishTwoMorningCheckBoxSunday);

        morningBeginTimeEditText = (EditText)findViewById(R.id.PublishTwoMorningClockBeginTimeEditText);
        morningEndTimeEditText = (EditText)findViewById(R.id.PublishTwoMorningClockEndTimeEditText);

        AMondayCheckBox = (CheckBox)findViewById(R.id.PublishTwoAfternoonCheckBoxMonday);
        ATuesdayCheckBox = (CheckBox)findViewById(R.id.PublishTwoAfternoonCheckBoxTuesday);
        AWednesdayCheckBox = (CheckBox)findViewById(R.id.PublishTwoAfternoonCheckBoxWednesday);
        AThursdayCheckBox = (CheckBox)findViewById(R.id.PublishTwoAfternoonCheckBoxThursday);
        AFridayCheckBox = (CheckBox)findViewById(R.id.PublishTwoAfternoonCheckBoxFriday);
        ASaturdayCheckBox = (CheckBox)findViewById(R.id.PublishTwoAfternoonCheckBoxSaturday);
        ASundayCheckBox = (CheckBox)findViewById(R.id.PublishTwoAfternoonCheckBoxSunday);

        afternoonBeginTimeEditText = (EditText)findViewById(R.id.PublishTwoAfternoonClockBeginTimeEditText);
        afternoonEndTimeEditText = (EditText)findViewById(R.id.PublishTwoAfternoonClockEndTimeEditText);

        beginCheckBoxs.add(MMondayCheckBox);
        beginCheckBoxs.add(MTuesdayCheckBox);
        beginCheckBoxs.add(MWednesdayCheckBox);
        beginCheckBoxs.add(MThursdayCheckBox);
        beginCheckBoxs.add(MFridayCheckBox);
        beginCheckBoxs.add(MSaturdayCheckBox);
        beginCheckBoxs.add(MSundayCheckBox);

        endCheckBoxs.add(AMondayCheckBox);
        endCheckBoxs.add(ATuesdayCheckBox);
        endCheckBoxs.add(AWednesdayCheckBox);
        endCheckBoxs.add(AThursdayCheckBox);
        endCheckBoxs.add(AFridayCheckBox);
        endCheckBoxs.add(ASaturdayCheckBox);
        endCheckBoxs.add(ASundayCheckBox);
    }

    //2.从上一个Activity传来的数据
    public void getData(){
        Bundle bundle = this.getIntent().getExtras();
        locationProvince = bundle.getString("province");        //目前定位的省
        locationCity = bundle.getString("city");                //目前定位的市
        locationDistrict = bundle.getString("district");        //目前定位的区
        className = bundle.getString("classname");              //课程名称
        classCode = bundle.getString("classcode");              //课程分类编码

        Intent intent = getIntent();
        pictureBitmap = intent.getParcelableExtra("bitmap");    //课程图片
    }

    //3.设置监听器
    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_PublishTwo.this.finish();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioListener());         //3-1.是否收费
        nextButton.setOnClickListener(new NextListener());                  //3-2.下一步按钮的监听器

        //3-3.选择时间
        morningBeginTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(Activity_PublishTwo.this, timeSetListener1, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });

        morningEndTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(Activity_PublishTwo.this, timeSetListener2, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });

        afternoonBeginTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(Activity_PublishTwo.this, timeSetListener3, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });

        afternoonEndTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(Activity_PublishTwo.this, timeSetListener4, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });
    }

    //4.设置各个下拉菜单
    public void setSpinners(){
        //获取今天日期
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = sDateFormat.format(new java.util.Date());
        String currentYear = date.substring(0, 4);

        //年份
        final String[] years = new String[10];
        int y = Integer.valueOf(currentYear);
        for (int i = 0; i < 10; i++){
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

    //3-2.下一步按钮的监听器
    public class NextListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            morningWeekday = "";
            afternoonWeekday = "";

            Intent intent = new Intent();
            intent.setClass(Activity_PublishTwo.this, Activity_PublishThree.class);

            Bundle bundle = new Bundle();
            bundle.putString("provinceCode", provinceCode);
            bundle.putString("cityCode", cityCode);
            bundle.putString("districtCode", districtCode);
            bundle.putString("classname", className);
            bundle.putString("classcode", classCode);
            intent.putExtra("bitmap", pictureBitmap);
            //以上都是从第一页传来的数据
            //以下是第二页的数据

            //免费
            if (isFree == true){
                price = "0";
            }
            //收费
            else {
                if (priceEditText.getText().length() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PublishTwo.this);
                    builder.setMessage("请填写价格");
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
                    price = priceEditText.getText().toString();
                }
            }
            bundle.putString("price", price);

            bundle.putString("beginYear", beginYear);                     //开始和结束的年月日
            bundle.putString("beginMonth", beginMonth);
            bundle.putString("beginDay", beginDay);
            bundle.putString("endYear", endYear);
            bundle.putString("endMonth", endMonth);
            bundle.putString("endDay", endDay);

            //开始和结束周几
            for (CheckBox c : beginCheckBoxs) {
                if (c.isChecked()){
                    morningWeekday = morningWeekday + c.getText().toString();
                }
                else {
                    morningWeekday = morningWeekday + "";
                }
            }

            for (CheckBox c : endCheckBoxs) {
                if (c.isChecked()){
                    afternoonWeekday = afternoonWeekday + c.getText().toString();
                }
                else {
                    afternoonWeekday = afternoonWeekday + "";
                }
            }

            if (morningWeekday.isEmpty() | afternoonWeekday.isEmpty()){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PublishTwo.this);
                builder.setMessage("请选择时间");
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
                bundle.putString("morningWeekday", morningWeekday);
                bundle.putString("afternoonWeekday", afternoonWeekday);
            }

            //上午和下午的开始和结束时间
            morningBeginTime = morningBeginTimeEditText.getText().toString();
            morningEndTime = morningEndTimeEditText.getText().toString();
            afternoonBeginTime = afternoonBeginTimeEditText.getText().toString();
            afternoonEndTime = afternoonEndTimeEditText.getText().toString();
            if (morningBeginTime.length() == 0 | morningEndTime.length() == 0l | afternoonBeginTime.length() == 0 | afternoonEndTime.length() == 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PublishTwo.this);
                builder.setMessage("请填写时间");
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
                bundle.putString("morningBeginTime", morningBeginTime);
                bundle.putString("morningEndTime", morningEndTime);
                bundle.putString("afternoonBeginTime", afternoonBeginTime);
                bundle.putString("afternoonEndTime", afternoonEndTime);
            }

            //详细地址
            if (locationEditText.getText().length() == 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PublishTwo.this);
                builder.setMessage("请填写详细地址");
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
                locationDetail = locationEditText.getText().toString();
            }
            bundle.putString("locationDetail", locationDetail);

            //开课人数
            if (enrollNumberEditText.getText().length() == 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PublishTwo.this);
                builder.setMessage("请填写开课人数");
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
                enrollNumber = enrollNumberEditText.getText().toString();
            }
            bundle.putString("enrollNumber", enrollNumber);

            //跳转到下一页
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    //3-3.选择时间
    TimePickerDialog.OnTimeSetListener timeSetListener1 = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            DateFormat df = new java.text.SimpleDateFormat("HH:mm");
            morningBeginTimeEditText.setText(df.format(calendar.getTime()));
        }
    };

    TimePickerDialog.OnTimeSetListener timeSetListener2 = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            DateFormat df = new java.text.SimpleDateFormat("HH:mm");
            morningEndTimeEditText.setText(df.format(calendar.getTime()));
        }
    };

    TimePickerDialog.OnTimeSetListener timeSetListener3 = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            DateFormat df = new java.text.SimpleDateFormat("HH:mm");
            afternoonBeginTimeEditText.setText(df.format(calendar.getTime()));
        }
    };

    TimePickerDialog.OnTimeSetListener timeSetListener4 = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            DateFormat df = new java.text.SimpleDateFormat("HH:mm");
            afternoonEndTimeEditText.setText(df.format(calendar.getTime()));
        }
    };

    //4-1.设置月份下拉菜单
    public void setMonthSpinner(String flag, String tempYear){
        final String localYear = tempYear;

        //月份
        final String[] months = new String[12];
        for (int i = 0; i < 12; i++){
            //月份小于10，前面加0
            if (i < 9){
                months[i] = "0" + (i + 1);
            }
            else {
                months[i] = String.valueOf(i + 1);
            }
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
                    setDaySpinner("begin", localYear, beginMonth);                   //4-2.设置开始日期下拉菜单
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
                    setDaySpinner("end", localYear, endMonth);                   //4-2.设置结束日期下拉菜单
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
            //日期小于10，前面加0
            if (i < 9){
                days28[i] = "0" + (i + 1);
            }
            else {
                days28[i] = String.valueOf(i + 1);
            }
        }

        String[] days29 =new String[29];
        for (int i = 0; i < 29; i++){
            if (i < 9){
                days29[i] = "0" + (i + 1);
            }
            else {
                days29[i] = String.valueOf(i + 1);
            }
        }

        String[] days30 =new String[30];
        for (int i = 0; i < 30; i++){
            if (i < 9){
                days30[i] = "0" + (i + 1);
            }
            else {
                days30[i] = String.valueOf(i + 1);
            }
        }

        String[] days31 =new String[31];
        for (int i = 0; i < 31; i++){
            if (i < 9){
                days31[i] = "0" + (i + 1);
            }
            else {
                days31[i] = String.valueOf(i + 1);
            }
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
        districtSpinner.setAdapter(countyAdapter);
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        districtSpinner.setSelection(countyNum, true);
    }
}
