package com.Gr8niteout.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.Gr8niteout.MainActivity;
import com.Gr8niteout.R;
import com.Gr8niteout.RegisterPubScreens.ValidationCases;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.SignUpModel;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class UserSignUpActivity extends AppCompatActivity {

    Spinner genderSpinner;
    TextView btnSignUp, tvDOB;
    EditText edtFirstName, edtLastName, edtEmail, edtPassword, edtConfirmPassword, edtUsername, edtDob, edtPostcode;

    String[] genderItems = new String[]{"Male", "Female"};
    
    String selectedDate = "";
    String selectedMonth = "";
    String selectedYear = "";

    EditText[] list;
    String[] errorTextList;

    ValidationCases validation;

    SignUpModel signUpModel;

    boolean check1 = false;
    boolean check2 = false;
    boolean check3 = false;
    boolean check4 = false;
    boolean check5 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

        // Initialize views
        genderSpinner = findViewById(R.id.genderSpinner);
        edtDob = findViewById(R.id.edtDob);
        btnSignUp = findViewById(R.id.btnSignUp);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtUsername = findViewById(R.id.edtUsername);
        edtPostcode = findViewById(R.id.edtPostcode);
        tvDOB = findViewById(R.id.tvDOB);

        list = new EditText[]{
                edtFirstName, edtLastName, edtEmail, edtPassword, edtConfirmPassword, edtUsername, edtPostcode
        };

        errorTextList = new String[]{
                "First name is required and must be a string.",
                "Last name is required and must be a string.",
                "Email address is required.",
                "Password must be 8-20 characters long.",
                "Confirm password is required.",
                "Username is required and should be alphanumeric.",
                "Postcode is required."
        };

        validation = new ValidationCases();
        validation.firstLetterSpace(list);

        genderSpinner.setAdapter(getArrayAdapter(genderItems));

        edtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(UserSignUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        edtDob.setText(day + "-" + (month + 1) + "-" + year);
                        selectedDate = day + "";
                        selectedMonth = (month + 1) + "";
                        selectedYear = year + "";
                        tvDOB.setError(null);
                    }
                }, year - 18, month, day);
                Calendar minDate = new GregorianCalendar(year - 18, month, day);
                dialog.getDatePicker().setMaxDate(minDate.getTimeInMillis());
                dialog.show();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performValidation();
            }
        });
    }

    private void performValidation() {
        check1 = validation.isEmptyEditText(list, errorTextList);
        check2 = validation.matchTwoFields(edtPassword, edtConfirmPassword, "Password and confirm password must be same.");
        check3 = validation.isEmailValid(edtEmail);
        check4 = false;
        check5 = false;

        // Validate first name is string
        if (!edtFirstName.getText().toString().trim().matches(".*[a-zA-Z].*")) {
            edtFirstName.setError("First name is required and must be a string.");
            check4 = true;
        } else {
            edtFirstName.setError(null);
        }

        // Validate last name is string
        if (!edtLastName.getText().toString().trim().matches(".*[a-zA-Z].*")) {
            edtLastName.setError("Last name is required and must be a string.");
            check4 = true;
        } else {
            edtLastName.setError(null);
        }

        // Validate password length
        if (edtPassword.getText().toString().length() < 8 || edtPassword.getText().toString().length() > 20) {
            edtPassword.setError("Password must be 8-20 characters long.");
            check4 = true;
        }

        // Validate DOB
        if (selectedDate.equals("") || selectedMonth.equals("") || selectedYear.equals("")) {
            tvDOB.setError("Please Select Date Of Birth!");
            check5 = true;
        } else {
            tvDOB.setError(null);
        }

        if (!check1 && !check2 && !check3 && !check4 && !check5) {
            callUserSignUpApi();
        }
    }

    public ArrayAdapter<String> getArrayAdapter(String[] items) {
        return new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
    }

    public void callUserSignUpApi() {
        Map<String, String> paramsTemp = new HashMap<>();
        
        // User details
        paramsTemp.put("first_name", edtFirstName.getText().toString().trim());
        paramsTemp.put("last_name", edtLastName.getText().toString().trim());
        paramsTemp.put("email", edtEmail.getText().toString().trim());
        paramsTemp.put("password", edtPassword.getText().toString().trim());
        paramsTemp.put("username", edtUsername.getText().toString().trim());
        paramsTemp.put("postcode", edtPostcode.getText().toString().trim());
        
        // Gender
        if (genderSpinner.getSelectedItem().toString().equals(genderItems[0])) {
            paramsTemp.put("gender", "0"); // Male
        } else {
            paramsTemp.put("gender", "1"); // Female
        }
        
        // Date of birth
        paramsTemp.put("day", selectedDate);
        paramsTemp.put("month", selectedMonth);
        paramsTemp.put("year", selectedYear);

        ServerAccess.getResponse(UserSignUpActivity.this, CommonUtilities.key_signup_service, paramsTemp, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                signUpModel = new SignUpModel().SignUpModel(result);
                if (signUpModel != null) {
                    if (signUpModel.response.status.equals(CommonUtilities.key_Success)) {
                        if (signUpModel.response.user_data.token != null)
                            CommonUtilities.setSecurity_Preference(UserSignUpActivity.this, CommonUtilities.key_security_toekn, signUpModel.response.user_data.token);
                        
                        CommonUtilities.setPreference(UserSignUpActivity.this, CommonUtilities.pref_UserData, result);
                        CommonUtilities.setPreference(UserSignUpActivity.this, CommonUtilities.pref_UserId, signUpModel.response.user_data.user_id);
                        
                        if (signUpModel.response.user_data.flag.equals("1")) {
                            if (signUpModel.response.user_data.user_active_status.equals("1")) {
                                CommonUtilities.ShowToast(UserSignUpActivity.this, "Sign up successful!");
                                
                                // Navigate to MainActivity
                                Intent i = new Intent(UserSignUpActivity.this, MainActivity.class);
                                startActivity(i);
                                finishAffinity();
                            } else {
                                CommonUtilities.alertdialog(UserSignUpActivity.this, signUpModel.response.msg);
                            }
                        } else if (signUpModel.response.user_data.flag.equals("0")) {
                            // User needs to complete mobile verification
                            Intent i = new Intent(UserSignUpActivity.this, SignUpMobile.class);
                            startActivity(i);
                            finish();
                        } else {
                            CommonUtilities.ShowToast(UserSignUpActivity.this, signUpModel.response.msg);
                        }
                    } else {
                        CommonUtilities.ShowToast(UserSignUpActivity.this, signUpModel.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(UserSignUpActivity.this, "Something went wrong!");
            }
        });
    }

}

