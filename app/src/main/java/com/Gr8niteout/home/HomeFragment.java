package com.Gr8niteout.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.Gr8niteout.MainActivity;
import com.Gr8niteout.R;
import com.Gr8niteout.adapter.NearByPubAdapter;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.DividerItemDecoration;
import com.Gr8niteout.config.GPSTracker;
import com.Gr8niteout.config.MyApplication;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.HomeData;
import com.Gr8niteout.model.SignUpModel;
import com.Gr8niteout.model.UserLoginResponse;
import com.Gr8niteout.pub.PubActivity;
import com.google.gson.Gson;
import com.Gr8niteout.signup.SignupLogin;
import com.facebook.login.LoginManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.LOCATION_SERVICE;

public class HomeFragment extends Fragment implements ViewPager.OnPageChangeListener, GoogleApiClient.ConnectionCallbacks {

    MainActivity mActivity;
    HomeData model;

    private RecyclerView recyclerView;
    private NearByPubAdapter mAdapter;

    TextView txtName;
    TextView txtHello;
    TextView txtNewest;

    SignUpModel signUpModel;
    UserLoginResponse userLoginModel;

    ViewPager pagerBanner;
    private LinearLayout viewPagerCountDots;
    private int dotsCount;
    private ImageView[] dots;
    private ViewPagerAdapter adapter;

    public Map<String, String> params = new HashMap<String, String>();
    int page = 0;
    Timer timer;
    public Menu menu;
    public static MainActivity act;

