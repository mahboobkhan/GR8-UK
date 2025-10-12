package com.Gr8niteout.signup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.GPSTracker;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.CountryModel;
import com.Gr8niteout.model.OtpModel;
import com.Gr8niteout.model.SignUpModel;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.BindView;

public class SignUpMobile extends AppCompatActivity {

    OtpModel model;
    SignUpModel model1;

//    @BindView(R.id.mob_no_edt)
    EditText mob_no_edt;

//    @BindView(R.id.txtSetCountry)
    TextView txtSetCountry;

//    @BindView(R.id.txtConfirmMobile)
    TextView txtConfirmMobile;

//    @BindView(R.id.txtSelectCountry)
    TextView txtSelectCountry;

//    @BindView(R.id.txtCode)
    EditText txtCode;
//    @BindView(R.id.main_layout)
    LinearLayout main_layout;

//    @BindView(R.id.btnSendSMS)
    Button btnSendSMS;


    String CountryCode, CountryName, country;
    String flag = "0";


    CountryModel countrymodel;
    double latitude; // latitude
    double longitude;
    String ComeFromProf;
    final static int REQUEST_LOCATION = 199;
    boolean fromprof = false;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_phone_number);
        ButterKnife.bind(this);

        mob_no_edt = (EditText) findViewById(R.id.mob_no_edt);
        txtSetCountry = (TextView) findViewById(R.id.txtSetCountry);
        txtConfirmMobile = (TextView) findViewById(R.id.txtConfirmMobile);
        txtSelectCountry = (TextView) findViewById(R.id.txtSelectCountry);
        txtCode = (EditText) findViewById(R.id.txtCode);
        main_layout = (LinearLayout) findViewById(R.id.main_layout);
        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        countrymodel = new CountryModel().CountryModel(CommonUtilities.getSecurity_Preference(this, CommonUtilities.pref_Countries));

        setFont();

        String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};


        if (CommonUtilities.hasPermissions(SignUpMobile.this, PERMISSIONS)) {
            setLoc();
        } else {
            CommonUtilities.setPermission(SignUpMobile.this, PERMISSIONS);
        }

        mob_no_edt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_GO)) {
                    btnSendSMS.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    public void SendSms(View v) {
        if (mob_no_edt.getText().toString().equals("")) {
            CommonUtilities.showSnackbar(findViewById(R.id.mob_no_edt), "Please enter your mobile number");
        } else if (mob_no_edt.length() < 6) {
            CommonUtilities.showSnackbar(findViewById(R.id.mob_no_edt), "Please enter a valid number");
        } else {

            SendSmsCall();
        }
    }

    public void SendCountry(View v) {
        Intent i = new Intent(SignUpMobile.this, CountryList.class);
        startActivityForResult(i, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setLoc();
        } else {

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 112 && resultCode == RESULT_OK) {

            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }

        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                CountryCode = data.getStringExtra("code");
                CountryName = data.getStringExtra("name");
                txtCode.setText("+" + CountryCode);
                txtSetCountry.setText(CountryName);
                //#0079FF
                txtSetCountry.setTextColor(getResources().getColor(R.color.CountryTextColor));
            }
        }
    }

    public void setLoc() {
        GPSTracker gps;
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        gps = new GPSTracker(SignUpMobile.this);
        if (statusOfGPS) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                if(addresses.size()!=0)
                {
                    country = addresses.get(0).getCountryName();
                    Log.i("country", country);

                    for (int i = 0; i < countrymodel.response.country_list.size(); i++) {

                        if (countrymodel.response.country_list.get(i).country_name.equalsIgnoreCase(country)) {

                            String cc = countrymodel.response.country_list.get(i).numcode;
                            txtSetCountry.setText(country);
                            txtCode.setText("+" + cc);

                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public void SendSmsCall() {
        Map<String, String> params = new HashMap<String, String>();

        params.put(CommonUtilities.key_user_id, CommonUtilities.getPreference(SignUpMobile.this, CommonUtilities.pref_UserId));
        params.put(CommonUtilities.key_mobile, mob_no_edt.getText().toString());
        params.put(CommonUtilities.key_cc_code, txtCode.getText().toString().substring(1));
        params.put(CommonUtilities.key_flag, flag);
        ServerAccess.getResponse(this, CommonUtilities.key_get_verification_code, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                model = new OtpModel().OtpModel(result);
                if (model != null) {
                    if (model.response.status.equals(CommonUtilities.key_Success)) {

                        if (model.response != null) {

                            model1 = new SignUpModel().SignUpModel(CommonUtilities.getPreference(SignUpMobile.this, CommonUtilities.pref_UserData));
                            CommonUtilities.setPreference(SignUpMobile.this, CommonUtilities.pref_otp, result);

                        }
                        Intent intent = new Intent(SignUpMobile.this, SignUpOtp.class);
                        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_drinks)) {
                            intent.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_drinks);
                        }
                        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_birtday)) {
                            intent.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_birtday);
                        }
                        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_profile)) {
                            intent.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_profile);
                        }
                        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_comment)) {
                            intent.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_comment);
                        }
                        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_my_profile)) {
                            intent.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_my_profile);
                        }
                        intent.putExtra("code", txtCode.getText().toString());
                        intent.putExtra("mobile_no", mob_no_edt.getText().toString());
                        startActivityForResult(intent, 112);
                    } else {
                        CommonUtilities.alertdialog(SignUpMobile.this, model.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void setFont() {
        CommonUtilities.setFontFamily(SignUpMobile.this, txtConfirmMobile, CommonUtilities.Avenir); //Avenir (OT1) : Regular
        CommonUtilities.setFontFamily(SignUpMobile.this, txtSelectCountry, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(SignUpMobile.this, txtSetCountry, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(SignUpMobile.this, mob_no_edt, CommonUtilities.Avenir); //Avenir (OT1) : Regular
        CommonUtilities.setFontFamily(SignUpMobile.this, txtCode, CommonUtilities.Avenir); //Avenir (OT1) : Regular
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
