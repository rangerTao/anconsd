package com.ranger.bmaterials.netresponse;

import android.util.Log;

import com.google.gson.Gson;
import com.ranger.bmaterials.tools.PinyinUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;

public class BMProvinceListResult extends BaseResult {

    private ArrayList<ProviceItem> proviceList = new ArrayList<ProviceItem>();

    public ArrayList<ProviceItem> getProviceList() {
        return proviceList;
    }

    public void setProviceList(ArrayList<ProviceItem> proviceList) {
        this.proviceList = proviceList;
    }

    public void addItem(String json){
        Gson gson = new Gson();
        ProviceItem pi = gson.fromJson(json, ProviceItem.class);

        pi.setPinyinName(pi.getName());

        if(pi != null)
            proviceList.add(pi);
    }


    public class ProviceItem{
        private int id;
        private String name;
        private String pinyinName;

        public String getPinyinName() {
            return pinyinName;
        }

        public void setPinyinName(String pinyinName) {
            if(pinyinName.equals("全国")){
                this.pinyinName = "#"+PinyinUtil.getPinyin(pinyinName);
                return;
            }
            this.pinyinName = PinyinUtil.getPinyin(pinyinName);
        }

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
