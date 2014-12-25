package com.ranger.lpa.pojos;

import java.util.ArrayList;

public class IncomeResult extends BaseInfo{

    public IncomeResult(int errorcode) {
        super(errorcode);
        // TODO Auto-generated constructor stub
    }

    ArrayList<WifiUser> users;
    long lock_period;
    String default_purnish;

    public String getDefault_purnish() {
        return default_purnish;
    }

    public void setDefault_purnish(String default_purnish) {
        this.default_purnish = default_purnish;
    }

    public long getLock_period() {
        return lock_period;
    }

    public void setLock_period(long lock_period) {
        this.lock_period = lock_period;
    }

    public ArrayList<WifiUser> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<WifiUser> users) {
        this.users = users;
    }
}
