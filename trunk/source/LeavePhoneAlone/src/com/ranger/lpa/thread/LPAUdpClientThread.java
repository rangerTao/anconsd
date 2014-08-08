package com.ranger.lpa.thread;

import android.content.Context;

import com.google.gson.Gson;
import com.ranger.lpa.Constants;
import com.ranger.lpa.pojos.BaseInfo;
import com.ranger.lpa.pojos.SocketMessage;
import com.ranger.lpa.pojos.SubmitNameMessage;
import com.ranger.lpa.tools.NotifyManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by taoliang on 14-8-5.
 */
public class LPAUdpClientThread extends Thread {

    private String mUDID;
    private byte[] msgIN;
    private DatagramSocket dSocket;
    private Context mContext;

    public LPAUdpClientThread(Context context, String threadName, String udid) {
        super(threadName);
        mUDID = udid;
        msgIN = new byte[1024];
        mContext = context;
        try {
            dSocket = new DatagramSocket(Constants.UDP_SOCKET);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        super.run();

        DatagramPacket dpIn = new DatagramPacket(msgIN, msgIN.length);

        if (dSocket != null) {
            try {
                dSocket.receive(dpIn);

                String msg = new String(msgIN, 0, msgIN.length);

                Gson gson = new Gson();
                BaseInfo baseInfo = gson.fromJson(msg, BaseInfo.class);

                while (baseInfo.getErrcode() != BaseInfo.MSG_EXIT_PARTY) {

                    try {
                        NotifyManager.getInstance(mContext).notifyStateChanged(baseInfo.getErrcode());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dSocket.receive(dpIn);

                    msg = new String(msgIN, 0, msgIN.length);

                    gson = new Gson();
                    baseInfo = gson.fromJson(msg, BaseInfo.class);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void sendNameReply(String name){

        SubmitNameMessage snm = new SubmitNameMessage(BaseInfo.MSG_SUBMIT_NAME);
        snm.setName(name);

        Gson gson = new Gson();
        String msg = gson.toJson(snm,SubmitNameMessage.class);
        DatagramPacket dpName = new DatagramPacket(msg.getBytes(),msg.length());

        try{
            dSocket.send(dpName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
