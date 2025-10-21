package com.Gr8niteout.myprofile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Gr8niteout.MainActivity;
import com.Gr8niteout.R;
import com.Gr8niteout.config.CircleTransform;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.MyApplication;
import com.Gr8niteout.model.SignUpModel;
import com.Gr8niteout.signup.SignupLogin;
import com.google.android.gms.analytics.HitBuilders;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MyProfileFragment extends Fragment implements View.OnClickListener {

    private ImageView imgProfile;
    private TextView userName;
    private TextView mobileNo;
    private TextView edit_prof;
    private TextView email;
    private TextView First_Lett, not_login_txt;
    private LinearLayout notLoginLayout;
    private LinearLayout loggedIn;
    private LinearLayout email_layout;
    private LinearLayout line;
    private RelativeLayout image_layout;
    String userid;
    SignUpModel model;
    MainActivity mActivity;
    private ImageView login;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_profile, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActivity.isExit = true;
        mActivity.getSupportActionBar().setTitle(null);
        (mActivity).goImage.setVisibility(View.GONE);
        mActivity.getSupportActionBar().setIcon(null);
        mActivity.tool_text.setText("My Profile");
        CommonUtilities.setFontFamily(mActivity, mActivity.tool_text, CommonUtilities.AvenirLTStd_Medium);
        login = (ImageView) view.findViewById(R.id.login);
        imgProfile = (ImageView) view.findViewById(R.id.imgProfile);
        userName = (TextView) view.findViewById(R.id.user_name);
        First_Lett = (TextView) view.findViewById(R.id.First_Lett);
        not_login_txt = (TextView) view.findViewById(R.id.not_login_txt);
        mobileNo = (TextView) view.findViewById(R.id.mobile_no);
        edit_prof = (TextView) view.findViewById(R.id.edit_prof);
        email = (TextView) view.findViewById(R.id.email);
        loggedIn = (LinearLayout) view.findViewById(R.id.logged_in);
        image_layout = (RelativeLayout) view.findViewById(R.id.image_layout);
        email_layout = (LinearLayout) view.findViewById(R.id.email_layout);
        line = (LinearLayout) view.findViewById(R.id.line);
        notLoginLayout = (LinearLayout) view.findViewById(R.id.not_login_layout);

        // Safely parse user data with null checks
        String userDataString = CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserData);
        if (userDataString != null && !userDataString.isEmpty()) {
            try {
                model = new SignUpModel().SignUpModel(userDataString);
            } catch (Exception e) {
                Log.e("MyProfileFragment", "Error parsing user data: " + e.getMessage());
                model = null;
            }
        } else {
            model = null;
        }

        setFont();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);

        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_setting).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_reset).setVisible(false);
        menu.findItem(R.id.action_search_white).setVisible(false);
        menu.findItem(R.id.action_transaction_history).setVisible(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        noinspection SimplifiableIfStatement
        if (id == R.id.action_setting) {
            mActivity.selectItem(4);
            mActivity.mDrawerList.setSelection(4);
            mActivity.mDrawerList.setItemChecked(4, true);
            return true;
        } else if(id == R.id.action_transaction_history){
            mActivity.selectItem(7);
            mActivity.mDrawerList.setSelection(-1);
            mActivity.mDrawerList.setItemChecked(-1, true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

//            case R.id.login :
//                Intent i = new Intent(mActivity,SignupLogin.class);
//                startActivity(i);
//                mActivity.finish();
//                break;
//
//            case R.id.edit_prof :
//                Intent act = new Intent(mActivity,EditProfileActivity.class);
//                startActivity(act);
//                break;

            default:
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.isExit = true;
        String sd = CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserData);
        // Safely parse user data with null checks
        String userDataString = CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserData);
        if (userDataString != null && !userDataString.isEmpty()) {
            try {
                model = new SignUpModel().SignUpModel(userDataString);
            } catch (Exception e) {
                Log.e("MyProfileFragment", "Error parsing user data in onResume: " + e.getMessage());
                model = null;
            }
        } else {
            model = null;
        }

        if (!CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId).equals("") && model != null && model.response != null && model.response.user_data != null) {
            loggedIn.setVisibility(View.VISIBLE);
            notLoginLayout.setVisibility(View.GONE);
            userid = model.response.user_data.user_id;

//            mobileNo.setText("+"+model.response.user_data.getCc_code() + model.response.user_data.getMobile());

            if (!model.response.user_data.mobile.equals("")) {

                if (model.response.user_data.mobile.length() > 6) {
                    String main = model.response.user_data.getMobile();
                    String firstthree = main.substring(0, 3);
                    String secthree = main.substring(3, 6);
                    String lastfour = main.substring(6, model.response.user_data.mobile.length());
                    mobileNo.setText("+" + model.response.user_data.getCc_code() + " " + firstthree + " " + secthree + " " + lastfour);
                } else
                    mobileNo.setText("+" + model.response.user_data.getCc_code() + " " + model.response.user_data.mobile);
            }

            userName.setText(model.response.user_data.fname + " " + model.response.user_data.lname);

            if (model.response.user_data.getEmail().equals("")) {
                email_layout.setVisibility(View.GONE);
                line.setVisibility(View.GONE);
            }
            email.setText(model.response.user_data.email);

//            Log.e("Pic name",model.response.user_data.photo);
            if (model.response.user_data.photo.equals("")) {
                // image_layout.setBackgroundResource(R.drawable.round);
                imgProfile.setImageResource(R.mipmap.no_user);
                First_Lett.setText(model.response.user_data.getFname().substring(0, 1));
            } else {
                First_Lett.setText("");
//                changed on 28-jan-2019
                Picasso.get()
                        .load(CommonUtilities.Gr8niteoutURL + CommonUtilities.User_Profile_URL +
                                model.response.user_data.photo)
//                            "")
                        .error(R.mipmap.user)
                        .transform(new CircleTransform())
                        .placeholder(R.mipmap.user) // optional
                        .into(imgProfile, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
            }
            edit_prof.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent act = new Intent(mActivity, EditProfileActivity.class);
                    act.putExtra("test", "test");
                    startActivity(act);
                }
            });
            mActivity.setDrawerItem();
            mActivity.mDrawerList.setItemChecked(3, true);
            mActivity.mDrawerList.setSelection(3);
        } else if (CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId).equals("")) {
            loggedIn.setVisibility(View.GONE);
            notLoginLayout.setVisibility(View.VISIBLE);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mActivity, SignupLogin.class);
                    i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_my_profile);
                    startActivity(i);
                }
            });
        }
    }

    public void setFont() {
        CommonUtilities.setFontFamily(mActivity, userName, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(mActivity, edit_prof, CommonUtilities.Avenir); //Avenir (OT1) : Regular
        CommonUtilities.setFontFamily(mActivity, mobileNo, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(mActivity, email, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(mActivity, not_login_txt, CommonUtilities.AvenirLTStd_Medium);
    }

    @Override
    public void onStart() {
        super.onStart();

        ((MyApplication) mActivity.getApplication()).getDefaultTracker().setScreenName("My Profile Screen");
        ((MyApplication) mActivity.getApplication()).getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }
}
