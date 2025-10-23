package com.Gr8niteout.ActivityFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import com.Gr8niteout.BuildConfig;
import com.Gr8niteout.MainActivity;
import com.Gr8niteout.R;
import com.Gr8niteout.config.CircleTransform;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.MyApplication;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.BirthdayModel;
import com.Gr8niteout.model.SignUpModel;
import com.Gr8niteout.model.UserLoginResponse;
import com.Gr8niteout.signup.SignupLogin;
import com.google.gson.Gson;
import com.google.android.gms.analytics.HitBuilders;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Birthdays extends Fragment {
    MainActivity mActivity;
    TextView textStatic_notlogin, textStatic_login;
    ImageView share_btn;
    ImageView btn_login;
    SignUpModel signUpModel;
    UserLoginResponse userLoginModel;
    BirthdayModel model;
    String userid;
    public static RecyclerView recyclerView;
    RelativeLayout main_layout;
    LinearLayout not_login_layout;
    RecyclerView.LayoutManager layoutManager;
    RecycleAdapter recyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_birthday, container, false);
        textStatic_notlogin = (TextView) v.findViewById(R.id.textStatic_notlogin);
        main_layout = (RelativeLayout) v.findViewById(R.id.main_layout);
        btn_login = (ImageView) v.findViewById(R.id.btn_login);
        share_btn = (ImageView) v.findViewById(R.id.share_btn);
        not_login_layout = (LinearLayout) v.findViewById(R.id.not_login_layout);
        textStatic_login = (TextView) v.findViewById(R.id.textStatic_login);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        CommonUtilities.setPreference(mActivity, CommonUtilities.pref_from_birthday, "false");

        // Safely parse user data with null checks
        String userDataString = CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserData);
        if (userDataString != null && !userDataString.isEmpty()) {
            try {
                // Try to parse as SignUpModel first (for Facebook login)
                signUpModel = new SignUpModel().SignUpModel(userDataString);
                if (signUpModel == null) {
                    // If SignUpModel parsing fails, try UserLoginResponse (for email/password login)
                    Gson gson = new Gson();
                    userLoginModel = gson.fromJson(userDataString, UserLoginResponse.class);
                }
            } catch (Exception e) {
                // Handle parsing errors gracefully
                signUpModel = null;
                userLoginModel = null;
            }
        } else {
            signUpModel = null;
            userLoginModel = null;
        }
        //userid = CommonUtilities.getPreference(mActivity,CommonUtilities.pref_UserId);

        if (!CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId).equals("")) {
            main_layout.setVisibility(View.VISIBLE);
            get_birthdays();
        } else {
            main_layout.setVisibility(View.GONE);
            not_login_layout.setVisibility(View.VISIBLE);
            textStatic_notlogin.setVisibility(View.VISIBLE);
            share_btn.setVisibility(View.GONE);
            textStatic_login.setVisibility(View.GONE);
            btn_login.setVisibility(View.VISIBLE);

        }

        recyclerViewAdapter = new RecycleAdapter();
        setHasOptionsMenu(true);

        CommonUtilities.setFontFamily(mActivity, textStatic_notlogin, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, textStatic_login, CommonUtilities.AvenirLTStd_Medium);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity, SignupLogin.class);
                i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_birtday);
