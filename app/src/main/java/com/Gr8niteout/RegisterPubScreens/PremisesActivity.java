package com.Gr8niteout.RegisterPubScreens;

import static com.Gr8niteout.RegisterPubScreens.ValidationCases.params;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.Gr8niteout.R;
import com.Gr8niteout.RegisterPubScreens.PubRegistrationModels.UkStateModel;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.CountryModel;

import java.util.HashMap;
import java.util.Map;

public class  PremisesActivity extends AppCompatActivity {
    Spinner premisesTypeSpinner, currencyPreferenceSpinner, countrySpinner, countryStateSpinner;
    TextView btnBack, btnNext, tvCountry, tvCountryState;
    EditText edtPremisesName, edtTelephone, edtNearestStation, edtPostCode, edtAddressLine1, edtAddressLine2, edtTownCity, edtCountryState;
    Spinner monStartSpinner, monEndSpinner, tueStartSpinner, tueEndSpinner, wedStartSpinner, wedEndSpinner, thuStartSpinner, thuEndSpinner,
            friStartSpinner, friEndSpinner, satStartSpinner, satEndSpinner, sunStartSpinner, sunEndSpinner;
    CheckBox monCheckBox, tueCheckBox, wedCheckBox, thuCheckBox, friCheckBox, satCheckBox, sunCheckBox;

    String[] premisesTypes = new String[]{"Managed", "Free Of Tie", "Tenant"};
    String[] currencyPreferences = new String[]{"GBP (£)", "EUR (€)", "USD ($)"};
    String[] countryList;
//    String[] countryCodeList;
    String[] countryStateList;

    String[] timeList = new String[48];

    String[] tempTime = new String[]{
            "12:00", "12:30", "1:00", "1:30", "2:00", "2:30", "3:00", "3:30", "4:00", "4:30", "5:00", "5:30", "6:00", "6:30",
            "7:00", "7:30", "8:00", "8:30", "9:00", "9:30", "10:00", "10:30", "11:00", "11:30"
    };
    int timeListIndex = 0;

    Spinner[] timeSpinners;
    ValidationCases validation;
    EditText[] listEditext;

    public CountryModel model1;
    public UkStateModel ukStateModel;
//    String countryCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premises);

        initViews();
        getCountries();
        validation = new ValidationCases();

        timeSpinners = new Spinner[]{
                monStartSpinner, monEndSpinner, tueStartSpinner, tueEndSpinner, wedStartSpinner, wedEndSpinner, thuStartSpinner, thuEndSpinner,
                friStartSpinner, friEndSpinner, satStartSpinner, satEndSpinner, sunStartSpinner, sunEndSpinner
        };

        listEditext = new EditText[]{
                edtPremisesName, edtTelephone, edtNearestStation, edtPostCode, edtAddressLine1, edtAddressLine2, edtTownCity, edtCountryState
        };

        for (String s : tempTime) {
            timeList[timeListIndex] = s + " AM";
            timeListIndex++;
        }

        for (String s : tempTime) {
            timeList[timeListIndex] = s + " PM";
            timeListIndex++;
        }

        premisesTypeSpinner.setAdapter(getArrayAdapter(premisesTypes));
        currencyPreferenceSpinner.setAdapter(getArrayAdapter(currencyPreferences));

        setAdapterToTimeSpinner();

        validation.firstLetterSpace(listEditext);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (countryList[i].equals("United Kingdom")) {
                    countryStateSpinner.setVisibility(View.VISIBLE);
                    edtCountryState.setVisibility(View.GONE);

                } else {
                    countryStateSpinner.setVisibility(View.GONE);
                    edtCountryState.setVisibility(View.VISIBLE);
                }
//                countryCode = countryCodeList[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        edtPremisesName.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        edtNearestStation.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        edtAddressLine1.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        edtAddressLine2.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        edtCountryState.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText[] emptyEdittextList = new EditText[]{edtPremisesName, edtTelephone, edtNearestStation, edtAddressLine1, edtTownCity};
                String[] errorTextList = new String[]{
                        "Premises name is required and should be string.",
                        "Telephone number is required.",
                        "Nearest station is required and should be valid string.",
                        "Address line 1 is required and should be valid string.",
                        "Town/City is required and should be valid string."
                };
                boolean check1 = validation.isEmptyEditText(emptyEdittextList, errorTextList);

