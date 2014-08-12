package com.ranger.lpa.pojos;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by taoliang on 14-8-11.
 */
public class SubmitNameResult  {

    private ArrayList<String> users;

    private int errcode;

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }
}
