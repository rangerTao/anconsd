package com.ranger.bmaterials.adapter;

import java.util.ArrayList;

import android.widget.Toast;

import com.ranger.bmaterials.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 推荐搜索关键字
* @Description: TODO
* 
* @author taoliang(taoliang@baidu-mgame.com)
* @date 2014年6月9日 下午3:29:26 
* @version V
*
 */
public class RecommendKeywordsAdapter extends BaseAdapter{

	private Context mContext;
	ArrayList<String> mkeys;
	private ItemClickListener mListener;
	
	public RecommendKeywordsAdapter(Context context,ArrayList<String> keywords){
		mContext = context;
		mkeys = keywords;
	}
	
	public void setOnItemClickListener(ItemClickListener listener){
		mListener = listener;
	}

    public void setData(ArrayList<String> keys){
        mkeys = keys;
        notifyDataSetChanged();
    }
	
	@Override
	public int getCount() {
		return mkeys.size();
	}

	@Override
	public Object getItem(int position) {
		return mkeys.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		KeyHolder kh;
		
		View keyword = null;
		
		if(convertView == null){
			kh = new KeyHolder();
			
			keyword = View.inflate(mContext, R.layout.search_recom_keyword_item, null);
			kh.tv_keyword = (TextView) keyword.findViewById(R.id.tv_search_recom_keyword);
			
			keyword.setTag(kh);
			convertView = keyword;
		}else{
			kh = (KeyHolder) convertView.getTag();
		}
		
		kh.tv_keyword.setText(mkeys.get(position));
		kh.key = mkeys.get(position);
		
		if(keyword!=null){
	        keyword.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                if(mListener!= null){
	                	KeyHolder kh = (KeyHolder) v.getTag();
	                	mListener.onItemClick(kh.key);
	                }
	            }
	        });
		}
		
		return convertView;
	}

	class KeyHolder{
		TextView tv_keyword;
		String key;
	}
	
	public interface ItemClickListener {
		public abstract void onItemClick(String key);
	}
	
}
