package com.ranger.bmaterials.compare;

import com.ranger.bmaterials.netresponse.BMProvinceListResult;

import java.util.Comparator;

/**
 * Created by taoliang on 14-8-8.
 */
public class ProvinceComparator implements Comparator<BMProvinceListResult.ProviceItem> {


    @Override
    public int compare(BMProvinceListResult.ProviceItem lhs, BMProvinceListResult.ProviceItem rhs) {

        try{
            char charA = lhs.getPinyinName().toCharArray()[0];
            char charB = rhs.getPinyinName().toCharArray()[0];

            if(charA > charB){
                return 1;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

}
