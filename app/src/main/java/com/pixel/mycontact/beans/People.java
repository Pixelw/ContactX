package com.pixel.mycontact.beans;

import java.io.Serializable;

public class People implements Serializable {

    private int id;
    private String name;
    private String firstName;
    private String lastName;
    private String number1;
    private String number2;
    private String email;
    private int birthYear;
    private int birthMonth;
    private int birthDay;
    private String note;

    public People(String fn, String ln, String n1, String n2,String em,
                  int by, int bm, int bd,String nt,String n,int i) {


        if (!fn.equals("") && !ln.equals("")) {
            if (fn.matches("^[a-zA-Z]*") ||
                    ln.matches("^[a-zA-Z]*")) {
                n = fn + " " + ln;
            } else {
                n = ln + " " + fn;
            }
        }else{
            n = fn;
        }

        this.id = i;
        this.name = n;
        this.firstName = fn;
        this.lastName = ln;
        this.number1 = n1;
        this.number2 = n2;
        this.email = em;
        this.note = nt;
        this.birthYear = by;
        this.birthMonth = bm;
        this.birthDay = bd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNumber1() {
        return number1;
    }

    public void setNumber1(String number1) {
        this.number1 = number1;
    }

    public String getNumber2() {
        return number2;
    }

    public void setNumber2(String number2) {
        this.number2 = number2;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public int getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(int birthMonth) {
        this.birthMonth = birthMonth;
    }

    public int getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(int birthDay) {
        this.birthDay = birthDay;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
