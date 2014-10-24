package com.ranger.lpa.pojos;

import java.util.ArrayList;

/**
 * Created by taoliang on 14-8-25.
 */
public class PurnishList {

    ArrayList<PurnishInfo> purnishes;

    public ArrayList<PurnishInfo> getPurnishes() {

        if(purnishes == null){
            purnishes = new ArrayList<PurnishInfo>();
        }

        return purnishes;
    }

    public void setPurnishes(ArrayList<PurnishInfo> purnishes) {
        this.purnishes = purnishes;
    }
}
