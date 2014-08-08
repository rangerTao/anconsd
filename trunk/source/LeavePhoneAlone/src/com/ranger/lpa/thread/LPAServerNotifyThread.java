package com.ranger.lpa.thread;

import com.ranger.lpa.Constants;
import com.ranger.lpa.pojos.BaseInfo;

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

    DatagramSocket ds_notify;

    public LPAServerNotifyThread() {

        try {
            ds_notify = new DatagramSocket(21999, InetAddress.getByName("255.255.255.255"));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    DatagramPacket dp_notify;

    @Override
    public void run() {
        super.run();

        BaseInfo serverNotify = new BaseInfo(BaseInfo.MSG_NOTIFY_SERVER);

        byte[] msg = serverNotify.toString().getBytes();

        try {
            ds_notify.setBroadcast(true);

            while (Constants.isBoradcastNeeded){

                dp_notify = new DatagramPacket(msg, msg.length);

                ds_notify.send(dp_notify);

                Thread.sleep(1000);

            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
