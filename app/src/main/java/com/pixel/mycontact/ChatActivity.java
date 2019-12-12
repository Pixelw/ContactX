package com.pixel.mycontact;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private static final String CLOSE_TOKEN = "JBdKZ7g7sub8bP3";
    private String targetIp;
    private int port;
    private EditText et_targetIp;
    private EditText et_ChatInput;
    //main socket
    private Socket socket;
    //need for listen
    private InputStream inputStream;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;
    //need for send
    private OutputStream outputStream;
    //TODO: 2019/12/11  线程池
    private ExecutorService mThreadPool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        et_targetIp = findViewById(R.id.et_targetip);
        et_ChatInput = findViewById(R.id.et_chatInput);
        port = 9832;

        ImageButton btn_send = findViewById(R.id.btn_chatSend);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = et_ChatInput.getText().toString();
                sendMsgViaSocket(message);
                et_ChatInput.setText("");
            }
        });

        targetIp = et_targetIp.getText().toString();
        mThreadPool = Executors.newCachedThreadPool();
        createSocket();

    }

    private void createSocket() {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("onCreate:", "try connect " + targetIp + ":" + port);
                    socket = new Socket(targetIp, port);
                    Log.d("onCreate:", "isConnected=" + socket.isConnected());
                    startListen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

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
        if (msg.equals(CLOSE_TOKEN)) {
            return false;
        } else {
            System.out.println(msg);
            return true;
        }
    }

    //
    private void showMessage(String msg) {

    }

    private void sendMsgViaSocket(final String message) {

        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    outputStream = socket.getOutputStream();
                    outputStream.write((message + "\n").getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                    if (message.equals(CLOSE_TOKEN)) {
                        closeConnections();
                    }
                } catch (Exception e) {
                    Log.e("send", "Error sending msg");
                    e.printStackTrace();
                }
            }
        });

    }

    private void closeConnections() throws IOException {
        outputStream.close();
        bufferedReader.close();
        socket.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sendMsgViaSocket(CLOSE_TOKEN);
    }
}
