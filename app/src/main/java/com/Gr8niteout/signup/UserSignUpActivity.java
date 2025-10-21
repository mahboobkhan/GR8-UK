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
import com.Gr8niteout.config.RetrofitClient;
import com.Gr8niteout.model.UserSignUpResponse;
import com.Gr8niteout.services.UserAuthService;
import com.Gr8niteout.utils.DeviceUtils;
import com.Gr8niteout.utils.LoadingDialog;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    UserAuthService userAuthService;
    LoadingDialog loadingDialog;


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
        
        // Initialize Retrofit service
        userAuthService = RetrofitClient.getInstance().getRetrofit().create(UserAuthService.class);
        
        // Initialize loading dialog
        loadingDialog = new LoadingDialog(this);

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
        // User details
        String firstName = edtFirstName.getText().toString().trim();
        String lastName = edtLastName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String postcode = edtPostcode.getText().toString().trim();
        
        // Gender
        String gender = genderSpinner.getSelectedItem().toString().equals(genderItems[0]) ? "0" : "1";
        
        // Device information
        String deviceUDID = DeviceUtils.getDeviceUDID(this);
        String appVersion = DeviceUtils.getAppVersion(this);
        String deviceType = DeviceUtils.getDeviceType();
        String timezone = DeviceUtils.getDeviceTimezone();
        String countryName = DeviceUtils.getCountryName();
        
        // Format birthdate (YYYY-MM-DD format)
        String birthdate = "";
        if (!selectedYear.equals("") && !selectedMonth.equals("") && !selectedDate.equals("")) {
            birthdate = selectedYear + "-" + String.format("%02d", Integer.parseInt(selectedMonth)) + "-" + String.format("%02d", Integer.parseInt(selectedDate));
        }
        
        // Generate FCM token (placeholder - you may want to get actual FCM token)
        String fcmToken = "dCi38BZzwkUjugg79mQd7M:APA91bF9yibQY4YbS94VMvD11QsInGwJoDwpxgswiPpAP4FoHY0Mm-eBbpfgfNFRkn8BfVDtQyQZFqh7LhuJ_rxaoJ97myIyc0XwO3l318QkkQUmB6JHmQc";
        
        Log.d("UserSignUp", "Signup request - Email: " + email + ", Username: " + username);
        
        // Show loading dialog
        loadingDialog.show("Creating your account...");
        
        Call<UserSignUpResponse> call = userAuthService.userSignUp(
                "", // photo
                appVersion, // app_ver
                "", // s_token
                "", // access_token
                email, // email
                "", // country_code
                deviceType, // d_type
                countryName, // country_name
                lastName, // lname
                "", // mobile
                fcmToken, // p_token
                deviceUDID, // udid
                firstName, // fname
                birthdate, // birthdate
                timezone, // timezone
                gender, // gender
                "", // fb_id
                password, // password
                username // username
        );
        
        call.enqueue(new Callback<UserSignUpResponse>() {
            @Override
            public void onResponse(Call<UserSignUpResponse> call, Response<UserSignUpResponse> response) {
                // Dismiss loading dialog
                loadingDialog.dismiss();
                
                Log.d("UserSignUp", "API Response Code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    UserSignUpResponse signUpResponse = response.body();
                    
                    if (signUpResponse.response != null) {
                        Log.d("UserSignUp", "Response Status: " + signUpResponse.response.status);
                        Log.d("UserSignUp", "Response Code: " + signUpResponse.response.code);
                        
                        if (signUpResponse.response.status.equals("Success") || 
                            signUpResponse.response.status.equals(CommonUtilities.key_Success)) {
                            
                            if (signUpResponse.response.responseInfo != null) {
                                Log.d("UserSignUp", "ResponseInfo error: " + signUpResponse.response.responseInfo.error);
                                Log.d("UserSignUp", "ResponseInfo status: " + signUpResponse.response.responseInfo.status);
                                
                                // Check if there's an error in ResponseInfo
                                if (signUpResponse.response.responseInfo.error != null && 
                                    signUpResponse.response.responseInfo.error) {
                                    // Email already exists or other error
                                    String errorMsg = signUpResponse.response.responseInfo.status != null ? 
                                            signUpResponse.response.responseInfo.status : "Sign up failed";
                                    Log.d("UserSignUp", "Showing error message: " + errorMsg);
                                    CommonUtilities.ShowToast(UserSignUpActivity.this, errorMsg);
                                    return;
                                }
                                
                                // Store user data
                                if (signUpResponse.response.responseInfo.data != null && 
                                    signUpResponse.response.responseInfo.data.userDetails != null) {
                                    
                                    UserSignUpResponse.UserDetails userDetails = signUpResponse.response.responseInfo.data.userDetails;
                                    
                                    // Store user ID
                                    if (userDetails.user_id != null && !userDetails.user_id.isEmpty()) {
                                        CommonUtilities.setPreference(UserSignUpActivity.this, 
                                                CommonUtilities.pref_UserId, userDetails.user_id);
                                    }
                                    
                                    // Store access token if available
                                    if (userDetails.access_token != null && !userDetails.access_token.isEmpty()) {
                                        CommonUtilities.setSecurity_Preference(UserSignUpActivity.this, 
                                                CommonUtilities.key_security_toekn, userDetails.access_token);
                                    }
                                    
                                    // Store user data as JSON string
                                    Gson gson = new Gson();
                                    String userDataJson = gson.toJson(response.body());
                                    CommonUtilities.setPreference(UserSignUpActivity.this, 
                                            CommonUtilities.pref_UserData, userDataJson);
                                    
                                    String successMsg = signUpResponse.response.responseInfo.msg != null ? 
                                            signUpResponse.response.responseInfo.msg : "Sign up successful!";
                                    CommonUtilities.ShowToast(UserSignUpActivity.this, successMsg);
                                    
                                    // Navigate to MainActivity
                                    Intent i = new Intent(UserSignUpActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finishAffinity();
                                } else {
                                    CommonUtilities.ShowToast(UserSignUpActivity.this, "Invalid response data");
                                }
                            } else {
                                CommonUtilities.ShowToast(UserSignUpActivity.this, "Invalid response info");
                            }
                        } else {
                            // Sign up failed
                            String errorMsg = signUpResponse.response.responseInfo != null && 
                                    signUpResponse.response.responseInfo.status != null ? 
                                    signUpResponse.response.responseInfo.status : "Sign up failed";
                            CommonUtilities.ShowToast(UserSignUpActivity.this, errorMsg);
                        }
                    } else {
                        CommonUtilities.ShowToast(UserSignUpActivity.this, "Invalid response format");
                    }
                } else {
                    Log.e("UserSignUp", "API call failed: " + response.code() + " - " + response.message());
                    CommonUtilities.ShowToast(UserSignUpActivity.this, "Sign up failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<UserSignUpResponse> call, Throwable t) {
                // Dismiss loading dialog
                loadingDialog.dismiss();
                
                Log.e("UserSignUp", "Network error: " + t.getMessage());
                CommonUtilities.ShowToast(UserSignUpActivity.this, "Network error. Please check your connection.");
            }
        });
    }

}

