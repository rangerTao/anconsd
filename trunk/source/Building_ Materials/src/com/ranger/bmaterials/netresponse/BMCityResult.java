package com.ranger.bmaterials.netresponse;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by taoliang on 14/11/17.
 */
public class BMCityResult extends BaseResult {

    private ArrayList<CityListResult> citys;

    public ArrayList<CityListResult> getCitys() {
        return citys;
    }

    public void setCitys(ArrayList<CityListResult> citys) {
        this.citys = citys;
    }

    public void addItem(String json){

        if(citys == null){
           citys = new ArrayList<CityListResult>();
        }

        Gson gson = new Gson();
        CityListResult pi = gson.fromJson(json, CityListResult.class);

        if(pi != null)
            citys.add(pi);
    }
}
