package com.Gr8niteout.ActivityFragment;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.MediaController;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CircleTransform;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.Dialog;
import com.Gr8niteout.model.DrinkModel;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.BindView;


public class DrinkDetails extends AppCompatActivity {

//    @BindView(R.id.crossimageView)
    ImageView crossimageView;
//    @BindView(R.id.imageView)
    ImageView imageView;
//    @BindView(R.id.attachimage)
    ImageView attachimage;
//    @BindView(R.id.imgPlaceHolder)
    ImageView imgPlaceHolder;
    VideoView VideoPlaceHolder;
//    @BindView(R.id.pubimageView)
    ImageView pubimageView;
//    @BindView(R.id.txtPubName)
    TextView txtPubName;
//    @BindView(R.id.days_ago)
    TextView days_ago;
//    @BindView(R.id.txtStatic)
    TextView txtStatic;
//    @BindView(R.id.txtName)
    TextView txtName;
//    @BindView(R.id.txtMsg)
    TextView txtMsg;
//    @BindView(R.id.txtMsgDesc)
    TextView txtMsgDesc;
//    @BindView(R.id.First_Lett)
    TextView First_Lett;
//    @BindView(R.id.txtamount)
    TextView txtamount;
//    @BindView(R.id.txtamountvalue)
    TextView txtamountvalue;
//    @BindView(R.id.txtAttached)
    TextView txtAttached;
//    @BindView(R.id.expiry_text)
    TextView expiry_text;
//    @BindView(R.id.btnPubCredit)
    Button btnPubCredit;
//    @BindView(R.id.layoutImageAttached)
    LinearLayout layoutImageAttached;
    LinearLayout layoutVideoAttached;
//    @BindView(R.id.msg_layout)
    LinearLayout msg_layout;
//    @BindView(R.id.image_layout)
    RelativeLayout image_layout;

//    @BindView(R.id.btnRedeemCode)
    Button btnRedeemCode;

    MediaController mediaController;


