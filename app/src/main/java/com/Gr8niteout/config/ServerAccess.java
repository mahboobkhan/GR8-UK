package com.Gr8niteout.config;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;

import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.Gr8niteout.BuildConfig;
import com.Gr8niteout.R;
import com.Gr8niteout.model.BuyPubCreditResponseModel;
import com.Gr8niteout.model.ClientSecretModel;
import com.Gr8niteout.model.Logout_Model;
import com.Gr8niteout.model.User_Status;
import com.Gr8niteout.services.BuyPubCredit;
import com.Gr8niteout.services.GetClientSecret;
import com.Gr8niteout.signup.SignupLogin;
import com.Gr8niteout.utils.NetworkCall;
import com.Gr8niteout.utils.Resource;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.Gr8niteout.config.CommonUtilities.Gr8niteoutURL;
import static com.Gr8niteout.config.CommonUtilities.WebServiceURL;
import static com.Gr8niteout.config.CommonUtilities.getDeviceId;

public class ServerAccess {

    public static void getResponse(final Context context, String method, final Map<String, String> params, boolean progress, final VolleyCallback callback) {
        final Dialog dialog;
        dialog = new Dialog(context);

        if (progress) {
            if (!dialog.isShowing()) {
                dialog.setCancelable(false);
                dialog.show();
            }
        }

        Log.d("response>>",method);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebServiceURL + method,
                response -> {
                    Log.e("response>>>", "response: " + response);
                    if (dialog.isShowing())
                        dialog.dismiss();
                    if (response.startsWith("{"))
                    {
                        if (response.contains("ERR003")) {

                            User_Status model = new User_Status().User_Status(response);
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                            builder1.setTitle(R.string.app_name);
                            builder1.setIcon(R.mipmap.app_icon);
                            builder1.setMessage(model.response.msg);
                            builder1.setCancelable(false);
                            builder1.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog1, int id) {
                                    LoginManager.getInstance().logOut();
                                    CommonUtilities.RemoveALlPreference(context);
                                    if (CommonUtilities.getPreference(context, CommonUtilities.pref_UserId).equals("")) {
                                        Intent intent = new Intent(context, SignupLogin.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);
                                        ((Activity) context).finish();
                                    } else {
                                        logout(context);
                                    }
                                }
                            });
                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        } else
                            callback.onSuccess(response.toString());
                    } else {
                        callback.onError("error");
                        CommonUtilities.alertdialog(context, "Something went wrong. Please try again");
                    }
                },
                error -> {
                    Log.e("error>>>", "error : " + error.toString());
                    dialog.dismiss();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setTitle(R.string.app_name);
                    builder1.setIcon(R.mipmap.app_icon);
                    builder1.setMessage("Something went wrong. Please try again");
                    builder1.setCancelable(false);
                    builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog12, int id) {
                            dialog12.dismiss();
                        }
                    });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                    callback.onError("Internet Error");//internet not available
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                params.put(CommonUtilities.key_udid, getDeviceId(context));
                params.put(CommonUtilities.key_security_toekn, CommonUtilities.getSecurity_Preference(context, CommonUtilities.key_security_toekn));
                params.put(CommonUtilities.key_device_type, "2");
                params.put(CommonUtilities.key_app_version, BuildConfig.VERSION_NAME);
                params.put(CommonUtilities.key_pushnotification_toekn, CommonUtilities.getPreference(context, CommonUtilities.pref_push_token));
