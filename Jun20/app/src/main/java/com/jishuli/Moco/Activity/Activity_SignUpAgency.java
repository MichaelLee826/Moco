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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.jishuli.Moco.ExitApplication;
import com.jishuli.Moco.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Activity_SignUpAgency extends Activity {
    private static final String PATH = "http://120.25.166.18/institutionNew";
    //static private String PATH = "http://10.0.2.2:8000/institutionNew";
    private OkHttpClient mOkHttpClient = null;
    private String _xsrf = "";

    private ImageButton backButton;
    private ImageButton pictureButton;
    private Button signUpButton;

    private File file;

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
                //sendData();                                             //2-3.提交数据
                okHttp();
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
            }
            catch (FileNotFoundException e){
            }

            try {
                //把bitmap转换为file
                saveFile(pictureBitmap, "test");

            } catch (IOException e) {
                e.printStackTrace();
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

        /*if (pictureBitmap == null){
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
        final String pictureString = Base64.encodeToString(coursePic, Base64.DEFAULT);*/

        new Thread(new Runnable() {
            //String content = String.valueOf(jsonObject);

            @Override
            public void run() {
                String PREFIX = "--";
                String BOUNDARY = UUID.randomUUID().toString();
                String LINE_END = "\r\n";
                String CHARSET = "UTF-8";

                //取出cookie
                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
                String cookie = sharedPreferences.getString("cookie", null);
                String session = sharedPreferences.getString("session", null);
                String _xsrf = sharedPreferences.getString("_xsrf", null);

                //设置要发送的数据
                HashMap<String, String> params = new HashMap<>();
                params.put("Cookie", session);
                params.put("name", agencyNameString);
                params.put("certificateID", agencyNumberString);
                params.put("legalPersonName", ownerNameString);
                params.put("legalPersonID", ownerIDString);
                params.put("PhoneNum", contactString);
                params.put("description", introString);

                try {
                    URL url = new URL(PATH);

                    //第三方库Jsoup，用于解析HTML
                    Document document = Jsoup.parse(url, 5000);
                    //System.out.println(document.html());

                    //从HTML中获得_xsrf
                    String _xsrfLocal = document.select("input").attr("value");
                    params.put("_xsrf", _xsrfLocal);

                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setRequestProperty("Cookie", cookie + ";" + _xsrf);
                    httpURLConnection.setRequestProperty("Connection", "keep-alive");
                    httpURLConnection.setRequestProperty("User-Agent", "Fiddler");
                    //httpURLConnection.setRequestProperty("Referer", "http://120.25.166.18");
                    //httpURLConnection.setRequestProperty("Content-Type", "text/html; charset=UTF-8");
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    //httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

                    String content = cookie + ";" + _xsrf + "&name=" + agencyNameString + "&certificateID=" + agencyNumberString
                                    + "&legalPersonName=" + ownerNameString + "&legalPersonID=" + ownerIDString
                                    + "&PhoneNum=" + contactString + "&description=" + introString + "\r\n";


                    // 首先组拼文本类型的参数
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        sb.append(PREFIX);
                        sb.append(BOUNDARY);
                        sb.append(LINE_END);
                        sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END);
                        sb.append("Content-Type: text/plain; charset=" + CHARSET + LINE_END);
                        //sb.append("Content-Transfer-Encoding: 8bit" + LINE_END);
                        sb.append(LINE_END);
                        sb.append(entry.getValue());
                        sb.append(LINE_END);
                    }

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(content.getBytes());

                    outputStream.write(sb.toString().getBytes());
                    outputStream.flush();

                    // 发送文件数据
                    if (file != null) {
                        StringBuilder sb1 = new StringBuilder();
                        sb1.append(PREFIX);
                        sb1.append(BOUNDARY);
                        sb1.append(LINE_END);
                        sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + LINE_END);
                        sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                        sb1.append("Content-Type: image/png" + LINE_END);
                        sb1.append(LINE_END);
                        outputStream.write(sb1.toString().getBytes());

                        InputStream is = new FileInputStream(file);
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = is.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, len);
                        }
                        is.close();
                    }
                    outputStream.write(LINE_END.getBytes());
                    outputStream.flush();

                    // 请求结束标志
                    byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();

                    outputStream.write(end_data);
                    outputStream.flush();
                    outputStream.close();

                    /*String key;
                    for  ( int  i =  1 ; (key = httpURLConnection.getHeaderFieldKey(i)) !=  null ; i++ ) {
                        System.out.println(key +"-->" + httpURLConnection.getHeaderField(key));
                    }*/

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

                        httpURLConnection.disconnect();
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

    //获得根目录
    public static String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
        }
        return sdDir.toString();
    }

    //把bitmap转换为file，并保存在SD卡中
    public void saveFile(Bitmap bm, String fileName) throws IOException {
        String path = getSDPath() +"/Moco/";
        File dirFile = new File(path);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        file = new File(path + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }



    public void okHttp(){
        //取出cookie
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        String cookie = sharedPreferences.getString("cookie", null);

        mOkHttpClient = new OkHttpClient();

        RequestBody fileBody = RequestBody.create(MediaType.parse("application/image"), file);
        String cookieString = "username=\"2|1:0|10:1466433607|8:username|12:emt5MTQxMzM3|a6f6207d7492505925dcfade6637fb8d2736b5e1f9c6f9af1f3676340a352a83\";_xsrf=2|0dc718df|2c51112ae8f549ca6268011324fdda36|1466433607";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(PATH);

                    Document document = Jsoup.parse(url, 5000);             //第三方库Jsoup，用于解析HTML
                    _xsrf = document.select("input").attr("value");         //从HTML中获得_xsrf

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"name\""), RequestBody.create(null, "test"))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"certificateID\""), RequestBody.create(null, "testID"))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"legalPersonName\""), RequestBody.create(null, "testPersonName"))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"legalPersonID\""), RequestBody.create(null, "testPersonID"))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"PhoneNum\""), RequestBody.create(null, "15150552739"))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"description\""), RequestBody.create(null, "testDescription"))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"" + file.getName()), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(PATH)
                .addHeader("Cookie", cookieString)
                .addHeader("_xsrf", _xsrf)
                .post(requestBody)
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
                System.out.println("失败" + response.body().string());
            }
        });
    }
}