package com.ranger.bmaterials.netresponse;

public class BMUserInfoResult extends BaseResult {

    private String nickname = "";
    private String realname = "";
    private String sex = "";
    private String provinces = "";
    private String city = "";
    private String signature = "";
    private String qq = "";
    private String photo = "";
    private String telephone = "";

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getProvinces() {
        return provinces;
    }

    public void setProvinces(String provinces) {
        this.provinces = provinces;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
