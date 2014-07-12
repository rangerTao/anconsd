package com.ranger.lpa.thread;

import android.bluetooth.BluetoothSocket;

import java.net.Socket;

/**
 * Created by taoliang on 14-7-12.
 */
public class LPAClientThread extends Thread {

    private Socket mSocket;
    private BluetoothSocket bSocket;

    public LPAClientThread(Socket socket){

        mSocket = socket;
    }

    public LPAClientThread(BluetoothSocket socket){

        bSocket = socket;

    }

    @Override
    public void run() {
        super.run();

    }
}
