package com.Gr8niteout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.Gr8niteout.ActivityFragment.ActivityFragment;
import com.Gr8niteout.adapter.NavDrawerListAdapter;
import com.Gr8niteout.config.CircleTransform;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.home.HomeFragment;
import com.Gr8niteout.model.Logout_Model;
import com.Gr8niteout.model.SignUpModel;
import com.Gr8niteout.myprofile.MyProfileFragment;
import com.Gr8niteout.myprofile.UserTransactionHistoryFragment;
import com.Gr8niteout.search.SearchFragment;
import com.Gr8niteout.setting.SettingFragment;
import com.Gr8niteout.signup.SignupLogin;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.BindView;

import static com.Gr8niteout.AllowAccess.REQUEST_LOCATION;

public class MainActivity extends AppCompatActivity {

    public ListView mDrawerList;
    public NavDrawerListAdapter adapter;
    public ArrayList<String> nav_drawer_items = new ArrayList<>();
    DrawerLayout drawer;
    public ArrayList<Integer> imgItems = new ArrayList<>();
//    @BindView(R.id.txtFullname)
    TextView txtFullname;
//    @BindView(R.id.tool_text)
    public TextView tool_text;
//    @BindView(R.id.textname)
    public TextView textname;
//    @BindView(R.id.imgProfile)
    ImageView imgProfile;
//    @BindView(R.id.goImage)
    public ImageView goImage;
    SignUpModel model;
    LocationManager locationManager;
    public int selectedPos, prevPos = -1;
    public static boolean isExit = false;
    public static MainActivity act;
    Logout_Model logmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.isInitialized();
        act = MainActivity.this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setContentInsetStartWithNavigation(-2);
        getSupportActionBar().setTitle(null);
        ButterKnife.bind(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        txtFullname = findViewById(R.id.txtFullname);
        tool_text =  findViewById(R.id.tool_text);
        textname =findViewById(R.id.textname);
        imgProfile =findViewById(R.id.imgProfile);
        goImage =findViewById(R.id.goImage);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toolbar.setContentInsetStartWithNavigation(0);
        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.mipmap.menu_icon, this.getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    CommonUtilities.hideKeyboard(true, MainActivity.this);
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    CommonUtilities.hideKeyboard(true, MainActivity.this);
                    drawer.openDrawer(GravityCompat.START);
                }

            }
        });

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
            }
        }

        toggle.syncState();
        mDrawerList = (ListView) findViewById(R.id.mDrawerList);

        setDrawerItem();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableLoc();
        } else {
            setItem();
        }
    }

    public void lin_login(View v) {

        if (CommonUtilities.getPreference(MainActivity.this, CommonUtilities.pref_UserId).equals("")) {
            Intent i = new Intent(MainActivity.this, SignupLogin.class);
            i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_menu);
            startActivity(i);
        } else {
            selectItem(3);
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void enableLoc() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    public void setItem() {
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_drinks)) {
            mDrawerList.setSelection(2);
            mDrawerList.setItemChecked(2, true);
            selectItem(2);
        } else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_birtday)) {
            mDrawerList.setSelection(2);
            mDrawerList.setItemChecked(2, true);
            selectItem(2);
        } else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_my_profile)) {
            mDrawerList.setSelection(3);
            mDrawerList.setItemChecked(3, true);
            selectItem(3);
        }
        else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_received_credit)) {
            mDrawerList.setSelection(2);
            mDrawerList.setItemChecked(2, true);
            selectItem(2);
        }else {
            mDrawerList.setSelection(0);
            mDrawerList.setItemChecked(0, true);
            selectItem(0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (nav_drawer_items.size() > 0) {
                if (selectedPos != 0) {
                    selectItem(0);
                    mDrawerList.setSelection(0);
                    mDrawerList.setItemChecked(0, true);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.app_name);
                    builder.setIcon(R.mipmap.app_icon);
                    builder.setMessage("Are you sure you want to quit?");

                    builder.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
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
            } else {
                //super.onBackPressed();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.app_name);
                builder.setIcon(R.mipmap.app_icon);
                builder.setMessage("Are you sure you want to quit?");

                builder.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
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
    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
                if (nav_drawer_items.get(position).equals(getResources().getString(R.string.title_share))) {
                    if (prevPos != -1) {
                        mDrawerList.setItemChecked(prevPos, true);
                    }
                } else
                    prevPos = position;
                selectItem(position);
            }
        }
    }

    public void selectItem(int position) {
        Fragment fragment = null;
        selectedPos = position;
        if (nav_drawer_items.get(position).equals("Home")) {
            fragment = new HomeFragment();
        } else if (nav_drawer_items.get(position).equals("Settings")) {
            fragment = new SettingFragment();
        } else if (nav_drawer_items.get(position).equals("Activity")) {

            fragment = new ActivityFragment();
        } else if (nav_drawer_items.get(position).equals("My Profile")) {

            fragment = new MyProfileFragment();
        } else if (nav_drawer_items.get(position).equals(getResources().getString(R.string.title_search))) {
            fragment = new SearchFragment();
        } else if (nav_drawer_items.get(position).equals(getResources().getString(R.string.title_Logout))) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setTitle(R.string.app_name);
            builder1.setIcon(R.mipmap.app_icon);
            builder1.setMessage("Do you want to Log out?");
            builder1.setCancelable(false);
            builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    logout();
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

        } else if (nav_drawer_items.get(position).equals(getResources().getString(R.string.title_share))) {
            String url = "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();
            Intent share = new Intent();
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, url);
            share.setAction(Intent.ACTION_SEND);
            startActivity(Intent.createChooser(share, "Share link!"));
        }else if (position == 7) {
            fragment = new UserTransactionHistoryFragment();
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment, nav_drawer_items.get(position));
            if (!isFinishing())
                fragmentTransaction.commitAllowingStateLoss();
        }
    }

    public void logout() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(CommonUtilities.key_user_id, CommonUtilities.getPreference(act, CommonUtilities.pref_UserId));
        ServerAccess.getResponse(act, "logout", params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                logmodel = new Logout_Model().Logout_Model(result);
                if (logmodel != null) {
                    if (logmodel.response.status.equals(CommonUtilities.key_Success)) {

                        if (logmodel.response.user_logout.token != null)
                            CommonUtilities.setSecurity_Preference(act, CommonUtilities.key_security_toekn, logmodel.response.user_logout.token);

                        LoginManager.getInstance().logOut();
                        CommonUtilities.RemoveALlPreference(MainActivity.this);
                        Intent intent = new Intent(MainActivity.this, SignupLogin.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        CommonUtilities.ShowToast(act, logmodel.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_reset).setVisible(false);
        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_search_white).setVisible(true);
        menu.findItem(R.id.action_setting).setVisible(false);
        menu.findItem(R.id.action_transaction_history).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search_white) {
            selectItem(1);
            mDrawerList.setSelection(2);
            mDrawerList.setItemChecked(1, true);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_search_white:
//                selectItem(1);
//                mDrawerList.setSelection(2);
//                mDrawerList.setItemChecked(1, true);
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        setItem();
                    }
                    case Activity.RESULT_CANCELED: {
                        setItem();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }

    }

    public void setDrawerItem() {

        nav_drawer_items.clear();
        imgItems.clear();

        imgItems.add(R.drawable.slider_home);
        imgItems.add(R.drawable.slider_search);
        imgItems.add(R.drawable.slider_activity);
        imgItems.add(R.drawable.slider_profile);
        imgItems.add(R.drawable.slider_setting);
        imgItems.add(R.drawable.slider_share);

        String array[] = getResources().getStringArray(R.array.nav_drawer_items);

        if (CommonUtilities.getPreference(MainActivity.this, CommonUtilities.pref_UserId).equals("")) {
            for (int j = 0; j < array.length - 2; j++) {
                nav_drawer_items.add(array[j]);
            }
        } else {

            for (int j = 0; j < array.length; j++) {
                nav_drawer_items.add(array[j]);
            }

            imgItems.add(R.drawable.slider_logout);
            imgItems.add(R.drawable.baseline_history_24);
            model = new SignUpModel().SignUpModel(CommonUtilities.getPreference(MainActivity.this, CommonUtilities.pref_UserData));
            CommonUtilities.setFontFamily(MainActivity.this, txtFullname, CommonUtilities.Avenir_Heavy);
            txtFullname.setText(model.response.user_data.fname + " " + model.response.user_data.lname);

            if (model.response.user_data.getPhoto().equals("")) {
                imgProfile.setImageResource(R.mipmap.no_user);
                if (!model.response.user_data.getFname().equals("")) {
                    textname.setText(model.response.user_data.getFname().substring(0, 1));
                } else {
                    imgProfile.setImageResource(R.mipmap.user);
                }
            } else {
                textname.setText("");
//                changed on 28-jan-2019
                Picasso.get()
                        .load(CommonUtilities.Gr8niteoutURL + CommonUtilities.User_Profile_URL + model.response.user_data.photo)
                        .error(R.mipmap.user).placeholder(R.mipmap.user) // optional
                        .transform(new CircleTransform())
                        .into(imgProfile, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.e("success", "success");
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("error", "error");
                            }
                        });
            }

        }
        adapter = new NavDrawerListAdapter(this, nav_drawer_items, imgItems);
        mDrawerList.setAdapter(adapter);
    }
}
