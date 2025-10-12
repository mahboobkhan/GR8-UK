package com.Gr8niteout.search;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.model.PubModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PubListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private int visibleThreshold = 5;
    private boolean isLoading = false;
    private PubRecyclerActivity.OnLoadMoreListener onLoadMoreListener;
    private PubRecyclerActivity.OnClickListener mClickListener;

    private Context mContext;
    private List<PubModel.response.find_pub.find_pubs_lists> mList;

    public PubListAdapter(PubRecyclerActivity.OnLoadMoreListener onLoadMoreListener, Context mContext, List<PubModel.response.find_pub.find_pubs_lists> mList, RecyclerView mRecyclerView,
                          PubRecyclerActivity.OnClickListener mClickListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        this.mContext = mContext;
        this.mList = mList;
        this.mClickListener = mClickListener;

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && (totalItemCount <= lastVisibleItem + visibleThreshold)) {
                    if (PubListAdapter.this.onLoadMoreListener != null) {
                        PubListAdapter.this.onLoadMoreListener.onLoadMore();
                    }
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false);
            return new LoadingHolder(view);
        } else if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pub_list_item, parent, false);
            return new ItemHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemHolder) {
            final ItemHolder itemHolder = (ItemHolder) holder;

            itemHolder.txtPubName.setText(mList.get(position).pub_name);

            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onClick(holder.getLayoutPosition());
                }
            });
            if (mList.get(position).currency.equals("1"))
                itemHolder.txtPrice.setText("£" + mList.get(position).avg_price);
            else if (mList.get(position).currency.equals("2"))
                itemHolder.txtPrice.setText("€" + mList.get(position).avg_price);
            else if (mList.get(position).currency.equals("3"))
                itemHolder.txtPrice.setText("$" + mList.get(position).avg_price);

            itemHolder.txtPrice.setBackgroundResource(R.drawable.square_edit_text_transfer);

            if (mList.get(position).status.equals("1"))
                itemHolder.txtStatus.setText("Now Open");
            else
                itemHolder.txtStatus.setText("Now Closed");

            if (!mList.get(position).distance.equals("") && !mList.get(position).equals("0"))
                itemHolder.txtMiles.setText(CommonUtilities.doRound(Double.parseDouble(mList.get(position).distance)) + " mi");

//            changed on 28-jan-2019
            Picasso.get().load(CommonUtilities.Gr8niteoutURL + CommonUtilities.Pub_page_URL + mList.get(position).pub_image)
                    .into(itemHolder.imgPub, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.i("", "onsuccess " + position);

                            itemHolder.layoutGray.setBackgroundResource(R.mipmap.search_result_transparent);
                            itemHolder.layoutGray.setAlpha(.5f);
                            itemHolder.imgPlaceHolder.setVisibility(View.GONE);
                            itemHolder.layoutGray.setBackgroundResource(R.mipmap.search_result_transparent);
                            itemHolder.layoutGray.setAlpha(.5f);
                        }

                        @Override
                        public void onError(Exception e) {
                            itemHolder.imgPlaceHolder.setVisibility(View.VISIBLE);
                        }
                    });

            itemHolder.ratingBar1.setRating(Float.parseFloat(mList.get(position).rating));
            itemHolder.ratingBar1.setIsIndicator(true);
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        private RelativeLayout layoutGray;
        private ImageView imgPub;
        private ImageView imgPlaceHolder;
        private RatingBar ratingBar1;
        private TextView txtPubName;
        private TextView txtPrice;
        private TextView txtStatus;
        private TextView txtMiles;

        public ItemHolder(View view) {
            super(view);

            layoutGray = (RelativeLayout) view.findViewById(R.id.layoutGray);
            imgPub = (ImageView) view.findViewById(R.id.imgPub);
            imgPlaceHolder = (ImageView) view.findViewById(R.id.imgPlaceHolder);
            ratingBar1 = (RatingBar) view.findViewById(R.id.ratingBar1);
            txtPubName = (TextView) view.findViewById(R.id.txtPubName);
            txtPrice = (TextView) view.findViewById(R.id.txtPrice);
            txtStatus = (TextView) view.findViewById(R.id.txtStatus);
            txtMiles = (TextView) view.findViewById(R.id.txtMiles);

            CommonUtilities.setFontFamily(mContext, txtPubName, CommonUtilities.AvenirNextLTPro_Regular);
            CommonUtilities.setFontFamily(mContext, txtPrice, CommonUtilities.AvenirNextLTPro_Regular);
            CommonUtilities.setFontFamily(mContext, txtMiles, CommonUtilities.AvenirNextLTPro_Regular);
            CommonUtilities.setFontFamily(mContext, txtStatus, CommonUtilities.AvenirNextLTPro_Regular);
        }
    }

    class LoadingHolder extends RecyclerView.ViewHolder {

        public LoadingHolder(View itemView) {
            super(itemView);
        }
    }
}
