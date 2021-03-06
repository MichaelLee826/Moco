package com.taiclouds.Moke.PersistenceLayer;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "COUNTY".
 */
public class County {

    private Long id;
    private Long identifier;
    private String name;
    private long cityID;

    public County() {
    }

    public County(Long id) {
        this.id = id;
    }

    public County(Long id, Long identifier, String name, long cityID) {
        this.id = id;
        this.identifier = identifier;
        this.name = name;
        this.cityID = cityID;
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

    public long getCityID() {
        return cityID;
    }

    public void setCityID(long cityID) {
        this.cityID = cityID;
    }

}
