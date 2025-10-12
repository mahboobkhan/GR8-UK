package com.Gr8niteout.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.model.CountryModel.response.country_list;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by sudhansu on 1/6/2017.
 */

public class CountryListAdapter extends BaseAdapter {

    ArrayList<country_list> model;
    ArrayList<country_list> model_country;
    Context context;

    public CountryListAdapter(Context context, ArrayList<country_list>  model) {
        this.model = model;
        this.context = context;
        this.model_country = new ArrayList<country_list>();
        this.model_country.addAll(model);
    }

    @Override
    public int getCount() {
        return model.size();
    }
    @Override
    public Object getItem(int position) {
        return model.get(position);
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
            convertView = mInflater.inflate(R.layout.country_list_row, null);
        }

        TextView countryname=(TextView) convertView.findViewById(R.id.CoutryName);
        TextView countrycode=(TextView) convertView.findViewById(R.id.CoutryCode);
        countryname.setText(model.get(position).country_name);
        countrycode.setText("+"+model.get(position).numcode);

        CommonUtilities.setFontFamily(context, countryname, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(context, countrycode, CommonUtilities.AvenirLTStd_Medium);

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        model.clear();
        if (charText.length() == 0) {
            model.addAll(model_country);
        } else {
            for (country_list wp : model_country) {
                if (wp.country_name.toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.numcode.toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    model.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }


}
