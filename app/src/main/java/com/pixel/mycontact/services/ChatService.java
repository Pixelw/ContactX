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
import com.pixel.mycontact.MainActivity;
import com.pixel.mycontact.beans.IMMessage;
import com.pixel.mycontact.net.ClientListener;
import com.pixel.mycontact.net.ClientSocketCore;

import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatService extends Service implements ClientListener {

    private ClientSocketCore core;
    private String userId;
    private Handler mainHandler;
    private Handler chatHandler;
    private Gson gson = new Gson();

    private Map<String, List<IMMessage>> sessionMap;

    //clientlistener implementations
    @Override
    public void onOpen(ClientSocketCore core, Socket socket) {
        core.sendTextMessage("Iam:" + userId);
        mainHandler.sendEmptyMessage(LINK_ESTABLISHED);
    }

    private CommunicationBinder mBinder = new CommunicationBinder();

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (core != null){
            core.close();
        }
    }

    @Override
    public void onMessage(ClientSocketCore core, Socket socket, String msg) {
        handleIncomingJson(msg);
    }

    private void handleIncomingJson(String msg) {
        System.out.println("sss" + msg);
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
        if (list != null) {
            list.add(imMessage);
        } else {
            List<IMMessage> imMessageList = new ArrayList<>();
            imMessageList.add(imMessage);
            sessionMap.put(imMessage.getMsgSource(), imMessageList);
        }
        Message mainMessage = Message.obtain(mainHandler);
        mainMessage.what = MainActivity.MESSAGE_NEW_MSG;
        mainMessage.obj = imMessage;
        mainHandler.sendMessage(mainMessage);

        chatHandler.sendEmptyMessage(ChatActivity.UPDATE_MSG);
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

        public void sendUsersId(List<String> list){
            sendJsonMsg("usersCrc32", gson.toJsonTree(list));
        }

        public void connectMain(String serverIp, int port, Handler handler, String id) {
            if (core == null || !core.isConnected()) {
                core = new ClientSocketCore(serverIp, port, ChatService.this);
                core.connect();
            }
            userId = id;
            mainHandler = handler;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
