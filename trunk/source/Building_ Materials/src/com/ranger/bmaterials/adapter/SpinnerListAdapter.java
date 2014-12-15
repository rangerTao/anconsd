package com.ranger.bmaterials.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by taoliang on 14/12/15.
 */
public class SpinnerListAdapter extends BaseAdapter {

    String[] baseArray;
    Context mContext;

    public SpinnerListAdapter(Context context, String[] strArray){

        mContext = context;
        baseArray = strArray;
    }

    @Override
    public int getCount() {
        return baseArray.length;
    }

    @Override
    public Object getItem(int position) {
        return baseArray[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView tvName = new TextView(mContext);
        tvName.setText((String)getItem(position));

        Log.e("TAG","province : " + baseArray[position]);

        tvName.setTextSize(18);
        tvName.setTextColor(Color.BLACK);

        return tvName;
    }
}
