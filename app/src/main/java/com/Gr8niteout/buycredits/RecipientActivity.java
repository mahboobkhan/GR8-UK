package com.Gr8niteout.buycredits;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import com.Gr8niteout.BuildConfig;
import com.Gr8niteout.model.BuyPubCreditResponseModel;
import com.Gr8niteout.model.ClientSecretModel;
import com.Gr8niteout.model.PubForgotPasswordModel;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.Dialog;
import com.Gr8niteout.config.RoundedTransformation;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.CheckedInUserModel;
import com.Gr8niteout.model.Contact;
import com.Gr8niteout.model.CreditModel;
import com.Gr8niteout.signup.CountryList;
import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.gson.Gson;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.BindView;

import static android.graphics.BitmapFactory.decodeFile;
import android.widget.MediaController;

public class RecipientActivity extends AppCompatActivity {
    Context mContext;
    List<String> items;
    private Uri fileUri;
    private AlertDialog dialogPic;
    private ArrayList<Image> images = new ArrayList<>();
//    @BindView(R.id.edtName)
    EditText edtName;
//    @BindView(R.id.edtMobile)
    EditText edtMobile;
//    @BindView(R.id.edtCCode)
    EditText edtCCode;
//    @BindView(R.id.edtEmail)
    EditText edtEmail;
//    @BindView(R.id.edtComment)
    EditText edtComment;
//    @BindView(R.id.imgAttach)
    ImageView imgAttach;
//    @BindView(R.id.imageCancel)
    ImageView imageCancel;
//    @BindView(R.id.ContactsList)
    ListView ContactsList;
//    @BindView(R.id.layoutContacts)
    LinearLayout layoutContacts;
//    @BindView(R.id.inputLayoutName)
    TextInputLayout inputLayoutName;
//    @BindView(R.id.webView)
    WebView webView;
//    @BindView(R.id.scrollview)
    ScrollView scrollview;
//    @BindView(R.id.txtTitle)
    TextView txtTitle;
//    @BindView(R.id.txtNotice)
    TextView txtNotice;
//    @BindView(R.id.txtNotice1)
    TextView txtNotice1;
//    @BindView(R.id.txtTouch)
    TextView txtTouch;
//    @BindView(R.id.txtDetails)
    TextView txtDetails;
//    @BindView(R.id.btnPhoto)
    Button btnPhoto;
//    @BindView(R.id.btnContinue)
    Button btnContinue;
//    @BindView(R.id.layoutContinue)

    VideoView VideoPlaceHolderRecipient;

