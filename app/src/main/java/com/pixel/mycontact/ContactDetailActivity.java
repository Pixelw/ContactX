package com.pixel.mycontact;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pixel.mycontact.beans.DetailList;
import com.pixel.mycontact.beans.People;
import com.pixel.mycontact.daos.PeopleDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactDetailActivity extends AppCompatActivity {

    private PeopleDB peopleDB;
    private List<DetailList> details;
    private People people;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);

        RecyclerView recyclerView = findViewById(R.id.detail_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Intent intent = getIntent();

        people = (People) intent.getSerializableExtra("people");
        toolbar.setTitle(people.getName());
        setSupportActionBar(toolbar);

        initList();
        DetailAdapter adapter = new DetailAdapter(details);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "todo", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initList() {
        details = new ArrayList<>();
        DetailList phone = new DetailList(getString(R.string.phone_number), R.drawable.ic_phone_black_24dp, people.getNumber1());
        details.add(phone);
        if (!people.getEmail().equals("")){
            DetailList email = new DetailList(getString(R.string.email_address), R.drawable.ic_email_black_24dp, people.getEmail());
            details.add(email);
        }
        if (people.getBirthMonth() != 0){
            DetailList birth = new DetailList(getString(R.string.birthday), R.drawable.ic_date_range_black_24dp, people.getBirthMonth() + "/" + people.getBirthDay());
            details.add(birth);
        }

    }
}
