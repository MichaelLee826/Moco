package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.jishuli.Moco.BusinessLogicLayer.dao.CourseDao;
import com.jishuli.Moco.BusinessLogicLayer.dao.SubjectDao;
import com.jishuli.Moco.BusinessLogicLayer.manager.DatabaseManager;
import com.jishuli.Moco.ExitApplication;
import com.jishuli.Moco.PersistenceLayer.Course;
import com.jishuli.Moco.PersistenceLayer.Subject;
import com.jishuli.Moco.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Activity_PublishOne extends Activity {
    private static final String RELEASED_PATH = "http://120.25.166.18/releasedInstitution";
    private static final String JOINED_PATH = "http://120.25.166.18/joinedInstitution";

    private EditText nameEditText;
    private Spinner firstCategorySpinner;
    private Spinner secondCategorySpinner;
    private ImageButton imageButton;
    private Button nextButton;
    private ImageButton backButton;

    private String subjectString = "学前教育";
    private String subjectCode = "10";
    private String courseString = "其它";
    private String courseCode = "11";
    private String className = null;
    private Bitmap pictureBitmap = null;
    private String picturePath = null;

    private String locationProvince;
    private String locationCity;
    private String locationDistrict;
    private String locationLat;
    private String locationLng;

    private ArrayAdapter<String> subjectAdapter;
    private ArrayAdapter<String> courseAdapter;

    private OkHttpClient mOkHttpClient = new OkHttpClient();
    private String cookie;
    private ArrayList<String> agencyList = new ArrayList<>();        //机构名称
    private ArrayList<String> agencyIDList = new ArrayList<>();      //机构编号

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
            intent.setClass(Activity_PublishOne.this, Activity_SignIn.class);
            startActivity(intent);
        }
        else {
            setContentView(R.layout.activity_publishone);

            Bundle bundle = this.getIntent().getExtras();
            locationProvince = bundle.getString("province");
            locationCity = bundle.getString("city");
            locationDistrict = bundle.getString("district");
            locationLat = bundle.getString("lat");
            locationLng = bundle.getString("lng");


            init();                 //1.初始化控件
            setListeners();         //2.设置监听器
            getJoinedData();        //3.获取已加入的机构列表
            getReleasedData();      //4.获取创建的机构列表
            setSpinners();          //5.设置下拉菜单
        }
    }

    //1.初始化控件
    public void init(){
        //取出cookie
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        cookie = sharedPreferences.getString("cookie", null);

        agencyList.add(0, "个人");        //机构列表第一项为“个人”
        agencyIDList.add(0, "000000");

        nameEditText = (EditText)findViewById(R.id.PublishOnenameEditText);
        firstCategorySpinner = (Spinner)findViewById(R.id.PublishOneSpinner1);
        secondCategorySpinner = (Spinner)findViewById(R.id.PublishOneSpinner2);
        imageButton = (ImageButton)findViewById(R.id.PublishOneImageButton);
        nextButton = (Button)findViewById(R.id.PublishOneNextButton);
        backButton = (ImageButton)findViewById(R.id.PublishOneBackgroundLayoutBackButton);
    }

    //2.设置监听器
    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_PublishOne.this.finish();
            }
        });

        //点击输入框后提示文字消失
        nameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEditText.setHint(null);
            }
        });

        //2-1.选择照片按钮的监听器
        imageButton.setOnClickListener(new ImageButtonListener());

        //2-3.下一步按钮的监听器
        nextButton.setOnClickListener(new NextListener());
    }

    //3.获取已加入的机构数据
    public void getJoinedData(){
        Request request = new Request.Builder()
                .url(JOINED_PATH)
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

                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray jsonArray = jsonObject.getJSONArray("detail");
                    int length = jsonObject.getInt("length");
                    for (int i = 0; i < length; i++){
                        JSONObject temp = (JSONObject)jsonArray.get(i);
                        String name = temp.getString("name");
                        String ID = temp.getString("institutionID");
                        agencyList.add(name);
                        agencyIDList.add(ID);
                        System.out.println(name+ID);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //4.获取创建的机构数据
    public void getReleasedData(){
        Request request = new Request.Builder()
                .url(RELEASED_PATH)
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

                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray jsonArray = jsonObject.getJSONArray("detail");
                    int length = jsonObject.getInt("length");
                    for (int i = 0; i < length; i++){
                        JSONObject temp = (JSONObject)jsonArray.get(i);
                        String name = temp.getString("name");
                        String ID = temp.getString("institutionID");
                        agencyList.add(name);
                        agencyIDList.add(ID);
                        System.out.println(name+ID);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //5.设置下拉菜单
    public void setSpinners(){
        //一级分类的默认下拉菜单
        final SubjectDao subjectDao = DatabaseManager.getmInstance(Activity_PublishOne.this).getSubjectDao();
        List<Subject> subjectList = subjectDao.queryBuilder().list();
        final String[] strings1 = new String[subjectList.size()];
        for (int i = 0; i < subjectList.size(); i++){
            strings1[i] = subjectList.get(i).getName();
        }
        subjectAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, strings1);
        firstCategorySpinner.setAdapter(subjectAdapter);
        firstCategorySpinner.setSelection(0, true);

        //根据选择的一级分类，设定另一个Spinner
        firstCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subjectString = strings1[position];
                Subject subject = subjectDao.queryBuilder().where(SubjectDao.Properties.Name.eq(subjectString)).unique();
                subjectCode = String.valueOf(subject.getIdentifier());
                subjectCode = subjectCode.substring(0, 2);            //取课程分类编码的前两位

                List<Course> couList = subject.getCourses();
                final String[] tempString = new String[couList.size()];
                for (int i = 0; i < couList.size(); i++){
                    tempString[i] = couList.get(i).getName();
                }
                courseAdapter = new ArrayAdapter<String>(Activity_PublishOne.this, android.R.layout.simple_spinner_dropdown_item, tempString);
                secondCategorySpinner.setAdapter(courseAdapter);
                secondCategorySpinner.setSelection(0, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //二级分类的默认下拉菜单
        //根据一级分类的名称确定二级分类列表
        Subject s = subjectDao.queryBuilder().where(SubjectDao.Properties.Name.eq(subjectString)).unique();
        List<Course> courseList = s.getCourses();
        final String[] strings2 = new String[courseList.size()];
        for (int i = 0; i < courseList.size(); i++){
            strings2[i] = courseList.get(i).getName();
        }
        courseAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, strings2);
        secondCategorySpinner.setAdapter(courseAdapter);
        secondCategorySpinner.setSelection(0, true);

        //选中某个二级分类后
        secondCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //根据一级分类的名称确定二级分类列表
                SubjectDao subjectDao = DatabaseManager.getmInstance(Activity_PublishOne.this).getSubjectDao();
                Subject subject = subjectDao.queryBuilder().where(SubjectDao.Properties.Name.eq(subjectString)).unique();
                List<Course> cl = subject.getCourses();
                final String[] s = new String[cl.size()];
                for (int i = 0; i < cl.size(); i++){
                    s[i] = cl.get(i).getName();
                }
                courseString = s[position];

                //此处是处理二级分类中有重名的问题
                CourseDao courseDao = DatabaseManager.getmInstance(Activity_PublishOne.this).getCourseDao();
                List<Course> courses = courseDao.queryBuilder().where(CourseDao.Properties.Name.eq(courseString)).list();
                for (Course c : courses) {
                    System.out.println(c.getName()+c.getIdentifier());
                    String identifier = String.valueOf(c.getIdentifier());
                    if (identifier.substring(0, 2).equals(subjectCode)){
                        courseCode = identifier.substring(2, 4);                  //获得二级分类编码
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //2-1.选择照片的监听器
    public class ImageButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 1);                  //2-2.选择好照片后
        }
    }

    //2-2.选择好照片后
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

                imageButton.setImageBitmap(resizeBmp);
                pictureBitmap = resizeBmp;

            }
            catch (FileNotFoundException e){
            }

            try {
                saveFile(pictureBitmap, picName);           //2-4.把bitmap转换为file，并保存在SD卡中

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //2-3.下一步按钮的监听器
    public class NextListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
             //判断是否输入了课程名称并上传了图片
             if (nameEditText.getText().length() == 0 & pictureBitmap == null){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PublishOne.this);
                builder.setMessage("请输入课程名称并上传图片");
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

            //判断是否输入了课程名称
            if (nameEditText.getText().length() == 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PublishOne.this);
                builder.setMessage("请输入课程名称");
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

            //判断是否选择了图片
            if (pictureBitmap == null){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PublishOne.this);
                builder.setMessage("请选择一张图片");
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

            Intent intent = new Intent();
            intent.setClass(Activity_PublishOne.this, Activity_PublishTwo.class);       //跳转到课程列表

            Bundle bundle = new Bundle();
            bundle.putString("province", locationProvince);                             //目前定位的省
            bundle.putString("city", locationCity);                                     //目前定位的市
            bundle.putString("district", locationDistrict);                             //目前定位的区
            bundle.putString("lat", locationLat);                                       //目前定位的经度
            bundle.putString("lng", locationLng);                                       //目前定位的纬度
            className = nameEditText.getText().toString();
            bundle.putString("classname", className);                                   //课程名称
            bundle.putString("subjectCode", subjectCode);                               //一级分类编码
            bundle.putString("courseCode", courseCode);                                 //二级分类编码
            bundle.putString("typeName", subjectString);                                //一级分类名称
            bundle.putString("subjectName", courseString);                              //二级分类名称
            bundle.putString("picturePath", picturePath);                               //图片文件的路径
            bundle.putStringArrayList("agencyList", agencyList);                        //机构列表
            bundle.putStringArrayList("agencyIDList", agencyIDList);                    //机构编号列表
            intent.putExtras(bundle);
            //intent.putExtra("bitmap", pictureBitmap);                                   //上传的图片*/

            startActivity(intent);
        }
    }

    //2-4.把bitmap转换为file，并保存在SD卡中
    public void saveFile(Bitmap bm, String fileName) throws IOException {
        String path = getSDPath() + "/Moco/";                        //2-5.获得根目录
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

    //2-5.获得根目录
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
        }
        return sdDir.toString();
    }
}
