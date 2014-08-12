package com.ranger.lpa.pojos;

/**
 * Created by taoliang on 14-8-5.
 */
public class WifiUser extends BaseInfo{

    private String name;
    private String udid;
    private int accept = 0;

    public int getAccept() {
        return accept;
    }

    public void setAccept(int accept) {
        this.accept = accept;
    }

    public WifiUser(int errorcode) {
        super(errorcode);
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
