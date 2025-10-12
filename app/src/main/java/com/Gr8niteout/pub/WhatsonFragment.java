package com.Gr8niteout.pub;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.MyApplication;
import com.Gr8niteout.config.RoundedTransformation;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.WhatsOnModel;
import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.resource.drawable.GlideDrawable;
//import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.analytics.HitBuilders;

import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class WhatsonFragment extends Fragment {

    PubActivity mActivity;
    int position;
    WhatsOnModel model2;
    public RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecycleAdapter recyclerViewAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    String pub_id = "";
    ImageView event_image;
    TextView no_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_whatson, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        no_text = (TextView) view.findViewById(R.id.no_text);
        event_image = (ImageView) view.findViewById(R.id.event_image);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new RecycleAdapter();
        // recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(20));

        if (getArguments() != null && getArguments().containsKey(CommonUtilities.key_pub_id)) {
            pub_id = getArguments().getString(CommonUtilities.key_pub_id);
            WhatsonCall(true);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getArguments() != null && getArguments().containsKey(CommonUtilities.key_pub_id)) {
                    pub_id = getArguments().getString(CommonUtilities.key_pub_id);
                    WhatsonCall(false);
                }
            }
        });


        return view;
    }

    public void WhatsonCall(final boolean loader) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(CommonUtilities.key_pub_id, pub_id);
//        params.put(CommonUtilities.key_pub_id,"306");
        ServerAccess.getResponse(mActivity, CommonUtilities.key_get_whatson, params, loader, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                model2 = new WhatsOnModel().WhatsOnModel(result);
                if (model2 != null) {
                    if (model2.response.status.equals(CommonUtilities.key_Success)) {
                        CommonUtilities.setPreference(mActivity, CommonUtilities.pref_whats_on, result);
                        recyclerViewAdapter = new RecycleAdapter();
                        recyclerView.setAdapter(recyclerViewAdapter);
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        no_text.setText(model2.response.msg);
                        no_text.setVisibility(View.VISIBLE);
                        event_image.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onError(String error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (PubActivity) activity;

    }


    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_whatson, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mainHead.setText(model2.response.whatson.get(position).title);
            holder.desc.setText(model2.response.whatson.get(position).description);

            if (model2.response.whatson.get(position).image.equals(""))
            {
                holder.image_layout.setVisibility(View.GONE);
                holder.upper_layout.setBackgroundResource(R.drawable.corner_rounded);

            } else {

                Log.d("ImageUrl", "onBindViewHolder1:----- "+CommonUtilities.Gr8niteoutURL );

                Log.d("ImageUrl", "onBindViewHolder2:----- " + CommonUtilities.Whats_On_URL );

                Log.d("ImageUrl", "onBindViewHolder3:----- "+ model2.response.whatson.get(position).image);

                Glide.with(mActivity)
                        .load(
                                model2.response.whatson.get(position).image) // Uri of the picture
                        .transform(new RoundedCornersTransformation(15, 0, RoundedCornersTransformation.CornerType.BOTTOM)) // Add corner transformation
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                // Handle error
                                holder.image_layout.setVisibility(View.GONE);
                                holder.upper_layout.setBackgroundResource(R.drawable.corner_rounded);
                                return false; // Return false to allow Glide to handle error placeholder
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                // Handle success
                                holder.imageViewHolder.setVisibility(View.GONE);
                                return false; // Return false to allow Glide to display the image
                            }
                        })
                        .into(holder.imageView);

//                Glide.with(mActivity)
//                        .load(CommonUtilities.Gr8niteoutURL + CommonUtilities.Whats_On_URL +
//                                model2.response.whatson.get(position).image)// Uri of the picture
//                        .bitmapTransform(new RoundedTransformation(mActivity,15,0, RoundedTransformation.CornerType.BOTTOM))
//                        .listener(new RequestListener<String, GlideDrawable>() {
//                            @Override
//                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//
//                                holder.image_layout.setVisibility(View.GONE);
//                                holder.upper_layout.setBackgroundResource(R.drawable.corner_rounded);
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                holder.imageViewHolder.setVisibility(View.GONE);
//                                return false;
//                            }
//                        })
//                        .into(holder.imageView);
//


               /* Picasso.with(mActivity)
                        .load(CommonUtilities.Gr8niteoutURL + CommonUtilities.Whats_On_URL +
                                model2.response.whatson.get(position).image)
                        .fit()
                        .transform(new RoundedTransformation_picasso(23, RoundedTransformation_picasso.Corners.BOTTOM))
                        .into(holder.imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.e("success","success");
                                holder.imageViewHolder.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                Log.e("error","error");
                                holder.image_layout.setVisibility(View.GONE);
                                holder.upper_layout.setBackgroundResource(R.drawable.corner_rounded);
                            }
                        });*/
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });


            CommonUtilities.setFontFamily(mActivity, holder.desc, CommonUtilities.AvenirNextLTPro_Demi);
            CommonUtilities.setFontFamily(mActivity, holder.mainHead, CommonUtilities.AvenirNextLTPro_Demi);

        }

        @Override
        public int getItemCount() {
            return model2.response.whatson.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView mainHead;
            TextView desc;
            ImageView imageView;
            ImageView imageViewHolder;
            RelativeLayout image_layout;
            LinearLayout upper_layout;

            public ViewHolder(View view) {
                super(view);
                mainHead = (TextView) view.findViewById(R.id.main_head);
                desc = (TextView) view.findViewById(R.id.desc);
                imageView = (ImageView) view.findViewById(R.id.imageView);
                imageViewHolder = (ImageView) view.findViewById(R.id.imageViewHolder);
                image_layout = (RelativeLayout) view.findViewById(R.id.image_layout);
                upper_layout = (LinearLayout) view.findViewById(R.id.upper_layout);
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        ((MyApplication)mActivity.getApplication()).getDefaultTracker().setScreenName("Whats on Screen");
        ((MyApplication)mActivity.getApplication()).getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

}
