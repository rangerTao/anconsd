package com.ranger.bmaterials.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.netresponse.BMCollectionResult;
import com.ranger.bmaterials.netresponse.BMSearchResult;

import java.util.List;

public class BMProductCollectionAdapter extends BaseAdapter {

	private List<BMCollectionResult.Collection> listGuideInfo = null;
	private LayoutInflater inflater = null;

	public BMProductCollectionAdapter(Context context, List<BMCollectionResult.Collection> guideList) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listGuideInfo = guideList;
	}
	
	@Override
	public int getCount() {
		return listGuideInfo.size();
	}

	@Override
	public Object getItem(int position) {
		return listGuideInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        BMCollectionResult.Collection bd = (BMCollectionResult.Collection) getItem(position);

        if (convertView == null) {
            view = inflater.inflate(R.layout.product_collect_list_item, parent,
                    false);
            holder = new ViewHolder();

            holder.productName = (TextView) view.findViewById(R.id.product_name);
            holder.modeName = (TextView) view.findViewById(R.id.mode_name);
            holder.price = (TextView) view.findViewById(R.id.bm_tv_price);
            holder.from = (TextView) view.findViewById(R.id.bm_tv_price_from);


            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.productName.setText(bd.getProductName());
        holder.modeName.setText(bd.getStandard());
        holder.price.setText(bd.getPrice() + "/" + bd.getUnit());
        holder.from.setText(bd.getArea());

        return view;
	}

    static class ViewHolder {
        TextView productName;
        TextView modeName;
        TextView price;
        TextView from;
    }
}
