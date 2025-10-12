package com.Gr8niteout.PubDashboardScreens.PubFragments;

import static android.content.Context.MODE_PRIVATE;
import static com.Gr8niteout.RegisterPubScreens.ValidationCases.params;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.Gr8niteout.PubDashboardScreens.DashboardHomeActivity;
import com.Gr8niteout.PubDashboardScreens.Models.ChangePasswordModel;
import com.Gr8niteout.R;
import com.Gr8niteout.RegisterPubScreens.PersonalInformationActivity1;
import com.Gr8niteout.RegisterPubScreens.PersonalInformationActivity2;
import com.Gr8niteout.RegisterPubScreens.ValidationCases;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.CheckUserEmailModel;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordFragment extends Fragment {

    EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    TextView tvSave;
    ChangePasswordModel changePasswordModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        edtOldPassword = view.findViewById(R.id.edtOldPassword);
        edtNewPassword = view.findViewById(R.id.edtNewPassword);
        edtConfirmPassword = view.findViewById(R.id.edtConfirmPassword);
        tvSave = view.findViewById(R.id.tvSave);

        EditText[] list = new EditText[]{edtOldPassword, edtNewPassword, edtConfirmPassword};
        ValidationCases validation = new ValidationCases();
        validation.firstLetterSpace(list);

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check1 = false;
                boolean check2 = false;
                boolean check3 = false;

                if(edtOldPassword.getText().toString().trim().equals("")){
                    edtOldPassword.setError("Please enter old password!");
                    check1 = true;
                }else{
                    if(edtOldPassword.getText().length() < 8){
                        edtOldPassword.setError("Password must be 8-20 characters long.");
                        check1 = true;
                    }else{
                        edtOldPassword.setError(null);
                    }
                }

                if(edtNewPassword.getText().toString().trim().equals("")){
                    edtNewPassword.setError("Please enter new password!");
                    check2 = true;
                }else{
                    if(edtNewPassword.getText().length() < 8){
                        edtNewPassword.setError("Password must be 8-20 characters long.");
                        check2 = true;
                    }else {
                        edtNewPassword.setError(null);
                    }
                }

                if(edtConfirmPassword.getText().toString().trim().equals("")){
                    edtConfirmPassword.setError("Please enter confirm password!");
                    check3 = true;
                }else{
                    if(edtConfirmPassword.getText().length() < 8){
                        edtConfirmPassword.setError("Password must be 8-20 characters long.");
                        check3 = true;
                    } else if(!edtConfirmPassword.getText().toString().trim().equals(edtNewPassword.getText().toString().trim())){
                        edtConfirmPassword.setError("Password and confirm password must be same.");
                        check3 = true;
                    }else {
                        edtConfirmPassword.setError(null);
                    }
                }

                if(!check1 && !check2 && !check3){
                    CommonUtilities.hideKeyboard(true, getActivity());
                    callChangePasswordApi();
                }

            }
        });

        return view;
    }

    public void callChangePasswordApi() {
        SharedPreferences preferences = getContext().getSharedPreferences("pub_details",MODE_PRIVATE);

        Map<String, String> paramsTemp = new HashMap<>();
        paramsTemp.put("owner_id", preferences.getString("owner_id",""));
        paramsTemp.put("old_password", edtOldPassword.getText().toString().trim());
        paramsTemp.put("new_password", edtNewPassword.getText().toString().trim());

        ServerAccess.getResponse(getContext(), CommonUtilities.key_change_password, paramsTemp, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                changePasswordModel = new ChangePasswordModel().ChangePasswordModel(result);
                CommonUtilities.ShowToast(getContext(),changePasswordModel.response.msg);

                if(changePasswordModel.response.code.equals(CommonUtilities.key_success_code)){
                    ((DashboardHomeActivity) getContext()).goToLoginScreen();
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(getContext(), "Something went wrong!");
            }
        });
    }

}