package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;

/**
 * Created by taoliang on 14-7-22.
 */
public class BMCollectionResult extends BaseResult {

    private ArrayList<Collection> data = new ArrayList<Collection>();

    public ArrayList<Collection> getData() {
        return data;
    }

    public void setData(ArrayList<Collection> data) {
        this.data = data;
    }

    public void addItem(Collection col){
        data.add(col);
    }

    public class Collection{
        private String supplyId;
        private String productName;
        private String brand;
        private String standard;
        private String unit;
        private String releaseTime;
        private String price;
        private String userid;
        private String companyName;
        private String linkName;
        private String area;

        public String getSupplyId() {
            return supplyId;
        }

        public void setSupplyId(String supplyId) {
            this.supplyId = supplyId;
        }

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

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getReleaseTime() {
            return releaseTime;
        }

        public void setReleaseTime(String releaseTime) {
            this.releaseTime = releaseTime;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
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

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }
    }
}
