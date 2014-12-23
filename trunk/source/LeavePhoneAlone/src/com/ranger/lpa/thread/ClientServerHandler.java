package com.ranger.lpa.thread;

import android.util.Log;

import com.google.gson.Gson;
import com.ranger.lpa.Constants;
import com.ranger.lpa.pojos.BaseInfo;
import com.ranger.lpa.tools.NotifyManager;
import com.ranger.lpa.ui.activity.LPAPartyCenter;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.SessionState;

/**
 * Created by taoliang on 14/12/18.
 */
public class ClientServerHandler extends IoHandlerAdapter {

    LPAPartyCenter mainActivity;

    public ClientServerHandler(LPAPartyCenter lpa){
        mainActivity = lpa;
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);

        if(Constants.DEBUG){
            Log.e("TAG","client hadler opened : server ip : " + session.getServiceAddress().toString());
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        super.messageReceived(session, message);

        if(Constants.DEBUG){
            Log.e("TAG","message from " + session.getServiceAddress().toString() + " received");
        }

        String inMsg = message.toString();

        inMsg = inMsg.substring(0, inMsg.lastIndexOf("}") + 1);

        Gson gson = new Gson();
        BaseInfo baseInfo = gson.fromJson(inMsg, BaseInfo.class);

        baseInfo.setMessage(inMsg);
        NotifyManager.getInstance(mainActivity.getApplicationContext()).notifyStateChanged(baseInfo.getErrcode(), baseInfo);

        if(mainActivity != null && mainActivity.serNotifyThread != null){
            mainActivity.serNotifyThread.closeThread();
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        super.inputClosed(session);
    }
}
