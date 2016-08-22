package com.taiclouds.Moke.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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

import com.taiclouds.Moke.BusinessLogicLayer.dao.ProvinceDao;
import com.taiclouds.Moke.BusinessLogicLayer.manager.DatabaseManager;
import com.taiclouds.Moke.PersistenceLayer.City;
import com.taiclouds.Moke.PersistenceLayer.Province;
import com.taiclouds.Moke.R;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class Activity_EditProfile extends Activity {
    private ImageButton backButton;

    private ImageView avatarImageView, avatarArrowImageView;
    private Bitmap pictureBitmap = null;
    private String picturePath = null;

    private TextView usernameTextView, nameTextView, genderTextView, birthdayTextView, cityTextView, phoneTextView, emailTextView;
    private ImageView usernameArrowImageView, nameArrowImageView, genderArrowImageView, birthdayArrowImageView, cityArrowImageView, phoneArrowImageView, emailArrowImageView, passwordArrowImageView;
    private String username, name, gender, birthday, province, city, phone, email;

    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        init();                         //1.初始化
        getData();                      //2.获得数据
        setListeners();                 //3.设置监听器
    }

    //1.初始化
    public void init(){
        backButton = (ImageButton)findViewById(R.id.EditProfile_BackgroundLayout_BackButton);
        avatarImageView = (ImageView)findViewById(R.id.EditProfile_Avatar_ImageView);
        avatarArrowImageView = (ImageView)findViewById(R.id.EditProfile_Avatar_Arrow);
        usernameTextView = (TextView)findViewById(R.id.EditProfile_Username_TextView);
        usernameArrowImageView = (ImageView)findViewById(R.id.EditProfile_Username_Arrow);
        nameTextView = (TextView)findViewById(R.id.EditProfile_Name_TextView);
        nameArrowImageView = (ImageView)findViewById(R.id.EditProfile_Name_Arrow);
        genderTextView = (TextView)findViewById(R.id.EditProfile_Gender_TextView);
        genderArrowImageView = (ImageView)findViewById(R.id.EditProfile_Gender_Arrow);
        birthdayTextView = (TextView)findViewById(R.id.EditProfile_Birthday_TextView);
        birthdayArrowImageView = (ImageView)findViewById(R.id.EditProfile_Birthday_Arrow);
        cityTextView = (TextView)findViewById(R.id.EditProfile_City_TextView);
        cityArrowImageView = (ImageView)findViewById(R.id.EditProfile_City_Arrow);
        phoneTextView = (TextView)findViewById(R.id.EditProfile_Phone_TextView);
        phoneArrowImageView = (ImageView)findViewById(R.id.EditProfile_Phone_Arrow);
        emailTextView = (TextView)findViewById(R.id.EditProfile_Email_TextView);
        emailArrowImageView = (ImageView)findViewById(R.id.EditProfile_Email_Arrow);
        passwordArrowImageView = (ImageView)findViewById(R.id.EditProfile_Password_Arrow);
        saveButton = (Button)findViewById(R.id.EditProfile_Save_Button);
    }

    //2.获得数据
    public void getData(){
        SharedPreferences sharedPreferences = getSharedPreferences("geographyInfo", Activity.MODE_PRIVATE);
        province = sharedPreferences.getString("province", null);
        city = sharedPreferences.getString("city", null);
    }

    //3.设置监听器
    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_EditProfile.this.finish();
            }
        });

        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);                          //3-1.选择好照片后
            }
        });

        avatarArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);                          //3-1.选择好照片后
            }
        });

        //设置用户名
        usernameTextView.setOnClickListener(new usernameListener());        //3-4
        usernameArrowImageView.setOnClickListener(new usernameListener());

        //设置真实姓名
        nameTextView.setOnClickListener(new nameListener());                //3-5
        nameArrowImageView.setOnClickListener(new nameListener());

        //设置性别
        genderTextView.setOnClickListener(new genderListener());            //3-6
        genderArrowImageView.setOnClickListener(new genderListener());

        //设置生日
        birthdayTextView.setOnClickListener(new birthdayListener());        //3-7
        birthdayArrowImageView.setOnClickListener(new birthdayListener());

        //设置所在地
        cityTextView.setOnClickListener(new cityListener());                //3-8
        cityArrowImageView.setOnClickListener(new cityListener());

        //设置手机号
        phoneTextView.setOnClickListener(new phoneListener());              //3-9
        phoneArrowImageView.setOnClickListener(new phoneListener());

        //设置邮箱
        emailTextView.setOnClickListener(new emailListener());              //3-10
        emailArrowImageView.setOnClickListener(new emailListener());

        passwordArrowImageView.setOnClickListener(new passwordListener());  //3-11
    }

    //3-1.选择好照片后
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                Matrix matrix = new Matrix();
                matrix.postScale(0.4f, 0.3f);            //长和宽缩小
                Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                avatarImageView.setImageBitmap(resizeBmp);
                pictureBitmap = resizeBmp;

            }
            catch (FileNotFoundException e){
            }

            try {
                saveFile(pictureBitmap, picName);                       //3-2.把bitmap转换为file，并保存在SD卡中

            } catch (IOException e) {
                e.printStackTrace();
            }
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

    //3-4.用户名
    public class usernameListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //在对话框里添加一个EditText
            LayoutInflater layoutInflater = LayoutInflater.from(Activity_EditProfile.this);
            final View usernameView = layoutInflater.inflate(R.layout.dialog_username, null);

            //找到EditText控件
            final EditText usernameEditText = (EditText)usernameView.findViewById(R.id.Dialog_Username_EditText);

            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_EditProfile.this);
            builder.setTitle("请输入用户名");
            builder.setView(usernameView);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    username = usernameEditText.getText().toString();
                    if (username.length() != 0){
                        usernameTextView.setText(username);
                        usernameTextView.setTextColor(Color.rgb(71, 187, 150));
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
                    imm.showSoftInput(usernameEditText, InputMethodManager.SHOW_IMPLICIT);
                }
            });

            alertDialog.show();
        }
    }

    //3-5.真实姓名
    public class nameListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //在对话框里添加一个EditText
            LayoutInflater layoutInflater = LayoutInflater.from(Activity_EditProfile.this);
            final View nameView = layoutInflater.inflate(R.layout.dialog_username, null);

            //找到EditText控件
            final EditText nameEditText = (EditText) nameView.findViewById(R.id.Dialog_Username_EditText);

            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_EditProfile.this);
            builder.setTitle("请输入真实姓名");
            builder.setView(nameView);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    name = nameEditText.getText().toString();
                    if (name.length() != 0){
                        nameTextView.setText(name);
                        nameTextView.setTextColor(Color.rgb(71, 187, 150));
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
                    imm.showSoftInput(nameEditText, InputMethodManager.SHOW_IMPLICIT);
                }
            });

            alertDialog.show();
        }
    }

    //3-6.性别
    public class genderListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_EditProfile.this);
            builder.setTitle("请选择性别");
            builder.setPositiveButton("男", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    gender = "男";
                    genderTextView.setText(gender);
                    genderTextView.setTextColor(Color.rgb(71, 187, 150));
                }
            });
            builder.setNegativeButton("女", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    gender = "女";
                    genderTextView.setText(gender);
                    genderTextView.setTextColor(Color.rgb(71, 187, 150));
                }
            });
            builder.create().show();
        }
    }

    //3-7.生日
    public class birthdayListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(Activity_EditProfile.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    monthOfYear = monthOfYear + 1;
                    birthday = year + "-" + monthOfYear + "-" + dayOfMonth;
                    birthdayTextView.setText(birthday);
                    birthdayTextView.setTextColor(Color.rgb(71, 187, 150));
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }
    }

    //3-8.设置城市
    public class cityListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //在对话框里添加两个Spinner
            LayoutInflater layoutInflater = LayoutInflater.from(Activity_EditProfile.this);
            final View view = layoutInflater.inflate(R.layout.dialog_classification, null);

            //找到两个Spinner
            final Spinner provinceSpinner = (Spinner)view.findViewById(R.id.Dialog_Province_Spinner);
            final Spinner citySpinner = (Spinner)view.findViewById(R.id.Dialog_City_Spinner);

            //省份
            final ProvinceDao provinceDao = DatabaseManager.getmInstance(Activity_EditProfile.this).getProvinceDao();
            List<Province> provinceList = provinceDao.queryBuilder().list();
            final String[] provinces = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++){
                provinces[i] = provinceList.get(i).getName().toString();        //获得省份列表
            }

            ArrayAdapter<String> provinceAdapter = new ArrayAdapter<String>(Activity_EditProfile.this, android.R.layout.simple_spinner_dropdown_item, provinces);
            provinceSpinner.setAdapter(provinceAdapter);

            //默认显示定位的省份
            int provinceNum = 0;
            for (int i = 0; i < provinceList.size(); i++){
                if (provinceList.get(i).getName().toString().equals(province)){
                    provinceNum = i;
                    break;
                }
            }
            provinceSpinner.setSelection(provinceNum, true);

            provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedProvince = provinces[position];
                    province = selectedProvince;
                    final Province tempProvince = provinceDao.queryBuilder().where(ProvinceDao.Properties.Name.eq(selectedProvince)).unique();

                    List<City> cityList = tempProvince.getCities();
                    final String[] cities = new String[cityList.size()];
                    for (int i = 0; i < cityList.size(); i++){
                        cities[i] = cityList.get(i).getName().toString();       //获得城市列表
                    }

                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(Activity_EditProfile.this, android.R.layout.simple_spinner_dropdown_item, cities);
                    citySpinner.setAdapter(cityAdapter);
                    citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedCity = cities[position];
                            city = selectedCity;
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

            //初始时设置城市列表
            Province tempProvince = provinceDao.queryBuilder().where(ProvinceDao.Properties.Name.eq(province)).unique();
            List<City> cityList = tempProvince.getCities();
            final String[] cities = new String[cityList.size()];
            for (int i = 0; i < cityList.size(); i++){
                cities[i] = cityList.get(i).getName().toString();       //获得城市列表
            }

            ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(Activity_EditProfile.this, android.R.layout.simple_spinner_dropdown_item, cities);
            citySpinner.setAdapter(cityAdapter);

            //默认显示定位的城市
            int cityNum = 0;
            for (int i = 0; i < cityList.size(); i ++){
                if (cityList.get(i).getName().toString().equals(city)){
                    cityNum = i;
                }
            }
            citySpinner.setSelection(cityNum, true);

            citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCity = cities[position];
                    city = selectedCity;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_EditProfile.this);
            builder.setTitle("请选择城市");
            builder.setView(view);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //直辖市
                    if (province.equals(city)){
                        cityTextView.setText(city);
                        cityTextView.setTextColor(Color.rgb(71, 187, 150));
                    }
                    else {
                        cityTextView.setText(province + "，" + city);
                        cityTextView.setTextColor(Color.rgb(71, 187, 150));
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

    //3-9.设置手机号
    public class phoneListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //在对话框里添加一个EditText
            LayoutInflater layoutInflater = LayoutInflater.from(Activity_EditProfile.this);
            final View phoneView = layoutInflater.inflate(R.layout.dialog_phone, null);

            //找到EditText控件
            final EditText phoneEditText = (EditText)phoneView.findViewById(R.id.Dialog_Phone_EditText);

            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_EditProfile.this);
            builder.setTitle("请输入手机号");
            builder.setView(phoneView);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    phone = phoneEditText.getText().toString();

                    if (!phone.startsWith("1") || phone.length() != 11){
                        Toast.makeText(Activity_EditProfile.this, "请输入正确格式的手机号", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (phone.length() != 0 && phone.startsWith("1")){
                        phoneTextView.setText(phone);
                        phoneTextView.setTextColor(Color.rgb(71, 187, 150));
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
                    imm.showSoftInput(phoneEditText, InputMethodManager.SHOW_IMPLICIT);
                }
            });

            alertDialog.show();
        }
    }

    //3-10.邮箱
    public class emailListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //在对话框里添加一个EditText
            LayoutInflater layoutInflater = LayoutInflater.from(Activity_EditProfile.this);
            final View emailView = layoutInflater.inflate(R.layout.dialog_email, null);

            //找到EditText控件
            final EditText emailEditText = (EditText)emailView.findViewById(R.id.Dialog_Email_EditText);

            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_EditProfile.this);
            builder.setTitle("请输入电子邮箱地址");
            builder.setView(emailView);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    email = emailEditText.getText().toString();

                    if (!email.contains("@") || !email.contains(".")){
                        Toast.makeText(Activity_EditProfile.this, "请输入正确格式的电子邮箱地址", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    emailTextView.setText(email);
                    emailTextView.setTextColor(Color.rgb(71, 187, 150));
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
                    imm.showSoftInput(emailEditText, InputMethodManager.SHOW_IMPLICIT);
                }
            });

            alertDialog.show();
        }
    }

    //3-11.密码
    public class passwordListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(Activity_EditProfile.this, Activity_ChangePassword.class);
            startActivity(intent);
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