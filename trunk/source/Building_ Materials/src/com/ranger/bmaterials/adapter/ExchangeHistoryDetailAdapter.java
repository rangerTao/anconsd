package com.ranger.bmaterials.adapter;

import java.util.ArrayList;
import java.util.List;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.Constants.Operator;
import com.ranger.bmaterials.netresponse.ExchangeHistoryDetailResult.ExchangeItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExchangeHistoryDetailAdapter extends BaseAdapter {

	LayoutInflater inflater;
	Context mContext;
	List<ExchangeItem> items;

	public ExchangeHistoryDetailAdapter(Context context, ArrayList<ExchangeItem> eis) {
		inflater = LayoutInflater.from(context);
		mContext = context;
		items = eis;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int arg0) {
		return items.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup arg2) {

		ExchangeHolder eh;
		ExchangeItem ei = (ExchangeItem) getItem(position);

		if (contentView == null) {
			eh = new ExchangeHolder();

			View view = inflater.inflate(R.layout.exchange_history_list_item, null);
			eh.operatorIcon = (ImageView) view.findViewById(R.id.ivExchangeItemOperator);
			eh.propicon = (ImageView) view.findViewById(R.id.ivExchangeItemIcon);
			eh.date = (TextView) view.findViewById(R.id.exchange_card_date);
			eh.expire = (TextView) view.findViewById(R.id.exchange_card_expire_date);
			eh.cardnum = (TextView) view.findViewById(R.id.exchange_card_number);
			eh.password = (TextView) view.findViewById(R.id.exchange_card_pwd);

			view.setTag(eh);
			contentView = view;
		} else {
			eh = (ExchangeHolder) contentView.getTag();
		}

		if (ei.metatype == Constants.EXCHANGE_META_TYPE_CARD) {
			switch (ei.operator) {
			case Operator.OPERATOR_MOBILE:
				eh.operatorIcon.setImageResource(R.drawable.opeartor_cm);

				break;
			case Operator.OPERATOR_UNICOM:
				eh.operatorIcon.setImageResource(R.drawable.operator_cu);

				break;
			case Operator.OPERATOR_TELCOM:
				eh.operatorIcon.setImageResource(R.drawable.operator_ct);

				break;
			default:
				break;
			}

			eh.cardnum.setText(ei.getCardnum());
			eh.password.setText(ei.getPassword());
			eh.date.setText(String.format(mContext.getString(R.string.exchange_history_list_item_date), ei.getDate()));
			eh.expire.setText(String.format(mContext.getString(R.string.exchange_history_list_item_expire_date), ei.getExpire()));
		}

		return contentView;
	}

	class ExchangeHolder {
		ImageView operatorIcon;
		ImageView propicon;
		TextView date;
		TextView expire;
		TextView cardnum;
		TextView password;
	}
}
