package com.Gr8niteout.PubDashboardScreens.PubFragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.Gr8niteout.PubDashboardScreens.Models.ChangePasswordModel;
import com.Gr8niteout.PubDashboardScreens.Models.GetPremisesModel;
import com.Gr8niteout.R;
import com.Gr8niteout.RegisterPubScreens.PubRegistrationModels.UkStateModel;
import com.Gr8niteout.RegisterPubScreens.ValidationCases;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.CountryModel;

import java.util.HashMap;
import java.util.Map;

public class PremisesFragment extends Fragment {

    Spinner premisesTypeSpinner, currencyPreferenceSpinner, countrySpinner, countryStateSpinner;
    TextView tvSave, tvCountry, tvCountryState;
    EditText edtPremisesName, edtTelephone, edtNearestStation, edtPostCode, edtAddressLine1, edtAddressLine2, edtTownCity, edtCountryState;
    Spinner monStartSpinner, monEndSpinner, tueStartSpinner, tueEndSpinner, wedStartSpinner, wedEndSpinner, thuStartSpinner, thuEndSpinner,
            friStartSpinner, friEndSpinner, satStartSpinner, satEndSpinner, sunStartSpinner, sunEndSpinner;
    CheckBox monCheckBox, tueCheckBox, wedCheckBox, thuCheckBox, friCheckBox, satCheckBox, sunCheckBox;

