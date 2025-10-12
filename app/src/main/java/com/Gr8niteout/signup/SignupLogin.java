package com.Gr8niteout.signup;

import static com.Gr8niteout.config.CommonUtilities.getDeviceId;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Gr8niteout.AllowAccess;
import com.Gr8niteout.BuildConfig;
import com.Gr8niteout.MainActivity;
import com.Gr8niteout.R;
import com.Gr8niteout.RegisterPubScreens.UserAgreementActivity;
import com.Gr8niteout.SplashScreen;
import com.Gr8niteout.Subscription.MultipleSubscription;
import com.Gr8niteout.buycredits.StripeWebViewActivity;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.Dialog;
import com.Gr8niteout.config.GPSTracker;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.SignUpModel;
import com.Gr8niteout.model.SubscriptionModel;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryPurchasesParams;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;
import butterknife.BindView;


public class SignupLogin extends AppCompatActivity implements Animation.AnimationListener {

    public static Integer[] mThumbIds = {
            R.mipmap.background_one, R.mipmap.background_two, R.mipmap.background_three
    };

//    @BindView(R.id.Layout)
    LinearLayout Layout;

//    @BindView(R.id.login_button)
    LoginButton loginButton;

//    @BindView(R.id.textView2)
    TextView textView2;

//    @BindView(R.id.textTerms)
    TextView textTerms;

//    @BindView(R.id.textView3)
    TextView textView3;

//    @BindView(R.id.textPrivacy)
    TextView textPrivacy;

//    @BindView(R.id.textSkip)
    TextView textSkip;
//    @BindView(R.id.textByRegistering)
    TextView textByRegistering;
//    @BindView(R.id.btn_fb_custom)
    Button btn_fb_custom;
    Button btn_pub_login;
    Button sub_btn_pub;
    CallbackManager callbackManager;
    AccessToken accessToken;
    SignUpModel model;
    Map<String, String> params = new HashMap<String, String>();
    String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    double latitude; // latitude
    double longitude;
    String CountryCode, CountryName, country;

    private BillingClient client;
    boolean isPremium = false;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_signup);
        Log.d("FacebookLogin", "on create sign up");
        ButterKnife.bind(this);

        Layout = findViewById(R.id.Layout);
        loginButton = findViewById(R.id.login_button);
        textView2 = findViewById(R.id.textView2);
        textTerms = findViewById(R.id.textTerms);
        textView3 = findViewById(R.id.textView3);
        textPrivacy = findViewById(R.id.textPrivacy);
        textSkip = findViewById(R.id.textSkip);
        textByRegistering = findViewById(R.id.textByRegistering);
        btn_fb_custom = findViewById(R.id.btn_fb_custom);


        //btn_fb_custom.setOnClickListener(this);
        callbackManager = CallbackManager.Factory.create();
        setFont();
        final Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slideltor);
        animFadein.setAnimationListener(SignupLogin.this);
        final Handler handler = new Handler();

        btn_pub_login = findViewById(R.id.btn_pub_login);
        sub_btn_pub = findViewById(R.id.sub_btn_pub);


        client = BillingClient.newBuilder(getApplicationContext()).setListener(purchasesUpdatedListener).enablePendingPurchases().build();


        btn_pub_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupLogin.this, PubLoginActivity.class);
                intent.putExtra("pub_id", "");
                intent.putExtra("isSuccess",3);
                startActivity(intent);
            }
        });
        Dialog dialog = new Dialog(SignupLogin.this);
        
        sub_btn_pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                SubscriptionService();
//                checkSubscriptionStatus();

                dialog.setContentView(R.layout.connect_subscription_dialog);
                dialog.setCancelable(false);
                dialog.show();
                TextView tvConnectStripe = dialog.findViewById(R.id.GetAccountSubscription);

                tvConnectStripe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.hide();


                        startActivity(new Intent(SignupLogin.this, MultipleSubscription.class));
                    }
                });



            }
        });





