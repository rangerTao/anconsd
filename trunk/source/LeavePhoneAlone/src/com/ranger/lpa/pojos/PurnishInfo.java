package com.ranger.lpa.pojos;

/**
 * Created by taoliang on 14-8-25.
 */
public class PurnishInfo {

    private String purnish_title;
    private String purnish_content;
    private boolean isdefault;

    public String getPurnish_title() {
        return purnish_title;
    }

    public void setPurnish_title(String purnish_title) {
        this.purnish_title = purnish_title;
    }

    public String getPurnish_content() {
        return purnish_content;
    }

    public void setPurnish_content(String purnish_content) {
        this.purnish_content = purnish_content;
    }

    public boolean isDefault() {
        return isdefault;
    }

    public void setDefault(boolean isDefault) {
        this.isdefault = isDefault;
    }
}
