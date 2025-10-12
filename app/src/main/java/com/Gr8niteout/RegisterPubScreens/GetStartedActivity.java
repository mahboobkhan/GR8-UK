package com.Gr8niteout.RegisterPubScreens;

import androidx.appcompat.app.AppCompatActivity;

import static com.Gr8niteout.RegisterPubScreens.ValidationCases.discountResult;
import static com.Gr8niteout.RegisterPubScreens.ValidationCases.params;
import static com.Gr8niteout.RegisterPubScreens.ValidationCases.vcFeatureImagePath;
import static com.Gr8niteout.RegisterPubScreens.ValidationCases.vcOtherImagesPathList;
import static com.Gr8niteout.RegisterPubScreens.ValidationCases.vcProfilePicturePath;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.Gr8niteout.R;

import java.util.ArrayList;
import java.util.HashMap;

public class GetStartedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        String str = "Thank you for using Gr8NiteOut. Please fill all the details below accuratley. Once you’ve completed the registration process you will receive an email asking you to verify your account. Until your account is verified you won’t be able to log in or use Gr8NiteOut.com. Rest assured your data will be transmitted via a secure connection.";
        String[] strArray = str.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }
        TextView textView = findViewById(R.id.textView);
        textView.setText(builder.toString());

        String textTitle = "Register your pub for free";
        String[] titleArray = textTitle.split(" ");
        StringBuilder builderTitle = new StringBuilder();
        for (String s : titleArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builderTitle.append(cap + " ");
        }
        TextView txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setText(builderTitle.toString());



        findViewById(R.id.tvGetStarted).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                params = new HashMap<>();
                vcProfilePicturePath = "";
                vcFeatureImagePath = "";
                vcOtherImagesPathList = new ArrayList<>();
                discountResult = "";

                startActivity(new Intent(GetStartedActivity.this,PersonalInformationActivity1.class));
                finish();
            }
        });
    }
}