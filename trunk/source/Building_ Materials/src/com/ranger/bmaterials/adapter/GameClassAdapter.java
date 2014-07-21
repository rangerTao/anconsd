package com.ranger.bmaterials.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.GameTypeInfo;
import com.ranger.bmaterials.tools.StringUtil;

public class GameClassAdapter extends BaseAdapter {

	// private GameClassActivity context;
	private LayoutInflater inflater;
	private ArrayList<GameTypeInfo> list;

	private int itemW;
	private int itemH;

	private DisplayImageOptions options = ImageLoaderHelper.getCustomOption(R.drawable.icon_default_small_game_class);
//	private DisplayImageOptions options = new DisplayImageOptions.Builder()
//	.cacheInMemory().cacheOnDisc()
//	.showStubImage(R.drawable.icon_default_small_game_class)
//	.showImageForEmptyUri(R.drawable.icon_default_small_game_class)
//	.showImageOnFail(R.drawable.icon_default_small_game_class)
//	.bitmapConfig(Bitmap.Config.RGB_565)// 减少内存占用 每像素站2byte 默认888占4byte
//	.imageScaleType(ImageScaleType.EXACTLY).build();


	public GameClassAdapter(Context c, ArrayList<GameTypeInfo> list,
			int itemW, int itemH) {
		inflater = LayoutInflater.from(c);
		this.list = list;
		// this.context = c;
		this.itemW = itemW;
		this.itemH = itemH;
		// this.textH = textH;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        MyHolder holder = null;

        if (convertView == null)
        {
            holder = new MyHolder();
            convertView = inflater.inflate(R.layout.item_gv_game_class_activity, null);

            holder.iv = (ImageView) convertView.findViewById(R.id.iv_gv_item_game_class_act);
            holder.tv = (TextView) convertView.findViewById(R.id.tv_label_gv_item_game_class_act);

            holder.tv.setTextColor(Color.WHITE);

            GridView.LayoutParams lp = new GridView.LayoutParams(itemW, itemH);
            // holder.iv.setLayoutParams(lp);
            convertView.setLayoutParams(lp);

            convertView.setTag(holder);
        }
        else
        {
            holder = (MyHolder) convertView.getTag();
        }
        
        int p = position % 4;
        
        switch (p)
        {
            case 0:
                holder.tv.setBackgroundColor(StringUtil.getColor("53B6DE"));
                break;
            case 1:
                holder.tv.setBackgroundColor(StringUtil.getColor("7DC255"));// DDEC00
                break;
            case 2:
                holder.tv.setBackgroundColor(StringUtil.getColor("D55062"));
                break;
            case 3:
                holder.tv.setBackgroundColor(StringUtil.getColor("601DB1"));
                break;
        }

        ImageLoaderHelper.displayImage(list.get(position).getGametypeicon(), holder.iv, options);
        holder.tv.setText(list.get(position).getGametypename());
        
        return convertView;
    }

	class MyHolder {
		ImageView iv;
		TextView tv;
	}

}
