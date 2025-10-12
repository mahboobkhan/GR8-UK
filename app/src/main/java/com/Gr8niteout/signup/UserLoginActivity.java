package com.Gr8niteout.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.Gr8niteout.MainActivity;
import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.SignUpModel;

import java.util.HashMap;
import java.util.Map;

public class UserLoginActivity extends AppCompatActivity {

    TextView tvCreateAnAccount, tvLogin, tvForgotPassword;
    EditText edtEmail, edtPassword;
    SignUpModel signUpModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        tvCreateAnAccount = findViewById(R.id.tvCreateAnAccount);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        tvLogin = findViewById(R.id.tvLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        tvCreateAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to user registration screen
                startActivity(new Intent(UserLoginActivity.this, UserSignUpActivity.class));
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
        Map<String, String> paramsTemp = new HashMap<>();
        paramsTemp.put("email", edtEmail.getText().toString().trim());
        paramsTemp.put("password", edtPassword.getText().toString().trim());

        ServerAccess.getResponse(UserLoginActivity.this, CommonUtilities.key_signup_service, paramsTemp, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                signUpModel = new SignUpModel().SignUpModel(result);
                if (signUpModel != null) {
                    if(signUpModel.response.status.equals(CommonUtilities.key_Success)){
                        if (signUpModel.response.user_data.token != null)
                            CommonUtilities.setSecurity_Preference(UserLoginActivity.this, CommonUtilities.key_security_toekn, signUpModel.response.user_data.token);
                        
                        CommonUtilities.setPreference(UserLoginActivity.this, CommonUtilities.pref_UserData, result);
                        CommonUtilities.setPreference(UserLoginActivity.this, CommonUtilities.pref_UserId, signUpModel.response.user_data.user_id);
                        
                        if (signUpModel.response.user_data.flag.equals("1")) {
                            if (signUpModel.response.user_data.user_active_status.equals("1")) {
                                CommonUtilities.ShowToast(UserLoginActivity.this, "Login successful!");
                                
                                // Navigate to MainActivity
                                Intent i = new Intent(UserLoginActivity.this, MainActivity.class);
                                startActivity(i);
                                finishAffinity();
                            } else {
                                CommonUtilities.alertdialog(UserLoginActivity.this, signUpModel.response.msg);
                            }
                        } else if (signUpModel.response.user_data.flag.equals("0")) {
                            // User needs to complete registration
                            Intent i = new Intent(UserLoginActivity.this, SignUpMobile.class);
                            startActivity(i);
                            finish();
                        } else {
                            CommonUtilities.ShowToast(UserLoginActivity.this, signUpModel.response.msg);
                        }
                    } else {
                        CommonUtilities.ShowToast(UserLoginActivity.this, signUpModel.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(UserLoginActivity.this, "Something went wrong!");
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

