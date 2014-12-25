package com.ranger.lpa.pojos;


public class SocketMessage extends BaseInfo{

    private long lock_period;
    private String default_purnish;

    public long getLock_period() {
        return lock_period;
    }

    public void setLock_period(long lock_period) {
        this.lock_period = lock_period;
    }

    public String getDefault_purnish() {
        return default_purnish;
    }

    public void setDefault_purnish(String default_purnish) {
        this.default_purnish = default_purnish;
    }

    public SocketMessage(int errorcode) {
        super(errorcode);
    }

}
