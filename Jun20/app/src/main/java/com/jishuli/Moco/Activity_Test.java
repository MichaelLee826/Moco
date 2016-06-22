package com.jishuli.Moco;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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
import java.util.UUID;

public class Activity_Test extends Activity {
    private static final String PATH = "http://120.25.166.18/file";

    ImageButton imageButton;
    Button button;
    Bitmap pictureBitmap;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        imageButton = (ImageButton)findViewById(R.id.testImageButton);
        button = (Button)findViewById(R.id.testButton);

        //“提交”按钮
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String BOUNDARY = UUID.randomUUID().toString();
                        String PREFIX = "--";
                        String LINE_END = "\r\n";
                        String CHARSET = "UTF-8";

                        //取出cookie
                        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
                        String cookie = sharedPreferences.getString("cookie", null);

                        try {
                            URL url = new URL(PATH);

                            //第三方库Jsoup，用于解析HTML
                            Document document = Jsoup.parse(url, 5000);
                            //System.out.println(document.html());

                            //从HTML中获得_xsrf
                            String _xsrf = document.select("input").attr("value");

                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setConnectTimeout(5000);
                            httpURLConnection.setRequestMethod("POST");
                            httpURLConnection.setDoOutput(true);
                            httpURLConnection.setDoOutput(true);
                            httpURLConnection.setUseCaches(false);
                            httpURLConnection.setRequestProperty("Cookie", cookie);
                            httpURLConnection.setRequestProperty("Connection", "keep-alive");
                            httpURLConnection.setRequestProperty("User-Agent", "Fiddler");
                            //httpURLConnection.setRequestProperty("Accept", "text/html, application/xhtml+xml, application/xml; q=0.9, */*; q=0.8");
                            //httpURLConnection.setRequestProperty("Accept-Language", "zh-CN, zh; q=0.8, en-US; q=0.5, en; q=0.3");
                            //httpURLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
                            //httpURLConnection.setRequestProperty("Referer", "http://120.25.166.18/insitutionNew");
                            //httpURLConnection.setRequestProperty("Content-Type", "text/html; charset=UTF-8");
                            //httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                            //httpURLConnection.setRequestProperty("Content-Length", String.valueOf(content.getBytes().length));
                            //httpURLConnection.setRequestProperty("Charset", "utf-8");


                            OutputStream outputStream = httpURLConnection.getOutputStream();


                            // 首先组拼文本类型的参数
                            StringBuilder sb = new StringBuilder();
                                sb.append(PREFIX);
                                sb.append(BOUNDARY);
                                sb.append(LINE_END);
                                sb.append("Content-Disposition: form-data; name=\"" + "_xsrf" + "\"" + LINE_END);
                                //sb.append("Content-Type: text/plain; charset=" + CHARSET + LINE_END);
                                //sb.append("Content-Transfer-Encoding: 8bit" + LINE_END);
                                sb.append(LINE_END);
                                sb.append(_xsrf);
                                sb.append(LINE_END);

                            //DataOutputStream outStream = new DataOutputStream(httpURLConnection.getOutputStream());
                            outputStream.write(sb.toString().getBytes());

                            // 发送文件数据
                            if (file != null) {
                                StringBuilder sb1 = new StringBuilder();
                                sb1.append(PREFIX);
                                sb1.append(BOUNDARY);
                                sb1.append(LINE_END);
                                // name是post中传参的键 filename是文件的名称
                                sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + LINE_END);
                                //sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                                sb1.append("Content-Type: image/png" + LINE_END);
                                sb1.append(LINE_END);
                                //outStream.write(sb1.toString().getBytes());
                                outputStream.write(sb1.toString().getBytes());

                                InputStream is = new FileInputStream(file);
                                byte[] buffer = new byte[1024];
                                int len = 0;
                                while ((len = is.read(buffer)) != -1) {
                                    //outStream.write(buffer, 0, len);
                                    outputStream.write(buffer, 0, len);
                                }
                                is.close();
                            }

                            //outStream.write(LINE_END.getBytes());
                            outputStream.write(LINE_END.getBytes());

                            // 请求结束标志
                            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                            //outStream.write(end_data);
                            //outStream.flush();
                            outputStream.write(end_data);
                            outputStream.flush();

                            String key;
                            for  ( int  i =  1 ; (key = httpURLConnection.getHeaderFieldKey(i)) !=  null ; i++ ) {
                                System.out.println(key +"-->" + httpURLConnection.getHeaderField(key));
                            }

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

                                }

                                outputStream.close();
                                //outStream.close();
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
        });

        //点击后去选择照片
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });
    }

    //得到所选择的照片
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

                imageButton.setImageBitmap(resizeBmp);
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
}