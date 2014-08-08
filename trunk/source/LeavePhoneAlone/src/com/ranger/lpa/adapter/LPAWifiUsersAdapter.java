package com.ranger.lpa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ranger.lpa.R;
import com.ranger.lpa.pojos.WifiUser;
import com.ranger.lpa.utils.WifiUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by taoliang on 14-8-5.
 */
public class LPAWifiUsersAdapter extends BaseAdapter {

    int[] user_heads = {R.drawable.user_head_1,
            R.drawable.user_head_2,
            R.drawable.user_head_3,
            R.drawable.user_head_4,
            R.drawable.user_head_5};

    LinkedList<WifiUser> users;
    private Context mContext;
    private LayoutInflater mInflater;

    public LPAWifiUsersAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        users = new LinkedList<WifiUser>();
    }

    public void addUser(WifiUser user) {

        users.add(user);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        UserHolder uh;
        View userView;
        WifiUser wu = (WifiUser) getItem(position);

        if (convertView == null) {

            uh = new UserHolder();

            userView = mInflater.inflate(R.layout.layout_wifi_barcode_users, null);

            uh.userhead = (ImageView) userView.findViewById(R.id.iv_joined_user_head);
            uh.tvUserName = (TextView) userView.findViewById(R.id.tv_joined_user_name);

            userView.setTag(wu);
            convertView = userView;
        } else {
            uh = (UserHolder) convertView.getTag();
        }

        int index = Math.abs(new Random().nextInt() % user_heads.length);

        uh.userhead.setImageResource(user_heads[index]);
        uh.tvUserName.setText(wu.getName());

        return convertView;
    }

    class UserHolder {
        String name;
        String udid;
        ImageView userhead;
        TextView tvUserName;
    }
}
