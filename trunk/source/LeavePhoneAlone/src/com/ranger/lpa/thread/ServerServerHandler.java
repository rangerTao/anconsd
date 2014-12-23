package com.ranger.lpa.thread;

import android.util.Log;

import com.ranger.lpa.connectity.SocketSessionManager;
import com.ranger.lpa.pojos.NotifyServerInfo;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * Created by taoliang on 14/12/18.
 */
public class ServerServerHandler extends IoHandlerAdapter {

    private String ipClient = "";

    public ServerServerHandler(String ip) {
        super();
        ipClient = ip;
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        Log.e("TAG", "server created");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);

        SocketSessionManager.getInstance().removeSession(ipClient);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);

        SocketSessionManager.getInstance().removeSession(ipClient);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        super.messageReceived(session, message);
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
