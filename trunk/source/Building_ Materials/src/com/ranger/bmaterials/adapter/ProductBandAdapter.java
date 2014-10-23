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

    public interface onBandClickListener{
        public void onBandClick(View view);
    }

    public interface onCategoryClickListener{
        public void onCategoryClick(View view);
    }

    public void notifyBandSelect(String band){
        this.band = band;
        notifyDataSetChanged();
    }

    public void notifyModalSelect(String modal){
        this.modal = modal;
        notifyDataSetChanged();
    }

    private onBandClickListener bandClick;
    private onCategoryClickListener cateClick;

    public void setOnBandClickListener(onBandClickListener listener){
        bandClick = listener;
    }

    public void setOnCategoryClickListener(onCategoryClickListener listener){
        cateClick = listener;
    }

    String[] groups = {"品牌","类别"};

    private BandAndModelResult bamr;
    private Context mContext;

    public ProductBandAdapter(Context context,BandAndModelResult bmr){
        mContext = context;
        bamr = bmr;
    }

    @Override
    public int getGroupCount() {
        return groups.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(groupPosition == 0){
            return bamr.getBrand().size();
        }else{
            return bamr.getCategory().size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if(groupPosition == 1){
            return bamr.getBrand().get(childPosition);
        }
        return null;
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

        convertView = LayoutInflater.from(mContext).inflate(R.layout.band_group_layout,null);
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_group_name);
        tvName.setText(groups[groupPosition]);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if(groupPosition == 0){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.band_item_layout,null);
            TextView tvBand = (TextView) convertView.findViewById(R.id.tv_band_name);

            String bandText = bamr.getBrand().get(childPosition);
            tvBand.setText(bandText);

            if(bandText.equals(band)){
                tvBand.setBackgroundColor(Color.BLUE);
            }else{
                tvBand.setBackgroundColor(Color.parseColor("#d4d4d4"));
            }

            tvBand.setTag(bamr.getBrand().get(childPosition));

            tvBand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bandClick != null)
                        bandClick.onBandClick(v);
                }
            });

        }else{
            convertView = LayoutInflater.from(mContext).inflate(R.layout.band_item_group_layout,null);
            TextView tvGroupName = (TextView) convertView.findViewById(R.id.tv_band_name);
            LinearLayout llGroup = (LinearLayout) convertView.findViewById(R.id.ll_band_group_content);

            BandAndModelResult.Category cate = bamr.getCategory().get(childPosition);
            String name = cate.getName();
            tvGroupName.setText(name);
            tvGroupName.setTag(name);
            tvGroupName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(cateClick != null)
                        cateClick.onCategoryClick(v);
                }
            });
            ArrayList<BandAndModelResult.Type> types = cate.getTypes();
            for(BandAndModelResult.Type type : types){

                if(type == null){
                    continue;
                }

                View subView =  LayoutInflater.from(mContext).inflate(R.layout.band_item_layout,null);
                TextView tvBand = (TextView) subView.findViewById(R.id.tv_band_name);
                tvBand.setText(type.getName());
                tvBand.setTextColor(Color.parseColor("#0f0f0f"));
                tvBand.setTag(type.getName());

                if(type.getName().equals(modal)){
                    tvBand.setBackgroundColor(Color.BLUE);
                }else{
                    tvBand.setBackgroundColor(Color.parseColor("#d4d4d4"));
                }

                tvBand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(cateClick != null){
                            cateClick.onCategoryClick(v);
                        }
                    }
                });
                subView.setBackgroundColor((Color.parseColor("#f3f3f3")));

                llGroup.addView(subView);
            }

        }


        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
