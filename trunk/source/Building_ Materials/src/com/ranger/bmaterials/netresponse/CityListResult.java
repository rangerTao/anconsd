package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;

/**
 * Created by taoliang on 14/11/17.
 */
public class CityListResult extends BaseResult {

    private int id;
    private String name;
    private ArrayList<City> children;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<City> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<City> children) {
        this.children = children;
    }

    public class City{
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
