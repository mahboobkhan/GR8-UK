package com.Gr8niteout.stripe;

import static android.content.Context.MODE_PRIVATE;
import static com.Gr8niteout.RegisterPubScreens.ValidationCases.params;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.Gr8niteout.R;
import com.Gr8niteout.RegisterPubScreens.UserAgreementActivity;
import com.Gr8niteout.buycredits.StripeWebViewActivity;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.StripeAccountAddModel;

import java.util.HashMap;
import java.util.Map;

public class StripeConnectScreen  extends Fragment
{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.stripe_connect_layout, container, false);

        Button connectStripeButton =  view.findViewById(R.id.connectStripeButton);


        connectStripeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences preferences = getContext().getSharedPreferences("pub_details", MODE_PRIVATE);
                String pub_id  = preferences.getString("pub_id", "");
                callConnectStripeApi(Integer.parseInt(pub_id));

            }
        });


        return view;
    }


    public void callConnectStripeApi(int pubId){

        Log.d("Email", "callConnectStripeApi: "+params.get("email"));
        Map<String, String> paramsTemp = new HashMap<>();
        paramsTemp.put("pub_id", String.valueOf(pubId));
        ServerAccess.getResponse(getContext(), CommonUtilities.key_stripe_account_add, paramsTemp, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                StripeAccountAddModel model = new StripeAccountAddModel().StripeAccountAddModel(result);

                if(model != null){
                    if(model.response.status.equals(CommonUtilities.key_Success)){
                        Intent intent = new Intent(getContext(), StripeWebViewActivity.class);
                        intent.putExtra("ViewUrl", model.response.data.url);
                        intent.putExtra("pub_id", String.valueOf(pubId));
                        intent.putExtra("successUrl", "");
                        intent.putExtra("failureUrl", "");
                        intent.putExtra("isFromUserLogin", 2);
                        intent.putExtra("email", params.get("email"));
                        startActivity(intent);
                    }else{
                        CommonUtilities.ShowToast(getContext(), model.response.msg);
                    }
                }

            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(getContext(), "Something went wrong!");
            }
        });
    }
}
