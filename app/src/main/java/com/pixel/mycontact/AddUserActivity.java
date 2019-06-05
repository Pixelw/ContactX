package com.pixel.mycontact;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.pixel.mycontact.beans.People;
import com.pixel.mycontact.daos.PeopleDB;

import java.util.Calendar;

public class AddUserActivity extends AppCompatActivity {

    private TextInputLayout nameText;
    private TextInputLayout lastNameText;
    private TextInputLayout numText;
    private TextInputLayout num2Text;
    private TextInputLayout emailText;
    private TextView dateAdd;
    private TextInputLayout noteAdd;
    private int birthYear;
    private int birthMonth;
    private int birthDay;

    private int nowYear;
    private int nowMonth;
    private int nowDay;

    private PeopleDB peopleDB;
    private People people;

    private boolean isModify = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.adduser_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = -39;//id 缺省值，不写入数据库
        //判断是否为修改模式，是修改模式的话id沿用数据库的id，方便数据库识别；
        if (isModify) {
            id = people.getId();
        }
        if (item.getItemId() == R.id.accept) {
            People savpeople = new People(
                    nameText.getEditText().getText().toString(),
                    lastNameText.getEditText().getText().toString(),
                    numText.getEditText().getText().toString(),
                    num2Text.getEditText().getText().toString(),
                    emailText.getEditText().getText().toString(),
                    birthYear, birthMonth, birthDay,
                    noteAdd.getEditText().getText().toString(),
                    id
            );
            Log.d("peopleToString", savpeople.toJSON());

            String emailInput = emailText.getEditText().getText().toString();
            if (nameText.getEditText().getText().toString().equals("")) {//判断至少填写一个电话一个姓名
                nameText.setErrorEnabled(true);
                nameText.setError(getString(R.string.missingname));
            }else if (numText.getEditText().getText().toString().equals("")
                    && num2Text.getEditText().getText().toString().equals("")) {
                numText.setErrorEnabled(true);
                numText.setError(getString(R.string.missingNumber));
            }else if (!emailInput.equals("") && !emailInput.contains("@")){
                emailText.setErrorEnabled(true);
                emailText.setError(getString(R.string.invalid_email));
            } else {
                if (isModify) {//修改模式，调用update方法

                    if (peopleDB.updateContact(savpeople) > 0) {
                        Toast.makeText(AddUserActivity.this, getString(R.string.contactsave),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }

                } else {//创建模式，调用insert方法；
                    if (peopleDB.insertContact(savpeople) > 0) {
                        Toast.makeText(AddUserActivity.this, getString(R.string.contactsave),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_user);
        Toolbar toolbar = findViewById(R.id.toolbarAdd);
        nameText = findViewById(R.id.nameAdd);
        lastNameText = findViewById(R.id.lastNameAdd);
        numText = findViewById(R.id.numAdd);
        num2Text = findViewById(R.id.num2Add);
        emailText = findViewById(R.id.emailAdd);
        dateAdd = findViewById(R.id.dateAdd);
        noteAdd = findViewById(R.id.noteAdd);

        //解析intent，如果有people对象则将对象的值填入文本框内，进入修改模式；
        Intent intent = getIntent();
        if (intent.hasExtra("people")) {
            people = (People) intent.getSerializableExtra("people");
            toolbar.setTitle(R.string.editcontact);
            toModify();
            isModify = true;
        } else {
            toolbar.setTitle(R.string.createcontact);
        }
        setSupportActionBar(toolbar);
        //返回键
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        peopleDB = new PeopleDB(AddUserActivity.this);
        //初始化日历，获取现在日期；
        Calendar calendar = Calendar.getInstance();
        nowYear = calendar.get(Calendar.YEAR);
        nowMonth = calendar.get(Calendar.MONTH);
        nowDay = calendar.get(Calendar.DAY_OF_MONTH);
        //创建选择日期对话框
        dateAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddUserActivity.this, onDateSetListener,
                        nowYear, nowMonth, nowDay).show();
            }
        });

    }

    @SuppressLint("getEditText().setTextI18n")
    //载入要修改的联系人
    private void toModify() {
        nameText.getEditText().setText(people.getFirstName());
        lastNameText.getEditText().setText(people.getLastName());
        numText.getEditText().setText(people.getNumber1());
        num2Text.getEditText().setText(people.getNumber2());
        emailText.getEditText().setText(people.getEmail());
        noteAdd.getEditText().setText(people.getNote());
        if (people.getBirthMonth() != 0) {
            nowDay = people.getBirthDay();
            nowMonth = people.getBirthMonth();
            nowYear = people.getBirthYear();
            dateAdd.setText(people.getBirthYear() + "/" + people.getBirthMonth() + "/" + people.getBirthDay());
        }
    }

    //选择日期操作，填入圆角矩形的TextView内
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            birthYear = year;
            birthMonth = month + 1;
            birthDay = dayOfMonth;
            String dateText = birthYear + "/" + birthMonth + "/" + birthDay;
            if (birthYear >= nowYear && birthMonth >= nowMonth && birthDay > nowDay) {
                Toast.makeText(AddUserActivity.this, getString(R.string.futurebirth),
                        Toast.LENGTH_SHORT).show();
            }
            dateAdd.setText(dateText);
        }
    };
}