    LinearLayout layoutContinue;
    ArrayList<Contact> listContacts = new ArrayList<>();
    ArrayList<Contact> listContacts1 = new ArrayList<>();
    Dialog dialog;
    ContactsAdapter adapter;
    CreditModel model;
    String blockCharacterSet = "~#^|$%&*!<>()";
    String blockCharacter = "~#^|$%&*!<>";
    String base64 = "";
    String imageUri = "";
    String VideoUri = "";
    String code, mobile = "";
    private TextInputLayout tIl_country_code, tIl_mobile_number, tIl_email_address;
    private String isFrom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient);
        ButterKnife.bind(this);

        edtName = findViewById(R.id.edtName);
        edtMobile = findViewById(R.id.edtMobile);
        edtCCode = findViewById(R.id.edtCCode);
        edtEmail = findViewById(R.id.edtEmail);
        edtComment = findViewById(R.id.edtComment);
        imgAttach = findViewById(R.id.imgAttach);
        VideoPlaceHolderRecipient =  findViewById(R.id.VideoPlaceHolderRecipient);
        imageCancel = findViewById(R.id.imageCancel);
        ContactsList = findViewById(R.id.ContactsList);
        layoutContacts = findViewById(R.id.layoutContacts);
        inputLayoutName = findViewById(R.id.inputLayoutName);
        webView = findViewById(R.id.webView);
        scrollview = findViewById(R.id.scrollview);
        txtTitle = findViewById(R.id.txtTitle);
        txtNotice = findViewById(R.id.txtNotice);
        txtNotice1 = findViewById(R.id.txtNotice1);
        txtTouch = findViewById(R.id.txtTouch);
        txtDetails = findViewById(R.id.txtDetails);
        btnPhoto = findViewById(R.id.btnPhoto);
        btnContinue = findViewById(R.id.btnContinue);
        layoutContinue = findViewById(R.id.layoutContinue);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setContentInsetStartWithNavigation(0);
        getSupportActionBar().setTitle("");
        txtTitle.setText("Recipient's Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dialog = new Dialog(RecipientActivity.this);

        mContext = this;
        tIl_country_code = findViewById(R.id.tIl_country_code);
        tIl_mobile_number = findViewById(R.id.tIl_mobile_number);
        tIl_email_address = findViewById(R.id.tIl_email_address);

        edtCCode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Intent i = new Intent(RecipientActivity.this, CountryList.class);
                    startActivityForResult(i, 2);

                    return true;
                }

                return false;
            }
        });
        edtCCode.setSelection(edtCCode.getText().length());

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        if (getIntent() != null) {
            isFrom = getIntent().getStringExtra("From");
            if (isFrom.equalsIgnoreCase("CheckIn")) {
                tIl_country_code.setVisibility(View.GONE);
                tIl_email_address.setVisibility(View.VISIBLE);
                edtEmail.setText(getIntent().getStringExtra("recipient_email"));
                tIl_mobile_number.setVisibility(View.GONE);
                txtNotice.setVisibility(View.GONE);
                txtNotice1.setVisibility(View.GONE);
                edtName.setEnabled(false);
            } else {
                if (CommonUtilities.getBooleanPreference(RecipientActivity.this, CommonUtilities.pref_Contact_Access)) {
                    if (!checkContactsPermission()) {
                        requestContacts();
                    } else
                        new get_contact_list().execute();
                } else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RecipientActivity.this);
                    builder1.setTitle(R.string.app_name);
                    builder1.setIcon(R.mipmap.app_icon);
                    builder1.setMessage("Do you want to allow access of contacts?");
                    builder1.setCancelable(false);
                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            CommonUtilities.setBooleanPreference(RecipientActivity.this, CommonUtilities.pref_Contact_Access, true);
                            if (!checkContactsPermission()) {
                                requestContacts();
                            } else
                                new get_contact_list().execute();
                            dialog.cancel();
                        }
                    });
                    builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                        }
                    });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if ((edtName.getInputType() & InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS) != InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS) {
                edtName.setInputType(edtName.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            }
        }

        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (CommonUtilities.getBooleanPreference(RecipientActivity.this, CommonUtilities.pref_Contact_Access) && checkContactsPermission()) {
                    String text = edtName.getText().toString().toLowerCase(Locale.getDefault());
                    if (adapter != null)
                        adapter.filter(text);
                }
              /*  if (count > 0) {
                    btnContinue.setTextColor(Color.WHITE);
                    btnContinue.setEnabled(true);
                } else if (count == 0) {
                    btnContinue.setTextColor(Color.parseColor("#FE8789"));
                    btnContinue.setEnabled(false);
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (CommonUtilities.getBooleanPreference(RecipientActivity.this, CommonUtilities.pref_Contact_Access) && checkContactsPermission()) {
                    if (listContacts.size() == 0) {
                        layoutContacts.setVisibility(View.VISIBLE);
                        ContactsList.setVisibility(View.GONE);
                        edtMobile.setText("");
                        btnContinue.setTextColor(Color.WHITE);
                        btnContinue.setEnabled(true);
                    }
                }
            }
        });

        ContactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edtMobile.setText(listContacts.get(position).number);
                edtEmail.setText(listContacts.get(position).email);
                edtName.setText(listContacts.get(position).name);
                layoutContacts.setVisibility(View.VISIBLE);
                ContactsList.setVisibility(View.GONE);
                btnContinue.setTextColor(Color.WHITE);
                btnContinue.setEnabled(true);
                CommonUtilities.hideSoftKeyboard(RecipientActivity.this, edtName);
                if (listContacts.get(0).email != null) {
                    edtComment.requestFocus();
                } else
                    edtEmail.requestFocus();
            }
        });

        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (source != null && blockCharacterSet.contains(("" + source))) {
                    return "";
                }
                return null;
            }
        };

        InputFilter filter1 = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (source != null && blockCharacter.contains(("" + source))) {
                    return "";
                }
                return null;
            }
        };

        edtName.setFilters(new InputFilter[]{filter});
        edtComment.setFilters(new InputFilter[]{filter1});

        setFont();

        imageCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgAttach.setImageBitmap(null);
                imgAttach.setVisibility(View.GONE);
                VideoPlaceHolderRecipient.setVisibility(View.GONE);
                imageCancel.setVisibility(View.GONE);

                VideoUri = "";
                imageUri = "";
            }
        });


        //checkin feature
        String prefCheck = CommonUtilities.getPreference(mContext, CommonUtilities.key_checkin_data);
        if (!TextUtils.isEmpty(prefCheck)) {
            CheckedInUserModel.CheckInData model = new Gson().fromJson(prefCheck, CheckedInUserModel.CheckInData.class);
            edtName.setText(model.first_name + " " + model.last_name);
            edtMobile.setText(model.mobile_no);
            edtEmail.setText(model.email);
            edtName.setSelection(edtName.getText().toString().length());
            Log.e("TAG", "country_code:-- " + model.cc_code);
            code = model.cc_code;
            edtCCode.setText(code);
            //enable continue button

            btnContinue.setTextColor(Color.WHITE);
            btnContinue.setEnabled(true);
        }

    }

    public String GetCountryZipCode(String countryName) {
        String CountryID = countryName;
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        //CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(countryName.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }

    public void setFont() {
        CommonUtilities.setFontFamily(RecipientActivity.this, txtTitle, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(RecipientActivity.this, txtDetails, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(RecipientActivity.this, edtName, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(RecipientActivity.this, edtCCode, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(RecipientActivity.this, edtMobile, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(RecipientActivity.this, edtEmail, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(RecipientActivity.this, edtComment, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(RecipientActivity.this, txtNotice, CommonUtilities.Avenir);
        CommonUtilities.setFontFamily(RecipientActivity.this, txtTouch, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(RecipientActivity.this, btnPhoto, CommonUtilities.AvenirNextLTPro_Demi);
        CommonUtilities.setFontFamily(RecipientActivity.this, btnContinue, CommonUtilities.Avenir_Heavy);
    }

    private class get_contact_list extends AsyncTask<String, Void, String> {
//        Dialog f;

        @Override
        protected String doInBackground(String... params) {

            getContacts();
            return "true";

        }

        @Override
        protected void onPostExecute(String result) {

//            if (f.isShowing())
//                f.dismiss();
            adapter = new ContactsAdapter();
            ContactsList.setAdapter(adapter);
        }

        @Override
        protected void onPreExecute() {
//            f = new Dialog(RecipientActivity.this);
//            f.show();
        }
    }

    public static String toBase64(String value){
        if (value == null)
            value = "";
        return Base64.encodeToString(value.trim().getBytes(), Base64.NO_WRAP);
    }

    private void sendPubCredit() {
//        String img_str;
        Map<String, String> params = new HashMap<String, String>();
        if (CommonUtilities.getPreference(RecipientActivity.this, CommonUtilities.pref_UserId).equals("")) {
            params.put(CommonUtilities.key_user_id, "");
        } else {
            params.put(CommonUtilities.key_user_id, CommonUtilities.getPreference(RecipientActivity.this, CommonUtilities.pref_UserId));
        }

        String intentAmount = getIntent().getExtras().getString("amount");
        DecimalFormat df = new DecimalFormat("#.00");
        df.setRoundingMode(RoundingMode.HALF_EVEN);
        double pubCreditAmountDouble = Double.parseDouble(intentAmount);
        double pubCreditAmountBookingFeeDouble = pubCreditAmountDouble + 1.00;
        String finalAmount = df.format(pubCreditAmountDouble);
        String finalAmountBookingFee = df.format(pubCreditAmountBookingFeeDouble);

        params.put(CommonUtilities.key_sender_name, getIntent().getExtras().getString("name"));
        params.put(CommonUtilities.key_sender_email, getIntent().getExtras().getString("email"));
        params.put(CommonUtilities.key_amount, finalAmountBookingFee);
        params.put(CommonUtilities.key_rec_name, edtName.getText().toString());
        params.put(CommonUtilities.key_rec_cc_code, code);
        params.put(CommonUtilities.key_rec_mobile, edtMobile.getText().toString());
        params.put(CommonUtilities.key_rec_email, edtEmail.getText().toString());
        params.put(CommonUtilities.key_rec_comment, edtComment.getText().toString());
//        if (imgAttach.getVisibility() == View.VISIBLE) {
//            params.put(CommonUtilities.key_rec_photo, base64);
//        } else {
//            params.put(CommonUtilities.key_rec_photo, "");
//        }
        params.put(CommonUtilities.key_pub_id, getIntent().getExtras().getString(CommonUtilities.key_pub_id));

        Intent intent = new Intent(RecipientActivity.this, PayActivity.class);
        intent.putExtra(CommonUtilities.key_amount, finalAmount);
        intent.putExtra(CommonUtilities.key_amount_booking_fee, finalAmountBookingFee);
        intent.putExtra("currency",getIntent().getStringExtra("currency"));
        intent.putExtra("user_name", getIntent().getStringExtra("name"));
        intent.putExtra("user_email", getIntent().getStringExtra("email"));
        intent.putExtra("comment", edtComment.getText().toString());
        if (imgAttach.getVisibility() == View.VISIBLE) {
            intent.putExtra(CommonUtilities.key_rec_photo, imageUri);

        } else {
            intent.putExtra(CommonUtilities.key_rec_photo, "");
        }

        if (VideoPlaceHolderRecipient.getVisibility() == View.VISIBLE || !VideoUri.isEmpty()) {
            intent.putExtra(CommonUtilities.key_rec_video, VideoUri);

        } else {
            intent.putExtra(CommonUtilities.key_rec_video, "");
        }


        intent.putExtra(CommonUtilities.key_pub_id, params.get(CommonUtilities.key_pub_id));
        intent.putExtra(CommonUtilities.key_user_id, params.get(CommonUtilities.key_user_id));
        if(isFrom.equals("CheckIn")){
            intent.putExtra("recipient_name",getIntent().getStringExtra("recipient_name"));
            intent.putExtra("recipient_email",getIntent().getStringExtra("recipient_email"));
        }else{
            intent.putExtra("recipient_name",edtName.getText().toString());
            intent.putExtra("recipient_email",edtEmail.getText().toString());
        }

        Log.d("RecipientActivity", "params: " + params.toString());
        Log.d("Video", "params: " + VideoUri);
        Log.d("imageUri", "params: " + imageUri);

        startActivity(intent);

//        Dialog gr8niteoutLoadingDialog = new Dialog(this);
//
//        String header = "Basic " + toBase64(BuildConfig.STRIPE_SECRET_KEY);
//
//        ServerAccess.getClientSecret(
//                header,
//                getIntent().getExtras().getString("amount"),
//                getIntent().getExtras().getString("currency"),
//                "card"
//        ).observe(this, responseBodyResource ->{
//            switch (responseBodyResource.getStatus()){
//                case SUCCESS:
//                    if(gr8niteoutLoadingDialog.isShowing()){
//                        gr8niteoutLoadingDialog.dismiss();
//                    }
//                    ClientSecretModel response = responseBodyResource.getData();
//                    if(response != null){
//                            String clientSecret = response.getClient_secret();
//                            Intent intent = new Intent(RecipientActivity.this, PayActivity.class);
//                            intent.putExtra(CommonUtilities.clientSecretIntentKey, clientSecret);
//                            intent.putExtra(CommonUtilities.key_amount, finalAmount);
//                            intent.putExtra(CommonUtilities.key_amount_booking_fee, finalAmountBookingFee);
//                            startActivity(intent);
//
//                    }else {
//                        CommonUtilities.alertdialog(RecipientActivity.this, "An error occurred. Please try again later.");
//                    }
//                    break;
//                case ERROR:
//                    CommonUtilities.alertdialog(RecipientActivity.this, responseBodyResource.getApiError().getMessage());
//                    if(gr8niteoutLoadingDialog.isShowing()){
//                        gr8niteoutLoadingDialog.dismiss();
//                    }
//                    break;
//                case LOADING:
//                    gr8niteoutLoadingDialog.show();
//                    break;
//            }
//        });

//        if(gr8niteoutLoadingDialog.isShowing()){
//            gr8niteoutLoadingDialog.dismiss();
//        }
//
//        Intent intent = new Intent(RecipientActivity.this, PayActivity.class);
//                            intent.putExtra(CommonUtilities.clientSecretIntentKey, "pi_3NcmSYHkN6vS2zhi10M8shkA_secret_mS10ciqCyeAtcP697BkHS3hXu");
//                            intent.putExtra(CommonUtilities.key_amount, finalAmount);
//                            intent.putExtra(CommonUtilities.key_amount_booking_fee, finalAmountBookingFee);
//                            startActivity(intent);



//        ServerAccess.buyPubCreditWithStripe(params).observe(this, responseBodyResource -> {
//            Log.d("responseBodyResource",responseBodyResource.getStatus().name());
//            Log.d("responseBodyResource",responseBodyResource.getStatus().toString());
//            switch (responseBodyResource.getStatus()){
//                case SUCCESS:
//                    if(gr8niteoutLoadingDialog.isShowing()){
//                        gr8niteoutLoadingDialog.dismiss();
//                    }
//                    BuyPubCreditResponseModel response = responseBodyResource.getData();
//                    if(response != null){
//                        if(response.getStatus().equals("success")){
//                            String clientSecret = response.getClientSecret();
//                            Log.d("RecipientActivity", "client_secret" + clientSecret);
//                            Intent intent = new Intent(RecipientActivity.this, PayActivity.class);
//                            intent.putExtra(CommonUtilities.clientSecretIntentKey, clientSecret);
//                            intent.putExtra(CommonUtilities.key_amount, finalAmount);
//                            intent.putExtra(CommonUtilities.key_amount_booking_fee, finalAmountBookingFee);
//                            startActivity(intent);
//                        }else {
//                            CommonUtilities.alertdialog(RecipientActivity.this, response.getError());
//                        }
//                    }else {
//                        CommonUtilities.alertdialog(RecipientActivity.this, "An error occurred. Please try again later.");
//                    }
//                    break;
//                case ERROR:
//                    Log.d("responseBodyResource",responseBodyResource.getApiError().getMessage());
//                    Log.d("responseBodyResource",responseBodyResource.getApiError().component2());
//                    Log.d("responseBodyResource",responseBodyResource.getApiError().toString());
//
//                    CommonUtilities.alertdialog(RecipientActivity.this, responseBodyResource.getApiError().getMessage());
//                    if(gr8niteoutLoadingDialog.isShowing()){
//                        gr8niteoutLoadingDialog.dismiss();
//                    }
//                    break;
//                case LOADING:
//                    gr8niteoutLoadingDialog.show();
//                    break;
//            }
//        });
    }

    public void checkRecipientEmail() {
        Map<String, String> paramsTemp = new HashMap<>();
        paramsTemp.put("email", edtEmail.getText().toString().trim());

        ServerAccess.getResponse(RecipientActivity.this, CommonUtilities.key_email_exist, paramsTemp, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                PubForgotPasswordModel pubForgotPasswordModel = new PubForgotPasswordModel().PubForgotPasswordModel(result);
                if (pubForgotPasswordModel != null) {
                    if(pubForgotPasswordModel.response.code.equals(CommonUtilities.key_success_code)){
                        sendPubCredit();
                    }else{
                        CommonUtilities.ShowToast(RecipientActivity.this, pubForgotPasswordModel.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(RecipientActivity.this, "Something went wrong!");
            }
        });
    }

    public String convert_img_base64(Bitmap bmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;

    }

    private void requestContacts() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 21);
    }

    private boolean checkContactsPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestCamera() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 22);
    }

    private boolean checkCamera() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 21:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new get_contact_list().execute();
                } else {

                }
                break;
            case 22:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captureImageInitialization();
                } else {

                }
                break;
        }
    }

    public void getContacts() {
        Contact contact;
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {

            while (cur.moveToNext()) {
                contact = new Contact();
                contact.name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                contact.uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                Cursor cur1 = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id}, null);
                while (cur1.moveToNext()) {
                    //to get the contact names
                    String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                    if (email != null && !email.equals(""))
                        contact.email = email;
                    else
                        contact.email = "";
                }
                cur1.close();

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        contact.number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    pCur.close();
                }
                listContacts.add(contact);
                listContacts1.add(contact);
            }
        }
    }

    public void AddPhoto(View view) {
        if (!checkCamera()) {
            requestCamera();
        } else {
            captureImageInitialization();
        }
    }

//    private void captureImageInitialization() {
//        items = new ArrayList<>();
//        items.add(getResources().getString(R.string.take_camera_photo));
//        items.add(getResources().getString(R.string.select_photo));
//        items.add(getResources().getString(R.string.take_camera_video));
//        items.add(getResources().getString(R.string.select_video));
//        items.add(getResources().getString(R.string.cancel_dialog));
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(RecipientActivity.this,
//                android.R.layout.select_dialog_item, items);
//        AlertDialog.Builder builder = new AlertDialog.Builder(RecipientActivity.this);
//        builder.setAdapter(adapter, (dialog, index) -> {
//            dialog.dismiss();
//
//            if (items.get(index).equals(getResources().getString(R.string.take_camera_photo))) {
//                // Capture photo from the camera
//                try {
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // Add fileUri if required
//                    startActivityForResult(intent, 100);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            } else if (items.get(index).equals(getResources().getString(R.string.select_photo))) {
//                // Select photo from the gallery
//                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                getIntent.setType("image/*");
//
//                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//                Intent chooserIntent = Intent.createChooser(getIntent, getResources().getString(R.string.select_image));
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
//
//                startActivityForResult(chooserIntent, 2000);
//
//            } else if (items.get(index).equals(getResources().getString(R.string.take_camera_video))) {
//                // Capture video from the camera
//                try {
//                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // Add fileUri if required
//                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60); // Set duration limit (in seconds)
//                    startActivityForResult(intent, 300);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            } else if (items.get(index).equals(getResources().getString(R.string.select_video))) {
//                // Select video from the gallery
//                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                getIntent.setType("video/*");
//
//                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//
//                Intent chooserIntent = Intent.createChooser(getIntent, getResources().getString(R.string.select_video));
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
//
//                startActivityForResult(chooserIntent, 400);
//
//            } else if (items.get(index).equals(getResources().getString(R.string.cancel_dialog))) {
//                // Cancel the dialog
//                dialog.dismiss();
//            }
//        });
//        dialogPic = builder.create();
//        dialogPic.show();
//    }


    private void captureImageInitialization() {

        items = new ArrayList<String>();
        items.add(getResources().getString(R.string.take_camera));
        items.add(getResources().getString(R.string.select_photo));

        items.add(getResources().getString(R.string.cancel_dialog));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RecipientActivity.this,
                android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(RecipientActivity.this);
        builder.setAdapter(adapter, (dialog, index) -> {
            // pick from camera
            dialog.dismiss();
//            if (items.get(index).equals(getResources().getString(R.string.take_camera))) {
//                try {
//                    // Check if the device has a camera
//                    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
//                        File mediaFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "photo.jpg");
//                        Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", mediaFile);
//
//                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//
//                        intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
//                        intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
//                        intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
//
//                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//
//                        startActivityForResult(intent, 100);
//                    } else {
//                        Toast.makeText(this, "No front camera available on this device", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    Log.d("Exception", "requestCode: " +  e.getMessage());
//
//                    e.printStackTrace();
//                    Toast.makeText(this, "Failed to open camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }

            if (items.get(index).equals(getResources().getString(R.string.take_camera))) {
                try{
//                    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                    intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
//                    intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
//                    intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
//                    intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
//                    startActivityForResult(intent, 100);


                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Start the activity with camera_intent, and request pic id
                    startActivityForResult(camera_intent, 100);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if (items.get(index).equals(getResources().getString(R.string.select_photo))) {
                // Intent for selecting photo or video
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("*/*"); // Allow all types
                getIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"}); // Restrict to image and video

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("*/*");
                pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});

                // Combine both intents in a chooser
                Intent chooserIntent = Intent.createChooser(getIntent, "Select Media");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                startActivityForResult(chooserIntent, 2000);
            }


