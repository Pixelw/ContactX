package com.pixel.mycontact.beans;

import java.io.Serializable;

/*
 *  people实体类，实现序列化
 *
 */
public class People implements Serializable {

    private int i;
    private String n;
    private String f;
    private String l;
    private String n1;
    private String n2;
    private String e;
    private int y;
    private int m;
    private int d;
    private String no;
    private Boolean isChecked = false;

    public People(String firstName, String lastName, String number1, String number2, String email,
                  int birthYear, int birthMonth, int birthDay, String note, int id) {

        String name;
        if (!firstName.equals("") && !lastName.equals("")) {
            if (firstName.matches("^[a-zA-Z]*") ||
                    lastName.matches("^[a-zA-Z]*")) {
                name = firstName + " " + lastName;
            } else {
                name = lastName + " " + firstName;
            }
        } else {
            name = firstName;
        }

        this.i = id;
        this.n = name;
        this.f = firstName;
        this.l = lastName;
        this.n1 = number1;
        this.n2 = number2;
        this.e = email;
        this.no = note;
        this.y = birthYear;
        this.m = birthMonth;
        this.d = birthDay;
    }


    public int getId() {
        return i;
    }

    public void setId(int id) {
        this.i = id;
    }

    public String getName() {
        return n;
    }

    public void setName(String name) {
        this.n = name;
    }

    public String getFirstName() {
        return f;
    }

    public void setFirstName(String firstName) {
        this.f = firstName;
    }

    public String getLastName() {
        return l;
    }

    public void setLastName(String lastName) {
        this.l = lastName;
    }

    public String getNumber1() {
        return n1;
    }

    public void setNumber1(String number1) {
        this.n1 = number1;
    }

    public String getNumber2() {
        return n2;
    }

    public void setNumber2(String number2) {
        this.n2 = number2;
    }

    public String getNumber() {
//   返回一个号码，有两个优先返回1，没有返回"unknown"
        if (!n1.equals("")){
            return n1;
        }else if (!n2.equals("")){
            return n2;
        }else {
            return "unknown";
        }

    }

    public String getNote() {
        return no;
    }

    public void setNote(String note) {
        this.no = note;
    }

    public void appendNote(String text) {
        no = no + "\n" + text;
    }

    public int getBirthYear() {
        return y;
    }

    public void setBirthYear(int birthYear) {
        this.y = birthYear;
    }

    public int getBirthMonth() {
        return m;
    }

    public void setBirthMonth(int birthMonth) {
        this.m = birthMonth;
    }

    public int getBirthDay() {
        return d;
    }

    public void setBirthDay(int birthDay) {
        this.d = birthDay;
    }

    public String getEmail() {
        return e;
    }

    public void setEmail(String email) {
        this.e = email;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "{"+ "\"i\":"
                + i
                + ",\"n\":\""
                + n + '\"'
                + ",\"f\":\""
                + f + '\"'
                + ",\"l\":\""
                + l + '\"'
                + ",\"n1\":\""
                + n1 + '\"'
                + ",\"n2\":\""
                + n2 + '\"'
                + ",\"e\":\""
                + e + '\"'
                + ",\"y\":"
                + y
                + ",\"m\":"
                + m
                + ",\"d\":"
                + d
                + ",\"no\":\""
                + no + "\"}";
    }
}
