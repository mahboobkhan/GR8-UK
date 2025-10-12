package com.Gr8niteout.config;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Gr8niteout.R;
import com.Gr8niteout.signup.PinEntryEditText;

import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtilities {

    public static final String clientSecretIntentKey = "clientSecretIntentKey";

//    public static final String Gr8niteoutURL = "http://gr8niteout.demowork.com/";   //Demowork
//        public static final String Gr8niteoutURL = "https://www.gr8niteout.com/";    //Live
        public static final String Gr8niteoutURL = "https://gr8niteout.co.uk/";    //Live last
//        public static final String Gr8niteoutURL = "https://gr8nit2.microlent.com/api/";    //Live
    public static final String WebServiceURL = Gr8niteoutURL + "webservice.php?request=";

    public static final String User_Profile_URL = "assets/uploads/user-profile/";
//    let GET_SUBSCRIPTION_STATUS = "https://gr8niteout.probrains.co/webservice_subs.php?request=get_user_subscription_by_email&email="

    public static final String GET_SUBSCRIPTION_STATUS = Gr8niteoutURL+ "/webservice_subs.php?request=get_user_subscription_by_email&email=";



    public static final String Home_page_URL = "assets/uploads/pub-profile/home_page/";
    public static final String Pub_page_URL = "assets/uploads/pub-profile/home_page/";
    public static final String Pub_photos_URL = "assets/uploads/pub-photos/";
    public static final String Pub_Thumb_URL = "assets/uploads/pub-profile/profile_pic/";
    public static final String Pub_attach_image_URL = "assets/uploads/pubcredit/";
    public static final String Pub_Banner_URL = "assets/uploads/pub-profile/banner/";
    public static final String Home_Banner_URL = "assets/uploads/mobile-banners/";
    public static final String Whats_On_URL = "assets/uploads/pub_notice/";
    public static final String Features_URL = "assets/feature/";

    public static final String key_upload_photos = "upload_photos";

    public static final String key_generate_token = "generate_token";
    public static final String key_get_verification_code = "get_verification_code";
    public static final String key_user_verification = "user_verification";
    public static final String key_get_drink_list = "get_drink_list";
    public static final String key_get_photos = "get_photos";
    public static final String key_home_data = "get_home_data";
    public static final String key_pub_profile = "user_get_pub_profile";
    public static final String key_signup_service = "sign_up";
    public static final String key_Subscription_Service = "user_subscription";
    public static final String key_countries = "get_countries";
    public static final String key_countries_states = "get_country_states";
    public static final String key_find_pub = "find_pub";
    public static final String key_setting_url = "get_settings";
    public static final String key_edit_profile = "edit_profile";
    public static final String key_chcek_in = "checkin_user";//checkin
    public static final String key_get_checked_in_user = "get_checked_in_user";//checkin
    public static final String key_get_whatson = "get_whatson";
    public static final String key_get_comments = "get_comments";
    public static final String key_post_comment = "post_comment";
    public static final String key_encoded_key = "e_key";
    public static final String key_timezone = "timezone";
    public static final String key_app_version = "app_ver";
    public static final String key_udid = "udid";
    public static final String key_security_toekn = "s_token";
    public static final String key_pushnotification_toekn = "p_token";
    public static final String key_device_type = "d_type";
    public static final String flag_birtday = "birtday";
    public static final String flag_received_credit = "birtday";
    public static final String key_Success = "Success";
    public static final String key_latitude = "lattitude";
    public static final String key_longitude = "longitude";
    public static final String key_email = "email";
    public static final String key_page_no = "page_no";
    public static final String key_user_id = "user_id";
    public static final String key_pub_id = "pub_id";
    public static final String key_redeem_code = "redeem_code";
    public static final String key_pub_credit_id = "pub_credit_id";
    public static final String key_send_credit = "send_pub_credit";
    public static final String key_features = "get_features";
    public static final String key_filter_count = "get_filter_count";
    public static final String key_filter_pub = "get_filter_pubs";
    public static final String key_img_id = "image_id";
    public static final String key_comment = "comment";
    public static final String key_check_reedem_code = "check_reedem_code";
    public static final String key_checkin_data = "check_in_data";
    public static final String key_pub_registration = "pub_registration";
    public static final String key_uk_states = "country_state";
    public static final String key_apply_coupan = "apply_coupan";
    public static final String key_pub_login = "pub_login";
    public static final String key_user_email_check = "user_email_check";
    public static final String key_pub_forgot_password = "forgot_password";

    public static final String key_stripe_user = "stripe_check";

    public static final String key_stripe_account_add = "stripe_account_add";

    public static final String key_fetch_pub_user_credits = "fetch_pub_user_credits";

    public static final String key_email_exist = "EmailExist";

    //Pub Dshboard Api's
    public static final String key_success_code = "SUC001";
    public static final String key_error_code = "ERR001";
    public static final String key_summary = "get_pub_summary";
    public static final String key_add_notice = "postnotice";
    public static final String key_get_account_details = "edit_pub_profile";
    public static final String key_update_account_details = "update_pub_profile";
    public static final String key_get_premises = "edit_pub_premises";
    public static final String key_update_premises = "update_pub_premises";
    public static final String key_get_pub_profile = "get_pub_profile";
    public static final String key_update_pub_profile = "update_pub";
    public static final String key_change_password = "change_password";
    public static final String key_get_notification = "notification_list";
    public static final String key_get_notice_board_list = "notice_list";
    public static final String key_edit_notice = "update_pub_notice";
    public static final String key_notice_action = "update_notice_flag";

    public static boolean isBack = false;
    public static boolean isBack_credit = false;

    public static final String key_pub_name = "pub_Name";

    public static final String key_mobile = "mobile";
    public static final String key_cc_code = "cc_code";
    public static final String key_country_id = "country_id";
    public static final String key_state_id = "state_id";
    public static final String key_city = "city";
    public static final String key_pub_event_name = "pub_event_name";
    public static final String key_flag = "flag";
    public static final String flag_drinks = "drinks";
    public static final String flag_my_profile = "my_profile";
    public static final String flag_profile = "profile";
    public static final String flag_comment = "comment";
    public static final String flag_menu = "menu";

    public static final String pref_Countries = "countries";
    public static final String pref_img_id = "img_id";
    public static final String pref_pub_id = "pub_id";
    public static final String pref_Notification_Access = "false";
    public static final String pref_Contact_Access = "contact";
    public static final String pref_setting_urls = "setting_urls";
    public static final String pref_pub_url = "pub_url";
    public static final String pref_pub_detail = "pub_detail";
    public static final String pref_Comments = "comments";
    public static final String pref_whats_on = "whats_on";
    public static final String pref_birthdays = "birthdays";
    public static final String pref_fb_id = "fb_id";
    public static final String pref_fb_access_token = "access_token";
    public static final String pref_fb_fname = "fname";
    public static final String pref_fb_lname = "lname";
    public static final String pref_fb_email = "email";
    public static final String pref_fb_birthday = "birthdate";
    public static final String pref_fb_mobile = "mobile";
    public static final String pref_fb_gender = "gender";
    public static final String pref_fb_photo = "photo";
    public static final String pref_Howtouseapp = "howtouseapp";
    public static final String pref_UserData = "userdata";
    public static final String pref_UserId = "userid";
    public static final String pref_otp = "123456";
    public static final String pref_homedata = "homedata";
    public static final String pref_drinks = "drinks";
    public static final String pref_drinks_detail = "drinkdetails";
    public static final String key_get_friends_birthdays = "get_friends_birthdays";
    public static final String pref_login_skip = "skipedLogin";
    public static final String pref_allow_Access = "AllowAccess";
    public static final String pref_from_birthday = "pref_from_birthday";
    public static final String pref_term_url = "term_url";
    public static final String pref_privacy_url = "privacy_url";
    public static final String pref_push_token = "push_token";
//    public static final boolean pref_from_birthday = false;

    public static final String key_sender_name = "sender_name";
    public static final String key_sender_email = "sender_email";
    public static final String key_amount = "amount";
    public static final String key_amount_booking_fee = "amount_booking_fee";
    public static final String key_rec_name = "rec_name";
    public static final String key_rec_cc_code = "rec_cc_code";
    public static final String key_rec_email = "rec_email";
    public static final String key_rec_mobile = "rec_mobile";
    public static final String key_rec_comment = "rec_comment";
    public static final String key_share_url = "share_url";
    public static final String key_share_text = "share_text";
    public static final String key_rec_photo = "rec_photo";
    public static final String key_rec_video = "rec_video";
    public static final String key_feature_id = "feature_id";
    public static final String key_sort_by = "sort_by";
    public static final String key_day = "day";
    public static final String key_open_time = "open_time";
    public static final String key_close_time = "close_time";
    public static final String key_pub_image = "pub_Image";
    public static final String key_country_code = "country_code";
    public static final String key_country_name = "country_name";
    //fonts

    public static String Avenir = "Avenir.ttc";
    public static String Avenir_Heavy = "Avenir_Heavy.otf";
    public static String Avenir_Roman = "Avenir_Roman.otf";
    public static String Avenir_Roman_Font_Download = "Avenir_Roman_Font_Download.otf";
    public static String AvenirLTStd_Black = "AvenirLTStd_Black.otf";
    public static String AvenirLTStd_Medium = "AvenirLTStd_Medium.otf";
    public static String AvenirNextLTPro = "AvenirNextLTPro.otf";
    public static String AvenirNextLTPro_Bold = "AvenirNextLTPro_Bold.otf";
    public static String AvenirNextLTPro_BoldCn = "AvenirNextLTPro_BoldCn.otf";
    public static String AvenirNextLTPro_BoldCnIt = "AvenirNextLTPro_BoldCnIt.otf";
    public static String AvenirNextLTPro_Cn = "AvenirNextLTPro_Cn.otf";
    public static String AvenirNextLTPro_CnIt = "AvenirNextLTPro_CnIt.otf";
    public static String AvenirNextLTPro_Demi = "AvenirNextLTPro_Demi.otf";
    public static String AvenirNextLTPro_DemiCn = "AvenirNextLTPro_DemiCn.otf";
    public static String AvenirNextLTPro_DemiCnIt = "AvenirNextLTPro_DemiCnIt.otf";
    public static String AvenirNextLTPro_DemiIt = "AvenirNextLTPro_DemiIt.otf";
    public static String AvenirNextLTPro_HeavyCn = "AvenirNextLTPro_HeavyCn.otf";
    public static String AvenirNextLTPro_HeavyCnIt = "AvenirNextLTPro_HeavyCnIt.otf";
    public static String AvenirNextLTPro_It = "AvenirNextLTPro_It.otf";
    public static String AvenirNextLTPro_MediumCn = "AvenirNextLTPro_MediumCn.otf";
    public static String AvenirNextLTPro_MediumCnIt = "AvenirNextLTPro_MediumCnIt.otf";
    public static String AvenirNextLTPro_Regular = "AvenirNextLTPro_Regular.otf";
    public static String AvenirNextLTPro_UltLtCn = "AvenirNextLTPro_UltLtCn.otf";
    public static String AvenirNextLTPro_UltLtCnIt = "AvenirNextLTPro_UltLtCnIt.otf";

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Security_PREFERENCES = "Security_Prefs";
    public static SharedPreferences sharedpreferences;
    public static SharedPreferences security_sharedpreferences;
    public static NumberFormat nf = NumberFormat.getInstance();

    public static void setPreference(Context context, String key, String value) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(key, value);
        editor.commit();
    }

    public static String getPreference(Context context, String key) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        return sharedpreferences.getString(key, "");
    }

    public static void setSecurity_Preference(Context context, String key, String value) {
        security_sharedpreferences = context.getSharedPreferences(Security_PREFERENCES,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = security_sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getSecurity_Preference(Context context, String key) {
        security_sharedpreferences = context.getSharedPreferences(Security_PREFERENCES,
                Context.MODE_PRIVATE);
        return security_sharedpreferences.getString(key, "");
    }

    public static void setBooleanPreference(Context context, String key, Boolean flag) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, flag);
        editor.commit();
    }

    public static boolean getBooleanPreference(Context context, String key) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);

        return sharedpreferences.getBoolean(key, false);
    }

    public static void setSecurityBooleanPreference(Context context, String key, Boolean flag) {
        security_sharedpreferences = context.getSharedPreferences(Security_PREFERENCES,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = security_sharedpreferences.edit();
        editor.putBoolean(key, flag);
        editor.commit();
    }

    public static boolean getSecurityBooleanPreference(Context context, String key) {
        security_sharedpreferences = context.getSharedPreferences(Security_PREFERENCES,
                Context.MODE_PRIVATE);

        return security_sharedpreferences.getBoolean(key, false);
    }

    public static void RemovePreference(Context context, String key) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    public static void RemoveALlPreference(Context context) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.clear();
        editor.commit();
    }

    public static void showSnackbar(View view, String msg) {
        Snackbar snackbar = Snackbar
                .make(view, msg, Snackbar.LENGTH_LONG);
        view = snackbar.getView();
        view.setBackgroundColor(Color.parseColor("#0053A8"));
        snackbar.show();
    }

    public static boolean isConnectingToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    public static String getDeviceId(Context mContext) {
        String DeviceId = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return DeviceId;
    }

    public static void alertdialog(Context context, String msg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(R.string.app_name);
        builder1.setIcon(R.mipmap.app_icon);
        builder1.setMessage(msg);
        builder1.setCancelable(false);
        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
        Button bq = alert11.getButton(DialogInterface.BUTTON_NEGATIVE);
        bq.setTextColor(Color.parseColor("#0053A8"));
    }

    public static boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void setAsteric(Context c, String simple, TextView txt) {
        String colored = "*";
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(simple);
        int start = builder.length();
        builder.append(colored);
        int end = builder.length();

        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#eb1111")), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        txt.setTextSize(16);
        Typeface typeface = Typeface
                .createFromAsset(c.getAssets(), "fonts/" + "AvenirLTStd_Medium.otf");
        txt.setTypeface(typeface);
        txt.setText(builder);
    }

    public static void ShowToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void setFontFamily(Context c, TextView txt, String font) {
        Typeface typeface = Typeface
                .createFromAsset(c.getAssets(), "fonts/" + font);
        txt.setTypeface(typeface);
    }

    public static void setFontFamily(Context c, TextInputLayout txt, String font) {
        Typeface typeface = Typeface
                .createFromAsset(c.getAssets(), "fonts/" + font);
        txt.setTypeface(typeface);
    }

    public static void setFontFamily(Context c, Button txt, String font) {
        Typeface typeface = Typeface
                .createFromAsset(c.getAssets(), "fonts/" + font);
        txt.setTypeface(typeface);
    }

    public static void setFontFamily(Context c, EditText txt, String font) {
        Typeface typeface = Typeface
                .createFromAsset(c.getAssets(), "fonts/" + font);
        txt.setTypeface(typeface);
    }

    public static void setFontFamily(Context c, PinEntryEditText txt, String font) {
        Typeface typeface = Typeface
                .createFromAsset(c.getAssets(), "fonts/" + font);
        txt.setTypeface(typeface);
    }

    public static void setPermission(Context context, String[] permission) {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = permission;
        if (!hasPermissions(context, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void hideSoftKeyboard(Activity act, EditText edt) {
        InputMethodManager im = (InputMethodManager) act
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }

    public static void showSoftKeyboard(Activity act, EditText edt) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                edt.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }

    public static String doRound(Double d) {
        nf.setMaximumFractionDigits(1); // set decimal places
        return nf.format(d);
    }

    public static void hideKeyboard(boolean val, Activity activity) {
        View view;
        view = activity.getWindow().getCurrentFocus();
        if (val) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(view != null) inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void hideKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
//        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
