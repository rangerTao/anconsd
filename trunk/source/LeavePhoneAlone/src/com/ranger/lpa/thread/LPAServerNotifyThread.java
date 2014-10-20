package com.ranger.lpa.thread;

import android.content.Context;
import android.util.Log;

import com.ranger.lpa.Constants;
import com.ranger.lpa.LPApplication;
import com.ranger.lpa.MineProfile;
import com.ranger.lpa.pojos.BaseInfo;
import com.ranger.lpa.pojos.NotifyServerInfo;
import com.ranger.lpa.pojos.WifiUser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by taoliang on 14-7-30.
 */
public class LPAServerNotifyThread extends Thread{

    private DatagramSocket ds_localserver;

    private Context mContext;

    private WifiUser wuSelf;

    public LPAServerNotifyThread(Context con) {

        mContext = con;

        wuSelf = new WifiUser(BaseInfo.MSG_NOTIFY_SERVER);
        wuSelf.setUdid(MineProfile.getInstance().getUdid());
        wuSelf.setName("test");

        NotifyServerInfo.getInstance().getUsers().add(wuSelf);
//        try {
//            if(Constants.ds_server == null)
//                Constants.ds_server = new DatagramSocket(Constants.UDP_SOCKET);
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }

    }

    DatagramPacket dp_notify;

    @Override
    public void run() {
        super.run();

        while (Constants.isBoradcastNeeded) {

            try {
                ds_localserver = new DatagramSocket();

                if(MineProfile.getInstance().isLocked()){
                    sendLockStartDatagram();
                }else{
                    sendNotifyServer();
                }

                if(ds_localserver != null){
                    ds_localserver.send(dp_notify);
                }

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try{
                    Thread.sleep(2000);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }

    }

    /**
     * Notify server when not locked;
     * @throws SocketException
     * @throws UnknownHostException
     */
    private void sendNotifyServer() throws SocketException, UnknownHostException {

        String json = NotifyServerInfo.getInstance().getJson();

        if(Constants.DEBUG){
            Log.e("TAG","notify server info : " + json);
        }

        byte[] msg = json.getBytes();

        ds_localserver.setBroadcast(true);

        dp_notify = new DatagramPacket(msg,0,msg.length);
        dp_notify.setAddress(InetAddress.getByName(LPApplication.getInstance().getLocalIP()));
        dp_notify.setPort(Constants.UDP_SOCKET);
    }

    /**
     * * Notify server when ready to lock;
     */
    public void sendLockStartDatagram(){

        try {
            BaseInfo lockStart = new BaseInfo(BaseInfo.MSG_LOCK_REQUEST);
            String lockMsg = lockStart.getMessageStringLimt();
            byte[] msg = lockMsg.getBytes();
            dp_notify = new DatagramPacket(msg,0,msg.length);
            dp_notify.setAddress(InetAddress.getByName(LPApplication.getInstance().getLocalIP()));
            dp_notify.setPort(Constants.UDP_SOCKET);

            ds_localserver.send(dp_notify);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
