package com.ranger.bmaterials.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.tools.install.PackageUtils;
import com.ranger.bmaterials.view.StickyListHeadersAdapter;

public class InstalledAppListAdapter extends AbstractListAdapter<InstalledAppInfo> implements StickyListHeadersAdapter, SectionIndexer {

    private SharedPreferences spSize;

    private Context mContext;

    private PackageUtils pUtil;

    private final int REFRESH_SINGLE_SIZE = 1;

	private static final String TAG = "InstalledAppListAdapter";
	/* 保存每个section的索引位置 */
	private int[] sectionIndices;
	/* 保存每个section的标记（首字母） */
	private Character[] sectionsLetters;

	public InstalledAppListAdapter(Context context) {
		super(context);
        pUtil = new PackageUtils();
        mContext = context;
        spSize = mContext.getSharedPreferences("app_size",Context.MODE_PRIVATE);

    }

    HashMap<String, TextView> kvMap = new HashMap<String, TextView>();

    Handler mHandler = new Handler(GameTingApplication.getAppInstance().getMainLooper()){
        public void handleMessage(Message msg) {

            switch (msg.what){
                case REFRESH_SINGLE_SIZE:

                    TextView size = (TextView) msg.obj;
                    long appSize = msg.getData().getLong("size");

                    size.setText(StringUtil.getDisplaySize(appSize));

                    break;
            }

        };
    };
	/**
	 * 获取每个section的标记(首字母)
	 * 
	 * @return
	 */
	private Character[] getStartingLetters(List<InstalledAppInfo> data) {
		Character[] letters = new Character[sectionIndices.length];
		for (int i = 0; i < sectionIndices.length; i++) {
			letters[i] = data.get(sectionIndices[i]).getPinyinName().charAt(0);
		}
		return letters;
	}

	/**
	 * 获取每个section在数据中的索引位置（注意数据首先保证是有序）
	 * 
	 * @return
	 */
	private int[] getSectionIndices(List<InstalledAppInfo> data) {

		List<Integer> sectionIndices = new ArrayList<Integer>();
		String pinyin = data.get(0).getPinyinName();
		if (pinyin == null) {
			pinyin = "#";
		}
		char lastFirstChar = pinyin.charAt(0);// lastFirstChar设置为第一个条目的首字母(null
												// poniter?)
		sectionIndices.add(0);
		int size = data.size();
		for (int i = 1; i < size; i++) {
			String pinyinName = data.get(i).getPinyinName();
			if (pinyinName == null) {
				pinyinName = "#";
			}
			if (pinyinName.charAt(0) != lastFirstChar) {
				lastFirstChar = pinyinName.charAt(0);
				sectionIndices.add(i);
			}
		}
		int[] sections = new int[sectionIndices.size()];
		for (int i = 0; i < sectionIndices.size(); i++) {
			sections[i] = sectionIndices.get(i);
		}
		return sections;
	}

	static class AppInfoViewHolder {
        String pkgName;
		TextView title;
		TextView version;
		TextView size;
		ImageView icon;
	}

