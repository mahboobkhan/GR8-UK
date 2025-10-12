package com.Gr8niteout.PubDashboardScreens;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.Gr8niteout.MainActivity;
import com.Gr8niteout.PubDashboardScreens.PubFragments.AccountDetailsFragment;
import com.Gr8niteout.PubDashboardScreens.PubFragments.ChangePasswordFragment;
import com.Gr8niteout.PubDashboardScreens.PubFragments.DashboardFragment;
import com.Gr8niteout.PubDashboardScreens.PubFragments.NoticeBoardFragment;
import com.Gr8niteout.PubDashboardScreens.PubFragments.NotificationFragment;
import com.Gr8niteout.PubDashboardScreens.PubFragments.PremisesFragment;
import com.Gr8niteout.PubDashboardScreens.PubFragments.PubProfileFragment;
import com.Gr8niteout.PubDashboardScreens.PubFragments.TransactionHistoryFragment;
import com.Gr8niteout.R;
import com.Gr8niteout.Subscription.MultipleSubscriptionFragment;
import com.Gr8niteout.adapter.PubDashboardNavDrawerAdapter;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.signup.PubLoginActivity;
import com.Gr8niteout.signup.SignupLogin;
import com.Gr8niteout.stripe.StripeConnectScreen;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;

public class DashboardHomeActivity extends AppCompatActivity {

    DrawerLayout drawer;
    public ListView mDrawerList;
    public PubDashboardNavDrawerAdapter adapter;
    public ArrayList<String> nav_drawer_items = new ArrayList<>();
    TextView tvTitle, tvLogout, tvFullName, tvUsername;
    ImageView ivNotifications, drawerIcon, ivHome, ivUser, ivDrawerUser;
    Toolbar toolbar;
    LinearLayout llLogout;
    CardView ivUserLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_home);

        init();
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        drawerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    CommonUtilities.hideKeyboard(true, DashboardHomeActivity.this);
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    CommonUtilities.hideKeyboard(true, DashboardHomeActivity.this);
                    drawer.openDrawer(GravityCompat.START);
                }

            }
        });
        toggle.syncState();

        setDrawerItem();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        selectItem(0);

        ivNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectItem(9);
                closeDrawer();
                mDrawerList.setItemChecked(-1, true);
            }
        });

        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(DashboardHomeActivity.this);
                builder1.setTitle(R.string.app_name);
                builder1.setIcon(R.mipmap.app_icon);
                builder1.setMessage("Do you want to Logout?");
                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences preferences = getSharedPreferences("pub_details",MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putBoolean("isPubLoggedIn",false);
                        editor.apply();

                        startActivity(new Intent(DashboardHomeActivity.this, SignupLogin.class));
                        finishAffinity();
                    }
                });
                builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert11 = builder1.create();
                alert11.show();

                Button bq = alert11.getButton(DialogInterface.BUTTON_NEGATIVE);
                bq.setTextColor(Color.parseColor("#0053A8"));
            }
        });

        CommonUtilities.setFontFamily(this, tvLogout, CommonUtilities.AvenirLTStd_Medium);

    }

    private void init(){
        toolbar = findViewById(R.id.toolbar);
        drawerIcon  = findViewById(R.id.drawerIcon);
        mDrawerList = findViewById(R.id.mDrawerList);
        drawer = findViewById(R.id.drawer_layout);
        ivNotifications = findViewById(R.id.ivNotifications);
        ivHome = findViewById(R.id.ivHome);
        tvTitle = findViewById(R.id.tvTitle);
        ivUser = findViewById(R.id.ivUser);
        tvLogout = findViewById(R.id.tvLogout);
        llLogout = findViewById(R.id.llLogout);
        ivUserLayout = findViewById(R.id.ivUserLayout);
        ivDrawerUser = findViewById(R.id.ivDrawerUser);
        tvFullName = findViewById(R.id.tvFullName);
        tvUsername = findViewById(R.id.tvUsername);
    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            selectItem(position);
        }
    }

    public void setDrawerItem() {

        nav_drawer_items.clear();

        String[] navItemsArray = getResources().getStringArray(R.array.pub_nav_drawer_items);

        nav_drawer_items.addAll(Arrays.asList(navItemsArray));
        adapter = new PubDashboardNavDrawerAdapter(this, nav_drawer_items);
        mDrawerList.setAdapter(adapter);
    }

    public void selectItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new DashboardFragment();
                mDrawerList.setItemChecked(0, true);
                updateTitle("Dashboard");
                break;

            case 1:
                fragment = new NoticeBoardFragment();
                mDrawerList.setItemChecked(1, true);
                updateTitle("Notice Board");
                break;

            case 2:
                fragment = new AccountDetailsFragment(ivUser);
                mDrawerList.setItemChecked(2, true);
                updateTitle("Account Details");
                break;

            case 3:

                fragment = new TransactionHistoryFragment();
                mDrawerList.setItemChecked(3, true);
                updateTitle("Payment History");

                break;

            case 4:
                fragment = new PubProfileFragment();
                mDrawerList.setItemChecked(4 , true);
                updateTitle("Pub Profile");

                break;

            case 5:
                fragment = new StripeConnectScreen();
                mDrawerList.setItemChecked(5, true);
                updateTitle("Connect Stripe");
                break;

            case 6:


                fragment = new PremisesFragment();
                mDrawerList.setItemChecked(6, true);
                updateTitle("Premises");

                break;

            case 7:

                fragment = new ChangePasswordFragment();
                mDrawerList.setItemChecked(7, true);
                updateTitle("Change Password");

                break;

            case 8:
                fragment = new MultipleSubscriptionFragment();
                mDrawerList.setItemChecked(8, true);
                updateTitle("Subscription");
                break;
            case 9:
                fragment = new NotificationFragment();
                updateTitle("Notifications");
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            if (!isFinishing())
                fragmentTransaction.commitAllowingStateLoss();
        }
    }

