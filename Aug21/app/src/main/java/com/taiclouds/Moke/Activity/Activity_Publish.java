package com.taiclouds.Moke.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.taiclouds.Moke.BusinessLogicLayer.dao.CityDao;
import com.taiclouds.Moke.BusinessLogicLayer.dao.CourseDao;
import com.taiclouds.Moke.BusinessLogicLayer.dao.ProvinceDao;
import com.taiclouds.Moke.BusinessLogicLayer.dao.SubjectDao;
import com.taiclouds.Moke.BusinessLogicLayer.manager.DatabaseManager;
import com.taiclouds.Moke.ExitApplication;
import com.taiclouds.Moke.PersistenceLayer.City;
import com.taiclouds.Moke.PersistenceLayer.Course;
import com.taiclouds.Moke.PersistenceLayer.Province;
import com.taiclouds.Moke.PersistenceLayer.Subject;
import com.taiclouds.Moke.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class Activity_Publish extends Activity {
    private static String PATH = "http://120.25.166.18/courseNew";

    private static final int PICK_PICTURE = 1;
    private static final int COURSE_INTRO = 2;
    private static final int TEACHER_LIST = 3;
    private static final int COURSE_TIME = 4;
    private static final int UNKNOWN_REASON = 100;

    private ImageButton backButton;

    private String locationProvince;
    private String locationCity;
    private String locationDistrict;
    private String locationLat;
    private String locationLng;

    private TextView courseNameTextView, courseClassificationTextView, coursePictureTextView, courseIntroTextView, teacherNameTextView, coursePriceTextView, courseNumberTextView, courseDateTextView, courseTimeTextView, courseLocationTextView;
    private ImageView courseNameArrowImageView, courseClassificationArrowImageView, coursePictureArrowImageView, courseIntroArrowImageView, teacherNameArrowImageView, coursePriceArrowImageView, courseNumberArrowImageView, courseDateArrowImageView, courseTimeArrowImageView, courseLocationArrowImageView;
    private String courseName, subjectCode, courseCode, coursePicture, courseIntro, teacherName, coursePrice, courseNumber, courseDate, courseTime, provinceCode, cityCode, courseLocation;
    private int beginYear, beginMonth, beginDay, endYear, endMonth, endDay;
    private String firstClassification = "学前教育", secondClassification = "孕产育儿";

    private Bitmap pictureBitmap = null;
    private String picturePath = null;

    private String tempAddress = null;

    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //添加到activityList列表中
        ExitApplication.getInstance().addActivity(this);

        //检查用户名和密码
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", null);
        String password = sharedPreferences.getString("password", null);

        if (userName == null | password == null){
            Intent intent = new Intent();
            intent.setClass(Activity_Publish.this, Activity_SignIn.class);
            startActivity(intent);
        }
        else {
            setContentView(R.layout.activity_publish);

            Bundle bundle = this.getIntent().getExtras();
            locationProvince = bundle.getString("province");
            locationCity = bundle.getString("city");
            locationDistrict = bundle.getString("district");
            locationLat = bundle.getString("lat");
            locationLng = bundle.getString("lng");

            init();                 //1.初始化

            setListeners();         //3.设置监听器
        }

    }

    //1.初始化
    public void init(){
        backButton = (ImageButton)findViewById(R.id.Publish_BackgroundLayout_BackButton);
        courseNameTextView = (TextView)findViewById(R.id.Publish_CourseName_TextView);
        courseNameArrowImageView = (ImageView)findViewById(R.id.Publish_CourseName_Arrow);
        courseClassificationTextView = (TextView)findViewById(R.id.Publish_CourseClassification_TextView);
        courseClassificationArrowImageView = (ImageView)findViewById(R.id.Publish_CourseClassification_Arrow);
        coursePictureTextView = (TextView)findViewById(R.id.Publish_CoursePicture_TextView);
        coursePictureArrowImageView = (ImageView)findViewById(R.id.Publish_CoursePicture_Arrow);
        coursePriceTextView = (TextView)findViewById(R.id.Publish_CoursePrice_TextView);
        coursePriceArrowImageView = (ImageView)findViewById(R.id.Publish_CoursePrice_Arrow);
        courseTimeTextView = (TextView)findViewById(R.id.Publish_CourseTime_TextView);
        courseTimeArrowImageView = (ImageView)findViewById(R.id.Publish_CourseTime_Arrow);
        courseDateTextView = (TextView)findViewById(R.id.Publish_CourseDate_TextView);
        courseDateArrowImageView = (ImageView)findViewById(R.id.Publish_CourseDate_Arrow);
        courseNumberTextView = (TextView)findViewById(R.id.Publish_CourseNumber_TextView);
        courseNumberArrowImageView = (ImageView)findViewById(R.id.Publish_CourseNumber_Arrow);
        teacherNameTextView = (TextView)findViewById(R.id.Publish_TeacherName_TextView);
        teacherNameArrowImageView = (ImageView)findViewById(R.id.Publish_TeacherName_Arrow);
        courseIntroTextView = (TextView)findViewById(R.id.Publish_CourseIntro_TextView);
        courseIntroArrowImageView = (ImageView)findViewById(R.id.Publish_CourseIntro_Arrow);
        courseLocationTextView = (TextView)findViewById(R.id.Publish_CourseLocation_TextView);
        courseLocationArrowImageView = (ImageView)findViewById(R.id.Publish_CourseLocation_Arrow);
        saveButton = (Button)findViewById(R.id.Publish_Save_Button);

        courseName = subjectCode = courseIntro =  teacherName =  coursePrice =  courseNumber =  courseDate =  courseTime = "未填写";
        coursePicture = "未上传";

        //清空缓存的数据
        SharedPreferences sharedPreferences1 = getSharedPreferences("courseIntro", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        editor1.clear();
        editor1.apply();

        SharedPreferences sharedPreferences2 = getSharedPreferences("courseTimeList", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        editor2.clear();
        editor2.apply();
    }

    //2.
    public void setData(){

    }

    //3.设置监听器
    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_Publish.this.finish();
            }
        });

        coursePictureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_PICTURE);                                                  //3-1.选择好照片后
            }
        });

        coursePictureArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_PICTURE);                                                  //3-1.选择好照片后
            }
        });

        //3-4.课程名称
        courseNameTextView.setOnClickListener(new courseNameListeners());
        courseNameArrowImageView.setOnClickListener(new courseNameListeners());

        //3-5.课程分类
        courseClassificationTextView.setOnClickListener(new courseClassificationListener());
        courseClassificationArrowImageView.setOnClickListener(new courseClassificationListener());

        //3-6.课程简介
        courseIntroTextView.setOnClickListener(new courseIntroListener());
        courseIntroArrowImageView.setOnClickListener(new courseIntroListener());

        //3-7.教师姓名
        teacherNameTextView.setOnClickListener(new teacherNameListener());
        teacherNameArrowImageView.setOnClickListener(new teacherNameListener());

        //3-8.课程价格
        coursePriceTextView.setOnClickListener(new coursePriceListener());
        coursePriceArrowImageView.setOnClickListener(new coursePriceListener());

        //3-9.开课人数
        courseNumberTextView.setOnClickListener(new courseNumberListener());
        courseNumberArrowImageView.setOnClickListener(new courseNumberListener());

        //3-10.开课日期
        courseDateTextView.setOnClickListener(new courseDateListener());
        courseDateArrowImageView.setOnClickListener(new courseDateListener());

        //3-11.开课时段
        courseTimeTextView.setOnClickListener(new courseTimeListener());
        courseTimeArrowImageView.setOnClickListener(new courseTimeListener());

        //3-12.开课地点
        courseLocationTextView.setOnClickListener(new courseLocationListener());
        courseLocationArrowImageView.setOnClickListener(new courseLocationListener());

        //3-13.提交按钮
        saveButton.setOnClickListener(new saveButtonListener());
    }

    //3-1.从另一个页面获得数据后
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            //选择好照片后
            case PICK_PICTURE:
                if (resultCode == RESULT_OK){
                    Uri uri = data.getData();

                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    cursor.moveToFirst();
                    String picNumber = cursor.getString(0);     // 图片编号
                    String picPath = cursor.getString(1);       // 图片文件路径
                    String picSize = cursor.getString(2);       // 图片大小
                    String picName = cursor.getString(3);       // 图片文件名
                    cursor.close();

                    ContentResolver contentResolver = this.getContentResolver();
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri));

                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();

                        float newWidth = 800;
                        float newHeight = 600;

                        float scaleWidth = newWidth / width;
                        float scaleHeight = newHeight / height;

                        Matrix matrix = new Matrix();
                        matrix.postScale(scaleWidth, scaleHeight);

                        pictureBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

                        coursePicture = "已上传";
                        coursePictureTextView.setText(coursePicture);
                        coursePictureTextView.setTextColor(Color.rgb(71, 187, 150));

                        //Matrix matrix = new Matrix();
                        //matrix.postScale(0.4f, 0.3f);            //长和宽缩小
                        //Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                        //pictureBitmap = resizeBmp;

                    }
                    catch (FileNotFoundException e){
                    }

                    try {
                        saveFile(pictureBitmap, picName);                       //3-2.把bitmap转换为file，并保存在SD卡中

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case COURSE_INTRO:
                if (data != null){
                    courseIntro = data.getStringExtra("result");

                    if (courseIntro.length() != 0){
                        courseIntro = "已填写";
                        courseIntroTextView.setText(courseIntro);
                        courseIntroTextView.setTextColor(Color.rgb(71, 187, 150));
                    }
                    else {
                        courseIntro = "未填写";
                        courseIntroTextView.setText(courseIntro);
                        courseIntroTextView.setTextColor(Color.GRAY);
                    }
                }
                break;

            case TEACHER_LIST:
                if (data != null){
                    teacherName = data.getStringExtra("result");

                    if (teacherName.length() != 0){
                        teacherNameTextView.setText(teacherName);
                        teacherNameTextView.setTextColor(Color.rgb(71, 187, 150));
                    }
                    else {
                        teacherName = "未填写";
                        teacherNameTextView.setText(teacherName);
                        teacherNameTextView.setTextColor(Color.GRAY);
                    }
                }
                break;

            case COURSE_TIME:
                if (data != null){
                    String result = data.getStringExtra("result");
                    if (result.equals("OK")){
                        courseTime = "已填写";
                        courseTimeTextView.setText(courseTime);
                        courseTimeTextView.setTextColor(Color.rgb(71, 187, 150));

                        courseTime = data.getStringExtra("courseTime");
                        System.out.println(courseTime);
                        try {
                            JSONObject jsonObj = new JSONObject(courseTime);
                            String[] times = new String[jsonObj.length()];
                            for (int i = 0; i < jsonObj.length() / 2; i++){
                                times[i] = "每" + jsonObj.get("weekDay" + i).toString() + "  " + jsonObj.get("time" + i).toString();
                                System.out.println(times[i]);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        courseTime = "未填写";
                        courseTimeTextView.setText(courseTime);
                        courseTimeTextView.setTextColor(Color.GRAY);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //3-2.把bitmap转换为file，并保存在SD卡中
    public void saveFile(Bitmap bm, String fileName) throws IOException {
        String path = getSDPath() + "/Moco/";                           //3-3.获得根目录
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File file = new File(path + fileName);
        picturePath = path + fileName;

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }

    //3-3.获得根目录
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
        }
        return sdDir.toString();
    }

    //3-4.课程名称
    public class courseNameListeners implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //在对话框里添加一个EditText
            LayoutInflater layoutInflater = LayoutInflater.from(Activity_Publish.this);
            final View courseNameView = layoutInflater.inflate(R.layout.dialog_username, null);

            //找到EditText控件
            final EditText courseNameEditText = (EditText) courseNameView.findViewById(R.id.Dialog_Username_EditText);

            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Publish.this);
            builder.setTitle("请输入课程名称");
            builder.setView(courseNameView);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    courseName = courseNameEditText.getText().toString();
                    if (courseName.length() != 0){
                        courseNameTextView.setText(courseName);
                        courseNameTextView.setTextColor(Color.rgb(71, 187, 150));
                    }
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            //自动弹出键盘
            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(courseNameEditText, InputMethodManager.SHOW_IMPLICIT);
                }
            });

            alertDialog.show();
        }
    }

    //3-5.课程分类
    public class courseClassificationListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //在对话框里添加两个Spinner
            LayoutInflater layoutInflater = LayoutInflater.from(Activity_Publish.this);
            final View view = layoutInflater.inflate(R.layout.dialog_classification, null);

            //找到两个Spinner
            final Spinner subjectSpinner = (Spinner)view.findViewById(R.id.Dialog_FirstClassification_Spinner);
            final Spinner courseSpinner = (Spinner)view.findViewById(R.id.Dialog_SecondClassification_Spinner);

            //一级分类
            final SubjectDao subjectDao = DatabaseManager.getmInstance(Activity_Publish.this).getSubjectDao();
            List<Subject> subjectList = subjectDao.queryBuilder().list();
            final String[] subjects = new String[subjectList.size()];
            for (int i = 0; i < subjectList.size(); i++){
                subjects[i] = subjectList.get(i).getName().toString();              //获得一级分类列表
            }

            ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>(Activity_Publish.this, android.R.layout.simple_spinner_dropdown_item, subjects);
            subjectSpinner.setAdapter(subjectAdapter);

            //默认显示的一级分类
            int subjectNum = 0;
            for (int i = 0; i < subjectList.size(); i++){
                if (subjectList.get(i).getName().toString().equals(firstClassification)){
                    subjectNum = i;
                    break;
                }
            }
            subjectSpinner.setSelection(subjectNum, true);

            subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedSubject = subjects[position];
                    firstClassification = selectedSubject;
                    Subject tempSubject = subjectDao.queryBuilder().where(SubjectDao.Properties.Name.eq(selectedSubject)).unique();

                    List<Course> courseList = tempSubject.getCourses();
                    final String[] courses = new String[courseList.size()];
                    for (int i = 0; i < courseList.size() - 1; i++){
                        courses[i] = courseList.get(i + 1).getName().toString();                //获得二级分类列表
                    }
                    courses[courseList.size() - 1] = courseList.get(0).getName().toString();    //把"其它"放到最后

                    ArrayAdapter<String> courseAdapter = new ArrayAdapter<String>(Activity_Publish.this, android.R.layout.simple_spinner_dropdown_item, courses);
                    courseSpinner.setAdapter(courseAdapter);
                    courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedCourse = courses[position];
                            secondClassification = selectedCourse;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            //初始时设置二级分类列表
            Subject tempSubject = subjectDao.queryBuilder().where(SubjectDao.Properties.Name.eq(firstClassification)).unique();
            List<Course> courseList = tempSubject.getCourses();
            final String[] courses = new String[courseList.size()];
            for (int i = 0; i < courseList.size() - 1; i++){
                courses[i] = courseList.get(i + 1).getName().toString();       //获得二级列表
            }
            courses[courseList.size() - 1] = courseList.get(0).getName().toString();    //把"其它"放到最后

            ArrayAdapter<String> courseAdapter = new ArrayAdapter<String>(Activity_Publish.this, android.R.layout.simple_spinner_dropdown_item, courses);
            courseSpinner.setAdapter(courseAdapter);

            //默认显示的二级分类
            int courseNum = 0;
            for (int i = 0; i < courseList.size(); i ++){
                if (courses[i].equals(secondClassification)){
                    courseNum = i;
                }
            }
            courseSpinner.setSelection(courseNum, true);

            courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCourse = courses[position];
                    secondClassification = selectedCourse;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Publish.this);
            builder.setTitle("请选择课程分类");
            builder.setView(view);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    courseClassificationTextView.setText(firstClassification + "，" + secondClassification);
                    courseClassificationTextView.setTextColor(Color.rgb(71, 187, 150));

                    Subject subject = subjectDao.queryBuilder().where(SubjectDao.Properties.Name.eq(firstClassification)).unique();
                    subjectCode = String.valueOf(subject.getIdentifier());
                    subjectCode = subjectCode.substring(0, 2);                        //取一级分类编码的前两位

                    //此处是处理二级分类中有重名的问题
                    CourseDao courseDao = DatabaseManager.getmInstance(Activity_Publish.this).getCourseDao();
                    List<Course> courses = courseDao.queryBuilder().where(CourseDao.Properties.Name.eq(secondClassification)).list();
                    for (Course c : courses) {
                        String identifier = String.valueOf(c.getIdentifier());
                        if (identifier.substring(0, 2).equals(subjectCode)){
                            courseCode = identifier.substring(2, 4);                  //获得二级分类编码
                            break;
                        }
                    }
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
        }
    }

    //3-6.课程简介
    public class courseIntroListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(Activity_Publish.this, Activity_CourseIntro.class);

            Bundle bundle = new Bundle();
            bundle.putInt("source", COURSE_INTRO);

            intent.putExtras(bundle);

            startActivityForResult(intent, COURSE_INTRO);
        }
    }

    //3-7.讲师姓名
    public class teacherNameListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(Activity_Publish.this, Activity_TeacherList.class);

            Bundle bundle = new Bundle();
            bundle.putInt("source", TEACHER_LIST);

            intent.putExtras(bundle);

            startActivityForResult(intent, TEACHER_LIST);
        }
    }

    //3-8.课程价格
    public class coursePriceListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //在对话框里添加一个EditText
            LayoutInflater layoutInflater = LayoutInflater.from(Activity_Publish.this);
            final View coursePriceView = layoutInflater.inflate(R.layout.dialog_price, null);

            //找到EditText控件
            final EditText coursePriceEditText = (EditText) coursePriceView.findViewById(R.id.Dialog_Price_EditText);

            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Publish.this);
            builder.setTitle("请输入课程价格");
            builder.setView(coursePriceView);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    coursePrice = coursePriceEditText.getText().toString();
                    if (coursePrice.length() != 0) {
                        coursePriceTextView.setText(coursePrice + "元");
                        coursePriceTextView.setTextColor(Color.rgb(71, 187, 150));
                    }
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            //自动弹出键盘
            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(coursePriceEditText, InputMethodManager.SHOW_IMPLICIT);
                }
            });

            alertDialog.show();
        }
    }

    //3-9.课程人数
    public class courseNumberListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //在对话框里添加一个EditText
            LayoutInflater layoutInflater = LayoutInflater.from(Activity_Publish.this);
            final View courseNumberView = layoutInflater.inflate(R.layout.dialog_price, null);

            //找到EditText控件
            final EditText courseNumberEditText = (EditText) courseNumberView.findViewById(R.id.Dialog_Price_EditText);

            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Publish.this);
            builder.setTitle("请输入课程人数");
            builder.setView(courseNumberView);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    courseNumber = courseNumberEditText.getText().toString();
                    if (courseNumber.length() != 0) {
                        courseNumberTextView.setText(courseNumber + "人");
                        courseNumberTextView.setTextColor(Color.rgb(71, 187, 150));
                    }
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            //自动弹出键盘
            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(courseNumberEditText, InputMethodManager.SHOW_IMPLICIT);
                }
            });

            alertDialog.show();
        }
    }

    //3-10.开课日期
    public class courseDateListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            View view = View.inflate(getApplicationContext(), R.layout.dialog_datepicker, null);

            final DatePicker beginDatePicker = (DatePicker)view.findViewById(R.id.Dialog_DatePicker_BeginDate);
            final DatePicker endDatePicker = (DatePicker)view.findViewById(R.id.Dialog_DatePicker_EndDate);

            //默认设置为当前时间
            final Calendar calendar = Calendar.getInstance();
            beginYear = endYear = calendar.get(Calendar.YEAR);
            beginMonth = endMonth = calendar.get(Calendar.MONTH);
            beginDay = endDay = calendar.get(Calendar.DAY_OF_MONTH);

            beginDatePicker.init(beginYear, beginMonth, beginDay, null);
            endDatePicker.init(endYear, endMonth, endDay, null);

            //显示对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Publish.this);
            builder.setView(view);
            builder.setTitle("请选择开始和结束日期");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    beginYear = beginDatePicker.getYear();
                    beginMonth = beginDatePicker.getMonth() + 1;
                    beginDay = beginDatePicker.getDayOfMonth();
                    String beginDate = beginYear + "-" + beginMonth + "-" + beginDay;

                    endYear = endDatePicker.getYear();
                    endMonth = endDatePicker.getMonth() + 1;
                    endDay = endDatePicker.getDayOfMonth();
                    String endDate = endYear + "-" + endMonth + "-" + endDay;

                    courseDate = beginDate + " 至 " + endDate;

                    courseDateTextView.setText(courseDate);
                    courseDateTextView.setTextColor(Color.rgb(71, 187, 150));
                }
            });
            builder.show();
        }
    }

    //3-11.开课时段
    public class courseTimeListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(Activity_Publish.this, Activity_CourseTime.class);

            Bundle bundle = new Bundle();
            bundle.putInt("source", COURSE_TIME);

            intent.putExtras(bundle);

            startActivityForResult(intent, COURSE_TIME);
        }
    }

    //3-12.开课地点
    public class courseLocationListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //在对话框里添加两个Spinner和一个EditText
            LayoutInflater layoutInflater = LayoutInflater.from(Activity_Publish.this);
            final View view = layoutInflater.inflate(R.layout.dialog_location, null);

            //找到两个Spinner和一个EditText
            final Spinner provinceSpinner = (Spinner)view.findViewById(R.id.Dialog_Province_Spinner);
            final Spinner citySpinner = (Spinner)view.findViewById(R.id.Dialog_City_Spinner);
            final EditText addressEditText = (EditText)view.findViewById(R.id.Dialog_Location_Address_EditText);

            if (tempAddress != null){
                addressEditText.setText(tempAddress);
            }

            //省份
            final ProvinceDao provinceDao = DatabaseManager.getmInstance(Activity_Publish.this).getProvinceDao();
            List<Province> provinceList = provinceDao.queryBuilder().list();
            final String[] provinces = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++){
                provinces[i] = provinceList.get(i).getName().toString();              //获得一级分类列表
            }

            ArrayAdapter<String> provinceAdapter = new ArrayAdapter<String>(Activity_Publish.this, android.R.layout.simple_spinner_dropdown_item, provinces);
            provinceSpinner.setAdapter(provinceAdapter);

            //默认显示的省份
            int provinceNum = 0;
            for (int i = 0; i < provinceList.size(); i++){
                if (provinceList.get(i).getName().toString().equals(locationProvince)){
                    provinceNum = i;
                    break;
                }
            }
            provinceSpinner.setSelection(provinceNum, true);

            provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedProvince = provinces[position];
                    locationProvince = selectedProvince;
                    Province tempProvince = provinceDao.queryBuilder().where(ProvinceDao.Properties.Name.eq(selectedProvince)).unique();

                    List<City> cityList = tempProvince.getCities();
                    final String[] cities = new String[cityList.size()];
                    for (int i = 0; i < cityList.size(); i++){
                        cities[i] = cityList.get(i).getName().toString();                //获得二级分类列表
                    }

                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(Activity_Publish.this, android.R.layout.simple_spinner_dropdown_item, cities);
                    citySpinner.setAdapter(cityAdapter);

                    //默认显示的城市
                    int courseNum = 0;
                    for (int i = 0; i < cityList.size(); i ++){
                        if (cities[i].equals(locationCity)){
                            courseNum = i;
                        }
                    }
                    citySpinner.setSelection(courseNum, true);

                    citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedCity = cities[position];
                            locationCity = selectedCity;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            //初始时设置城市
            Province tempProvince = provinceDao.queryBuilder().where(ProvinceDao.Properties.Name.eq(locationProvince)).unique();
            List<City> cityList = tempProvince.getCities();
            final String[] cities = new String[cityList.size()];
            for (int i = 0; i < cityList.size(); i++){
                cities[i] = cityList.get(i).getName().toString();       //获得二级列表
            }

            ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(Activity_Publish.this, android.R.layout.simple_spinner_dropdown_item, cities);
            citySpinner.setAdapter(cityAdapter);

            //默认显示的城市
            int courseNum = 0;
            for (int i = 0; i < cityList.size(); i ++){
                if (cities[i].equals(locationCity)){
                    courseNum = i;
                }
            }
            citySpinner.setSelection(courseNum, true);

            citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCity = cities[position];
                    locationCity = selectedCity;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Publish.this);
            builder.setTitle("请填写开课地址");
            builder.setView(view);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    courseLocationTextView.setText("已填写");
                    courseLocationTextView.setTextColor(Color.rgb(71, 187, 150));

                    Province province = provinceDao.queryBuilder().where(ProvinceDao.Properties.Name.eq(locationProvince)).unique();
                    provinceCode = String.valueOf(province.getIdentifier());
                    provinceCode = provinceCode.substring(0, 2);                        //取省份编码的前两位

                    //此处是处理城市中有重名的问题
                    CityDao cityDao = DatabaseManager.getmInstance(Activity_Publish.this).getCityDao();
                    List<City> cities = cityDao.queryBuilder().where(CityDao.Properties.Name.eq(locationCity)).list();
                    for (City c : cities) {
                        String identifier = String.valueOf(c.getIdentifier());
                        if (identifier.substring(0, 2).equals(subjectCode)){
                            cityCode = identifier.substring(2, 4);                      //获得城市分类编码
                            break;
                        }
                    }

                    tempAddress = addressEditText.getText().toString();
                    courseLocation = locationProvince + locationCity + tempAddress;
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
        }
    }

    //3-13.提交按钮
    public class saveButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            System.out.println(courseName);
            System.out.println(subjectCode);
            System.out.println(coursePicture);
            System.out.println(courseIntro);
            System.out.println(teacherName);
            System.out.println(coursePrice);
            System.out.println(courseNumber);
            System.out.println(courseDate);
            System.out.println(courseTime);
            System.out.println(courseLocation);

            if (courseName.equals("未填写") | subjectCode.equals("未填写") | coursePicture.equals("未上传") | courseIntro.equals("未填写") | teacherName.equals("未填写") | coursePrice.equals("未填写") | courseNumber.equals("未填写") | courseDate.equals("未填写") | courseTime.equals("未填写") | courseLocation.equals("未填写")){
                Toast.makeText(Activity_Publish.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(Activity_Publish.this, "上传成功", Toast.LENGTH_SHORT).show();
            }
        }
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