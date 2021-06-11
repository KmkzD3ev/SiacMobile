package br.com.zenitech.siacmobile.network;

import java.net.Socket;

public interface PrinterServerListener {
    public void onConnect(Socket socket);
}
