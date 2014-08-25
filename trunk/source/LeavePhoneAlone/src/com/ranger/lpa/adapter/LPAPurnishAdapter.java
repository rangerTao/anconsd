package com.ranger.lpa.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ranger.lpa.LPApplication;
import com.ranger.lpa.MineProfile;
import com.ranger.lpa.R;
import com.ranger.lpa.pojos.PurnishInfo;
import com.ranger.lpa.pojos.PurnishList;

import org.w3c.dom.Text;

/**
 * Created by taoliang on 14-8-25.
 */
public class LPAPurnishAdapter extends BaseAdapter {

    LayoutInflater mInflater;
    PurnishList mPurnish;

    public LPAPurnishAdapter(){
        mInflater = LayoutInflater.from(LPApplication.getInstance().getApplicationContext());
        mPurnish = MineProfile.getInstance().getPurnish();
    }

    @Override
    public int getCount() {
        return mPurnish.getPurnishes().size();
    }

    @Override
    public Object getItem(int position) {
        return mPurnish.getPurnishes().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        PurnishHolder ph;
        PurnishInfo pi;

        if(convertView == null){
            view = mInflater.inflate(R.layout.item_purnish_list,null);
            ph = new PurnishHolder();

            ph.ivIndex = (ImageView) view.findViewById(R.id.iv_purnish_index);
            ph.tvTitle = (TextView) view.findViewById(R.id.purnish_index_title);
            ph.tvContent = (TextView) view.findViewById(R.id.purnish_content);

            view.setTag(ph);

            convertView = view;
        }else{
            ph = (PurnishHolder) convertView.getTag();
        }

        pi = (PurnishInfo) getItem(position);

        if(pi.isDefault()){
            ph.ivIndex.setImageResource(R.drawable.punish_selected);
        }

        ph.tvTitle.setText(pi.getPurnish_title());
        ph.tvContent.setText(pi.getPurnish_content());

        return convertView;
    }

    class PurnishHolder{

        ImageView ivIndex;
        TextView tvTitle;
        TextView tvContent;
    }

}
