package com.Gr8niteout.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.Gr8niteout.PubDashboardScreens.DashboardHomeActivity;
import com.Gr8niteout.R;
import com.Gr8niteout.RegisterPubScreens.GetStartedActivity;
import com.Gr8niteout.buycredits.StripeWebViewActivity;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.Dialog;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.PubLoginModel;
import com.Gr8niteout.model.StripeAccountAddModel;

import java.util.HashMap;
import java.util.Map;

public class PubLoginActivity extends AppCompatActivity {

    TextView tvCreateAnAccount, tvLogin, tvForgotPassword;
    EditText edtEmail, edtPassword;
    PubLoginModel pubLoginModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub_login);

        tvCreateAnAccount = findViewById(R.id.tvCreateAnAccount);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        tvLogin = findViewById(R.id.tvLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        int isSuccess = getIntent().getIntExtra("isSuccess",0);

        if(isSuccess == 1 || isSuccess == 2){
            showStripeDialog(isSuccess);
        }

        tvCreateAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PubLoginActivity.this, GetStartedActivity.class));
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PubLoginActivity.this, PubForgotPasswordActivity.class));
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check1 = false;
                boolean check2 = false;
                boolean check3 = false;

                if (edtEmail.getText().toString().trim().equals("")) {
                    edtEmail.setError("Please enter email");
                    check1 = true;
                } else {
                    edtEmail.setError(null);
                    check3 = isEmailValid(edtEmail);
                }

                if (edtPassword.getText().toString().trim().equals("")) {
                    check2 = true;
                    edtPassword.setError("Please enter password");
                } else {
                    edtPassword.setError(null);
                }

                if (!check1 && !check2 && !check3) {
                    callPubLoginApi();
                }

            }
        });

    }

    public void showStripeDialog(int isSuccess){
        Dialog dialog = new Dialog(PubLoginActivity.this);
        dialog.setContentView(R.layout.pub_registration_dialog);
        dialog.setCancelable(true);
        dialog.show();
        ImageView imageView = dialog.findViewById(R.id.imageView);
        TextView title1 = dialog.findViewById(R.id.title1);
        TextView title2 = dialog.findViewById(R.id.title2);
        TextView description = dialog.findViewById(R.id.description);
        TextView tvConnectStripe = dialog.findViewById(R.id.tvConnectStripe);

        imageView.setVisibility(View.GONE);

        if(isSuccess == 1){
            title1.setText("Congratulations");
            title2.setText("Your stripe account is successfully linked.");
            description.setText("Please login to continue.");
            tvConnectStripe.setText("Continue");
            tvConnectStripe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }else{
            title1.setText("Sorry");
            title2.setText("Your stripe account is not linked successfully.");
            description.setText("Please click below button and re-link your stripe account.");
            tvConnectStripe.setText("Connect Stripe Account");
            tvConnectStripe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callConnectStripeApi(getIntent().getStringExtra("pub_id"));
                }
            });
        }

    }

    public void callConnectStripeApi(String pubId){
        Map<String, String> paramsTemp = new HashMap<>();
        paramsTemp.put("pub_id", pubId);
        ServerAccess.getResponse(PubLoginActivity.this, CommonUtilities.key_stripe_account_add, paramsTemp, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                StripeAccountAddModel model = new StripeAccountAddModel().StripeAccountAddModel(result);

                if(model != null){
                    if(model.response.status.equals(CommonUtilities.key_Success)){
                        Intent intent = new Intent(PubLoginActivity.this, StripeWebViewActivity.class);
                        intent.putExtra("ViewUrl", model.response.data.url);
                        intent.putExtra("pub_id", pubId);
                        intent.putExtra("successUrl", "");
                        intent.putExtra("failureUrl", "");
                        intent.putExtra("isFromUserLogin", 2);
                        startActivity(intent);
                    }else{
                        CommonUtilities.ShowToast(PubLoginActivity.this, model.response.msg);
                    }
                }

            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(PubLoginActivity.this, "Something went wrong!");
            }
        });
    }

    public void callPubLoginApi() {
        Map<String, String> paramsTemp = new HashMap<>();
        paramsTemp.put("email", edtEmail.getText().toString().trim());
        paramsTemp.put("password", edtPassword.getText().toString().trim());

        ServerAccess.getResponse(PubLoginActivity.this, CommonUtilities.key_pub_login, paramsTemp, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                pubLoginModel = new PubLoginModel().PubLoginModel(result);
                if (pubLoginModel != null) {
                    if(pubLoginModel.response.code.equals(CommonUtilities.key_success_code)){
                        SharedPreferences preferences = getSharedPreferences("pub_details",MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("pub_id",pubLoginModel.response.data.pub_id);
                        editor.putString("owner_id",pubLoginModel.response.data.owner_id);
                        editor.putString("email",pubLoginModel.response.data.email);
                        editor.putBoolean("isPubLoggedIn",true);
                        editor.apply();

                        Log.d("Email", "onSuccess: "+pubLoginModel.response.data.email);

                        startActivity(new Intent(PubLoginActivity.this, DashboardHomeActivity.class));
                        finishAffinity();
                    }else{
                        CommonUtilities.ShowToast(PubLoginActivity.this, pubLoginModel.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(PubLoginActivity.this, "Something went wrong!");
            }
        });
    }

    public boolean isEmailValid(EditText email) {
        boolean check = false;

        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.setError("Please enter a valid email!");
            check = true;
        }else{
            email.setError(null);
        }

        return check;
    }

}