package com.Gr8niteout.ActivityFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.DrinkModel;
import com.Gr8niteout.signup.PinEntryEditText;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.BindView;


public class ActivityRedeemCode extends AppCompatActivity {

//    @BindView(R.id.back)
    ImageView back;
//    @BindView(R.id.txtEnterConfitmation)
    TextView txtEnterConfitmation;
//    @BindView(R.id.txt_pin_entry)
    PinEntryEditText txtPinEntry;
//    @BindView(R.id.btnConfirm)
    Button btnConfirm;



    String pub_id, pub_credit_id, amount;
    public String code=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pub_credit_redeem_code);
        ButterKnife.bind(this);

        back = findViewById(R.id.back);
        txtEnterConfitmation = findViewById(R.id.txtEnterConfitmation);
        txtPinEntry = findViewById(R.id.txt_pin_entry);
        btnConfirm = findViewById(R.id.btnConfirm);

        pub_id = getIntent().getExtras().getString("pub_id");
        pub_credit_id = getIntent().getExtras().getString("pub_credit_id");
        amount = getIntent().getExtras().getString("amount");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtPinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence str) {
                if(str.toString().length()>3)
                {
                    code=str.toString();
                }
                else
                {
                    code=null;
                    Toast.makeText(ActivityRedeemCode.this, "please enter valid code", Toast.LENGTH_SHORT).show();

                }


            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(code!=null)
                {
                    RedeemCodeService(pub_id,pub_credit_id,code,true);
                }
                else {
                    Toast.makeText(ActivityRedeemCode.this, "Please enter valid code", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    public void RedeemCodeService(String pub_id, String pub_credit_id, final String pubcode, boolean loader) {

        Map<String, String> params = new HashMap<String, String>();
//        params.put(CommonUtilities.key_user_id, CommonUtilities.getPreference(ActivityRedeemCode.this, CommonUtilities.pref_UserId));
        params.put(CommonUtilities.key_pub_credit_id, pub_credit_id);
        params.put(CommonUtilities.key_pub_id, pub_id);
        params.put(CommonUtilities.key_redeem_code, pubcode);
        params.put("redeem_amount", amount);
        params.put("reciever_id", CommonUtilities.getPreference(ActivityRedeemCode.this, CommonUtilities.pref_UserId));

        ServerAccess.getResponse(ActivityRedeemCode.this, CommonUtilities.key_check_reedem_code, params, loader, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
               DrinkModel modelDrink = new DrinkModel().DrinkModel(result);

                if(modelDrink.response.code.equals("ERR001"))
                {
                    code=null;
                    Toast.makeText(ActivityRedeemCode.this, modelDrink.response.msg, Toast.LENGTH_SHORT).show();
                    txtPinEntry.setText(null);

                    if(modelDrink.response.msg.equalsIgnoreCase("Insufficient redeem amount")){
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result",result);
                        setResult(Activity.RESULT_CANCELED,returnIntent);
                        finish();
                    }

                }
                else
                {
                    Toast.makeText(ActivityRedeemCode.this, modelDrink.response.msg, Toast.LENGTH_SHORT).show();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",result);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();

                }

            }

            @Override
            public void onError(String error) {

            }
        });
    }

}
