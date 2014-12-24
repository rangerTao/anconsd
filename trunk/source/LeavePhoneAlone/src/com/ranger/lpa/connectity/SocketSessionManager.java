package com.ranger.lpa.connectity;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.ranger.lpa.Constants;
import com.ranger.lpa.pojos.BaseInfo;
import com.ranger.lpa.pojos.IncomeResult;
import com.ranger.lpa.pojos.NotifyServerInfo;
import com.ranger.lpa.pojos.WifiUser;
import com.ranger.lpa.thread.ServerServerHandler;
import com.ranger.lpa.tools.NotifyManager;

import org.apache.http.protocol.SyncBasicHttpContext;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.SessionState;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.math.MathContext;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by taoliang on 14/12/22.
 */
public class SocketSessionManager {

    private static SocketSessionManager _instance;

    private HashMap<String,ConnectFuture> sessions;

    private HashMap<String,String> addedSessionIDs;

    private static Context mContext;

    private SocketSessionManager(){
        sessions = new HashMap<String, ConnectFuture>();
        addedSessionIDs = new HashMap<String, String>();
    }

    public class WifiSession{
        String udid;
        ConnectFuture cf;

        public ConnectFuture getFuture() {
            return cf;
        }

        public void setCf(ConnectFuture cf) {
            this.cf = cf;
        }

        public String getUdid() {
            return udid;
        }

        public void setUdid(String udid) {
            this.udid = udid;
        }
    }

    public static SocketSessionManager getInstance(){

        if(mContext == null){
            throw new NullPointerException("Have to init the manager first!!!");
        }

        if(_instance == null){
            _instance = new SocketSessionManager();
        }

        return _instance;
    }

    public static SocketSessionManager getInstance(Context context){
        mContext = context;

        if(_instance == null){
            _instance = new SocketSessionManager();
        }

        return _instance;
    }

    public static void init(Context context){

    }

    public void addSession(ConnectFuture cf,String message){

        if(!sessions.containsKey(cf.getSession().getServiceAddress().toString())){

            Gson gson = new Gson();
            BaseInfo baseInfo = gson.fromJson(message, BaseInfo.class);

            baseInfo.setMessage(message);
            NotifyManager.getInstance(mContext).notifyStateChanged(baseInfo.getErrcode(), baseInfo);

            IncomeResult wfUser  = new Gson().fromJson(message,IncomeResult.class);

            sessions.put(cf.getSession().getServiceAddress().toString(),cf);
            addedSessionIDs.put(cf.getSession().getServiceAddress().toString(),wfUser.getUsers().get(0).getUdid());

            notifyNewUser();
        }else{
            notifyNewUser();
            Log.e("TAG", "already connected : " + cf.getSession().getServiceAddress().toString());
        }
    }

    public void notifyNewUser(){

        if(Constants.DEBUG){
            Log.e("TAG","notify new users");
        }

        notifyMessage(NotifyServerInfo.getInstance().getJson());

    }

    public void notifyLockStart(){

        if(Constants.DEBUG){
            Log.e("TAG","notify lock start");
        }

        BaseInfo lockStart = new BaseInfo(BaseInfo.MSG_LOCK_REQUEST);
        String lockMsg = lockStart.getMessageStringLimt();

        notifyMessage(lockMsg);

    }

    public void notifyMessage(String message){
        Iterator<String> sessionIterator = sessions.keySet().iterator();

        while (sessionIterator.hasNext()){

            String entrySession = sessionIterator.next();

            ConnectFuture cf = sessions.get(entrySession);
            if(cf != null && !cf.isCanceled()){

                try{
                    cf.getSession().write(message);

                    if(Constants.DEBUG){
                        Log.e("TAG","notify user " + cf.getSession().getServiceAddress().toString());
                    }

                    Log.e("TAG","notify new user to : " + cf.getSession().getServiceAddress().toString() + " with message : " + NotifyServerInfo.getInstance().getJson());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }

    }

    public synchronized void connect(InetAddress incomeAddress,String message){

        if(!sessions.containsKey(incomeAddress.getHostAddress())){
            NioSocketConnector connector = new NioSocketConnector();
            connector.getFilterChain().addLast("logger",new LoggingFilter());
            connector.getFilterChain().addLast("codec",new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
            connector.setConnectTimeoutMillis(3000);
            connector.setHandler(new ServerServerHandler(incomeAddress.getHostAddress()));
            ConnectFuture connectFuture = connector.connect(new InetSocketAddress(incomeAddress.getHostAddress(), Constants.UDP_CLIENT));
            connectFuture.awaitUninterruptibly();

            addSession(connectFuture,message);
        }else{
            if(Constants.DEBUG){
                Log.e("TAG","already connected " + incomeAddress.getHostAddress());
            }

            notifyNewUser();
        }

    }

    public void removeSession(String ip){

        NotifyServerInfo.getInstance().removeUser(addedSessionIDs.get("/" + ip + ":" + Constants.UDP_CLIENT));

        notifyNewUser();

        if(Constants.DEBUG){
            Log.e("TAG","client closed : " + ip);
        }

        sessions.remove(ip);
        addedSessionIDs.remove(ip);

        NotifyManager.getInstance(mContext).notifyUserChanged();

    }
}