//                if (items.get(index).equals(getResources().getString(R.string.select_photo))) {
//                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                getIntent.setType("image/*");
//
//                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
////                pickIntent.setType("image/*");
//
//                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
//
//                startActivityForResult(chooserIntent, 2000);
//            }

            else if (items.get(index).equals(getResources().getString(R.string.cancel_dialog))) {
                dialog.dismiss();
            } else {
                dialog.dismiss();
            }
        });
        dialogPic = builder.create();
        dialogPic.show();
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*ink
     * returning image / video
     */
    private File getOutputMediaFile(int type) {
//        File mediaStorageDir = new File(
//                Environment
//                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                IMAGE_DIRECTORY_NAME);
//
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
//                        + IMAGE_DIRECTORY_NAME + " directory");
//                return null;
//            }
//        }

        File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
//        mediaFile = new File(mediaStorageDir.getPath() + File.separator
//                + "IMG_" + timeStamp + ".jpg");

        mediaFile = new File(picturesDirectory + File.separator
                + "IMG_" + timeStamp + ".jpg");


        return mediaFile;
    }

    public void OnContinue(View view) {
        String number;
        if (edtName.getText().toString().equals("")) {
            CommonUtilities.showSnackbar(edtName, "Please enter recipient's name");
        } else if (edtEmail.getText().toString().equals("")) {
            CommonUtilities.showSnackbar(edtName, "Please enter recipient's email");
        }
//        else if (edtCCode.getText().toString().equals("")) {
//            CommonUtilities.showSnackbar(edtName, "Please enter recipient's country code");
//        } else if (edtMobile.getText().toString().equals("")) {
//            CommonUtilities.showSnackbar(edtMobile, "Please enter recipient's mobile no");
//        }
        else if (!edtEmail.getText().toString().equals("") && !CommonUtilities.emailValidator(edtEmail.getText().toString())) {
            CommonUtilities.showSnackbar(edtMobile, "Please enter valid email address");
        }
        else {
//            if (edtMobile.getText().toString().startsWith("+00"))
//                number = edtMobile.getText().toString().substring(3);
//            else if (edtMobile.getText().toString().startsWith("+"))
//                number = edtMobile.getText().toString().substring(1);
//            else if (edtMobile.getText().toString().startsWith("00"))
//                number = edtMobile.getText().toString().substring(2);
//            else if (edtMobile.getText().toString().startsWith("0"))
//                number = edtMobile.getText().toString().substring(1);
//            else
//                number = edtMobile.getText().toString();
//
//            if (code.equals("1") && number.startsWith("1"))
//                number = number.substring(1);
//            else if (code.equals("44") && number.startsWith("44"))
//                number = number.substring(2);
//            else if (code.toString().equals("353") && number.startsWith("353"))
//                number = number.substring(3);
//            else {
//                if (number.startsWith(code)) {
//                    number = number.substring(code.length());
//                }
//            }

//            if (!isFrom.equalsIgnoreCase("CheckIn")) {
//                final Dialog dialogView = new Dialog(this);
//                dialogView.setContentView(R.layout.custom_number_alert);
//                TextView txtTitle = (TextView) dialogView.findViewById(R.id.txtTitle);
//                TextView txtMessage = (TextView) dialogView.findViewById(R.id.txtMessage);
//                final EditText edtNumber = (EditText) dialogView.findViewById(R.id.edtNumber);
//                Button btnOK = (Button) dialogView.findViewById(R.id.btnOK);
//                Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
//                txtTitle.setText("Mobile Number Verification");
//                txtMessage.setText("Please verify recipient’s mobile number");
////            if(number.contains("(") || number.contains(")") || number.contains("-"))
////                number = number.replaceAll("\\(|\\)|\\-","");
////            if(number.contains(" "))
////                number = number.replace(" ","");
//
//                number = number.replaceAll("\\D+", "");
//                //*#+,./
//                edtNumber.setText(number);
//
//                btnOK.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        // TODO Auto-generated method stub
//                        String no = "";
//                        if (!edtNumber.getText().toString().equals("")) {
//                            edtMobile.setText(edtNumber.getText().toString().replaceAll("\\D+", ""));
//                            sendPubCredit();
//                            dialogView.dismiss();
//                        } else {
//                            CommonUtilities.showSnackbar(edtMobile, "Please enter recipient's mobile no");
//                        }
//                    }
//                });
//                btnCancel.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        // TODO Auto-generated method stub
//                        dialogView.dismiss();
//                    }
//                });
//                dialogView.show();
//            } else {
            if(isFrom.equals("CheckIn")){
                sendPubCredit();
            }else{
                checkRecipientEmail();
            }
//            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_setting).setVisible(false);
        menu.findItem(R.id.action_search_white).setVisible(false);
        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_reset).setVisible(false);
        menu.findItem(R.id.action_transaction_history).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        Log.d("onActivityResult", "requestCode: " + requestCode);
//        Log.d("onActivityResult", "data: " + data);
//        Log.d("onActivityResult", "data: " + data.getExtras().get("data"));

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                code = data.getStringExtra("code");
                edtCCode.setText("+" + data.getStringExtra("code") + "(" + data.getStringExtra("name") + ")");
                edtMobile.requestFocus();
            }
        }
        if (requestCode == 2000 && resultCode == RESULT_OK) {
            displaySelectedMedia(data);
        }

        Log.d("onActivityRequestCode", "onActivityResultRequestCode: " + requestCode);

        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                onCaptureImageResult(data);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(),
                                "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

