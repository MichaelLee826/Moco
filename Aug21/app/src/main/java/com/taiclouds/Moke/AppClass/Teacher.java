package com.taiclouds.Moke.AppClass;

import android.graphics.Bitmap;

/**
 * Created by MichaelLee826 on 2016-08-10-0010.
 */
public class Teacher {
    private Bitmap avatar;
    private String name;
    private String title;
    private String gender;

    public Teacher(Bitmap avatar, String name, String title, String gender) {
        this.avatar = avatar;
        this.name = name;
        this.title = title;
        this.gender = gender;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}

