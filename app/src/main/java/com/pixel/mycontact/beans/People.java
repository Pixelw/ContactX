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
        //set strings to "" rather than null
        this.l = lastName == null ? "" : lastName;
        this.n1 = number1 == null ? "" : number1;
        this.n2 = number2 == null ? "" : number2;
        this.e = email == null ? "" : email;
        this.no = note == null ? "" : note;
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
        if (!n1.equals("")) {
            return n1;
        } else if (!n2.equals("")) {
            return n2;
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

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public String toJSON() {
        String adaptiveJson = "[{"
                + "\"f\":\"" + f + '\"';
        if (l.length() > 0) {
            adaptiveJson += ",\"l\":\"" + l + '\"';
        }
        if (n1.length() > 0) {
            adaptiveJson += ",\"n1\":\"" + n1 + '\"';
        }
        if (n2.length() > 0) {
            adaptiveJson += ",\"n2\":\"" + n2 + '\"';
        }
        if (e.length() > 0) {
            adaptiveJson += ",\"e\":\"" + e + '\"';
        }
        if (y != 0 || m != 0 || d != 0) {
            adaptiveJson += ",\"y\":" + y + ",\"m\":" + m
                    + ",\"d\":" + d;
        }
        if (no.length() > 0) {
            adaptiveJson += ",\"no\":\"" + no + '\"';
        }

        adaptiveJson += "}]";

        return adaptiveJson;
    }
}
