package com.Gr8niteout.RegisterPubScreens;

import static android.graphics.BitmapFactory.decodeFile;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

import static com.Gr8niteout.RegisterPubScreens.ValidationCases.params;
import static com.Gr8niteout.RegisterPubScreens.ValidationCases.vcFeatureImagePath;
import static com.Gr8niteout.RegisterPubScreens.ValidationCases.vcOtherImagesPathList;
import static com.Gr8niteout.RegisterPubScreens.ValidationCases.vcProfilePicturePath;
import static com.Gr8niteout.myprofile.EditProfileActivity.fileUri;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.pub.SingleMediaScanner;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    TextView tvProfilePicture, tvRemoveProfile, tvFeaturedImage, tvRemoveFeaturedImage, btnBack, btnNext, imgUploadText,
            tvRemoveOtherImage1, tvRemoveOtherImage2, tvRemoveOtherImage3, tvRemoveOtherImage4, tvUploadOtherImage, tvOtherPhotos;
    ImageView imgProfile, imgFeaturedImage, imgOther1, imgOther2, imgOther3, imgOther4;
    LinearLayout btnUploadProfile, btnUploadFeaturedImage, otherImageLayout;
    Spinner liveSportsSpinner, foodSpinner, karaokeSpinner, liveMusicSpinner, doorChargeSpinner, freeWifiSpinner, disabledAccessSpinner,
            quizNightsSpinner, realAleSpinner, minimumAgeSpinner, childrenAllowedSpinner, dogsAllowedSpinner;
    EditText edtLiveSports, edtKaraoke, edtLiveMusic, edtDoorCharge, edtQuizNights, edtMinimumAge, edtAverageDrinkPrice,
            edtPremisesName;

    private final int CAMERA_REQUEST_CODE = 100;
    private final int GALLERY_REQUEST_CODE = 2000;
    private final String IMAGE_DIRECTORY_NAME = "Gr8niteout";

    int imgUploadCount = 0;
    String timeStamp;
    boolean imgAttachedProfile = false;
    boolean imgAttachedFeatured = false;
    int checkImage;

    List<String> items;
    private AlertDialog dialogPic;
    private ArrayList<Image> images = new ArrayList<>();
    ArrayList<Image> otherImagesPathList = new ArrayList<>();

    String[] yesNoItems = new String[]{"Yes", "No"};
    Spinner[] spinnerList;
    Spinner[] spinnerWithEditText;
    EditText[] editTextSpinnerList;
    EditText[] editTextList;
    ValidationCases validation;

    String profilePicturePath = "";
    String featuredImagePath = "";
    String[] otherImagePathTemp = new String[4];

//    public CouponCodeModel couponCodeModel;
//    boolean isApplyButtonTapped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initView();
        validation = new ValidationCases();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        spinnerList = new Spinner[]{
                liveSportsSpinner, foodSpinner, karaokeSpinner, liveMusicSpinner, doorChargeSpinner, freeWifiSpinner, disabledAccessSpinner,
                quizNightsSpinner, realAleSpinner, minimumAgeSpinner, childrenAllowedSpinner, dogsAllowedSpinner
        };

        spinnerWithEditText = new Spinner[]{
                liveSportsSpinner, karaokeSpinner, liveMusicSpinner, doorChargeSpinner, quizNightsSpinner, minimumAgeSpinner,
        };

        editTextSpinnerList = new EditText[]{
                edtLiveSports, edtKaraoke, edtLiveMusic, edtDoorCharge, edtQuizNights, edtMinimumAge
        };

        editTextList = new EditText[]{
                edtLiveSports, edtKaraoke, edtLiveMusic, edtDoorCharge, edtQuizNights, edtMinimumAge, edtAverageDrinkPrice
        };

        for (Spinner spinner : spinnerList) {
            spinner.setAdapter(getArrayAdapter(yesNoItems));
        }

        updateFields();

        validation.firstLetterSpace(editTextList);

        showHideEditText(spinnerWithEditText, editTextSpinnerList);

        imgUploadText.setText(imgUploadCount + "");

        edtPremisesName.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        edtKaraoke.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        edtLiveMusic.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        edtLiveSports.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        edtQuizNights.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

//        btnDiscountApply.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(edtDiscountCode.getText().toString().equals("")){
//                    CommonUtilities.ShowToast(ProfileActivity.this,"Please enter coupon code to apply!");
//                }else{
//                    callCheckDiscountApi();
//                }
//            }
//        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//        edtDiscountCode.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                isApplyButtonTapped = false;
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!imgAttachedProfile) {
                    tvProfilePicture.setError("Please upload pub profile picture.");
                }else{
                    tvProfilePicture.setError(null);
                }

                if (!imgAttachedFeatured) {
                    tvFeaturedImage.setError("Please upload Featured Image.");
                }else{
                    tvFeaturedImage.setError(null);
                }

                EditText[] newEditTextList = new EditText[]{edtDoorCharge, edtMinimumAge};
                Spinner[] newSpinnerList = new Spinner[]{doorChargeSpinner, minimumAgeSpinner};
                String[] errorTextList = new String[]{"Door charge is required.", "Minimum age is required."};

                boolean check = isEmptyEditText(newEditTextList, newSpinnerList, errorTextList);
