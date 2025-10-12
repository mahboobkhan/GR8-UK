package com.Gr8niteout.buycredits;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Gr8niteout.MainActivity;
import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.facebook.share.Share;

import butterknife.ButterKnife;
import butterknife.BindView;

public class ShareActivity extends AppCompatActivity {
//    @BindView(R.id  .layoutClose)
    RelativeLayout layoutClose;

//    @BindView(R.id.txtShare)
//    TextView txtShare;
//    @BindView(R.id.txtPurchase)
//    TextView txtPurchase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_purchase);
        ButterKnife.bind(this);
        layoutClose = (RelativeLayout) findViewById(R.id.layoutClose);
        TextView buyAnotherDrink = findViewById(R.id.buyAnotherDrinkBtn);
        buyAnotherDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMain();
            }
        });

        layoutClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMain();
            }
        });
//        CommonUtilities.setFontFamily(ShareActivity.this,txtPurchase,CommonUtilities.AvenirLTStd_Medium);
//        CommonUtilities.setFontFamily(ShareActivity.this,txtShare,CommonUtilities.Avenir_Heavy);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToMain();
    }

    private void goToMain(){
        CommonUtilities.isBack_credit = true;
        Intent intent = new Intent(ShareActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        ShareActivity.this.finish();
    }
}



