package com.ranger.lpa.pojos;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by taoliang on 14-8-11.
 */
public class SubmitNameResult  {

    private ArrayList<WifiUser> users;

    private int errcode;

    public ArrayList<WifiUser> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<WifiUser> users) {
        this.users = users;
    }

    public boolean isExists(String usudid){

        if(users != null){
            for(WifiUser user : users){
                if(user.getUdid().equals(usudid)){
                    return true;
                }
            }
        }

        return false;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }
}
