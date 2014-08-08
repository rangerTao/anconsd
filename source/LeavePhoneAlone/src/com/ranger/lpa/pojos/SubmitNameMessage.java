package com.ranger.lpa.pojos;

/**
 * Created by taoliang on 14-8-5.
 */
public class SubmitNameMessage extends BaseInfo {

    public SubmitNameMessage(int errorcode) {
        super(errorcode);
    }

    private String name;
    private String udid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }
}
