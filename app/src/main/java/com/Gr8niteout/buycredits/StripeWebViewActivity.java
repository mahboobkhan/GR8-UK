package com.Gr8niteout.buycredits;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import com.Gr8niteout.R;
import com.Gr8niteout.Subscription.MultipleSubscription;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.Dialog;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.SubscriptionModel;
import com.Gr8niteout.signup.PubLoginActivity;
import java.util.HashMap;
import java.util.Map;

public class StripeWebViewActivity extends AppCompatActivity {

    String getUrl, successUrl, failureUrl, pubId , user_id ,email;
    int isFromUserLogin;
    Dialog progressBar;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_web_view);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        webView = (WebView) findViewById(R.id.webview);
        progressBar = new Dialog(StripeWebViewActivity.this);

        getUrl = getIntent().getStringExtra("ViewUrl");
        successUrl = getIntent().getStringExtra("successUrl");
        failureUrl = getIntent().getStringExtra("failureUrl");
        pubId = getIntent().getStringExtra("pub_id");
        email = getIntent().getStringExtra("email");
//        user_id = getIntent().getStringExtra(CommonUtilities.key_user_id);
        isFromUserLogin = getIntent().getIntExtra("isFromUserLogin", 0);

        progressBar.show();

        Dialog dialog = new Dialog(StripeWebViewActivity.this);


        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                String[] urlList = url.split("/");
                if(isFromUserLogin == 1){
                    if(url.contains("payment_successfull")){
                        Intent intent = new Intent(StripeWebViewActivity.this, ShareActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }else if(url.equals(failureUrl)){
                        finish();
                        CommonUtilities.ShowToast(StripeWebViewActivity.this,"Payment failed. Please try again");
                    }
                }else if(isFromUserLogin == 2){
                    if(urlList.length > 3) {
                        String finalUrl = urlList[0] + "//" + urlList[2] + "/" + urlList[3];

                        if(finalUrl.equals("https://gr8niteout.co.uk/sucess")){

//                            SubscriptionService();

                            dialog.setContentView(R.layout.connect_subscription_dialog);
                            dialog.setCancelable(false);
                            dialog.show();
                            TextView tvConnectStripe = dialog.findViewById(R.id.GetAccountSubscription);
                            ImageView IconCross = dialog.findViewById(R.id.closeIconSubDialog);

                            IconCross.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                        Intent intent = new Intent(StripeWebViewActivity.this, PubLoginActivity.class);
                                        intent.putExtra("pub_id", pubId);
                                        intent.putExtra("isSuccess",1);
                                        startActivity(intent);
                                        finishAffinity();
                                }
                            });

                            tvConnectStripe.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(StripeWebViewActivity.this, MultipleSubscription.class);
                                    intent.putExtra("pub_id", pubId);
                                    intent.putExtra("email", email);
                                    intent.putExtra("isSuccess",1);
                                    startActivity(intent);
//                                    finishAffinity();

                                }
                            });

                        }else if( finalUrl.equals("https://gr8niteout.co.uk/unsuccess")){
                            Intent intent = new Intent(StripeWebViewActivity.this, PubLoginActivity.class);
                            intent.putExtra("pub_id", pubId);
                            intent.putExtra("isSuccess",2);
                            startActivity(intent);
                            finishAffinity();
                        }
                    }
                }
                super.onPageStarted(view, url, favicon);
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }
        });

        webView.loadUrl(getUrl);
    }


    public void SubscriptionService() {
        Log.d("SubscriptionService","Subscription Service Request :-----");
        Map<String, String> params = new HashMap<String, String>();

        params.put("pub_id", pubId);
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
                        CommonUtilities.alertdialog(StripeWebViewActivity.this, model.getResponse().getData().getSuccessMessage());
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }
}