//                boolean check2 = false;
//                if (edtVerificationCode.getText().toString().equals("")) {
//                    edtVerificationCode.setError("Please enter verification code.");
//                    check2 = true;
//                }

                boolean check3 = false;
                if (edtAverageDrinkPrice.getText().toString().equals("")) {
                    edtAverageDrinkPrice.setError("Average drink price is required.");
                    check3 = true;
                }else{
                    edtAverageDrinkPrice.setError(null);
                }

                if (imgAttachedProfile && imgAttachedFeatured && !check && !check3) {
                    StringBuilder otherImageArr = new StringBuilder();

                    params.put("about_pub", edtPremisesName.getText().toString());
                    params.put("pub_profile", getStringImage(profilePicturePath));
                    params.put("pub_featured_Image", getStringImage(featuredImagePath));

                    for(int i=0; i<imgUploadCount; i++){
                        if(i < imgUploadCount-1){
                            otherImageArr.append(getStringImage(otherImagePathTemp[i])).append(",");
                        }else{
                            otherImageArr.append(getStringImage(otherImagePathTemp[i]));
                        }
                    }

                    params.put("other_photos", String.valueOf(otherImageArr));

                    //live sports
                    if (liveSportsSpinner.getSelectedItem().toString().equals(yesNoItems[1])) {
                        params.put("1_status", "0");
                    } else {
                        params.put("1_status", "1");
                        params.put("1_comment", edtLiveSports.getText().toString());
                    }

                    //food
                    if (foodSpinner.getSelectedItem().toString().equals(yesNoItems[1])) {
                        params.put("2_status", "0");
                    } else {
                        params.put("2_status", "1");
                    }

                    //karaoke
                    if (karaokeSpinner.getSelectedItem().toString().equals(yesNoItems[1])) {
                        params.put("3_status", "0");
                    } else {
                        params.put("3_status", "1");
                        params.put("3_comment", edtKaraoke.getText().toString());
                    }

                    //live music
                    if (liveMusicSpinner.getSelectedItem().toString().equals(yesNoItems[1])) {
                        params.put("4_status", "0");
                    } else {
                        params.put("4_status", "1");
                        params.put("4_comment", edtLiveMusic.getText().toString());
                    }

                    //average drink price
                    params.put("5_comment", edtAverageDrinkPrice.getText().toString());

                    //door charge
                    if (doorChargeSpinner.getSelectedItem().toString().equals(yesNoItems[1])) {
                        params.put("6_status", "0");
                    } else {
                        params.put("6_status", "1");
                        params.put("6_comment", edtDoorCharge.getText().toString());
                    }

                    //free wifi
                    if (freeWifiSpinner.getSelectedItem().toString().equals(yesNoItems[1])) {
                        params.put("7_status", "0");
                    } else {
                        params.put("7_status", "1");
                    }

                    //disabled access
                    if (disabledAccessSpinner.getSelectedItem().toString().equals(yesNoItems[1])) {
                        params.put("8_status", "0");
                    } else {
                        params.put("8_status", "1");
                    }

                    //quiz nights
                    if (quizNightsSpinner.getSelectedItem().toString().equals(yesNoItems[1])) {
                        params.put("9_status", "0");
                    } else {
                        params.put("9_status", "1");
                        params.put("9_comment", edtQuizNights.getText().toString());
                    }

                    //real ale
                    if (realAleSpinner.getSelectedItem().toString().equals(yesNoItems[1])) {
                        params.put("10_status", "0");
                    } else {
                        params.put("10_status", "1");
                    }

                    //minimum age
                    if (minimumAgeSpinner.getSelectedItem().toString().equals(yesNoItems[1])) {
                        params.put("11_status", "0");
                    } else {
                        params.put("11_status", "1");
                        params.put("11_comment", edtMinimumAge.getText().toString());
                    }

                    //are children allowed
                    if (childrenAllowedSpinner.getSelectedItem().toString().equals(yesNoItems[1])) {
                        params.put("12_status", "0");
                    } else {
                        params.put("12_status", "1");
                    }

                    //are dogs allowed
                    if (dogsAllowedSpinner.getSelectedItem().toString().equals(yesNoItems[1])) {
                        params.put("13_status", "0");
                    } else {
                        params.put("13_status", "1");
                    }

//                    boolean checkCouponCode = true;

//                    if(edtDiscountCode.getText().toString().equals("")){
//                        params.put("couponcode","");
//                    }else{
//                        if(isApplyButtonTapped){
//                            if(couponCodeModel.response.apply_coupan.valid) {
//                                params.put("couponcode", edtDiscountCode.getText().toString());
//                            }else{
//                                checkCouponCode = false;
//                                CommonUtilities.ShowToast(ProfileActivity.this,"Please enter valid discount code!");
//                            }
//                        }else{
//                            CommonUtilities.ShowToast(ProfileActivity.this,"Please apply discount code first!");
//                            checkCouponCode = false;
//                        }
//                    }
//                    params.put("vcode", edtVerificationCode.getText().toString());

                    startActivity(new Intent(ProfileActivity.this, UserAgreementActivity.class));

                }
            }
        });

        tvUploadOtherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    String[] PERMISSIONS = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA};
                    if (!CommonUtilities.hasPermissions(ProfileActivity.this, PERMISSIONS)) {
                        CommonUtilities.setPermission(ProfileActivity.this, PERMISSIONS);
                    }else{
                        captureImageInitialization(4);
                    }
                }else{
                    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                    if (!CommonUtilities.hasPermissions(ProfileActivity.this, PERMISSIONS)) {
                        CommonUtilities.setPermission(ProfileActivity.this, PERMISSIONS);
                    } else {
                        captureImageInitialization(4);
                    }
                }
