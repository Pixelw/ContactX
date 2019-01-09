package com.pixel.mycontact;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pixel.mycontact.beans.People;
import com.pixel.mycontact.daos.PeopleDB;
import com.pixel.mycontact.daos.SQLHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    protected SQLHelper dbHelper;
    protected SQLiteDatabase db;

    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView recyclerView;

    protected List<People> list;
    protected PeopleAdapter adapter;

    PeopleDB peopleDB;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_initdb:
                dbHelper = new SQLHelper(this, "Contacts.db", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if (db.isOpen()) {
                    Toast.makeText(MainActivity.this, "DB is ready", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_copylist:
                copyList();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
                startActivity(intent);
            }
        });
        dbHelper = new SQLHelper(this, "Contacts.db", null, 1);
        dbHelper.getWritableDatabase();
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDB();
            }
        });

        recyclerView = findViewById(R.id.mainList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        peopleDB = new PeopleDB();
        db = dbHelper.getReadableDatabase();
        list = new ArrayList<>();
        list = peopleDB.queryAll(db, list);
        adapter = new PeopleAdapter(list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        list.clear();
        list = peopleDB.queryAll(db, list);
        adapter.notifyDataSetChanged();
    }

    private void copyList() {
        list = peopleDB.queryAll(db, list);
        adapter.notifyDataSetChanged();
    }

    private void refreshDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("f5", "run: ");
                list.clear();
                list = peopleDB.queryAll(db, list);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        adapter.notifyDataSetChanged();
                    }
                });
                try {
                    Thread.sleep(1339);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();

    }

}
