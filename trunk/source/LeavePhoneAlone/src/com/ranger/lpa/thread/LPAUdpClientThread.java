package com.ranger.lpa.thread;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;

import com.google.gson.Gson;
import com.ranger.lpa.Constants;
import com.ranger.lpa.LPApplication;
import com.ranger.lpa.MineProfile;
import com.ranger.lpa.pojos.BaseInfo;
import com.ranger.lpa.pojos.SocketMessage;
import com.ranger.lpa.pojos.SubmitNameMessage;
import com.ranger.lpa.pojos.SubmitNameResult;
import com.ranger.lpa.tools.NotifyManager;
import com.ranger.lpa.utils.DeviceId;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.spec.DSAParameterSpec;

/**
 * Created by taoliang on 14-8-5.
 */
public class LPAUdpClientThread extends Thread {

    private String mUDID;
    private byte[] msgIN;
    private DatagramSocket dSocket;
    private Context mContext;

    public LPAUdpClientThread(Context context) {
        init(context);
    }

    public LPAUdpClientThread(Context context, String threadName, String udid) {
        super(threadName);
        mUDID = udid;
        init(context);
    }

    private void init(Context context) {
        msgIN = new byte[1024];
        mContext = context;
        try {
            dSocket = new DatagramSocket(Constants.UDP_SOCKET);
        } catch (Exception e) {
            Log.e("TAG",e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopSocket(){
        try{
            dSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();

        Log.e("TAG","lpa client thread start");

        DatagramPacket dpIn = new DatagramPacket(msgIN, msgIN.length);

        if (dSocket != null) {
            try {
                dSocket.receive(dpIn);

                String msg = new String(msgIN, 0, msgIN.length);
                msg = msg.substring(0, msg.lastIndexOf("}") + 1);

                Gson gson = new Gson();
                BaseInfo baseInfo = gson.fromJson(msg, BaseInfo.class);

                while (baseInfo.getErrcode() != BaseInfo.MSG_EXIT_PARTY) {

                    try {
                        switch (baseInfo.getErrcode()) {
                            case BaseInfo.MSG_NOTIFY_SERVER:
                                if (!LPApplication.getInstance().isSelfServer()){

                                    if(Constants.DEBUG){
                                        Log.e("TAG", "msg notify server received: " + msg);
                                    }

                                    SubmitNameResult snr = gson.fromJson(msg,SubmitNameResult.class);

                                    if(!snr.getUsers().contains(MineProfile.getInstance().getUdid())){
                                        sendNameReply();
                                    }
                                }
                            default:
                                baseInfo.setMessage(msg);
                                NotifyManager.getInstance(mContext).notifyStateChanged(baseInfo.getErrcode(), baseInfo);
                                break;

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.e("TAG","income msg: " + msg);

                    msgIN = new byte[1024];
                    dpIn = new DatagramPacket(msgIN, msgIN.length);

                    dSocket.receive(dpIn);
                    msg = new String(msgIN, 0, msgIN.length);
                    msg = msg.substring(0, msg.lastIndexOf("}") + 1);


                    gson = new Gson();
                    baseInfo = gson.fromJson(msg, BaseInfo.class);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void sendNameReply() {
        sendNameReply(MineProfile.getInstance().getNickName());
    }

    public void sendNameReply(String name) {

        SubmitNameMessage snm = new SubmitNameMessage(BaseInfo.MSG_SUBMIT_NAME);
        snm.setName(name);
        snm.setUdid(MineProfile.getInstance().getUdid());

        Gson gson = new Gson();
        String msg = gson.toJson(snm, SubmitNameMessage.class);
        DatagramPacket dpName = new DatagramPacket(msg.getBytes(), msg.length());

        try {
            dpName.setAddress(InetAddress.getByName(LPApplication.getInstance().getLocalIP()));
            dSocket.send(dpName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
