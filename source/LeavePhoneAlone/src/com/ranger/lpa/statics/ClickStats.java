package com.ranger.lpa.statics;

import android.content.Context;

import com.baidu.mobstat.StatService;

/**
 * Created by taoliang on 15/3/20.
 */
public class ClickStats {

    public static enum CLICK_TYPE{
        ENTER,
        ENTER_SETTING,
        ENTER_SETTING_Purnish,
        Enter_setting_pattern,
        Enter_purnish_add,
        Enter_purnish_edit,
        Enter_Patter_Couple,
        Enter_Patter_Party,
        Enter_Patter_Work,
        START_COUPLE,
        START_PARTY,
        START_WORK,
        Click_Purnish_Save,
        Click_Purnish_Cancel,
        Click_Purnish_del,
        Click_Purnish_default,
        Click_Pattern_Period_C,
        Click_Pattern_Period_P,
        Click_Pattern_Period_W

    }

    public static void onClickStats(Context context,CLICK_TYPE click_type){
        StatService.onEvent(context,click_type.name(),click_type.name());
    }

    public static void onClickStats(Context context,CLICK_TYPE click_type,String longP){
        StatService.onEvent(context,click_type.name(),longP);
    }

}
