package com.Gr8niteout.signup;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.Gr8niteout.AllowAccess;
import com.Gr8niteout.MainActivity;
import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.OtpModel;
import com.Gr8niteout.model.SignUpModel;
import com.Gr8niteout.model.UserVerification;
import com.Gr8niteout.myprofile.EditProfileActivity;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;


public class SignUpOtp extends AppCompatActivity {

    OtpModel model;
    private CountDownTimer countDownTimer;
    private final long startTime = 60 * 1000;
    private final long interval = 2 * 1000;
    //    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //    @BindView(R.id.mob_no)
    TextView mob_no;

    //    @BindView(R.id.txtEnterConfitmation)
    TextView txtEnterConfitmation;

    //    @BindView(R.id.back)
    ImageView back;
    //    @BindView(R.id.textView4)
    TextView textView4;

    //    @BindView(R.id.textView5)
    TextView textView5;

    String CCode, MobNo;
    String flag = "0";
    LocationManager locationManager;
    PinEntryEditText pinEntry;
    Intent intent;
    SignUpModel signUpModel;
    UserVerification user_model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_otp);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mob_no = (TextView) findViewById(R.id.mob_no);
        txtEnterConfitmation = (TextView) findViewById(R.id.txtEnterConfitmation);

        back = (ImageView) findViewById(R.id.back);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setFont();

        signUpModel = new SignUpModel().SignUpModel(CommonUtilities.getPreference(this, CommonUtilities.pref_UserData));
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        intent = getIntent();
        if (intent != null) {
            CCode = intent.getStringExtra("code");
            MobNo = intent.getStringExtra("mobile_no");
            mob_no.setText(CCode + " " + MobNo + " to complete");
        }
        model = new OtpModel().OtpModel(CommonUtilities.getPreference(this, CommonUtilities.pref_otp));
        countDownTimer = new MyCountDownTimer(startTime, interval);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pinEntry = (PinEntryEditText) findViewById(R.id.txt_pin_entry);

