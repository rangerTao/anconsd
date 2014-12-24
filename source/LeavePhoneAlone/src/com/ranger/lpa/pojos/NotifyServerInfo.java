package com.ranger.lpa.pojos;

import android.os.Build;

import com.google.gson.Gson;
import com.ranger.lpa.MineProfile;
import com.ranger.lpa.tools.DeviceUtil;
import com.ranger.lpa.utils.DeviceId;
import com.tencent.mm.sdk.platformtools.PhoneUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by taoliang on 14-8-11.
 */
public class NotifyServerInfo{

    private int errcode;

    private static HashMap<String,WifiUser> userids;

    private static NotifyServerInfo _instance;

    public static NotifyServerInfo getInstance(){
        if(_instance == null){
            users = new LinkedList<WifiUser>();
            userids = new HashMap<String, WifiUser>();
            _instance = new NotifyServerInfo();
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

    public void addAllUsers(ArrayList<WifiUser> userlist){
        for(WifiUser user : userlist){
            if(!isUserExists(user)){
                users.add(user);
                userids.put(user.getUdid(),user);
            }
        }
    }

    public void removeUser(String udid){

        WifiUser userToRemove = null;

        if(userids.containsKey(udid)){

            userToRemove = userids.get(udid);

            if(userToRemove != null && users.contains(userToRemove)){
                users.remove(userToRemove);
            }

            userids.remove(udid);
        }


    }

    public boolean isUserExists(WifiUser user){

        if(user.getUdid().equals(MineProfile.getInstance().getUdid())){
            return true;
        }

        return userids.containsKey(user.getUdid());
    }

    public void addUser(WifiUser udid){
        users.add(udid);
        userids.put(udid.getUdid(),udid);
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
                jsonArray.put(userObject);
            }

            JSONObject userObject = new JSONObject();
            userObject.put("name",Build.MODEL);
            userObject.put("udid", MineProfile.getInstance().getUdid());
            jsonArray.put(userObject);

            jsonRoot.put("users",jsonArray);

            jsonRoot.put("lock_period",MineProfile.getInstance().getLockPeriodParty());

            res = jsonRoot.toString();

        }catch (Exception e){
            e.printStackTrace();
        }

        return res;
    }
}
