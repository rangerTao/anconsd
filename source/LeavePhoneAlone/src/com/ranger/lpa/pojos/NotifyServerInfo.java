package com.ranger.lpa.pojos;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by taoliang on 14-8-11.
 */
public class NotifyServerInfo{

    private int errcode;

    private static NotifyServerInfo _instance;

    public static NotifyServerInfo getInstance(){
        if(_instance == null){
            _instance = new NotifyServerInfo();
            users = new LinkedList<WifiUser>();
        }

        return _instance;
    }

    private NotifyServerInfo(){
        errcode = BaseInfo.MSG_NOTIFY_SERVER;
    }

    static LinkedList<WifiUser> users;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public LinkedList<WifiUser> getUsers() {
        return users;
    }

    public void addUser(WifiUser udid){
        users.add(udid);
    }

    public void setUsers(LinkedList<WifiUser> users) {
        this.users = users;
    }

    public String getJson(){
        String res = "";

        try{
            JSONObject jsonRoot = new JSONObject();
            jsonRoot.put("errcode",errcode);
            JSONArray jsonArray = new JSONArray();
            for(WifiUser user: users){
                JSONObject userObject = new JSONObject();
                userObject.put("name",user.getName());
                userObject.put("udid",user.getUdid());
                jsonArray.put(userObject.toString());
            }

            jsonRoot.put("users",jsonArray);

            res = jsonRoot.toString();

        }catch (Exception e){
            e.printStackTrace();
        }

        return res;
    }
}
