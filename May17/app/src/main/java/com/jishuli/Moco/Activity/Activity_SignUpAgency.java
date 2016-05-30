package com.jishuli.Moco.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.jishuli.Moco.ExitApplication;
import com.jishuli.Moco.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Activity_SignUpAgency extends Activity {
    private static final String PATH = "http://120.25.166.18/institutionNew";

    private ImageButton backButton;
    private ImageButton pictureButton;
    private Button signUpButton;

    private EditText agencyNameEditText;
    private EditText agencyNumberEditText;
    private EditText ownerNameEditText;
    private EditText ownerIDEditText;
    private EditText contactEditText;
    private EditText introEditText;

    private String agencyNameString;
    private String agencyNumberString;
    private String ownerNameString;
    private String ownerIDString;
    private String contactString;
    private String introString;
    private Bitmap pictureBitmap = null;

    private static final int SUCCESS = 0;
    private static final int AGENCY_EXIST = 1;
    private static final int PHONE_NUMBER_EXIST = 2;
    private static final int CODE_WRONG = 3;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            AlertDialog.Builder builder;
            switch (msg.what){
                case SUCCESS:
                    //显示“注册成功”，并跳转到登陆页面
                    builder = new AlertDialog.Builder(Activity_SignUpAgency.this);
                    builder.setMessage("注册成功！");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setClass(Activity_SignUpAgency.this, Activity_Main.class);
                            startActivity(intent);

                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;

                case AGENCY_EXIST:
                    builder = new AlertDialog.Builder(Activity_SignUpAgency.this);
                    builder.setMessage("该机构已存在，请修改");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;

                case PHONE_NUMBER_EXIST:
                case CODE_WRONG:
                default: break;
            }
        }
    };

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
            intent.setClass(Activity_SignUpAgency.this, Activity_SignIn.class);
            startActivity(intent);
        }
        else {
            setContentView(R.layout.activity_signupagency);

            findViews();                                    //1.找到各种控件
            setListeners();                                 //2.设置监听器
        }
    }

    //1.找到各种控件
    public void findViews(){
        backButton = (ImageButton)findViewById(R.id.SignUpAgencyBackgroundLayoutBackButton);
        pictureButton = (ImageButton)findViewById(R.id.SignUpAgencyPictureImageButton);
        agencyNameEditText = (EditText)findViewById(R.id.SignUpAgencyAgencyNameEditText);
        agencyNumberEditText = (EditText)findViewById(R.id.SignUpAgencyAgencyNumberEditText);
        ownerNameEditText = (EditText)findViewById(R.id.SignUpAgencyOwnerNameEditText);
        ownerIDEditText = (EditText)findViewById(R.id.SignUpAgencyOwnerIDEditText);
        contactEditText = (EditText)findViewById(R.id.SignUpAgencyContactEditText);
        introEditText = (EditText)findViewById(R.id.SignUpAgencyIntroEditText);
        signUpButton = (Button)findViewById(R.id.SignUpAgencySignUpButton);
    }

    //2.设置监听器
    public void setListeners(){
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_SignUpAgency.this.finish();
            }
        });

        pictureButton.setOnClickListener(new ImageButtonListener());    //2-1.选择图片

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();                                             //2-3.提交数据
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
            ContentResolver contentResolver = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri));
                Matrix matrix = new Matrix();
                matrix.postScale(0.4f, 0.3f);            //长和宽缩小
                Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                pictureButton.setImageBitmap(resizeBmp);
                pictureBitmap = resizeBmp;
                System.out.println(pictureBitmap.getByteCount());
                System.out.println(pictureBitmap.getRowBytes() * pictureBitmap.getHeight());
            }
            catch (FileNotFoundException e){
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //2-3.提交数据
    public void sendData(){
        agencyNameString = agencyNameEditText.getText().toString();
        agencyNumberString = agencyNumberEditText.getText().toString();
        ownerNameString = ownerNameEditText.getText().toString();
        ownerIDString = ownerIDEditText.getText().toString();
        contactString = contactEditText.getText().toString();
        introString = introEditText.getText().toString();

        if (agencyNameString.length() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SignUpAgency.this);
            builder.setMessage("请填写机构名称");
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

        if (agencyNumberString.length() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SignUpAgency.this);
            builder.setMessage("请填写工商局注册号");
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

        if (ownerNameString.length() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SignUpAgency.this);
            builder.setMessage("请填写注册人姓名");
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

        if (ownerIDString.length() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SignUpAgency.this);
            builder.setMessage("请填写注册人证件号");
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

        if (contactString.length() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SignUpAgency.this);
            builder.setMessage("请填写联系电话");
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

        if (introString.length() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SignUpAgency.this);
            builder.setMessage("请填写机构简介");
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

        if (pictureBitmap == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SignUpAgency.this);
            builder.setMessage("请上传机构图片");
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

        //课程图片  bitmap转为String
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        pictureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] coursePic = byteArrayOutputStream.toByteArray();
        final String pictureString = Base64.encodeToString(coursePic, Base64.DEFAULT);


        //取出cookie
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        final String cookie = sharedPreferences.getString("cookie", null);

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_xsrf", cookie.substring(6));
            jsonObject.put("name", agencyNameString);
            jsonObject.put("certificateID", agencyNumberString);
            jsonObject.put("legalPersonName", ownerNameString);
            jsonObject.put("legalPersonID", ownerIDString);
            jsonObject.put("PhoneNum", contactString);
            jsonObject.put("description", introString);
            jsonObject.put("certificateImg", "abc");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            //String content = String.valueOf(jsonObject);
            String content = cookie + "&name=" + agencyNameString + "&certificateID=" + agencyNumberString
                            + "&legalPersonName=" + ownerNameString + "&legalPersonID=" + ownerIDString
                            + "&PhoneNum=" +contactString + "&description=" + introString
                            + "&certificateImg=" + "abc";

            @Override
            public void run() {
                try {
                    URL url = new URL(PATH);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setRequestProperty("Cookie", cookie);
                    httpURLConnection.setRequestProperty("Connection", "keep-alive");
                    httpURLConnection.setRequestProperty("User-Agent", "Fiddler");
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    //httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Content-Length", String.valueOf(content.getBytes().length));
                    httpURLConnection.setRequestProperty("Charset", "utf-8");

                    System.out.println(content);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(content.getBytes());
                    outputStream.close();

                    //获得返回代码
                    String responseCode = httpURLConnection.getResponseCode() + "";
                    System.out.println(responseCode);

                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        String data;
                        String responseData = "";
                        while((data = bufferedReader.readLine()) != null)
                        {
                            responseData += data;
                        }
                        System.out.println(responseData);

                        JSONObject jsonObject = new JSONObject(responseData);
                        String feedback = jsonObject.getString("feedback");
                        System.out.println(feedback);

                        bufferedReader.close();

                        //根据返回的提示，显示相应的对话框
                        //注册成功
                        if (responseData.contains("success")){
                            handler.sendEmptyMessage(SUCCESS);
                        }
                    }

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}