package com.ranger.bmaterials.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.ranger.bmaterials.R;

public class SuggestAdapter extends BaseAdapter implements Filterable{
    private Context context;
    private List<String> mOriginalValues;	// 所有的Item

    public SuggestAdapter(Context context,
                          List<String> mOriginalValues, int maxMatch) {
        this.context = context;
        this.mOriginalValues = mOriginalValues;
    }

//    private class ArrayFilter extends Filter {
//        @Override
//        protected FilterResults performFiltering(CharSequence prefix) {
//            // TODO Auto-generated method stub
//            FilterResults results = new FilterResults();
//
//            // if (mOriginalValues == null) {
//            // synchronized (mLock) {
//            // mOriginalValues = new ArrayList<String>(mObjects);//
//            // }
//            // }
//
//            if (prefix == null || prefix.length() == 0) {
//                synchronized (mLock) {
//                    ArrayList<String> list = new ArrayList<String>(mOriginalValues);
//                    results.values = list;
//                    results.count = list.size();
//                    return results;
//                }
//            } else {
//                String prefixString = prefix.toString().toLowerCase();
//                final int count = mOriginalValues.size();
//                final ArrayList<String> newValues = new ArrayList<String>(count);
//                for (int i = 0; i < count; i++) {
//                    final String value = mOriginalValues.get(i);
//                    final String valueText = value.toLowerCase();
//                    // if(valueText.contains(prefixString)){//匹配所有
//                    // }
//                    // First match against the whole, non-splitted value
//                    if (valueText.startsWith(prefixString)) { // 源码 ,匹配开头
//                        newValues.add(value);
//                    }
//                    // else {
//                    // final String[] words = valueText.split(" ");//分隔符匹配，效率低
//                    // final int wordCount = words.length;
//                    //
//                    // for (int k = 0; k < wordCount; k++) {
//                    // if (words[k].startsWith(prefixString)) {
//                    // newValues.add(value);
//                    // break;
//                    // }
//                    // }
//                    // }
//                    if (maxMatch > 0) {// 有数量限制
//                        if (newValues.size() > maxMatch - 1) {// 不要太多
//                            break;
//                        }
//                    }
//                }
//
//                results.values = newValues;
//                results.count = newValues.size();
//            }
//
//            return results;
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint,
//                                      FilterResults results) {
//            mObjects = (List<String>) results.values;
//            if (results.count > 0) {
//                notifyDataSetChanged();
//            } else {
//                notifyDataSetInvalidated();
//            }
//        }
//
//    }

    @Override
    public int getCount() {
        return mOriginalValues.size() > 5 ? 5:mOriginalValues.size();
    }

    @Override
    public Object getItem(int position) {
        // 此方法有误，尽量不要使用
        return mOriginalValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(
                    R.layout.suggest_item, null);
            holder.tv = (TextView) convertView.findViewById(R.id.suggest_item_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv.setText(mOriginalValues.get(position));
		/*holder.iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String obj = mObjects.remove(position);
				mOriginalValues.remove(obj);
				notifyDataSetChanged();
			}
		});*/
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    class ViewHolder {
        TextView tv;
    }

    public List<String> getAllItems() {
        return mOriginalValues;
    }
}
