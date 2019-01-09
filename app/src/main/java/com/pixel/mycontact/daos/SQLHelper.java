package com.pixel.mycontact.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.pixel.mycontact.beans.People;


public class SQLHelper extends SQLiteOpenHelper {

    Context mContext;
    public SQLHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table Contacts (" +
                "id integer primary key autoincrement," +
                "name text,firstname text,lastname text," +
                "number1 text,number2 text,email text," +
                "birthYear integer,birthMonth integer,birthDay integer,note text)";
        db.execSQL(sql);

        if (db.isOpen()){
            Toast.makeText(mContext,"Created",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
