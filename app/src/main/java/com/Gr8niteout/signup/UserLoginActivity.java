package com.Gr8niteout.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.Gr8niteout.MainActivity;
import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.RetrofitClient;
import com.Gr8niteout.model.UserLoginResponse;
import com.Gr8niteout.model.SignUpModel;
import com.Gr8niteout.services.UserAuthService;
import com.Gr8niteout.utils.LoadingDialog;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLoginActivity extends AppCompatActivity {

    TextView tvCreateAnAccount, tvLogin, tvForgotPassword;
    EditText edtEmail, edtPassword;
    ImageView btnTogglePassword;
    UserAuthService userAuthService;
    LoadingDialog loadingDialog;
    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);


        tvCreateAnAccount = findViewById(R.id.tvCreateAnAccount);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        tvLogin = findViewById(R.id.tvLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);

        // Initialize Retrofit service
        userAuthService = RetrofitClient.getInstance().getRetrofit().create(UserAuthService.class);
        
        // Initialize loading dialog
        loadingDialog = new LoadingDialog(this);

        tvCreateAnAccount.setOnClickListener(view -> {
            // Navigate to user registration screen
            startActivity(new Intent(UserLoginActivity.this, UserSignUpActivity.class));
        });

        // Password visibility toggle
        btnTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Hide password
                edtPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnTogglePassword.setImageResource(R.drawable.ic_eye_off);
                isPasswordVisible = false;
            } else {
                // Show password
                edtPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnTogglePassword.setImageResource(R.drawable.ic_eye_on);
                isPasswordVisible = true;
            }
            // Move cursor to end
            edtPassword.setSelection(edtPassword.getText().length());
        });

        tvForgotPassword.setOnClickListener(view -> {
            CommonUtilities.ShowToast(UserLoginActivity.this, "Forgot Password feature coming soon!");
        });

        tvLogin.setOnClickListener(view -> {
            boolean check1 = false;
            boolean check2 = false;
            boolean check3 = false;

            if (edtEmail.getText().toString().trim().equals("")) {
                edtEmail.setError("Please enter email");
                check1 = true;
            } else {
                edtEmail.setError(null);
                check3 = isEmailValid(edtEmail);
            }

            if (edtPassword.getText().toString().trim().equals("")) {
                check2 = true;
                edtPassword.setError("Please enter password");
            } else {
                edtPassword.setError(null);
            }

            if (!check1 && !check2 && !check3) {
                callUserLoginApi();
            }

        });

    }

    public void callUserLoginApi() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        Log.d("UserLogin", "Attempting login with email: " + email + " password: " + password);
        Log.d("UserLogin", "Base URL: https://gr8niteout.probrains.co/");
        Log.d("UserLogin", "Endpoint: webservice.php?request=user_login");
        
        // Show loading dialog
        loadingDialog.show("Signing you in...");

        Call<UserLoginResponse> call = userAuthService.userLogin(email, password);
        call.enqueue(new Callback<UserLoginResponse>() {
            @Override
            public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                // Dismiss loading dialog
                loadingDialog.dismiss();
                
                if (response.isSuccessful() && response.body() != null) {
                    UserLoginResponse loginResponse = response.body();
                    // Log the raw JSON response
                    Gson gson = new Gson();
                    String jsonResponse = gson.toJson(loginResponse);
                    Log.d("UserLogin", "Raw JSON Response: " + jsonResponse);
                    Log.d("UserLogin", "Response Status: " + (loginResponse.response != null ? loginResponse.response.status : "null"));
                    Log.d("UserLogin", "Response Code: " + (loginResponse.response != null ? loginResponse.response.code : "null"));
                    Log.d("UserLogin", "Response Message: " + (loginResponse.response != null ? loginResponse.response.msg : "null"));
                    
                    // Debug ResponseInfo structure
                    if (loginResponse.response != null && loginResponse.response.responseInfo != null) {
                        Log.d("UserLogin", "ResponseInfo Status: " + loginResponse.response.responseInfo.status);
                        Log.d("UserLogin", "ResponseInfo Code: " + loginResponse.response.responseInfo.code);
                        Log.d("UserLogin", "ResponseInfo Data: " + (loginResponse.response.responseInfo.data != null ? "present" : "null"));
                    } else {
                        Log.d("UserLogin", "ResponseInfo is null");
                    }
                    
                    if (loginResponse.response != null) {
                        if (loginResponse.response.status.equals("Success") || 
                            loginResponse.response.status.equals(CommonUtilities.key_Success)) {
                            
                            // Store user data following Facebook login pattern
                            if (loginResponse.response.responseInfo != null && loginResponse.response.responseInfo.data != null) {
                                UserLoginResponse.Data userData = loginResponse.response.responseInfo.data;
                                
                                // Debug logging
                                Log.d("UserLogin", "User ID from API: " + userData.user_id);
                                Log.d("UserLogin", "User logged in: " + userData.user_logged_in);
                                Log.d("UserLogin", "Email from API: " + userData.end_email);
                                Log.d("UserLogin", "First Name from API: " + userData.end_first_name);
                                Log.d("UserLogin", "Last Name from API: " + userData.end_last_name);
                                Log.d("UserLogin", "Username from API: " + userData.end_user_name);
                                
                                // Store token if available (like Facebook login)
                                if (userData.token != null && !userData.token.isEmpty()) {
                                    CommonUtilities.setSecurity_Preference(UserLoginActivity.this, CommonUtilities.key_security_toekn, userData.token);
                                    Log.d("UserLogin", "Token stored successfully");
                                } else {
                                    Log.d("UserLogin", "Token is null or empty");
                                }
                                
                                // Store user ID (like Facebook login)
                                if (userData.user_id != null && !userData.user_id.isEmpty()) {
                                    CommonUtilities.setPreference(UserLoginActivity.this, CommonUtilities.pref_UserId, userData.user_id);
                                    Log.d("UserLogin", "User ID stored successfully: " + userData.user_id);
                                } else {
                                    Log.d("UserLogin", "User ID is null or empty");
                                }
                                
                                // Store user data as JSON string (like Facebook login)
                                 gson = new Gson();
                                String userDataJson = gson.toJson(response.body());
                                CommonUtilities.setPreference(UserLoginActivity.this, CommonUtilities.pref_UserData, userDataJson);
                                
                                // Debug: Verify preferences are set
                                String storedUserId = CommonUtilities.getPreference(UserLoginActivity.this, CommonUtilities.pref_UserId);
                                String storedUserData = CommonUtilities.getPreference(UserLoginActivity.this, CommonUtilities.pref_UserData);
                                Log.d("UserLogin", "Stored User ID: " + storedUserId);
                                Log.d("UserLogin", "Stored User Data length: " + (storedUserData != null ? storedUserData.length() : "null"));
                                
                                // Handle user login success
                                if (userData.user_logged_in) {
                                    // User is logged in - proceed to MainActivity
                                    CommonUtilities.ShowToast(UserLoginActivity.this, "Login successful!");
                                    
                                    // Check for different flags and navigate accordingly
                                    Intent i = new Intent(UserLoginActivity.this, MainActivity.class);
                                    
                                    // Handle different navigation scenarios like Facebook login
                                    if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_flag)) {
                                        String flag = getIntent().getExtras().getString(CommonUtilities.key_flag);
                                        if (flag.equals(CommonUtilities.flag_drinks)) {
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_drinks);
                                        } else if (flag.equals(CommonUtilities.flag_birtday)) {
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_birtday);
                                            CommonUtilities.setPreference(UserLoginActivity.this, CommonUtilities.pref_from_birthday, "true");
                                        } else if (flag.equals(CommonUtilities.flag_comment)) {
                                            Intent intent = new Intent();
                                            setResult(RESULT_OK, intent);
                                            finish();
                                            return;
                                        } else if (flag.equals(CommonUtilities.flag_my_profile)) {
                                            i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_my_profile);
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        }
                                    }
                                    
                                    startActivity(i);
                                    finish();
                                } else {
                                    // User login failed
                                    CommonUtilities.ShowToast(UserLoginActivity.this, "Login failed. Please try again.");
                                }
                            } else {
                                CommonUtilities.ShowToast(UserLoginActivity.this, "Invalid response data");
                            }
                        } else {
                            // Login failed
                            String errorMsg = "Login failed";
                            if (loginResponse.response != null) {
                                // Check for error message in different fields
                                if (loginResponse.response.msg != null && !loginResponse.response.msg.isEmpty()) {
                                    errorMsg = loginResponse.response.msg;
                                } else if (loginResponse.response.responseInfo != null && loginResponse.response.responseInfo.status != null) {
                                    errorMsg = loginResponse.response.responseInfo.status;
                                } else if (loginResponse.response.code != null && !loginResponse.response.code.isEmpty()) {
                                    // Map error codes to user-friendly messages
                                    if (loginResponse.response.code.equals("ERR001")) {
                                        errorMsg = "Invalid credentials. Please check your email and password.";
                                    } else {
                                        errorMsg = "Login failed with code: " + loginResponse.response.code;
                                    }
                                }
                            }
                            Log.d("UserLogin", "Login failed with error: " + errorMsg);
                            CommonUtilities.ShowToast(UserLoginActivity.this, errorMsg);
                        }
                    } else {
                        CommonUtilities.ShowToast(UserLoginActivity.this, "Invalid response format");
                    }
                } else {
                    Log.e("UserLogin", "API call failed: " + response.code() + " - " + response.message());
                    CommonUtilities.ShowToast(UserLoginActivity.this, "Login failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                // Dismiss loading dialog
                loadingDialog.dismiss();
                
                Log.e("UserLogin", "Network error: " + t.getMessage());
                CommonUtilities.ShowToast(UserLoginActivity.this, "Network error. Please check your connection.");
            }
        });
    }

    public boolean isEmailValid(EditText email) {
        boolean check = false;

        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.setError("Please enter a valid email!");
            check = true;
        }else{
            email.setError(null);
        }

        return check;
    }

}

