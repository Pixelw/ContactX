package com.pixel.mycontact;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.pixel.mycontact.adapter.ClassicChatAdapter;
import com.pixel.mycontact.beans.IMMessage;
import com.pixel.mycontact.services.ChatService;
import com.pixel.mycontact.utils.HashUtil;
import com.pixel.mycontact.utils.LogUtil;
import com.pixel.mycontact.utils.StyleUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private EditText et_ChatInput;
    private Gson gson;
    private String me = "";  //user itself
    private RecyclerView recyclerView;
    private ClassicChatAdapter chatAdapter;
    //receive incoming message from ClientSocketCore with handler
    private List<IMMessage> chatList;
    private ChatService.CommunicationBinder mBinder;
    public static final int NEW_MESSAGE = 200;
    private ChatHandler chatHandler;
    private String userId;
    private String opponent;
    private String myCrc32;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (ChatService.CommunicationBinder) service;
            if (userId != null) {
                chatList = mBinder.connectChat(userId, chatHandler);
                chatAdapter = new ClassicChatAdapter(chatList,opponent,myCrc32);
                recyclerView.setAdapter(chatAdapter);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d("", "onServiceDisconnected: ");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        gson = ContactXApplication.getGsonInstance();
        userId = getIntent().getStringExtra("userId");
        opponent = getIntent().getStringExtra("userName");
        chatHandler = new ChatHandler(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        me = preferences.getString("me_name", "");
        myCrc32 = HashUtil.toCrc32(preferences.getString("me_number", "").getBytes());

        initView();
        bindService(new Intent(getApplicationContext(), ChatService.class), connection, BIND_AUTO_CREATE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_detail){
            Intent intent = new Intent(ChatActivity.this,ContactDetailActivity.class);
            intent.putExtra("people", getIntent().getSerializableExtra("people"));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        et_ChatInput = findViewById(R.id.et_chatInput);

        recyclerView = findViewById(R.id.recyclerView_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        chatList = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbarChat);
        toolbar.setTitle(R.string.chat);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton btn_send = findViewById(R.id.btn_chatSend);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String userStr = et_targetUser.getText().toString();
                chatAdapter.getItemCount();
                String message = et_ChatInput.getText().toString();
                if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(message)) {
                    sendMsgObj(message, userId);
                }
                et_ChatInput.setText("");
            }
        });
        int currentMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && currentMode == Configuration.UI_MODE_NIGHT_NO) {
            StyleUtils.setStatusBarLightModeM(getWindow());
        }
    }

    private void showNewMessage(int after) {
        chatAdapter.notifyItemInserted(after);
        recyclerView.smoothScrollToPosition(after);
    }

    private void sendMsgObj(String msgBody, String targetUser) {
        long time = System.currentTimeMillis();
        IMMessage imMessage = new IMMessage();
        imMessage.setMsgBody(msgBody);
        imMessage.setMsgTime(time);
        imMessage.setMsgDestination(targetUser);
        imMessage.setMsgUser(me);
        imMessage.setMsgSource(myCrc32);
        int after;
        chatList.add(imMessage);
        after = chatList.indexOf(imMessage);
        //gson 序列化
        JsonElement msgElement = gson.toJsonTree(imMessage, IMMessage.class);
        mBinder.sendJsonMsg("chatMsg", msgElement);
        showNewMessage(after);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    private static class ChatHandler extends Handler {
        WeakReference<ChatActivity> activity;

        ChatHandler(ChatActivity chatActivity) {
            activity = new WeakReference<>(chatActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            ChatActivity ac = activity.get();
            switch (msg.what) {
                case NEW_MESSAGE:
                    ac.showNewMessage(msg.arg2);
                    break;
                default:
                    break;
            }
        }
    }

}
