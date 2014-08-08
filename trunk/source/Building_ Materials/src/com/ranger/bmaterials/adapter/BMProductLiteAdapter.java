package com.ranger.bmaterials.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.netresponse.BMSearchResult;

public class BMProductLiteAdapter extends BaseAdapter {

	private List<BMSearchResult.BMSearchData> listGuideInfo = null;
	private LayoutInflater inflater = null;
	
	public BMProductLiteAdapter(Context context, List<BMSearchResult.BMSearchData> guideList) {
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

        BMSearchResult.BMSearchData bd = (BMSearchResult.BMSearchData) getItem(position);

        if (convertView == null) {
            view = inflater.inflate(R.layout.search_result_list_item, parent,
                    false);
            holder = new ViewHolder();

            holder.productName = (TextView) view.findViewById(R.id.product_name);
            holder.modeName = (TextView) view.findViewById(R.id.mode_name);
            view.findViewById(R.id.company_name).setVisibility(View.GONE);
            holder.price = (TextView) view.findViewById(R.id.bm_tv_price);
            holder.from = (TextView) view.findViewById(R.id.bm_tv_price_from);


            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.productName.setText(bd.getProductName());
        holder.modeName.setText(bd.getModel());
        if(!bd.getPrice().equals(""))
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
