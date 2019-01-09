package com.pixel.mycontact;

import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pixel.mycontact.beans.People;
import com.pixel.mycontact.daos.PeopleDB;
import com.pixel.mycontact.daos.SQLHelper;

import java.util.Calendar;

public class AddUserActivity extends AppCompatActivity {

    protected SQLHelper dbHelper;
    protected SQLiteDatabase db;

    private EditText nameText;
    private EditText lastNameText;
    private EditText numText;
    private EditText num2Text;
    private EditText emailText;
    private TextView dateAdd;
    private EditText noteAdd;
    private int birthYear;
    private int birthMonth;
    private int birthDay;

    private int nowYear;
    private int nowMonth;
    private int nowDay;

    private PeopleDB peopleDB;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.adduser_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accept:
                People people = new People(
                        nameText.getText().toString(),
                        lastNameText.getText().toString(),
                        numText.getText().toString(),
                        num2Text.getText().toString(),
                        emailText.getText().toString(),
                        birthYear, birthMonth, birthDay,
                        noteAdd.getText().toString(),
                        null, 1039
                );
                if (nameText.getText().toString().equals("")) {
                    if (num2Text.getText().toString().equals("") || numText.getText().toString().equals(""))
                        Toast.makeText(AddUserActivity.this,
                                getString(R.string.missingname), Toast.LENGTH_SHORT).show();
                } else {
                    if (peopleDB.insertContact(people, db) > 0) {
                        Toast.makeText(AddUserActivity.this, getString(R.string.contactsave),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        Toolbar toolbar = findViewById(R.id.toolbarAdd);
        toolbar.setTitle(R.string.createcontact);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nameText = findViewById(R.id.nameAdd);
        lastNameText = findViewById(R.id.lastNameAdd);
        numText = findViewById(R.id.numAdd);
        num2Text = findViewById(R.id.num2Add);
        emailText = findViewById(R.id.emailAdd);
        dateAdd = findViewById(R.id.dateAdd);
        noteAdd = findViewById(R.id.noteAdd);

        dbHelper = new SQLHelper(this, "Contacts.db", null, 1);
        db = dbHelper.getWritableDatabase();

        peopleDB = new PeopleDB();

        Calendar calendar = Calendar.getInstance();
        nowYear = calendar.get(Calendar.YEAR);
        nowMonth = calendar.get(Calendar.MONTH);
        nowDay = calendar.get(Calendar.DAY_OF_MONTH);

        dateAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddUserActivity.this, onDateSetListener,
                        nowYear, nowMonth, nowDay).show();
            }
        });
    }

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            birthYear = year;
            birthMonth = month + 1;
            birthDay = dayOfMonth;
            String dateText = birthYear + "/" + birthMonth + "/" + birthDay;
            if (birthYear >= nowYear && birthMonth >= nowMonth && birthDay > nowDay) {
                Toast.makeText(AddUserActivity.this, getString(R.string.futurebirth), Toast.LENGTH_SHORT).show();
            }
            dateAdd.setText(dateText);
        }
    };
}
