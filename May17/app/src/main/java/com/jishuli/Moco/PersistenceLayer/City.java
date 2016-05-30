package com.jishuli.Moco.PersistenceLayer;

import java.util.List;
import com.jishuli.Moco.BusinessLogicLayer.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.jishuli.Moco.BusinessLogicLayer.dao.CityDao;
import com.jishuli.Moco.BusinessLogicLayer.dao.CountyDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "CITY".
 */
public class City {

    private Long id;
    private Long identifier;
    private String name;
    private long provinceID;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient CityDao myDao;

    private List<County> counties;

    public City() {
    }

    public City(Long id) {
        this.id = id;
    }

    public City(Long id, Long identifier, String name, long provinceID) {
        this.id = id;
        this.identifier = identifier;
        this.name = name;
        this.provinceID = provinceID;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCityDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Long identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(long provinceID) {
        this.provinceID = provinceID;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<County> getCounties() {
        if (counties == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CountyDao targetDao = daoSession.getCountyDao();
            List<County> countiesNew = targetDao._queryCity_Counties(id);
            synchronized (this) {
                if(counties == null) {
                    counties = countiesNew;
                }
            }
        }
        return counties;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetCounties() {
        counties = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
