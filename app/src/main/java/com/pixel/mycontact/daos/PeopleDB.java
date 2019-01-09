package com.pixel.mycontact.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.strictmode.SqliteObjectLeakedViolation;

import com.pixel.mycontact.R;
import com.pixel.mycontact.beans.DetailList;
import com.pixel.mycontact.beans.People;

import java.util.ArrayList;
import java.util.List;

public class PeopleDB {


    private List<DetailList> details = null;

    public long insertContact(People people, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("name", people.getName());
        values.put("firstname", people.getFirstName());
        values.put("lastname", people.getLastName());
        values.put("number1", people.getNumber1());
        values.put("number2", people.getNumber2());
        values.put("email", people.getEmail());
        values.put("birthYear", people.getBirthYear());
        values.put("birthmonth", people.getBirthMonth());
        values.put("birthDay", people.getBirthDay());
        values.put("note", people.getNote());
        long r;
        r = db.insert("Contacts", null, values);
        return r;
    }

    public List<People> queryAll(SQLiteDatabase db,List<People> peopleList ) {
        Cursor cursor = db.query("Contacts", null, null, null,
                null, null, "id");
        int id;
        String name;
        String firstName;
        String lastName;
        String number1;
        String number2;
        String email;
        int birthYear;
        int birthMonth;
        int birthDay;
        String note;
       // = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"));
                name = cursor.getString(cursor.getColumnIndex("name"));
                firstName = cursor.getString(cursor.getColumnIndex("firstname"));
                lastName = cursor.getString(cursor.getColumnIndex("lastname"));
                number1 = cursor.getString(cursor.getColumnIndex("number1"));
                number2 = cursor.getString(cursor.getColumnIndex("number2"));
                email = cursor.getString(cursor.getColumnIndex("email"));
                birthYear = cursor.getInt(cursor.getColumnIndex("birthYear"));
                birthMonth = cursor.getInt(cursor.getColumnIndex("birthMonth"));
                birthDay = cursor.getInt(cursor.getColumnIndex("birthDay"));
                note = cursor.getString(cursor.getColumnIndex("note"));
                People people = new People(
                        firstName,lastName,number1,number2,email,birthYear,
                        birthMonth,birthDay,note,name,id
                );
                peopleList.add(people);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return peopleList;
    }


}