	class HeaderViewHolder {
		TextView sectionHeader;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final AppInfoViewHolder appInfoView;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.manager_activity_installed_list_item, parent, false);
			appInfoView = new AppInfoViewHolder();
			appInfoView.title = (TextView) view.findViewById(R.id.manager_activity_installed_list_item_name);
			appInfoView.icon = (ImageView) view.findViewById(R.id.manager_activity_installed_list_item_icon);
			appInfoView.version = (TextView) view.findViewById(R.id.manager_activity_installed_list_item_version);
			appInfoView.size = (TextView) view.findViewById(R.id.manager_activity_installed_list_item_size);
			// appInfoView.icon.setOnClickListener(this);
			view.setTag(appInfoView);
		} else {
			view = convertView;
			appInfoView = (AppInfoViewHolder) view.getTag();
		}

		InstalledAppInfo item = getItem(position);
		appInfoView.title.setText(item.getName());
		appInfoView.version.setText(mContext.getString(R.string.app_info_version_dis,item.getVersion()));

        try {

            String size_from_sp = spSize.getString(item.getPackageName(),item.getSize() + "");

            if(size_from_sp == null || size_from_sp.equals("") || size_from_sp.toLowerCase().equals("null")){
                appInfoView.size.setText(StringUtil.getDisplaySize(item.getSize()));
            }else{
                appInfoView.size.setText(StringUtil.getDisplaySize(size_from_sp));
            }

            final String pkgname = item.getPackageName();
            final TextView tvSize = appInfoView.size;

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try{
                        if(!kvMap.containsKey(pkgname)){
                            pUtil.getpkginfoAsync(context, pkgname, new InstalledPackageStatsObserver(appInfoView));
                            kvMap.put(pkgname, tvSize);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        appInfoView.pkgName = item.getPackageName();

		Drawable icon = null;

		icon = item.getDrawable();
		if (icon == null) {
			appInfoView.icon.setImageResource(R.drawable.game_icon_list_default);
		} else {
			appInfoView.icon.setImageDrawable(icon);
		}
		appInfoView.icon.setTag(position);
		return view;
	}

    private void partlyRefresh(AppInfoViewHolder app,String packageName, long size){

        if(app.pkgName.equals(packageName)){

            Message msg = mHandler.obtainMessage();
            msg.what = REFRESH_SINGLE_SIZE;
            Bundle data = new Bundle();
            data.putLong("size", size);
            msg.setData(data);
            msg.obj = app.size;

            mHandler.sendMessage(msg);
            Editor editor = spSize.edit();
            editor.putString(packageName,size + "");
            editor.commit();
        }

    }

	// //////////////////////////////////////////////////////
	// SectionIndexer callbacks
	// //////////////////////////////////////////////////////

	/**
	 * 
	 * @param orderIndex
	 *            0--26,分别表示#,a,b,...,z
	 * @return
	 */
	private int getLetter(int orderIndex, boolean isUpper) {
		if (orderIndex == 0) {
			return '#';
		}
		if (isUpper) {
			return (orderIndex + 'A' - 1);
		} else {
			return (orderIndex + 'a' - 1);
		}

	}

	private int getRealSection(int sideSection) {
		// add
		if (sectionsLetters == null) {
			return -1;
		}
		for (int i = 0; i < sectionsLetters.length; i++) {
			if (sectionsLetters[i].charValue() == getLetter(sideSection, false)) {
				return i;
			}
		}
		return -1;
	}

	private int getSideSection(int realSection) {
		for (int i = 0; i < 27; i++) {
			if (sectionsLetters[realSection].charValue() == getLetter(i, false)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * fastScrollBar即使存在，系统也不会调用，这个是我们自己使用的，所以需要注意section参数
	 */
	@Override
	public int getPositionForSection(int sideSection) {
		int realSection = getRealSection(sideSection);
		if (realSection == -1) {
			return -1;
		}
		return sectionIndices[realSection];
	}

	@Override
	public int getSectionForPosition(int position) {
		int realSection = sectionIndices.length - 1;
		for (int i = 0; i < sectionIndices.length; i++) {
			if (position < sectionIndices[i]) {
				realSection = i - 1;
				break;
			}
		}
		if (Constants.DEBUG)
			Log.i(TAG, "getSectionForPosition position:" + position + " return:" + (sectionIndices.length - 1));
		return getSideSection(realSection);
	}

	@Override
	public Object[] getSections() {
		if (Constants.DEBUG)
			Log.i(TAG, "getSections " + Arrays.toString(sectionsLetters));
		return sectionsLetters;
	}

	// //////////////////////////////////////////////////////

	// //////////////////////////////////////////////////////
	// StickyListHeadersAdapter callbacks
	// //////////////////////////////////////////////////////
	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		if (Constants.DEBUG)
			Log.i("TestBaseAdapter", "getHeaderView " + position);
		HeaderViewHolder holder;
		if (convertView == null) {
			holder = new HeaderViewHolder();
			convertView = mInflater.inflate(R.layout.manager_activity_installed_list_header, parent, false);
			holder.sectionHeader = (TextView) convertView.findViewById(R.id.manager_activity_installed_list_header_text);
			convertView.setTag(holder);
		} else {
			holder = (HeaderViewHolder) convertView.getTag();
		}

		
		InstalledAppInfo item = getItem(position);
		char headerChar = item.getPinyinName().toUpperCase().subSequence(0, 1).charAt(0);
		holder.sectionHeader.setText(String.valueOf(headerChar));
		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		InstalledAppInfo item = getItem(position);
		return item.getPinyinName().subSequence(0, 1).charAt(0);
	}

	// //////////////////////////////////////////////////////

	public void clear() {
	}

	@Override
	public void setData(List<InstalledAppInfo> data) {
		if (data != null && data.size() > 0) {
			sectionIndices = getSectionIndices(data);
			sectionsLetters = getStartingLetters(data);
		} else {
			clear();
		}
		super.setData(data);

	}

	@Override
	public void onClick(View v) {
		if (onListItemClickListener == null) {
			return;
		}
		onListItemClickListener.onItemIconClick(v, (Integer) v.getTag());
	}

    class InstalledPackageStatsObserver extends IPackageStatsObserver.Stub {

        private AppInfoViewHolder app;

        public InstalledPackageStatsObserver(AppInfoViewHolder appinfo){
            app = appinfo;
        }

        @SuppressLint("NewApi")
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {

            long size = pStats.cacheSize + pStats.codeSize + pStats.dataSize ;

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){

                size += pStats.externalCacheSize + pStats.externalDataSize + pStats.externalMediaSize + pStats.externalCodeSize + pStats.externalObbSize;
            }

            if(app != null && app.size != null ){
                partlyRefresh(app,pStats.packageName,size);
            }

        }
    }

}
