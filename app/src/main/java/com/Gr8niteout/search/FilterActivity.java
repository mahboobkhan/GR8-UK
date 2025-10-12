package com.Gr8niteout.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.GPSTracker;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.FeatureModel;
import com.Gr8niteout.model.FilterCountModel;
//import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
//import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.gms.analytics.GoogleAnalytics;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.BindView;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener ,GoogleApiClient.ConnectionCallbacks{
    FeatureModel model;
    FilterCountModel model1;
//    @BindView(R.id.layoutFeatures)
    LinearLayout layoutFeatures;
//    @BindView(R.id.txtShowall)
    TextView txtShowall;
//    @BindView(R.id.txtNewest)
    TextView txtNewest;
//    @BindView(R.id.txtDistance)
    TextView txtDistance;
//    @BindView(R.id.txtPrice)
    TextView txtPrice;
//    @BindView(R.id.txtOpeningTImes)
    TextView txtOpeningTImes;
//    @BindView(R.id.txtDay)
    TextView txtDay;
//    @BindView(R.id.txtTime)
    TextView txtTime;
//    @BindView(R.id.txtPubFeatures)
    TextView txtPubFeatures;
//    @BindView(R.id.txtSortBy)
    TextView txtSortBy;
//    @BindView(R.id.txtRating)
    TextView txtRating;
//    @BindView(R.id.txtMonday)
    TextView txtMonday;
//    @BindView(R.id.txtTue)
    TextView txtTue;
//    @BindView(R.id.txtWed)
    TextView txtWed;
//    @BindView(R.id.txtThur)
    TextView txtThur;
//    @BindView(R.id.txtFri)
    TextView txtFri;
//    @BindView(R.id.txtSat)
    TextView txtSat;
//    @BindView(R.id.txtSun)
    TextView txtSun;
//    @BindView(R.id.btnPubs)
    Button btnPubs;
//    @BindView(R.id.textMin)
    TextView textMin;
//    @BindView(R.id.textMax)
    TextView textMax;
//    @BindView(R.id.rangeSeekbar)
//    CrystalRangeSeekbar rangeSeekbar;
//    @BindView(R.id.txtTitle)
    TextView txtTitle;
//    @BindView(R.id.layoutNewest)
    LinearLayout layoutNewest;
//    @BindView(R.id.layoutDis)
    LinearLayout layoutDis;
//    @BindView(R.id.layoutDri)
    LinearLayout layoutDri;
