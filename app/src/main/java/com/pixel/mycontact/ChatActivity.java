package com.pixel.mycontact;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixel.mycontact.adapter.ClassicChatAdapter;
import com.pixel.mycontact.beans.IMMessage;
import com.pixel.mycontact.services.ChatService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {


    private static final int UPDATE_MSG = 10;
    private EditText et_ChatInput;
    private EditText et_targetUser;
    private Gson gson;
    private String me = "";
    private RecyclerView recyclerView;
    private ClassicChatAdapter chatAdapter;
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE_MSG:
                    resolveMessage((String) message.obj);
                    break;
                default:
                    break;
            }
        }
    };
    private List<IMMessage> chatList;
    private ChatService.CommunicationBinder mBinder;
    private String targetIp;
    private int port;

    private void resolveMessage(String obj) {
        IMMessage imMessage = gson.fromJson(obj, IMMessage.class);
        showNewMessage(imMessage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd H:mm:ss")
                .create();
        TextView tv_targetIP = findViewById(R.id.tv_targetip);
        et_ChatInput = findViewById(R.id.et_chatInput);
        et_targetUser = findViewById(R.id.et_targetUser);

        recyclerView = findViewById(R.id.recyclerView_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        chatList = new ArrayList<>();
        chatAdapter = new ClassicChatAdapter(chatList);
        recyclerView.setAdapter(chatAdapter);

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
                String userStr = et_targetUser.getText().toString();
                String message = et_ChatInput.getText().toString();
//                StringUtils.gzipString(message);
                sendMsgObj(message, userStr);
                et_ChatInput.setText("");
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        targetIp = preferences.getString("server_ip", getString(R.string.pixelw_design));
        me = preferences.getString("nickname", "Unknown");
        port = Integer.valueOf(preferences.getString("server_port", "9999"));
        String displayServer = targetIp + ":" + port;
        tv_targetIP.setText(displayServer);


        bindService(new Intent(getApplicationContext(), ChatService.class), connection, BIND_AUTO_CREATE);


    }

    private void sendMsgObj(String msgBody, String targetUser) {

        Date nowDate = new Date();
        IMMessage imMessage = new IMMessage();
        imMessage.setMsgBody(msgBody);
        imMessage.setMsgTime(nowDate);
        imMessage.setMsgDestination(targetUser);
        imMessage.setMsgUser(me);
        String strJson = gson.toJson(imMessage);
        Log.d("Gson.toJson", strJson);
        mBinder.sendTextMsg(strJson);
        showNewMessage(imMessage);

    }

    private void showNewMessage(IMMessage imMessage) {
        int position = chatAdapter.add(imMessage);
        //滚动到最新消息
        recyclerView.smoothScrollToPosition(position);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (ChatService.CommunicationBinder) service;
            mBinder.createSocket(targetIp, port, handler, me);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("", "onServiceDisconnected: ");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

}