//                if(Build.VERSION.SDK_INT >= 30) {
//                    if (!Environment.isExternalStorageManager()){
//                        Intent intent = new Intent();
//                        intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                        Uri uri = Uri.fromParts("package", ProfileActivity.this.getPackageName(), null);
//                        intent.setData(uri);
//                        startActivity(intent);
//                    }else{
//                        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
//                        if (!CommonUtilities.hasPermissions(ProfileActivity.this, PERMISSIONS)) {
//                            CommonUtilities.setPermission(ProfileActivity.this, PERMISSIONS);
//                        } else {
//                            captureImageInitialization(4);
//                        }
//                    }
//                }else{
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
//                        if (!CommonUtilities.hasPermissions(ProfileActivity.this, PERMISSIONS)) {
//                            CommonUtilities.setPermission(ProfileActivity.this, PERMISSIONS);
//                        } else {
//                            captureImageInitialization(4);
//                        }
//                    } else {
//                        captureImageInitialization(4);
//                    }
//                }
            }
        });

        btnUploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    String[] PERMISSIONS = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA};
                    if (!CommonUtilities.hasPermissions(ProfileActivity.this, PERMISSIONS)) {
                        CommonUtilities.setPermission(ProfileActivity.this, PERMISSIONS);
                    }else{
                        captureImageInitialization(1);
                        checkImage = 1;
                    }
                }else{
                    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                    if (!CommonUtilities.hasPermissions(ProfileActivity.this, PERMISSIONS)) {
                        CommonUtilities.setPermission(ProfileActivity.this, PERMISSIONS);
                    } else {
                        captureImageInitialization(1);
                        checkImage = 1;
                    }
                }
//                if(Build.VERSION.SDK_INT >= 30) {
//                    if (!Environment.isExternalStorageManager()){
//                        Intent intent = new Intent();
//                        intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                        Uri uri = Uri.fromParts("package", ProfileActivity.this.getPackageName(), null);
//                        intent.setData(uri);
//                        startActivity(intent);
//                    }else{
//                        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
//                        if (!CommonUtilities.hasPermissions(ProfileActivity.this, PERMISSIONS)) {
//                            CommonUtilities.setPermission(ProfileActivity.this, PERMISSIONS);
//                        } else {
//                            captureImageInitialization(1);
//                            checkImage = 1;
//                        }
//                    }
//                }else{
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
//                        if (!CommonUtilities.hasPermissions(ProfileActivity.this, PERMISSIONS)) {
//                            CommonUtilities.setPermission(ProfileActivity.this, PERMISSIONS);
//                        } else {
//                            captureImageInitialization(1);
//                            checkImage = 1;
//                        }
//                    } else {
//                        captureImageInitialization(1);
//                        checkImage = 1;
//                    }
//                }
            }
        });

        btnUploadFeaturedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    String[] PERMISSIONS = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA};
                    if (!CommonUtilities.hasPermissions(ProfileActivity.this, PERMISSIONS)) {
                        CommonUtilities.setPermission(ProfileActivity.this, PERMISSIONS);
                    }else{
                        captureImageInitialization(1);
                        checkImage = 2;
                    }
                }else{
                    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                    if (!CommonUtilities.hasPermissions(ProfileActivity.this, PERMISSIONS)) {
                        CommonUtilities.setPermission(ProfileActivity.this, PERMISSIONS);
                    } else {
                        captureImageInitialization(1);
                        checkImage = 2;
                    }
                }
