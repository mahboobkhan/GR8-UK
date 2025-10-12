package com.Gr8niteout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Gr8niteout.config.CommonUtilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import butterknife.ButterKnife;
import butterknife.BindView;

public class AllowAccess extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {

//    @BindView(R.id.btnNext)
    Button btnNext;
//    @BindView(R.id.textSkip)
    TextView textSkip;

//    @BindView(R.id.allow_access_image)
    ImageView allow_access_image;

//    @BindView(R.id.how_use_layout)
    RelativeLayout how_use_layout;

    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;

    String flag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howto_useapp);
        ButterKnife.bind(this);

        btnNext = findViewById(R.id.btnNext);
        textSkip = findViewById(R.id.textSkip);
        allow_access_image = findViewById(R.id.allow_access_image);
        how_use_layout = findViewById(R.id.how_use_layout);
        allow_access_image.setVisibility(View.VISIBLE);

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        setFont();
        CommonUtilities.setSecurity_Preference(AllowAccess.this,CommonUtilities.pref_allow_Access,"true");

        if(statusOfGPS){
            how_use_layout.setBackgroundResource(R.mipmap.contacts);
            allow_access_image.setImageResource(R.mipmap.contacts);
            btnNext.setBackgroundResource(R.mipmap.ok_access_it_button);
            flag = "2";
            flag = "5";
            findViewById(R.id.textSkip).setVisibility(View.VISIBLE);
        }else {
            flag = "1";
            how_use_layout.setBackgroundResource(R.mipmap.location);
            btnNext.setBackgroundResource(R.mipmap.turn_it_on);
            allow_access_image.setImageResource(R.mipmap.location);
            findViewById(R.id.textSkip).setVisibility(View.VISIBLE);

        }
    }
    public void Next(View v) {
        if(flag.equals("1")) {
            noLocation();
        }
        else if(flag.equals("2")){
            how_use_layout.setBackgroundResource(R.mipmap.contacts);
            allow_access_image.setImageResource(R.mipmap.contacts);
            btnNext.setBackgroundResource(R.mipmap.ok_access_it_button);
            flag = "3";
        }
        else if(flag.equals("3") || flag.equals("5") ||flag.equals("6")){
            CommonUtilities.setBooleanPreference(AllowAccess.this, CommonUtilities.pref_Contact_Access,true);
            how_use_layout.setBackgroundResource(R.mipmap.notifications);
            allow_access_image.setImageResource(R.mipmap.notifications);
            btnNext.setBackgroundResource(R.mipmap.turn_it_on);
            flag = "4";
        }
        else if(flag.equals("4") || flag.equals("7")){
            CommonUtilities.setSecurityBooleanPreference(AllowAccess.this, CommonUtilities.pref_Notification_Access,true);
            Intent i = new Intent(AllowAccess.this,MainActivity.class);
            startActivity(i);
            finish();
        }
    }


    public boolean noLocation() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //  buildAlertMessageNoGps();
            enableLoc();
            return true;
        }else{
            flag = "2";
            btnNext.performClick();

        }
        return false;

    }


    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

//                            Log.v("Location error " + connectionResult.getErrorCode()+"");
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        AllowAccess.this, REQUEST_LOCATION);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK:{
                        flag = "2";
                        btnNext.performClick();
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        flag = "2";
                        btnNext.performClick();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    public void setFont(){
        CommonUtilities.setFontFamily(AllowAccess.this, textSkip, CommonUtilities.Avenir_Heavy);
    }

    public void OnSkip(View v){
         if(flag.equals("1")){
            how_use_layout.setBackgroundResource(R.mipmap.contacts);
             allow_access_image.setImageResource(R.mipmap.contacts);
            btnNext.setBackgroundResource(R.mipmap.ok_access_it_button);
            flag = "6";
        }
        else if(flag.equals("6") || flag.equals("5") ||  flag.equals("3")){
            how_use_layout.setBackgroundResource(R.mipmap.notifications);
             allow_access_image.setImageResource(R.mipmap.notifications);
            btnNext.setBackgroundResource(R.mipmap.turn_it_on);
            flag = "7";
        }
        else if(flag.equals("7") ||   flag.equals("4")){
            Intent i = new Intent(AllowAccess.this,MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}
