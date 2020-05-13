package com.pixel.mycontact.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.pixel.mycontact.ChatActivity;
import com.pixel.mycontact.ContactXApplication;
import com.pixel.mycontact.MainActivity;
import com.pixel.mycontact.beans.IMMessage;
import com.pixel.mycontact.daos.RealmTransactions;
import com.pixel.mycontact.net.ClientListener;
import com.pixel.mycontact.net.ClientSocketCore;
import com.pixel.mycontact.utils.LogUtil;

import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatService extends Service implements ClientListener {

    private static final String TAG = "ChatService";
    private ClientSocketCore core;
    private String userId;
    private Handler mainHandler;
    private Handler chatHandler;
    private RealmTransactions realmTransactions;
    private Gson gson = new Gson();

    private Map<String, List<IMMessage>> sessionMap;
    private List<IMMessage> imMessageAll;
    private CommunicationBinder mBinder = new CommunicationBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        sessionMap = new HashMap<>();
        realmTransactions = new RealmTransactions(ContactXApplication.getRealmInstance());
        imMessageAll = realmTransactions.loadMessages();

    }

    private void sessionRestorer(String myCrc32) {
        for (IMMessage imMessage : imMessageAll) {
            //判断session
            String id = imMessage.getMsgSource().equals(myCrc32) ?
                    imMessage.getMsgDestination() : imMessage.getMsgSource();
            List<IMMessage> imMessageList1 = sessionMap.get(id);
            if (imMessageList1 == null) {
                imMessageList1 = new ArrayList<>();
                imMessageList1.add(imMessage);
                sessionMap.put(id, imMessageList1);
            } else {
                imMessageList1.add(imMessage);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy triggered");
        if (core != null) {
            core.close();
        }
        for (List<IMMessage> imMessageList : sessionMap.values()) {
            realmTransactions.saveMessages(imMessageList);
        }
    }

    //clientlistener implementations
    @Override
    public void onOpen(ClientSocketCore core, Socket socket) {
        mBinder.sayHello();
        mainHandler.sendEmptyMessage(LINK_ESTABLISHED);
    }

    @Override
    public void onMessage(ClientSocketCore core, Socket socket, String msg) {
        LogUtil.v(TAG, "receive:" + msg);
        handleIncomingJson(msg);
    }

    private void handleIncomingJson(String msg) {
        JsonObject jsonObject;
        try {
            jsonObject = JsonParser.parseString(msg).getAsJsonObject();
            if (jsonObject.has("onlineUsersCrc32")) {
                JsonElement element = jsonObject.getAsJsonArray("onlineUsersCrc32");
                Type type = new TypeToken<List<String>>() {
                }.getType();
                List<String> onlineList = gson.fromJson(element, type);
                Message message = Message.obtain(mainHandler);
                message.what = MainActivity.MESSAGE_ONLINE_USERS;
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("1", (ArrayList<String>) onlineList);
                message.setData(bundle);
                mainHandler.sendMessage(message);
            } else if (jsonObject.has("chatMsg")) {
                JsonElement element = jsonObject.getAsJsonObject("chatMsg");
                IMMessage imMessage = gson.fromJson(element, IMMessage.class);
                handleIncomingMessage(imMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleIncomingMessage(IMMessage imMessage) {
        List<IMMessage> list = sessionMap.get(imMessage.getMsgSource());
        int afterAdd;
        if (list != null) {
            list.add(imMessage);
            afterAdd = list.indexOf(imMessage);
        } else {
            List<IMMessage> imMessageList = new ArrayList<>();
            imMessageList.add(imMessage);
            afterAdd = imMessageList.indexOf(imMessage);
            sessionMap.put(imMessage.getMsgSource(), imMessageList);
        }
        if (mainHandler != null) {
            Message mainMessage = Message.obtain(mainHandler);
            mainMessage.what = MainActivity.MESSAGE_NEW_MSG;
            mainMessage.obj = imMessage;
            mainHandler.sendMessage(mainMessage);
        }
        if (chatHandler != null) {
            Message chatMessage = Message.obtain(chatHandler);
            chatMessage.what = ChatActivity.NEW_MESSAGE;
            chatMessage.arg2 = afterAdd;
            chatHandler.sendMessage(chatMessage);
        }
    }

    @Override
    public void onDisconnect(ClientSocketCore core, Socket socket) {

    }

    @Override
    public void onServerClosed(ClientSocketCore core) {

    }

    public class CommunicationBinder extends Binder {
        public void sendTextMsg(String msg) {
            core.sendTextMessage(msg);
        }

        public void sendJsonMsg(String msgType, JsonElement jsonElement) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add(msgType, jsonElement);
            core.sendTextMessage(jsonObject.toString());
        }

        public void sayHello() {
            core.sendTextMessage("Iam:" + userId);
        }

        public void sendUsersId(List<String> list) {
            sendJsonMsg("usersCrc32", gson.toJsonTree(list));
        }

        public void connectMain(String serverIp, int port, Handler handler, String id) {
            if (core == null || !core.isConnected()) {
                core = new ClientSocketCore(serverIp, port, ChatService.this);
                core.connect();
            }
            userId = id;
            mainHandler = handler;
            sessionRestorer(id);
        }

        public List<IMMessage> connectChat(String userId, Handler handler) {
            chatHandler = handler;
            if (sessionMap.get(userId) == null) {
                List<IMMessage> list = new ArrayList<>();
                sessionMap.put(userId, list);
            }
            return sessionMap.get(userId);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
