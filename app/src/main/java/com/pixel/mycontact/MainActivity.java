package com.pixel.mycontact;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.Snackbar;
import com.pixel.mycontact.adapter.PeopleAdapter;
import com.pixel.mycontact.beans.People;
import com.pixel.mycontact.daos.PeopleDB;
import com.pixel.mycontact.utils.PeopleUrl;
import com.pixel.mycontact.utils.StyleUtils;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        StyleUtils.setStatusBarTransparent(getWindow(), toolbar,false);
        FloatingActionMenu fabMenu = findViewById(R.id.fab_menu);
        fabMenu.setClosedOnTouchOutside(true);
        com.github.clans.fab.FloatingActionButton fabNew = findViewById(R.id.fab_new);
        com.github.clans.fab.FloatingActionButton fabScan = findViewById(R.id.fab_scan);
        fabMenu.setIconTint(getResources().getColor(R.color.colorCommonWhite));
        //创建联系人入口（悬浮按钮）
        fabNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
                startActivity(intent);
            }
        });

        fabScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 10);
                } else {
                    Intent intentQR = new Intent(MainActivity.this, QRCodeScanActivity.class);
                    startActivity(intentQR);
                }
            }
        });


        //下拉刷新功能
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorSecondary);
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

    //获取 Toolbar上的 menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //给菜单设置点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_initdb:
                if (peopleDB.checkdb()) {
                    Snackbar.make(cdntlayout, "DB is ready", Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_chat:
                startActivity(new Intent(MainActivity.this, ChatActivity.class));
                break;
            case R.id.menu_copylist:
                copyList();
                break;
            case R.id.menu_import_contact:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
                } else {
                    startActivity(new Intent(MainActivity.this, ImportActivity.class));
                }
                break;
            case R.id.menu_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.menu_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void urlIntentResolve() {
        Intent urlIntent = getIntent();
        if (urlIntent != null) {
            String intentAction = urlIntent.getAction();

            if (Intent.ACTION_VIEW.equals(intentAction)) {
                String string = urlIntent.getDataString();
                People peopleFromUrl;
                if (string != null) {
                    peopleFromUrl = PeopleUrl.parseUrl(string);

                    if (peopleFromUrl != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        final People finalPeopleFromUrl = peopleFromUrl;
                        builder.setTitle(R.string.foundcontact)
                                .setMessage(getString(R.string.addthis) + "\n" + peopleFromUrl.getName())
                                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        peopleDB = new PeopleDB(MainActivity.this);
                                        if (peopleDB.insertContact(finalPeopleFromUrl) > 0) {
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
                    } else {
                        Snackbar.make(cdntlayout, getString(R.string.invalidqr), Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MainActivity.this, ImportActivity.class);
                startActivity(intent);
            } else {
                Snackbar.make(cdntlayout, R.string.perde, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }

    @Override
    protected void onResume() {
        //活动继续运行时，检查列表长度，若为空显示“无联系人”
        super.onResume();
        if (list.size() == 0) {
            noContact.setVisibility(View.VISIBLE);
        } else {
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