//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        String refreshedToken = FirebaseMessaging.getInstance().getToken().toString();
//        CommonUtilities.setPreference(this, CommonUtilities.pref_push_token, refreshedToken);

        Runnable runnable = new Runnable() {
            int i = 0;

            public void run() {
                Layout.setBackgroundResource(mThumbIds[i]);
                Layout.startAnimation(animFadein);
                i++;
                if (i > mThumbIds.length - 1) {
                    i = 0;
                }
                handler.postDelayed(this, 3000);  //for interval...
            }
        };
        handler.postDelayed(runnable, 0); //for initial delay..


        QueryPurchases();
        generate_token();
    }


    public void SubscriptionService() {
        Log.d("SubscriptionService","Subscription Service Request :-----");
        Map<String, String> params = new HashMap<String, String>();

        params.put("pub_id", String.valueOf(1237));
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
                        CommonUtilities.alertdialog(SignupLogin.this, model.getResponse().getData().getSuccessMessage());
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }



    private void checkSubscriptionStatus() {

        BillingClient   billingClient = BillingClient.newBuilder(this).setListener(purchasesUpdatedListener)
                .enablePendingPurchases().build();
        billingClient.queryPurchasesAsync(BillingClient.ProductType.SUBS, (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if (purchases != null && !purchases.isEmpty()) {
                    for (Purchase purchase : purchases) {
                        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                            // Check if the subscription is acknowledged
                            if (purchase.isAcknowledged()) {
                                Toast.makeText(SignupLogin.this, "Subscription is Active", Toast.LENGTH_SHORT).show();

                                // Optionally, navigate or perform any actions
//                                Intent intent = new Intent(SignupLogin.this, PubLoginActivity.class);
//                                intent.putExtra("pub_id", getIntent().getStringExtra("pub_id"));
//                                intent.putExtra("isSuccess", 1);
//                                startActivity(intent);
//                                finishAffinity();
                            } else {
                                Toast.makeText(SignupLogin.this, "Subscription is not acknowledged", Toast.LENGTH_SHORT).show();
                            }
                        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                            Toast.makeText(SignupLogin.this, "Subscription is Pending", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignupLogin.this, "Subscription is in an Unspecified State", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // No active subscriptions
                    Toast.makeText(SignupLogin.this, "No Active Subscriptions Found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SignupLogin.this, "Failed to Query Subscriptions", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

        }
    };

    private  void  QueryPurchases(){
        client.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){

                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.execute(()->{
                        try{
                            client.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),(billingResult1, purchaseList)->{
                                for (Purchase purchase : purchaseList){
                                    if(purchase != null && purchase.isAcknowledged()){
                                        isPremium = true;
                                    }
                                }
                            });
                        } catch (Exception e) {
                            isPremium = false;
                        }

                        runOnUiThread(() -> {
                         try{
                             Thread.sleep(1000);
                         } catch (InterruptedException e) {
                             e.printStackTrace();
                         }

                         if(isPremium){
                             textByRegistering.setText("Already Subscribed");
                         }
                        });


                    });

                }

            }
        });
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

