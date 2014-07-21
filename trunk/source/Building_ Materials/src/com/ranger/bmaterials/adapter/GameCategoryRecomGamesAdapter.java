package com.ranger.bmaterials.adapter;

import java.net.ContentHandler;

import android.text.TextUtils;
import android.widget.*;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.mode.GameTypeInfo;
import com.ranger.bmaterials.tools.UIUtil;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;

/**
 * 
* @Description: TODO
* 
* @author taoliang(taoliang@baidu-mgame.com)
* @date 2014年6月11日 上午10:39:04 
* @version V
*
 */
public class GameCategoryRecomGamesAdapter extends BaseAdapter{
	
	private Context mContext;
	private GameTypeInfo mInfo;
    private int colNum;
	
	public GameCategoryRecomGamesAdapter(Context context,GameTypeInfo cateInfo,int col){
		mContext = context;
		mInfo = cateInfo;
        colNum = col;
	}

	@Override
	public int getCount() {
		return (mInfo.getGames().size() % colNum) > 0 ? ((mInfo.getGames().size() / colNum) + 1) * colNum : mInfo.getGames().size();
	}

	@Override
	public Object getItem(int position) {

        if(position >= mInfo.getGames().size()){
            return null;
        }

		return mInfo.getGames().get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

        String name = "";

        if(getItem(position) != null){
            name = mInfo.getGames().get(position).gamename;
        }

		TextView tvName = (TextView) View.inflate(mContext, R.layout.game_category_recom_item, null);
		tvName.setText(name);
        tvName.setSingleLine();
        tvName.setEllipsize(TextUtils.TruncateAt.END);
		return tvName;
	}

}
