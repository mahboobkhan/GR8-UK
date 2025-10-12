package com.Gr8niteout.adapter;


import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.model.HomeData.response.home_data.home_pubinfo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NearByPubAdapter extends RecyclerView.Adapter<NearByPubAdapter.MyViewHolder> {

    ArrayList<home_pubinfo> model;
    private Activity mContext;

    public NearByPubAdapter(Activity mContext, ArrayList<home_pubinfo> model) {
        this.model = model;
        this.mContext = mContext;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgPub;
        ImageView imgPlaceHolder;
        private RatingBar ratingBar1;
        private TextView txtPubName;
        private TextView txtPrice;
        private TextView txtStatus;
        private TextView txtMiles;
        LinearLayout layoutGray;
        LinearLayout linearlayout;

        public MyViewHolder(View view) {
            super(view);
            imgPub = (ImageView) view.findViewById(R.id.imgPub);
            imgPlaceHolder = (ImageView) view.findViewById(R.id.imgPlaceHolder);
            ratingBar1 = (RatingBar) view.findViewById(R.id.ratingBar1);
            txtPubName = (TextView) view.findViewById(R.id.txtPubName);
            txtPrice = (TextView) view.findViewById(R.id.txtPrice);
            txtStatus = (TextView) view.findViewById(R.id.txtStatus);
            txtMiles = (TextView) view.findViewById(R.id.txtMiles);
            layoutGray = (LinearLayout) view.findViewById(R.id.layoutGray);
            linearlayout = (LinearLayout) view.findViewById(R.id.linearlayout);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pub_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        if (model.size() == 1) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.MATCH_PARENT
            );
            holder.linearlayout.setLayoutParams(lp);
        }
        holder.txtPubName.setText(model.get(position).pub_name);

        if (model.get(position).currency.equals("1"))
            holder.txtPrice.setText("£" + model.get(position).price);
        else if (model.get(position).currency.equals("2"))
            holder.txtPrice.setText("€" + model.get(position).price);
        else if (model.get(position).currency.equals("3"))
            holder.txtPrice.setText("$" + model.get(position).price);

        if (model.get(position).price.equals(""))
            holder.txtPrice.setVisibility(View.GONE);

        holder.txtPrice.setBackgroundResource(R.drawable.square_edit_text_transfer);

        if (model.get(position).status == 1)
            holder.txtStatus.setText("Now Open");
        else
            holder.txtStatus.setText("Now Closed");

        if (!model.get(position).distance.equals("") && !model.get(position).distance.equals("0"))
            holder.txtMiles.setText(CommonUtilities.doRound(Double.parseDouble(model.get(position).distance)) + " mi");


//        changed on 28-jan-2019
        Picasso.get()
                .load(CommonUtilities.Gr8niteoutURL + CommonUtilities.Pub_page_URL + model.get(position).profile_pic)
                .into(holder.imgPub, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.imgPlaceHolder.setVisibility(View.GONE);
                        holder.layoutGray.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.layoutGray.setVisibility(View.GONE);
                    }
                });
        holder.ratingBar1.setRating(Float.parseFloat(model.get(position).rating));
        holder.ratingBar1.setIsIndicator(true);

        CommonUtilities.setFontFamily(mContext, holder.txtPubName, CommonUtilities.AvenirNextLTPro_Regular);
        CommonUtilities.setFontFamily(mContext, holder.txtPrice, CommonUtilities.AvenirNextLTPro_Regular);
        CommonUtilities.setFontFamily(mContext, holder.txtMiles, CommonUtilities.AvenirNextLTPro_Regular);
    }

    @Override
    public int getItemCount() {
        return model.size();
    }
}
