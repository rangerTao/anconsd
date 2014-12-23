package com.ranger.lpa.thread;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;

import com.google.gson.Gson;
import com.ranger.lpa.Constants;
import com.ranger.lpa.LPApplication;
import com.ranger.lpa.MineProfile;
import com.ranger.lpa.connectity.SocketSessionManager;
import com.ranger.lpa.pojos.BaseInfo;
import com.ranger.lpa.pojos.IncomeResult;
import com.ranger.lpa.pojos.NotifyServerInfo;
import com.ranger.lpa.pojos.SocketMessage;
import com.ranger.lpa.pojos.SubmitNameMessage;
import com.ranger.lpa.pojos.SubmitNameResult;
import com.ranger.lpa.pojos.WifiUser;
import com.ranger.lpa.tools.NotifyManager;
import com.ranger.lpa.utils.DeviceId;
import com.ranger.lpa.utils.WifiUtils;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.nio.charset.Charset;
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
        msgIN = new byte[4096];
        mContext = context;
        try {
            dSocket = new DatagramSocket(Constants.UDP_CLIENT);
        } catch (Exception e) {
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
            while (true){
                try {
                    dSocket.receive(dpIn);

                    InetAddress inComeAddress = dpIn.getAddress();

                    String msg = new String(msgIN, 0, dpIn.getLength());

                    Log.e("TAG","msg received : " + msg);

                    msg = msg.substring(0, msg.lastIndexOf("}") + 1);
                    Log.e("TAG", "incoming address : " + inComeAddress.getHostAddress());

                    SocketSessionManager.getInstance(mContext).connect(inComeAddress,msg);
//
//                    Gson gson = new Gson();
//                    BaseInfo baseInfo = gson.fromJson(msg, BaseInfo.class);
//
//                    while (baseInfo.getErrcode() != BaseInfo.MSG_EXIT_PARTY) {
//
//                        try {
//                            switch (baseInfo.getErrcode()) {
//                                case BaseInfo.MSG_NOTIFY_SERVER:
//                                    if (!LPApplication.getInstance().isSelfServer()){
//
//                                        if(Constants.DEBUG){
//                                            Log.e("TAG", "msg notify server received: " + msg);
//                                        }
//
//                                        SubmitNameResult snr = gson.fromJson(msg,SubmitNameResult.class);
//
//                                        if(!snr.isExists(MineProfile.getInstance().getUdid())){
//                                            sendNameReply();
//                                        }
//                                    }
//                                default:
//                                    baseInfo.setMessage(msg);
//                                    NotifyManager.getInstance(mContext).notifyStateChanged(baseInfo.getErrcode(), baseInfo);
//                                    break;
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        Log.e("TAG","income msg: " + msg);
//
//                        msgIN = new byte[4096];
//                        dpIn = new DatagramPacket(msgIN, msgIN.length);
//
//                        dSocket.receive(dpIn);
//                        msg = new String(msgIN, 0, msgIN.length);
//                        msg = msg.substring(0, msg.lastIndexOf("}") + 1);
//
//
//                        try{
//                            gson = new Gson();
//                            IncomeResult wu = gson.fromJson(msg, IncomeResult.class);
//
//                            NotifyServerInfo.getInstance().addAllUsers(wu.getUsers());
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }


        }

    }

    public void sendNameReply() {
        sendNameReply(MineProfile.getInstance().getNickName());
    }

    SubmitNameMessage snm;

    public void sendNameReply(String name) {

        if(snm == null){
            snm = new SubmitNameMessage(BaseInfo.MSG_SUBMIT_NAME);
            snm.setName(name);
            snm.setUdid(MineProfile.getInstance().getUdid());
        }

        Gson gson = new Gson();
        String msg = gson.toJson(snm, SubmitNameMessage.class);
        DatagramPacket dpName = new DatagramPacket(msg.getBytes(), msg.length());

        try {
            dpName.setAddress(InetAddress.getByName("255.255.255.255"));
            dSocket.send(dpName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
