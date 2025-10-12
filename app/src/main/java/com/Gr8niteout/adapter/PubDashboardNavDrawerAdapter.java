package com.Gr8niteout.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;

import java.util.ArrayList;

public class PubDashboardNavDrawerAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<String> navDrawerItems;

    public PubDashboardNavDrawerAdapter(Context context, ArrayList<String> navDrawerItems){
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.pub_dashboard_drawer_list_item, null);
        }

        TextView txtTitle =  convertView.findViewById(R.id.title);
        CommonUtilities.setFontFamily(context, txtTitle, CommonUtilities.AvenirLTStd_Medium);

        txtTitle.setText(navDrawerItems.get(position));

        txtTitle.setTextColor(AppCompatResources.getColorStateList(context,R.color.nav_slider_text));

        return convertView;
    }
}
