package com.pixel.mycontact.net;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

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

    private static final String CLOSE_TOKEN = "JBdKZ7g7sub8bP3";
    private static final int UPDATE_MSG = 10;
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
    private android.os.Handler mHandler;

    public ClientSocketCore(String serverIp, int port, Handler handler, String userID) {
        this.serverIp = serverIp;
        this.port = port;
        this.mHandler = handler;
        this.userID = userID;
        mThreadPool = Executors.newCachedThreadPool();
        createSocket();
    }

    private void createSocket() {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("onCreate:", "try connect " + serverIp + ":" + port);
                    socket = new Socket(serverIp, port);
                    Log.d("onCreate:", "isConnected=" + socket.isConnected());
                    hello();
                    startListen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void hello() {
        sendMsgViaSocket("Iam:" + userID);
    }

    private void startListen() {
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
                        Log.d("listen", "received:" + receivedMsg);
                    } while (receiveMessage(receivedMsg));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean receiveMessage(String msg) {
        Message message = new Message();
        message.what = UPDATE_MSG;
        message.obj = msg;
        mHandler.sendMessage(message);
        return !msg.equals(CLOSE_TOKEN);
    }


    //todo 线程问题
    public void sendMsgViaSocket(final String message) {
        try {
            outputStream = socket.getOutputStream();
            outputStream.write((message + "\n").getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            Log.e("send", "Error sending msg");
            e.printStackTrace();
        }
    }

    public void close() {
        if (socket != null && socket.isConnected()) {
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    sendMsgViaSocket(CLOSE_TOKEN);
                    try {
                        closeConnections();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

    }

    public void sendTextMessage(final String textMessage) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                sendMsgViaSocket(textMessage);
            }
        });
    }


    private void closeConnections() throws IOException {
        outputStream.close();
        bufferedReader.close();
        socket.close();
    }
}
