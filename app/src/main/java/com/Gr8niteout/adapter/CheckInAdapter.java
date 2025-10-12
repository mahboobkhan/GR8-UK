package com.Gr8niteout.adapter;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CircleTransform;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.model.CheckedInUserModel;
import com.Gr8niteout.pub.CheckInFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CheckInAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private final boolean isBuyVisible;

    private Context mContext;
    private List<CheckedInUserModel.CheckInData> mList;
    private CheckInFragment.OnClickCancelListener mListener;
    private CheckInFragment.OnLoadMoreListener onLoadMoreListener;

    private int visibleThreshold = 3;
    private boolean isLoading = false;

    public CheckInAdapter(Context mContext, List<CheckedInUserModel.CheckInData> mList, CheckInFragment.OnClickCancelListener mListener, CheckInFragment.OnLoadMoreListener onLoadMoreListener, RecyclerView mRecyclerView, String pub_type) {
        this.mContext = mContext;
        this.mList = mList;
        this.mListener = mListener;
        this.onLoadMoreListener = onLoadMoreListener;
        this.isBuyVisible = !TextUtils.isEmpty(pub_type) && pub_type.equals("1");

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && (totalItemCount <= lastVisibleItem + visibleThreshold)) {
                    if (CheckInAdapter.this.onLoadMoreListener != null) {
                        CheckInAdapter.this.onLoadMoreListener.onLoadMore();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_in, parent, false);
            return new ItemHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ItemHolder mViewHolder = (ItemHolder) holder;
            final int pos = holder.getAdapterPosition();

            CheckedInUserModel.CheckInData item = mList.get(position);
            if (position % 2 == 0) {
                mViewHolder.listcolor.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                mViewHolder.listcolor.setBackgroundColor(ContextCompat.getColor(mContext, R.color.listodd));
            }

            mViewHolder.txtName.setText(item.first_name + " " + item.last_name);
            mViewHolder.days_ago.setText(item.checkin_datetime);

            mViewHolder.First_Lett.setVisibility(View.GONE);

            if (TextUtils.isEmpty(item.profile_image)) {
                mViewHolder.imageView.setImageResource(R.mipmap.no_user);
                if (!TextUtils.isEmpty(item.first_name)) {
                    mViewHolder.First_Lett.setVisibility(View.VISIBLE);
                    mViewHolder.First_Lett.setText(item.first_name.substring(0, 1).toUpperCase());
                } else {
                    mViewHolder.imageView.setImageResource(R.mipmap.user);
                    mViewHolder.First_Lett.setVisibility(View.GONE);
                }
            } else {
                Picasso.get()
                        .load(CommonUtilities.Gr8niteoutURL + CommonUtilities.User_Profile_URL +
                                item.profile_image)
                        .error(R.mipmap.user).placeholder(R.mipmap.user)
                        .transform(new CircleTransform())
                        .into(mViewHolder.imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
            }

            mViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(pos);
                }
            });

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
        TextView txtName;
        TextView days_ago;
        TextView txtBuyCredits;
        ImageView imageView;
        LinearLayout listcolor;
        RelativeLayout image_layout;
        TextView First_Lett;
        LinearLayout btnBuyCredit;

        public ItemHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            days_ago = itemView.findViewById(R.id.days_ago);
            txtBuyCredits = itemView.findViewById(R.id.txtBuyCredits);
            imageView = itemView.findViewById(R.id.imageView);
            listcolor = itemView.findViewById(R.id.listcolor);
            image_layout = itemView.findViewById(R.id.image_layout);
            First_Lett = itemView.findViewById(R.id.First_Lett);
            btnBuyCredit = itemView.findViewById(R.id.btn_buy_credit);

            CommonUtilities.setFontFamily(mContext, txtName, CommonUtilities.AvenirNextLTPro_Demi);
            CommonUtilities.setFontFamily(mContext, days_ago, CommonUtilities.AvenirLTStd_Medium);
            CommonUtilities.setFontFamily(mContext, txtBuyCredits, CommonUtilities.AvenirLTStd_Medium);

            if (isBuyVisible) {
                btnBuyCredit.setVisibility(View.VISIBLE);
            } else {
                btnBuyCredit.setVisibility(View.GONE);
            }
        }
    }

    class LoadingHolder extends RecyclerView.ViewHolder {

        public LoadingHolder(View itemView) {
            super(itemView);
        }
    }
}
