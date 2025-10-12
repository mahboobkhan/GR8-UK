package com.Gr8niteout.pub;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.SlidingTabLayout;
import com.Gr8niteout.model.HomeData;

import butterknife.ButterKnife;

public class PubActivity extends AppCompatActivity {
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    String pub_id, pub_name;
    HomeData model;
    TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setContentView(R.layout.activity_pub_nougat);
        }
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setContentInsetStartWithNavigation(0);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        CommonUtilities.setFontFamily(PubActivity.this, txtTitle, CommonUtilities.AvenirLTStd_Medium);

        if (getIntent().getExtras() != null) {
            pub_id = getIntent().getExtras().getString(CommonUtilities.key_pub_id);
            pub_name = getIntent().getExtras().getString(CommonUtilities.key_pub_name);
            getSupportActionBar().setTitle(null);
            txtTitle.setText(pub_name);
        }

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);

        mSlidingTabLayout.setDistributeEvenly(false);
        mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(0);
        setUpTabColor();
    }

    void setUpTabColor() {
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {

                return PubActivity.this.getResources().getColor(R.color.white);
            }

            @Override
            public int getDividerColor(int position) {

                return PubActivity.this.getResources().getColor(R.color.white);
            }
        });
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        public String Titles[] = {"Details", "Photos", "What's on", "Check-Ins"};

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putString(CommonUtilities.key_pub_id, pub_id);
            if (position == 0) {
                Fragment fragment = new PubDetailFragment();
                fragment.setArguments(bundle);
                return fragment;
            } else if (position == 1) {
//                Fragment fragment = new PhotosFragment();
                Fragment fragment = new MultiPhotosFragment();
                bundle.putString(CommonUtilities.key_flag, getIntent().getExtras().getString(CommonUtilities.key_flag));
                bundle.putString(CommonUtilities.key_pub_name, getIntent().getExtras().getString(CommonUtilities.key_pub_name));
                fragment.setArguments(bundle);
                return fragment;
            } else if (position == 2) {
                Fragment fragment = new WhatsonFragment();
                fragment.setArguments(bundle);
                return fragment;
            } else if (position == 3) {
                Fragment fragment = new CheckInFragment();
                fragment.setArguments(bundle);
                return fragment;
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Titles[position];
        }

        @Override
        public int getCount() {
            return 4;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_reset).setVisible(false);
        menu.findItem(R.id.action_share).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_search_white).setVisible(false);
        menu.findItem(R.id.action_setting).setVisible(false);
        menu.findItem(R.id.action_transaction_history).setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_share) {
            String url = CommonUtilities.getPreference(PubActivity.this, CommonUtilities.pref_pub_url);
            Intent share = new Intent();
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, url);
            share.setAction(Intent.ACTION_SEND);
            startActivity(Intent.createChooser(share, "Share link!"));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                return true;
//            case R.id.action_share:
//                String url = CommonUtilities.getPreference(PubActivity.this, CommonUtilities.pref_pub_url);
//                Intent share = new Intent();
//                share.setType("text/plain");
//                share.putExtra(Intent.EXTRA_TEXT, url);
//                share.setAction(Intent.ACTION_SEND);
//                startActivity(Intent.createChooser(share, "Share link!"));
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//
//        }
//    }
}

