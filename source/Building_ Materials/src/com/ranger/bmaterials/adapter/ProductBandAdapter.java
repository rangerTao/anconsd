package com.ranger.bmaterials.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.netresponse.BandAndModelResult;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by taoliang on 14-8-4.
 */
public class ProductBandAdapter extends BaseExpandableListAdapter {

    private String band = "";

    private String modal = "";
    private int gpo = -1;
    private int cpo = -1;

    public interface onBandClickListener {
        public void onBandClick(View view);
    }

    public interface onCategoryClickListener {
        public void onCategoryClick(View view, int gpos, int cpos);
    }

    public void notifyBandSelect(String band) {
        this.band = band;
        notifyDataSetChanged();
    }

    public void notifyModalSelect(String modal, int gpos, int cpos) {
        gpo = gpos;
        cpo = cpos;
        this.modal = modal;
        notifyDataSetChanged();
    }

    private onBandClickListener bandClick;
    private onCategoryClickListener cateClick;

    public void setOnBandClickListener(onBandClickListener listener) {
        bandClick = listener;
    }

    public void setOnCategoryClickListener(onCategoryClickListener listener) {
        cateClick = listener;
    }

    String[] groups = {"品牌", "类别"};

    private BandAndModelResult bamr;
    private Context mContext;

    public ProductBandAdapter(Context context, BandAndModelResult bmr) {
        mContext = context;
        bamr = bmr;
    }

    @Override
    public int getGroupCount() {
        return bamr.getCategory().size();
//        return groups.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return bamr.getCategory().get(groupPosition).getTypes().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return bamr.getCategory().get(groupPosition).getTypes().get(childPosition).getName();
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

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(mContext).inflate(R.layout.band_group_layout, null);
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_group_name);
//        tvName.setText(groups[groupPosition]);
        tvName.setText(bamr.getCategory().get(groupPosition).getName());
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(mContext).inflate(R.layout.band_item_group_layout, null);
        TextView tvGroupName = (TextView) convertView.findViewById(R.id.tv_band_name);
//        LinearLayout llGroup = (LinearLayout) convertView.findViewById(R.id.ll_band_group_content);

        String name = (String) getChild(groupPosition,childPosition);
        tvGroupName.setText(name);
        tvGroupName.setTag(name);
        tvGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cateClick != null)
                    cateClick.onCategoryClick(v, groupPosition, childPosition);
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
