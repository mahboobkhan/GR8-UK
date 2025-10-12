package com.Gr8niteout.PubDashboardScreens.PubFragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.Gr8niteout.PubDashboardScreens.Models.TransactionHistoryModel;
import com.Gr8niteout.PubDashboardScreens.adapters.TransactionHistoryRecyclerAdapter;
import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;

import java.util.HashMap;
import java.util.Map;

public class TransactionHistoryFragment extends Fragment {

    RecyclerView recyclerView;
    LinearLayout centerLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_history, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        centerLayout = view.findViewById(R.id.centerLayout);
        callTransactionHistoryApi();
        return view;
    }

    public void callTransactionHistoryApi() {
        SharedPreferences preferences = getContext().getSharedPreferences("pub_details",MODE_PRIVATE);
        Map<String, String> paramsTemp = new HashMap<>();
        paramsTemp.put("pub_id", preferences.getString( "pub_id",""));
        paramsTemp.put("page", "1");
        paramsTemp.put("limit", "20");

        ServerAccess.getResponse(getContext(), CommonUtilities.key_fetch_pub_user_credits, paramsTemp, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                TransactionHistoryModel model = new TransactionHistoryModel().TransactionHistoryModel(result);
                if (model != null) {
                    if(model.response.code.equals(CommonUtilities.key_success_code)){
                        if(model.response.data.isEmpty()){
                            centerLayout.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }else {
                            centerLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(new TransactionHistoryRecyclerAdapter(
                                    getContext(),
                                    model.response.data,
                                    false,
                                    String.valueOf(model.response.totalCountRow),
                                    "",
                                    preferences.getString( "pub_id","")
                                )
                            );
                        }
                    }else{
                        CommonUtilities.ShowToast(getContext(), model.response.msg);
                        if(model.response.data.isEmpty()){
                            centerLayout.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(getContext(), "Something went wrong!");
            }
        });
    }
}