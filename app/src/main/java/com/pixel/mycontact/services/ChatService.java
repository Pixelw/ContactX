package com.pixel.mycontact.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.pixel.mycontact.net.ClientSocketCore;

public class ChatService extends Service {

    private ClientSocketCore core;

    public class CommunicationBinder extends Binder{
        public void sendTextMsg(String msg){
            core.sendTextMessage(msg);
        }

        public void createSocket(String serverIp, int port, Handler handler, String userID){
            core=new ClientSocketCore(serverIp, port, handler, userID);
        }
    }
    private CommunicationBinder mBinder = new CommunicationBinder();

    @Override
    public void onDestroy() {
        super.onDestroy();
        core.close();
    }

    public ChatService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
