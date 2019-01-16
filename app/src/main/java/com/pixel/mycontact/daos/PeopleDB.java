package com.pixel.mycontact.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pixel.mycontact.beans.People;

import java.util.List;

            public class PeopleDB {

                private SQLiteDatabase db;

                public PeopleDB(Context context) {
                    SQLHelper dbHelper = new SQLHelper(context, "Contacts.db", null, 1);
                    db = dbHelper.getWritableDatabase();
                }

                //导入系统联系人
                public long insertSysContacts(List<People> list) {
                    long d = 0;
                    for (int p = 0; p < list.size(); p++) {
                        People people = list.get(p);
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
            d += db.insert("Contacts", null, values);
        }
        return d;
    }

    //创建联系人
    public long insertContact(People people) {
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
    //查询所有联系人
    public List<People> queryAll(List<People> peopleList) {
        Cursor cursor = db.query("Contacts", null, null, null,
                null, null, "id");
        int id;
        String firstName;
        String lastName;
        String number1;
        String number2;
        String email;
        int birthYear;
        int birthMonth;
        int birthDay;
        String note;
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"));
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
                        firstName, lastName, number1, number2, email, birthYear,
                        birthMonth, birthDay, note, id
                );
                peopleList.add(people);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return peopleList;
    }
    //修改更新联系人
    public long updateContact(People people) {
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
        r = db.update("Contacts", values, "id=?", new String[]{people.getId() + ""});
        return r;
    }
//删除联系人
    public long deleteContact(int id) {
        return db.delete("Contacts", "id=?", new String[]{id + ""});
    }

    public boolean checkdb() {
        return db.isOpen();
    }


}