//                Log.e("params>>>", params.toString());
                return params;
            }


        };
        call_webService(context, stringRequest, dialog, callback);
    }

    public static void getAPIResponse(final Context context, String method, boolean progress, final VolleyCallback callback) {
        final Dialog dialog;
        dialog = new Dialog(context);

        if (progress) {
            if (!dialog.isShowing()) {
                dialog.setCancelable(false);
                dialog.show();
            }
        }

        Log.d("response>>", method);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, method,
                response -> {
                    Log.e("response>>>", "response: " + response);
                    if (dialog.isShowing())
                        dialog.dismiss();
                    if (response.startsWith("{")) {
                        if (response.contains("ERR003")) {
                            User_Status model = new User_Status().User_Status(response);
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                            builder1.setTitle(R.string.app_name);
                            builder1.setIcon(R.mipmap.app_icon);
                            builder1.setMessage(model.response.msg);
                            builder1.setCancelable(false);
                            builder1.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog1, int id) {
                                    LoginManager.getInstance().logOut();
                                    CommonUtilities.RemoveALlPreference(context);
                                    if (CommonUtilities.getPreference(context, CommonUtilities.pref_UserId).equals("")) {
                                        Intent intent = new Intent(context, SignupLogin.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);
                                        ((Activity) context).finish();
                                    } else {
                                        logout(context);
                                    }
                                }
                            });
                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        } else {
                            callback.onSuccess(response.toString());
                        }
                    } else {
                        callback.onError("error");
                        CommonUtilities.alertdialog(context, "Something went wrong. Please try again");
                    }
                },
                error -> {
                    Log.e("error>>>", "error : " + error.toString());
                    dialog.dismiss();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setTitle(R.string.app_name);
                    builder1.setIcon(R.mipmap.app_icon);
                    builder1.setMessage("Something went wrong. Please try again");
                    builder1.setCancelable(false);
                    builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog12, int id) {
                            dialog12.dismiss();
                        }
                    });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    callback.onError("Internet Error");
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put(CommonUtilities.key_udid, getDeviceId(context));
                headers.put(CommonUtilities.key_security_toekn, CommonUtilities.getSecurity_Preference(context, CommonUtilities.key_security_toekn));
                headers.put(CommonUtilities.key_device_type, "2");
                headers.put(CommonUtilities.key_app_version, BuildConfig.VERSION_NAME);
                headers.put(CommonUtilities.key_pushnotification_toekn, CommonUtilities.getPreference(context, CommonUtilities.pref_push_token));
                return headers;
            }
        };
        call_webService(context, stringRequest, dialog, callback);
    }

    public static void logout(final Context context) {

        Map<String, String> params = new HashMap<String, String>();
        params.put(CommonUtilities.key_user_id, CommonUtilities.getPreference(context, CommonUtilities.pref_UserId));
        ServerAccess.getResponse(context, "logout", params, true, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Logout_Model logmodel = new Logout_Model().Logout_Model(result);
                if (logmodel != null) {
                    if (logmodel.response.status.equals(CommonUtilities.key_Success)) {

                        if (logmodel.response.user_logout.token != null)
                            CommonUtilities.setSecurity_Preference(context, CommonUtilities.key_security_toekn, logmodel.response.user_logout.token);

                        LoginManager.getInstance().logOut();
                        CommonUtilities.RemoveALlPreference(context);
                        Intent intent = new Intent(context, SignupLogin.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    } else {
                        CommonUtilities.ShowToast(context, logmodel.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private static RequestQueue requestQueue;

    public static void call_webService(final Context context, final StringRequest stringRequest, final Dialog dialog, final VolleyCallback callback) {
        Log.d("FacebookLoginServer",stringRequest.toString());
        int socketTimeout = 20000;//20 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        if (CommonUtilities.isConnectingToInternet(context)) {
            requestQueue = Volley.newRequestQueue(context);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } else {
            if (dialog.isShowing())
                dialog.dismiss();
            final Dialog int_dialog = new Dialog(context);
            int_dialog.setContentView(R.layout.no_internet_img);
            Button text = (Button) int_dialog.findViewById(R.id.btn_ok);
            TextView txt = (TextView) int_dialog.findViewById(R.id.text);
            // if button is clicked, close the custom dialog
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(1000); //You can manage the blinking time with this parameter
            anim.setStartOffset(100);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            txt.startAnimation(anim);

            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int_dialog.dismiss();
                    call_webService(context, stringRequest, dialog, callback);
                }
            });

            int_dialog.show();
        }
    }

    public interface VolleyCallback {
        void onSuccess(String result);

        void onError(String error);

    }

    public static LiveData<Resource<BuyPubCreditResponseModel>> buyPubCreditWithStripe(Map<String, String> params){
        NetworkCall<BuyPubCreditResponseModel> networkCall = new NetworkCall<>();
        return networkCall.makeCall(createBuyPubCreditService()
                .buyPubCredit(
                        params.get(CommonUtilities.key_pub_id),
                        params.get(CommonUtilities.key_amount),
                        params.get(CommonUtilities.key_rec_photo),
                        "",
                        params.get(CommonUtilities.key_sender_email),
                        params.get(CommonUtilities.key_sender_name),
                        "",
                        "",
                        "",
                        params.get(CommonUtilities.key_rec_name),
                        params.get(CommonUtilities.key_rec_email),
                        params.get(CommonUtilities.key_rec_mobile),
                        params.get(CommonUtilities.key_rec_cc_code),
                        params.get(CommonUtilities.key_rec_comment)
                        ));
    }

    public static LiveData<Resource<ClientSecretModel>> getClientSecret(
            String header,
            String amount,
            String currency,
            String paymentMethod
    ){
        NetworkCall<ClientSecretModel> networkCall = new NetworkCall<>();

        return networkCall.makeCall(createClientSecretService()
                .getClientSecret(
                        header,
                        amount,
                        currency,
                        paymentMethod
                ));
    }

    private static BuyPubCredit createBuyPubCreditService(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://www.gr8niteout.com/")
                .baseUrl(Gr8niteoutURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        return retrofit.create(BuyPubCredit.class);
    }

    private static GetClientSecret createClientSecretService(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.stripe.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        return retrofit.create(GetClientSecret.class);
    }

}















