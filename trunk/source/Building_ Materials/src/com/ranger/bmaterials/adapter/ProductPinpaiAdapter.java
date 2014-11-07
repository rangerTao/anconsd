package com.ranger.bmaterials.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.netresponse.BandAndModelResult;

import java.util.ArrayList;

/**
 * Created by taoliang on 14/11/7.
 */
public class ProductPinpaiAdapter extends BaseExpandableListAdapter {

    public interface onCategoryClickListener {
        public void onCategoryClick(View view, int gpos, int cpos);
    }

    private Context mContext;
    private ArrayList<String> bands;

    public ProductPinpaiAdapter(Context context,ArrayList<String> band){
        mContext = context;
        bands = band;
    }

    String[] groups = {"品牌"};

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return bands.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return bands.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private String modal = "";
    private int gpo = -1;
    private int cpo = -1;

    public void notifyChange(){
        gpo = -1;
        cpo = -1;

        notifyDataSetChanged();
    }

    public void notifyModalSelect(String modal, int gpos, int cpos) {
        gpo = gpos;
        cpo = cpos;
        this.modal = modal;
        notifyDataSetChanged();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.band_group_layout,null);
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_group_name);
//        tvName.setText(groups[groupPosition]);
        tvName.setText(groups[groupPosition]);
        tvName.setVisibility(View.GONE);
        return convertView;
    }

    private onCategoryClickListener categoryClickListener;

    public void setOnCategoryClickListener(onCategoryClickListener listener) {
        categoryClickListener = listener;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.band_item_group_layout,null);
        final TextView tvGroupName = (TextView) convertView.findViewById(R.id.tv_band_name);
//        LinearLayout llGroup = (LinearLayout) convertView.findViewById(R.id.ll_band_group_content);

        String name = bands.get(childPosition);
        tvGroupName.setText(name);
        tvGroupName.setTag(name);
        tvGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categoryClickListener != null){
                    categoryClickListener.onCategoryClick(tvGroupName,groupPosition,childPosition);
                }
            }
        });

        if (gpo == groupPosition && cpo == childPosition) {
            tvGroupName.setBackgroundColor(Color.BLUE);
        } else {
            tvGroupName.setBackgroundColor(Color.parseColor("#d4d4d4"));
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