    String[] premisesTypes = new String[]{"Managed", "Free Of Tie", "Tenant"};
    String[] currencyPreferences = new String[]{"GBP (£)", "EUR (€)", "USD ($)"};
    String[] countryList;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_premises, container, false);

        initViews(view);
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

        getCountries();

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
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
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

                if (countrySpinner.getSelectedItem().toString().equals("United Kingdom")) {
                    if (countryStateSpinner.getSelectedItem().toString().equals("Select Country/State")) {
                        tvCountryState.setError("Please select country/state.");
                        check2 = true;
                    } else {
                        tvCountryState.setError(null);
                    }
                } else {
                    if (edtCountryState.getText().toString().equals("")) {
                        edtCountryState.setError("Please enter country/state.");
                        check2 = true;
                    } else {
                        edtCountryState.setError(null);
                    }
                }

                if (!check1 && !check2) {
                    callUpdatePremisesApi();
                }
            }
        });

        return view;
    }

    public ArrayAdapter<String> getArrayAdapter(String[] items) {
        return new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
    }

    public void setAdapterToTimeSpinner() {
        for (Spinner timeSpinner : timeSpinners) {
            timeSpinner.setAdapter(getArrayAdapter(timeList));
        }
    }

    public void initViews(View view) {
        tvSave = view.findViewById(R.id.tvSave);
        tvCountry = view.findViewById(R.id.tvCountry);
        tvCountryState = view.findViewById(R.id.tvCountryState);
        premisesTypeSpinner = view.findViewById(R.id.premisesTypeSpinner);
        currencyPreferenceSpinner = view.findViewById(R.id.currencyPreferenceSpinner);
        countrySpinner = view.findViewById(R.id.countrySpinner);
        countryStateSpinner = view.findViewById(R.id.countryStateSpinner);
        edtPremisesName = view.findViewById(R.id.edtPremisesName);
        edtTelephone = view.findViewById(R.id.edtTelephone);
        edtNearestStation = view.findViewById(R.id.edtNearestStation);
        edtPostCode = view.findViewById(R.id.edtPostCode);
        edtAddressLine1 = view.findViewById(R.id.edtAddressLine1);
        edtAddressLine2 = view.findViewById(R.id.edtAddressLine2);
        edtTownCity = view.findViewById(R.id.edtTownCity);
        monStartSpinner = view.findViewById(R.id.monStartSpinner);
        monEndSpinner = view.findViewById(R.id.monEndSpinner);
        tueStartSpinner = view.findViewById(R.id.tueStartSpinner);
        tueEndSpinner = view.findViewById(R.id.tueEndSpinner);
        wedStartSpinner = view.findViewById(R.id.wedStartSpinner);
        wedEndSpinner = view.findViewById(R.id.wedEndSpinner);
        thuStartSpinner = view.findViewById(R.id.thuStartSpinner);
        thuEndSpinner = view.findViewById(R.id.thuEndSpinner);
        friStartSpinner = view.findViewById(R.id.friStartSpinner);
        friEndSpinner = view.findViewById(R.id.friEndSpinner);
        satStartSpinner = view.findViewById(R.id.satStartSpinner);
        satEndSpinner = view.findViewById(R.id.satEndSpinner);
        sunStartSpinner = view.findViewById(R.id.sunStartSpinner);
        sunEndSpinner = view.findViewById(R.id.sunEndSpinner);
        monCheckBox = view.findViewById(R.id.monCheckBox);
        tueCheckBox = view.findViewById(R.id.tueCheckBox);
        wedCheckBox = view.findViewById(R.id.wedCheckBox);
        thuCheckBox = view.findViewById(R.id.thuCheckBox);
        friCheckBox = view.findViewById(R.id.friCheckBox);
        satCheckBox = view.findViewById(R.id.satCheckBox);
        sunCheckBox = view.findViewById(R.id.sunCheckBox);
        edtCountryState = view.findViewById(R.id.edtCountryState);
    }

    public void getCountries() {
        Map<String, String> params = new HashMap<String, String>();
        ServerAccess.getResponse(getContext(), CommonUtilities.key_countries, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                model1 = new CountryModel().CountryModel(result);
                if (model1 != null && model1.response != null && model1.response.country_list != null) {

                    if (model1.response.status != null && model1.response.status.equals(CommonUtilities.key_Success)) {
                        countryList = new String[model1.response.country_list.size()];

                        for (int i = 0; i < model1.response.country_list.size(); i++) {
                            countryList[i] = model1.response.country_list.get(i).country_name;
                        }

                        countrySpinner.setAdapter(getArrayAdapter(countryList));

                        for (int i = 0; i < countryList.length; i++) {
                            if (countryList[i].equals("United Kingdom")) {
                                countrySpinner.setSelection(i);
                                break;
                            }
                        }

                        getUkStates();
                    } else {
                        CommonUtilities.alertdialog(getContext(), model1.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void getUkStates() {
        Map<String, String> params = new HashMap<String, String>();
        ServerAccess.getResponse(getContext(), CommonUtilities.key_uk_states, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                ukStateModel = new UkStateModel().UkStateModel(result);
                if (ukStateModel != null) {

                    if (ukStateModel.response.status.equals(CommonUtilities.key_Success)) {
                        countryStateList = new String[ukStateModel.response.country_states.size() + 2];

                        countryStateList[0] = "Select Country/State";

                        for (int i = 0; i < ukStateModel.response.country_states.size(); i++) {
                            countryStateList[i + 1] = ukStateModel.response.country_states.get(i).name;
                        }

                        countryStateList[ukStateModel.response.country_states.size() + 1] = "Other";

                        countryStateSpinner.setAdapter(getArrayAdapter(countryStateList));

                        getPremises();
                    } else {
                        CommonUtilities.alertdialog(getContext(), "Something went wrong!");
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void getPremises() {
        SharedPreferences preferences = getContext().getSharedPreferences("pub_details", MODE_PRIVATE);
        Map<String, String> params = new HashMap<String, String>();
        String url = CommonUtilities.key_get_premises + "&pub_id="+ preferences.getString("pub_id", "");

        ServerAccess.getResponse(getContext(), url, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                GetPremisesModel getPremisesModel = new GetPremisesModel().GetPremisesModel(result);
                if (getPremisesModel != null) {

                    if (getPremisesModel.response.code.equals(CommonUtilities.key_success_code)) {
                        edtPremisesName.setText(getPremisesModel.response.data.pub_name);

                        if (premisesTypes[0].equalsIgnoreCase(getPremisesModel.response.data.premises_type)) {
                            premisesTypeSpinner.setSelection(0);
                        } else if (premisesTypes[1].equalsIgnoreCase(getPremisesModel.response.data.premises_type)) {
                            premisesTypeSpinner.setSelection(1);
                        } else {
                            premisesTypeSpinner.setSelection(2);
                        }

                        currencyPreferenceSpinner.setSelection(Integer.parseInt(getPremisesModel.response.data.currency) - 1);
                        edtTelephone.setText(getPremisesModel.response.data.phone_no);
                        edtNearestStation.setText(getPremisesModel.response.data.nearest_station);
                        edtPostCode.setText(getPremisesModel.response.data.post_code);
                        edtAddressLine1.setText(getPremisesModel.response.data.address1);
                        edtAddressLine2.setText(getPremisesModel.response.data.address2);
                        edtTownCity.setText(getPremisesModel.response.data.town);

                        for (int i = 0; i < countryList.length; i++) {
                            if (countryList[i].equals(getPremisesModel.response.data.country)) {
                                countrySpinner.setSelection(i);
                                break;
                            }
                        }

                        if (getPremisesModel.response.data.country.equals("United Kingdom")) {
                            countryStateSpinner.setVisibility(View.VISIBLE);
                            edtCountryState.setVisibility(View.GONE);
                            for (int i = 0; i < countryStateList.length; i++) {
                                if (countryStateList[i].equals(getPremisesModel.response.data.other_state)) {
                                    countryStateSpinner.setSelection(i);
                                    break;
                                }
                            }
                        } else {
                            countryStateSpinner.setVisibility(View.GONE);
                            edtCountryState.setVisibility(View.VISIBLE);
                            edtCountryState.setText(getPremisesModel.response.data.other_state);
                        }

                        if(getPremisesModel.response.data.one_chk.equals("1")) {
                            monCheckBox.setChecked(true);
                        }

                        if(getPremisesModel.response.data.two_chk.equals("1")) {
                            tueCheckBox.setChecked(true);
                        }

                        if(getPremisesModel.response.data.three_chk.equals("1")) {
                            wedCheckBox.setChecked(true);
                        }

                        if(getPremisesModel.response.data.four_chk.equals("1")) {
                            thuCheckBox.setChecked(true);
                        }

                        if(getPremisesModel.response.data.five_chk.equals("1")) {
                            friCheckBox.setChecked(true);
                        }

                        if(getPremisesModel.response.data.six_chk.equals("1")) {
                            satCheckBox.setChecked(true);
                        }

                        if(getPremisesModel.response.data.seven_chk.equals("1")) {
                            sunCheckBox.setChecked(true);
                        }

                        updateTimeSpinner(getPremisesModel.response.data.one_from,0);
                        updateTimeSpinner(getPremisesModel.response.data.one_to,1);

                        updateTimeSpinner(getPremisesModel.response.data.two_from,2);
                        updateTimeSpinner(getPremisesModel.response.data.two_to,3);

                        updateTimeSpinner(getPremisesModel.response.data.three_from,4);
                        updateTimeSpinner(getPremisesModel.response.data.three_to,5);

                        updateTimeSpinner(getPremisesModel.response.data.four_from,6);
                        updateTimeSpinner(getPremisesModel.response.data.four_to,7);

                        updateTimeSpinner(getPremisesModel.response.data.five_from,8);
                        updateTimeSpinner(getPremisesModel.response.data.five_to,9);

                        updateTimeSpinner(getPremisesModel.response.data.six_from,10);
                        updateTimeSpinner(getPremisesModel.response.data.six_to,11);

                        updateTimeSpinner(getPremisesModel.response.data.seven_from,12);
                        updateTimeSpinner(getPremisesModel.response.data.seven_to,13);

                    } else {
                        CommonUtilities.ShowToast(getContext(), getPremisesModel.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(getContext(), "Something went wrong!");
            }
        });
    }

    public void callUpdatePremisesApi(){
        SharedPreferences preferences = getContext().getSharedPreferences("pub_details", MODE_PRIVATE);
        Map<String, String> params = new HashMap<String, String>();

        params.put("pub_id", preferences.getString("pub_id", ""));
        params.put("pub_name", edtPremisesName.getText().toString().trim());
        params.put("premises", premisesTypeSpinner.getSelectedItem().toString().trim());

        if (currencyPreferenceSpinner.getSelectedItem().toString().equals(currencyPreferences[0])) {
            params.put("currency_prefer", "1");
        } else if (currencyPreferenceSpinner.getSelectedItem().toString().equals(currencyPreferences[1])) {
            params.put("currency_prefer", "2");
        } else {
            params.put("currency_prefer", "3");
        }

        params.put("tel2", edtTelephone.getText().toString().trim());
        params.put("near_station", edtNearestStation.getText().toString().trim());
        params.put("post_code", edtPostCode.getText().toString().trim());
        params.put("address1", edtAddressLine1.getText().toString().trim());
        params.put("address2", edtAddressLine2.getText().toString().trim());
        params.put("town", edtTownCity.getText().toString().trim());
        params.put("country", countrySpinner.getSelectedItem().toString().trim());

        if (countrySpinner.getSelectedItem().toString().equals("United Kingdom")) {
            params.put("other_state", countryStateSpinner.getSelectedItem().toString().trim());
        } else {
            params.put("other_state", edtCountryState.getText().toString().trim());
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

        ServerAccess.getResponse(getContext(), CommonUtilities.key_update_premises, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                ChangePasswordModel changePasswordModel = new ChangePasswordModel().ChangePasswordModel(result);
                if (changePasswordModel != null) {
                    CommonUtilities.ShowToast(getContext(),changePasswordModel.response.msg);
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(getContext(), "Something went wrong!");
            }
        });
    }

    public void updateTimeSpinner(String time, int index){
        if(time.equals("")){
            timeSpinners[index].setSelection(0);
        }else {
            for (int i = 0; i < timeList.length; i++) {
                if (timeList[i].equals(time)) {
                    timeSpinners[index].setSelection(i);
                    break;
                }
            }
        }
    }

}