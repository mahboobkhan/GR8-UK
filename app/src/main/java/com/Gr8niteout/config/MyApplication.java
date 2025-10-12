package com.Gr8niteout.config;

import android.app.Application;

import androidx.annotation.NonNull;

import com.Gr8niteout.BuildConfig;
import com.Gr8niteout.R;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
//import com.google.firebase.iid.FirebaseInstanceId;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.stripe.android.PaymentConfiguration;

public class MyApplication extends Application {
    private Tracker mTracker;
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        String refreshedToken = FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
//                if (task.isSuccessful() && task.getResult() != null) {
//                    sendFCMTokenToDatabase(task.getResult());
//                }
            }
        }).toString();
//        CommonUtilities.ShowToast(this,refreshedToken+"");
        CommonUtilities.setPreference(this,CommonUtilities.pref_push_token,refreshedToken);
        PaymentConfiguration.init(
                getApplicationContext(),
                BuildConfig.STRIPE_PUBLISHABLE_KEY
        );
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}