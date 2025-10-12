package com.Gr8niteout;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.Gr8niteout.buycredits.ShareActivity;
import com.Gr8niteout.signup.SignupLogin;

import butterknife.ButterKnife;
import butterknife.BindView;

public class HowToUseApp extends AppCompatActivity {

//    @BindView(R.id.btnNext)
    Button btnNext;

//    @BindView(R.id.how_use_layout)
    RelativeLayout how_use_layout;

    String flag = "1";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howto_useapp);
        ButterKnife.bind(this);
        btnNext = findViewById(R.id.btnNext);
        how_use_layout = findViewById(R.id.how_use_layout);
    }
    public void Next(View v) {

        if(flag.equals("1")) {
            how_use_layout.setBackgroundResource(R.mipmap.buy_credits);
            btnNext.setBackgroundResource(R.mipmap.next);
            flag = "2";
        }

        else if(flag.equals("2")){
            how_use_layout.setBackgroundResource(R.mipmap.upload_photos);
            btnNext.setBackgroundResource(R.mipmap.get_started);
            flag = "3";
        }

        else if(flag.equals("3")){
            Intent i = new Intent(HowToUseApp.this,SignupLogin.class);
            startActivity(i);
            finish();
        }
    }
}
