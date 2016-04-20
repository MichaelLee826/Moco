package com.jishuli.Moco.BusinessLogicLayer.utility;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.jishuli.Moco.BusinessLogicLayer.dao.CityDao;
import com.jishuli.Moco.BusinessLogicLayer.dao.CountyDao;
import com.jishuli.Moco.BusinessLogicLayer.dao.CourseDao;
import com.jishuli.Moco.BusinessLogicLayer.dao.ProvinceDao;
import com.jishuli.Moco.BusinessLogicLayer.dao.SubjectDao;
import com.jishuli.Moco.BusinessLogicLayer.manager.DatabaseManager;
import com.jishuli.Moco.PersistenceLayer.City;
import com.jishuli.Moco.PersistenceLayer.County;
import com.jishuli.Moco.PersistenceLayer.Course;
import com.jishuli.Moco.PersistenceLayer.Province;
import com.jishuli.Moco.PersistenceLayer.Subject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhaoqin on 3/14/16.
 */
public class Utility {

    private static final String TAG = "Utility";

    public static void readSubjectFromAssets(final Context context) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                AssetManager assetManager = context.getAssets();
                try {
                    InputStream inputStream = assetManager.open("subject.txt");
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    Map<String, String> map = new TreeMap<>(
                            new Comparator<String>() {
                                public int compare(String obj1, String obj2) {
                                    // 升序排序
                                    return obj1.compareTo(obj2);
                                }
                            });
                    while ((line = bufferedReader.readLine()) != null) {
                        if (!line.contains("#")) {
                            matchSubjectIdentifier(line, map);
                        }
                    }
                    SubjectDao subjectDao = DatabaseManager.getmInstance(context).getSubjectDao();
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        if (entry.getKey().substring(2).equals("10")) {
                            Subject subject = subjectDao.queryBuilder().where(SubjectDao.Properties.Identifier.eq(Long.parseLong(entry.getKey()))).unique();
                            if (subject == null) {
                                subject = new Subject(null, Long.parseLong(entry.getKey()), entry.getValue());
                                subjectDao.insert(subject);
                            }
                        }
                    }

                    CourseDao courseDao = DatabaseManager.getmInstance(context).getCourseDao();
                    List<Subject> list = subjectDao.queryBuilder().list();
                    for (Subject subject : list) {
                        String identifier = String.valueOf(subject.getIdentifier());
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            if (entry.getKey().substring(0, 2).equals(identifier.substring(0, 2))
                                    && !entry.getKey().equals(identifier)) {
                                Course course = courseDao.queryBuilder().where(CourseDao.Properties.Identifier.eq(Long.parseLong(entry.getKey()))).unique();
                                if (course == null) {
                                    course = new Course(null, Long.parseLong(entry.getKey()), entry.getValue(), subject.getId());
                                    courseDao.insert(course);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();


    }

    public static void readProvinceFromAssets(final Context context) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                AssetManager assetManager = context.getAssets();
                try {
                    InputStream inputStream = assetManager.open("province.txt");

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    Map<String, String> map = new TreeMap<>(
                            new Comparator<String>() {
                                public int compare(String obj1, String obj2) {
                                    // 升序排序
                                    return obj1.compareTo(obj2);
                                }
                            });

                    while ((line = bufferedReader.readLine()) != null) {
                        if (!line.contains("#") && !line.equals("\t")) {
                            matchProvinceIdentifier(line, map);
                        }
                    }

                    ProvinceDao provinceDao = DatabaseManager.getmInstance(context).getProvinceDao();
                    map.remove("");
                    Log.e(TAG, "before read province");
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        if (entry.getKey().substring(2).equals("0000")) {
                            Province province = provinceDao.queryBuilder().where(ProvinceDao.Properties.Identifier.eq(Long.parseLong(entry.getKey()))).unique();
                            if (province == null) {
                                province = new Province(null, Long.parseLong(entry.getKey()), entry.getValue());
                                provinceDao.insert(province);
                            }
                        }
                    }

                    Log.e(TAG, "before read city");
                    CityDao cityDao = DatabaseManager.getmInstance(context).getCityDao();
                    List<Province> provinceList = provinceDao.queryBuilder().list();
                    for (Province province : provinceList) {
                        String identifier = String.valueOf(province.getIdentifier());
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            if (!entry.getValue().contains("市")) {
                                continue;
                            }
                            if (entry.getKey().substring(0, 2).equals(identifier.substring(0, 2))
                                    && entry.getKey().substring(4, 6).equals(identifier.substring(4, 6))
                                    && !entry.getKey().equals(identifier)) {
                                City city = cityDao.queryBuilder().where(CityDao.Properties.Identifier.eq(Long.parseLong(entry.getKey()))).unique();
                                if (city == null) {
                                    city = new City(null, Long.parseLong(entry.getKey()), entry.getValue(), province.getId());
                                    cityDao.insert(city);
                                }
                            }
                        }
                    }

                    Log.e(TAG, "before read county");
                    CountyDao countyDao = DatabaseManager.getmInstance(context).getCountyDao();
                    List<City> cityList = cityDao.queryBuilder().list();
                    for (City city : cityList) {
                        String identifier = String.valueOf(city.getIdentifier());
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            if (entry.getKey().substring(0, 4).equals(identifier.substring(0, 4))
                                    && !entry.getKey().equals(identifier)
                                    && !entry.getKey().substring(4, 6).equals("00")) {
                                County county = countyDao.queryBuilder().where(CountyDao.Properties.Identifier.eq(Long.parseLong(entry.getKey()))).unique();
                                if (county == null) {
                                    county = new County(null, Long.parseLong(entry.getKey()), entry.getValue(), city.getId());
                                    countyDao.insert(county);
                                }
                            }
                        }
                    }
                    Log.e(TAG, "finished");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();



    }

    private static void matchSubjectIdentifier(String input, Map<String, String> map) {
        String patternNumber = "[^0-9]";
        Pattern rNumber = Pattern.compile(patternNumber);
        Matcher matcherNumber = rNumber.matcher(input);

        String patternName = "[0-9]";
        Pattern rName = Pattern.compile(patternName);
        Matcher matcherName = rName.matcher(input);
        map.put(matcherNumber.replaceAll("").trim(), matcherName.replaceAll("").trim().replaceAll("\\t", "").replaceAll("\\n", ""));
    }

    private static void matchProvinceIdentifier(String input, Map<String, String> map) {
        String patternNumber = "[^0-9]";
        Pattern rNumber = Pattern.compile(patternNumber);
        Matcher matcherNumber = rNumber.matcher(input);

        String patternName = "[0-9]";
        Pattern rName = Pattern.compile(patternName);
        Matcher matcherName = rName.matcher(input);
        String name = matcherName.replaceAll("").trim().replaceAll("\\t", "");
        if (!name.equals("市辖区") && !name.equals("县")) {
            map.put(matcherNumber.replaceAll("").trim(), name);
        }
    }

}