//    public void displaySelectedImage(Intent data) {
//        try {
//            imageCancel.setVisibility(View.VISIBLE);
//            imgAttach.setVisibility(View.VISIBLE);
//            VideoPlaceHolderRecipient.setVisibility(View.VISIBLE);
//
//            Uri pickedImage = data.getData();
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pickedImage);
//
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
//            byte[] byteArray = stream.toByteArray();
//
//            base64 = convert_img_base64(bitmap);
//            imageUri = String.valueOf(pickedImage);
//
//            Glide.with(RecipientActivity.this)
//                    .load(byteArray) // Uri of the picture
//                    .transform(new RoundedTransformation(RecipientActivity.this, 15, 0, RoundedTransformation.CornerType.TOP))
//                    .into(imgAttach);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    public void displaySelectedMedia(Intent data) {
        try {

            Uri pickedMedia = data.getData();
            String mimeType = getContentResolver().getType(pickedMedia);

            if (mimeType != null) {
                if (mimeType.startsWith("image/")) {
                    // Handle image selection
                    // Handle image selection
                    imgAttach.setVisibility(View.VISIBLE); // Show image view
                    InputStream inputStream = getContentResolver().openInputStream(pickedMedia);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    // Correct rotation based on EXIF data
                    Bitmap correctedBitmap = correctImageRotation(pickedMedia, bitmap);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    correctedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    byte[] byteArray = stream.toByteArray();

                    base64 = convert_img_base64(correctedBitmap);
                    imageUri = String.valueOf(pickedMedia);
                    imageCancel.setVisibility(View.VISIBLE);

                    Glide.with(RecipientActivity.this)
                            .load(byteArray) // Byte array of the corrected image
                            .transform(new RoundedTransformation(RecipientActivity.this, 15, 0, RoundedTransformation.CornerType.TOP))
                            .into(imgAttach);

                } else if (mimeType.startsWith("video/")) {
                    // Handle video selection
                    VideoPlaceHolderRecipient.setVisibility(View.VISIBLE); // Show video placeholder
                    imageCancel.setVisibility(View.VISIBLE);
                    VideoUri = String.valueOf(pickedMedia);
                    VideoPlaceHolderRecipient.setVideoURI(pickedMedia);
                    VideoPlaceHolderRecipient.setMediaController(new MediaController(this)); // Add media controls
                    VideoPlaceHolderRecipient.requestFocus();
                    VideoPlaceHolderRecipient.start(); // Start playing video
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap correctImageRotation(Uri imageUri, Bitmap bitmap) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            ExifInterface exifInterface = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exifInterface = new ExifInterface(inputStream);
            }

            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Matrix matrix = new Matrix();

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    // No rotation required
                    return bitmap;
            }

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            return bitmap;
        }
    }


