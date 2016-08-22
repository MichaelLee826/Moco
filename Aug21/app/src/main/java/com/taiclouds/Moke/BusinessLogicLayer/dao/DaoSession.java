package com.taiclouds.Moke.BusinessLogicLayer.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.taiclouds.Moke.PersistenceLayer.Province;
import com.taiclouds.Moke.PersistenceLayer.City;
import com.taiclouds.Moke.PersistenceLayer.County;
import com.taiclouds.Moke.PersistenceLayer.Subject;
import com.taiclouds.Moke.PersistenceLayer.Course;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig provinceDaoConfig;
    private final DaoConfig cityDaoConfig;
    private final DaoConfig countyDaoConfig;
    private final DaoConfig subjectDaoConfig;
    private final DaoConfig courseDaoConfig;

    private final ProvinceDao provinceDao;
    private final CityDao cityDao;
    private final CountyDao countyDao;
    private final SubjectDao subjectDao;
    private final CourseDao courseDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        provinceDaoConfig = daoConfigMap.get(ProvinceDao.class).clone();
        provinceDaoConfig.initIdentityScope(type);

        cityDaoConfig = daoConfigMap.get(CityDao.class).clone();
        cityDaoConfig.initIdentityScope(type);

        countyDaoConfig = daoConfigMap.get(CountyDao.class).clone();
        countyDaoConfig.initIdentityScope(type);

        subjectDaoConfig = daoConfigMap.get(SubjectDao.class).clone();
        subjectDaoConfig.initIdentityScope(type);

        courseDaoConfig = daoConfigMap.get(CourseDao.class).clone();
        courseDaoConfig.initIdentityScope(type);

        provinceDao = new ProvinceDao(provinceDaoConfig, this);
        cityDao = new CityDao(cityDaoConfig, this);
        countyDao = new CountyDao(countyDaoConfig, this);
        subjectDao = new SubjectDao(subjectDaoConfig, this);
        courseDao = new CourseDao(courseDaoConfig, this);

        registerDao(Province.class, provinceDao);
        registerDao(City.class, cityDao);
        registerDao(County.class, countyDao);
        registerDao(Subject.class, subjectDao);
        registerDao(Course.class, courseDao);
    }
    
    public void clear() {
        provinceDaoConfig.getIdentityScope().clear();
        cityDaoConfig.getIdentityScope().clear();
        countyDaoConfig.getIdentityScope().clear();
        subjectDaoConfig.getIdentityScope().clear();
        courseDaoConfig.getIdentityScope().clear();
    }

    public ProvinceDao getProvinceDao() {
        return provinceDao;
    }

    public CityDao getCityDao() {
        return cityDao;
    }

    public CountyDao getCountyDao() {
        return countyDao;
    }

    public SubjectDao getSubjectDao() {
        return subjectDao;
    }

    public CourseDao getCourseDao() {
        return courseDao;
    }

}
