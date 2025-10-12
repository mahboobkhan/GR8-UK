package com.Gr8niteout.Subscription;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Gr8niteout.R;
import com.Gr8niteout.buycredits.StripeWebViewActivity;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.StripeAccountAddModel;
import com.Gr8niteout.model.SubscriptionAPIModel;
import com.Gr8niteout.signup.PubLoginActivity;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultipleSubscriptionFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<itemDS> itmLST;
    private BillingClient billingClient;
    private boolean isSuccess = false;
    private String productId, dur;
    private int planeIdx;
    private Context context;
    private TextView SubscriptionStatus;
    TextView subName;
    TextView userName;
    TextView userEmail;
    TextView subscriptionType;
    TextView tv_start_date_label,tv_expiration_date_label;
    CardView subscription_card;
    LinearLayout detailOfSubCard;
    ImageView close ;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_multiple_subscription, container, false);

        initViews(view);
        return view;
    }


    public void initViews(View view) {
        context = requireContext();
        Button expButton = view.findViewById(R.id.explore_subscription_button);
        subName = view.findViewById(R.id.tv_subscription_name);
        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        subscriptionType = view.findViewById(R.id.tx_Subscription_Type);
        tv_start_date_label = view.findViewById(R.id.tv_start_date_label);
        tv_expiration_date_label = view.findViewById(R.id.tv_expiration_date_label);
        subscription_card = view.findViewById(R.id.subscription_card);
        detailOfSubCard = view.findViewById(R.id.detailOfSubCard);

        close = view.findViewById(R.id.close);
        close.setVisibility(View.GONE);

        expButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Action", "onClick: ---------->>>");
//                String url = "https://gr8niteout.probrains.co/"; // Replace with your desired URL
                String url = "https://gr8niteout.co.uk/pub"; // Replace with your desired URL
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        callConnectStripeApi();


    }

    @Override
    public void onStart() {
        super.onStart();
        callConnectStripeApi();
    }

    @Override
    public void onResume() {
        super.onResume();
        callConnectStripeApi();
    }

    public void callConnectStripeApi(){

        SharedPreferences preferences = context.getSharedPreferences("pub_details",MODE_PRIVATE);

        String email = preferences.getString("email","");

        String url = CommonUtilities.GET_SUBSCRIPTION_STATUS+email;

        Log.d("getAPIResponse", "callConnectStripeApi: "+url);

        ServerAccess.getAPIResponse(context, CommonUtilities.GET_SUBSCRIPTION_STATUS+email, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {


                SubscriptionAPIModel model = new SubscriptionAPIModel().SubscriptionAPIModel(result);

                if (model != null) {
                    if (model.getResponse().getStatus().equals(CommonUtilities.key_Success) && model.getResponse().getLatestSubscription() != null) {
                        subscription_card.setVisibility(View.VISIBLE);
                        detailOfSubCard.setVisibility(View.GONE);

                        String name = model.getResponse().getName();
                        String namePlan = model.getResponse().getLatestSubscription().getPlanName();
                        String email = model.getResponse().getEmail();
                        String planName = model.getResponse().getLatestSubscription().getPlanName();
                        String endSubscription = model.getResponse().getLatestSubscription().getEndSubscription();
                        String type = model.getResponse().getLatestSubscription().getType();
                        String createdAt = model.getResponse().getLatestSubscription().getCreatedAt();

                        Log.d("Response", "Name: "+name);
                        Log.d("Response", "namePlan: "+namePlan);
                        Log.d("Response", "email: "+email);
                        Log.d("Response", "planName: "+planName);
                        Log.d("Response", "endSubscription: "+endSubscription);
                        Log.d("Response", "type: "+type);
                        Log.d("Response", "createdAt: "+createdAt);



                        subName.setText(namePlan);
                        userName.setText(name);
                        userEmail.setText(email);
                        subscriptionType.setText(type);
                        tv_start_date_label.setText(createdAt);
                        tv_expiration_date_label.setText(endSubscription);


                        // Handle success case
                    } else {
                        subscription_card.setVisibility(View.GONE);
                        detailOfSubCard.setVisibility(View.VISIBLE);

                        CommonUtilities.ShowToast(context, model.getResponse().getMsg());
                    }
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(context, "Something went wrong!");
            }
        });
    }



}
