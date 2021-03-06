package com.taiclouds.Moke.AppClass;

public class ClassDetailItem {
    public String status;
    public String typeID;
    public String courseID;
    public int studentIn;
    public String teacherName;
    public String institutionName;
    public String courseName;
    public int score;
    public String beginTime;
    public String endTime;

    public ClassDetailItem(String status, String typeID, String courseID, int studentIn, String teacherName,String institutionName,
                           String courseName, int score, String beginTime, String endTime){
        this.status = status;
        this.typeID = typeID;
        this.courseID = courseID;
        this.studentIn = studentIn;
        this.teacherName = teacherName;
        this.institutionName = institutionName;
        this.courseName = courseName;
        this.score = score;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }
}