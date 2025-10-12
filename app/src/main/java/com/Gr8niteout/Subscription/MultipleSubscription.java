package com.Gr8niteout.Subscription;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.SubscriptionAPIModel;
import com.android.billingclient.api.BillingClient;
import java.util.ArrayList;

public class MultipleSubscription extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<itemDS> itmLST;
    private BillingClient billingClient;
    private boolean isSuccess = false;
    private String productId, dur;
    private int planeIdx;
    private Context context;
    private TextView SubscriptionStatus;
    private TextView subName;
    private TextView userName;
    private TextView userEmail;
    private TextView subscriptionType;
    private TextView tv_start_date_label, tv_expiration_date_label;
    private CardView subscription_card;
    private LinearLayout detailOfSubCard;
    ImageView close ;

    String emailWithOutLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_subscription);

        initViews();
    }

    public void initViews() {
        context = this;
        Button expButton = findViewById(R.id.explore_subscription_button);
        subName = findViewById(R.id.tv_subscription_name);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        subscriptionType = findViewById(R.id.tx_Subscription_Type);
        tv_start_date_label = findViewById(R.id.tv_start_date_label);
        tv_expiration_date_label = findViewById(R.id.tv_expiration_date_label);
        subscription_card = findViewById(R.id.subscription_card);
        detailOfSubCard = findViewById(R.id.detailOfSubCard);
        emailWithOutLogin = getIntent().getStringExtra("email");
        close = findViewById(R.id.close);
        close.setVisibility(View.VISIBLE);

        close.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        expButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Action", "onClick: ---------->>>");
                String url = "https://gr8niteout.probrains.co/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        callConnectStripeApi();
    }

    @Override
    protected void onStart() {
        super.onStart();
        callConnectStripeApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        callConnectStripeApi();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void callConnectStripeApi() {
        SharedPreferences preferences = context.getSharedPreferences("pub_details", MODE_PRIVATE);
        String email = preferences.getString("email", "");

        if(email.equals("")){
            email = emailWithOutLogin;
        }

        String url = CommonUtilities.GET_SUBSCRIPTION_STATUS + email;

        Log.d("getAPIResponse", "callConnectStripeApi: " + url);

        ServerAccess.getAPIResponse(context, CommonUtilities.GET_SUBSCRIPTION_STATUS + email, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                SubscriptionAPIModel model = new SubscriptionAPIModel().SubscriptionAPIModel(result);

                if (model != null) {
                    if (model.getResponse().getStatus().equals(CommonUtilities.key_Success)) {
                        subscription_card.setVisibility(View.VISIBLE);
                        detailOfSubCard.setVisibility(View.GONE);

                        String name = model.getResponse().getName();
                        String namePlan = model.getResponse().getLatestSubscription().getPlanName();
                        String email = model.getResponse().getEmail();
                        String planName = model.getResponse().getLatestSubscription().getPlanName();
                        String endSubscription = model.getResponse().getLatestSubscription().getEndSubscription();
                        String type = model.getResponse().getLatestSubscription().getType();
                        String createdAt = model.getResponse().getLatestSubscription().getCreatedAt();

                        Log.d("Response", "Name: " + name);
                        Log.d("Response", "namePlan: " + namePlan);
                        Log.d("Response", "email: " + email);
                        Log.d("Response", "planName: " + planName);
                        Log.d("Response", "endSubscription: " + endSubscription);
                        Log.d("Response", "type: " + type);
                        Log.d("Response", "createdAt: " + createdAt);

                        subName.setText(namePlan);
                        userName.setText(name);
                        userEmail.setText(email);
                        subscriptionType.setText(type);
                        tv_start_date_label.setText(createdAt);
                        tv_expiration_date_label.setText(endSubscription);
                    } else {
                        subscription_card.setVisibility(View.GONE);
                        detailOfSubCard.setVisibility(View.VISIBLE);

                        if(model.getResponse().getMsg().equals("Email not found in ci_pub_owners")){
                            CommonUtilities.ShowToast(context, "Your account is not approved by admin.");
                        }
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