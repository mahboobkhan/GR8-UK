package com.Gr8niteout.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.PubForgotPasswordModel;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PubForgotPasswordActivity extends AppCompatActivity {

    TextView tvSend;
    EditText edtEmail;
    PubForgotPasswordModel pubForgotPasswordModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub_forgot_password);

        tvSend = findViewById(R.id.tvSend);
        edtEmail = findViewById(R.id.edtEmail);

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtEmail.getText().toString().trim().equals("")){
                    edtEmail.setError("Please enter email!");
                }else if(isEmailValid(edtEmail)){
                    edtEmail.setError("Please enter valid email!");
                }else{
                    edtEmail.setError(null);
                    callForgotPasswordApi();
                }
            }
        });

    }

    public boolean isEmailValid(EditText email) {
        boolean check = false;

        final Pattern EMAIL_REGEX = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);
        if (!EMAIL_REGEX.matcher(email.getText().toString()).matches()) {
            check = true;
        }

        return check;
    }

    public void callForgotPasswordApi() {
        Map<String, String> paramsTemp = new HashMap<>();
        paramsTemp.put("email", edtEmail.getText().toString().trim());

        ServerAccess.getResponse(PubForgotPasswordActivity.this, CommonUtilities.key_pub_forgot_password, paramsTemp, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                pubForgotPasswordModel = new PubForgotPasswordModel().PubForgotPasswordModel(result);
                if(pubForgotPasswordModel != null){
                    CommonUtilities.ShowToast(PubForgotPasswordActivity.this, pubForgotPasswordModel.response.msg);
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(PubForgotPasswordActivity.this, "Something went wrong!");
            }
        });
    }

}