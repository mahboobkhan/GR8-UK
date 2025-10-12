package com.Gr8niteout.RegisterPubScreens;

import static com.Gr8niteout.RegisterPubScreens.ValidationCases.params;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.Gr8niteout.R;

public class PersonalInformationActivity2 extends AppCompatActivity {

    EditText edtFullName, edtMobileNumber, edtConfirmMobileNumber;
    ValidationCases validation;

    EditText[] list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information2);

        TextView btnBack = findViewById(R.id.btnBack);
        TextView btnNext = findViewById(R.id.btnNext);

        edtFullName = findViewById(R.id.edtFullName);
        edtMobileNumber = findViewById(R.id.edtMobileNumber);
        edtConfirmMobileNumber = findViewById(R.id.edtConfirmMobileNumber);

        validation = new ValidationCases();
        list = new EditText[]{edtFullName, edtMobileNumber, edtConfirmMobileNumber};

        validation.firstLetterSpace(list);

        updateFields();
        edtFullName.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                params.put("recommend_fullname", edtFullName.getText().toString());
                params.put("recommend_phone", edtMobileNumber.getText().toString());
                params.put("recommend_cphone", edtConfirmMobileNumber.getText().toString());

                Intent intent = new Intent(PersonalInformationActivity2.this, PremisesActivity.class);

                if (edtFullName.getText().toString().equals("")) {
                    if (edtMobileNumber.getText().toString().equals("") && edtConfirmMobileNumber.getText().toString().equals("")) {
                        startActivity(intent);
                    } else {
                        boolean check1 = validation.matchTwoFields(edtMobileNumber, edtConfirmMobileNumber, "Mobile Number and confirm number must be same.");
                        if (!check1) {
                            startActivity(intent);
                        }
                    }
                } else {
                    EditText[] listNew = new EditText[]{edtMobileNumber, edtConfirmMobileNumber};
                    String[] errorTextList = new String[]{
                            "The Recommended Person Mobile Number field is required.",
                            "Mobile Number and confirm number must be same."
                    };
                    boolean check1 = validation.isEmptyEditText(listNew, errorTextList);
                    boolean check2 = validation.matchTwoFields(edtMobileNumber, edtConfirmMobileNumber, "Mobile Number and confirm number must be same.");
                    if (!check1 && !check2) {
                        startActivity(intent);
                    }
                }
            }
        });
    }

    public void updateFields(){

        if(params.get("recommend_fullname") != null) {
            edtFullName.setText(params.get("recommend_fullname"));
        }

        if(params.get("recommend_phone") != null) {
            edtMobileNumber.setText(params.get("recommend_phone"));
        }

        if(params.get("recommend_cphone") != null) {
            edtConfirmMobileNumber.setText(params.get("recommend_cphone"));
        }
    }


}