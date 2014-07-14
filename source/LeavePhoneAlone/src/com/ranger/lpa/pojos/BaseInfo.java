package com.ranger.lpa.pojos;

import java.net.Socket;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.google.gson.Gson;
import com.ranger.lpa.receiver.BlueToothReceiver;

public class BaseInfo {

    public BaseInfo(int errorcode) {
        errcode = errorcode;
    }

    //Stop the server
    public static final int MSG_STOPSERVER = 2;
    //A request of lock received.
    public static final int MSG_LOCK_REQUEST = 2 << 2;
    //Accept to lock phone.
    public static final int MSG_LOCK_ACCEPT = 2 << 3;
    //Refuse to lock phone.
    public static final int MSG_LOCK_REFUSE = 2 << 4;
    //give up
    public static final int MSG_GIVEUP_REQUEST = 2 << 5;
    //give up accept
    public static final int MSG_GIVEUP_ACCEPT = 2 << 6;
    //
    public static final int MSG_GIVEUP_REFUSE = 2 << 7;



    private int errcode;
    private String errmsg;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    private byte[] getMessageString(int msg) {
        Gson gson = new Gson();
        setErrcode(msg);
        String strMsg = gson.toJson(this);

        return strMsg.getBytes();
    }

    private String getMessageString() {
        Gson gson = new Gson();
        String strMsg = gson.toJson(this);

        return strMsg + "\n";
    }

    public void sendMessage(Socket serverSocket) {

        try {
            serverSocket.getOutputStream().write(getMessageString().getBytes());
            serverSocket.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(BluetoothSocket serverSocket, int msg) {

        try {
            serverSocket.getOutputStream().write(getMessageString(msg));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return getMessageString();
    }
}
