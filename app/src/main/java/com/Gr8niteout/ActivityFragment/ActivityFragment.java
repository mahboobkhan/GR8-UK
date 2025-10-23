package com.Gr8niteout.ActivityFragment;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Gr8niteout.MainActivity;
import com.Gr8niteout.R;
import com.Gr8niteout.adapter.ViewPagerAdapter;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.SlidingTabLayout;
import com.Gr8niteout.model.SignUpModel;
import com.Gr8niteout.model.UserLoginResponse;
import com.google.gson.Gson;

public class ActivityFragment extends Fragment {

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    MainActivity mActivity;
    String getArgument;
    SignUpModel model;
    UserLoginResponse userLoginModel;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v ;
        v = inflater.inflate(R.layout.fragment_activity, container, false);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {
            v = inflater.inflate(R.layout.fragment_activity_nougat, container, false);
        }
        mActivity.getSupportActionBar().setTitle(null);
        mActivity.getSupportActionBar().setIcon(null);
        (mActivity).goImage.setVisibility(View.GONE);
        mActivity.tool_text.setText("Activity");
        CommonUtilities.setFontFamily(mActivity,mActivity.tool_text , CommonUtilities.AvenirLTStd_Medium);
        mActivity.isExit = true;

       // getArgument = getArguments().getString(CommonUtilities.key_flag);


        return v;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));

            if(CommonUtilities.getPreference(mActivity,CommonUtilities.pref_from_birthday).equals("true")) {
                mViewPager.setCurrentItem(1);
            }else{
                mViewPager.setCurrentItem(0);
            }


        setUpPager(view);
        setUpTabColor();
        handleLoggedInState();
    }

    void setUpPager(View view) {

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

    }

    void setUpTabColor() {
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                // TODO Auto-generated method stub
                return ActivityFragment.this.getResources().getColor(R.color.white);
            }

            @Override
            public int getDividerColor(int position) {
                // TODO Auto-generated method stub
                return ActivityFragment.this.getResources().getColor(R.color.ActionBarColor);
            }
        });
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }

    private void handleLoggedInState() {
        // Check if user is logged in and handle accordingly
        String userId = CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId);
        Log.d("ActivityFragment", "User ID from preferences: " + userId);
        
        // If user ID is empty, try to extract it from user data
        if (userId.equals("")) {
            if (model != null && model.response != null && model.response.user_data != null) {
                userId = model.response.user_data.user_id;
                Log.d("ActivityFragment", "Extracted User ID from SignUpModel: " + userId);
            } else if (userLoginModel != null && userLoginModel.response != null && userLoginModel.response.responseInfo != null && userLoginModel.response.responseInfo.data != null) {
                userId = userLoginModel.response.responseInfo.data.user_id;
                Log.d("ActivityFragment", "Extracted User ID from UserLoginResponse: " + userId);
            }
        }
        
        if (!userId.equals("")) {
            // User is logged in - parse user data safely
            String userDataString = CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserData);
            if (userDataString != null && !userDataString.isEmpty()) {
                try {
                    // Try to parse as SignUpModel first (for Facebook login)
                    model = new SignUpModel().SignUpModel(userDataString);
                    if (model != null && model.response != null && model.response.user_data != null) {
                        // User data is available - you can use it for Activity-specific features
                        Log.d("ActivityFragment", "User logged in (SignUpModel): " + model.response.user_data.fname);
                        // Add any Activity-specific logic here based on user data
                    } else {
                        // Try UserLoginResponse (for email/password login)
                        Gson gson = new Gson();
                        userLoginModel = gson.fromJson(userDataString, UserLoginResponse.class);
                        if (userLoginModel != null && userLoginModel.response != null && userLoginModel.response.responseInfo != null && userLoginModel.response.responseInfo.data != null) {
                            Log.d("ActivityFragment", "User logged in (UserLoginResponse): " + userLoginModel.response.responseInfo.data.end_first_name);
                            // Add any Activity-specific logic here based on user data
                        } else {
                            Log.d("ActivityFragment", "User logged in but both models are null");
                        }
                    }
                } catch (Exception e) {
                    Log.e("ActivityFragment", "Error parsing user data: " + e.getMessage());
                }
            } else {
                Log.d("ActivityFragment", "User logged in but no user data available");
            }
        } else {
            // User is not logged in
            Log.d("ActivityFragment", "User not logged in");
        }
    }

}