    DrinkModel.response.drinklist.drinks_lists drinkModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_details);
        ButterKnife.bind(this);

        crossimageView = (ImageView) findViewById(R.id.crossimageView);
        imageView = (ImageView) findViewById(R.id.imageView);
        attachimage = (ImageView) findViewById(R.id.attachimage);
        imgPlaceHolder = (ImageView) findViewById(R.id.imgPlaceHolder);
        pubimageView = (ImageView) findViewById(R.id.pubimageView);
        txtPubName = (TextView) findViewById(R.id.txtPubName);
        days_ago = (TextView) findViewById(R.id.days_ago);
        txtStatic = (TextView) findViewById(R.id.txtStatic);
        txtName = (TextView) findViewById(R.id.txtName);
        txtMsg = (TextView) findViewById(R.id.txtMsg);
        txtMsgDesc  = (TextView) findViewById(R.id.txtMsgDesc);
        First_Lett = (TextView) findViewById(R.id.First_Lett);
        txtamount = (TextView) findViewById(R.id.txtamount);
        txtamountvalue = (TextView) findViewById(R.id.txtamountvalue);
        txtAttached = (TextView) findViewById(R.id.txtAttached);
        expiry_text = (TextView) findViewById(R.id.expiry_text);
        btnPubCredit = (Button) findViewById(R.id.btnPubCredit);
        layoutImageAttached = (LinearLayout) findViewById(R.id.layoutImageAttached);
        layoutVideoAttached = (LinearLayout) findViewById(R.id.layoutVideoAttached);
        msg_layout = (LinearLayout) findViewById(R.id.msg_layout);
        image_layout = (RelativeLayout) findViewById(R.id.image_layout);
        btnRedeemCode = (Button) findViewById(R.id.btnRedeemCode);
        VideoPlaceHolder = (VideoView) findViewById(R.id.VideoPlaceHolder);





        Gson gson = new Gson();
        drinkModel = (DrinkModel.response.drinklist.drinks_lists) gson.fromJson(CommonUtilities.getPreference(this,
                CommonUtilities.pref_drinks_detail), DrinkModel.response.drinklist.drinks_lists.class);

        setFont();
        if (drinkModel != null) {
            btnPubCredit.setText("Pub Credit Code: " + drinkModel.temp_pub_rcode);
            txtamountvalue.setText("£" + drinkModel.amount);
            if (drinkModel.message.equals("")) {
                msg_layout.setVisibility(View.GONE);
            } else {
                msg_layout.setVisibility(View.VISIBLE);
                txtMsgDesc.setText(drinkModel.message);
            }
            txtName.setText(drinkModel.names);
            txtPubName.setText(drinkModel.pub_name);
            expiry_text.setText(drinkModel.expire_days);
            days_ago.setText(drinkModel.days_ago);

            if (drinkModel.expire_days.equals("Redeemed")) {
                expiry_text.setTextColor(Color.parseColor("#1eaca1"));
            }
            if (drinkModel.expire_days.equals("Expired") || drinkModel.expire_days.equals("Redeemed")) {
                btnRedeemCode.setVisibility(View.GONE);
            } else {
                btnRedeemCode.setVisibility(View.VISIBLE);
            }

            mediaController = new MediaController(this);

            if (!drinkModel.video_attached.isEmpty()) {
                layoutImageAttached.setVisibility(View.GONE);
                layoutVideoAttached.setVisibility(View.VISIBLE);

                Uri uri = Uri.parse(CommonUtilities.Gr8niteoutURL + CommonUtilities.Pub_attach_image_URL +drinkModel.video_attached);

                // sets the resource from the
                // videoUrl to the videoView
                VideoPlaceHolder.setVideoURI(uri);

                // creating object of
                // media controller class

                // sets the anchor view
                // anchor view for the videoView
                mediaController.setAnchorView(VideoPlaceHolder);

                // sets the media player to the videoView
                mediaController.setMediaPlayer(VideoPlaceHolder);

                // sets the media controller to the videoView
                VideoPlaceHolder.setMediaController(mediaController);
                VideoPlaceHolder.start();

            } else
            {
                layoutImageAttached.setVisibility(View.GONE);
            }

            if (!drinkModel.image_attached.isEmpty()){
                layoutImageAttached.setVisibility(View.VISIBLE);
                layoutVideoAttached.setVisibility(View.GONE);

//                changed on 28-jan-2019
                Picasso.get()
                        .load(CommonUtilities.Gr8niteoutURL + CommonUtilities.Pub_attach_image_URL +
                                drinkModel.image_attached)
//                        .error(R.mipmap.img_placeholder).placeholder(R.mipmap.img_placeholder)
                        .into(attachimage, new Callback() {
                            @Override
                            public void onSuccess() {
                                findViewById(R.id.imgPlaceHolder12).setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });

            }else {
                layoutImageAttached.setVisibility(View.GONE);
            }

            if (drinkModel.photo.equals("")) {
                image_layout.setVisibility(View.VISIBLE);
                image_layout.setBackgroundResource(R.drawable.round);
                if(!drinkModel.names.isEmpty()) {
                    First_Lett.setText(drinkModel.names.substring(0, 1));
                }
            } else {
//                changed on 28-jan-2019
                Picasso.get()
                        .load(CommonUtilities.Gr8niteoutURL + CommonUtilities.User_Profile_URL +
                                drinkModel.photo)
                        .error(R.mipmap.user).placeholder(R.mipmap.user) // optional
                        .transform(new CircleTransform())
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
            }
//            changed on 28-jan-2019
            Picasso.get()
                    .load(CommonUtilities.Gr8niteoutURL + CommonUtilities.Pub_Banner_URL +
                            drinkModel.pub_img)
                    .into(pubimageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            imgPlaceHolder.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });


        }

        btnRedeemCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DrinkDetails.this, ActivityRedeemCode.class);
                showEnterCreditScoreDialog(drinkModel.total_amount);
