package com.Gr8niteout.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;

import java.util.ArrayList;


public class NavDrawerListAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<String> navDrawerItems;
	private ArrayList<Integer> imgItems;
	private int previousPos = -1;
	
	public NavDrawerListAdapter(Context context, ArrayList<String> navDrawerItems, ArrayList<Integer> imgItems){
		this.context = context;
		this.navDrawerItems = navDrawerItems;
		this.imgItems = imgItems;
	}

	@Override
	public int getCount() {
		return navDrawerItems.size()-1;
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
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }

		LinearLayout line = (LinearLayout)convertView.findViewById(R.id.line);
		TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
		ImageView imgDrawerItem = (ImageView) convertView.findViewById(R.id.imgDrawerItem);
		imgDrawerItem.setImageResource(imgItems.get(position));
		CommonUtilities.setFontFamily(context, txtTitle, CommonUtilities.AvenirLTStd_Medium);

		txtTitle.setText(navDrawerItems.get(position));

		if(txtTitle.getText().equals("Share App"))
			txtTitle.setTextColor(context.getResources().getColorStateList(R.color.slider_share));
		else
			txtTitle.setTextColor(context.getResources().getColorStateList(R.color.slider_text));

		if(position==4)
			line.setVisibility(View.VISIBLE);
		else
			line.setVisibility(View.GONE);
        return convertView;
	}

}
