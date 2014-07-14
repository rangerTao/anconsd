package com.ranger.lpa.thread;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

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
    private boolean isServer;

    public LPAClientThread(Context context, Socket socket) {

        mContext = context;
        mSocket = socket;
    }

    public LPAClientThread(Context context, BluetoothSocket socket,boolean isser) {

        mContext = context;
        bSocket = socket;
        isServer = isser;

    }

    public LPAClientThread(Context context, Socket socket,boolean isser) {

        mContext = context;
        mSocket = socket;
        isServer = isser;

    }

    @Override
    public void run() {
        super.run();

        try {

            if(isServer == true){
                SocketMessage stopServer = new SocketMessage(SocketMessage.MSG_STOPSERVER);
                stopServer.sendMessage(bSocket);
//
//                try{
//                    Thread.sleep(1000);
//                }catch (Exception timesleep){
//                    timesleep.printStackTrace();
//                }
//
//                SocketMessage lockRequest = new SocketMessage(SocketMessage.MSG_LOCK_REQUEST);
//                lockRequest.sendMessage(bSocket);
            }

            InputStream inputStream = bSocket.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String strIncome = br.readLine();

            Gson gson = new Gson();
            SocketMessage sm = gson.fromJson(strIncome, SocketMessage.class);
            if(sm!= null){
                NotifyManager.getInstance(mContext).notifyStateChanged(sm.getErrcode());
            }

            while (sm != null && sm.getErrcode() != SocketMessage.MSG_STOPSERVER) {

                strIncome = br.readLine();
                sm = gson.fromJson(strIncome, SocketMessage.class);
                try{
                    NotifyManager.getInstance(mContext).notifyStateChanged(sm.getErrcode());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void writeToSocket(String out){

        if(bSocket!= null){
            try {
                OutputStream os = bSocket.getOutputStream();
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
        smAccept.sendMessage(bSocket);

    }

    public void sendLockRequestRefuse(){

        SocketMessage smAccept = new SocketMessage(SocketMessage.MSG_LOCK_REFUSE);
        smAccept.sendMessage(bSocket);

    }

    public void sendGiveupRequestAccept(){

        SocketMessage smAccept = new SocketMessage(SocketMessage.MSG_GIVEUP_ACCEPT);
        smAccept.sendMessage(bSocket);

    }

    public void sendGiveupRequestRefuse(){

        SocketMessage smAccept = new SocketMessage(SocketMessage.MSG_GIVEUP_REFUSE);
        smAccept.sendMessage(bSocket);

    }

    public void sendGiveupRequest(){

        SocketMessage smAccept = new SocketMessage(SocketMessage.MSG_GIVEUP_REQUEST);
        smAccept.sendMessage(bSocket);

    }
}
