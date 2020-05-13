package com.pixel.mycontact.net;

import android.util.Log;

import com.pixel.mycontact.utils.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Carl Su
 * @date 2019/12/12
 */
public class ClientSocketCore {

    public static final String CLOSE_TOKEN = "JBdKZ7g7sub8bP3";
    private ClientListener clientListener;
    private String serverIp;
    private int port;
    private String userID;
    //main socket
    private Socket socket;
    //need for listen
    private InputStream inputStream;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;
    //need for send
    private OutputStream outputStream;
    private ExecutorService mThreadPool;
    private boolean connected = false;

    public ClientSocketCore(String serverIp, int port, ClientListener listener) {
        this.serverIp = serverIp;
        this.port = port;
        this.userID = userID;
        mThreadPool = Executors.newCachedThreadPool();
        clientListener = listener;
    }

    public void connect() {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.d("onCreate:", "try connect " + serverIp + ":" + port);
                    socket = new Socket(serverIp, port);
                    LogUtil.d("onCreate:", "isConnected=" + socket.isConnected());
                    startListen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startListen() {
        clientListener.onOpen(this,socket);
        connected = true;
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String receivedMsg;
                    do {
                        inputStream = socket.getInputStream();
                        inputStreamReader = new InputStreamReader(inputStream);
                        bufferedReader = new BufferedReader(inputStreamReader);
                        receivedMsg = bufferedReader.readLine();
                    } while (receiveMessage(receivedMsg));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean receiveMessage(String msg) {
        if (msg != null && msg.equals(CLOSE_TOKEN)){
            clientListener.onServerClosed(this);
            connected = false;
            return false;
        }
        clientListener.onMessage(this,socket,msg);
        return true;
    }

    public void sendTextMessage(final String textMessage) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                sendMsgViaSocket(textMessage);
            }
        });
    }

    private void sendMsgViaSocket(final String message) {
        try {
            Log.v("sending: ", message);
            outputStream = socket.getOutputStream();
            outputStream.write((message + "\n").getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            LogUtil.e("send", "Error sending msg");
            e.printStackTrace();
        }
    }


    public void close() {
        if (socket != null && socket.isConnected()) {
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    sendMsgViaSocket(CLOSE_TOKEN);
                    clientListener.onDisconnect(ClientSocketCore.this,socket);
                    connected = false;
                    try {
                        closeConnections();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public boolean isConnected(){
        return connected;
    }

    private void closeConnections() throws IOException {
        outputStream.close();
        bufferedReader.close();
        socket.close();
    }

}
