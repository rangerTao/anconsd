package com.ranger.bmaterials.adapter;

import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.Formatter;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.netresponse.BMSearchResult;

public class BMSearchResultAdapter extends AbstractListAdapter<BMSearchResult.BMSearchData> {

	private static final String TAG = null;
	private static final boolean DEBUG = true;
	
	ImageSpan imageSpanFace;

	public BMSearchResultAdapter(Context context) {
		super(context);
		this.data = new CopyOnWriteArrayList<BMSearchResult.BMSearchData>();
	}

	static class Holder {
        TextView productName;
        TextView modeName;
        TextView comName;
        TextView price;
        TextView from;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		Holder holder;

        BMSearchResult.BMSearchData bd = getItem(position);

		if (convertView == null) {
			view = mInflater.inflate(R.layout.search_result_list_item, parent,
					false);
			holder = new Holder();

            holder.productName = (TextView) view.findViewById(R.id.product_name);
            holder.modeName = (TextView) view.findViewById(R.id.mode_name);
            holder.comName = (TextView) view.findViewById(R.id.company_name);
            holder.price = (TextView) view.findViewById(R.id.bm_tv_price);
            holder.from = (TextView) view.findViewById(R.id.bm_tv_price_from);


            view.setTag(holder);
		} else {
			view = convertView;
			holder = (Holder) view.getTag();
		}

        holder.productName.setText(bd.getProductName());
        holder.modeName.setText(bd.getModel());
        holder.comName.setText(bd.getCompanyName());
        holder.price.setText(bd.getPrice() + "/" + bd.getUnit());
        holder.from.setText(bd.getArea());

		return view;
	}

	public int getProgressValue(long total, long current) {
		if (total <= 0)
			return 0;
		return (int) (100L * current / total);
	}

	@Override
	public void onClick(View v) {
		if (onListItemClickListener == null) {
			return;
		}
		onListItemClickListener.onItemButtonClick(v, (Integer) v.getTag());
	}
}