                boolean check2 = false;

                if(countrySpinner.getSelectedItem().toString().equals("United Kingdom")){
                    if(countryStateSpinner.getSelectedItem().toString().equals("Select Country/State")){
                        tvCountryState.setError("Please select country/state.");
                        check2 = true;
                    }else{
                        tvCountryState.setError(null);
                    }
                }else{
                    if(edtCountryState.getText().toString().equals("")){
                        edtCountryState.setError("Please enter country/state.");
                        check2 = true;
                    }else{
                        edtCountryState.setError(null);
                    }
                }

                if (!check1 && !check2) {

                    params.put("pub_name", edtPremisesName.getText().toString());
                    params.put("premises", premisesTypeSpinner.getSelectedItem().toString());

                    if (currencyPreferenceSpinner.getSelectedItem().toString().equals(currencyPreferences[0])) {
                        params.put("currency_prefer", "1");
                    } else if (currencyPreferenceSpinner.getSelectedItem().toString().equals(currencyPreferences[1])) {
                        params.put("currency_prefer", "2");
                    } else {
                        params.put("currency_prefer", "3");
                    }

                    params.put("tel2", edtTelephone.getText().toString());
                    params.put("near_station", edtNearestStation.getText().toString());
                    params.put("post_code", edtPostCode.getText().toString());
                    params.put("address1", edtAddressLine1.getText().toString());
                    params.put("address2", edtAddressLine2.getText().toString());
                    params.put("town", edtTownCity.getText().toString());
                    params.put("country", countrySpinner.getSelectedItem().toString());
//                    params.put("country_code", countryCode);

                    if (countrySpinner.getSelectedItem().toString().equals("United Kingdom")) {
                        params.put("other_state", countryStateSpinner.getSelectedItem().toString());
                    } else {
                        params.put("other_state", edtCountryState.getText().toString());
                    }

                    if (monCheckBox.isChecked()) {
                        params.put("1_chk", "1");
                        params.put("1_from", "");
                        params.put("1_to", "");
                    } else {
                        params.put("1_chk", "0");
                        params.put("1_from", monStartSpinner.getSelectedItem().toString());
                        params.put("1_to", monEndSpinner.getSelectedItem().toString());
                    }

                    if (tueCheckBox.isChecked()) {
                        params.put("2_chk", "1");
                        params.put("2_from", "");
                        params.put("2_to", "");
                    } else {
                        params.put("2_chk", "0");
                        params.put("2_from", tueStartSpinner.getSelectedItem().toString());
                        params.put("2_to", tueEndSpinner.getSelectedItem().toString());
                    }

                    if (wedCheckBox.isChecked()) {
                        params.put("3_chk", "1");
                        params.put("3_from", "");
                        params.put("3_to", "");
                    } else {
                        params.put("3_chk", "0");
                        params.put("3_from", wedStartSpinner.getSelectedItem().toString());
                        params.put("3_to", wedEndSpinner.getSelectedItem().toString());
                    }

                    if (thuCheckBox.isChecked()) {
                        params.put("4_chk", "1");
                        params.put("4_from", "");
                        params.put("4_to", "");
                    } else {
                        params.put("4_chk", "0");
                        params.put("4_from", thuStartSpinner.getSelectedItem().toString());
                        params.put("4_to", thuEndSpinner.getSelectedItem().toString());
                    }

                    if (friCheckBox.isChecked()) {
                        params.put("5_chk", "1");
                        params.put("5_from", "");
                        params.put("5_to", "");
                    } else {
                        params.put("5_chk", "0");
                        params.put("5_from", friStartSpinner.getSelectedItem().toString());
                        params.put("5_to", friEndSpinner.getSelectedItem().toString());
                    }

                    if (satCheckBox.isChecked()) {
                        params.put("6_chk", "1");
                        params.put("6_from", "");
                        params.put("6_to", "");
                    } else {
                        params.put("6_chk", "0");
                        params.put("6_from", satStartSpinner.getSelectedItem().toString());
                        params.put("6_to", satEndSpinner.getSelectedItem().toString());
                    }

                    if (sunCheckBox.isChecked()) {
                        params.put("7_chk", "1");
                        params.put("7_from", "");
                        params.put("7_to", "");
                    } else {
                        params.put("7_chk", "0");
                        params.put("7_from", sunStartSpinner.getSelectedItem().toString());
                        params.put("7_to", sunEndSpinner.getSelectedItem().toString());
                    }

                    startActivity(new Intent(PremisesActivity.this, ProfileActivity.class));
                }
            }
        });

    }

    public ArrayAdapter<String> getArrayAdapter(String[] items) {
        return new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
    }

    public void setAdapterToTimeSpinner() {
        for (Spinner timeSpinner : timeSpinners) {
            timeSpinner.setAdapter(getArrayAdapter(timeList));
        }
    }

    public void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        tvCountry = findViewById(R.id.tvCountry);
        tvCountryState = findViewById(R.id.tvCountryState);
        premisesTypeSpinner = findViewById(R.id.premisesTypeSpinner);
        currencyPreferenceSpinner = findViewById(R.id.currencyPreferenceSpinner);
        countrySpinner = findViewById(R.id.countrySpinner);
        countryStateSpinner = findViewById(R.id.countryStateSpinner);
        edtPremisesName = findViewById(R.id.edtPremisesName);
        edtTelephone = findViewById(R.id.edtTelephone);
        edtNearestStation = findViewById(R.id.edtNearestStation);
        edtPostCode = findViewById(R.id.edtPostCode);
        edtAddressLine1 = findViewById(R.id.edtAddressLine1);
        edtAddressLine2 = findViewById(R.id.edtAddressLine2);
        edtTownCity = findViewById(R.id.edtTownCity);
        monStartSpinner = findViewById(R.id.monStartSpinner);
        monEndSpinner = findViewById(R.id.monEndSpinner);
        tueStartSpinner = findViewById(R.id.tueStartSpinner);
        tueEndSpinner = findViewById(R.id.tueEndSpinner);
        wedStartSpinner = findViewById(R.id.wedStartSpinner);
        wedEndSpinner = findViewById(R.id.wedEndSpinner);
        thuStartSpinner = findViewById(R.id.thuStartSpinner);
        thuEndSpinner = findViewById(R.id.thuEndSpinner);
        friStartSpinner = findViewById(R.id.friStartSpinner);
        friEndSpinner = findViewById(R.id.friEndSpinner);
        satStartSpinner = findViewById(R.id.satStartSpinner);
        satEndSpinner = findViewById(R.id.satEndSpinner);
        sunStartSpinner = findViewById(R.id.sunStartSpinner);
        sunEndSpinner = findViewById(R.id.sunEndSpinner);
        monCheckBox = findViewById(R.id.monCheckBox);
        tueCheckBox = findViewById(R.id.tueCheckBox);
        wedCheckBox = findViewById(R.id.wedCheckBox);
        thuCheckBox = findViewById(R.id.thuCheckBox);
        friCheckBox = findViewById(R.id.friCheckBox);
        satCheckBox = findViewById(R.id.satCheckBox);
        sunCheckBox = findViewById(R.id.sunCheckBox);
        edtCountryState = findViewById(R.id.edtCountryState);
    }

    public void getCountries() {
        Map<String, String> params = new HashMap<String, String>();
        ServerAccess.getResponse(PremisesActivity.this, CommonUtilities.key_countries, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                model1 = new CountryModel().CountryModel(result);
                if (model1 != null) {

                    if (model1.response.status.equals(CommonUtilities.key_Success)) {
                        countryList = new String[model1.response.country_list.size()];
//                        countryCodeList = new String[model1.response.country_list.size()];

                        for (int i = 0; i < model1.response.country_list.size(); i++) {
                            countryList[i] = model1.response.country_list.get(i).country_name;
//                            countryCodeList[i] = model1.response.country_list.get(i).country_code;
                        }

                        countrySpinner.setAdapter(getArrayAdapter(countryList));

                        for (int i = 0; i < countryList.length; i++) {
                            if (countryList[i].equals("United Kingdom")) {
                                countrySpinner.setSelection(i);
//                                countryCode = model1.response.country_list.get(i).country_code;
                                break;
                            }
                        }

                        getUkStates();
                    } else {
                        CommonUtilities.alertdialog(PremisesActivity.this, model1.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void getUkStates(){
        Map<String, String> params = new HashMap<String, String>();
        ServerAccess.getResponse(PremisesActivity.this, CommonUtilities.key_uk_states, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                ukStateModel= new UkStateModel().UkStateModel(result);
                if (ukStateModel != null) {

                    if (ukStateModel.response.status.equals(CommonUtilities.key_Success)) {
                        countryStateList = new String[ukStateModel.response.country_states.size() + 2];

                        countryStateList[0] = "Select Country/State";

                        for (int i = 0; i < ukStateModel.response.country_states.size(); i++) {
                            countryStateList[i+1] = ukStateModel.response.country_states.get(i).name;
                        }

                        countryStateList[ukStateModel.response.country_states.size() + 1] = "Other";

                        countryStateSpinner.setAdapter(getArrayAdapter(countryStateList));

                        updateFields();

                    } else {
                        CommonUtilities.alertdialog(PremisesActivity.this, "Something went wrong!");
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void updateFields(){

        if(params.get("pub_name") != null) {
            edtPremisesName.setText(params.get("pub_name"));
        }

        if(params.get("premises") != null) {
            if (premisesTypes[0].equals(params.get("premises"))) {
                premisesTypeSpinner.setSelection(0);
            } else if (premisesTypes[1].equals(params.get("premises"))) {
                premisesTypeSpinner.setSelection(1);
            } else {
                premisesTypeSpinner.setSelection(2);
            }
        }

        if(params.get("currency_prefer") != null) {
            int index = Integer.parseInt(params.get("currency_prefer"));
            currencyPreferenceSpinner.setSelection(index - 1);
        }

        if(params.get("tel2") != null) {
            edtTelephone.setText(params.get("tel2"));
        }

        if(params.get("near_station") != null) {
            edtNearestStation.setText(params.get("near_station"));
        }

        if(params.get("post_code") != null) {
            edtPostCode.setText(params.get("post_code"));
        }

        if(params.get("address1") != null) {
            edtAddressLine1.setText(params.get("address1"));
        }

        if(params.get("address2") != null) {
            edtAddressLine2.setText(params.get("address2"));
        }

        if(params.get("town") != null) {
            edtTownCity.setText(params.get("town"));
        }

        if(params.get("country") != null) {
            for (int i = 0; i < countryList.length; i++) {
                if (countryList[i].equals(params.get("country"))) {
                    countrySpinner.setSelection(i);
                    break;
                }
            }
        }

        if(params.get("other_state") != null) {
            if (params.get("country").equals("United Kingdom")) {
                countryStateSpinner.setVisibility(View.VISIBLE);
                edtCountryState.setVisibility(View.GONE);
                for (int i = 0; i < countryStateList.length; i++) {
                    if (countryStateList[i].equals(params.get("other_state"))) {
                        countryStateSpinner.setSelection(i);
                        break;
                    }
                }
            } else {
                countryStateSpinner.setVisibility(View.GONE);
                edtCountryState.setVisibility(View.VISIBLE);
                edtCountryState.setText(params.get("other_state"));
            }
        }

        if(params.get("1_chk") != null) {
            monCheckBox.setChecked(params.get("1_chk").equals("1"));
        }

        if(params.get("2_chk") != null) {
            tueCheckBox.setChecked(params.get("2_chk").equals("1"));
        }

        if(params.get("3_chk") != null) {
            wedCheckBox.setChecked(params.get("3_chk").equals("1"));
        }

        if(params.get("4_chk") != null) {
            thuCheckBox.setChecked(params.get("4_chk").equals("1"));
        }

        if(params.get("5_chk") != null) {
            friCheckBox.setChecked(params.get("5_chk").equals("1"));
        }

        if(params.get("6_chk") != null) {
            satCheckBox.setChecked(params.get("6_chk").equals("1"));
        }

        if(params.get("7_chk") != null) {
            sunCheckBox.setChecked(params.get("7_chk").equals("1"));
        }

        if(params.get("1_from") != null) {
            if(params.get("1_from").equals("")){
                monStartSpinner.setSelection(0);
            }else {
                for (int i = 0; i < timeList.length; i++) {
                    if (timeList[i].equals(params.get("1_from"))) {
                        monStartSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        if(params.get("1_to") != null) {
            if(params.get("1_to").equals("")){
                monEndSpinner.setSelection(0);
            }else {
                for (int i = 0; i < timeList.length; i++) {
                    if (timeList[i].equals(params.get("1_to"))) {
                        monEndSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        if(params.get("2_from") != null) {
            if(params.get("2_from").equals("")){
                tueStartSpinner.setSelection(0);
            }else {
                for (int i = 0; i < timeList.length; i++) {
                    if (timeList[i].equals(params.get("2_from"))) {
                        tueStartSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        if(params.get("2_to") != null) {
            if(params.get("2_to").equals("")){
                tueEndSpinner.setSelection(0);
            }else {
                for (int i = 0; i < timeList.length; i++) {
                    if (timeList[i].equals(params.get("2_to"))) {
                        tueEndSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        if(params.get("3_from") != null) {
            if(params.get("3_from").equals("")){
                wedStartSpinner.setSelection(0);
            }else {
                for (int i = 0; i < timeList.length; i++) {
                    if (timeList[i].equals(params.get("3_from"))) {
                        wedStartSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        if(params.get("3_to") != null) {
            if(params.get("3_to").equals("")){
                wedEndSpinner.setSelection(0);
            }else {
                for (int i = 0; i < timeList.length; i++) {
                    if (timeList[i].equals(params.get("3_to"))) {
                        wedEndSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        if(params.get("4_from") != null) {
            if(params.get("4_from").equals("")){
                thuStartSpinner.setSelection(0);
            }else {
                for (int i = 0; i < timeList.length; i++) {
                    if (timeList[i].equals(params.get("4_from"))) {
                        thuStartSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        if(params.get("4_to") != null) {
            if(params.get("4_to").equals("")){
                thuEndSpinner.setSelection(0);
            }else {
                for (int i = 0; i < timeList.length; i++) {
                    if (timeList[i].equals(params.get("4_to"))) {
                        thuEndSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        if(params.get("5_from") != null) {
            if(params.get("5_from").equals("")){
                friStartSpinner.setSelection(0);
            }else {
                for (int i = 0; i < timeList.length; i++) {
                    if (timeList[i].equals(params.get("5_from"))) {
                        friStartSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        if(params.get("5_to") != null) {
            if(params.get("5_to").equals("")){
                friEndSpinner.setSelection(0);
            }else {
                for (int i = 0; i < timeList.length; i++) {
                    if (timeList[i].equals(params.get("5_to"))) {
                        friEndSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        if(params.get("6_from") != null) {
            if(params.get("6_from").equals("")){
                satStartSpinner.setSelection(0);
            }else {
                for (int i = 0; i < timeList.length; i++) {
                    if (timeList[i].equals(params.get("6_from"))) {
                        satStartSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        if(params.get("6_to") != null) {
            if(params.get("6_to").equals("")){
                satEndSpinner.setSelection(0);
            }else {
                for (int i = 0; i < timeList.length; i++) {
                    if (timeList[i].equals(params.get("6_to"))) {
                        satEndSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        if(params.get("7_from") != null) {
            if(params.get("7_from").equals("")){
                sunStartSpinner.setSelection(0);
            }else {
                for (int i = 0; i < timeList.length; i++) {
                    if (timeList[i].equals(params.get("7_from"))) {
                        sunStartSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        if(params.get("7_to") != null) {
            if(params.get("7_to").equals("")){
                sunEndSpinner.setSelection(0);
            }else {
                for (int i = 0; i < timeList.length; i++) {
                    if (timeList[i].equals(params.get("7_to"))) {
                        sunEndSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

    }

}