//        if (model.response.verification_code.varification_code != null)
//            CommonUtilities.alertdialog(this, "Use this code for verification:" + model.response.verification_code.varification_code);

        if (pinEntry != null) {
            pinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
                @Override
                public void onPinEntered(CharSequence str) {
                    if (!model.response.verification_code.varification_code.toString().equals("")) {
                        if (flag.equals("0")) {
                            if (str.toString().equals(model.response.verification_code.varification_code)) {
                                getUserDetails();
                            } else {
                                Toast.makeText(SignUpOtp.this, "Enter correct OTP", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                        }
                    }
                }
            });
        }
    }


    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            flag = "1";
            AlertDialog.Builder builder1 = new AlertDialog.Builder(SignUpOtp.this);
            builder1.setTitle(R.string.app_name);
            builder1.setIcon(R.mipmap.app_icon);
            builder1.setMessage("OTP has been expired");
            builder1.setCancelable(false);
            builder1.setPositiveButton("RESEND", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ResendSmsCall();
                    pinEntry.setText(null);
                    dialog.cancel();
                }
            });

            builder1.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    pinEntry.setText(null);
                    dialog.cancel();
                }
            });

            AlertDialog alert11 = builder1.create();
            alert11.show();

            Button bq = alert11.getButton(DialogInterface.BUTTON_POSITIVE);
            bq.setTextColor(Color.parseColor("#0053A8"));
            Button bq1 = alert11.getButton(DialogInterface.BUTTON_NEGATIVE);
            bq1.setTextColor(Color.parseColor("#0053A8"));
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }
    }


    public void ResendSmsBtn(View v) {
        ResendSmsCall();
    }

    public void ResendSmsCall() {
        flag = "0";
        Map<String, String> params = new HashMap<String, String>();
        params.put(CommonUtilities.key_user_id, CommonUtilities.getPreference(SignUpOtp.this, CommonUtilities.pref_UserId));
        params.put(CommonUtilities.key_mobile, MobNo);
        params.put(CommonUtilities.key_cc_code, CCode.substring(1));
        params.put(CommonUtilities.key_flag, "1");
        ServerAccess.getResponse(this, CommonUtilities.key_get_verification_code, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                model = new OtpModel().OtpModel(result);
                if (model != null) {
                    if (model.response.status.equals(CommonUtilities.key_Success)) {
                        if (model.response != null) {
                            CommonUtilities.setPreference(SignUpOtp.this, CommonUtilities.pref_otp, model.response.verification_code.varification_code);
                            countDownTimer.cancel();
                            countDownTimer.start();
                            pinEntry.setText(null);
                        }
                    } else {
                        CommonUtilities.alertdialog(SignUpOtp.this, model.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void getUserDetails() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(CommonUtilities.key_user_id, signUpModel.response.user_data.user_id);
        params.put("mobile_no", MobNo);
        params.put(CommonUtilities.key_cc_code, CCode.substring(1));
        ServerAccess.getResponse(this, CommonUtilities.key_user_verification, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                user_model = new UserVerification().UserVerification(result);
                if (user_model != null) {
                    if (user_model.response.status.equals(CommonUtilities.key_Success)) {
                        if (user_model.response != null) {
                            Toast.makeText(SignUpOtp.this, "All Done!", Toast.LENGTH_SHORT).show();

//                            CommonUtilities.setPreference(SignUpOtp.this, CommonUtilities.pref_UserId, signUpModel.response.user_data.user_id);
                            signUpModel = new SignUpModel().SignUpModel(CommonUtilities.getPreference(SignUpOtp.this, CommonUtilities.pref_UserData));

                            CommonUtilities.setPreference(SignUpOtp.this, CommonUtilities.pref_UserId, user_model.response.user_data.user_id);
                            CommonUtilities.setPreference(SignUpOtp.this, CommonUtilities.pref_UserData, new Gson().toJson(signUpModel));

                            if (CommonUtilities.getSecurity_Preference(SignUpOtp.this, CommonUtilities.pref_allow_Access).equals("true")) {

                                if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_drinks)) {
                                    Intent i = new Intent(SignUpOtp.this, MainActivity.class);
                                    CommonUtilities.setPreference(SignUpOtp.this, CommonUtilities.pref_UserData, result);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_drinks);
                                    if (intent != null) {
                                        signUpModel.response.user_data.setMobile(MobNo);
                                        signUpModel.response.user_data.setCc_code(CCode.substring(1));
                                    }
                                    startActivity(i);
                                    finish();

                                } else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_profile)) {
                                    Intent i = new Intent(SignUpOtp.this, EditProfileActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_profile);
                                    CCode = intent.getStringExtra("code");
                                    MobNo = intent.getStringExtra("mobile_no");
                                    i.putExtra(CommonUtilities.key_cc_code, CCode.substring(1));
                                    i.putExtra(CommonUtilities.key_mobile, MobNo);
                                    startActivity(i);
                                    finish();

                                } else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_birtday)) {

                                    CommonUtilities.setPreference(SignUpOtp.this, CommonUtilities.pref_UserData, result);
                                    Intent i = new Intent(SignUpOtp.this, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_birtday);
                                    if (intent != null) {
                                        signUpModel.response.user_data.setMobile(MobNo);
                                        signUpModel.response.user_data.setCc_code(CCode.substring(1));
                                    }
                                    CommonUtilities.setPreference(SignUpOtp.this, CommonUtilities.pref_from_birthday, "true");
                                    startActivity(i);
                                    finish();

                                } else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_comment)) {

                                    CommonUtilities.setPreference(SignUpOtp.this, CommonUtilities.pref_UserData, result);
                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
                                    if (intent != null) {
                                        signUpModel.response.user_data.setMobile(MobNo);
                                        signUpModel.response.user_data.setCc_code(CCode.substring(1));
                                    }
                                    finish();
                                } else {
                                    Intent i = new Intent(SignUpOtp.this, MainActivity.class);
                                    CommonUtilities.setPreference(SignUpOtp.this, CommonUtilities.pref_UserData, result);
                                    if (intent != null) {
                                        signUpModel.response.user_data.setMobile(MobNo);
                                        signUpModel.response.user_data.setCc_code(CCode.substring(1));
                                    }
                                    if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_my_profile)) {
                                        i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_my_profile);
                                        if (intent != null) {
                                            signUpModel.response.user_data.setMobile(MobNo);
                                            signUpModel.response.user_data.setCc_code(CCode.substring(1));
                                        }
                                    }
                                    startActivity(i);
                                    finish();
                                }

                            } else {
                                CommonUtilities.setPreference(SignUpOtp.this, CommonUtilities.pref_UserData, result);
                                Intent i = new Intent(SignUpOtp.this, AllowAccess.class);
                                signUpModel.response.user_data.setMobile(user_model.response.user_data.getMobile());
                                signUpModel.response.user_data.setCc_code(user_model.response.user_data.getCc_code().substring(1));
                                startActivity(i);
                                finish();
                            }
                            countDownTimer.cancel();
                        }
                    } else {
                        CommonUtilities.alertdialog(SignUpOtp.this, signUpModel.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        countDownTimer.start();
    }

    public void setFont() {
        CommonUtilities.setFontFamily(SignUpOtp.this, txtEnterConfitmation, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(SignUpOtp.this, textView4, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(SignUpOtp.this, mob_no, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(SignUpOtp.this, textView5, CommonUtilities.AvenirLTStd_Medium);
//        CommonUtilities.setFontFamily(SignUpOtp.this, pinEntry, CommonUtilities.Avenir); //Avenir (OT1) : Regular

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
