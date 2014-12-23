package com.ranger.lpa.thread;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.ranger.lpa.Constants;
import com.ranger.lpa.LPApplication;
import com.ranger.lpa.MineProfile;
import com.ranger.lpa.connectity.wifi.LPAWifiManager;
import com.ranger.lpa.pojos.BaseInfo;
import com.ranger.lpa.pojos.NotifyServerInfo;
import com.ranger.lpa.pojos.WifiUser;
import com.ranger.lpa.tools.DeviceUtil;
import com.ranger.lpa.utils.WifiUtils;
import com.tencent.mm.sdk.platformtools.PhoneUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
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
        wuSelf.setName(Build.MODEL);

        NotifyServerInfo.getInstance().getUsers().add(wuSelf);

    }



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

    public void closeThread(){

        Log.e("TAG","close client's notify server thread");

        Constants.isBoradcastNeeded = false;
        try{
            ds_localserver.close();
        }catch (Exception d){
        }

    }

    /**
     * Notify server when not locked;
     * @throws SocketException
     * @throws UnknownHostException
     */
    private void sendNotifyServer() throws SocketException, UnknownHostException {

        String json = NotifyServerInfo.getInstance().getJson();

        byte[] msg = json.getBytes();

        ds_localserver.setBroadcast(true);

        DatagramPacket dp_notify = new DatagramPacket(msg,msg.length);

        try{
            InetAddress address = InetAddress.getByName("255.255.255.255");
            dp_notify.setAddress(address);

            dp_notify.setPort(Constants.UDP_CLIENT);

            ds_localserver.send(dp_notify);

            if(Constants.DEBUG){
                Log.e("TAG","notify server info : " + json);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * * Notify server when ready to lock;
     */
    public void sendLockStartDatagram(){

        try {

            MulticastSocket ms = new MulticastSocket(Constants.UDP_SOCKET);
            ms.joinGroup(InetAddress.getByName("226.1.3.5"));

            ms.setTimeToLive(5000);

//            ds_localserver.setBroadcast(true);
            BaseInfo lockStart = new BaseInfo(BaseInfo.MSG_LOCK_REQUEST);
            String lockMsg = lockStart.getMessageStringLimt();
            byte[] msg = lockMsg.getBytes();

            if(Constants.DEBUG){
                Log.e("TAG","notify server info for lock : " + lockMsg);
            }

            DatagramPacket dp_notify = new DatagramPacket(msg,msg.length);
//            dp_notify.setAddress(InetAddress.getByName("255.255.255.255"));
            dp_notify.setPort(Constants.UDP_SOCKET);

            ms.send(dp_notify);

//            ds_localserver.send(dp_notify);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

}
