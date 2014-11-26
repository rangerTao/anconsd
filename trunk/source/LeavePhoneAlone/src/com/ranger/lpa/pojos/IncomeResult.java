package com.ranger.lpa.pojos;

import java.util.ArrayList;

public class IncomeResult extends BaseInfo{

    public IncomeResult(int errorcode) {
        super(errorcode);
        // TODO Auto-generated constructor stub
    }

    ArrayList<WifiUser> users;

    public ArrayList<WifiUser> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<WifiUser> users) {
        this.users = users;
    }
}
