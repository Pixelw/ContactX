package com.pixel.mycontact.beans;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.pixel.mycontact.utils.HashUtil;
import com.pixel.mycontact.utils.StringUtils;

import java.io.Serializable;

/*
 *  people实体类，实现序列化
 *
 */
public class People implements Serializable {

    private int i;
    @Expose
    private String n;
    @Expose
    private String f;
    @Expose
    private String l;
    @Expose
    private String n1;
    @Expose
    private String n2;
    @Expose
    private String e;
    @Expose
    private int y;
    @Expose
    private int m;
    @Expose
    private int d;
    @Expose
    private String no;


    private Boolean selected;
    private String status;
    private String crc32;
    private String displayMsg;
    private int unreadMsg = 0;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public People(String firstName, String lastName, String number1, String number2, String email,
                  int birthYear, int birthMonth, int birthDay, String note, int id) {

        String name;
        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)) {
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

    public String getName() {
        return n;
    }

    public String getFirstName() {
        return f;
    }

    public String getLastName() {
        return l;
    }

    public String getNumber1() {
        return n1;
    }

    public String getNumber2() {
        return n2;
    }

    public String getNumber() {
//   返回一个号码，有两个优先返回1，没有返回"unknown"
        if (!TextUtils.isEmpty(n1)) {
            return StringUtils.getTrimmedNumber(n1);
        } else if (!TextUtils.isEmpty(n2)) {
            return StringUtils.getTrimmedNumber(n2);
        } else {
            return "unknown";
        }

    }

    public String getNote() {
        return no;
    }

    public void appendNote(String text) {
        no = no + "\n" + text;
    }

    public int getBirthYear() {
        return y;
    }

    public int getBirthMonth() {
        return m;
    }

    public int getBirthDay() {
        return d;
    }

    public String getEmail() {
        return e;
    }

    public Boolean isSelected() {
        return selected;
    }

    public void isSelected(Boolean checked) {
        selected = checked;
    }

    public String getCrc32() {
        if (!getNumber().equals("unknown")) {
            if (TextUtils.isEmpty(crc32)) {
                String s = HashUtil.toCrc32(getNumber().getBytes());
                setCrc32(s);
                return s;
            } else {
                return crc32;
            }
        }
        return null;
    }

    private void setCrc32(String crc32) {
        this.crc32 = crc32;
    }

    public String getDisplayMsg() {
        return displayMsg;
    }

    public void setDisplayMsg(String displayMsg) {
        this.displayMsg = displayMsg;
    }

    public int getUnreadMsg() {
        return unreadMsg;
    }

    public void addUnreadMsg(){
        this.unreadMsg++;
    }
}
