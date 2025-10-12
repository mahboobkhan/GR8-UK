package com.Gr8niteout.setting;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Gr8niteout.MainActivity;
import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.MyApplication;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.SettingUrlModel;
import com.google.android.gms.analytics.HitBuilders;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;


public class SettingFragment extends Fragment implements View.OnClickListener {

    TextView txtAboutus;
    TextView txtTerms;
    TextView txtSendFeed;
    TextView txtFaq;
    TextView txtRateApp;
    TextView txtHowWork;
    TextView txtContact;
    LinearLayout notifLayout;
    TextView txtNptif;
    TextView txtNptifOn;
    ImageView img_notif;
    TextView txtGen;
    TextView txtAbout;

    View v;
    Intent sendUrl;
    String howto_url, cont_url, about_url, faq_url, term_url, feedbackmail;
    LinearLayout AboutLayout, HowLayout, FaqLayout, TermLayout, ContactLayout, RateAppLayout, SendFeedLayout,layout;
    SettingUrlModel model;
    MainActivity mActivity;
    boolean isOn = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.setting, container, false);

        layout = (LinearLayout) v.findViewById(R.id.layout);
        layout.setVisibility(View.GONE);
        getSettingUrl();
        mActivity.isExit = true;
        mActivity.getSupportActionBar().setTitle(null);
        mActivity.tool_text.setText("Settings");
        (mActivity).goImage.setVisibility(View.GONE);
        CommonUtilities.setFontFamily(mActivity,mActivity.tool_text , CommonUtilities.AvenirLTStd_Medium);
        mActivity.getSupportActionBar().setIcon(null);

        notifLayout = (LinearLayout) v.findViewById(R.id.NotifLayout);

        img_notif = (ImageView) v.findViewById(R.id.img_notif);
        txtNptif = (TextView) v.findViewById(R.id.txtNptif);
        txtNptifOn = (TextView) v.findViewById(R.id.txtNptifOn);
        txtContact = (TextView) v.findViewById(R.id.txtContact);
        txtRateApp = (TextView) v.findViewById(R.id.txtRateApp);
        txtHowWork = (TextView) v.findViewById(R.id.txtHowWork);
        txtFaq = (TextView) v.findViewById(R.id.txtFaq);
        txtGen = (TextView) v.findViewById(R.id.txtGen);
        txtAbout = (TextView) v.findViewById(R.id.txtAbout);
        txtAboutus = (TextView) v.findViewById(R.id.txtAboutus);
        txtTerms = (TextView) v.findViewById(R.id.txtTerms);
        txtSendFeed = (TextView) v.findViewById(R.id.txtSendFeed);
        HowLayout = (LinearLayout) v.findViewById(R.id.HowLayout);
        SendFeedLayout = (LinearLayout) v.findViewById(R.id.SendFeedLayout);
        RateAppLayout = (LinearLayout) v.findViewById(R.id.RateAppLayout);
        AboutLayout = (LinearLayout) v.findViewById(R.id.AboutLayout);
        ContactLayout = (LinearLayout) v.findViewById(R.id.ContactLayout);
        TermLayout = (LinearLayout) v.findViewById(R.id.TermLayout);
        FaqLayout = (LinearLayout) v.findViewById(R.id.FaqLayout);
        FaqLayout.setOnClickListener(this);
        ContactLayout.setOnClickListener(this);
        TermLayout.setOnClickListener(this);
        SendFeedLayout.setOnClickListener(this);
        HowLayout.setOnClickListener(this);
        AboutLayout.setOnClickListener(this);
        RateAppLayout.setOnClickListener(this);

        setFont();

        if (CommonUtilities.getSecurityBooleanPreference(mActivity, CommonUtilities.pref_Notification_Access)) {
            img_notif.setImageResource(R.mipmap.on);
            isOn = true;
            txtNptifOn.setText("Notifications are on");
        } else {
            isOn = false;
            txtNptifOn.setText("Notifications are off");
        }
        img_notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOn) {
                    img_notif.setImageResource(R.mipmap.off);
                    isOn = false;
                    txtNptifOn.setText("Notifications are off");
                    CommonUtilities.setSecurityBooleanPreference(mActivity, CommonUtilities.pref_Notification_Access, false);
                } else {
                    img_notif.setImageResource(R.mipmap.on);
                    isOn = true;
                    FirebaseMessaging.getInstance().subscribeToTopic("news");
                    // [END subscribe_topics]
                    // Log and toast
                    txtNptifOn.setText("Notifications are on");
                    CommonUtilities.setSecurityBooleanPreference(mActivity, CommonUtilities.pref_Notification_Access, true);
                }
            }
        });

        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }

    public void getSettingUrl()
    {

        Map<String, String> params = new HashMap<String, String>();
        ServerAccess.getResponse(mActivity, CommonUtilities.key_setting_url, params,true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                model =  new SettingUrlModel().SettingUrlModel(result);
                if (model != null) {
                    if (model.response.status.equals(CommonUtilities.key_Success)) {
                        layout.setVisibility(View.VISIBLE);
                        CommonUtilities.setSecurity_Preference(mActivity, CommonUtilities.pref_setting_urls,result);
                        String c = model.response.settings_data.get(0).title;
                        Log.i("Log title", c);

                        txtHowWork.setText(model.response.settings_data.get(1).title);
                        txtRateApp.setText(model.response.settings_data.get(2).title);
                        txtContact.setText(model.response.settings_data.get(3).title);
                        txtAboutus.setText(model.response.settings_data.get(6).title);
                        txtFaq.setText(model.response.settings_data.get(7).title);
                        txtSendFeed.setText(model.response.settings_data.get(8).title);
                        txtTerms.setText(model.response.settings_data.get(9).title);

                        howto_url = model.response.settings_data.get(1).url;
                        cont_url = model.response.settings_data.get(3).url;
                        about_url = model.response.settings_data.get(6).url;
                        faq_url = model.response.settings_data.get(7).url;
                        term_url = model.response.settings_data.get(9).url;
                        feedbackmail = model.response.settings_data.get(8).url;
                    }
                    else
                    {
                        CommonUtilities.ShowToast(mActivity,model.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.AboutLayout) {
            sendUrl = new Intent(mActivity, WebViewSetting.class);
            sendUrl.putExtra("ViewUrl", about_url);
            sendUrl.putExtra("title", model.response.settings_data.get(6).title);
            startActivity(sendUrl);
        } else if (id == R.id.HowLayout) {
            sendUrl = new Intent(mActivity, WebViewSetting.class);
            sendUrl.putExtra("ViewUrl", howto_url);
            sendUrl.putExtra("title", model.response.settings_data.get(1).title);
            startActivity(sendUrl);
        } else if (id == R.id.FaqLayout) {
            sendUrl = new Intent(mActivity, WebViewSetting.class);
            sendUrl.putExtra("ViewUrl", faq_url);
            sendUrl.putExtra("title", model.response.settings_data.get(7).title);
            startActivity(sendUrl);
        } else if (id == R.id.RateAppLayout) {
            final String appPackageName = mActivity.getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        } else if (id == R.id.SendFeedLayout) {
            Intent i = new Intent(Intent.ACTION_SENDTO);
            i.setData(Uri.parse("mailto:" + feedbackmail));
            i.putExtra(Intent.EXTRA_SUBJECT, "User Feedback");
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(mActivity, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.ContactLayout) {
            sendUrl = new Intent(mActivity, WebViewSetting.class);
            sendUrl.putExtra("ViewUrl", cont_url);
            sendUrl.putExtra("title", model.response.settings_data.get(3).title);
            startActivity(sendUrl);
        } else if (id == R.id.TermLayout) {
            sendUrl = new Intent(mActivity, WebViewSetting.class);
            sendUrl.putExtra("ViewUrl", term_url);
            sendUrl.putExtra("title", model.response.settings_data.get(9).title);
            startActivity(sendUrl);
        }
    }


//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.AboutLayout:
//                sendUrl = new Intent(mActivity, WebViewSetting.class);
//                sendUrl.putExtra("ViewUrl", about_url);
//                sendUrl.putExtra("title", model.response.settings_data.get(6).title);
//                startActivity(sendUrl);
//                break;
//            case R.id.HowLayout:
//                sendUrl = new Intent(mActivity, WebViewSetting.class);
//                sendUrl.putExtra("ViewUrl", howto_url);
//                sendUrl.putExtra("title", model.response.settings_data.get(1).title);
//                startActivity(sendUrl);
//                break;
//            case R.id.FaqLayout:
//                sendUrl = new Intent(mActivity, WebViewSetting.class);
//                sendUrl.putExtra("ViewUrl", faq_url);
//                sendUrl.putExtra("title", model.response.settings_data.get(7).title);
//                startActivity(sendUrl);
//                break;
//            case R.id.RateAppLayout:
//                final String appPackageName = mActivity.getPackageName(); // getPackageName() from Context or Activity object
//                try {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//                } catch (android.content.ActivityNotFoundException anfe) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//                }
//                break;
//            case R.id.SendFeedLayout:
//
//                Intent i = new Intent(Intent.ACTION_SENDTO);
//                i.setData(Uri.parse("mailto:" +feedbackmail));
//                i.putExtra(Intent.EXTRA_SUBJECT, "User Feedback");
//                startActivity(Intent.createChooser(i, "Send feedback"));
//                try {
//                    startActivity(Intent.createChooser(i, "Send mail..."));
//                } catch (android.content.ActivityNotFoundException ex) {
//                    Toast.makeText(mActivity, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case R.id.ContactLayout:
//                sendUrl = new Intent(mActivity, WebViewSetting.class);
//                sendUrl.putExtra("ViewUrl", cont_url);
//                sendUrl.putExtra("title", model.response.settings_data.get(3).title);
//                startActivity(sendUrl);
//                break;
//            case R.id.TermLayout:
//                sendUrl = new Intent(mActivity, WebViewSetting.class);
//                sendUrl.putExtra("ViewUrl", term_url);
//                sendUrl.putExtra("title", model.response.settings_data.get(9).title);
//                startActivity(sendUrl);
//                break;
//
//        }
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);

        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_setting).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_reset).setVisible(false);
        menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_search_white).setVisible(false);
        menu.findItem(R.id.action_transaction_history).setVisible(false);
    }

    public void setFont() {
        CommonUtilities.setFontFamily(mActivity, txtGen, CommonUtilities.AvenirNextLTPro_Demi);
        CommonUtilities.setFontFamily(mActivity, txtHowWork, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, txtRateApp, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, txtContact, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, txtAbout, CommonUtilities.AvenirNextLTPro_Demi);
        CommonUtilities.setFontFamily(mActivity, txtAboutus, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, txtFaq, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, txtSendFeed, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, txtTerms, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, txtNptif, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, txtNptifOn, CommonUtilities.AvenirLTStd_Medium);
    }

    @Override
    public void onResume() {
        super.onResume();
        (mActivity).goImage.setVisibility(View.GONE);
        mActivity.isExit = true;
    }

    @Override
    public void onStart() {
        super.onStart();

        ((MyApplication)mActivity.getApplication()).getDefaultTracker().setScreenName("Settings Screen");
        ((MyApplication)mActivity.getApplication()).getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }
}