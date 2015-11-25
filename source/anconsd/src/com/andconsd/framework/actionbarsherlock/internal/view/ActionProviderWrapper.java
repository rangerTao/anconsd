package com.andconsd.framework.actionbarsherlock.internal.view;

import com.andconsd.framework.actionbarsherlock.internal.view.menu.SubMenuWrapper;
import com.andconsd.framework.actionbarsherlock.view.ActionProvider;
import android.view.View;

public class ActionProviderWrapper extends android.view.ActionProvider {
    private final ActionProvider mProvider;


    public ActionProviderWrapper(ActionProvider provider) {
        super(null/*TODO*/); //XXX this *should* be unused
        mProvider = provider;
    }


    public ActionProvider unwrap() {
        return mProvider;
    }

    @Override
    public View onCreateActionView() {
        return mProvider.onCreateActionView();
    }

    @Override
    public boolean hasSubMenu() {
        return mProvider.hasSubMenu();
    }

    @Override
    public boolean onPerformDefaultAction() {
        return mProvider.onPerformDefaultAction();
    }

    @Override
    public void onPrepareSubMenu(android.view.SubMenu subMenu) {
        mProvider.onPrepareSubMenu(new SubMenuWrapper(subMenu));
    }
}