//    public void FBtnClick(View v) {
//        if (AccessToken.getCurrentAccessToken() != null) {
//            LoginManager.getInstance().logOut();
//        }
//
//        LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY).registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//
//                        accessToken = loginResult.getAccessToken();
//
//                        GraphRequest request = GraphRequest.newMeRequest(
//                                loginResult.getAccessToken(),
//                                new GraphRequest.GraphJSONObjectCallback() {
//                                    @Override
//                                    public void onCompleted(
//                                            JSONObject object,
//                                            GraphResponse response) {
//
//                                        if (response != null) {
//                                            try {
//                                                params.put(CommonUtilities.key_encoded_key, SplashScreen.random_function());
//                                                params.put(CommonUtilities.key_timezone, getCurrentTimezoneOffset());
//                                                params.put(CommonUtilities.pref_fb_id, object.getString("id"));
//                                                params.put(CommonUtilities.pref_fb_access_token, accessToken.getToken());
//                                                params.put(CommonUtilities.pref_fb_birthday, object.isNull("birthday") ? "" :
//                                                        object.getString("birthday"));
//                                                params.put(CommonUtilities.pref_fb_fname, object.getString("first_name"));
//                                                params.put(CommonUtilities.pref_fb_lname, object.getString("last_name"));
//                                                params.put(CommonUtilities.pref_fb_photo, object.getJSONObject("picture").getJSONObject("data").getString("url"));
//                                                params.put(CommonUtilities.pref_fb_gender, object.isNull("gender") ? "" :
//                                                        object.getString("gender"));
//                                                params.put(CommonUtilities.pref_fb_email, object.isNull("email") ? "" :
//                                                        object.getString("email"));
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//
//                                        login_fb_call();
//                                    }
//                                });
//                        Bundle parameters = new Bundle();
//                        parameters.putString("fields", "id,email,first_name,birthday,last_name,gender,picture.width(300).height(300),verified");
//                        request.setParameters(parameters);
//                        request.executeAsync();
//
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        Log.v("facebook - onCancel", "cancelled");
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception) {
//                        Log.e("SignUp", exception.toString());
//                    }
//                });
//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_birthday"));
//
//    }

    String generate_token = "";
    public void generate_token()
    {

        Map<String, String> params = new HashMap<String, String>();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        // If the task fails, log the error
                        Log.w("FCM Token", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get the FCM token
                    String token = task.getResult();

                    // Log or use the token as needed
                    Log.d("FCM Token Login", "FCM Token: " + token);
                    generate_token = token;
                    CommonUtilities.setPreference(this,CommonUtilities.pref_push_token,token);
                    params.put(CommonUtilities.key_pushnotification_toekn,token);


                    // You can now use this token to send notifications to this device.
                });
    }
    public void FBtnClick(View v){
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
        Log.d("FacebookLogin", "button clicked");
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY).registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FacebookLogin", "facebook:onSuccess:" + loginResult);
                accessToken = loginResult.getAccessToken();

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {

                                Log.d("FacebookLogin","on completed block");
                                Log.d("FacebookLogin",response.toString());
                                Log.d("FacebookLogin",object.toString());
                                Log.d("FacebookLoginGenerate_token",generate_token);

                                if (response != null) {
                                    try {
//                                        Log.d("myBirthday",object.getString("birthday"));
                                        Log.d("FacebookLogin",object.toString());
                                        Log.d("FacebookLogin",response.toString());
                                        params.put(CommonUtilities.key_encoded_key, SplashScreen.random_function());
                                        params.put(CommonUtilities.key_timezone, getCurrentTimezoneOffset());
                                        params.put(CommonUtilities.pref_fb_id, object.getString("id"));
//                                        params.put(CommonUtilities.key_pushnotification_toekn, CommonUtilities.getPreference(getApplicationContext(), CommonUtilities.pref_push_token));
//                                        params.put(CommonUtilities.key_pushnotification_toekn,getIntent().getStringExtra("token"));
                                        params.put(CommonUtilities.key_pushnotification_toekn,generate_token);
                                        params.put(CommonUtilities.pref_fb_birthday, object.isNull("birthday") ? "" : object.getString("birthday"));
                                        params.put(CommonUtilities.pref_fb_fname, object.getString("first_name"));
                                        params.put(CommonUtilities.pref_fb_lname, object.getString("last_name"));
                                        params.put(CommonUtilities.pref_fb_photo, object.getJSONObject("picture").getJSONObject("data").getString("url"));
                                        params.put(CommonUtilities.pref_fb_gender, object.isNull("gender") ? "" :
                                                object.getString("gender"));
                                        params.put(CommonUtilities.pref_fb_email, object.isNull("email") ? "" :
                                                object.getString("email"));
                                        params.put(CommonUtilities.key_udid, Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
                                        params.put(CommonUtilities.key_security_toekn, CommonUtilities.getSecurity_Preference(getApplicationContext(), CommonUtilities.key_security_toekn));
                                        params.put(CommonUtilities.key_device_type, "2");
                                        params.put(CommonUtilities.key_app_version, BuildConfig.VERSION_NAME);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    Log.d("FacebookLogin","else block");
                                }

                                login_fb_call();
                            }
                        });
