package com.Gr8niteout.signup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.Gr8niteout.R;
import com.Gr8niteout.adapter.CountryListAdapter;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.CountryModel;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.BindView;

public class CountryList extends AppCompatActivity {

//    @BindView(R.id.inputSearch)
    EditText inputSearch;

//    @BindView(R.id.CountryList)
    ListView CountryList;

//    @BindView(R.id.toolbar)
    Toolbar toolbar;
//    @BindView(R.id.toolbar_title)
    TextView toolbar_title;

    CountryListAdapter listadpater;

    CountryModel model;
    String intentCountryName,intentCountryCode;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_countrylist);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        CountryList = (ListView) findViewById(R.id.CountryList);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setFont();

        toolbar_title.setText("Select Country");
        
        // Safely initialize country model with null check
        String countriesData = CommonUtilities.getSecurity_Preference(this, CommonUtilities.pref_Countries);
        if (countriesData != null && !countriesData.isEmpty()) {
            model = new CountryModel().CountryModel(countriesData);
            
            // Check if model and response are valid
            if (model != null && model.response != null && model.response.country_list != null) {
                listadpater = new CountryListAdapter(this, model.response.country_list);
                CountryList.setAdapter(listadpater);
            } else {
                Log.e("CountryList", "Failed to parse country model data");
                // Try to load from API as fallback
                loadCountriesFromAPI();
            }
        } else {
            Log.e("CountryList", "Countries data not available in preferences, loading from API");
            // Load from API as fallback
            loadCountriesFromAPI();
        }

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = inputSearch.getText().toString().toLowerCase(Locale.getDefault());
                listadpater.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

        CountryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Add null safety check
                if (model != null && model.response != null && model.response.country_list != null 
                        && position < model.response.country_list.size()) {
                    intentCountryName = model.response.country_list.get(position).country_name;
                    intentCountryCode = model.response.country_list.get(position).numcode;
                    Log.i("CODE", intentCountryCode);
                    Log.i("NAME", intentCountryName);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("code", intentCountryCode);
                    returnIntent.putExtra("name", intentCountryName);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    Log.e("CountryList", "Invalid country selection");
                    CommonUtilities.ShowToast(CountryList.this, "Failed to select country");
                }
            }
        });

    }
    
    private void loadCountriesFromAPI() {
        Map<String, String> params = new HashMap<String, String>();
        
        ServerAccess.getResponse(CountryList.this, CommonUtilities.key_countries, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                model = new CountryModel().CountryModel(result);
                if (model != null && model.response != null) {
                    if (model.response.status != null && model.response.status.equals(CommonUtilities.key_Success)) {
                        // Save to preferences for future use
                        CommonUtilities.setSecurity_Preference(CountryList.this, CommonUtilities.pref_Countries, result);
                        
                        // Check if country list is valid
                        if (model.response.country_list != null && model.response.country_list.size() > 0) {
                            listadpater = new CountryListAdapter(CountryList.this, model.response.country_list);
                            CountryList.setAdapter(listadpater);
                            Log.d("CountryList", "Successfully loaded " + model.response.country_list.size() + " countries from API");
                        } else {
                            Log.e("CountryList", "Country list is empty");
                            CommonUtilities.ShowToast(CountryList.this, "No countries available");
                            finish();
                        }
                    } else {
                        Log.e("CountryList", "API returned error status");
                        CommonUtilities.ShowToast(CountryList.this, model.response.msg != null ? model.response.msg : "Failed to load countries");
                        finish();
                    }
                } else {
                    Log.e("CountryList", "Failed to parse country data from API");
                    CommonUtilities.ShowToast(CountryList.this, "Failed to load country list. Please try again.");
                    finish();
                }
            }

            @Override
            public void onError(String error) {
                Log.e("CountryList", "API error: " + error);
                CommonUtilities.ShowToast(CountryList.this, "Network error. Please check your connection.");
                finish();
            }
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();// close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    public void setFont() {
        CommonUtilities.setFontFamily(CountryList.this, toolbar_title, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(CountryList.this, inputSearch, CommonUtilities.AvenirLTStd_Medium);
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
