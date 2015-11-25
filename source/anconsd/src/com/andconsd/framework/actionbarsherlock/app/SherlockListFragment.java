package com.andconsd.framework.actionbarsherlock.app;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import com.andconsd.framework.actionbarsherlock.internal.view.menu.MenuItemWrapper;
import com.andconsd.framework.actionbarsherlock.internal.view.menu.MenuWrapper;
import com.andconsd.framework.actionbarsherlock.view.Menu;
import com.andconsd.framework.actionbarsherlock.view.MenuInflater;
import com.andconsd.framework.actionbarsherlock.view.MenuItem;

import static com.andconsd.framework.app.Watson.OnCreateOptionsMenuListener;
import static com.andconsd.framework.app.Watson.OnOptionsItemSelectedListener;
import static com.andconsd.framework.app.Watson.OnPrepareOptionsMenuListener;

public class SherlockListFragment extends ListFragment implements OnCreateOptionsMenuListener, OnPrepareOptionsMenuListener, OnOptionsItemSelectedListener {
    private SherlockFragmentActivity mActivity;

    public SherlockFragmentActivity getSherlockActivity() {
        return mActivity;
    }

    @Override
    public void onAttach(Activity activity) {
        if (!(activity instanceof SherlockFragmentActivity)) {
            throw new IllegalStateException(getClass().getSimpleName() + " must be attached to a SherlockFragmentActivity.");
        }
        mActivity = (SherlockFragmentActivity)activity;

        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    @Override
    public final void onCreateOptionsMenu(android.view.Menu menu, android.view.MenuInflater inflater) {
        onCreateOptionsMenu(new MenuWrapper(menu), mActivity.getSupportMenuInflater());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Nothing to see here.
    }

    @Override
    public final void onPrepareOptionsMenu(android.view.Menu menu) {
        onPrepareOptionsMenu(new MenuWrapper(menu));
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //Nothing to see here.
    }

    @Override
    public final boolean onOptionsItemSelected(android.view.MenuItem item) {
        return onOptionsItemSelected(new MenuItemWrapper(item));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Nothing to see here.
        return false;
    }
}