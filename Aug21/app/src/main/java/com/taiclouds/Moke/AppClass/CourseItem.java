package com.taiclouds.Moke.AppClass;

/**
 * Created by MichaelLee826 on 2016-03-26-0026.
 */
public class CourseItem {
    public String status;
    public String classDescription;
    public String img1;
    public String img2;
    public String districtID;
    public int studentIn;
    public String courseName;
    public String phoneNum;
    public String subjectID;
    public double price;
    public String beginTime;
    public String endTime;
    public String releaseTime;
    public String student;
    public String address;
    public String shareLink;
    public String teacherName;
    public String teacherTitle;
    public String typeID;
    public int studentMax;
    public String courseTime;
    public String addition;
    public String targetCustomer;
    public String courseContents;
    public String userID;
    public String comments;
    public String institutionID;
    public String institutionDescription;
    public String institutionName;
    public String cityID;
    public String courseID;
    public int score;
    public double lat;
    public double lng;

    public CourseItem(String status, String classDescription, String img1, String img2, String districtID, int studentIn, String courseName,
                      String phoneNum, String subjectID, double price, String beginTime, String endTime, String releaseTime, String student,
                      String address, String shareLink, String teacherName, String teacherTitle, String typeID, int studentMax, String courseTime,
                      String addition, String targetCustomer, String courseContents, String userID, String comments, String institutionID,
                      String institutionDescription, String institutionName, String cityID, String courseID,
                      int score, double lat, double lng){
        this.status = status;
        this.classDescription = classDescription;
        this.img1 = img1;
        this.img2 = img2;
        this.districtID = districtID;
        this.studentIn = studentIn;
        this.courseName = courseName;
        this.phoneNum = phoneNum;
        this.subjectID = subjectID;
        this.price = price;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.releaseTime = releaseTime;
        this.student = student;
        this.address = address;
        this.shareLink = shareLink;
        this.teacherName = teacherName;
        this.teacherTitle = teacherTitle;
        this.typeID = typeID;
        this.studentMax = studentMax;
        this.courseTime = courseTime;
        this.addition = addition;
        this.targetCustomer = targetCustomer;
        this.courseContents = courseContents;
        this.userID = userID;
        this.comments = comments;
        this.institutionID = institutionID;
        this.institutionDescription = institutionDescription;
        this.institutionName = institutionName;
        this.cityID = cityID;
        this.courseID =courseID;
        this.score = score;
        this.lat = lat;
        this.lng = lng;
    }
}
