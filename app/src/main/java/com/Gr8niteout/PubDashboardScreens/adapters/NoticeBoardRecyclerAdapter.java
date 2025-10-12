package com.Gr8niteout.PubDashboardScreens.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Gr8niteout.PubDashboardScreens.AdapterCallback;
import com.Gr8niteout.PubDashboardScreens.Models.AddNewNoticeModel;
import com.Gr8niteout.PubDashboardScreens.Models.GetNoticeBoardModel;
import com.Gr8niteout.PubDashboardScreens.PubFragments.NoticeBoardFragment;
import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.Dialog;
import com.Gr8niteout.config.ServerAccess;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NoticeBoardRecyclerAdapter extends RecyclerView.Adapter<NoticeBoardRecyclerAdapter.ViewHolder> {
    Context context;
    ArrayList<GetNoticeBoardModel.response.data> list;
    boolean isDropDownVisible = false;
    int check;
    AdapterCallback adapterCallback;
    String count;
    int pageNo = 2;
    int currentPosition = -1;
    TabLayout tabLayout;
    RecyclerView recyclerView;
    LinearLayout centerLayout;

    public NoticeBoardRecyclerAdapter(Context context, String count, ArrayList<GetNoticeBoardModel.response.data> list, int check, AdapterCallback callback, TabLayout tabLayout, LinearLayout centerLayout, RecyclerView recyclerView) {
        this.context = context;
        this.list = list;
        this.check = check;
        this.adapterCallback = callback;
        this.count = count;
        this.tabLayout = tabLayout;
        this.centerLayout = centerLayout;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notice_board_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (currentPosition != holder.getBindingAdapterPosition()) {
            holder.dropDown1.setVisibility(View.GONE);
            holder.dropDown2.setVisibility(View.GONE);
        }else{
            if (isDropDownVisible) {
                holder.dropDown1.setVisibility(View.GONE);
                holder.dropDown2.setVisibility(View.GONE);
            } else {
                if (list.get(holder.getBindingAdapterPosition()).notices_image.equals("")) {
                    holder.dropDown2.setVisibility(View.VISIBLE);
                } else {
                    holder.dropDown1.setVisibility(View.VISIBLE);
                }
            }
            isDropDownVisible = !isDropDownVisible;
        }

        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentPosition != holder.getBindingAdapterPosition()){
                    currentPosition = holder.getBindingAdapterPosition();
                    isDropDownVisible = false;
                }
                notifyDataSetChanged();
            }
        });

        holder.tvDate1.setText(list.get(position).created_date);
        holder.tvDate2.setText(list.get(position).created_date);
        holder.tvTitle.setText(list.get(position).title);
        holder.tvNotice.setText(list.get(position).description);

        Glide.with(context)
                .load(Uri.parse(list.get(position).notices_image))
                .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                .into(holder.imageView);

        if (list.get(position).notices_image.equals("")) {
            holder.dateLayout1.setVisibility(View.VISIBLE);
            holder.rvImageLayout.setVisibility(View.GONE);
            holder.dateLayout2.setVisibility(View.GONE);
        } else {
            holder.dateLayout1.setVisibility(View.GONE);
            holder.rvImageLayout.setVisibility(View.VISIBLE);
            holder.dateLayout2.setVisibility(View.VISIBLE);
        }

        if (check == 0) {
            holder.tvEdit1.setText("Edit");
            holder.tvEdit2.setText("Edit");
        } else {
            holder.tvEdit1.setText("Activate");
            holder.tvEdit2.setText("Activate");
        }

        holder.tvEdit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.dropDown1.setVisibility(View.GONE);
                holder.dropDown2.setVisibility(View.GONE);
                if (check == 0) {
                    adapterCallback.onMethodCallback(1, list.get(holder.getBindingAdapterPosition()).title,
                            list.get(holder.getBindingAdapterPosition()).description,
                            list.get(holder.getBindingAdapterPosition()).notices_image,
                            list.get(holder.getBindingAdapterPosition()).pub_notice_id
                    );
                } else {
                    callActivateNoticeApi(list.get(holder.getBindingAdapterPosition()).pub_notice_id);
                }
            }
        });

        holder.tvEdit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.dropDown1.setVisibility(View.GONE);
                holder.dropDown2.setVisibility(View.GONE);
                if (check == 0) {
                    adapterCallback.onMethodCallback(1, list.get(holder.getBindingAdapterPosition()).title,
                            list.get(holder.getBindingAdapterPosition()).description,
                            list.get(holder.getBindingAdapterPosition()).notices_image,
                            list.get(holder.getBindingAdapterPosition()).pub_notice_id
                    );
                } else {
                    callActivateNoticeApi(list.get(holder.getBindingAdapterPosition()).pub_notice_id);
                }
            }
        });

        holder.tvDelete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.dropDown1.setVisibility(View.GONE);
                holder.dropDown2.setVisibility(View.GONE);
                showDeleteDialog(list.get(holder.getBindingAdapterPosition()).pub_notice_id, check);
            }
        });

        holder.tvDelete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.dropDown1.setVisibility(View.GONE);
                holder.dropDown2.setVisibility(View.GONE);
                showDeleteDialog(list.get(holder.getBindingAdapterPosition()).pub_notice_id, check);
            }
        });

        if (list.size() < Integer.parseInt(count)) {
            if (position == list.size() - 1) {
                holder.viewMoreLayout.setVisibility(View.VISIBLE);
            } else {
                holder.viewMoreLayout.setVisibility(View.GONE);
            }
        } else {
            holder.viewMoreLayout.setVisibility(View.GONE);
        }

        int remainingCount = Integer.parseInt(count) - list.size();
        holder.tvViewMoreCount.setText(String.valueOf(remainingCount));

        holder.viewMoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callGetNoticeBoardApi(holder.viewMoreLayout);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMore, imageView;
        TextView tvEdit1, tvDelete1, tvEdit2, tvDelete2, tvViewMoreCount, tvDate1, tvDate2, tvTitle, tvNotice;
        RelativeLayout dropDown1, dropDown2, rvImageLayout;
        LinearLayout viewMoreLayout, dateLayout1, dateLayout2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMore = itemView.findViewById(R.id.ivMore);
            dropDown1 = itemView.findViewById(R.id.dropDown1);
            tvEdit1 = itemView.findViewById(R.id.tvEdit1);
            tvDelete1 = itemView.findViewById(R.id.tvDelete1);
            dropDown2 = itemView.findViewById(R.id.dropDown2);
            tvEdit2 = itemView.findViewById(R.id.tvEdit2);
            tvDelete2 = itemView.findViewById(R.id.tvDelete2);
            viewMoreLayout = itemView.findViewById(R.id.viewMoreLayout);
            tvViewMoreCount = itemView.findViewById(R.id.tvViewMoreCount);
            tvDate1 = itemView.findViewById(R.id.tvDate1);
            tvDate2 = itemView.findViewById(R.id.tvDate2);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvNotice = itemView.findViewById(R.id.tvNotice);
            imageView = itemView.findViewById(R.id.imageView);
            dateLayout1 = itemView.findViewById(R.id.dateLayout1);
            dateLayout2 = itemView.findViewById(R.id.dateLayout2);
            rvImageLayout = itemView.findViewById(R.id.rvImageLayout);
        }
    }

    public void showDeleteDialog(String pubNoticeId, int check) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.delete_dialog_layout);
        dialog.setCancelable(true);

        TextView tvYes, tvNo, tvDescription;
        RelativeLayout ivClose = dialog.findViewById(R.id.ivClose);
        tvYes = dialog.findViewById(R.id.tvYes);
        tvNo = dialog.findViewById(R.id.tvNo);
        tvDescription = dialog.findViewById(R.id.tvDescription);

        if (check == 0) {
            tvDescription.setText("Are you sure you want to delete selected Notice(s)?");
        } else {
            tvDescription.setText("Are you sure you want to delete selected Notice(s) permanently?");
        }

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                callDeleteNoticeApi(pubNoticeId, check);
            }
        });

        dialog.show();
    }

    public void callGetNoticeBoardApi(LinearLayout viewMoreLayout) {
        SharedPreferences preferences = context.getSharedPreferences("pub_details", MODE_PRIVATE);
        Map<String, String> params = new HashMap<String, String>();

        int flag;
        if (check == 0) {
            flag = 0;
        } else {
            flag = 1;
        }

        String url = CommonUtilities.key_get_notice_board_list + "&pub_id=" + preferences.getString("pub_id", "")
                + "&page_no=" + pageNo + "&flag=" + flag;

        ServerAccess.getResponse(context, url, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                GetNoticeBoardModel noticeBoardModel = new GetNoticeBoardModel().GetNoticeBoardModel(result);
                if (noticeBoardModel != null) {
                    if (noticeBoardModel.response.code.equals(CommonUtilities.key_success_code)) {
                        list.addAll(noticeBoardModel.response.data);
                        pageNo++;
                        notifyDataSetChanged();
                    } else {
                        viewMoreLayout.setVisibility(View.GONE);
                        CommonUtilities.ShowToast(context, noticeBoardModel.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(context, "Something went wrong!");
            }
        });
    }

    public void callActivateNoticeApi(String pubNoticeID) {
        SharedPreferences preferences = context.getSharedPreferences("pub_details", MODE_PRIVATE);
        Map<String, String> params = new HashMap<String, String>();
        params.put("pub_notice_id", pubNoticeID);
        params.put("pub_id", preferences.getString("pub_id", ""));
        params.put("flag", "1");

        ServerAccess.getResponse(context, CommonUtilities.key_notice_action, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                AddNewNoticeModel addNewNoticeModel = new AddNewNoticeModel().AddNewNoticeModel(result);
                if(addNewNoticeModel != null){
                    if(addNewNoticeModel.response.code.equals(CommonUtilities.key_success_code)){
                        CommonUtilities.ShowToast(context, addNewNoticeModel.response.data.success);
                        tabLayout.getTabAt(0).select();
                        NoticeBoardFragment.refreshAllFragment = true;
                        NoticeBoardFragment.refreshDeleteFragment = true;
                    }else{
                        CommonUtilities.ShowToast(context, addNewNoticeModel.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(context, "Something went wrong!");
            }
        });
    }

    public void callDeleteNoticeApi(String pubNoticeID, int check) {
        int flag;
        if (check == 0) {
            flag = 2;
        } else {
            flag = 3;
        }
        SharedPreferences preferences = context.getSharedPreferences("pub_details", MODE_PRIVATE);
        Map<String, String> params = new HashMap<String, String>();
        params.put("pub_notice_id", pubNoticeID);
        params.put("pub_id", preferences.getString("pub_id", ""));
        params.put("flag", String.valueOf(flag));

        ServerAccess.getResponse(context, CommonUtilities.key_notice_action, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                AddNewNoticeModel addNewNoticeModel = new AddNewNoticeModel().AddNewNoticeModel(result);
                if(addNewNoticeModel != null){
                    if(addNewNoticeModel.response.code.equals(CommonUtilities.key_success_code)){
                        CommonUtilities.ShowToast(context, addNewNoticeModel.response.data.success);
                        if(check == 0) {
                            NoticeBoardFragment.refreshDeleteFragment = true;
                            tabLayout.getTabAt(1).select();
                            NoticeBoardFragment.refreshAllFragment = true;
                        }else{
                            adapterCallback.onMethodCallback(
                                    1, "","","",""
                            );
                        }
                    }else{
                        CommonUtilities.ShowToast(context, addNewNoticeModel.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(context, "Something went wrong!");
            }
        });
    }

    public void closeDropDown(){
        currentPosition = -1;
        notifyDataSetChanged();
    }

}
