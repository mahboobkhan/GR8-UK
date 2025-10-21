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

        tvCreateAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to user registration screen
                startActivity(new Intent(UserLoginActivity.this, UserSignUpActivity.class));
            }
        });

        // Password visibility toggle
        btnTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Create UserForgotPasswordActivity if needed
                CommonUtilities.ShowToast(UserLoginActivity.this, "Forgot Password feature coming soon!");
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

            }
        });

    }

    public void callUserLoginApi() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        Log.d("UserLogin", "Attempting login with email: " + email);
        
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
                    Log.d("UserLogin", "API Response: " + loginResponse.toString());
                    Log.d("UserLogin", "Response Status: " + (loginResponse.response != null ? loginResponse.response.status : "null"));
                    Log.d("UserLogin", "Response Message: " + (loginResponse.response != null ? loginResponse.response.msg : "null"));
                    
                    if (loginResponse.response != null) {
                        if (loginResponse.response.status.equals("Success") || 
                            loginResponse.response.status.equals(CommonUtilities.key_Success)) {
                            
                            // Store user data
                            if (loginResponse.response.data != null) {
                                UserLoginResponse.Data userData = loginResponse.response.data;
                                
                                // Store token if available
                                if (userData.token != null && !userData.token.isEmpty()) {
                                    CommonUtilities.setSecurity_Preference(UserLoginActivity.this, 
                                            CommonUtilities.key_security_toekn, userData.token);
                                }
                                
                                // Store user ID
                                if (userData.user_id != null && !userData.user_id.isEmpty()) {
                                    CommonUtilities.setPreference(UserLoginActivity.this, 
                                            CommonUtilities.pref_UserId, userData.user_id);
                                }
                                
                                // Store user data as JSON string
                                Gson gson = new Gson();
                                String userDataJson = gson.toJson(response.body());
                                CommonUtilities.setPreference(UserLoginActivity.this, 
                                        CommonUtilities.pref_UserData, userDataJson);
                                
                                CommonUtilities.ShowToast(UserLoginActivity.this, "Login successful!");
                                
                                // Navigate to MainActivity
                                Intent i = new Intent(UserLoginActivity.this, MainActivity.class);
                                startActivity(i);
                                finishAffinity();
                            } else {
                                CommonUtilities.ShowToast(UserLoginActivity.this, "Invalid response data");
                            }
                        } else {
                            // Login failed
                            String errorMsg = loginResponse.response.msg != null ? 
                                    loginResponse.response.msg : "Login failed";
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

