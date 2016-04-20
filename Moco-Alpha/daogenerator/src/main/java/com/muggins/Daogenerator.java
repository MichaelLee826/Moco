package com.muggins;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class Daogenerator {
    public static void main(String[] args) throws Exception {
        // 两个参数分别代表：数据库版本号与自动生成代码的包路径。
        Schema schema = new Schema(9, "com.jishuli.Moco.PersistenceLayer");
        schema.setDefaultJavaPackageDao("com.jishuli.Moco.BusinessLogicLayer.dao");

        addProvince(schema);
        addSubject(schema);

        new DaoGenerator().generateAll(schema, "./app/src/main/java");
    }

    private static void addProvince(Schema schema) {
        Entity province = schema.addEntity("Province");
        province.addIdProperty();
        province.addLongProperty("identifier");
        province.addStringProperty("name");

        Entity city = schema.addEntity("City");
        city.addIdProperty();
        city.addLongProperty("identifier");
        city.addStringProperty("name");

        Property provinceID = city.addLongProperty("provinceID").notNull().getProperty();
        ToMany provinceToCities = province.addToMany(city, provinceID);
        provinceToCities.setName("cities");

        Entity county = schema.addEntity("County");
        county.addIdProperty();
        county.addLongProperty("identifier");
        county.addStringProperty("name");

        Property cityID = county.addLongProperty("cityID").notNull().getProperty();
        ToMany cityToCounties = city.addToMany(county, cityID);
        cityToCounties.setName("counties");

    }

    private static void addSubject(Schema schema) {
        Entity subject = schema.addEntity("Subject");
        subject.addIdProperty();
        subject.addLongProperty("identifier");
        subject.addStringProperty("name");


        Entity course = schema.addEntity("Course");
        course.addIdProperty();
        course.addLongProperty("identifier");
        course.addStringProperty("name");

        Property subjectID = course.addLongProperty("subjectID").notNull().getProperty();
        ToMany subjectToCourses = subject.addToMany(course, subjectID);
        subjectToCourses.setName("courses");
    }


}
