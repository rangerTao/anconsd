package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;

/**
 * Created by taoliang on 14-8-4.
 */
public class BandAndModelResult extends BaseResult {

    private ArrayList<String> brand;

    private ArrayList<Category> category;

    public ArrayList<String> getBrand() {
        return brand;
    }

    public void setBrand(ArrayList<String> brand) {
        this.brand = brand;
    }

    public ArrayList<Category> getCategory() {
        return category;
    }

    public void setCategory(ArrayList<Category> category) {
        this.category = category;
    }

    public class Category{
        private String name;
        private ArrayList<Type> type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<Type> getTypes() {
            return type;
        }

        public void setTypes(ArrayList<Type> types) {
            this.type = types;
        }
    }

    public class Type{
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}