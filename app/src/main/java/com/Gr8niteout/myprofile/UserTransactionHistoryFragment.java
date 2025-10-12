package com.Gr8niteout.myprofile;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.Gr8niteout.MainActivity;
import com.Gr8niteout.PubDashboardScreens.Models.TransactionHistoryModel;
import com.Gr8niteout.PubDashboardScreens.adapters.TransactionHistoryRecyclerAdapter;
import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import java.util.HashMap;
import java.util.Map;

public class UserTransactionHistoryFragment extends Fragment {
    MainActivity mActivity;

    RecyclerView recyclerView;
    LinearLayout centerLayout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_transaction_history, container, false);
        mActivity.isExit = true;
        mActivity.getSupportActionBar().setTitle(null);
        mActivity.tool_text.setText("Transaction History");
        (mActivity).goImage.setVisibility(View.GONE);
        CommonUtilities.setFontFamily(mActivity,mActivity.tool_text , CommonUtilities.AvenirLTStd_Medium);
        mActivity.getSupportActionBar().setIcon(null);

        recyclerView = view.findViewById(R.id.recyclerView);
        centerLayout = view.findViewById(R.id.centerLayout);

        callTransactionHistoryApi();

        return view;
    }

    public void callTransactionHistoryApi() {
        final String userId;
        Map<String, String> paramsTemp = new HashMap<>();
        if (CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId).equals("")) {
            userId = "";
        } else {
            userId = CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId);
        }
        paramsTemp.put("user_id", userId);
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
                                    true,
                                    String.valueOf(model.response.totalCountRow),
                                    userId,
                                    ""
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_setting).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_reset).setVisible(false);
        menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_search_white).setVisible(false);
        menu.findItem(R.id.action_transaction_history).setVisible(false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        (mActivity).goImage.setVisibility(View.GONE);
        mActivity.isExit = true;
    }

}