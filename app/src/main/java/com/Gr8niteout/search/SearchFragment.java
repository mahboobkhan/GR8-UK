package com.Gr8niteout.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.Gr8niteout.MainActivity;
import com.Gr8niteout.R;
import com.Gr8niteout.RegisterPubScreens.EmojiExcludeFilter;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.MyApplication;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.CountryStateModel;
import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment implements View.OnClickListener {

    MainActivity mActivity;
    private EditText edtCity;
    private TextView edtPub;
    Button btnShowMe;
    CountryStateModel model;
    Spinner spnCountry;
    Spinner spnState;
    LinearLayout layoutMain,layoutCountry,layoutState;
    List<String> CountryList = new ArrayList<>();
    List<String> StateList = new ArrayList<>();
    public String State_id = "", stateName = "";
    int pos = -1;
    AdapterView.OnItemSelectedListener countryListener;
    ArrayAdapter<String> CountryAdapter;
    ArrayAdapter<String> StateAdapter;
    ImageView imgState,imgCountry;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mActivity.getSupportActionBar().setTitle(null);
        mActivity.tool_text.setText("Find a Pub");
        CommonUtilities.setFontFamily(mActivity,mActivity.tool_text , CommonUtilities.AvenirLTStd_Medium);
        (mActivity).goImage.setVisibility(View.GONE);
        mActivity.getSupportActionBar().setIcon(null);
        edtPub = (EditText) view.findViewById(R.id.edtPub);
        edtCity = (EditText) view.findViewById(R.id.edtCity);
        btnShowMe = (Button) view.findViewById(R.id.btnShowMe);
        spnCountry = (Spinner) view.findViewById(R.id.spnCountry);
        spnState = (Spinner) view.findViewById(R.id.spnState);
        layoutMain = (LinearLayout) view.findViewById(R.id.layoutMain);
        layoutCountry = (LinearLayout) view.findViewById(R.id.layoutCountry);
        layoutState = (LinearLayout) view.findViewById(R.id.layoutState);
        imgCountry = (ImageView) view.findViewById(R.id.imgCountry);
        imgState = (ImageView) view.findViewById(R.id.imgState);

        edtCity.setHintTextColor(mActivity.getResources().getColor(R.color.mgray));
        edtPub.setHintTextColor(mActivity.getResources().getColor(R.color.mgray));
        getCountries();
        setHasOptionsMenu(true);

        edtCity.clearFocus();
        edtPub.clearFocus();
        layoutMain.setVisibility(View.GONE);
        countryListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view.findViewById(R.id.txtItem);
                CommonUtilities.setFontFamily(mActivity, textView, CommonUtilities.AvenirLTStd_Medium);
                pos = position;
                StateList.clear();
                StateList.add("County or State");
                if (pos == 0) {
                    for (int i = 0; i < model.response.country_states.get(pos).state_list.size(); i++)
                        StateList.add(model.response.country_states.get(pos).state_list.get(i).state_name);
                    spnState.setEnabled(false);
                    layoutState.setEnabled(false);
                    imgState.setEnabled(false);

                } else {
                    for (int i = 0; i < model.response.country_states.get(pos - 1).state_list.size(); i++)
                        StateList.add(model.response.country_states.get(pos - 1).state_list.get(i).state_name);
                    spnState.setEnabled(true);
                    layoutState.setEnabled(true);
                    imgState.setEnabled(true);
                }

                StateAdapter = new ArrayAdapter<String>(mActivity,
                        R.layout.spinner_item, StateList) {

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {

                        View v = null;
                        if (position == 0) {
                            TextView tv = new TextView(getContext());
                            tv.setHeight(0);
                            tv.setVisibility(View.GONE);
                            v = tv;
                        } else {
                            v = super.getDropDownView(position, null, parent);
                            TextView textView = (TextView)v.findViewById(R.id.txtItem);
                            textView.setPadding(15,15,0,15);

                            textView.setTextColor(getResources().getColor(R.color.mgray));
                            textView.setTextSize(19);
                            CommonUtilities.setFontFamily(mActivity, textView, CommonUtilities.AvenirLTStd_Medium);
                        }
                        parent.setVerticalScrollBarEnabled(false);
                        return v;
                    }
                };
                StateAdapter.setDropDownViewResource(R.layout.spinner_item);
                spnState.setAdapter(StateAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        spnState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(view!=null)
                {
                    TextView textView = (TextView)view.findViewById(R.id.txtItem);
                    CommonUtilities.setFontFamily(mActivity, textView, CommonUtilities.AvenirLTStd_Medium);
                }
                stateName = "";
                State_id = "";
                if (pos != 0 && position != 0) {
                    State_id = model.response.country_states.get(pos - 1).state_list.get(position - 1).state_id;
                    stateName = model.response.country_states.get(pos - 1).state_list.get(position - 1).state_name;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setFont();
        btnShowMe.setOnClickListener(this);
        imgCountry.setOnClickListener(this);
        imgState.setOnClickListener(this);
        layoutCountry.setOnClickListener(this);
        layoutState.setOnClickListener(this);
        edtPub.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        return view;
    }


    public void setFont() {
        CommonUtilities.setFontFamily(mActivity, btnShowMe, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(mActivity, edtCity, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(mActivity, edtPub, CommonUtilities.AvenirLTStd_Medium);
    }

    public void getCountries() {
        Map<String, String> params = new HashMap<String, String>();
        ServerAccess.getResponse(mActivity, CommonUtilities.key_countries_states, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                model = new CountryStateModel().CountryStateModel(result);
                if (model != null) {
                    if (model.response.status.equals(CommonUtilities.key_Success)) {
                        CountryList.add("Country");
                        layoutMain.setVisibility(View.VISIBLE);
                        for (int i = 0; i < model.response.country_states.size(); i++)
                            CountryList.add(model.response.country_states.get(i).country);

                        CountryAdapter = new ArrayAdapter<String>(mActivity,
                                R.layout.spinner_item, CountryList) {

                            @Override
                            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                                View v = null;

                                if (position == 0) {
                                    TextView tv = new TextView(getContext());
                                    tv.setHeight(0);
                                    tv.setVisibility(View.GONE);
                                    v = tv;
                                } else {
                                    v = super.getDropDownView(position, null, parent);
                                    TextView textView = (TextView)v.findViewById(R.id.txtItem);
                                    textView.setPadding(15,15,0,18);
                                    textView.setTextColor(getResources().getColor(R.color.mgray));
                                    textView.setTextSize(19);
                                    CommonUtilities.setFontFamily(mActivity, textView, CommonUtilities.AvenirLTStd_Medium);
                                }

                                parent.setVerticalScrollBarEnabled(false);
                                return v;
                            }
                        };
                        CountryAdapter.setDropDownViewResource(R.layout.spinner_item);

                        // attaching data adapter to spinner
                        spnCountry.setAdapter(CountryAdapter);
                        spnCountry.setOnItemSelectedListener(countryListener);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_reset).setVisible(false);
        menu.findItem(R.id.action_setting).setVisible(false);
        menu.findItem(R.id.action_search_white).setVisible(false);
        menu.findItem(R.id.action_cancel).setVisible(true);
        menu.findItem(R.id.action_transaction_history).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cancel) {
            State_id = "";
            stateName = "";
            edtCity.setText("");
            edtPub.setText("");
            spnCountry.setSelection(0);
            spnState.setSelection(0);
            edtPub.clearFocus();
            edtCity.clearFocus();
            CommonUtilities.hideSoftKeyboard(mActivity,edtCity);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnShowMe) {
            //Intent i = new Intent(mActivity, PubRecyclerActivity.class);
            Intent i = new Intent(mActivity, PubListActivity.class);
            if (pos == -1 || pos == 0) {
                i.putExtra(CommonUtilities.key_country_id, "");
                i.putExtra("country", "");
            } else {
                i.putExtra(CommonUtilities.key_country_id, model.response.country_states.get(pos - 1).iso);
                i.putExtra("country", model.response.country_states.get(pos - 1).country);
            }
            i.putExtra(CommonUtilities.key_state_id, State_id);
            i.putExtra(CommonUtilities.key_city, edtCity.getText().toString() + "");
            i.putExtra(CommonUtilities.key_pub_event_name, edtPub.getText().toString() + "");
            i.putExtra("state", stateName);
            startActivity(i);
        } else if (id == R.id.imgCountry) {
            spnCountry.performClick();
        } else if (id == R.id.imgState) {
            spnState.performClick();
        } else if (id == R.id.layoutCountry) {
            spnCountry.performClick();
        } else if (id == R.id.layoutState) {
            spnState.performClick();
        }
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btnShowMe:
//                //Intent i = new Intent(mActivity, PubRecyclerActivity.class);
//                Intent i = new Intent(mActivity, PubListActivity.class);
//                if (pos == -1 || pos == 0) {
//                    i.putExtra(CommonUtilities.key_country_id, "");
//                    i.putExtra("country","");
//                }
//
//                else
//                {
//                    i.putExtra(CommonUtilities.key_country_id, model.response.country_states.get(pos - 1).iso);
//                    i.putExtra("country", model.response.country_states.get(pos - 1).country);
//                }
//            i.putExtra(CommonUtilities.key_state_id, State_id);
//            i.putExtra(CommonUtilities.key_city, edtCity.getText().toString() + "");
//            i.putExtra(CommonUtilities.key_pub_event_name, edtPub.getText().toString() + "");
//            i.putExtra("state", stateName);
//            startActivity(i);
//            break;
//            case R.id.imgCountry:
//                spnCountry.performClick();
//                break;
//            case R.id.imgState:
//                spnState.performClick();
//                break;
//            case R.id.layoutCountry:
//                spnCountry.performClick();
//                break;
//            case R.id.layoutState:
//                spnState.performClick();
//                break;
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        CommonUtilities.isBack = false;
    }

    @Override
    public void onStart() {
        super.onStart();

        ((MyApplication)mActivity.getApplication()).getDefaultTracker().setScreenName("Search Screen");
        ((MyApplication)mActivity.getApplication()).getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }
}

