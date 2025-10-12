package com.Gr8niteout.Subscription;

import static com.android.volley.VolleyLog.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Gr8niteout.R;
import com.Gr8niteout.home.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Objects;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.viewHolder> {

    Context context;

    ArrayList<itemDS> itmLST;

    private OnItemClickListener mListener;

    public  interface  OnItemClickListener {
        void onItemClick(int position);
    }

    public PlanAdapter(Context context, ArrayList<itemDS> itmLST) {
        this.context = context;
        this.itmLST = itmLST;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R
                .layout.itemist,parent,false);
        PlanAdapter.viewHolder vh = new PlanAdapter.viewHolder(view,mListener);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        String planeName = itmLST.get(position).planeName;

        if (Objects.equals(planeName, "Free null")) {
            holder.name.setText("Free");
        } else {
            holder.name.setText(planeName);
        }

        String planePrice = itmLST.get(position).planePrice;

//        holder.price.setText(itmLST.get(position).planePrice);
        if (Objects.equals(planePrice, "Free null")) {
            holder.price.setText("Free Trial for 30 days");
        } else {
            holder.price.setText(planePrice);
        }

        Log.d(TAG, "onBindViewHolderPlanePrice: "+(itmLST.get(position).planePrice));
        Log.d(TAG, "onBindViewHolderPlaneName: "+(itmLST.get(position).planeName));

    }

    @Override
    public int getItemCount() {
        return itmLST.size();
    }


    public  class viewHolder extends RecyclerView.ViewHolder {
        TextView name,price;

        viewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            name = itemView.findViewById(R.id.tvSubPlan);
            price = itemView.findViewById(R.id.tvPlanPrice);

            itemView.setOnClickListener(v->{
                if (listener != null){
                   int position = getAdapterPosition();
                   if(position != RecyclerView.NO_POSITION){
                       listener.onItemClick(position);
                   }
                }
            });
        }
    }
}
