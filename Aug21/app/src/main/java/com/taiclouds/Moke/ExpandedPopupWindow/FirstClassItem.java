package com.taiclouds.Moke.ExpandedPopupWindow;

import java.util.List;

/**
 * Created by MichaelLee826 on 2016-07-05-0005.
 */
public class FirstClassItem {
    private long id;
    private String name;
    private List<SecondClassItem> secondList;

    public FirstClassItem(){

    }

    public FirstClassItem(long id, String name, List<SecondClassItem> secondList) {
        this.id = id;
        this.name = name;
        this.secondList = secondList;
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

    public List<SecondClassItem> getSecondList() {
        return secondList;
    }

    public void setSecondList(List<SecondClassItem> secondList) {
        this.secondList = secondList;
    }

    @Override
    public String toString() {
        return "FirstClassItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", secondList=" + secondList +
                '}';
    }
}