//    @BindView(R.id.layoutRating)
    LinearLayout layoutRating;

    ArrayList<String> features = new ArrayList<>();
    String feature = "", sortby = "", day = "", open_time = "", close_time = "";
    public int i;
    int minTime, maxTime;
    GPSTracker loc;
    String lat, longi;
    final static int REQUEST_LOCATION = 200;
    boolean monFlag = false, tueFlag = false, wedFlag = false, thuFlag = false, friFlag = false, satFlag = false, sunFlag = false;
    boolean newFlag = false, disFlag = false, ratFlag = false, priFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setContentInsetStartWithNavigation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.close);
        getSupportActionBar().setTitle(null);
        loc = new GPSTracker(FilterActivity.this);

        layoutFeatures = (LinearLayout) findViewById(R.id.layoutFeatures);
        txtShowall = (TextView) findViewById(R.id.txtShowall);
        txtNewest = (TextView) findViewById(R.id.txtNewest);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        txtOpeningTImes = (TextView) findViewById(R.id.txtOpeningTImes);
        txtDay = (TextView) findViewById(R.id.txtDay);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtPubFeatures = (TextView) findViewById(R.id.txtPubFeatures);
        txtSortBy = (TextView) findViewById(R.id.txtSortBy);
        txtRating = (TextView) findViewById(R.id.txtRating);
        txtMonday = (TextView) findViewById(R.id.txtMonday);
        txtTue = (TextView) findViewById(R.id.txtTue);
        txtWed = (TextView) findViewById(R.id.txtWed);
        txtThur = (TextView) findViewById(R.id.txtThur);
        txtFri = (TextView) findViewById(R.id.txtFri);
        txtSat = (TextView) findViewById(R.id.txtSat);
        txtSun = (TextView) findViewById(R.id.txtSun);
        btnPubs = (Button) findViewById(R.id.btnPubs);
        textMin = (TextView) findViewById(R.id.textMin);
        textMax = (TextView) findViewById(R.id.textMax);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        layoutNewest = (LinearLayout) findViewById(R.id.layoutNewest);
        layoutDis = (LinearLayout) findViewById(R.id.layoutDis);
        layoutDri = (LinearLayout) findViewById(R.id.layoutDri);
        layoutRating = (LinearLayout) findViewById(R.id.layoutRating);





        txtTitle.setText("Filters");

        txtShowall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtShowall.getText().toString().equals("Show all")) {
                    setFeatures(model.response.features.feature_list.size());
                    txtShowall.setText("Show less");
                } else {
                    setFeatures(5);
                    txtShowall.setText("Show all");
                }
            }
        });

        txtNewest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newFlag)
                {
                    newFlag = false;
                    setSorting(txtNewest, "false");
                    sortby = "";
                }
                else
                {
                    newFlag = true;disFlag = false;priFlag = false;ratFlag = false;
                    setSorting(txtNewest, "true");
                    sortby = "newest";
                }
                getFilterCount();
            }
        });
        txtDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    enableLoc();
                }else{
                    if(disFlag)
                    {
                        disFlag = false;
                        setSorting(txtDistance, "false");
                        sortby = "";
                    }
                    else
                    {
                        disFlag = true; newFlag = false;priFlag = false;ratFlag = false;
                        setSorting(txtDistance, "true");
                        sortby = "distance";
                    }
                    getFilterCount();
                }
            }
        });
        txtRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ratFlag)
                {
                    ratFlag = false;
                    setSorting(txtRating, "false");
                    sortby = "";
                }
                else
                {
                    ratFlag = true; newFlag = false;disFlag = false;priFlag = false;
                    setSorting(txtRating, "true");
                    sortby = "rating";
                }
                getFilterCount();
            }
        });
        txtPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(priFlag)
                {
                    priFlag = false;
                    setSorting(txtPrice, "false");
                    sortby = "";
                }
                else
                {
                    priFlag = true; newFlag = false;disFlag = false;ratFlag = false;
                    setSorting(txtPrice, "true");
                    sortby = "price";
                }
                getFilterCount();
            }
        });
        txtMonday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("enter",txtMonday.getCurrentTextColor()+"");
                if(monFlag)
                {
                    monFlag = false;
                    setColor(txtMonday, "false");
                    Log.e("if",monFlag+"");
                    day = "";
                }
                else
                {
                    monFlag = true;tueFlag = false;wedFlag=false;thuFlag=false;friFlag=false;satFlag=false;sunFlag=false;
                    setColor(txtMonday, "true");
                    Log.e("else",monFlag+"");
                    day = "1";
                }
                getFilterCount();
            }
        });
        txtTue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tueFlag)
                {
                    tueFlag = false;
                    setColor(txtTue, "false");
                    Log.e("if",tueFlag+"");
                    day = "";
                }
                else
                {
                    tueFlag = true;monFlag=false;wedFlag=false;thuFlag=false;friFlag=false;satFlag=false;sunFlag=false;
                    setColor(txtTue, "true");
                    Log.e("else",tueFlag+"");
                    day = "2";
                }
                getFilterCount();
            }
        });

        txtWed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wedFlag)
                {
                    wedFlag = false;
                    setColor(txtWed, "false");
                    Log.e("if",wedFlag+"");
                    day = "";
                }
                else
                {
                    wedFlag = true; tueFlag = false;monFlag=false;thuFlag=false;friFlag=false;satFlag=false;sunFlag=false;
                    setColor(txtWed, "true");
                    Log.e("else",wedFlag+"");
                    day = "3";
                }
                getFilterCount();
            }
        });

        txtThur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(thuFlag)
                {
                    thuFlag = false;
                    setColor(txtThur, "false");
                    day = "";
                }
                else
                {
                    thuFlag = true; tueFlag = false;monFlag=false;wedFlag=false;friFlag=false;satFlag=false;sunFlag=false;
                    setColor(txtThur, "true");
                    day = "4";
                }
                getFilterCount();
            }
        });
        txtFri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(friFlag)
                {
                    friFlag = false;
                    setColor(txtFri, "false");
                    day = "";
                }
                else
                {
                    friFlag = true; tueFlag = false;monFlag=false;wedFlag=false;thuFlag=false;satFlag=false;sunFlag=false;
                    setColor(txtFri, "true");
                    day = "5";
                }
                getFilterCount();
            }
        });


        txtSat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(satFlag)
                {
                    satFlag = false;
                    setColor(txtSat, "false");
                    day = "";
                }
                else
                {
                    satFlag = true; tueFlag = false;monFlag=false;wedFlag=false;thuFlag=false;friFlag=false;sunFlag=false;
                    setColor(txtSat, "true");
                    day = "6";
                }
                getFilterCount();
            }
        });
        txtSun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sunFlag)
                {
                    sunFlag = false;
                    setColor(txtSun, "false");
                    day = "";
                }
                else
                {
                    sunFlag = true; tueFlag = false;monFlag=false;wedFlag=false;thuFlag=false;friFlag=false;satFlag=false;
                    setColor(txtSun, "true");
                    day = "7";
                }
                getFilterCount();
            }
        });
        layoutNewest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        layoutDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    enableLoc();
                }else{
                    if(disFlag)
                    {
                        disFlag = false;
                        setSorting(txtDistance, "false");
                        sortby = "";
                    }
                    else
                    {
                        disFlag = true; newFlag = false;priFlag = false;ratFlag = false;
                        setSorting(txtDistance, "true");
                        sortby = "distance";
                    }
                    getFilterCount();
                }
            }
        });
        layoutDri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(priFlag)
                {
                    priFlag = false;
                    setSorting(txtPrice, "false");
                    sortby = "";
                }
                else
                {
                    priFlag = true; newFlag = false;disFlag = false;ratFlag = false;
                    setSorting(txtPrice, "true");
                    sortby = "price";
                }
                getFilterCount();
            }
        });
        layoutRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ratFlag)
                {
                    ratFlag = false;
                    setSorting(txtRating, "false");
                    sortby = "";
                }
                else
                {
                    ratFlag = true; priFlag = false; newFlag = false;disFlag = false;
                    setSorting(txtRating, "true");
                    sortby = "rating";
                }
                getFilterCount();
            }
        });
        btnPubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!btnPubs.getText().toString().equals("0 Pubs Found"))
                {
//                    Intent intent = new Intent(FilterActivity.this, PubRecyclerActivity.class);
                    Intent intent = new Intent(FilterActivity.this, PubListActivity.class);
                    intent.putExtra(CommonUtilities.key_country_id, getIntent().getExtras().getString(CommonUtilities.key_country_id));
                    intent.putExtra(CommonUtilities.key_state_id, getIntent().getExtras().getString(CommonUtilities.key_state_id));
                    intent.putExtra(CommonUtilities.key_city, getIntent().getExtras().getString(CommonUtilities.key_city));
                    intent.putExtra(CommonUtilities.key_pub_event_name, getIntent().getExtras().getString(CommonUtilities.key_pub_event_name));
                    intent.putExtra("country", getIntent().getExtras().getString("country"));
                    intent.putExtra("state", getIntent().getExtras().getString("state"));
                    intent.putExtra(CommonUtilities.key_feature_id, feature);
                    intent.putExtra(CommonUtilities.key_sort_by, sortby);
                    intent.putExtra(CommonUtilities.key_day, day);
                    intent.putExtra(CommonUtilities.key_open_time, textMin.getText().toString());
                    intent.putExtra(CommonUtilities.key_close_time, textMax.getText().toString());
                    intent.putExtra(CommonUtilities.key_close_time, textMax.getText().toString());
                    intent.putExtra(CommonUtilities.key_latitude, lat);
                    intent.putExtra(CommonUtilities.key_longitude, longi);
                    startActivity(intent);
                }
            }
        });

        textMin.setText("12 am");
        textMax.setText("12 am");

