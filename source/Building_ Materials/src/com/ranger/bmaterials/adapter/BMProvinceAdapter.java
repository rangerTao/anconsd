package com.ranger.bmaterials.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.netresponse.BMProvinceListResult;
import com.ranger.bmaterials.view.StickyListHeadersAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by taoliang on 14-7-22.
 */
public class BMProvinceAdapter extends AbstractListAdapter<BMProvinceListResult.ProviceItem> implements StickyListHeadersAdapter, SectionIndexer {

    private SharedPreferences spSize;

    private Context mContext;

    private final int REFRESH_SINGLE_SIZE = 1;

    private static final String TAG = "InstalledAppListAdapter";
    /* 保存每个section的索引位置 */
    private int[] sectionIndices;
    /* 保存每个section的标记（首字母） */
    private Character[] sectionsLetters;

    public BMProvinceAdapter(Context context,ArrayList<BMProvinceListResult.ProviceItem> pros) {
        super(context);
        mContext = context;
        data = pros;
    }


    /**
     * 获取每个section的标记(首字母)
     *
     * @return
     */
    private Character[] getStartingLetters(List<BMProvinceListResult.ProviceItem> data) {
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
    private int[] getSectionIndices(List<BMProvinceListResult.ProviceItem> data) {

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

    public void setAll(ArrayList<BMProvinceListResult.ProviceItem> ins){
        setData(ins);
        notifyDataSetChanged();
    }

    class HeaderViewHolder {
        TextView sectionHeader;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BMProvinceListResult.ProviceItem pi = getItem(position);

        View view;
        ProvinceHoder ph;

        if(convertView == null){

            view = LayoutInflater.from(mContext).inflate(R.layout.bm_layout_province_item,null);
            ph = new ProvinceHoder();

            ph.tvName = (TextView) view.findViewById(R.id.bm_tv_province_name);

            view.setTag(ph);

            convertView = view;
        }else{
            ph = (ProvinceHoder) convertView.getTag();
        }

        ph.tvName.setText(pi.getName());
        ph.id = pi.getId();

        return convertView;
    }

    class ProvinceHoder{
        int id;
        TextView tvName;
    }

    // //////////////////////////////////////////////////////
    // SectionIndexer callbacks
    // //////////////////////////////////////////////////////

    /**
     * @param orderIndex 0--26,分别表示#,a,b,...,z
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


        BMProvinceListResult.ProviceItem item = getItem(position);
        char headerChar = item.getPinyinName().toUpperCase().subSequence(0, 1).charAt(0);
        holder.sectionHeader.setText(String.valueOf(headerChar));
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        BMProvinceListResult.ProviceItem item = getItem(position);
        return item.getPinyinName().subSequence(0, 1).charAt(0);
    }

    // //////////////////////////////////////////////////////

    public void clear() {
    }

    @Override
    public void setData(List<BMProvinceListResult.ProviceItem> data) {
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
}