//                startActivityForResult(i, 222);
                startActivity(i);
            }
        });

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Gr8niteout!!");
                    i.putExtra(Intent.EXTRA_TEXT, url);
                    startActivity(Intent.createChooser(i, "Share link!"));
                } catch (Exception e) {
                    //e.toString();
                }
            }
        });

        return v;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 222 && resultCode == RESULT_OK) {
//            MainActivity.act.setDrawerItem();
//            MainActivity.act.mDrawerList.setItemChecked(2,true);
//            MainActivity.act.mDrawerList.setSelection(2);
//            userid = CommonUtilities.getPreference(mActivity,CommonUtilities.pref_UserId);
//
//        }
//    }

    public void get_birthdays() {
        Map<String, String> params = new HashMap<String, String>();
//        params.put(CommonUtilities.key_user_id,"149");
        params.put(CommonUtilities.key_user_id, CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId));
        
        // Safely get fb_id from user data
        String fbId = "";
        if (signUpModel != null && signUpModel.response != null && signUpModel.response.user_data != null) {
            fbId = signUpModel.response.user_data.fb_id != null ? signUpModel.response.user_data.fb_id : "";
        } else if (userLoginModel != null && userLoginModel.response != null && userLoginModel.response.responseInfo != null && userLoginModel.response.responseInfo.data != null) {
            fbId = userLoginModel.response.responseInfo.data.facebook_id != null ? userLoginModel.response.responseInfo.data.facebook_id : "";
        }
        params.put("fb_id", fbId);
//        params.put("fb_id","769356289804290");
        ServerAccess.getResponse(mActivity, CommonUtilities.key_get_friends_birthdays, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                model = new BirthdayModel().BirthdayModel(result);
                if (model != null) {
                    if (model.response.status.equals(CommonUtilities.key_Success)) {
                        CommonUtilities.setPreference(mActivity, CommonUtilities.pref_birthdays, result);
                        recyclerView.setAdapter(recyclerViewAdapter);
                    } else {
                        main_layout.setVisibility(View.GONE);
                        textStatic_login.setVisibility(View.VISIBLE);
                        not_login_layout.setVisibility(View.VISIBLE);
                        share_btn.setVisibility(View.VISIBLE);
                        textStatic_notlogin.setVisibility(View.GONE);
                        btn_login.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.birthday_list_row, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            if (position % 2 == 0) {
                holder.listcolor.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.listodd));
            } else {
                holder.listcolor.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));
            }
            holder.txtName.setText(model.response.friends_birthdays.get(position).name);
            holder.daysAgo.setText(model.response.friends_birthdays.get(position).days_ago);

            if (model.response.friends_birthdays.get(position).photo.equals("")) {
                holder.image_layout.setBackgroundResource(R.drawable.round);
                if (!model.response.friends_birthdays.get(position).name.equals("")) {
                    holder.firstLett.setText(model.response.friends_birthdays.get(position).name.substring(0, 1));
                } else {
                    holder.image_layout.setBackgroundResource(R.mipmap.user);
                }
            } else {
//                changed on 28-jan-2019
                Picasso.get()
                        .load(model.response.friends_birthdays.get(position).photo)
                        .error(R.mipmap.user).placeholder(R.mipmap.user)
                        .transform(new CircleTransform())
                        .into(holder.userImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });

            }

            CommonUtilities.setFontFamily(mActivity, holder.txtName, CommonUtilities.AvenirNextLTPro_Demi);
            CommonUtilities.setFontFamily(mActivity, holder.daysAgo, CommonUtilities.AvenirLTStd_Medium);

        }

        @Override
        public int getItemCount() {
            return model.response.friends_birthdays.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView userImage;
            private TextView firstLett;
            private TextView txtName;
            private TextView txtStatic;
            private TextView daysAgo;
            private RelativeLayout image_layout;
            private LinearLayout listcolor;

            public ViewHolder(View view) {
                super(view);
                listcolor = (LinearLayout) view.findViewById(R.id.listcolor);
                userImage = (ImageView) view.findViewById(R.id.user_image);
                image_layout = (RelativeLayout) view.findViewById(R.id.image_layout);
                firstLett = (TextView) view.findViewById(R.id.First_Lett);
                txtName = (TextView) view.findViewById(R.id.txtName);
                txtStatic = (TextView) view.findViewById(R.id.txtStatic);
                daysAgo = (TextView) view.findViewById(R.id.days_ago);
            }

        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

//        menu.findItem(R.id.action_logout).setVisible(true);
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);

        menu.findItem(R.id.action_share).setVisible(true);
        menu.findItem(R.id.action_setting).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_reset).setVisible(false);
        menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_search_white).setVisible(false);
        menu.findItem(R.id.action_transaction_history).setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_share) {
            String url = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Gr8niteout!!");
                i.putExtra(Intent.EXTRA_TEXT, url);
                startActivity(Intent.createChooser(i, "Share link!"));
            } catch (Exception e) {
                //e.toString();
            }
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();

        ((MyApplication) mActivity.getApplication()).getDefaultTracker().setScreenName("Birthday Listing Screen");
        ((MyApplication) mActivity.getApplication()).getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }
}
