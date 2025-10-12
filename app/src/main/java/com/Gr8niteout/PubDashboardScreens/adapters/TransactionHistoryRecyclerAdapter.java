package com.Gr8niteout.PubDashboardScreens.adapters;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Gr8niteout.PubDashboardScreens.Models.TransactionHistoryModel;
import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionHistoryRecyclerAdapter extends RecyclerView.Adapter<TransactionHistoryRecyclerAdapter.ViewHolder> {
    Context context;
    List<TransactionHistoryModel.data> list;

    boolean isUser;
    int pageNo = 2;

    String count, userId, pubId;

    public TransactionHistoryRecyclerAdapter(Context context, List<TransactionHistoryModel.data> list, boolean isUser, String count, String userId, String pubId){
        this.context = context;
        this.list = list;
        this.isUser = isUser;
        this.count = count;
        this.userId = userId;
        this.pubId = pubId;
    }

    @NonNull
    @Override
    public TransactionHistoryRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionHistoryRecyclerAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.transaction_history_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionHistoryRecyclerAdapter.ViewHolder holder, int position) {
//        if(isUser) {
//            holder.tvTitle.setText(list.get(position).pub_name == null ? "--" : list.get(position).pub_name);
//        }else{
//            holder.tvTitle.setText(list.get(position).username == null ? "--" : list.get(position).username);
//        }
        String currency = "$";
        if(list.get(position).currency.toLowerCase().equals("usd")){
            currency = "$";
        }else if(list.get(position).currency.toLowerCase().equals("eur")){
            currency = "€";
        }else if(list.get(position).currency.toLowerCase().equals("gbp")){
            currency = "£";
        }

        holder.tvAmount.setText(currency+list.get(position).amount);
        holder.tvDate.setText(formatDate(list.get(position).date));
        holder.senderName.setText(list.get(position).sender_name == null ? "--" : list.get(position).sender_name);
        holder.receiverName.setText(list.get(position).recipient_name == null ? "--" : list.get(position).recipient_name);
        holder.message.setText(list.get(position).comment == null ? "--" : list.get(position).comment);
        holder.pubName.setText(list.get(position).pub_name == null ? "--" : list.get(position).pub_name);
//      holder.tvTransactionId.setText(list.get(position).transactionId);

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
                callTransactionHistoryApi(holder.viewMoreLayout);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
//        TextView tvTitle, tvAmount, tvDate, tvTransactionId;
        TextView tvAmount, tvDate, tvTitle, tvViewMoreCount , senderName , receiverName , message , pubName;
        LinearLayout viewMoreLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
            viewMoreLayout = itemView.findViewById(R.id.viewMoreLayout);
            tvViewMoreCount = itemView.findViewById(R.id.tvViewMoreCount);
            senderName = itemView.findViewById(R.id.senderName);
            receiverName = itemView.findViewById(R.id.receiverName);
            message = itemView.findViewById(R.id.messageText);
            pubName = itemView.findViewById(R.id.pubName);
//            tvTransactionId = itemView.findViewById(R.id.tvTransactionId);
        }
    }

    public void callTransactionHistoryApi(LinearLayout viewMoreLayout) {
        Map<String, String> paramsTemp = new HashMap<>();
        if(isUser) {
            paramsTemp.put("user_id", userId);
        }else{
            paramsTemp.put("pub_id", pubId);
        }
        paramsTemp.put("page", String.valueOf(pageNo));
        paramsTemp.put("limit", "20");

        ServerAccess.getResponse(context, CommonUtilities.key_fetch_pub_user_credits, paramsTemp, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                TransactionHistoryModel model = new TransactionHistoryModel().TransactionHistoryModel(result);
                if (model != null) {
                    if(model.response.code.equals(CommonUtilities.key_success_code)){
                        list.addAll(model.response.data);
                        pageNo++;
                        notifyDataSetChanged();
                    }else{
                        viewMoreLayout.setVisibility(View.GONE);
                        CommonUtilities.ShowToast(context, model.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(context, "Something went wrong!");
            }
        });
    }

    private String formatDate(String inputDate) {
        // Original format (assuming "yyyy-MM-dd" format for input)
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        // Desired format
        SimpleDateFormat desiredFormat = new SimpleDateFormat("MMM dd yyyy");
        try {
            // Parse the input date
            Date date = originalFormat.parse(inputDate);
            // Format it to the desired format and return
            return desiredFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            // If parsing fails, return the original string
            return inputDate;
        }
    }
}
