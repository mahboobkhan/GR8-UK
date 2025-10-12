package com.Gr8niteout.pub;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.InflateException;
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
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.Gr8niteout.MainActivity;
import com.Gr8niteout.R;
import com.Gr8niteout.buycredits.BuyCreditActivity;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.GPSTracker;
import com.Gr8niteout.config.MyApplication;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.home.HomeFragment;
import com.Gr8niteout.model.CheckinResponse;
import com.Gr8niteout.model.PubProfile;
import com.Gr8niteout.signup.SignupLogin;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import at.blogc.android.views.ExpandableTextView;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class PubDetailFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener, OnMapReadyCallback {

    public TextView txtReadMore;
    public TextView txtAmenities;
    public TextView txtdistance;
    public TextView txtOpen;
    public TextView txtStatus;
    public LinearLayout layoutFeatures;
    public LinearLayout layoutDays;
    public LinearLayout layoutStation;
    public ScrollView scroll;
    public LinearLayout layoutNumber, layoutAmenities;
    public TextView txtFeatures;
    public ArrayList<Integer> imgFeatures = new ArrayList<>();
    public int day;
    String pub_id;
    ViewPager pagerBanner;
    PubActivity mActivity;
    PubProfile model;
    int page = 0;
    Timer timer;
    ArrayList<String> listbanner = new ArrayList<>();
    SupportMapFragment mapFragment;
    View view;
    private LinearLayout viewPagerCountDots;
    private int dotsCount;
    private ImageView[] dots;
    private ViewPagerAdapter adapter;
    private GoogleMap mMap;
    private ImageView imgThumb;
    private TextView txtPubName;
    private RatingBar ratingBar1;
    private ExpandableTextView txtDescription;
    private TextView txtAddress, txtLocation, txtTel, txtNearest, txtOpening;
    private Button txtBuyCredits;
    private TextView txtNumber;
    private TextView txtStation;
    private boolean m_iAmVisible = false;
    String creditNotAvailableMsg = "Credit not Available";

    private LinearLayout llCheckin;//checkin feature
    private String userid = "";
    private TextView txtCheckin;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_pub_detail, container, false);
        } catch (InflateException e) {
        }
        viewPagerCountDots = (LinearLayout) view.findViewById(R.id.viewPagerCountDots);
        pagerBanner = (ViewPager) view.findViewById(R.id.pagerBanner);
        imgThumb = (ImageView) view.findViewById(R.id.imgThumb);
        txtPubName = (TextView) view.findViewById(R.id.txtPubName);
        txtReadMore = (TextView) view.findViewById(R.id.txtReadMore);
        txtOpen = (TextView) view.findViewById(R.id.txtOpen);
        txtStatus = (TextView) view.findViewById(R.id.txtStatus);
        txtAmenities = (TextView) view.findViewById(R.id.txtAmenities);
        txtdistance = (TextView) view.findViewById(R.id.txtdistance);
        ratingBar1 = (RatingBar) view.findViewById(R.id.ratingBar1);
        txtDescription = (ExpandableTextView) view.findViewById(R.id.txtDescription);
        layoutFeatures = (LinearLayout) view.findViewById(R.id.layoutFeatures);
        layoutStation = (LinearLayout) view.findViewById(R.id.layoutStation);
        layoutNumber = (LinearLayout) view.findViewById(R.id.layoutNumber);
        layoutAmenities = (LinearLayout) view.findViewById(R.id.layoutAmenities);
        layoutDays = (LinearLayout) view.findViewById(R.id.layoutDays);
        txtNumber = (TextView) view.findViewById(R.id.txtNumber);
        txtStation = (TextView) view.findViewById(R.id.txtStation);
        txtAddress = (TextView) view.findViewById(R.id.txtAddress);
        txtBuyCredits = (Button) view.findViewById(R.id.txtBuyCredits);
        txtFeatures = (TextView) view.findViewById(R.id.txtFeatures);
        txtLocation = (TextView) view.findViewById(R.id.txtLocation);
        txtTel = (TextView) view.findViewById(R.id.txtTel);
        txtNearest = (TextView) view.findViewById(R.id.txtNearest);
        txtOpening = (TextView) view.findViewById(R.id.txtOpening);
        scroll = (ScrollView) view.findViewById(R.id.scroll);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        //checkin feature
        llCheckin = view.findViewById(R.id.img_checkin);
        txtCheckin = view.findViewById(R.id.txt_check_in);
        userid = CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId);//

        mapFragment.getMapAsync(this);
       /* imgFeatures.clear();
        imgFeatures.add(R.mipmap.tv);
        imgFeatures.add(R.mipmap.food);
        imgFeatures.add(R.mipmap.mic);
        imgFeatures.add(R.mipmap.headphone);
        imgFeatures.add(R.mipmap.wine);
        imgFeatures.add(R.mipmap.money);
        imgFeatures.add(R.mipmap.wifi);
        imgFeatures.add(R.mipmap.disable);
        imgFeatures.add(R.mipmap.question);
        imgFeatures.add(R.mipmap.beer);
        imgFeatures.add(R.mipmap.stop);
        imgFeatures.add(R.mipmap.children);
        imgFeatures.add(R.mipmap.dog);
*/
        if (getArguments() != null && getArguments().containsKey(CommonUtilities.key_pub_id)) {
            pub_id = getArguments().getString(CommonUtilities.key_pub_id);
            if (!m_iAmVisible)
                getPubProfile();
        }
        m_iAmVisible = true;

        llCheckin.setOnClickListener(this);
        txtReadMore.setOnClickListener(this);
        txtAmenities.setOnClickListener(this);
        txtNumber.setOnClickListener(this);
        layoutAmenities.setOnClickListener(this);
        ratingBar1.setIsIndicator(true);
        pagerBanner.setOnPageChangeListener(this);
        setHasOptionsMenu(true);

        txtBuyCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtBuyCredits.getText().equals(creditNotAvailableMsg))
                    if (model != null) {

                        Log.d("Image", "onClick: "+model.response.pub_profile.banner_array.get(0));

                        Intent i = new Intent(mActivity, BuyCreditActivity.class);
                        i.putExtra(CommonUtilities.key_pub_name, model.response.pub_profile.pub_name);
                        i.putExtra(CommonUtilities.key_pub_image, model.response.pub_profile.banner_array.get(0));
                        i.putExtra(CommonUtilities.key_pub_id, model.response.pub_profile.pub_id);
                        i.putExtra("currency", model.response.pub_profile.currency);
                        i.putExtra("from", "Details");
                        i.putExtra("recipient_name", "");
                        i.putExtra("recipient_email", "");
                        startActivity(i);
                    }
            }
        });

        setFonts();

        CommonUtilities.setPreference(mActivity, CommonUtilities.key_checkin_data, "");//clear checkin
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mActivity != null)
            CommonUtilities.setPreference(mActivity, CommonUtilities.key_checkin_data, "");//clear checkin
        if (isVisibleToUser) {
            if (m_iAmVisible)
                getPubProfile();
        } else {

        }
    }

    public void setFonts() {
        CommonUtilities.setFontFamily(mActivity, txtDescription, CommonUtilities.Avenir);
        CommonUtilities.setFontFamily(mActivity, txtFeatures, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, txtdistance, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, txtAmenities, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(mActivity, txtLocation, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, txtReadMore, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(mActivity, txtPubName, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, txtAddress, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, txtTel, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, txtNumber, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, txtNearest, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, txtStation, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, txtOpening, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, txtStatus, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, txtBuyCredits, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(mActivity, txtCheckin, CommonUtilities.Avenir_Heavy);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void getPubProfile() {

        Map<String, String> params = new HashMap<String, String>();
        GPSTracker gps = new GPSTracker(getActivity());
        if (gps.getLatitude() != 0.0 && gps.getLongitude() != 0.0) {
            params.put(CommonUtilities.key_latitude, String.valueOf(gps.getLatitude()));
            params.put(CommonUtilities.key_longitude, String.valueOf(gps.getLongitude()));
        } else {
            params.put(CommonUtilities.key_latitude, "");
            params.put(CommonUtilities.key_longitude, "");
        }
        params.put(CommonUtilities.key_pub_id, pub_id);
        ServerAccess.getResponse(getActivity(), CommonUtilities.key_pub_profile, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                model = new PubProfile().PubProfile(result);
                if (model != null) {
                    if (model.response.status.equals(CommonUtilities.key_Success)) {
                        CommonUtilities.setPreference(mActivity, CommonUtilities.pref_pub_url, model.response.pub_profile.url);
                        CommonUtilities.setPreference(mActivity, CommonUtilities.pref_pub_detail, new Gson().toJson(model.response.pub_profile));
                        listbanner.clear();
                        listbanner.addAll(model.response.pub_profile.banner_array);

                        adapter = new ViewPagerAdapter(getActivity(), listbanner);
                        pagerBanner.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        txtAmenities.setVisibility(View.VISIBLE);
                        setFeatures(4);
                        setDays();
                        pagerBanner.setCurrentItem(0);
                        pagerBanner.setOffscreenPageLimit(1);
                        if (listbanner.size() != 1)
                            setUiPageViewController();
                        CountDownTimer timer = new CountDownTimer(8000, 1250) {

                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {
                                pageSwitcher(2);
                            }
                        };
                        timer.start();
                        setData();
                        if (!TextUtils.isEmpty(model.response.pub_profile.pub_type)) {
                            if (model.response.pub_profile.pub_type.equals("1")) {
                                txtBuyCredits.setVisibility(View.VISIBLE);
                            } else if (model.response.pub_profile.pub_type.equals("0")) {
                                txtBuyCredits.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        CommonUtilities.alertdialog(getActivity(), model.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void setFeatures(int size) {
        layoutFeatures.removeAllViews();
        LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (model.response.pub_profile.feature_list.size() >= size - 1) {
            for (int i = 0; i < size; i++) {
                View convertView = vi.inflate(R.layout.feature_row, null);
                convertView.setId(i);
                ImageView imgFeature = (ImageView) convertView.findViewById(R.id.imgFeature);
                TextView txtFeature = (TextView) convertView.findViewById(R.id.txtFeature);
                LinearLayout layoutLine = (LinearLayout) convertView.findViewById(R.id.layoutLine);
                txtFeature.setTextSize((float) 13.75);
                txtFeature.setTextColor(getResources().getColor(R.color.mgray));
                CommonUtilities.setFontFamily(mActivity, txtFeature, CommonUtilities.AvenirLTStd_Medium);
                TextView txtFeatureValue = (TextView) convertView.findViewById(R.id.txtFeatureValue);
                CommonUtilities.setFontFamily(mActivity, txtFeatureValue, CommonUtilities.AvenirLTStd_Medium);

//            changed on 28-jan-2019
                Picasso.get()
                        .load(CommonUtilities.Gr8niteoutURL + CommonUtilities.Features_URL + model.response.pub_profile.feature_list.get(i).big_image)
                        .into(imgFeature, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError(Exception e) {

                            }

                        });
                //imgFeature.setImageResource(imgFeatures.get(i));
                txtFeature.setText(model.response.pub_profile.feature_list.get(i).name);
                txtFeatureValue.setText(model.response.pub_profile.feature_list.get(i).value);
                txtFeatureValue.setTextSize((float) 13.75);
                if (i == 4) {
                    if (model.response.pub_profile.currency.equals("1"))
                        txtFeatureValue.setText("£" + model.response.pub_profile.feature_list.get(i).value);
                    else if (model.response.pub_profile.currency.equals("2"))
                        txtFeatureValue.setText("€" + model.response.pub_profile.feature_list.get(i).value);
                    else if (model.response.pub_profile.currency.equals("3"))
                        txtFeatureValue.setText("$" + model.response.pub_profile.feature_list.get(i).value);
                }

                if (i == size - 1) {
                    layoutLine.setVisibility(View.INVISIBLE);
                }

                layoutFeatures.addView(convertView);
            }
        }
    }

    public void setDays() {
        layoutDays.removeAllViews();
        LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_WEEK);
        String array[] = getResources().getStringArray(R.array.days);
        if (model.response.pub_profile.days_list.size() > 0) {
            for (int i = 0; i < model.response.pub_profile.days_list.size(); i++) {
                View convertView = vi.inflate(R.layout.days_row, null);
                convertView.setId(i);
                TextView txtDays = (TextView) convertView.findViewById(R.id.txtDays);
                TextView txtTime = (TextView) convertView.findViewById(R.id.txtTime);
                txtTime.setTextSize((float) 13.25);
                txtTime.setTextColor(getResources().getColor(R.color.mgray));
                CommonUtilities.setFontFamily(mActivity, txtTime, CommonUtilities.Avenir_Heavy);
                txtDays.setTextSize((float) 12.25);
                txtDays.setTextColor(getResources().getColor(R.color.day));
                CommonUtilities.setFontFamily(mActivity, txtDays, CommonUtilities.AvenirLTStd_Medium);
                txtDays.setText(array[i]);
                if (day == 1 && i == 6) {
                    txtDays.setBackgroundResource(R.drawable.square_text_parrot);
                    txtDays.setTextColor(Color.parseColor("#73C541"));
                } else if (model.response.pub_profile.days_list.get(i).day.equals(String.valueOf(day - 1))) {
                    txtDays.setBackgroundResource(R.drawable.square_text_parrot);
                    txtDays.setTextColor(Color.parseColor("#73C541"));
                }
                if (model.response.pub_profile.days_list.get(i).is_closed.equals("1")) {
                    txtTime.setText("Closed");
                } else
                    txtTime.setText(model.response.pub_profile.days_list.get(i).from_time + " - " + model.response.pub_profile.days_list.get(i).to_time);
                layoutDays.addView(convertView);
            }
        }

    }

    public void setData() {
        txtPubName.setText(model.response.pub_profile.pub_name + "");

        if (model.response.pub_profile.description != null && model.response.pub_profile.description.equals("")) {
            txtDescription.setVisibility(View.GONE);
            txtReadMore.setVisibility(View.GONE);
        } else if (model.response.pub_profile.description != null) {
            txtDescription.setText(model.response.pub_profile.description + "");
            txtDescription.setVisibility(View.VISIBLE);
            if (txtDescription.getText().toString().length() > 180)
                txtReadMore.setVisibility(View.VISIBLE);
            else
                txtReadMore.setVisibility(View.GONE);
        }

        txtAddress.setText(model.response.pub_profile.address + "");

        if (model.response.pub_profile.station.equals(""))
            layoutStation.setVisibility(View.GONE);
        else
            txtStation.setText(model.response.pub_profile.station + "");
        String main = "";
        if (model.response.pub_profile.phone.equals(""))
            layoutNumber.setVisibility(View.GONE);
        else
            main = model.response.pub_profile.phone;

        if (main.length() >= 10) {
            if (main.contains(" "))
                main = main.replaceAll(" ", "");

            String firstthree = main.substring(0, 3);
            String secthree = main.substring(3, 6);
            String lastfour = main.substring(6, main.length());
            txtNumber.setText(firstthree + " " + secthree + " " + lastfour);
        } else {
            txtNumber.setText(model.response.pub_profile.phone);
        }

        ratingBar1.setVisibility(View.VISIBLE);
        ratingBar1.setRating(Float.parseFloat(model.response.pub_profile.rating));

        if (mMap != null) {
            if (!model.response.pub_profile.latitude.equals("") && !model.response.pub_profile.longitude.equals("")) {

                LatLng sourceLocation = new LatLng(Double.parseDouble(model.response.pub_profile.latitude), Double.parseDouble(model.response.pub_profile.longitude));

                CameraPosition newCamPos = new CameraPosition(sourceLocation,
                        14,
                        mMap.getCameraPosition().tilt, //use old tilt
                        mMap.getCameraPosition().bearing);

//                mMap.moveCamera(CameraUpdateFactory.newLatLng(sourceLocation));
                mMap.addMarker(new MarkerOptions().position(sourceLocation));
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 2000, null);
//                mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
            } else {
            }
        }

        if (!model.response.pub_profile.distans.equals("") && !model.response.pub_profile.distans.equals("0"))
            txtdistance.setText(CommonUtilities.doRound(Double.parseDouble(model.response.pub_profile.distans)) + " miles");

        if (model.response.pub_profile.status == 1) {
            txtStatus.setText("Open");
            txtOpen.setText("Now Open");
        } else if (model.response.pub_profile.status == 0) {
            txtStatus.setText("Closed");
            txtOpen.setText("Now Closed");
        }

        if (model != null) {
//            changed on 28-jan-2019
            Picasso.get()
                    .load(CommonUtilities.Gr8niteoutURL + CommonUtilities.Pub_Thumb_URL + model.response.pub_profile.thumb_image)
                    .error(R.mipmap.img_placeholder).placeholder(R.mipmap.img_placeholder) // optional
                    .into(imgThumb, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                        }

                    });

            boolean test = false;
            if (test)
                txtBuyCredits.setText(creditNotAvailableMsg);
        }


    }

    private void setUiPageViewController() {

        dotsCount = adapter.getCount();
        dots = new ImageView[0];
        dots = new ImageView[dotsCount];
        viewPagerCountDots.removeAllViews();
        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(getActivity());
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
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.txtReadMore) {
            if (txtDescription.isExpanded()) {
                txtDescription.collapse();
                txtReadMore.setText("Read more");
            } else {
                txtDescription.expand();
                txtReadMore.setText("Read less");
            }
        } else if (id == R.id.txtAmenities) {
            if (txtAmenities.getText().toString().equals("See All Amenities")) {
                setFeatures(model.response.pub_profile.feature_list.size());
                txtAmenities.setText("See Less Amenities");
            } else {
                setFeatures(4);
                txtAmenities.setText("See All Amenities");
            }
        } else if (id == R.id.txtNumber) {
            if (!checkCallPermission()) {
                requestCall();
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + model.response.pub_profile.phone));
                startActivity(callIntent);
            }
        } else if (id == R.id.layoutAmenities) {
            if (txtAmenities.getText().toString().equals("See All Amenities")) {
                setFeatures(model.response.pub_profile.feature_list.size());
                txtAmenities.setText("See Less Amenities");
            } else {
                setFeatures(4);
                scroll.post(() -> scroll.scrollTo(0, scroll.getTop()));
                txtAmenities.setText("See All Amenities");
            }
        } else if (id == R.id.img_checkin) {
            if (userid.equals("")) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(mActivity);
                builder1.setTitle(R.string.app_name);
                builder1.setIcon(R.mipmap.app_icon);
                builder1.setMessage("Please login to check-in");
                builder1.setCancelable(true);
                builder1.setPositiveButton("LOGIN", (dialog, which) -> {
                    dialog.cancel();
                    Intent i = new Intent(mActivity, SignupLogin.class);
                    i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_comment);
                    startActivityForResult(i, 111);
                });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            } else {
                callCheckIn();
            }
        }
    }


//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.txtReadMore:
//                if (txtDescription.isExpanded()) {
//                    txtDescription.collapse();
//                    txtReadMore.setText("Read more");
//                } else {
//                    txtDescription.expand();
//                    txtReadMore.setText("Read less");
//                }
//                break;
//            case R.id.txtAmenities:
//                if (txtAmenities.getText().toString().equals("See All Amenities")) {
//                    setFeatures(model.response.pub_profile.feature_list.size());
//                    txtAmenities.setText("See Less Amenities");
//                } else {
//                    setFeatures(4);
//                    txtAmenities.setText("See All Amenities");
//                }
//                break;
//            case R.id.txtNumber:
//                if (!checkCallPermission())
//                    requestCall();
//                else {
//                    Intent callIntent = new Intent(Intent.ACTION_CALL);
//                    callIntent.setData(Uri.parse("tel:" + model.response.pub_profile.phone));
//                    startActivity(callIntent);
//                }
//                break;
//            case R.id.layoutAmenities:
//                if (txtAmenities.getText().toString().equals("See All Amenities")) {
//                    setFeatures(model.response.pub_profile.feature_list.size());
//                    txtAmenities.setText("See Less Amenities");
//                } else {
//                    setFeatures(4);
//                    scroll.post(new Runnable() {
//                        public void run() {
//                            scroll.scrollTo(0, scroll.getTop());
//                        }
//                    });
//                    txtAmenities.setText("See All Amenities");
//                }
//                break;
//
//            //checkin feature
//            case R.id.img_checkin:
//                if (userid.equals("")) {
//                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mActivity);
//                    builder1.setTitle(R.string.app_name);
//                    builder1.setIcon(R.mipmap.app_icon);
//                    builder1.setMessage("Please login to check-in");
//                    builder1.setCancelable(true);
//                    builder1.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                            Intent i = new Intent(mActivity, SignupLogin.class);
//                            i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_comment);
//                            startActivityForResult(i, 111);
//                        }
//                    });
//
//                    AlertDialog alert11 = builder1.create();
//                    alert11.show();
//                } else {
//                    //call check-in service
//                    callCheckIn();
//                }
//                break;
//        }
//    }

    private void callCheckIn() {

        Map<String, String> params = new HashMap<>();
        params.put(CommonUtilities.key_pub_id, pub_id);
        params.put(CommonUtilities.key_user_id, userid);

        ServerAccess.getResponse(mActivity, CommonUtilities.key_chcek_in, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
//                {"response":{"status":"Success","code":"SUC001","checkin_user":{"success":"You are checked in successfully."}}}
                CheckinResponse mResponse = new CheckinResponse().getModel(result);

                if (mResponse != null) {
                    if (CommonUtilities.key_Success.equals(mResponse.response.status)) {
                        CommonUtilities.ShowToast(mActivity, mResponse.response.checkin_user.success);
                    } else {
                        CommonUtilities.alertdialog(mActivity, mResponse.response.msg);
                    }
                } else {
                    CommonUtilities.alertdialog(mActivity, "Something went wrong. Please try again");
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    //chekin feature
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 111 && resultCode == RESULT_OK) {
            if (getArguments() != null && getArguments().containsKey(CommonUtilities.key_flag)) {
                MainActivity.act.setDrawerItem();
                MainActivity.act.mDrawerList.setItemChecked(1, true);
            } else {
                MainActivity.act.setDrawerItem();
//            MainActivity.act.adapter.notifyDataSetChanged();
                MainActivity.act.mDrawerList.setItemChecked(0, true);
                userid = CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId);
                FragmentManager fm = ((FragmentActivity) MainActivity.act).getSupportFragmentManager();
                HomeFragment currentFragment = (HomeFragment) fm.findFragmentByTag("Home");
                currentFragment.setDetails();
            }

        }
    }

    private void requestCall() {
        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CALL_PHONE}, 21);
    }

    private boolean checkCallPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 21:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + model.response.pub_profile.phone));
                    startActivity(callIntent);
                } else {
                }
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (PubActivity) activity;

    }

    @Override
    public void onPause() {
        super.onPause();
        if (timer != null)
            timer.cancel();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_reset).setVisible(false);
      /*  menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_cancel).setVisible(false);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Intent share = new Intent();
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, model.response.pub_profile.url);
            share.setAction(Intent.ACTION_SEND);
            startActivity(Intent.createChooser(share, "Share link!"));
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        CommonUtilities.isBack_credit = false;
    }

    @Override
    public void onStart() {
        super.onStart();

        ((MyApplication) mActivity.getApplication()).getDefaultTracker().setScreenName("Pub Profile Screen");
        ((MyApplication) mActivity.getApplication()).getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    if (page > dotsCount) {
                        pagerBanner.setCurrentItem(0);
                        page = 0;
                    } else {
                        pagerBanner.setCurrentItem(page++);
                    }
                }
            });
        }
    }

    public class ViewPagerAdapter extends PagerAdapter {

        private Context mContext;
        private ArrayList<String> listBanner = new ArrayList<>();

        public ViewPagerAdapter(Context mContext, ArrayList<String> listBanner) {
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
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.banner_image_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);
            final ImageView imgPlaceHolder = (ImageView) itemView.findViewById(R.id.imgPlaceHolder);
            final LinearLayout layoutGray = (LinearLayout) itemView.findViewById(R.id.layoutGray);

            final ProgressBar progress_bar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            progress_bar.setVisibility(View.VISIBLE);

//            changed on 28-jan-2019
            Picasso.get()
                    .load(listBanner.get(position))
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progress_bar.setVisibility(View.GONE);
                            layoutGray.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            imgPlaceHolder.setVisibility(View.VISIBLE);
                            progress_bar.setVisibility(View.GONE);
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
}
