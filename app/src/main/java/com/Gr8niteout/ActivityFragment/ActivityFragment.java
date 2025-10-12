package com.Gr8niteout.ActivityFragment;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
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

public class ActivityFragment extends Fragment {

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    MainActivity mActivity;
    String getArgument;

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


}



