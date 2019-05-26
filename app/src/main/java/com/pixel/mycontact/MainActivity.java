package com.pixel.mycontact;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pixel.mycontact.beans.People;
import com.pixel.mycontact.daos.PeopleDB;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView recyclerView;

    protected List<People> list;
    protected PeopleAdapter adapter;
    private TextView noContact;

    private PeopleDB peopleDB;

    private CoordinatorLayout cdntlayout;

    //获取 Toolbar上的 menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //给菜单设置点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_initdb:
                if (peopleDB.checkdb()){
                    Snackbar.make(cdntlayout,"DB is ready",Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_copylist:
                copyList();
                break;
            case R.id.import_contact:
                Intent intent =  new Intent(MainActivity.this, ImportActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_about:
                Intent intenta = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intenta);
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
        //创建联系人入口（悬浮按钮）
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
                startActivity(intent);
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                            ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{Manifest.permission.CAMERA},10);
                    }else {
                        Intent intentQR = new Intent(MainActivity.this,QRCodeScanActivity.class);
                        startActivity(intentQR);
                    }
                return false;
            }
        });
        //下拉刷新功能
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDB();
            }
        });

        noContact = findViewById(R.id.noContact);
        cdntlayout = findViewById(R.id.cdntlayoutMain);
        recyclerView = findViewById(R.id.mainList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //由peopledb类获取数据库联系人list，然后传入RecyclerView的适配器类
        peopleDB = new PeopleDB(MainActivity.this);
        list = new ArrayList<>();
        list = peopleDB.queryAll(list);
        adapter = new PeopleAdapter(list);
        recyclerView.setAdapter(adapter);
//URL启动的方式
        urlIntentResolve();
    }

    private void urlIntentResolve() {
        Intent urlIntent = getIntent();
        if (urlIntent != null){
            String intentAction = urlIntent.getAction();

            if (Intent.ACTION_VIEW.equals(intentAction)){
                Uri intentData = urlIntent.getData();
                String json;
                if (intentData != null) {
                    Log.d("uri", intentData.toString());
                    json = intentData.getQueryParameter("json");
                    final People peopleFromUrl = PeopleResolver.resolveJson(json);
                    if (peopleFromUrl !=null){
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(R.string.foundcontact)
                                .setMessage(getString(R.string.addthis) + "\n" + peopleFromUrl.getName())
                                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        peopleDB = new PeopleDB(MainActivity.this);
                                        if (peopleDB.insertContact(peopleFromUrl) > 0) {
                                            Toast.makeText(getApplicationContext(), getString(R.string.contactsave), Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.cancelDia, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    }
                }

            }
        }
    }

    @Override
    protected void onResume() {
        //活动继续运行时，检查列表长度，若为空显示“无联系人”
        super.onResume();
        if (list.size() == 0){
            noContact.setVisibility(View.VISIBLE);
        }else{
            noContact.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onRestart() {
        //活动从暂停重新启动时，刷新列表
        super.onRestart();
        list.clear();
        list = peopleDB.queryAll(list);
        adapter.notifyDataSetChanged();
    }

    private void copyList() {
        //复制列表的项目，测试滑动功能；
        list = peopleDB.queryAll(list);
        adapter.notifyDataSetChanged();
    }

    private void refreshDB() {
        //创建一个线程，处理动画和读取数据库的操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                list.clear();
                list = peopleDB.queryAll(list);
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
