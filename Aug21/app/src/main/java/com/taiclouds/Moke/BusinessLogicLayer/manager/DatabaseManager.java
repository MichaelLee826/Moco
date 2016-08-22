package com.taiclouds.Moke.BusinessLogicLayer.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.taiclouds.Moke.BusinessLogicLayer.dao.CityDao;
import com.taiclouds.Moke.BusinessLogicLayer.dao.CountyDao;
import com.taiclouds.Moke.BusinessLogicLayer.dao.CourseDao;
import com.taiclouds.Moke.BusinessLogicLayer.dao.DaoMaster;
import com.taiclouds.Moke.BusinessLogicLayer.dao.DaoSession;
import com.taiclouds.Moke.BusinessLogicLayer.dao.ProvinceDao;
import com.taiclouds.Moke.BusinessLogicLayer.dao.SubjectDao;

/**
 * Created by zhaoqin on 3/14/16.
 */
public class DatabaseManager {
    private static Context mContext;
    private static DatabaseManager mInstance;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private SubjectDao subjectDao;
    private CourseDao courseDao;
    private ProvinceDao provinceDao;
    private CityDao cityDao;
    private CountyDao countyDao;

    private DatabaseManager(Context context) {
        mContext = context;
    }

    public static synchronized DatabaseManager getmInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseManager(context);
        }
        return mInstance;
    }

    private DaoSession getSession() {
        if (daoSession == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, "muggins-db", null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    public SubjectDao getSubjectDao() {
        if (subjectDao == null) {
            subjectDao = getSession().getSubjectDao();
        }
        return subjectDao;
    }

    public CourseDao getCourseDao() {
        if (courseDao == null) {
            courseDao = getSession().getCourseDao();
        }
        return courseDao;
    }

    public ProvinceDao getProvinceDao() {
        if (provinceDao == null) {
            provinceDao = getSession().getProvinceDao();
        }
        return provinceDao;
    }

    public CityDao getCityDao() {
        if (cityDao == null) {
            cityDao = getSession().getCityDao();
        }
        return cityDao;
    }

    public CountyDao getCountyDao() {
        if (countyDao == null) {
            countyDao = getSession().getCountyDao();
        }
        return countyDao;
    }
}
