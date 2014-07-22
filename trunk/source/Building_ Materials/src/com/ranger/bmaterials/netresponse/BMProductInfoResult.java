package com.ranger.bmaterials.netresponse;

import com.google.gson.Gson;
import com.ranger.bmaterials.tools.PinyinUtil;

import java.util.ArrayList;

public class BMProductInfoResult extends BaseResult {

    public PInfo product;
    public CInfo company;

    public PInfo getMypro() {
        return product;
    }

    public void setMypro(PInfo mypro) {
        this.product = mypro;
    }

    public CInfo getmCom() {
        return company;
    }

    public void setmCom(CInfo mCom) {
        this.company = mCom;
    }

    public class PInfo{

        private String productName;
        private String brand;
        private String standard;
        private String material;
        private String productImage;
        private String detail;

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getStandard() {
            return standard;
        }

        public void setStandard(String standard) {
            this.standard = standard;
        }

        public String getMaterial() {
            return material;
        }

        public void setMaterial(String material) {
            this.material = material;
        }

        public String getProductImage() {
            return productImage;
        }

        public void setProductImage(String productImage) {
            this.productImage = productImage;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }
    }

    public class CInfo{
        private int userid;
        private String companyName;
        private String linkName;
        private String phone;
        private String telephone;
        private String integralGrade;

        public int getUserid() {
            return userid;
        }

        public void setUserid(int userid) {
            this.userid = userid;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getLinkName() {
            return linkName;
        }

        public void setLinkName(String linkName) {
            this.linkName = linkName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getIntegralGrade() {
            return integralGrade;
        }

        public void setIntegralGrade(String integralGrade) {
            this.integralGrade = integralGrade;
        }
    }
}
