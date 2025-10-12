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

import com.Gr8niteout.PubDashboardScreens.Models.GetNotificationModel;
import com.Gr8niteout.PubDashboardScreens.Models.GetPremisesModel;
import com.Gr8niteout.PubDashboardScreens.adapters.NotificationRecyclerAdapter;
import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationFragment extends Fragment {

    RecyclerView recyclerView;
    LinearLayout centerLayout;
    GetNotificationModel notificationModel;
    ArrayList<GetNotificationModel.response.data> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        centerLayout = view.findViewById(R.id.centerLayout);

        callGetNotificationApi();

        return view;
    }

    public void callGetNotificationApi() {
        SharedPreferences preferences = getContext().getSharedPreferences("pub_details", MODE_PRIVATE);
        Map<String, String> params = new HashMap<String, String>();
        String url = CommonUtilities.key_get_notification + "&pub_id="+ preferences.getString("pub_id", "")+"&page_no=1";

        ServerAccess.getResponse(getContext(), url, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                notificationModel = new GetNotificationModel().GetNotificationModel(result);
                if (notificationModel != null) {
                    if (notificationModel.response.code.equals(CommonUtilities.key_success_code)) {
                        list.addAll(notificationModel.response.data);
                        recyclerView.setVisibility(View.VISIBLE);
                        centerLayout.setVisibility(View.GONE);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(new NotificationRecyclerAdapter(getContext(), list));
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        centerLayout.setVisibility(View.VISIBLE);
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