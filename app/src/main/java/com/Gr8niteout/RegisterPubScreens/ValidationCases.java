package com.Gr8niteout.RegisterPubScreens;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.EditText;

import com.nguyenhoanglam.imagepicker.model.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ValidationCases {
    public static Map<String, String> params = new HashMap<>();
    public static String vcProfilePicturePath = "";
    public static String vcFeatureImagePath = "";
    public static ArrayList<Image> vcOtherImagesPathList = new ArrayList<>();
    public static String discountResult = "";

    public boolean isEmptyEditText(EditText[] list, String[] errorTextList){
        boolean check = false;

        for (int i = 0; i < list.length; i++) {
            if (list[i].getText().toString().trim().equals("")) {
                list[i].setError(errorTextList[i]);
                check = true;
            }
        }

        return check;
    }

    public void firstLetterSpace(EditText[] list){
        for(EditText editText: list){
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(editText.getText().toString().length()<=1){
                        if(editable.toString().equals(" ")){
                            editText.setText("");
                        }
                    }
                }
            });
        }
    }

    public boolean matchTwoFields(EditText editText, EditText editText1, String message){
        boolean check = false;
        if(!editText.getText().toString().trim().equals(editText1.getText().toString().trim())){
            editText1.setError(message);
            check = true;
        }
        return check;
    }

    public boolean isEmailValid(EditText email) {
        boolean check = false;
        if(!email.getText().toString().equals("")) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
                email.setError("Please enter a valid email!");
                check = true;
            }
        }else{
            email.setError("Email address is required.");
            check = true;
        }
        return check;
    }

}
