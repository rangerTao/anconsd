package com.ranger.lpa.thread;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.google.gson.Gson;
import com.ranger.lpa.pojos.SocketMessage;
import com.ranger.lpa.tools.NotifyManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.SortedMap;

/**
 * Created by taoliang on 14-7-12.
 */
public class LPAClientThread extends Thread {

    private Context mContext;
    private Socket mSocket;
    private BluetoothSocket bSocket;

    public LPAClientThread(Context context, Socket socket) {

        mContext = context;
        mSocket = socket;
    }

    public LPAClientThread(BluetoothSocket socket) {

        bSocket = socket;

    }

    @Override
    public void run() {
        super.run();

        try {

            SocketMessage stopServer = new SocketMessage(SocketMessage.MSG_STOPSERVER);
            stopServer.sendMessage(mSocket);

            InputStream inputStream = mSocket.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String strIncome = br.readLine();

            Gson gson = new Gson();
            SocketMessage sm = gson.fromJson(strIncome, SocketMessage.class);

            while (sm != null && sm.getErrcode() != SocketMessage.MSG_STOPSERVER) {

                strIncome = br.readLine();
                sm = gson.fromJson(strIncome, SocketMessage.class);
                NotifyManager.getInstance(mContext).notifyStateChanged(sm.getErrcode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void writeToSocket(String out){

        if(mSocket!= null){
            try {
                OutputStream os = mSocket.getOutputStream();
                PrintWriter pw = new PrintWriter(os);

                if(out != null && !out.equals("")){
                    pw.write(out+"\n");
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void sendLockRequestAccept(){

        SocketMessage smAccept = new SocketMessage(SocketMessage.MSG_LOCK_ACCEPT);
        smAccept.sendMessage(mSocket);

    }
}