//                intent.putExtra("pub_id", drinkModel.pub_id);
//                intent.putExtra("pub_credit_id", drinkModel.pub_credit_id);
//                intent.putExtra("temp_pub_rcode", drinkModel.temp_pub_rcode);
//                startActivityForResult(intent, 1);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                btnRedeemCode.setVisibility(View.GONE);
                expiry_text.setText("Redeemed");
                expiry_text.setTextColor(Color.parseColor("#1eaca1"));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void showEnterCreditScoreDialog(String totalAmount){
        Dialog dialog = new Dialog(DrinkDetails.this);
        dialog.setContentView(R.layout.enter_credit_score_dialog);
        dialog.setCancelable(true);
        dialog.show();
        TextView tvHeading = dialog.findViewById(R.id.tvHeading);
        TextView subText = dialog.findViewById(R.id.subText);
        TextView tvContinue = dialog.findViewById(R.id.tvContinue);
        EditText edtAmount = dialog.findViewById(R.id.edtAmount);

        String amountText = "Total amount available for redeem : £" + totalAmount;
        subText.setText(amountText);

        CommonUtilities.setFontFamily(DrinkDetails.this, subText, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(DrinkDetails.this, tvHeading, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(DrinkDetails.this, tvContinue, CommonUtilities.AvenirLTStd_Medium);

        tvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double total = Double.parseDouble(totalAmount);
                double enteredAmount = edtAmount.getText().toString().equals("") ?
                        0 : Double.parseDouble(edtAmount.getText().toString());

                if (edtAmount.getText().toString().equals("")) {
                    CommonUtilities.ShowToast(DrinkDetails.this, "Please enter amount to continue.");
                } else if (enteredAmount > total) {
                    CommonUtilities.ShowToast(DrinkDetails.this, "Amount should not be greater than total available amount.");
                } else if (enteredAmount <= 0) {
                    CommonUtilities.ShowToast(DrinkDetails.this, "Amount should be greater than 0.");
                } else {
                    Intent intent = new Intent(DrinkDetails.this, ActivityRedeemCode.class);
                    intent.putExtra("pub_id", drinkModel.pub_id);
                    intent.putExtra("pub_credit_id", drinkModel.pub_credit_id);
                    intent.putExtra("amount", String.valueOf(enteredAmount));
                    startActivityForResult(intent, 1);
                    dialog.dismiss();
                }

//                int total = Integer.parseInt(totalAmount);
//                int enteredAmount = edtAmount.getText().toString().equals("") ?
//                        0 : Integer.parseInt(edtAmount.getText().toString());
//
//                if(edtAmount.getText().toString().equals("")){
//                 CommonUtilities.ShowToast(DrinkDetails.this, "Please enter amount to continue.");
//                } else if(enteredAmount > total){
//                    CommonUtilities.ShowToast(DrinkDetails.this, "Amount should not be greater than total available amount.");
//                } else if(enteredAmount < 1){
//                    CommonUtilities.ShowToast(DrinkDetails.this, "Amount should be greater than 0.");
//                } else {
//                    Intent intent = new Intent(DrinkDetails.this, ActivityRedeemCode.class);
//                    intent.putExtra("pub_id", drinkModel.pub_id);
//                    intent.putExtra("pub_credit_id", drinkModel.pub_credit_id);
////                    intent.putExtra("temp_pub_rcode", drinkModel.temp_pub_rcode);
//                    intent.putExtra("amount", edtAmount.getText().toString());
//                    startActivityForResult(intent, 1);
//                    dialog.dismiss();
//                }
            }
        });
    }

    public void PubCreditClick(View v) {

        if (drinkModel != null) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(drinkModel.temp_pub_rcode);
            Toast.makeText(DrinkDetails.this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
        }

    }

    public void close(View v) {
        onBackPressed();
    }

    public void setFont() {
        CommonUtilities.setFontFamily(DrinkDetails.this, txtPubName, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(DrinkDetails.this, expiry_text, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(DrinkDetails.this, txtName, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(DrinkDetails.this, txtStatic, CommonUtilities.Avenir); //Avenir (OT1) : Regular
        CommonUtilities.setFontFamily(DrinkDetails.this, days_ago, CommonUtilities.Avenir); //Avenir (OT1) : Regular
        CommonUtilities.setFontFamily(DrinkDetails.this, txtamount, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(DrinkDetails.this, txtamountvalue, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(DrinkDetails.this, txtMsg, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(DrinkDetails.this, txtMsgDesc, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(DrinkDetails.this, txtAttached, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(DrinkDetails.this, btnPubCredit, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(DrinkDetails.this, btnRedeemCode, CommonUtilities.Avenir_Heavy);
    }
}