//                gender,user_birthday
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,first_name,birthday,last_name,gender,picture.width(300).height(300),verified");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d("FacebookLogin", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FacebookLogin", "facebook:onError", error);
            }
        });

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_birthday"));

    }

    public String getCurrentTimezoneOffset() {

        TimeZone tz = TimeZone.getDefault();

        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());

        String offset = String.format("%02d:%02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
        offset = "GMT" + (offsetInMillis >= 0 ? "+" : "-") + offset;

        return tz.getID();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 111 && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void setFont() {
        CommonUtilities.setFontFamily(SignupLogin.this, textView2, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(SignupLogin.this, textSkip, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(SignupLogin.this, textByRegistering, CommonUtilities.AvenirNextLTPro_Demi);
        CommonUtilities.setFontFamily(SignupLogin.this, textTerms, CommonUtilities.AvenirNextLTPro_Demi);
        CommonUtilities.setFontFamily(SignupLogin.this, textView3, CommonUtilities.AvenirNextLTPro_Demi);
        CommonUtilities.setFontFamily(SignupLogin.this, textPrivacy, CommonUtilities.AvenirNextLTPro_Demi);
    }

    public void OnSkip(View v) {
        CommonUtilities.setPreference(SignupLogin.this, CommonUtilities.pref_login_skip, "true");
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag)) {
            finish();
        } else if (CommonUtilities.getSecurity_Preference(SignupLogin.this, CommonUtilities.pref_allow_Access).equals("true")) {
            Intent i = new Intent(SignupLogin.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(SignupLogin.this, AllowAccess.class);
            startActivity(i);
            finish();
        }
    }

    public void login_fb_call() {
        Log.d("FacebookLogin","login fb call");
        Log.d("FacebookLogin",params.toString());
        ServerAccess.getResponse(this, CommonUtilities.key_signup_service, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                model = new SignUpModel().SignUpModel(result);
                Log.d("FacebookLogin",result);
                if (model != null) {
                    if (model.response.status.equals(CommonUtilities.key_Success)) {
                        if (model.response.user_data.token != null)
                            CommonUtilities.setSecurity_Preference(SignupLogin.this, CommonUtilities.key_security_toekn, model.response.user_data.token);
                        CommonUtilities.setPreference(SignupLogin.this, CommonUtilities.pref_UserData, result);
                        CommonUtilities.setPreference(SignupLogin.this, CommonUtilities.pref_UserId, model.response.user_data.user_id);
                        if (model.response.user_data.flag.equals("1")) {
                            if (model.response.user_data.user_active_status.equals("1")) {
                                CommonUtilities.setPreference(SignupLogin.this, CommonUtilities.pref_UserData, result);
                                //CommonUtilities.ShowToast(SignupLogin.this, model.response.msg);
                                if (CommonUtilities.getSecurity_Preference(SignupLogin.this, CommonUtilities.pref_allow_Access).equals("true")) {
                                    Intent i = new Intent(SignupLogin.this, MainActivity.class);
                                    if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_drinks)) {
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_drinks);
                                        startActivity(i);
                                        finish();
                                    } else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_birtday)) {
//                                        Intent intent = new Intent();
//                                        setResult(RESULT_OK, intent);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_birtday);
                                        CommonUtilities.setPreference(SignupLogin.this, CommonUtilities.pref_from_birthday, "true");
                                        startActivity(i);
                                        finish();
                                    } else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_comment)) {
                                        Intent intent = new Intent();
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    } else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_my_profile)) {
                                        i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_my_profile);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        startActivity(i);
                                        finish();
                                    }
                                } else {
                                    Intent i = new Intent(SignupLogin.this, AllowAccess.class);
                                    startActivity(i);
                                    finish();
                                }
                            } else {
                                CommonUtilities.alertdialog(SignupLogin.this, model.response.msg);
                            }
                        }
                        if (model.response.user_data.flag.equals("0")) {
                            Intent i = new Intent(SignupLogin.this, SignUpMobile.class);
                            //CommonUtilities.ShowToast(SignupLogin.this, model.response.msg);
                            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_drinks)) {
                                i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_drinks);
                                startActivity(i);
                                finish();
                            } else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_birtday)) {
                                i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_birtday);
//                                startActivityForResult(i, 222);
                                startActivity(i);
                                finish();
                            } else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_comment)) {
                                i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_comment);
                                startActivityForResult(i, 111);
                            } else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_my_profile)) {
                                i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_my_profile);
                                startActivity(i);
                                finish();
                            } else {
                                startActivity(i);
                                finish();
                            }
                        }
                    } else {
                        CommonUtilities.alertdialog(SignupLogin.this, model.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void policyClick(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CommonUtilities.getSecurity_Preference(SignupLogin.this, CommonUtilities.pref_privacy_url)));
        startActivity(browserIntent);
    }

    public void termsClick(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CommonUtilities.getSecurity_Preference(SignupLogin.this, CommonUtilities.pref_term_url)));
        startActivity(browserIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    @Override
    protected void onStart() {
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {

        }


    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.btn_fb_custom:
//                if (CommonUtilities.hasPermissions(SignupLogin.this, PERMISSIONS)) {
//                    setLoc();
//                    FBtnClick();
//                } else {
//                    CommonUtilities.setPermission(SignupLogin.this, PERMISSIONS);
//                }
//                break;
//        }
//    }

    public void setLoc() {
        GPSTracker gps;
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        gps = new GPSTracker(SignupLogin.this);
        if (statusOfGPS) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                if (addresses.size() != 0) {
                    CountryName = addresses.get(0).getCountryName();
                    CountryCode = addresses.get(0).getCountryCode();
                    Log.i("country", CountryCode);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

}
