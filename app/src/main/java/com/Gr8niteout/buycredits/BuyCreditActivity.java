package com.Gr8niteout.buycredits;



import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.Gr8niteout.R;
import com.Gr8niteout.config.CircleTransform;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.RoundImageview;
import com.Gr8niteout.config.RoundedTransformation;
import com.Gr8niteout.model.SignUpModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.BindView;

import static com.Gr8niteout.config.CommonUtilities.isBack_credit;

public class BuyCreditActivity extends AppCompatActivity {

//    @BindView(R.id.img_pub)
    ImageView imgPub;
//    @BindView(R.id.imgPlaceHolder)
    ImageView imgPlaceHolder;
//    @BindView(R.id.layoutGray)
    LinearLayout layoutGray;
//    @BindView(R.id.txtPub)
    TextView txtPub;
//    @BindView(R.id.txt_first_name)
    TextView txt_first_name;
//    @BindView(R.id.txtPubName)
    TextView txtPubName;
//    @BindView(R.id.txtNotice)
    TextView txtNotice;
//    @BindView(R.id.layoutLoggedIn)
    LinearLayout layoutLoggedIn;
//    @BindView(R.id.img_layout)
    RelativeLayout img_layout;
//    @BindView(R.id.imgProfile)
    RoundImageview imgProfile;
//    @BindView(R.id.txtName)
    TextView txtName;
//    @BindView(R.id.txtEmail)
    TextView txtEmail;
//    @BindView(R.id.txtTitle)
    TextView txtTitle;
//    @BindView(R.id.txtConfirm)
    TextView txtConfirm;
//    @BindView(R.id.layoutNotLoggedIn)
    LinearLayout layoutNotLoggedIn;
//    @BindView(R.id.edtFullName)
    EditText edtFullName;
//    @BindView(R.id.edtEmail)
    EditText edtEmail;
//    @BindView(R.id.edtCredit)
    EditText edtCredit;
//    @BindView(R.id.btnContinue)
    Button btnContinue;
//    @BindView(R.id.btnCurrency)
    Button btnCurrency;
    SignUpModel signUpModel;
    private String From = "";
    String currency = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_credit);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        imgPub = (ImageView) findViewById(R.id.img_pub);
        imgPlaceHolder = (ImageView) findViewById(R.id.imgPlaceHolder);
        layoutGray = (LinearLayout) findViewById(R.id.layoutGray);
        txtPub = (TextView) findViewById(R.id.txtPub);
        txt_first_name = (TextView) findViewById(R.id.txt_first_name);
        txtPubName = (TextView) findViewById(R.id.txtPubName);
        txtNotice = (TextView) findViewById(R.id.txtNotice);
        layoutLoggedIn = (LinearLayout) findViewById(R.id.layoutLoggedIn);
        img_layout = (RelativeLayout) findViewById(R.id.img_layout);
        imgProfile = (RoundImageview) findViewById(R.id.imgProfile);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtConfirm = (TextView) findViewById(R.id.txtConfirm);
        layoutNotLoggedIn = (LinearLayout) findViewById(R.id.layoutNotLoggedIn);
        edtFullName = (EditText) findViewById(R.id.edtFullName);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtCredit = (EditText) findViewById(R.id.edtCredit);
        btnContinue = (Button) findViewById(R.id.btnContinue);
        btnCurrency = (Button) findViewById(R.id.btnCurrency);


        setSupportActionBar(toolbar);
        toolbar.setContentInsetStartWithNavigation(0);
        getSupportActionBar().setTitle("");
        txtTitle.setText("Buy Pub Credit");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imgProfile.setVisibility(View.VISIBLE);
        if (getIntent().getExtras() != null) {
            txtPubName.setText(getIntent().getExtras().getString(CommonUtilities.key_pub_name));

            From = getIntent().getStringExtra("from");
            Picasso.get()
                    .load(getIntent().getExtras().getString(CommonUtilities.key_pub_image))
                    .into(imgPub, new Callback() {
                        @Override
                        public void onSuccess() {
                            imgPlaceHolder.setVisibility(View.GONE);
                            layoutGray.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            imgPlaceHolder.setVisibility(View.GONE);
                        }
                    });

            if (getIntent().getExtras().getString("currency").equals("1")) {
                btnCurrency.setText("GBP");
                edtCredit.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.currency, 0, 0, 0);
                currency = "gbp";
            }
            if (getIntent().getExtras().getString("currency").equals("2")) {
                btnCurrency.setText("EUR");
                edtCredit.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.euro, 0, 0, 0);
                currency = "eur";
            }
            if (getIntent().getExtras().getString("currency").equals("3")) {
                btnCurrency.setText("USD");
                edtCredit.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.dollar, 0, 0, 0);
                currency = "usd";
            }
        }
        if (CommonUtilities.getPreference(BuyCreditActivity.this, CommonUtilities.pref_UserId).equals("")) {
            layoutLoggedIn.setVisibility(View.GONE);
            layoutNotLoggedIn.setVisibility(View.VISIBLE);
        } else {
            signUpModel = new SignUpModel().SignUpModel(CommonUtilities.getPreference(BuyCreditActivity.this, CommonUtilities.pref_UserData));
            txtName.setText(signUpModel.response.user_data.fname + " " + signUpModel.response.user_data.lname);
            txtEmail.setText(signUpModel.response.user_data.email);
            if (signUpModel.response.user_data.getPhoto().equals("")) {
//                imgProfile.setImageResource(R.mipmap.no_user);
                img_layout.setBackgroundResource(R.drawable.round);
                imgProfile.setVisibility(View.GONE);
                if (!signUpModel.response.user_data.getFname().equals("")) {
                    txt_first_name.setVisibility(View.VISIBLE);
                    txt_first_name.setText(signUpModel.response.user_data.getFname().substring(0, 1));
                } else {
                    imgProfile.setImageResource(R.mipmap.user);
                    txt_first_name.setVisibility(View.GONE);
                }
            } else {
                txt_first_name.setVisibility(View.GONE);
//                changed on 28-jan-2019
                Picasso.get()
                        .load(CommonUtilities.Gr8niteoutURL + CommonUtilities.User_Profile_URL + signUpModel.response.user_data.photo)
                        .error(R.mipmap.user).placeholder(R.mipmap.user) // optional
                        .transform(new CircleTransform())
                        .into(imgProfile, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
            }
            layoutLoggedIn.setVisibility(View.VISIBLE);
            layoutNotLoggedIn.setVisibility(View.GONE);
        }

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtCredit.getText().toString().equals("")) {
                    CommonUtilities.showSnackbar(edtCredit, "Please enter an amount");
                } else if (edtCredit.getText().toString().equals("0") || edtCredit.getText().toString().equals("0.00") || edtCredit.getText().toString().equals("0.0") || edtCredit.getText().toString().equals(".")) {
                    CommonUtilities.showSnackbar(edtCredit, "Please enter an amount");
                } else if (CommonUtilities.getPreference(BuyCreditActivity.this, CommonUtilities.pref_UserId).equals("")) {
                    if (edtFullName.getText().toString().equals("")) {
                        CommonUtilities.showSnackbar(edtFullName, "Please enter Full Name");
                    } else if (edtEmail.getText().toString().equals("")) {
                        CommonUtilities.showSnackbar(edtEmail, "Please enter Email Address");
                    } else if (!CommonUtilities.emailValidator(edtEmail.getText().toString())) {
                        CommonUtilities.showSnackbar(edtEmail, "Please enter valid Email Address");
                    } else {
                        CommonUtilities.hideSoftKeyboard(BuyCreditActivity.this, edtFullName);
                        Intent i = new Intent(BuyCreditActivity.this, RecipientActivity.class);
                        if (edtCredit.getText().toString().startsWith("."))
                            i.putExtra("amount", "0" + edtCredit.getText().toString());
                        else
                            i.putExtra("amount", edtCredit.getText().toString());

                        i.putExtra("currency",currency.toUpperCase());
                        i.putExtra("name", edtFullName.getText().toString());
                        i.putExtra("email", edtEmail.getText().toString());
                        i.putExtra("From",From);
                        i.putExtra(CommonUtilities.key_pub_id, getIntent().getExtras().getString(CommonUtilities.key_pub_id));
                        i.putExtra("recipient_name",getIntent().getStringExtra("recipient_name"));
                        i.putExtra("recipient_email",getIntent().getStringExtra("recipient_email"));
                        startActivity(i);
                    }
                } else {
                    CommonUtilities.hideSoftKeyboard(BuyCreditActivity.this, edtFullName);
                    Intent i = new Intent(BuyCreditActivity.this, RecipientActivity.class);
                    if (edtCredit.getText().toString().startsWith("."))
                        i.putExtra("amount", "0" + edtCredit.getText().toString());
                    else
                        i.putExtra("amount", edtCredit.getText().toString());
                    i.putExtra("currency",currency.toUpperCase());
                    i.putExtra("name", txtName.getText().toString());
                    i.putExtra("email", txtEmail.getText().toString());
                    i.putExtra("From",From);
                    i.putExtra(CommonUtilities.key_pub_id, getIntent().getExtras().getString(CommonUtilities.key_pub_id));
                    i.putExtra("recipient_name",getIntent().getStringExtra("recipient_name"));
                    i.putExtra("recipient_email",getIntent().getStringExtra("recipient_email"));
                    startActivity(i);
                }
            }
        });

        edtEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_GO)) {
                    btnContinue.performClick();
                    return true;
                }
                return false;
            }
        });

        setFont();

    }

    public void setFont() {
        CommonUtilities.setFontFamily(BuyCreditActivity.this, txtTitle, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(BuyCreditActivity.this, txtPubName, CommonUtilities.Avenir);
        CommonUtilities.setFontFamily(BuyCreditActivity.this, txtPub, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(BuyCreditActivity.this, edtCredit, CommonUtilities.Avenir);
        CommonUtilities.setFontFamily(BuyCreditActivity.this, btnCurrency, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(BuyCreditActivity.this, txtName, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(BuyCreditActivity.this, txtEmail, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(BuyCreditActivity.this, edtEmail, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(BuyCreditActivity.this, edtFullName, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(BuyCreditActivity.this, btnContinue, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(BuyCreditActivity.this, txtNotice, CommonUtilities.Avenir);
        CommonUtilities.setFontFamily(BuyCreditActivity.this, txtConfirm, CommonUtilities.Avenir);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_setting).setVisible(false);
        menu.findItem(R.id.action_search_white).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_reset).setVisible(false);
        menu.findItem(R.id.action_transaction_history).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        isBack_credit = true;
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CommonUtilities.isBack_credit) {
            CommonUtilities.isBack_credit = false;
            super.onBackPressed();
        }
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
