package com.jishuli.Moco.Activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.jishuli.Moco.Adapter.Adapter_ClassItem;
import com.jishuli.Moco.Adapter.Adapter_PopupPlus;
import com.jishuli.Moco.AppClass.ClassItem;
import com.jishuli.Moco.BusinessLogicLayer.dao.CityDao;
import com.jishuli.Moco.BusinessLogicLayer.dao.ProvinceDao;
import com.jishuli.Moco.BusinessLogicLayer.manager.DatabaseManager;
import com.jishuli.Moco.BusinessLogicLayer.utility.Utility;
import com.jishuli.Moco.ExitApplication;
import com.jishuli.Moco.PersistenceLayer.City;
import com.jishuli.Moco.PersistenceLayer.County;
import com.jishuli.Moco.PersistenceLayer.Province;
import com.jishuli.Moco.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Activity_Main extends Activity{
    static private String ROOT_PATH = "http://120.25.166.18";
    static private String PATH = "http://120.25.166.18/mainpage";

    private AMapLocationClient mLocationClient = null;
    private AMapLocationListener mapLocationListener = new mListener();
    private AMapLocationClientOption mLocationOption = null;
    private String locationProvince = "山东省";                            //定位的省
    private String locationCity = "青岛市";                                //定位的市
    private String locationDistrict = "市南区";                            //定位的区
    private double locationLat = 0;
    private double locationLng = 0;

    //Slide的容器
    private ViewPager viewPager;

    //Slide中的控件
    private ImageView[] imageViews;
    private ImageView[] dotsViews;
    private int[] imageResIds;

    //Slide中的定时器
    private ScheduledExecutorService scheduledExecutorService;

    //Slide中的图片和点数
    private int currentItem = 0;
    private int totalImages = 0;
    private int totalDots = 0;
    private ViewGroup viewGroup;

    //Slide切换间隔时间
    private static final int INTERVAL_TIME = 5;

    private static final int SET_SLIDE = 0;
    private static final int SET_LISTVIEW = 1;
    private static final int SET_LOCATION = 2;
    private static final int NETWORK_DOWN = -1;

    //用于Slide切换图片 和 更新ListView 的Handler
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //Slide的handler
                case SET_SLIDE: {
                    viewPager.setCurrentItem(currentItem);
                    break;
                }

                //ListView的handler
                case SET_LISTVIEW: {
                    setListViewAdapter();                   //9.设置ListView的适配器
                    break;
                }

                //更新定位按钮的显示
                case SET_LOCATION: {
                    //先把“市”去掉
                    String temp = locationCity.substring(0, locationCity.length() - 1);
                    locationTextView.setText(temp);

                    CityDao cityDao = DatabaseManager.getmInstance(Activity_Main.this).getCityDao();
                    City city = cityDao.queryBuilder().where(CityDao.Properties.Name.eq(locationCity)).unique();
                    Long ID = city.getIdentifier();
                    String cityID = ID.toString().substring(0, 4);
                    String path = PATH + "?cityID=" + cityID;

                    getDataFromServer(path);                //2.从服务器端获取数据
                    break;
                }

                //网络故障
                case NETWORK_DOWN: {
                    //其他情况
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Main.this);
                    builder.setMessage("无法连接网络，请检查");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }

                default:
                    break;
            }
        }
    };

    //ListView的控件
    private List<ClassItem> classItems = new ArrayList<ClassItem>();
    private int listNumber = 8;
    private int[] listNumberRes = new int[listNumber];                            //ListView课程数量数组
    private ListAdapter listAdapter;
    private ListView classListView = null;

    private EditText searchBarEditText;
    private ImageButton frontPageImageButton;
    private ImageButton myClassImageButton;
    private ImageButton myProfileImageButton;
    private ImageButton plusImageButton;
    private TextView locationTextView;

    private PopupWindow plusPopupWindow;

    //程序退出时的时间
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //添加到activityList列表中
        ExitApplication.getInstance().addActivity(this);

        //首先检查是否连接网络
        if (!connectionState()){
            setContentView(R.layout.activity_null);
        }
        else {
            setContentView(R.layout.activity_main);

            isFirstTimeBoot();                              //0.判断是否需要加载数据库
            init();                                         //1.首先初始化控件
            getDataFromServer(PATH + "?cityID=3201");       //2.从服务器端获取数据（默认南京）
            setSlideImages();                               //3.设置Slide中的图片
            setSlideDots();                                 //4.设置Slide中的点
            setSlideAdapter();                              //5.设置Slide的设配器
            setListViewListener();                          //6.设置ListView的监听器
            setButtonListeners();                           //7.设置各个按钮的监听器
            getLocation();                                  //8.定位
        }
    }

    @Override
    protected void onStart() {
        //让Slide开始滚动的计时器
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 0, INTERVAL_TIME, TimeUnit.SECONDS);
        super.onStart();
    }

    @Override
    protected void onStop() {
        scheduledExecutorService.shutdown();
        super.onStop();
    }

    //获取屏幕尺寸
    public void getScreenSize(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics = getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        int densityDPI = displayMetrics.densityDpi;
        float width = displayMetrics.widthPixels;
        float height = displayMetrics.heightPixels;
        Toast.makeText(this, "密度："+density + "  密度DPI："+densityDPI + "\r\n" + "宽："+width + "  高："+height, Toast.LENGTH_LONG).show();
    }

    //判断网络状态
    private boolean connectionState(){
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo WiFi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo GPRS = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (WiFi.isConnected()){
            System.out.println("已连接WIFI");
            return true;
        }
        else if (GPRS.isConnected()){
            System.out.println("已连接GPRS");
            return true;
        }
        else{
            System.out.println("无网络连接");
            return false;
        }
    }

    //0.判断是否需要加载数据库
    public void isFirstTimeBoot() {
        //判断是否是第一次启动
        SharedPreferences sharedPreferences = getSharedPreferences("isFirstTime", Activity.MODE_PRIVATE);
        Boolean isFirst = sharedPreferences.getBoolean("FIRST", true);

        //如果是，则加载数据库
        if(isFirst){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("FIRST", false);
            editor.apply();

            Utility.readSubjectFromAssets(Activity_Main.this);      //读取课程txt文件
            Utility.readProvinceFromAssets(Activity_Main.this);     //读取省区txt文件
        }
    }

    //1.初始化控件
    public void init(){
        //显示课程分类的ListView
        classListView = (ListView)findViewById(R.id.listViewLayout);

        //ListView的Header，用于显示Slide
        View headerView = getLayoutInflater().inflate(R.layout.main_headerview, null);
        classListView.addHeaderView(headerView);

        //从headerView中才能找到这两个控件
        viewPager = (ViewPager)headerView.findViewById(R.id.viewPager);
        viewGroup = (ViewGroup)headerView.findViewById(R.id.dotLayout);

        locationTextView = (TextView)findViewById(R.id.locationTextView);
        plusImageButton = (ImageButton)findViewById(R.id.plusImageButton);

        searchBarEditText = (EditText)findViewById(R.id.Main_SearchBar_EditText);

        frontPageImageButton = (ImageButton)findViewById(R.id.frontPage);
        myClassImageButton = (ImageButton)findViewById(R.id.schedule);
        myProfileImageButton = (ImageButton)findViewById(R.id.profile);

        //设置底部导航栏的样式
        frontPageImageButton.setBackgroundResource(R.drawable.bt_frontpage_pressed);
        myClassImageButton.setBackgroundResource(R.drawable.bt_schedule);
        myProfileImageButton.setBackgroundResource(R.drawable.bt_myprofile);
    }

    //2.从服务器上获得数据
    public void getDataFromServer(final String path){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(path);
                    URL url = new URL(path);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setConnectTimeout(5000);      //超时时间，5秒
                    httpURLConnection.setRequestMethod("GET");      //方式为GET
                    httpURLConnection.setDoInput(true);

                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        InputStream inputStream = httpURLConnection.getInputStream();   //获得输入流
                        byte[] data = readStream(inputStream);                          //把输入流转换成字符串组，单独一个函数：2-1
                        String JSONString = new String(data);                           //把字符串组转换成字符串

                        JSONObject jsonObject = new JSONObject(JSONString);             //得到总的JSON数据

                        //得到ListView中的课程数目
                        for (int i = 0; i < jsonObject.length(); i++){
                            listNumberRes[i] = jsonObject.getInt("typeID_1" + i);       //课程的数目
                        }
                        //说明已下载完数据
                        handler.sendEmptyMessage(SET_LISTVIEW);                         //9.设置ListView的适配器
                    }
                    else {
                        System.out.println("网络有问题");
                        handler.sendEmptyMessage(NETWORK_DOWN);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("JSON异常");

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("其他异常");
                    handler.sendEmptyMessage(NETWORK_DOWN);
                }
            }
        }).start();
    }

    //3.设置Slide中的图片
    public void setSlideImages(){
        //Slide中的图片资源
        imageResIds = new int[]{R.drawable.slide, R.drawable.slide, R.drawable.slide, R.drawable.slide};
        //Slide中图片的总数
        totalImages = imageResIds.length;
        //Slide中点的数量
        totalDots = totalImages - 2;

        imageViews = new ImageView[totalImages];
        for (int i = 0; i < totalImages; i++){
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(imageResIds[i]);
            imageViews[i] = imageView;
        }
    }
    //4.设置Slide中的点
    public void setSlideDots(){
        //设置点的图片
        dotsViews = new ImageView[totalDots];
        for (int i = 0; i < totalDots; i++){
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ActionBar.LayoutParams(10, 10));
            imageView.setImageResource(R.drawable.slide_dot_white);
            dotsViews[i] = imageView;

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;

            viewGroup.addView(imageView, layoutParams);
        }
    }

    //5.设置Slide的适配器
    public void setSlideAdapter(){
        viewPager.setAdapter(new viewPagerAdapter());
        viewPager.setOnPageChangeListener(new pageChaneListener());
    }

    //6.设置ListView的监听器
    public void setListViewListener(){
        classListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(Activity_Main.this, Activity_ClassDetail.class);      //跳转到课程列表

                Bundle bundle = new Bundle();
                bundle.putString("city", locationCity);                 //目前定位的城市
                bundle.putDouble("lat", locationLat);                   //目前定位的纬度
                bundle.putDouble("lng", locationLng);                   //目前定位的经度
                bundle.putInt("id", position);                          //点击了第几个课程分类

                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }

    //7.设置各个按钮的监听器
    public void setButtonListeners(){
        //定位按钮
        locationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity_Main.this, Activity_Main_Location.class);
                startActivityForResult(intent, 2);                  //7-1.选择城市后
            }
        });

        //搜索框
        searchBarEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity_Main.this, Activity_Search.class);

                Bundle bundle = new Bundle();
                bundle.putString("city", locationCity);                 //目前定位的城市
                bundle.putDouble("lat", locationLat);                   //目前定位的纬度
                bundle.putDouble("lng", locationLng);                   //目前定位的经度

                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        //加号按钮
        plusImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plusPopupWindow();                                  //7-3
            }
        });

        //底部导航按钮只需设置第二和第三个
        myClassImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(Activity_Main.this, Activity_MyClass.class);
                startActivity(intent);
            }
        });

        myProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(Activity_Main.this, Activity_MyProfile.class);
                startActivity(intent);
            }
        });
    }

    //8.定位
    public void getLocation(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mLocationClient = new AMapLocationClient(Activity_Main.this.getApplicationContext());
                mLocationClient.setLocationListener(mapLocationListener);

                //初始化定位参数
                mLocationOption = new AMapLocationClientOption();
                //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
                mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                //设置是否返回地址信息（默认返回地址信息）
                mLocationOption.setNeedAddress(true);
                //设置是否只定位一次,默认为false
                mLocationOption.setOnceLocation(true);
                //设置是否强制刷新WIFI，默认为强制刷新
                mLocationOption.setWifiActiveScan(true);
                //设置是否允许模拟位置,默认为false，不允许模拟位置
                mLocationOption.setMockEnable(false);
                //设置定位间隔,单位毫秒,默认为2000ms
                mLocationOption.setInterval(2000);
                //给定位客户端对象设置定位参数
                mLocationClient.setLocationOption(mLocationOption);
                //启动定位
                mLocationClient.startLocation();
            }
        }).start();
    }

    //9.handler中：ListView的适配器
    public void setListViewAdapter(){
        //清空数据，防止叠加
        classItems.clear();

        //ListView的图片
        int[] listImageRes = new int[] {R.mipmap.class1, R.mipmap.class2, R.mipmap.class3, R.mipmap.class4,
                R.mipmap.class5, R.mipmap.class6, R.mipmap.class7, R.mipmap.class8};

        for (int i = 0; i < listImageRes.length; i++){
            classItems.add(new ClassItem(listNumberRes[i], listImageRes[i]));
        }

        listAdapter = new Adapter_ClassItem(this, classItems);
        classListView.setAdapter(listAdapter);
    }

    //2-1.读数据中用到的函数
    private static byte[] readStream(InputStream inputStream) throws Exception{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1){
            byteArrayOutputStream.write(buffer, 0, length);
        }
        byteArrayOutputStream.close();
        inputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    //2-2.得到Slide中的图片
    private Bitmap getSlideImage(String imagePath){
        Bitmap bitmap = null;
        try {
            URL u = new URL(ROOT_PATH + imagePath);

            HttpURLConnection h = (HttpURLConnection)u.openConnection();
            h.setConnectTimeout(5000);
            h.setRequestMethod("GET");
            h.setDoInput(true);

            h.connect();

            InputStream is = h.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  bitmap;
    }

    //3-1.ViewPager的适配器
    private class viewPagerAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int i = position % totalImages;
            ImageView imageView = imageViews[i];
            viewPager.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            int i = position % totalImages;
            ImageView imageView = imageViews[i];
            viewPager.removeView(imageView);
        }

        @Override
        public int getCount() {
            int count = totalImages;
            return count;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }
    }

    //3-2.ViewPager的监听器
    private class pageChaneListener implements ViewPager.OnPageChangeListener{
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            currentItem = position;
            setDotsBackground(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE){
                if (currentItem == totalImages - 1){
                    viewPager.setCurrentItem(1, false);
                }else if (currentItem == 0){
                    viewPager.setCurrentItem(totalImages - 2, false);
                }
            }
        }
    }

    //3-4.Slide中切换图片的线程
    private class ScrollTask implements Runnable{
        @Override
        public void run() {
            synchronized (viewPager){
                currentItem = (currentItem + 1) % totalImages;
                handler.obtainMessage().sendToTarget();         //通过Handler切换图片
            }
        }
    }

    //4-1.设置点的样式
    private void setDotsBackground(int position){
        //position == 0代表最后一张图片
        if (position == 0){
            dotsViews[totalDots - 1].setImageResource(R.drawable.slide_dot_green);
            for (int i = 0; i <= totalDots - 2; i++){
                dotsViews[i].setImageResource(R.drawable.slide_dot_white);
            }
            //position == totalImages - 1代表第一张图片
        }else if (position == totalImages - 1){
            dotsViews[0].setImageResource(R.drawable.slide_dot_green);
            for (int i = 1; i <= totalDots - 1; i++){
                dotsViews[i].setImageResource(R.drawable.slide_dot_white);
            }
        }
        else {
            for (int i = 0; i <= totalDots - 1; i++){
                dotsViews[i].setImageResource(R.drawable.slide_dot_white);
            }
            dotsViews[position - 1].setImageResource(R.drawable.slide_dot_green);
        }
    }

    //7-1.跳转到另一个Activity，选择城市后
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2){

            //选取了某个城市
            if (resultCode == 2){
                Bundle bundle=data.getExtras();
                String cityName = bundle.getString("cityName");

                locationCity = cityName;
                /*String temp = cityName.substring(0, cityName.length() - 1);   //把“市”去掉
                locationTextView.setText(temp);*/

                //根据所选择的城市，确定省份
                CityDao cityDao = DatabaseManager.getmInstance(Activity_Main.this).getCityDao();
                City city = cityDao.queryBuilder().where(CityDao.Properties.Name.eq(locationCity)).unique();
                String identifier = city.getIdentifier().toString();

                ProvinceDao provinceDao = DatabaseManager.getmInstance(Activity_Main.this).getProvinceDao();
                List<Province> provinceList = provinceDao.queryBuilder().list();
                for (Province p : provinceList) {
                    if (p.getIdentifier().toString().substring(0, 2).equals(identifier.substring(0, 2))){
                        locationProvince = p.getName().toString();
                        break;
                    }
                }

                //根据所选择的城市，默认第一个区
                List<County> countyList = city.getCounties();
                locationDistrict = countyList.get(0).getName().toString();

                handler.sendEmptyMessage(SET_LOCATION);
            }

            //按下了返回键，没有选择城市，什么也不做
            else {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //7-2.搜索课程
    public void searchCourse(String searchContent){
        Intent intent = new Intent();
        intent.setClass(Activity_Main.this, Activity_ClassDetail.class);      //跳转到课程列表

        Bundle bundle = new Bundle();
        bundle.putString("city", locationCity);                               //目前定位的城市
        bundle.putDouble("lat", locationLat);                                 //目前定位的纬度
        bundle.putDouble("lng", locationLng);                                 //目前定位的经度
        bundle.putString("searchContent", searchContent);                     //搜索的内容

        int position = -1;
        bundle.putInt("id", position);                                        //点击了第几个课程分类

        intent.putExtras(bundle);

        startActivity(intent);
    }


    //7-3.加号按钮
    public void plusPopupWindow(){
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_window, null);
        ListView listView = (ListView)view.findViewById(R.id.popup_window_listView);

        String[] strings = new String[]{"发布课程", "注册机构"};
        Adapter_PopupPlus adapter = new Adapter_PopupPlus(this, strings);
        listView.setAdapter(adapter);

        plusPopupWindow = new PopupWindow(findViewById(R.id.Main_Layout));
        plusPopupWindow.setContentView(view);
        plusPopupWindow.setFocusable(true);

        listView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        plusPopupWindow.setWidth(listView.getMeasuredWidth());
        plusPopupWindow.setHeight(listView.getMeasuredHeight() * 2);

        //不设置背景的话下面的代码不管用
        plusPopupWindow.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.slide_dot_white));
        plusPopupWindow.setOutsideTouchable(true);
        plusPopupWindow.showAsDropDown(plusImageButton, 0, 20);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    //发布课程
                    case 0: Intent intent1 = new Intent();
                            intent1.setClass(Activity_Main.this, Activity_PublishOne.class);      //跳转到课程列表
                            Bundle bundle = new Bundle();
                            bundle.putString("province", locationProvince);     //目前定位的省
                            bundle.putString("city", locationCity);             //目前定位的市
                            bundle.putString("district", locationDistrict);     //目前定位的区
                            bundle.putString("lat", locationLat + "");          //目前定位的经度
                            bundle.putString("lng", locationLng + "");          //目前定位的纬度
                            intent1.putExtras(bundle);
                            startActivity(intent1);
                            break;

                    //注册机构
                    case 1: Intent intent2 = new Intent();
                            intent2.setClass(Activity_Main.this, Activity_SignUpAgency.class);
                            startActivity(intent2);
                            break;
                }
                plusPopupWindow.dismiss();
                plusPopupWindow = null;
            }
        });
    }

    //8-1.高德地图定位监听器
    public class mListener implements AMapLocationListener{
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    double lat = amapLocation.getLatitude();//获取纬度
                    double lng = amapLocation.getLongitude();//获取经度
                    amapLocation.getAccuracy();//获取精度信息
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    df.format(date);//定位时间
                    amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    amapLocation.getCountry();//国家信息
                    String province = amapLocation.getProvince();//省信息
                    String city = amapLocation.getCity();//城市信息
                    String district = amapLocation.getDistrict();//城区信息
                    amapLocation.getStreet();//街道信息
                    amapLocation.getStreetNum();//街道门牌号信息
                    amapLocation.getCityCode();//城市编码
                    amapLocation.getAdCode();//地区编码
                    amapLocation.getAoiName();//获取当前定位点的AOI信息

                    locationProvince = province;
                    locationCity = city;
                    locationDistrict = district;
                    locationLat = lat;
                    locationLng = lng;

                    //将目前的地理信息保存在SharedPreferences中
                    SharedPreferences sharedPreferences = getSharedPreferences("geographyInfo", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("province", locationProvince);
                    editor.putString("city", locationCity);
                    editor.putString("district", locationDistrict);
                    editor.putString("lat", locationLat + "");
                    editor.putString("lng", locationLng + "");
                    editor.apply();

                    if (locationCity != null){
                        handler.sendEmptyMessage(SET_LOCATION);
                    }

                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    }

    //按两下返回键，退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis() - exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
            else {
                ExitApplication.getInstance().exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}