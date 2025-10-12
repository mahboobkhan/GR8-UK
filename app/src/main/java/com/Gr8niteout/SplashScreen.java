package com.Gr8niteout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import com.Gr8niteout.PubDashboardScreens.DashboardHomeActivity;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.CountryModel;
import com.Gr8niteout.model.SettingUrlModel;
import com.Gr8niteout.model.TokenInfoModel;
import com.Gr8niteout.signup.SignupLogin;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.iid.FirebaseInstanceId;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {
    public TokenInfoModel model;
    public CountryModel model1;
    public SettingUrlModel model2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        String refreshedToken = FirebaseMessaging.getInstance().getToken().toString();
//        CommonUtilities.ShowToast(this,refreshedToken);
//        Log.d("KeyHashRefreshedToken:", refreshedToken);

//        CommonUtilities.setPreference(this,CommonUtilities.pref_push_token,refreshedToken);
//        generate_token();
//        getCountries();

        doNext();
        try {


            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.Gr8niteout",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
//        getFCMToken();
    }


    public void getFCMToken() {
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
                    Log.d("FCM Token", "FCM Token: " + token);
                    CommonUtilities.setPreference(this,CommonUtilities.pref_push_token,token);

                    // You can now use this token to send notifications to this device.
                });
    }

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
                    Log.d("FCM Token", "FCM Token: " + token);
                    generate_token = token;
                    CommonUtilities.setPreference(this,CommonUtilities.pref_push_token,token);
                    params.put(CommonUtilities.key_pushnotification_toekn,token);


                    // You can now use this token to send notifications to this device.
                });

        params.put(CommonUtilities.key_encoded_key,random_function());
        params.put(CommonUtilities.key_udid, Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));

        Log.d("TAG", "generate_token1:"+params);
        ServerAccess.getResponse(this, CommonUtilities.key_generate_token, params,false, new ServerAccess.VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                model =  new TokenInfoModel().TokenInfoModel(result);
                if (model != null) {
                    if (model.response.status.equals(CommonUtilities.key_Success)) {
                        if(model.response.token_info.token!=null) {
                            CommonUtilities.setSecurity_Preference(SplashScreen.this, CommonUtilities.pref_term_url, model.response.token_info.term_url);
                            CommonUtilities.setSecurity_Preference(SplashScreen.this, CommonUtilities.pref_privacy_url, model.response.token_info.privacy_url);
                            CommonUtilities.setSecurity_Preference(SplashScreen.this, CommonUtilities.key_security_toekn, model.response.token_info.token);
                            if (CommonUtilities.getPreference(SplashScreen.this, CommonUtilities.pref_Countries).equals(""))
                                getCountries();
                            else
                                doNext();
                        }
                    }
                    else
                    {
                        CommonUtilities.alertdialog(SplashScreen.this,model.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void doNext()
    {
        SharedPreferences preferences = getSharedPreferences("pub_details",MODE_PRIVATE);
        if(preferences.getBoolean("isPubLoggedIn",false)){
            startActivity(new Intent(SplashScreen.this, DashboardHomeActivity.class));
        }else {
            if (CommonUtilities.getSecurity_Preference(SplashScreen.this, CommonUtilities.pref_Howtouseapp).equals("")) {
                CommonUtilities.setSecurity_Preference(SplashScreen.this, CommonUtilities.pref_Howtouseapp, "true");
                Intent i = new Intent(SplashScreen.this, HowToUseApp.class);
                startActivity(i);
            } else if (CommonUtilities.getPreference(SplashScreen.this, CommonUtilities.pref_login_skip).equals("true")) {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
            } else if (CommonUtilities.getPreference(SplashScreen.this, CommonUtilities.pref_UserId).equals("")) {
                Intent i = new Intent(SplashScreen.this, SignupLogin.class);
                i.putExtra("token", generate_token);
                startActivity(i);
            } else {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
            }
        }
        finishAffinity();
    }

    public void getCountries()
    {
        Map<String, String> params = new HashMap<String, String>();

        ServerAccess.getResponse(SplashScreen.this, CommonUtilities.key_countries, params,false, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                model1 =  new CountryModel().CountryModel(result);
                if (model1 != null) {
                    if (model1.response.status.equals(CommonUtilities.key_Success)) {
                        CommonUtilities.setSecurity_Preference(SplashScreen.this, CommonUtilities.pref_Countries,result);
                        doNext();
                    }
                    else
                    {
                        CommonUtilities.alertdialog(SplashScreen.this,model1.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public static String random_function() {
        String source = "oeeCokGTeTCGoo";
        String base64 = "";
        byte[] data = null;
        try {
            data = source.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        base64 = Base64.encodeToString(data, Base64.NO_WRAP);
        return base64;
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
}
