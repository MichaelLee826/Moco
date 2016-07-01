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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

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

    private String cookie = "";
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

    private static final int FAILED = -1;
    private static final int SUCCESS = 0;
    private static final int INSTITUTION_EXIST = 1;
    private static final int UNKNOWN_REASON = 100;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            AlertDialog.Builder builder;
            switch (msg.what) {
                case FAILED:
                    builder = new AlertDialog.Builder(Activity_SignUpAgency.this);
                    builder.setMessage("网络有问题，请检查");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;

                case SUCCESS:
                    //显示“提交成功”，并跳转到首页
                    builder = new AlertDialog.Builder(Activity_SignUpAgency.this);
                    builder.setMessage("提交成功！");
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

                case INSTITUTION_EXIST:
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

                case UNKNOWN_REASON:
                    //其他情况
                    builder = new AlertDialog.Builder(Activity_SignUpAgency.this);
                    builder.setMessage("好像哪里出了问题，请再试一次");
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

                default:
                    break;
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

        if (userName == null | password == null) {
            Intent intent = new Intent();
            intent.setClass(Activity_SignUpAgency.this, Activity_SignIn.class);
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_signupagency);

            init();                                         //1.初始化控件
            setListeners();                                 //2.设置监听器
            getUserData();                                  //3.获取用户数据
        }
    }

    //1.初始化控件
    public void init() {
        backButton = (ImageButton) findViewById(R.id.SignUpAgencyBackgroundLayoutBackButton);
        pictureButton = (ImageButton) findViewById(R.id.SignUpAgencyPictureImageButton);
        agencyNameEditText = (EditText) findViewById(R.id.SignUpAgencyAgencyNameEditText);
        agencyNumberEditText = (EditText) findViewById(R.id.SignUpAgencyAgencyNumberEditText);
        ownerNameEditText = (EditText) findViewById(R.id.SignUpAgencyOwnerNameEditText);
        ownerIDEditText = (EditText) findViewById(R.id.SignUpAgencyOwnerIDEditText);
        contactEditText = (EditText) findViewById(R.id.SignUpAgencyContactEditText);
        introEditText = (EditText) findViewById(R.id.SignUpAgencyIntroEditText);
        signUpButton = (Button) findViewById(R.id.SignUpAgencySignUpButton);
    }

    //2.设置监听器
    public void setListeners() {
        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_SignUpAgency.this.finish();
            }
        });

        pictureButton.setOnClickListener(new ImageButtonListener());        //2-1.选择图片

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();                                                 //2-3.提交数据
            }
        });
    }

    //3.获取用户数据
    public void getUserData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(PATH);
                    Document document = Jsoup.parse(url, 5000);             //第三方库Jsoup，用于解析HTML
                    //System.out.println(document.html());
                    _xsrf = document.select("input").attr("value");         //从HTML中获得_xsrf

                    //取出cookie
                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
                    cookie = sharedPreferences.getString("cookie", null);
                    cookie = cookie + "; _xsrf = " + _xsrf;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //2-1.选择照片的监听器
    public class ImageButtonListener implements View.OnClickListener {
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
        if (resultCode == RESULT_OK) {
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

                pictureButton.setImageBitmap(resizeBmp);
                pictureBitmap = resizeBmp;
            }
            catch (FileNotFoundException e) {
            }

            try {
                saveFile(pictureBitmap, picName);                    //2-4.把bitmap转换为file

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //2-3.提交数据
    public void sendData() {
        agencyNameString = agencyNameEditText.getText().toString();
        agencyNumberString = agencyNumberEditText.getText().toString();
        ownerNameString = ownerNameEditText.getText().toString();
        ownerIDString = ownerIDEditText.getText().toString();
        contactString = contactEditText.getText().toString();
        introString = introEditText.getText().toString();

        if (agencyNameString.length() == 0) {
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

        if (agencyNumberString.length() == 0) {
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

        if (ownerNameString.length() == 0) {
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

        if (ownerIDString.length() == 0) {
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

        if (contactString.length() == 0) {
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

        if (!contactString.startsWith("1") | contactString.length() != 11 ){
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SignUpAgency.this);
            builder.setMessage("请填写正确格式的联系电话");
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

        if (introString.length() == 0) {
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

        if (pictureBitmap == null) {
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

        uploadData();                   //3-6.利用OkHttp上传数据
    }

    //2-4.把bitmap转换为file，并保存在SD卡中
    public void saveFile(Bitmap bm, String fileName) throws IOException {
        String path = getSDPath() + "/Moco/";                        //2-5.获得根目录
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        file = new File(path + fileName);
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

    //3-6.利用OkHttp上传数据
    public void uploadData(){
        //用OkHttp发送POST数据
        OkHttpClient mOkHttpClient = new OkHttpClient();

        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"_xsrf\""), RequestBody.create(null, _xsrf))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"name\""), RequestBody.create(null, agencyNameString))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"institutionID\""), RequestBody.create(null, agencyNumberString))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"legalPersonName\""), RequestBody.create(null, ownerNameString))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"legalPersonID\""), RequestBody.create(null, ownerIDString))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"PhoneNum\""), RequestBody.create(null, contactString))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"description\""), RequestBody.create(null, introString))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"" + file.getName() + ".jpg\""), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(PATH)
                .addHeader("Cookie", cookie)
                .post(requestBody)
                .build();

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                System.out.println("失败");
                //网络错误
                handler.sendEmptyMessage(FAILED);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                System.out.println(responseData);

                //获取返回状态
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    String feedback = jsonObject.getString("feedback");

                    //提交成功
                    if (feedback.equals("success")){
                        handler.sendEmptyMessage(SUCCESS);
                    }

                    //用户名不存在
                    else if (feedback.equals("institution exist")){
                        handler.sendEmptyMessage(INSTITUTION_EXIST);
                    }

                    else {
                        handler.sendEmptyMessage(UNKNOWN_REASON);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(UNKNOWN_REASON);
                    System.out.println("其他原因");
                }
            }
        });
    }
}