//                if(Build.VERSION.SDK_INT >= 30) {
//                    if (!Environment.isExternalStorageManager()){
//                        Intent intent = new Intent();
//                        intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                        Uri uri = Uri.fromParts("package", ProfileActivity.this.getPackageName(), null);
//                        intent.setData(uri);
//                        startActivity(intent);
//                    }else{
//                        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
//                        if (!CommonUtilities.hasPermissions(ProfileActivity.this, PERMISSIONS)) {
//                            CommonUtilities.setPermission(ProfileActivity.this, PERMISSIONS);
//                        } else {
//                            captureImageInitialization(1);
//                            checkImage = 2;
//                        }
//                    }
//                }else{
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
//                        if (!CommonUtilities.hasPermissions(ProfileActivity.this, PERMISSIONS)) {
//                            CommonUtilities.setPermission(ProfileActivity.this, PERMISSIONS);
//                        } else {
//                            captureImageInitialization(1);
//                            checkImage = 2;
//                        }
//                    } else {
//                        captureImageInitialization(1);
//                        checkImage = 2;
//                    }
//                }
            }
        });

        tvRemoveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgAttachedProfile = false;
                imgProfile.setImageResource(R.drawable.pub_user_profile);
                tvRemoveProfile.setVisibility(View.INVISIBLE);
                vcProfilePicturePath = "";
            }
        });

        tvRemoveFeaturedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgAttachedFeatured = false;
                imgFeaturedImage.setImageResource(R.drawable.pub_user_profile);
                tvRemoveFeaturedImage.setVisibility(View.INVISIBLE);
                vcFeatureImagePath = "";
            }
        });

        tvRemoveOtherImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeUploadedOtherImage(1);
            }
        });

        tvRemoveOtherImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeUploadedOtherImage(2);
            }
        });

        tvRemoveOtherImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeUploadedOtherImage(3);
            }
        });

        tvRemoveOtherImage4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeUploadedOtherImage(4);
            }
        });

    }

    public void initView() {
        edtAverageDrinkPrice = findViewById(R.id.edtAverageDrinkPrice);
//        edtVerificationCode = findViewById(R.id.edtVerificationCode);
        edtLiveSports = findViewById(R.id.edtLiveSports);
        edtKaraoke = findViewById(R.id.edtKaraoke);
        edtLiveMusic = findViewById(R.id.edtLiveMusic);
        edtDoorCharge = findViewById(R.id.edtDoorCharge);
        edtQuizNights = findViewById(R.id.edtQuizNights);
        edtMinimumAge = findViewById(R.id.edtMinimumAge);
        liveSportsSpinner = findViewById(R.id.liveSportsSpinner);
        foodSpinner = findViewById(R.id.foodSpinner);
        karaokeSpinner = findViewById(R.id.karaokeSpinner);
        liveMusicSpinner = findViewById(R.id.liveMusicSpinner);
        doorChargeSpinner = findViewById(R.id.doorChargeSpinner);
        freeWifiSpinner = findViewById(R.id.freeWifiSpinner);
        disabledAccessSpinner = findViewById(R.id.disabledAccessSpinner);
        quizNightsSpinner = findViewById(R.id.quizNightsSpinner);
        realAleSpinner = findViewById(R.id.realAleSpinner);
        minimumAgeSpinner = findViewById(R.id.minimumAgeSpinner);
        childrenAllowedSpinner = findViewById(R.id.childrenAllowedSpinner);
        dogsAllowedSpinner = findViewById(R.id.dogsAllowedSpinner);
        imgUploadText = findViewById(R.id.imgUploadText);
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        tvProfilePicture = findViewById(R.id.tvProfilePicture);
        tvRemoveProfile = findViewById(R.id.tvRemoveProfile);
        tvFeaturedImage = findViewById(R.id.tvFeaturedImage);
        tvRemoveFeaturedImage = findViewById(R.id.tvRemoveFeaturedImage);
        imgProfile = findViewById(R.id.imgProfile);
        imgFeaturedImage = findViewById(R.id.imgFeaturedImage);
        btnUploadProfile = findViewById(R.id.btnUploadProfile);
        btnUploadFeaturedImage = findViewById(R.id.btnUploadFeaturedImage);
        tvRemoveOtherImage1 = findViewById(R.id.tvRemoveOtherImage1);
        tvRemoveOtherImage2 = findViewById(R.id.tvRemoveOtherImage2);
        tvRemoveOtherImage3 = findViewById(R.id.tvRemoveOtherImage3);
        tvRemoveOtherImage4 = findViewById(R.id.tvRemoveOtherImage4);
        tvUploadOtherImage = findViewById(R.id.tvUploadOtherImage);
        imgOther1 = findViewById(R.id.imgOther1);
        imgOther2 = findViewById(R.id.imgOther2);
        imgOther3 = findViewById(R.id.imgOther3);
        imgOther4 = findViewById(R.id.imgOther4);
        otherImageLayout = findViewById(R.id.otherImageLayout);
        edtPremisesName = findViewById(R.id.edtPremisesName);
//        edtDiscountCode = findViewById(R.id.edtDiscountCode);
//        btnDiscountApply = findViewById(R.id.btnDiscountApply);
//        tvCouponResponse = findViewById(R.id.tvCouponResponse);
        tvOtherPhotos = findViewById(R.id.tvOtherPhotos);
    }

    private boolean getDropboxIMGSize(Uri uri, int width, int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        boolean check = false;
        if(imageWidth<width || imageHeight<height){
            check = true;
        }
        return check;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            images.clear();
            images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);

            if (checkImage == 1) {
                File file = new File(images.get(0).getPath());

                if(file.length() > 8010000){
                    CommonUtilities.ShowToast(ProfileActivity.this, "Image size must be less than 8 MB.");
                }else if(getDropboxIMGSize(Uri.parse(images.get(0).getPath()),338,228)){
                    CommonUtilities.ShowToast(ProfileActivity.this,"Your image is too small, please upload an image bigger than 338*228");
                }else {
                    imgProfile.setImageURI(Uri.parse(images.get(0).getPath()));
                    imgAttachedProfile = true;
                    tvProfilePicture.setError(null);
                    tvRemoveProfile.setVisibility(View.VISIBLE);
                    profilePicturePath = compressImage(images.get(0).getPath());
                    checkImage = 0;
                    vcProfilePicturePath = compressImage(images.get(0).getPath());
                }
            } else if (checkImage == 2) {
                File file = new File(images.get(0).getPath());

                if(file.length() > 8010000){
                    CommonUtilities.ShowToast(ProfileActivity.this, "Image size must be less than 8 MB.");
                }else if(getDropboxIMGSize(Uri.parse(images.get(0).getPath()),1055,410)){
                    CommonUtilities.ShowToast(ProfileActivity.this,"Your image is too small, please upload an image bigger than 1055*410");
                }
                else {
                    imgFeaturedImage.setImageURI(Uri.parse(images.get(0).getPath()));
                    imgAttachedFeatured = true;
                    tvFeaturedImage.setError(null);
                    tvRemoveFeaturedImage.setVisibility(View.VISIBLE);
                    featuredImagePath = compressImage(images.get(0).getPath());
                    checkImage = 0;
                    vcFeatureImagePath = compressImage(images.get(0).getPath());
                }
            } else {
                if (images.size() <= (4 - imgUploadCount)) {
                    for(int i=0; i<images.size(); i++){
                        File file = new File(images.get(i).getPath());
                        if(file.length() > 8010000){
                            CommonUtilities.ShowToast(ProfileActivity.this, "Image size must be less than 8 MB.");
                        }else if(getDropboxIMGSize(Uri.parse(images.get(i).getPath()),1055,410)){
                            CommonUtilities.ShowToast(ProfileActivity.this,"Your image is too small, please upload an image bigger than 1055*410");
                        }else {
                            otherImagesPathList.add(images.get(i));
                            vcOtherImagesPathList.add(images.get(i));
                        }
                    }
                    imgUploadCount = otherImagesPathList.size();
                    imgUploadText.setText(imgUploadCount + "");
                    showOtherImageLayout(otherImagesPathList);
                } else {
                    Toast.makeText(this, "You can upload max 4 other images.", Toast.LENGTH_SHORT).show();
                }
            }


        }

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                images.clear();
                new SingleMediaScanner(ProfileActivity.this, fileUri.getPath());
                images.add(new Image(1, timeStamp, fileUri.getPath(), false, ""));

                if (checkImage == 1) {
                    File file = new File(images.get(0).getPath());

                    if(file.length() > 8010000){
                        CommonUtilities.ShowToast(ProfileActivity.this, "Image size must be less than 8 MB.");
                    }else if(getDropboxIMGSize(Uri.parse(images.get(0).getPath()),338,228)){
                        CommonUtilities.ShowToast(ProfileActivity.this,"Your image is too small, please upload an image bigger than 338*228");
                    }else {
                        imgProfile.setImageURI(Uri.parse(images.get(0).getPath()));
                        imgAttachedProfile = true;
                        tvProfilePicture.setError(null);
                        tvRemoveProfile.setVisibility(View.VISIBLE);
                        profilePicturePath = compressImage(images.get(0).getPath());
                        checkImage = 0;
                        vcProfilePicturePath = compressImage(images.get(0).getPath());
                    }
                } else if (checkImage == 2) {
                    File file = new File(images.get(0).getPath());

                    if(file.length() > 8010000){
                        CommonUtilities.ShowToast(ProfileActivity.this, "Image size must be less than 8 MB.");
                    }else if(getDropboxIMGSize(Uri.parse(images.get(0).getPath()),1055,410)){
                        CommonUtilities.ShowToast(ProfileActivity.this,"Your image is too small, please upload an image bigger than 1055*410");
                    }else {
                        imgFeaturedImage.setImageURI(Uri.parse(images.get(0).getPath()));
                        imgAttachedFeatured = true;
                        tvFeaturedImage.setError(null);
                        tvRemoveFeaturedImage.setVisibility(View.VISIBLE);
                        featuredImagePath = compressImage(images.get(0).getPath());
                        checkImage = 0;
                        vcFeatureImagePath = compressImage(images.get(0).getPath());
                    }
                } else {
                    if (imgUploadCount < 4) {
                        File file = new File(images.get(0).getPath());

                        if(file.length() > 8010000){
                            CommonUtilities.ShowToast(ProfileActivity.this, "Image size must be less than 8 MB.");
                        }else if(getDropboxIMGSize(Uri.parse(images.get(0).getPath()),1055,410)){
                            CommonUtilities.ShowToast(ProfileActivity.this,"Your image is too small, please upload an image bigger than 1055*410");
                        }else {
                            imgUploadCount++;
                            imgUploadText.setText(imgUploadCount + "");
                            otherImagesPathList.add(images.get(0));
                            vcOtherImagesPathList.add(images.get(0));
                            showOtherImageLayout(otherImagesPathList);
                        }
                    } else {
                        Toast.makeText(this, "You can upload max 4 other images.", Toast.LENGTH_SHORT).show();
                    }
                }

            } else if (resultCode == RESULT_CANCELED) {
            } else {
                Toast.makeText(getApplicationContext(),
                                "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void captureImageInitialization(int limit) {

        items = new ArrayList<String>();
        items.add(getResources().getString(R.string.take_camera));
        items.add(getResources().getString(R.string.select_photo));

        items.add(getResources().getString(R.string.cancel_dialog));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ProfileActivity.this,
                android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int index) { // pick
                // from
                dialog.dismiss(); // camera
                if (items.get(index).equals(
                        getResources().getString(R.string.take_camera))) {

                    captureImage();


                } else if (items.get(index).equals(
                        getResources().getString(R.string.select_photo))) {

                    getImages(limit);


                } else if (items.get(index).equals(
                        getResources().getString(R.string.cancel_dialog))) {
                    dialogPic.dismiss();
                } else {
                    dialogPic.dismiss();
                }
            }
        });
        dialogPic = builder.create();
        dialogPic.show();
    }

    private void getImages(int limit) {
        com.nguyenhoanglam.imagepicker.activity.ImagePicker.create(this)
                .folderMode(true)
                .folderTitle("Select photos to upload")
                .imageTitle("Tap to select")
                .single()
                .multi()
                .limit(limit)
                .showCamera(false)
                .imageDirectory("Camera")
//                .origin(images)
                .start(GALLERY_REQUEST_CODE);
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type) {
//        File mediaStorageDir = new File(
//                Environment
//                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                IMAGE_DIRECTORY_NAME);
//
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
//                        + IMAGE_DIRECTORY_NAME + " directory");
//                return null;
//            }
//        }

        File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
//        mediaFile = new File(mediaStorageDir.getPath() + File.separator
//                + "IMG_" + timeStamp + ".jpg");

        mediaFile = new File(picturesDirectory + File.separator
                + "IMG_" + timeStamp + ".jpg");


        return mediaFile;
    }

    public void showOtherImageLayout(ArrayList<Image> images) {
        switch (imgUploadCount) {
            case 0: {
                otherImageLayout.setVisibility(View.GONE);
                break;
            }
            case 1: {
                otherImageLayout.setVisibility(View.VISIBLE);
                imgOther1.setVisibility(View.VISIBLE);
                imgOther2.setVisibility(View.INVISIBLE);
                imgOther3.setVisibility(View.INVISIBLE);
                imgOther4.setVisibility(View.INVISIBLE);
                tvRemoveOtherImage1.setVisibility(View.VISIBLE);
                tvRemoveOtherImage2.setVisibility(View.INVISIBLE);
                tvRemoveOtherImage3.setVisibility(View.INVISIBLE);
                tvRemoveOtherImage4.setVisibility(View.INVISIBLE);
                imgOther1.setImageURI(Uri.parse(images.get(0).getPath()));
                otherImagePathTemp[0] = compressImage(images.get(0).getPath());
                break;
            }
            case 2: {
                otherImageLayout.setVisibility(View.VISIBLE);
                imgOther1.setVisibility(View.VISIBLE);
                imgOther2.setVisibility(View.VISIBLE);
                imgOther3.setVisibility(View.INVISIBLE);
                imgOther4.setVisibility(View.INVISIBLE);
                tvRemoveOtherImage1.setVisibility(View.VISIBLE);
                tvRemoveOtherImage2.setVisibility(View.VISIBLE);
                tvRemoveOtherImage3.setVisibility(View.INVISIBLE);
                tvRemoveOtherImage4.setVisibility(View.INVISIBLE);
                imgOther1.setImageURI(Uri.parse(images.get(0).getPath()));
                imgOther2.setImageURI(Uri.parse(images.get(1).getPath()));
                otherImagePathTemp[0] = compressImage(images.get(0).getPath());
                otherImagePathTemp[1] = compressImage(images.get(1).getPath());
                break;
            }
            case 3: {
                otherImageLayout.setVisibility(View.VISIBLE);
                imgOther1.setVisibility(View.VISIBLE);
                imgOther2.setVisibility(View.VISIBLE);
                imgOther3.setVisibility(View.VISIBLE);
                imgOther4.setVisibility(View.INVISIBLE);
                tvRemoveOtherImage1.setVisibility(View.VISIBLE);
                tvRemoveOtherImage2.setVisibility(View.VISIBLE);
                tvRemoveOtherImage3.setVisibility(View.VISIBLE);
                tvRemoveOtherImage4.setVisibility(View.INVISIBLE);
                imgOther1.setImageURI(Uri.parse(images.get(0).getPath()));
                imgOther2.setImageURI(Uri.parse(images.get(1).getPath()));
                imgOther3.setImageURI(Uri.parse(images.get(2).getPath()));
                otherImagePathTemp[0] = compressImage(images.get(0).getPath());
                otherImagePathTemp[1] = compressImage(images.get(1).getPath());
                otherImagePathTemp[2] = compressImage(images.get(2).getPath());
                break;
            }
            case 4: {
                otherImageLayout.setVisibility(View.VISIBLE);
                imgOther1.setVisibility(View.VISIBLE);
                imgOther2.setVisibility(View.VISIBLE);
                imgOther3.setVisibility(View.VISIBLE);
                imgOther4.setVisibility(View.VISIBLE);
                tvRemoveOtherImage1.setVisibility(View.VISIBLE);
                tvRemoveOtherImage2.setVisibility(View.VISIBLE);
                tvRemoveOtherImage3.setVisibility(View.VISIBLE);
                tvRemoveOtherImage4.setVisibility(View.VISIBLE);
                imgOther1.setImageURI(Uri.parse(images.get(0).getPath()));
                imgOther2.setImageURI(Uri.parse(images.get(1).getPath()));
                imgOther3.setImageURI(Uri.parse(images.get(2).getPath()));
                imgOther4.setImageURI(Uri.parse(images.get(3).getPath()));
                otherImagePathTemp[0] = compressImage(images.get(0).getPath());
                otherImagePathTemp[1] = compressImage(images.get(1).getPath());
                otherImagePathTemp[2] = compressImage(images.get(2).getPath());
                otherImagePathTemp[3] = compressImage(images.get(3).getPath());

                break;
            }
        }
    }

    public void removeUploadedOtherImage(int index) {

        switch (index) {
            case 1: {
                if (imgUploadCount == 4) {
                    imgOther1.setImageURI(Uri.parse(otherImagesPathList.get(1).getPath()));
                    imgOther2.setImageURI(Uri.parse(otherImagesPathList.get(2).getPath()));
                    imgOther3.setImageURI(Uri.parse(otherImagesPathList.get(3).getPath()));
                    otherImagePathTemp[0] = otherImagePathTemp[1];
                    otherImagePathTemp[1] = otherImagePathTemp[2];
                    otherImagePathTemp[2] = otherImagePathTemp[3];
                    otherImagePathTemp[3] = "";
                    imgOther4.setVisibility(View.INVISIBLE);
                    tvRemoveOtherImage4.setVisibility(View.INVISIBLE);
                } else if (imgUploadCount == 3) {
                    imgOther1.setImageURI(Uri.parse(otherImagesPathList.get(1).getPath()));
                    imgOther2.setImageURI(Uri.parse(otherImagesPathList.get(2).getPath()));
                    otherImagePathTemp[0] = otherImagePathTemp[1];
                    otherImagePathTemp[1] = otherImagePathTemp[2];
                    otherImagePathTemp[2] = "";
                    imgOther3.setVisibility(View.INVISIBLE);
                    tvRemoveOtherImage3.setVisibility(View.INVISIBLE);
                } else if (imgUploadCount == 2) {
                    imgOther1.setImageURI(Uri.parse(otherImagesPathList.get(1).getPath()));
                    otherImagePathTemp[0] = otherImagePathTemp[1];
                    otherImagePathTemp[1] = "";
                    imgOther2.setVisibility(View.INVISIBLE);
                    tvRemoveOtherImage2.setVisibility(View.INVISIBLE);
                } else if (imgUploadCount == 1) {
                    otherImageLayout.setVisibility(View.GONE);
                    otherImagePathTemp[0] = "";
                }
                imgUploadCount--;
                imgUploadText.setText(imgUploadCount+"");
                otherImagesPathList.remove(0);
                vcOtherImagesPathList.remove(0);
                break;
            }
            case 2: {
                if (imgUploadCount == 4) {
                    imgOther2.setImageURI(Uri.parse(otherImagesPathList.get(2).getPath()));
                    imgOther3.setImageURI(Uri.parse(otherImagesPathList.get(3).getPath()));
                    otherImagePathTemp[1] = otherImagePathTemp[2];
                    otherImagePathTemp[2] = otherImagePathTemp[3];
                    otherImagePathTemp[3] = "";
                    imgOther4.setVisibility(View.INVISIBLE);
                    tvRemoveOtherImage4.setVisibility(View.INVISIBLE);
                } else if (imgUploadCount == 3) {
                    imgOther2.setImageURI(Uri.parse(otherImagesPathList.get(2).getPath()));
                    otherImagePathTemp[1] = otherImagePathTemp[2];
                    otherImagePathTemp[2] = "";
                    imgOther3.setVisibility(View.INVISIBLE);
                    tvRemoveOtherImage3.setVisibility(View.INVISIBLE);
                } else if (imgUploadCount == 2) {
                    otherImagePathTemp[1] = "";
                    imgOther2.setVisibility(View.INVISIBLE);
                    tvRemoveOtherImage2.setVisibility(View.INVISIBLE);
                }
                imgUploadCount--;
                otherImagesPathList.remove(1);
                vcOtherImagesPathList.remove(1);
                imgUploadText.setText(imgUploadCount+"");
                break;
            }
            case 3: {
                if (imgUploadCount == 4) {
                    imgOther3.setImageURI(Uri.parse(otherImagesPathList.get(3).getPath()));
                    otherImagePathTemp[2] = otherImagePathTemp[3];
                    otherImagePathTemp[3] = "";
                    imgOther4.setVisibility(View.INVISIBLE);
                    tvRemoveOtherImage4.setVisibility(View.INVISIBLE);
                } else if (imgUploadCount == 3) {
                    imgOther3.setVisibility(View.INVISIBLE);
                    tvRemoveOtherImage3.setVisibility(View.INVISIBLE);
                    otherImagePathTemp[2] = "";
                }
                imgUploadCount--;
                otherImagesPathList.remove(2);
                vcOtherImagesPathList.remove(2);
                imgUploadText.setText(imgUploadCount+"");
                break;
            }
            case 4: {
                imgUploadCount--;
                otherImagesPathList.remove(3);
                vcOtherImagesPathList.remove(3);
                otherImagePathTemp[3] = "";
                imgOther4.setVisibility(View.INVISIBLE);
                tvRemoveOtherImage4.setVisibility(View.INVISIBLE);
                imgUploadText.setText(imgUploadCount+"");
                break;
            }
        }
    }

    public ArrayAdapter<String> getArrayAdapter(String[] items) {
        return new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
    }

    public void showHideEditText(Spinner[] spinners, EditText[] editTexts) {
        for (int i = 0; i < spinners.length; i++) {
            int finalI = i;
            spinners[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    if (position == 1) {
                        editTexts[finalI].setVisibility(View.GONE);
                    } else {
                        editTexts[finalI].setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    public boolean isEmptyEditText(EditText[] list, Spinner[] spinners, String[] errorTextList) {
        boolean check = false;
        for (int i = 0; i < list.length; i++) {
            if (spinners[i].getSelectedItem().toString().equals("Yes")) {
                if (list[i].getText().toString().equals("")) {
                    list[i].setError(errorTextList[i]);
                    check = true;
                }
            }
        }
        return check;
    }

    public String getStringImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public String compressImage(String path) {

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = decodeFile(path, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = decodeFile(path, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(path);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }

    public String getFilename() {
//        File file = new File(Environment.getExternalStorageDirectory().getPath(), "Gr8niteout");
//        if (!file.exists()) {
//            file.mkdirs();
//        }
        String uriSting = getCacheDir().getPath() + "/" + System.currentTimeMillis() + ".jpg";
        return uriSting;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

//    public void callCheckDiscountApi(){
//        isApplyButtonTapped = true;
//        Map<String, String> paramsTemp = new HashMap<>();
//        paramsTemp.put("coupan",edtDiscountCode.getText().toString());
//        paramsTemp.put("currency_code",params.get("currency_prefer"));
//        ServerAccess.getResponse(ProfileActivity.this, CommonUtilities.key_apply_coupan, paramsTemp, true, new ServerAccess.VolleyCallback() {
//            @Override
//            public void onSuccess(String result) {
//                couponCodeModel = new CouponCodeModel().CouponCodeModel(result);
//                discountResult = result;
//                if(couponCodeModel != null){
//                    tvCouponResponse.setVisibility(View.VISIBLE);
//                    if(couponCodeModel.response.apply_coupan.valid){
//                        String currencySymbol = "";
//
//                        if(params.get("currency_prefer").equals("1")){
//                            currencySymbol = "£";
//                        }else if(params.get("currency_prefer").equals("2")){
//                            currencySymbol = "€";
//                        }else{
//                            currencySymbol = "$";
//                        }
//
//                        String msg = "Congratulations on your savings! Your discount code is valid. Amount Payable: "+currencySymbol+couponCodeModel.response.apply_coupan.discountprice+" + VAT";
//                        tvCouponResponse.setText(msg);
//                        tvCouponResponse.setTextColor(getResources().getColor(R.color.pub_green));
//                    }else{
//                        tvCouponResponse.setText(couponCodeModel.response.apply_coupan.msg);
//                        tvCouponResponse.setTextColor(getResources().getColor(android.R.color.holo_red_light));
//                    }
//                }
//            }
//
//            @Override
//            public void onError(String error) {
//                isApplyButtonTapped = false;
//                CommonUtilities.ShowToast(ProfileActivity.this, "Something went wrong!");
//            }
//        });
//    }

    public void updateFields(){

        if(params.get("about_pub") != null) {
            edtPremisesName.setText(params.get("about_pub"));
        }

        if(params.get("pub_profile") != null && !vcProfilePicturePath.equals("")) {
            imgProfile.setImageURI(Uri.parse(vcProfilePicturePath));
            imgAttachedProfile = true;
            tvProfilePicture.setError(null);
            tvRemoveProfile.setVisibility(View.VISIBLE);
            profilePicturePath = vcProfilePicturePath;
            checkImage = 0;
        }

        if(params.get("pub_featured_Image") != null && !vcFeatureImagePath.equals("")) {
            imgFeaturedImage.setImageURI(Uri.parse(vcFeatureImagePath));
            imgAttachedFeatured = true;
            tvFeaturedImage.setError(null);
            tvRemoveFeaturedImage.setVisibility(View.VISIBLE);
            featuredImagePath = vcFeatureImagePath;
            checkImage = 0;
        }

        if(params.get("other_photos") != null && vcOtherImagesPathList.size() != 0) {
            imgUploadCount = vcOtherImagesPathList.size();
            imgUploadText.setText(imgUploadCount + "");
            showOtherImageLayout(vcOtherImagesPathList);
            otherImagesPathList = vcOtherImagesPathList;

            for(int i=0; i<vcOtherImagesPathList.size(); i++){
                otherImagePathTemp[i] = compressImage(vcOtherImagesPathList.get(i).getPath());
            }
        }

        if(params.get("1_status") != null){
            if(params.get("1_status").equals("0")){
                liveSportsSpinner.setSelection(1);
            }else{
                liveSportsSpinner.setSelection(0);
                edtLiveSports.setVisibility(View.VISIBLE);
                if(params.get("1_comment") != null){
                    edtLiveSports.setText(params.get("1_comment"));
                }
            }
        }

        if(params.get("2_status") != null){
            if(params.get("2_status").equals("0")){
                foodSpinner.setSelection(1);
            }else{
                foodSpinner.setSelection(0);
            }
        }

        if(params.get("3_status") != null){
            if(params.get("3_status").equals("0")){
                karaokeSpinner.setSelection(1);
            }else{
                karaokeSpinner.setSelection(0);
                edtKaraoke.setVisibility(View.VISIBLE);
                if(params.get("3_comment") != null){
                    edtKaraoke.setText(params.get("3_comment"));
                }
            }
        }

        if(params.get("4_status") != null){
            if(params.get("4_status").equals("0")){
                liveMusicSpinner.setSelection(1);
            }else{
                liveMusicSpinner.setSelection(0);
                edtLiveMusic.setVisibility(View.VISIBLE);
                if(params.get("4_comment") != null){
                    edtLiveMusic.setText(params.get("4_comment"));
                }
            }
        }

        if(params.get("5_comment") != null) {
            edtAverageDrinkPrice.setText(params.get("5_comment"));
        }

        if(params.get("6_status") != null){
            if(params.get("6_status").equals("0")){
                doorChargeSpinner.setSelection(1);
            }else{
                doorChargeSpinner.setSelection(0);
                edtDoorCharge.setVisibility(View.VISIBLE);
                if(params.get("6_comment") != null){
                    edtDoorCharge.setText(params.get("6_comment"));
                }
            }
        }

        if(params.get("7_status") != null){
            if(params.get("7_status").equals("0")){
                freeWifiSpinner.setSelection(1);
            }else{
                freeWifiSpinner.setSelection(0);
            }
        }

        if(params.get("8_status") != null){
            if(params.get("8_status").equals("0")){
                disabledAccessSpinner.setSelection(1);
            }else{
                disabledAccessSpinner.setSelection(0);
            }
        }

        if(params.get("9_status") != null){
            if(params.get("9_status").equals("0")){
                quizNightsSpinner.setSelection(1);
            }else{
                quizNightsSpinner.setSelection(0);
                edtQuizNights.setVisibility(View.VISIBLE);
                if(params.get("9_comment") != null){
                    edtQuizNights.setText(params.get("9_comment"));
                }
            }
        }

        if(params.get("10_status") != null){
            if(params.get("10_status").equals("0")){
                realAleSpinner.setSelection(1);
            }else{
                realAleSpinner.setSelection(0);
            }
        }

        if(params.get("11_status") != null){
            if(params.get("11_status").equals("0")){
                minimumAgeSpinner.setSelection(1);
            }else{
                minimumAgeSpinner.setSelection(0);
                edtMinimumAge.setVisibility(View.VISIBLE);
                if(params.get("11_comment") != null){
                    edtMinimumAge.setText(params.get("11_comment"));
                }
            }
        }

        if(params.get("12_status") != null){
            if(params.get("12_status").equals("0")){
                childrenAllowedSpinner.setSelection(1);
            }else{
                childrenAllowedSpinner.setSelection(0);
            }
        }

        if(params.get("13_status") != null){
            if(params.get("13_status").equals("0")){
                dogsAllowedSpinner.setSelection(1);
            }else{
                dogsAllowedSpinner.setSelection(0);
            }
        }

//        if(params.get("couponcode") != null) {
//            if(!params.get("couponcode").equals("") && !discountResult.equals("")){
//                isApplyButtonTapped = true;
//                couponCodeModel = new CouponCodeModel().CouponCodeModel(discountResult);
//            }
//            edtDiscountCode.setText(params.get("couponcode"));
//        }

    }

}