    int savePosition, selected_page;
    String userid = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        act = new MainActivity();
        mActivity.getSupportActionBar().setTitle(null);
        mActivity.getSupportActionBar().setIcon(R.mipmap.g_icon);
        mActivity.tool_text.setText("");
        txtName = (TextView) v.findViewById(R.id.txtName);
        txtHello = (TextView) v.findViewById(R.id.txtHello);
        txtNewest = (TextView) v.findViewById(R.id.txtNewest);
        pagerBanner = (ViewPager) v.findViewById(R.id.pagerBanner);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.HORIZONTAL_LIST));
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        pagerBanner.setOnPageChangeListener(this);
        viewPagerCountDots = (LinearLayout) v.findViewById(R.id.viewPagerCountDots);

        setFonts();
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};
        CommonUtilities.setPermission(mActivity, PERMISSIONS);

        LocationManager locationManager = (LocationManager) mActivity.getSystemService(LOCATION_SERVICE);

        if (CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId).equals(""))
            userid = "";
        else
            userid = CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            getLocation("0");
        else
            getLocation("1");

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(mActivity, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent(mActivity, PubActivity.class);
                        i.putExtra(CommonUtilities.key_pub_id, model.response.home_data.home_pubinfo.get(position).pub_id);
                        i.putExtra(CommonUtilities.key_pub_name, model.response.home_data.home_pubinfo.get(position).pub_name);
                        startActivity(i);
                    }
                })
        );

        setHasOptionsMenu(true);
        pagerBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selected_page = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (savePosition > state) {
                    if (selected_page == (dotsCount - 1)) {
                        selected_page++;
                    } else if (selected_page > (dotsCount - 1)) {
                        pagerBanner.setCurrentItem(0);
                        page = 0;
                    }
                    // scrolling Right ...
                } else {
                    //scrolling left ...
                }
                savePosition = state;
            }
        });

        return v;
    }


    private void setUiPageViewController() {

        dotsCount = adapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(mActivity);
            dots[i].setImageDrawable(mActivity.getResources().getDrawable(R.mipmap.bullet_two));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            viewPagerCountDots.addView(dots[i], params);
        }
        if (dots.length > 0)
            dots[0].setImageDrawable(mActivity.getResources().getDrawable(R.mipmap.bullet_one));
    }

    public void pageSwitcher(int seconds) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000);
    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    if (page >= dotsCount) {
                        pagerBanner.setCurrentItem(0,false);
                        page = 0;
                    } else {
                        pagerBanner.setCurrentItem(page++);
                    }
                }
            });
        }
    }

    public void getLocation(String flag) {
        if (flag.equals("1")) {
            txtNewest.setText("Nearest Pubs");
            GPSTracker gps = new GPSTracker(mActivity);
            params.put(CommonUtilities.key_latitude, String.valueOf(gps.getLatitude()));
            params.put(CommonUtilities.key_longitude, String.valueOf(gps.getLongitude()));
        } else {
            txtNewest.setText("Newest Pubs");
            params.put(CommonUtilities.key_latitude, "");
            params.put(CommonUtilities.key_longitude, "");
        }
        params.put(CommonUtilities.key_user_id, userid);
        getHomeData();
    }


    private void getHomeData() {

        ServerAccess.getResponse(mActivity, CommonUtilities.key_home_data, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                model = new HomeData().HomeData(result);
                if (model != null) {
                    if (model.response.status.equals(CommonUtilities.key_Success)) {
                        if ("1".equals(model.response.home_data.user_status) || model.response.home_data.user_status == null || (model.response.home_data.user_status != null && model.response.home_data.user_status.equals(""))) {
                            CommonUtilities.setPreference(mActivity, CommonUtilities.pref_homedata, result);
                            adapter = new ViewPagerAdapter(mActivity, model.response.home_data.banner);
                            pagerBanner.setAdapter(adapter);
                            pagerBanner.setOffscreenPageLimit(1);
                            pagerBanner.setCurrentItem(0);
                            if (model.response.home_data.banner.size() != 1)
                                setUiPageViewController();
                            CountDownTimer timer = new CountDownTimer(8000, 1250) {

                                public void onTick(long millisUntilFinished) {

                                }

                                public void onFinish() {
                                    pageSwitcher(2);
                                }
                            };
                            timer.start();
                            mAdapter = new NearByPubAdapter(mActivity, model.response.home_data.home_pubinfo);
                            recyclerView.setAdapter(mAdapter);
                        } else if ("0".equals(model.response.home_data.user_status)) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(mActivity);
                            builder1.setTitle(R.string.app_name);
                            builder1.setIcon(R.mipmap.app_icon);
                            builder1.setMessage(model.response.msg);
                            builder1.setCancelable(false);
                            builder1.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    LoginManager.getInstance().logOut();
                                    CommonUtilities.RemoveALlPreference(mActivity);
                                    Intent intent = new Intent(mActivity, SignupLogin.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    mActivity.finish();
                                }
                            });
                            AlertDialog alert11 = builder1.create();
                            alert11.show();

                            Button bq = alert11.getButton(DialogInterface.BUTTON_POSITIVE);
                            bq.setTextColor(Color.parseColor("#0053A8"));
                        }
                    } else {
                        CommonUtilities.alertdialog(mActivity, model.response.msg);
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(mActivity.getResources().getDrawable(R.mipmap.bullet_two));
        }
        if (dots != null && dots.length > 0) {
            dots[position].setImageDrawable(mActivity.getResources().getDrawable(R.mipmap.bullet_one));
            page = position;
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    public class ViewPagerAdapter extends PagerAdapter {

        private Context mContext;
        private ArrayList<HomeData.response.home_data.banner> listBanner = new ArrayList<>();

        public ViewPagerAdapter(Context mContext, ArrayList<HomeData.response.home_data.banner> listBanner) {
            this.mContext = mContext;
            this.listBanner = listBanner;
        }

        @Override
        public int getCount() {
            return listBanner.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.home_banner_image_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);
            final ImageView imgPlaceHolder = (ImageView) itemView.findViewById(R.id.imgPlaceHolder);

            final ProgressBar progress_bar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            progress_bar.setVisibility(View.VISIBLE);

//            changed on 28-jan-2019
            Picasso.get()
                    .load(CommonUtilities.Gr8niteoutURL + CommonUtilities.Home_Banner_URL + listBanner.get(position).image_name)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progress_bar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            imgPlaceHolder.setVisibility(View.VISIBLE);
                            progress_bar.setVisibility(View.GONE);
                        }
                    });

            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (listBanner.get(position).banner_url != null && !listBanner.get(position).banner_url.equals("")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(listBanner.get(position).banner_url));
                        startActivity(browserIntent);
                    }
                }
            });
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    public void setDetails() {

        if (!CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId).equals("")) {
            // Safely parse user data with null checks
            String userDataString = CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserData);
            if (userDataString != null && !userDataString.isEmpty()) {
                try {
                    // Try to parse as SignUpModel first (for Facebook login)
                    signUpModel = new SignUpModel().SignUpModel(userDataString);
                    if (signUpModel != null && signUpModel.response != null && signUpModel.response.user_data != null) {
                        txtName.setVisibility(View.VISIBLE);
                        String firstName = signUpModel.response.user_data.fname != null ? signUpModel.response.user_data.fname : "";
                        if (!firstName.isEmpty()) {
                            txtName.setText(firstName + ".");
                        } else {
                            txtName.setText("User.");
                        }
                    } else {
                        // Try UserLoginResponse (for email/password login)
                        Gson gson = new Gson();
                        userLoginModel = gson.fromJson(userDataString, UserLoginResponse.class);
                        if (userLoginModel != null && userLoginModel.response != null && userLoginModel.response.responseInfo != null && userLoginModel.response.responseInfo.data != null) {
                            txtName.setVisibility(View.VISIBLE);
                            String firstName = userLoginModel.response.responseInfo.data.end_first_name != null ? userLoginModel.response.responseInfo.data.end_first_name : "";
                            if (!firstName.isEmpty()) {
                                txtName.setText(firstName + ".");
                            } else {
                                txtName.setText("User.");
                            }
                        } else {
                            // Fallback when both models are null but user is logged in
                            txtName.setVisibility(View.VISIBLE);
                            txtName.setText("User.");
                        }
                    }
                } catch (Exception e) {
                    Log.e("HomeFragment", "Error parsing user data: " + e.getMessage());
                    // Fallback when parsing fails but user is logged in
                    txtName.setVisibility(View.VISIBLE);
                    txtName.setText("User.");
                }
            } else {
                // Fallback when no user data but user is logged in
                txtName.setVisibility(View.VISIBLE);
                txtName.setText("User.");
            }
        } else {
            txtHello.setText("Hello,");
            txtName.setText("Good Looking.");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_reset).setVisible(false);
        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_search_white).setVisible(true);
        menu.findItem(R.id.action_setting).setVisible(false);
        menu.findItem(R.id.action_transaction_history).setVisible(false);

    }

    @Override
    public void onResume() {
        super.onResume();
        setDetails();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    public void setFonts() {
        CommonUtilities.setFontFamily(mActivity, txtHello, CommonUtilities.AvenirLTStd_Black);
        CommonUtilities.setFontFamily(mActivity, txtName, CommonUtilities.AvenirLTStd_Black);
        CommonUtilities.setFontFamily(mActivity, txtNewest, CommonUtilities.AvenirLTStd_Medium);

    }

    @Override
    public void onStart() {
        super.onStart();

        ((MyApplication) mActivity.getApplication()).getDefaultTracker().setScreenName("Home Screen");
        ((MyApplication) mActivity.getApplication()).getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }
}
