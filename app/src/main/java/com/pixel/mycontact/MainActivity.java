package com.pixel.mycontact;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
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
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.Snackbar;
import com.pixel.mycontact.adapter.PeopleAdapter;
import com.pixel.mycontact.beans.IMMessage;
import com.pixel.mycontact.beans.People;
import com.pixel.mycontact.daos.RealmTransactions;
import com.pixel.mycontact.net.ClientListener;
import com.pixel.mycontact.services.ChatService;
import com.pixel.mycontact.utils.HashUtil;
import com.pixel.mycontact.utils.PeopleUrl;
import com.pixel.mycontact.utils.StyleUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView recyclerView;

    protected List<People> list;
    protected PeopleAdapter adapter;
    private TextView noContact;
    //    private PeopleDB peopleDB;
    private CoordinatorLayout cdntlayout;
    public static final int MESSAGE_ONLINE_USERS = 1;
    public static final int MESSAGE_NEW_MSG = 2;
    private Map<String, People> peopleMap;
    private ChatService.CommunicationBinder mBinder;


    private String targetIp;
    private Integer port;
    private boolean linkReady = false;
    private List<String> onlineCrc32List;
    private String myCrc32;
    private String me;
    private MainHandler mainHandler;
    private RealmTransactions realmTransactions;
    private RealmTransactions.Callback callback = new RealmTransactions.Callback() {
        @Override
        public void onSuccess() {
            Toast.makeText(getApplicationContext(), getString(R.string.contactsave), Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onFailed(String reason) {
            Toast.makeText(getApplicationContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (ChatService.CommunicationBinder) service;
            mBinder.connectMain(targetIp, port, mainHandler, myCrc32);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    protected void showNewMessage(IMMessage imMessage) {
        People people = peopleMap.get(imMessage.getMsgSource());
        if (people != null) {
            people.setDisplayMsg(imMessage.getMsgBody());
            people.addUnreadMsg();
            adapter.notifyDataSetChanged();
        }
    }

    protected void resolveMessage(List<String> list) {
        if (onlineCrc32List != null) clearOnlineStatus();
        onlineCrc32List = list;
        if (onlineCrc32List.size() > 0) {
            revealOnlineUsers(onlineCrc32List);
        }
    }

    private void clearOnlineStatus() {
        for (String string : onlineCrc32List) {
            People people = peopleMap.get(string);
            if (people != null) {
                people.setStatus(null);
            }
        }
    }

    private void revealOnlineUsers(List<String> onlineCrc32List) {
        for (String string : onlineCrc32List) {
            People people = peopleMap.get(string);
            if (people != null) {
                people.setStatus("Online");
            }
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initView
        initView();
//        peopleDB = new PeopleDB(MainActivity.this);
        //由peopledb类获取数据库联系人list，然后传入RecyclerView的适配器类
        realmTransactions = new RealmTransactions(ContactXApplication.getRealmInstance());
        list = new ArrayList<>();
        peopleMap = new HashMap<>();
        adapter = new PeopleAdapter(list, this);
        recyclerView.setAdapter(adapter);
        //URL启动的方式
        urlIntentResolve();
        startService(new Intent(this, ChatService.class));
        mainHandler = new MainHandler(this);
    }

    @Override
    protected void onResume() {
        //活动继续运行时，检查列表长度，若为空显示“无联系人”
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        targetIp = preferences.getString("server_ip", getString(R.string.pixelw_design));
        port = Integer.valueOf(preferences.getString("server_port", "9832"));
        String me_number = preferences.getString("me_number", "");
        if (TextUtils.isEmpty(me_number)) {
            Snackbar.make(cdntlayout, R.string.noinfo, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.settings, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(MainActivity.this, MeActivity.class));
                        }
                    }).show();
        } else {
            myCrc32 = HashUtil.toCrc32(me_number.getBytes());
            bindService(new Intent(getApplicationContext(), ChatService.class), connection, BIND_AUTO_CREATE);
        }
        refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        stopService(new Intent(getApplicationContext(), ChatService.class));
    }


    private void refreshOnline() {
        List<String> crc32List = new ArrayList<>();
        for (People people : list) {
            String crc = people.getCrc32();
            crc32List.add(crc);
            peopleMap.put(crc, people);
        }
        mBinder.sendUsersId(crc32List);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initView() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        StyleUtils.setStatusBarTransparent(getWindow(), toolbar, false);
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
                refresh();
            }
        });

        noContact = findViewById(R.id.noContact);
        cdntlayout = findViewById(R.id.cdntlayoutMain);
        recyclerView = findViewById(R.id.mainList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    //给菜单设置点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_me:
                startActivity(new Intent(MainActivity.this, MeActivity.class));
                break;
//            case R.id.menu_initdb:
//                if (peopleDB.checkdb()) {
//                    Snackbar.make(cdntlayout, "DB is ready", Snackbar.LENGTH_SHORT).show();
//                }
//                break;
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

    //获取 Toolbar上的 menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void urlIntentResolve() {
        Intent urlIntent = getIntent();
        if (urlIntent != null) {
            String intentAction = urlIntent.getAction();

            if (Intent.ACTION_VIEW.equals(intentAction)) {
                String urlString = urlIntent.getDataString();
                People peopleFromUrl;
                if (urlString != null) {
                    peopleFromUrl = PeopleUrl.parseUrl(urlString);

                    if (peopleFromUrl != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(R.string.foundcontact)
                                .setMessage(getString(R.string.addthis) + "\n" + peopleFromUrl.getName())
                                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        peopleDB = new PeopleDB(MainActivity.this);
                                        realmTransactions.insertAContact(peopleFromUrl, callback);
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

    private void refresh() {
        list.clear();
        list.addAll(realmTransactions.queryAll());
        //创建一个线程，处理动画和读取数据库的操作
        new Thread(new Runnable() {
            @Override
            public void run() {
//                list = peopleDB.queryAll(list);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        adapter.notifyDataSetChanged();
                        if (list.size() == 0) {
                            noContact.setVisibility(View.VISIBLE);
                        } else {
                            noContact.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                if (linkReady) {
                    mBinder.sayHello();
                    refreshOnline();
                }
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

    //不会内存泄漏
    private static class MainHandler extends Handler {
        WeakReference<MainActivity> activity;

        MainHandler(MainActivity mainActivity) {
            activity = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            MainActivity ac = activity.get();
            switch (msg.what) {
                case ClientListener.LINK_ESTABLISHED:
                    ac.linkReady = true;
                    ac.refreshOnline();
                    break;
                case MESSAGE_ONLINE_USERS:
                    ac.resolveMessage(msg.getData().getStringArrayList("1"));
                    break;
                case MESSAGE_NEW_MSG:
                    ac.showNewMessage((IMMessage) msg.obj);
                    break;
                default:
                    break;
            }
        }
    }

}
