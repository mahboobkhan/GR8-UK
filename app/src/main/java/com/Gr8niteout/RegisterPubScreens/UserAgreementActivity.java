package com.Gr8niteout.RegisterPubScreens;

import static com.Gr8niteout.RegisterPubScreens.ValidationCases.params;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Gr8niteout.R;
import com.Gr8niteout.buycredits.StripeWebViewActivity;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.Dialog;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.PubRegistrationModel;
import com.Gr8niteout.model.StripeAccountAddModel;
import com.Gr8niteout.model.SubscriptionModel;
import com.Gr8niteout.signup.PubLoginActivity;

import java.util.HashMap;
import java.util.Map;


public class UserAgreementActivity extends AppCompatActivity {

    PubRegistrationModel pubRegistrationModel;
    CheckBox checkBox1, checkBox2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agreement);

        TextView myTextView = findViewById(R.id.terms);
        TextView btnSubmit = findViewById(R.id.btnSubmit);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);

        SpannableString ss = new SpannableString("I accept the User Agreement which is the terms and conditions that apply to my use of Gr8NiteOut, and have read the Privacy Policy.*");
        ClickableSpan span1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // do some thing
                Toast.makeText(UserAgreementActivity.this, "User Agreement", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.linkColor = getResources().getColor(R.color.pub_blue);
            }
        };

        ClickableSpan span2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // do another thing
                Toast.makeText(UserAgreementActivity.this, "Privacy Policy", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.linkColor = getResources().getColor(R.color.pub_blue);
            }
        };

        ClickableSpan span3 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // do another thing
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.red_star));
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(span1, 13, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(span2, ss.length() - 16, ss.length() - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(span3, ss.length() - 1, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        myTextView.setText(ss);
        myTextView.setMovementMethod(LinkMovementMethod.getInstance());
        myTextView.setHighlightColor(getResources().getColor(android.R.color.transparent));

        updateFields();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox1.isChecked() && checkBox2.isChecked()) {

                    params.put("agree1", "1");
                    params.put("agree3", "1");

                    callPubApi();

                } else {
                    Toast.makeText(UserAgreementActivity.this, "Please accept all terms & condition first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void callPubApi() {
        Log.d("Email", "callConnectStripeApi: "+params.get("email"));

        SharedPreferences preferences = getSharedPreferences("pub_details", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear(); // Clears all key-value pairs in the preferences
        editor.apply();

        ServerAccess.getResponse(UserAgreementActivity.this, CommonUtilities.key_pub_registration, params, true,
                new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pubRegistrationModel = new PubRegistrationModel().PubRegistrationModel(result);

                if(pubRegistrationModel != null){
                    if(pubRegistrationModel.response.status.equals(CommonUtilities.key_Success)){
                        Dialog dialog = new Dialog(UserAgreementActivity.this);
                        dialog.setContentView(R.layout.pub_registration_dialog);
                        dialog.setCancelable(false);
                        dialog.show();
                        TextView tvConnectStripe = dialog.findViewById(R.id.tvConnectStripe);
                        ImageView closeIconRegDialog = dialog.findViewById(R.id.closeIconRegDialog);
//                        SubscriptionService(pubRegistrationModel.response.pub_id);
                        closeIconRegDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        tvConnectStripe.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {



                                callConnectStripeApi(pubRegistrationModel.response.pub_id);
                            }
                        });
                    }else{
                        CommonUtilities.ShowToast(UserAgreementActivity.this, pubRegistrationModel.response.msg);
                        Log.d("Email", "callConnectStripeApi: "+params.get("email"));

                        if(pubRegistrationModel.response.msg.equals("Email already exists")){
                            Intent intent = new Intent(UserAgreementActivity.this, PubLoginActivity.class);
                            startActivity(intent);

                        }
                    }
                }

            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(UserAgreementActivity.this, "Something went wrong!");
            }
        });
    }

    public void callConnectStripeApi(int pubId){

        Log.d("Email", "callConnectStripeApi: "+params.get("email"));
        Map<String, String> paramsTemp = new HashMap<>();
        paramsTemp.put("pub_id", String.valueOf(pubId));
        ServerAccess.getResponse(UserAgreementActivity.this, CommonUtilities.key_stripe_account_add, paramsTemp, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                StripeAccountAddModel model = new StripeAccountAddModel().StripeAccountAddModel(result);

                if(model != null){
                    if(model.response.status.equals(CommonUtilities.key_Success)){
                        Intent intent = new Intent(UserAgreementActivity.this, StripeWebViewActivity.class);
                        intent.putExtra("ViewUrl", model.response.data.url);
                        intent.putExtra("pub_id", String.valueOf(pubId));
                        intent.putExtra("successUrl", "");
                        intent.putExtra("failureUrl", "");
                        intent.putExtra("isFromUserLogin", 2);
                        intent.putExtra("email", params.get("email"));
                        startActivity(intent);
                    }else{
                        CommonUtilities.ShowToast(UserAgreementActivity.this, model.response.msg);
                    }
                }

            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(UserAgreementActivity.this, "Something went wrong!");
            }
        });
    }


    public void SubscriptionService(int pubId) {
        Log.d("SubscriptionService","Subscription Service Request :-----");
        Map<String, String> params = new HashMap<String, String>();

        params.put("pub_id", String.valueOf(pubId));
//        params.put("user_id", user_id);
        params.put("plan_name", "No Subscription");
        params.put("type", "cancel");

        Log.d("SubscriptionParam",params.toString());
        ServerAccess.getResponse(this, CommonUtilities.key_Subscription_Service, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                SubscriptionModel model = new SubscriptionModel().SubscriptionModel(result);
                Log.d("SubscriptionResult","Subscription Request result :-----"+result);
                if (model != null) {
                    if (model.getResponse().getStatus().equals(CommonUtilities.key_Success)) {


                    } else {
                        CommonUtilities.alertdialog(UserAgreementActivity.this, model.getResponse().getData().getSuccessMessage());
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }


    public void updateFields(){

        if(params.get("agree1") != null && params.get("agree1").equals("1")) {
            checkBox1.setChecked(true);
        }

        if(params.get("agree3") != null && params.get("agree3").equals("1")) {
            checkBox2.setChecked(true);
        }
    }


}