package com.ranger.lpa.pojos;


public class WifiInfo extends BaseInfo{

    private String mSSID = "";
    private String mPWD = "";
    private String mIP = "";

    public WifiInfo(String mSSID, String mPWD, String mIP) {
        super(MSG_ERROR_OK);
        this.mSSID = mSSID;
        this.mPWD = mPWD;
        this.mIP = mIP;
    }

    public String getmSSID() {
        return mSSID;
    }

    public void setmSSID(String mSSID) {
        this.mSSID = mSSID;
    }

    public String getmPWD() {
        return mPWD;
    }

    public void setmPWD(String mPWD) {
        this.mPWD = mPWD;
    }

    public String getmIP() {
        return mIP;
    }

    public void setmIP(String mIP) {
        this.mIP = mIP;
    }

    public WifiInfo(int errorcode) {
        super(errorcode);
    }

}