//    working code
//    public void displaySelectedMedia(Intent data) {
//        try {
//
//            Uri pickedMedia = data.getData();
//            String mimeType = getContentResolver().getType(pickedMedia);
//
//            if (mimeType != null) {
//                if (mimeType.startsWith("image/")) {
//                    // Handle image selection
//                    imgAttach.setVisibility(View.VISIBLE); // Show image view
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pickedMedia);
//
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
//                    byte[] byteArray = stream.toByteArray();
//
//                    base64 = convert_img_base64(bitmap);
//                    imageUri = String.valueOf(pickedMedia);
//                    imageCancel.setVisibility(View.VISIBLE);
//
//                    Glide.with(RecipientActivity.this)
//                            .load(byteArray) // Uri of the picture
//                            .transform(new RoundedTransformation(RecipientActivity.this, 15, 0, RoundedTransformation.CornerType.TOP))
//                            .into(imgAttach);
//
//                } else if (mimeType.startsWith("video/")) {
//                    // Handle video selection
//                    VideoPlaceHolderRecipient.setVisibility(View.VISIBLE); // Show video placeholder
//                    imageCancel.setVisibility(View.VISIBLE);
//                    VideoUri = String.valueOf(pickedMedia);
//                    VideoPlaceHolderRecipient.setVideoURI(pickedMedia);
//                    VideoPlaceHolderRecipient.setMediaController(new MediaController(this)); // Add media controls
//                    VideoPlaceHolderRecipient.requestFocus();
//                    VideoPlaceHolderRecipient.start(); // Start playing video
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    public String compressImage(String path) {

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = decodeFile(path, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = decodeFile(path, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(path);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG,50, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }

    public String getFilename() {
//        File file = new File(Environment.getExternalStorageDirectory().getPath(), "Gr8niteout");
//        if (!file.exists()) {
//            file.mkdirs();
//        }
        String uriSting = getCacheDir().getPath() + "/" + System.currentTimeMillis() + ".jpg";
        return uriSting;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private void onCaptureImageResult(Intent data) {


        try {
            imageCancel.setVisibility(View.VISIBLE);
            imgAttach.setVisibility(View.VISIBLE);


            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

//            imgAttach.setImageBitmap(imageBitmap);

//            base64 = convert_img_base64(imageBitmap);
            imageUri = saveBitmapToFile(imageBitmap); // URI from Intent data
            Glide.with(RecipientActivity.this)
                    .load(imageUri) // Uri of the picture
                    .transform(new RoundedTransformation(RecipientActivity.this, 15, 0, RoundedTransformation.CornerType.TOP))
                    .into(imgAttach);



            Log.d("saveBitmapToFile", "path: " +  saveBitmapToFile(imageBitmap));



        }catch (Exception e){
            e.printStackTrace();

            Log.d("onCaptureImageResultException", "requestError: " + e);

        }
    }

    private String saveBitmapToFile(Bitmap bitmap) {
        String filePath = null;
        try {
            // Define the file where the bitmap will be saved
            File mediaFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "captured_image.jpg");

            // Write the bitmap to the file
            FileOutputStream outputStream = new FileOutputStream(mediaFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            // Get the file path as a string
            filePath = mediaFile.getAbsolutePath();
            Log.d("saveBitmapToFile", "Image saved to: " + filePath);
        } catch (Exception e) {
            Log.e("saveBitmapToFile", "Error saving bitmap: " + e.getMessage());
            e.printStackTrace();
        }
        return filePath;
    }


//    public void displayImage(int code) {
//        imageCancel.setVisibility(View.VISIBLE);
//        imgAttach.setVisibility(View.VISIBLE);
//        String path;
//        if (code == 100)
//            path = compressImage(fileUri.getPath());
//        else
//            path = compressImage(images);
//        Log.e("path", path);
//        Bitmap myBitmap = decodeFile(path);
//        base64 = convert_img_base64(myBitmap);
//        Glide.with(RecipientActivity.this)
//                .load(new File(path)) // Uri of the picture
//                .bitmapTransform(new RoundedTransformation(RecipientActivity.this, 15, 0, RoundedTransformation.CornerType.TOP))
//                .listener(new RequestListener<File, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        return false;
//                    }
//                })
//                .into(imgAttach);
//    }

    public class ContactsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listContacts.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            listContacts.clear();
            if (charText.length() == 0) {
                listContacts.addAll(listContacts1);
                layoutContacts.setVisibility(View.VISIBLE);
                ContactsList.setVisibility(View.GONE);
                btnContinue.setTextColor(Color.WHITE);
                btnContinue.setEnabled(true);
            } else {
                for (int i = 0; i < listContacts1.size(); i++) {
                    if (listContacts1.get(i).name != null && !listContacts1.get(i).name.equals("") && listContacts1.get(i).number != null && !listContacts1.get(i).number.equals("") && listContacts1.get(i).name.toLowerCase(Locale.getDefault()).contains(charText)) {
                        Contact wp = new Contact();
                        wp.name = listContacts1.get(i).name;
                        wp.number = listContacts1.get(i).number;
                        wp.email = listContacts1.get(i).email;
                        wp.uri = listContacts1.get(i).uri;
                        listContacts.add(wp);
                    }
                }
                layoutContacts.setVisibility(View.GONE);
                ContactsList.setVisibility(View.VISIBLE);
                btnContinue.setTextColor(Color.parseColor("#778899"));
                btnContinue.setEnabled(false);
            }
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.contacts_row, null);
            }

            TextView txtName = (TextView) convertView.findViewById(R.id.txtName);
            TextView txtNumber = (TextView) convertView.findViewById(R.id.txtNumber);
            TextView textnum = (TextView) convertView.findViewById(R.id.textnum);
            ImageView imgContact = (ImageView) convertView.findViewById(R.id.imgContact);


            txtName.setText(listContacts.get(position).name);
            txtNumber.setText(listContacts.get(position).number);
            try {
                if (listContacts.get(position).uri != null && !listContacts.get(position).uri.equals("null")) {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(listContacts.get(position).uri));
                    textnum.setVisibility(View.GONE);
                    imgContact.setImageBitmap(bitmap);
                } else {
                    imgContact.setImageResource(R.mipmap.no_user);
                    textnum.setVisibility(View.VISIBLE);
                    textnum.setText(listContacts.get(position).name.substring(0, 1));
                }
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
                e.printStackTrace();
            }

            return convertView;
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