//        rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
//            @Override
//            public void valueChanged(Number minValue, Number maxValue) {
//                minTime = Integer.parseInt(String.valueOf(minValue));
//                maxTime = Integer.parseInt(String.valueOf(maxValue));
//                if (minTime < 12) {
//                    if (minTime == 0)
//                        minTime = 12;
//                    textMin.setText(String.valueOf(minTime) + " am");
//                } else if (minTime == 12 || maxTime == 12) {
//                    textMin.setText(String.valueOf(minTime) + " noon");
//
//                } else if (minTime > 12) {
//                    textMin.setText((minTime - 12) + " pm");
//                }
//                if (maxTime > 12) {
//                    if (maxTime == 24)
//                        textMax.setText("12 am");
//                    else {
//                        maxTime = maxTime - 12;
//                        textMax.setText(String.valueOf(maxTime) + " pm");
//                    }
//                } else if (maxTime == 12) {
//                    textMax.setText(String.valueOf(maxTime) + " noon");
//                } else if (maxTime < 12) {
//                    textMax.setText((maxTime) + " am");
//                }
//            }
//        });
//
//        rangeSeekbar.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View view, MotionEvent event) {
//                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
//                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
//                    getFilterCount();
//                }
//                return false;
//            }
//        });

        setFont();
        getFeatures();
    }

    public void setFont() {
        CommonUtilities.setFontFamily(FilterActivity.this, txtShowall, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(FilterActivity.this, txtOpeningTImes, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(FilterActivity.this, txtSortBy, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(FilterActivity.this, txtDay, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(FilterActivity.this, txtTime, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(FilterActivity.this, txtMonday, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(FilterActivity.this, txtTue, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(FilterActivity.this, txtWed, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(FilterActivity.this, txtThur, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(FilterActivity.this, txtFri, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(FilterActivity.this, txtSat, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(FilterActivity.this, txtSun, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(FilterActivity.this, textMax, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(FilterActivity.this, textMin, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(FilterActivity.this, txtNewest, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(FilterActivity.this, txtDistance, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(FilterActivity.this, txtPrice, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(FilterActivity.this, txtRating, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(FilterActivity.this, txtPubFeatures, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(FilterActivity.this, btnPubs, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(FilterActivity.this, btnPubs, CommonUtilities.Avenir_Heavy);
    }

    public void setFeatures(int size) {

        layoutFeatures.removeAllViews();
        LayoutInflater vi = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        for (i = 0; i < size; i++) {
            View convertView = vi.inflate(R.layout.feature_item, null);
            convertView.setId(i);
            TextView txtFeature = (TextView) convertView.findViewById(R.id.txtFeature);
            txtFeature.setTextSize(12);
            txtFeature.setTextColor(getResources().getColor(R.color.mgray));
            CommonUtilities.setFontFamily(FilterActivity.this, txtFeature, CommonUtilities.AvenirLTStd_Medium);
            final CheckBox chkFeature = (CheckBox) convertView.findViewById(R.id.chkFeature);
            if (features.contains(model.response.features.feature_list.get(i).id))
                chkFeature.setChecked(true);
            chkFeature.setId(Integer.parseInt(model.response.features.feature_list.get(i).id));
            txtFeature.setText(model.response.features.feature_list.get(i).title);
            layoutFeatures.addView(convertView);

            chkFeature.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        features.add(chkFeature.getId() + "");
                    } else {
                        for (int j = 0; j < features.size(); j++) {
                            if (features.get(j).equals(chkFeature.getId() + ""))
                                features.remove(j);
                        }
                    }
                    feature = "";
                    for (int j = 0; j < features.size(); j++) {
                        if (j == 0)
                            feature = features.get(j);
                        else
                            feature = feature + "," + features.get(j);
                    }
                    getFilterCount();
                }
            });
        }
    }

    public void getFeatures() {
        Map<String, String> params = new HashMap<String, String>();
        ServerAccess.getResponse(FilterActivity.this, CommonUtilities.key_features, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                model = new FeatureModel().FeatureModel(result);
                if (model != null) {
                    if (model.response.status.equals(CommonUtilities.key_Success)) {
                        setFeatures(5);
                    } else {
                        CommonUtilities.alertdialog(FilterActivity.this, model.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void  getFilterCount() {

        Map<String, String> params = new HashMap<String, String>();
        params.put(CommonUtilities.key_feature_id, feature);
        params.put(CommonUtilities.key_sort_by, sortby);
        params.put(CommonUtilities.key_day, day);
        params.put(CommonUtilities.key_open_time, textMin.getText().toString().replace(" ",""));
        params.put(CommonUtilities.key_close_time, textMax.getText().toString().replace(" ",""));
        params.put(CommonUtilities.key_country_id, getIntent().getExtras().getString(CommonUtilities.key_country_id));
        params.put(CommonUtilities.key_state_id, getIntent().getExtras().getString(CommonUtilities.key_state_id));
        params.put(CommonUtilities.key_city, getIntent().getExtras().getString(CommonUtilities.key_city));
        params.put(CommonUtilities.key_pub_event_name, getIntent().getExtras().getString(CommonUtilities.key_pub_event_name));
        params.put(CommonUtilities.key_latitude, getIntent().getExtras().getString(CommonUtilities.key_latitude));
        params.put(CommonUtilities.key_longitude, getIntent().getExtras().getString(CommonUtilities.key_longitude));
        ServerAccess.getResponse(FilterActivity.this, CommonUtilities.key_filter_count, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                model1 = new FilterCountModel().FilterCountModel(result);
                if (model1 != null) {
                    if (model1.response.status.equals(CommonUtilities.key_Success)) {
                        if (model1.response.pub_find_count.counts > 0) {
                            btnPubs.setVisibility(View.VISIBLE);
                            if (model1.response.pub_find_count.counts == 1)
                                btnPubs.setText("View " + model1.response.pub_find_count.counts + " pub");
                            else
                                btnPubs.setText("View " + model1.response.pub_find_count.counts + " pubs");
                        } else {
                            btnPubs.setText("0 Pubs Found");

                        }
                    } else {
                        btnPubs.setVisibility(View.VISIBLE);
                        btnPubs.setText("0 Pubs Found");
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
        menu.findItem(R.id.action_search_white).setVisible(false);
        menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_setting).setVisible(false);
        menu.findItem(R.id.action_transaction_history).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
//            case R.id.action_reset:
//                features.clear();
//                setFeatures(5);
//                day = "";
//                sortby = "";
//                feature = "";
//                open_time = "";
//                close_time = "";
//                textMin.setText("12 am");
//                textMax.setText("12 am");
//                setColor(txtMonday, "false");
//                setSorting(txtDistance, "false");
//                rangeSeekbar.setMinValue(0);
//                rangeSeekbar.setMaxValue(24);
//                rangeSeekbar.setMinStartValue(0);
//                rangeSeekbar.setMaxStartValue(24);
//                rangeSeekbar.setGap(1);
//                rangeSeekbar.apply();


//                btnPubs.setVisibility(View.GONE);
//                txtShowall.setText("Show all");
//                monFlag = false;tueFlag = false;wedFlag=false;thuFlag=false;friFlag=false;satFlag=false;sunFlag=false;
//                ratFlag = false; newFlag = false;disFlag = false;priFlag = false;
//                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean flag1 = true;
        switch (v.getId()) {
//            case R.id.txtShowall:
//                if (txtShowall.getText().toString().equals("Show all")) {
//                    setFeatures(model.response.features.feature_list.size());
//                    txtShowall.setText("Show less");
//                } else {
//                    setFeatures(5);
//                    txtShowall.setText("Show all");
//                }
//                break;
//            case R.id.txtNewest:
//                if(newFlag)
//                {
//                    newFlag = false;
//                    setSorting(txtNewest, "false");
//                    sortby = "";
//                }
//                else
//                {
//                    newFlag = true;disFlag = false;priFlag = false;ratFlag = false;
//                    setSorting(txtNewest, "true");
//                    sortby = "newest";
//                }
//                getFilterCount();
//                break;
//            case R.id.txtDistance:
//
//                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    enableLoc();
//                }else{
//                    if(disFlag)
//                    {
//                        disFlag = false;
//                        setSorting(txtDistance, "false");
//                        sortby = "";
//                    }
//                    else
//                    {
//                        disFlag = true; newFlag = false;priFlag = false;ratFlag = false;
//                        setSorting(txtDistance, "true");
//                        sortby = "distance";
//                    }
//                    getFilterCount();
//                }
//                break;
//            case R.id.txtPrice:
//                if(priFlag)
//                {
//                    priFlag = false;
//                    setSorting(txtPrice, "false");
//                    sortby = "";
//                }
//                else
//                {
//                    priFlag = true; newFlag = false;disFlag = false;ratFlag = false;
//                    setSorting(txtPrice, "true");
//                    sortby = "price";
//                }
//                getFilterCount();
//                break;
//            case R.id.txtRating:
//                if(ratFlag)
//                {
//                    ratFlag = false;
//                    setSorting(txtRating, "false");
//                    sortby = "";
//                }
//                else
//                {
//                    ratFlag = true; newFlag = false;disFlag = false;priFlag = false;
//                    setSorting(txtRating, "true");
//                    sortby = "rating";
//                }
//                getFilterCount();
//                break;
//            case R.id.txtMonday:
//                Log.e("enter",txtMonday.getCurrentTextColor()+"");
//                if(monFlag)
//                {
//                    monFlag = false;
//                    setColor(txtMonday, "false");
//                    Log.e("if",monFlag+"");
//                    day = "";
//                }
//                 else
//                {
//                    monFlag = true;tueFlag = false;wedFlag=false;thuFlag=false;friFlag=false;satFlag=false;sunFlag=false;
//                    setColor(txtMonday, "true");
//                    Log.e("else",monFlag+"");
//                    day = "1";
//                }
//                getFilterCount();
//                break;
//            case R.id.txtTue:
//                if(tueFlag)
//                {
//                    tueFlag = false;
//                    setColor(txtTue, "false");
//                    Log.e("if",tueFlag+"");
//                    day = "";
//                }
//                else
//                {
//                    tueFlag = true;monFlag=false;wedFlag=false;thuFlag=false;friFlag=false;satFlag=false;sunFlag=false;
//                    setColor(txtTue, "true");
//                    Log.e("else",tueFlag+"");
//                    day = "2";
//                }
//                getFilterCount();
//                break;
//            case R.id.txtWed:
//                if(wedFlag)
//                {
//                    wedFlag = false;
//                    setColor(txtWed, "false");
//                    Log.e("if",wedFlag+"");
//                    day = "";
//                }
//                else
//                {
//                    wedFlag = true; tueFlag = false;monFlag=false;thuFlag=false;friFlag=false;satFlag=false;sunFlag=false;
//                    setColor(txtWed, "true");
//                    Log.e("else",wedFlag+"");
//                    day = "3";
//                }
//                getFilterCount();
//                break;
//            case R.id.txtThur:
//                if(thuFlag)
//                {
//                    thuFlag = false;
//                    setColor(txtThur, "false");
//                    day = "";
//                }
//                else
//                {
//                    thuFlag = true; tueFlag = false;monFlag=false;wedFlag=false;friFlag=false;satFlag=false;sunFlag=false;
//                    setColor(txtThur, "true");
//                    day = "4";
//                }
//                getFilterCount();
//                break;
//            case R.id.txtFri:
//                if(friFlag)
//                {
//                    friFlag = false;
//                    setColor(txtFri, "false");
//                    day = "";
//                }
//                else
//                {
//                    friFlag = true; tueFlag = false;monFlag=false;wedFlag=false;thuFlag=false;satFlag=false;sunFlag=false;
//                    setColor(txtFri, "true");
//                    day = "5";
//                }
//                getFilterCount();
//                break;
//            case R.id.txtSat:
//                if(satFlag)
//                {
//                    satFlag = false;
//                    setColor(txtSat, "false");
//                    day = "";
//                }
//                else
//                {
//                    satFlag = true; tueFlag = false;monFlag=false;wedFlag=false;thuFlag=false;friFlag=false;sunFlag=false;
//                    setColor(txtSat, "true");
//                    day = "6";
//                }
//                getFilterCount();
//                break;
//            case R.id.txtSun:
//                if(sunFlag)
//                {
//                    sunFlag = false;
//                    setColor(txtSun, "false");
//                    day = "";
//                }
//                else
//                {
//                    sunFlag = true; tueFlag = false;monFlag=false;wedFlag=false;thuFlag=false;friFlag=false;satFlag=false;
//                    setColor(txtSun, "true");
//                    day = "7";
//                }
//                getFilterCount();
//                break;
//            case R.id.btnPubs:
//                if(!btnPubs.getText().toString().equals("0 Pubs Found"))
//                {
////                    Intent intent = new Intent(FilterActivity.this, PubRecyclerActivity.class);
//                    Intent intent = new Intent(FilterActivity.this, PubListActivity.class);
//                    intent.putExtra(CommonUtilities.key_country_id, getIntent().getExtras().getString(CommonUtilities.key_country_id));
//                    intent.putExtra(CommonUtilities.key_state_id, getIntent().getExtras().getString(CommonUtilities.key_state_id));
//                    intent.putExtra(CommonUtilities.key_city, getIntent().getExtras().getString(CommonUtilities.key_city));
//                    intent.putExtra(CommonUtilities.key_pub_event_name, getIntent().getExtras().getString(CommonUtilities.key_pub_event_name));
//                    intent.putExtra("country", getIntent().getExtras().getString("country"));
//                    intent.putExtra("state", getIntent().getExtras().getString("state"));
//                    intent.putExtra(CommonUtilities.key_feature_id, feature);
//                    intent.putExtra(CommonUtilities.key_sort_by, sortby);
//                    intent.putExtra(CommonUtilities.key_day, day);
//                    intent.putExtra(CommonUtilities.key_open_time, textMin.getText().toString());
//                    intent.putExtra(CommonUtilities.key_close_time, textMax.getText().toString());
//                    intent.putExtra(CommonUtilities.key_close_time, textMax.getText().toString());
//                    intent.putExtra(CommonUtilities.key_latitude, lat);
//                    intent.putExtra(CommonUtilities.key_longitude, longi);
//                    startActivity(intent);
//                }
//                break;
//            case R.id.layoutNewest:
//                if(newFlag)
//                {
//                    newFlag = false;
//                    setSorting(txtNewest, "false");
//                    sortby = "";
//                }
//                else
//                {
//                    newFlag = true;disFlag = false;priFlag = false;ratFlag = false;
//                    setSorting(txtNewest, "true");
//                    sortby = "newest";
//                }
//                getFilterCount();
//                break;
//            case R.id.layoutDis:
//                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    enableLoc();
//                }else{
//                    if(disFlag)
//                    {
//                        disFlag = false;
//                        setSorting(txtDistance, "false");
//                        sortby = "";
//                    }
//                    else
//                    {
//                        disFlag = true; newFlag = false;priFlag = false;ratFlag = false;
//                        setSorting(txtDistance, "true");
//                        sortby = "distance";
//                    }
//                    getFilterCount();
//                }
//                break;
//            case R.id.layoutDri:
//                if(priFlag)
//                {
//                    priFlag = false;
//                    setSorting(txtPrice, "false");
//                    sortby = "";
//                }
//                else
//                {
//                    priFlag = true; newFlag = false;disFlag = false;ratFlag = false;
//                    setSorting(txtPrice, "true");
//                    sortby = "price";
//                }
//                getFilterCount();
//                break;
//            case R.id.layoutRating:
//                if(ratFlag)
//                {
//                    ratFlag = false;
//                    setSorting(txtRating, "false");
//                    sortby = "";
//                }
//                else
//                {
//                    ratFlag = true; priFlag = false; newFlag = false;disFlag = false;
//                    setSorting(txtRating, "true");
//                    sortby = "rating";
//                }
//                getFilterCount();
//                break;
        }
    }

    public void setSorting(TextView txtview, String flag) {

        txtRating.setTextColor(Color.parseColor("#C5C5C5"));
        txtDistance.setTextColor(Color.parseColor("#C5C5C5"));
        txtPrice.setTextColor(Color.parseColor("#C5C5C5"));
        txtNewest.setTextColor(Color.parseColor("#C5C5C5"));

        if (flag.equals("true")) {
            txtview.setTextColor(Color.parseColor("#73C541"));

        }
        else if(flag.equals("false")){
            txtview.setTextColor(Color.parseColor("#C5C5C5"));

        }
    }

    public boolean setColor(TextView txtview, String flag) {

        txtMonday.setBackgroundResource(R.drawable.square_edit_text);
        txtMonday.setTextColor(Color.parseColor("#C5C5C5"));

        txtTue.setBackgroundResource(R.drawable.square_edit_text);
        txtTue.setTextColor(Color.parseColor("#C5C5C5"));

        txtWed.setBackgroundResource(R.drawable.square_edit_text);
        txtWed.setTextColor(Color.parseColor("#C5C5C5"));

        txtThur.setBackgroundResource(R.drawable.square_edit_text);
        txtThur.setTextColor(Color.parseColor("#C5C5C5"));

        txtFri.setBackgroundResource(R.drawable.square_edit_text);
        txtFri.setTextColor(Color.parseColor("#C5C5C5"));

        txtSat.setBackgroundResource(R.drawable.square_edit_text);
        txtSat.setTextColor(Color.parseColor("#C5C5C5"));

        txtSun.setBackgroundResource(R.drawable.square_edit_text);
        txtSun.setTextColor(Color.parseColor("#C5C5C5"));

        if (flag.equals("true")) {
            txtview.setBackgroundResource(R.drawable.square_text_parrot);
            txtview.setTextColor(Color.parseColor("#73C541"));
            return true;
        } else if(flag.equals("false")) {
            txtview.setBackgroundResource(R.drawable.square_edit_text);
            txtview.setTextColor(Color.parseColor("#C5C5C5"));
            return false;
        }
        else
        {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CommonUtilities.isBack) {
            super.onBackPressed();
        }
    }
    public boolean noLocation() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //  buildAlertMessageNoGps();
            enableLoc();
            return true;
        }else{

        }
        return false;

    }


    private void enableLoc() {

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(FilterActivity.this)
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
                            status.startResolutionForResult(FilterActivity.this, REQUEST_LOCATION);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK:{
                        txtDistance.performClick();
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }


    @Override
    protected void onStart() {
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }
}

