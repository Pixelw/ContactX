package com.pixel.mycontact.net;

import java.net.Socket;

public interface ClientListener {
    int LINK_ESTABLISHED = 100;
    int INCOMING_JSON = 101;
    int SERVER_CLOSED = -100;

    void onOpen(ClientSocketCore core, Socket socket);
    void onMessage(ClientSocketCore core, Socket socket, String msg);
    void onDisconnect(ClientSocketCore core, Socket socket);
    void onServerClosed(ClientSocketCore core);

}
