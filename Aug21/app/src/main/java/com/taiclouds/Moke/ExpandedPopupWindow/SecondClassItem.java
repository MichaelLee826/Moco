package com.taiclouds.Moke.ExpandedPopupWindow;

/**
 * Created by MichaelLee826 on 2016-07-05-0005.
 */
public class SecondClassItem {
    private long id;
    private String name;

    public SecondClassItem(){

    }

    public SecondClassItem(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SecondClassItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