//    public void selectItem(int position) {
//        Fragment fragment = null;
//
//        switch (position) {
//            case 0:
//                fragment = new DashboardFragment();
//                mDrawerList.setItemChecked(0, true);
//                updateTitle("Dashboard");
//                break;
//
//            case 1:
//                fragment = new NoticeBoardFragment();
//                mDrawerList.setItemChecked(1, true);
//                updateTitle("Notice Board");
//                break;
//            case 2:
//                fragment = new AccountDetailsFragment(ivUser);
//                mDrawerList.setItemChecked(2, true);
//                updateTitle("Account Details");
//                break;
//            case 3:
//                fragment = new PubProfileFragment();
//                mDrawerList.setItemChecked(3, true);
//                updateTitle("Pub Profile");
//                break;
//            case 4:
//                fragment = new PremisesFragment();
//                mDrawerList.setItemChecked(4, true);
//                updateTitle("Premises");
//                break;
//            case 5:
//                fragment = new TransactionHistoryFragment();
//                mDrawerList.setItemChecked(5, true);
//                updateTitle("Payment History");
//                break;
//            case 6:
//                fragment = new ChangePasswordFragment();
//                mDrawerList.setItemChecked(6, true);
//                updateTitle("Change Password");
//                break;
//            case 7:
//                fragment = new MultipleSubscriptionFragment();
//                mDrawerList.setItemChecked(7, true);
//                updateTitle("Subscription");
//                break;
//            case 8:
//                fragment = new StripeConnectScreen();
//                mDrawerList.setItemChecked(8, true);
//                updateTitle("Connect Stripe");
//                break;
//            case 9:
//                fragment = new NotificationFragment();
//                updateTitle("Notifications");
//                break;
//
//        }
//
//        if (fragment != null) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.container, fragment);
//            if (!isFinishing())
//                fragmentTransaction.commitAllowingStateLoss();
//        }
//    }

    public void closeDrawer(){
        CommonUtilities.hideKeyboard(true, DashboardHomeActivity.this);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void updateTitle(String title){
        tvTitle.setText(title);
        if(title.equals("Dashboard")){
            ivHome.setVisibility(View.VISIBLE);
        }else{
            ivHome.setVisibility(View.GONE);
        }

        if(title.equals("Profile")){
            ivUserLayout.setVisibility(View.VISIBLE);
        }else{
            ivUserLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerVisible(GravityCompat.START)) {
            CommonUtilities.hideKeyboard(true, DashboardHomeActivity.this);
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.mipmap.app_icon);
            builder.setMessage("Are you sure you want to quit?");

            builder.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    DashboardHomeActivity.super.onBackPressed();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

            Button bq = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            bq.setTextColor(Color.parseColor("#0053A8"));
        }
    }

    public void goToLoginScreen(){
        Intent intent = new Intent(DashboardHomeActivity.this, PubLoginActivity.class);
        intent.putExtra("pub_id", "");
        intent.putExtra("isSuccess",3);
        startActivity(intent);
        finishAffinity();
    }

    public void updateDrawerProfile(String imageLink, String fullName, String username, int check){
        tvFullName.setText(fullName);
        tvUsername.setText(username);
        if(check == 0) {
            Glide.with(DashboardHomeActivity.this)
                    .load(Uri.parse(imageLink))
                    .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                    .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                    .into(ivDrawerUser);
        }else{
            if(!imageLink.equals("")) {
                ivDrawerUser.setImageURI(Uri.parse(imageLink));
            }
        }
    }

}