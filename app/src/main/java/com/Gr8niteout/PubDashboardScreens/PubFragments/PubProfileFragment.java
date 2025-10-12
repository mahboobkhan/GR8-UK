package com.Gr8niteout.PubDashboardScreens.PubFragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.graphics.BitmapFactory.decodeFile;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.Gr8niteout.myprofile.EditProfileActivity.fileUri;
import static com.facebook.share.internal.DeviceShareDialogFragment.TAG;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.Gr8niteout.PubDashboardScreens.Models.ChangePasswordModel;
import com.Gr8niteout.PubDashboardScreens.Models.GetPubProfileModel;
import com.Gr8niteout.R;
import com.Gr8niteout.RegisterPubScreens.ValidationCases;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.pub.SingleMediaScanner;
import com.bumptech.glide.Glide;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PubProfileFragment extends Fragment {

    TextView imgUploadText, tvUploadOtherImage, tvSave, tvRemoveUploaded1, tvRemoveUploaded2, tvRemoveUploaded3, tvRemoveUploaded4, tvRemoveProfile, tvRemoveFeaturedImage, tvRemoveSelected1, tvRemoveSelected2, tvRemoveSelected3, tvRemoveSelected4,
            tvNoImageUploaded, tvNoImageSelected;
    ImageView imgProfile, imgFeaturedImage, imgUploaded1, imgUploaded2, imgUploaded3, imgUploaded4, imgSelected1, imgSelected2,
            imgSelected3, imgSelected4;
    LinearLayout btnUploadProfile, btnUploadFeaturedImage, selectedImagesLayout, uploadedImagesLayout;
    Spinner liveSportsSpinner, foodSpinner, karaokeSpinner, liveMusicSpinner, doorChargeSpinner, freeWifiSpinner, disabledAccessSpinner,
            quizNightsSpinner, realAleSpinner, minimumAgeSpinner, childrenAllowedSpinner, dogsAllowedSpinner;
    EditText edtLiveSports, edtKaraoke, edtLiveMusic, edtDoorCharge, edtQuizNights, edtMinimumAge, edtAverageDrinkPrice,
            edtPremisesName;

    private final int CAMERA_REQUEST_CODE = 100;
    private final int GALLERY_REQUEST_CODE = 2000;
    private final String IMAGE_DIRECTORY_NAME = "Gr8niteout";

    int imgUploadCount = 0;
    int selectedImagesCount = 0;
    String timeStamp;
    int checkImage = 0;

    List<String> items;
    private AlertDialog dialogPic;
    private ArrayList<Image> images = new ArrayList<>();

    String[] yesNoItems = new String[]{"Yes", "No"};
    Spinner[] spinnerList;
    Spinner[] spinnerWithEditText;
    EditText[] editTextSpinnerList;
    EditText[] editTextList;
    ValidationCases validation;

    String profilePicturePath = "";
    String profilePictureLink = "";
    String featuredImagePath = "";
    String featuredImageLink = "";
    String[] otherImagePathTemp = new String[]{"", "", "", ""};
    ArrayList<String> removedImagesList = new ArrayList<>();

    GetPubProfileModel getPubProfileModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pub_profile, container, false);

        initView(view);
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

        validation.firstLetterSpace(editTextList);

        showHideEditText(spinnerWithEditText, editTextSpinnerList);

        callGetPubProfileApi();

        imgUploadText.setText(imgUploadCount + "");

        tvUploadOtherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((imgUploadCount+selectedImagesCount) < 4) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        String[] PERMISSIONS = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA};
                        if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
                            CommonUtilities.setPermission(getContext(), PERMISSIONS);
                        }else{
                            captureImageInitialization(4);
                        }
                    }else{
                        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                        if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
                            CommonUtilities.setPermission(getContext(), PERMISSIONS);
                        } else {
                            captureImageInitialization(4);
                        }
                    }
//                    if(Build.VERSION.SDK_INT >= 30) {
//                        if (!Environment.isExternalStorageManager()){
//                            Intent intent = new Intent();
//                            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                            Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
//                            intent.setData(uri);
//                            startActivity(intent);
//                        }else{
//                            String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
//                            if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
//                                CommonUtilities.setPermission(getContext(), PERMISSIONS);
//                            } else {
//                                captureImageInitialization(4);
//                            }
//                        }
//                    }else{
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
//                            if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
//                                CommonUtilities.setPermission(getContext(), PERMISSIONS);
//                            } else {
//                                captureImageInitialization(4);
//                            }
//                        } else {
//                            captureImageInitialization(4);
//                        }
//                    }
                } else {
                    CommonUtilities.ShowToast(getContext(), "You can upload max 4 other photos.");
                }
            }
        });

        btnUploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    String[] PERMISSIONS = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA};
                    if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
                        CommonUtilities.setPermission(getContext(), PERMISSIONS);
                    }else{
                        captureImageInitialization(1);
                        checkImage = 1;
                    }
                }else{
                    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                    if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
                        CommonUtilities.setPermission(getContext(), PERMISSIONS);
                    } else {
                        captureImageInitialization(1);
                        checkImage = 1;
                    }
                }
//                if(Build.VERSION.SDK_INT >= 30) {
//                    if (!Environment.isExternalStorageManager()){
//                        Intent intent = new Intent();
//                        intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
//                        intent.setData(uri);
//                        startActivity(intent);
//                    }else{
//                        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
//                        if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
//                            CommonUtilities.setPermission(getContext(), PERMISSIONS);
//                        } else {
//                            captureImageInitialization(1);
//                            checkImage = 1;
//                        }
//                    }
//                }else{
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
//                        if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
//                            CommonUtilities.setPermission(getContext(), PERMISSIONS);
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
                    if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
                        CommonUtilities.setPermission(getContext(), PERMISSIONS);
                    }else{
                        captureImageInitialization(1);
                        checkImage = 2;
                    }
                }else{
                    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                    if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
                        CommonUtilities.setPermission(getContext(), PERMISSIONS);
                    } else {
                        captureImageInitialization(1);
                        checkImage = 2;
                    }
                }
//                if(Build.VERSION.SDK_INT >= 30) {
//                    if (!Environment.isExternalStorageManager()){
//                        Intent intent = new Intent();
//                        intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
//                        intent.setData(uri);
//                        startActivity(intent);
//                    }else{
//                        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
//                        if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
//                            CommonUtilities.setPermission(getContext(), PERMISSIONS);
//                        } else {
//                            captureImageInitialization(1);
//                            checkImage = 2;
//                        }
//                    }
//                }else{
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
//                        if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
//                            CommonUtilities.setPermission(getContext(), PERMISSIONS);
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

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText[] newEditTextList = new EditText[]{edtDoorCharge, edtMinimumAge};
                Spinner[] newSpinnerList = new Spinner[]{doorChargeSpinner, minimumAgeSpinner};
                String[] errorTextList = new String[]{"Door charge is required.", "Minimum age is required."};

                boolean check = isEmptyEditText(newEditTextList, newSpinnerList, errorTextList);

                boolean check2 = false;
                if (edtAverageDrinkPrice.getText().toString().equals("")) {
                    edtAverageDrinkPrice.setError("Average drink price is required.");
                    check2 = true;
                } else {
                    edtAverageDrinkPrice.setError(null);
                }

                if (!check && !check2) {
                   callUpdatePubProfileApi();
                }
            }
        });

        tvRemoveUploaded1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeUploadedImage(1);
            }
        });

        tvRemoveUploaded2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeUploadedImage(2);
            }
        });

        tvRemoveUploaded3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeUploadedImage(3);
            }
        });

        tvRemoveUploaded4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeUploadedImage(4);
            }
        });

        tvRemoveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profilePicturePath = "";
                tvRemoveProfile.setVisibility(View.INVISIBLE);
                Glide.with(getContext())
                        .load(Uri.parse(profilePictureLink))
                        .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                        .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                        .into(imgProfile);
            }
        });

        tvRemoveFeaturedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                featuredImagePath = "";
                tvRemoveFeaturedImage.setVisibility(View.INVISIBLE);
                Glide.with(getContext())
                        .load(Uri.parse(featuredImageLink))
                        .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                        .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                        .into(imgFeaturedImage);
            }
        });

        tvRemoveSelected1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeSelectedImage(1);
            }
        });

        tvRemoveSelected2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeSelectedImage(2);
            }
        });

        tvRemoveSelected3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeSelectedImage(3);
            }
        });

        tvRemoveSelected4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeSelectedImage(4);
            }
        });
        return view;
    }

    public void initView(View view) {
        edtAverageDrinkPrice = view.findViewById(R.id.edtAverageDrinkPrice);
        edtLiveSports = view.findViewById(R.id.edtLiveSports);
        edtKaraoke = view.findViewById(R.id.edtKaraoke);
        edtLiveMusic = view.findViewById(R.id.edtLiveMusic);
        edtDoorCharge = view.findViewById(R.id.edtDoorCharge);
        edtQuizNights = view.findViewById(R.id.edtQuizNights);
        edtMinimumAge = view.findViewById(R.id.edtMinimumAge);
        liveSportsSpinner = view.findViewById(R.id.liveSportsSpinner);
        foodSpinner = view.findViewById(R.id.foodSpinner);
        karaokeSpinner = view.findViewById(R.id.karaokeSpinner);
        liveMusicSpinner = view.findViewById(R.id.liveMusicSpinner);
        doorChargeSpinner = view.findViewById(R.id.doorChargeSpinner);
        freeWifiSpinner = view.findViewById(R.id.freeWifiSpinner);
        disabledAccessSpinner = view.findViewById(R.id.disabledAccessSpinner);
        quizNightsSpinner = view.findViewById(R.id.quizNightsSpinner);
        realAleSpinner = view.findViewById(R.id.realAleSpinner);
        minimumAgeSpinner = view.findViewById(R.id.minimumAgeSpinner);
        childrenAllowedSpinner = view.findViewById(R.id.childrenAllowedSpinner);
        dogsAllowedSpinner = view.findViewById(R.id.dogsAllowedSpinner);
        imgUploadText = view.findViewById(R.id.imgUploadText);
        tvSave = view.findViewById(R.id.tvSave);
        imgProfile = view.findViewById(R.id.imgProfile);
        imgFeaturedImage = view.findViewById(R.id.imgFeaturedImage);
        btnUploadProfile = view.findViewById(R.id.btnUploadProfile);
        btnUploadFeaturedImage = view.findViewById(R.id.btnUploadFeaturedImage);
        tvUploadOtherImage = view.findViewById(R.id.tvUploadOtherImage);
        imgUploaded1 = view.findViewById(R.id.imgUploaded1);
        imgUploaded2 = view.findViewById(R.id.imgUploaded2);
        imgUploaded3 = view.findViewById(R.id.imgUploaded3);
        imgUploaded4 = view.findViewById(R.id.imgUploaded4);
        imgSelected1 = view.findViewById(R.id.imgSelected1);
        imgSelected2 = view.findViewById(R.id.imgSelected2);
        imgSelected3 = view.findViewById(R.id.imgSelected3);
        imgSelected4 = view.findViewById(R.id.imgSelected4);
        selectedImagesLayout = view.findViewById(R.id.selectedImagesLayout);
        uploadedImagesLayout = view.findViewById(R.id.uploadedImagesLayout);
        edtPremisesName = view.findViewById(R.id.edtPremisesName);
        tvRemoveSelected1 = view.findViewById(R.id.tvRemoveSelected1);
        tvRemoveSelected2 = view.findViewById(R.id.tvRemoveSelected2);
        tvRemoveSelected3 = view.findViewById(R.id.tvRemoveSelected3);
        tvRemoveSelected4 = view.findViewById(R.id.tvRemoveSelected4);
        tvRemoveUploaded1 = view.findViewById(R.id.tvRemoveUploaded1);
        tvRemoveUploaded2 = view.findViewById(R.id.tvRemoveUploaded2);
        tvRemoveUploaded3 = view.findViewById(R.id.tvRemoveUploaded3);
        tvRemoveUploaded4 = view.findViewById(R.id.tvRemoveUploaded4);
        tvRemoveProfile = view.findViewById(R.id.tvRemoveProfile);
        tvRemoveFeaturedImage = view.findViewById(R.id.tvRemoveFeaturedImage);
        tvNoImageSelected = view.findViewById(R.id.tvNoImageSelected);
        tvNoImageUploaded = view.findViewById(R.id.tvNoImageUploaded);
    }

    private boolean getDropboxIMGSize(Uri uri, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        boolean check = false;
        if (imageWidth < width || imageHeight < height) {
            check = true;
        }
        return check;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            images.clear();
            images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);

            if (checkImage == 1) {
                File file = new File(images.get(0).getPath());

                if(file.length() > 8010000){
                    CommonUtilities.ShowToast(getContext(), "Image size must be less than 8 MB.");
                } else if (getDropboxIMGSize(Uri.parse(images.get(0).getPath()), 338, 228)) {
                    CommonUtilities.ShowToast(getContext(), "Your image is too small, please upload an image bigger than 338*228");
                } else {
                    imgProfile.setImageURI(Uri.parse(images.get(0).getPath()));
                    profilePicturePath = compressImage(images.get(0).getPath());
                    tvRemoveProfile.setVisibility(View.VISIBLE);
                    checkImage = 0;
                }
            }
            else if (checkImage == 2) {
                File file = new File(images.get(0).getPath());

                if(file.length() > 8010000){
                    CommonUtilities.ShowToast(getContext(), "Image size must be less than 8 MB.");
                } else if (getDropboxIMGSize(Uri.parse(images.get(0).getPath()), 1055, 410)) {
                    CommonUtilities.ShowToast(getContext(), "Your image is too small, please upload an image bigger than 1055*410");
                } else {
                    imgFeaturedImage.setImageURI(Uri.parse(images.get(0).getPath()));
                    featuredImagePath = compressImage(images.get(0).getPath());
                    tvRemoveFeaturedImage.setVisibility(View.VISIBLE);
                    checkImage = 0;
                }
            }
            else {
                if (images.size() <= (4 - (imgUploadCount+selectedImagesCount))) {
                    for (int i = 0; i < images.size(); i++) {
                        File file = new File(images.get(i).getPath());
                        if(file.length() > 8010000){
                            CommonUtilities.ShowToast(getContext(), "Image size must be less than 8 MB.");
                        } else if (getDropboxIMGSize(Uri.parse(images.get(i).getPath()), 1055, 410)) {
                            CommonUtilities.ShowToast(getContext(), "Your image is too small, please upload an image bigger than 1055*410");
                        } else {
                            Log.d(TAG, "onActivityResult: "+images.get(i).getPath());
                            otherImagePathTemp[selectedImagesCount] = compressImage(images.get(i).getPath());
                            selectedImagesCount++;
                        }
                    }
                    imgUploadText.setText((imgUploadCount+selectedImagesCount) + "");
                    showSelectedImagesLayout();
                } else {
                    Toast.makeText(getContext(), "You can upload max 4 other images.", Toast.LENGTH_SHORT).show();
                }
            }


        }

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                images.clear();
                new SingleMediaScanner(getContext(), fileUri.getPath());
                images.add(new Image(1, timeStamp, fileUri.getPath(), false, ""));

                if (checkImage == 1) {
                    File file = new File(images.get(0).getPath());

                    if(file.length() > 8010000){
                        CommonUtilities.ShowToast(getContext(), "Image size must be less than 8 MB.");
                    } else if (getDropboxIMGSize(Uri.parse(images.get(0).getPath()), 338, 228)) {
                        CommonUtilities.ShowToast(getContext(), "Your image is too small, please upload an image bigger than 338*228");
                    } else {
                        imgProfile.setImageURI(Uri.parse(images.get(0).getPath()));
                        profilePicturePath = compressImage(images.get(0).getPath());
                        tvRemoveProfile.setVisibility(View.VISIBLE);
                        checkImage = 0;
                    }
                } else if (checkImage == 2)
                {
                    File file = new File(images.get(0).getPath());

                    if(file.length() > 8010000){
                        CommonUtilities.ShowToast(getContext(), "Image size must be less than 8 MB.");
                    } else if (getDropboxIMGSize(Uri.parse(images.get(0).getPath()), 1055, 410)) {
                        CommonUtilities.ShowToast(getContext(), "Your image is too small, please upload an image bigger than 1055*410");
                    } else {
                        imgFeaturedImage.setImageURI(Uri.parse(images.get(0).getPath()));
                        featuredImagePath = compressImage(images.get(0).getPath());
                        tvRemoveFeaturedImage.setVisibility(View.VISIBLE);
                        checkImage = 0;
                    }
                } else {

                    File file = new File(images.get(0).getPath());

                    if(file.length() > 8010000){
                        CommonUtilities.ShowToast(getContext(), "Image size must be less than 8 MB.");
                    } else if (getDropboxIMGSize(Uri.parse(images.get(0).getPath()), 1055, 410)) {
                        CommonUtilities.ShowToast(getContext(), "Your image is too small, please upload an image bigger than 1055*410");
                    } else {
                        Log.d(TAG, "onActivityResult: "+images.size());
                        Log.d(TAG, "onActivityResult: "+images.get(0).getPath());
                        otherImagePathTemp[selectedImagesCount] = compressImage(images.get(0).getPath());
                        selectedImagesCount++;
                        imgUploadText.setText((imgUploadCount+selectedImagesCount) + "");
                        showSelectedImagesLayout();
                    }

                }

            } else if (resultCode == RESULT_CANCELED) {
            } else {
                Toast.makeText(getContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void captureImageInitialization(int limit) {

        items = new ArrayList<String>();
        items.add(getResources().getString(R.string.take_camera));
        items.add(getResources().getString(R.string.select_photo));

        items.add(getResources().getString(R.string.cancel_dialog));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

    public void showSelectedImagesLayout() {
        selectedImagesLayout.setVisibility(View.VISIBLE);
        tvNoImageSelected.setVisibility(View.GONE);
        switch (selectedImagesCount) {
            case 1: {
                imgSelected1.setVisibility(View.VISIBLE);
                tvRemoveSelected1.setVisibility(View.VISIBLE);
                imgSelected2.setVisibility(View.GONE);
                imgSelected3.setVisibility(View.GONE);
                imgSelected4.setVisibility(View.GONE);
                tvRemoveSelected2.setVisibility(View.GONE);
                tvRemoveSelected3.setVisibility(View.GONE);
                tvRemoveSelected4.setVisibility(View.GONE);
                imgSelected1.setImageURI(Uri.parse(otherImagePathTemp[selectedImagesCount-1]));
                break;
            }
            case 2: {
                imgSelected1.setVisibility(View.VISIBLE);
                imgSelected2.setVisibility(View.VISIBLE);
                tvRemoveSelected1.setVisibility(View.VISIBLE);
                tvRemoveSelected2.setVisibility(View.VISIBLE);
                imgSelected3.setVisibility(View.GONE);
                imgSelected4.setVisibility(View.GONE);
                tvRemoveSelected3.setVisibility(View.GONE);
                tvRemoveSelected4.setVisibility(View.GONE);
                imgSelected1.setImageURI(Uri.parse(otherImagePathTemp[selectedImagesCount-2]));
                imgSelected2.setImageURI(Uri.parse(otherImagePathTemp[selectedImagesCount-1]));
                break;
            }
            case 3: {
                imgSelected1.setVisibility(View.VISIBLE);
                imgSelected2.setVisibility(View.VISIBLE);
                imgSelected3.setVisibility(View.VISIBLE);
                tvRemoveSelected1.setVisibility(View.VISIBLE);
                tvRemoveSelected2.setVisibility(View.VISIBLE);
                tvRemoveSelected3.setVisibility(View.VISIBLE);
                imgSelected4.setVisibility(View.GONE);
                tvRemoveSelected4.setVisibility(View.GONE);
                imgSelected1.setImageURI(Uri.parse(otherImagePathTemp[selectedImagesCount-3]));
                imgSelected2.setImageURI(Uri.parse(otherImagePathTemp[selectedImagesCount-2]));
                imgSelected3.setImageURI(Uri.parse(otherImagePathTemp[selectedImagesCount-1]));
                break;
            }
            case 4: {
                imgSelected1.setVisibility(View.VISIBLE);
                imgSelected2.setVisibility(View.VISIBLE);
                imgSelected3.setVisibility(View.VISIBLE);
                imgSelected4.setVisibility(View.VISIBLE);
                tvRemoveSelected1.setVisibility(View.VISIBLE);
                tvRemoveSelected2.setVisibility(View.VISIBLE);
                tvRemoveSelected3.setVisibility(View.VISIBLE);
                tvRemoveSelected4.setVisibility(View.VISIBLE);
                imgSelected1.setImageURI(Uri.parse(otherImagePathTemp[selectedImagesCount-4]));
                imgSelected2.setImageURI(Uri.parse(otherImagePathTemp[selectedImagesCount-3]));
                imgSelected3.setImageURI(Uri.parse(otherImagePathTemp[selectedImagesCount-2]));
                imgSelected4.setImageURI(Uri.parse(otherImagePathTemp[selectedImagesCount-1]));
                break;
            }
        }
    }

    public void removeSelectedImage(int index) {
        switch (index) {
            case 1: {
                if (selectedImagesCount == 4) {
                    otherImagePathTemp[0] = otherImagePathTemp[1];
                    otherImagePathTemp[1] = otherImagePathTemp[2];
                    otherImagePathTemp[2] = otherImagePathTemp[3];
                    otherImagePathTemp[3] = "";
                    imgSelected1.setImageURI(Uri.parse(otherImagePathTemp[0]));
                    imgSelected2.setImageURI(Uri.parse(otherImagePathTemp[1]));
                    imgSelected3.setImageURI(Uri.parse(otherImagePathTemp[2]));
                    imgSelected4.setVisibility(View.INVISIBLE);
                    tvRemoveSelected4.setVisibility(View.INVISIBLE);
                } else if (selectedImagesCount == 3) {
                    otherImagePathTemp[0] = otherImagePathTemp[1];
                    otherImagePathTemp[1] = otherImagePathTemp[2];
                    otherImagePathTemp[2] = "";
                    imgSelected1.setImageURI(Uri.parse(otherImagePathTemp[0]));
                    imgSelected2.setImageURI(Uri.parse(otherImagePathTemp[1]));
                    imgSelected3.setVisibility(View.INVISIBLE);
                    tvRemoveSelected3.setVisibility(View.INVISIBLE);
                } else if (selectedImagesCount == 2) {
                    otherImagePathTemp[0] = otherImagePathTemp[1];
                    otherImagePathTemp[1] = "";
                    imgSelected1.setImageURI(Uri.parse(otherImagePathTemp[0]));
                    imgSelected2.setVisibility(View.INVISIBLE);
                    tvRemoveSelected2.setVisibility(View.INVISIBLE);
                } else if (selectedImagesCount == 1) {
                    otherImagePathTemp[1] = "";
                    imgSelected1.setVisibility(View.INVISIBLE);
                    tvRemoveSelected1.setVisibility(View.INVISIBLE);
                    selectedImagesLayout.setVisibility(View.GONE);
                    tvNoImageSelected.setVisibility(View.VISIBLE);
                }
                break;
            }
            case 2: {
                if (selectedImagesCount == 4) {
                    otherImagePathTemp[1] = otherImagePathTemp[2];
                    otherImagePathTemp[2] = otherImagePathTemp[3];
                    otherImagePathTemp[3] = "";
                    imgSelected2.setImageURI(Uri.parse(otherImagePathTemp[1]));
                    imgSelected3.setImageURI(Uri.parse(otherImagePathTemp[2]));
                    imgSelected4.setVisibility(View.INVISIBLE);
                    tvRemoveSelected4.setVisibility(View.INVISIBLE);
                } else if (selectedImagesCount == 3) {
                    otherImagePathTemp[1] = otherImagePathTemp[2];
                    otherImagePathTemp[2] = "";
                    imgSelected2.setImageURI(Uri.parse(otherImagePathTemp[1]));
                    imgSelected3.setVisibility(View.INVISIBLE);
                    tvRemoveSelected3.setVisibility(View.INVISIBLE);
                } else if (selectedImagesCount == 2) {
                    otherImagePathTemp[1] = "";
                    imgSelected2.setVisibility(View.INVISIBLE);
                    tvRemoveSelected2.setVisibility(View.INVISIBLE);
                }
                break;
            }
            case 3: {
                if (selectedImagesCount == 4) {
                    otherImagePathTemp[2] = otherImagePathTemp[3];
                    otherImagePathTemp[3] = "";
                    imgSelected3.setImageURI(Uri.parse(otherImagePathTemp[2]));
                    imgSelected4.setVisibility(View.INVISIBLE);
                    tvRemoveSelected4.setVisibility(View.INVISIBLE);
                } else if (selectedImagesCount == 3) {
                    otherImagePathTemp[2] = "";
                    imgSelected3.setVisibility(View.INVISIBLE);
                    tvRemoveSelected3.setVisibility(View.INVISIBLE);
                }
                break;
            }
            case 4: {
                otherImagePathTemp[3] = "";
                imgSelected4.setVisibility(View.INVISIBLE);
                tvRemoveSelected4.setVisibility(View.INVISIBLE);
                break;
            }
        }
        selectedImagesCount--;
        imgUploadText.setText((imgUploadCount + selectedImagesCount) + "");
    }

    public void removeUploadedImage(int index) {
        switch (index) {
            case 1: {
                if (imgUploadCount == 4) {
                    Glide.with(getContext())
                            .load(Uri.parse(getPubProfileModel.response.data.other_photos[1]))
                            .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .into(imgUploaded1);
                    Glide.with(getContext())
                            .load(Uri.parse(getPubProfileModel.response.data.other_photos[2]))
                            .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .into(imgUploaded2);
                    Glide.with(getContext())
                            .load(Uri.parse(getPubProfileModel.response.data.other_photos[3]))
                            .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .into(imgUploaded3);
                    imgUploaded4.setVisibility(View.INVISIBLE);
                    tvRemoveUploaded4.setVisibility(View.INVISIBLE);
                } else if (imgUploadCount == 3) {
                    Glide.with(getContext())
                            .load(Uri.parse(getPubProfileModel.response.data.other_photos[1]))
                            .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .into(imgUploaded1);
                    Glide.with(getContext())
                            .load(Uri.parse(getPubProfileModel.response.data.other_photos[2]))
                            .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .into(imgUploaded2);
                    imgUploaded3.setVisibility(View.INVISIBLE);
                    tvRemoveUploaded3.setVisibility(View.INVISIBLE);
                } else if (imgUploadCount == 2) {
                    Glide.with(getContext())
                            .load(Uri.parse(getPubProfileModel.response.data.other_photos[1]))
                            .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .into(imgUploaded1);
                    imgUploaded2.setVisibility(View.INVISIBLE);
                    tvRemoveUploaded2.setVisibility(View.INVISIBLE);
                } else if (imgUploadCount == 1) {
                    imgUploaded1.setVisibility(View.INVISIBLE);
                    tvRemoveUploaded1.setVisibility(View.INVISIBLE);
                    uploadedImagesLayout.setVisibility(View.GONE);
                    tvNoImageUploaded.setVisibility(View.VISIBLE);
                }
                break;
            }
            case 2: {
                if (imgUploadCount == 4) {
                    Glide.with(getContext())
                            .load(Uri.parse(getPubProfileModel.response.data.other_photos[2]))
                            .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .into(imgUploaded2);
                    Glide.with(getContext())
                            .load(Uri.parse(getPubProfileModel.response.data.other_photos[3]))
                            .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .into(imgUploaded3);
                    imgUploaded4.setVisibility(View.INVISIBLE);
                    tvRemoveUploaded4.setVisibility(View.INVISIBLE);
                } else if (imgUploadCount == 3) {
                    Glide.with(getContext())
                            .load(Uri.parse(getPubProfileModel.response.data.other_photos[2]))
                            .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .into(imgUploaded2);
                    imgUploaded3.setVisibility(View.INVISIBLE);
                    tvRemoveUploaded3.setVisibility(View.INVISIBLE);
                } else if (imgUploadCount == 2) {
                    imgUploaded2.setVisibility(View.INVISIBLE);
                    tvRemoveUploaded2.setVisibility(View.INVISIBLE);
                }
                break;
            }
            case 3: {
                if (imgUploadCount == 4) {
                    Glide.with(getContext())
                            .load(Uri.parse(getPubProfileModel.response.data.other_photos[3]))
                            .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                            .into(imgUploaded3);
                    imgUploaded4.setVisibility(View.INVISIBLE);
                    tvRemoveUploaded4.setVisibility(View.INVISIBLE);
                } else if (imgUploadCount == 3) {
                    imgUploaded3.setVisibility(View.INVISIBLE);
                    tvRemoveUploaded3.setVisibility(View.INVISIBLE);
                }
                break;
            }
            case 4: {
                imgUploaded4.setVisibility(View.INVISIBLE);
                tvRemoveUploaded4.setVisibility(View.INVISIBLE);
                break;
            }
        }
        imgUploadCount--;
        imgUploadText.setText((imgUploadCount+selectedImagesCount) + "");
        removedImagesList.add(getPubProfileModel.response.data.other_photos[index - 1]);
    }

    public ArrayAdapter<String> getArrayAdapter(String[] items) {
        return new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
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

    public void callGetPubProfileApi() {
        SharedPreferences preferences = getContext().getSharedPreferences("pub_details", MODE_PRIVATE);
        Map<String, String> params = new HashMap<String, String>();
        String url = CommonUtilities.key_get_pub_profile + "&pub_id=" + preferences.getString("pub_id", "");

        ServerAccess.getResponse(getContext(), url, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                getPubProfileModel = new GetPubProfileModel().GetPubProfileModel(result);
                if (getPubProfileModel != null) {
                    if (getPubProfileModel.response.code.equals(CommonUtilities.key_success_code)) {
                        GetPubProfileModel.response.data data = getPubProfileModel.response.data;
                        edtPremisesName.setText(data.about_pub);

                        Glide.with(getContext())
                                .load(Uri.parse(data.pub_profile))
                                .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                                .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                                .into(imgProfile);
                        profilePictureLink = data.pub_profile;

                        Glide.with(getContext())
                                .load(Uri.parse(data.pub_featured_Image))
                                .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                                .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                                .into(imgFeaturedImage);
                        featuredImageLink = data.pub_featured_Image;

                        String[] tempOther = new String[]{"","","",""};
                        int tempIndex = 0;

                        for(int i=0; i<data.other_photos.length; i++){
                            if(!data.other_photos[i].equals("")){
                                tempOther[tempIndex] = data.other_photos[i];
                                tempIndex++;
                            }
                        }

                        data.other_photos = tempOther;

                        int tempCount = 0;

                        Log.d(TAG, "data.other_photos: "+data.other_photos);

                        if (!data.other_photos[0].equals("")) {
                            uploadedImagesLayout.setVisibility(View.VISIBLE);
                            tvNoImageUploaded.setVisibility(View.GONE);
                            otherImagePathTemp[0] = data.other_photos[0];
                            imgUploaded1.setVisibility(View.VISIBLE);
                            tvRemoveUploaded1.setVisibility(View.VISIBLE);
                            Glide.with(getContext())
                                    .load(Uri.parse(data.other_photos[0]))
                                    .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                                    .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                                    .into(imgUploaded1);
                            tempCount++;
                        } else {
                            uploadedImagesLayout.setVisibility(View.GONE);
                            tvNoImageUploaded.setVisibility(View.VISIBLE);
                        }

                        if (!data.other_photos[1].equals("")) {
                            otherImagePathTemp[1] = data.other_photos[1];
                            imgUploaded2.setVisibility(View.VISIBLE);
                            tvRemoveUploaded2.setVisibility(View.VISIBLE);
                            Glide.with(getContext())
                                    .load(Uri.parse(data.other_photos[1]))
                                    .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                                    .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                                    .into(imgUploaded2);
                            tempCount++;
                        }else{
                            imgUploaded2.setVisibility(View.GONE);
                            tvRemoveUploaded2.setVisibility(View.GONE);
                        }

                        if (!data.other_photos[2].equals("")) {
                            otherImagePathTemp[2] = data.other_photos[2];
                            imgUploaded3.setVisibility(View.VISIBLE);
                            tvRemoveUploaded3.setVisibility(View.VISIBLE);
                            Glide.with(getContext())
                                    .load(Uri.parse(data.other_photos[2]))
                                    .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                                    .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                                    .into(imgUploaded3);
                            tempCount++;
                        }else{
                            imgUploaded3.setVisibility(View.GONE);
                            tvRemoveUploaded3.setVisibility(View.GONE);
                        }

                        if (!data.other_photos[3].equals("")) {
                            otherImagePathTemp[3] = data.other_photos[3];
                            imgUploaded4.setVisibility(View.VISIBLE);
                            tvRemoveUploaded4.setVisibility(View.VISIBLE);
                            Glide.with(getContext())
                                    .load(Uri.parse(data.other_photos[3]))
                                    .placeholder(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                                    .error(com.nguyenhoanglam.imagepicker.R.drawable.image_placeholder)
                                    .into(imgUploaded4);
                            tempCount++;
                        }else{
                            imgUploaded4.setVisibility(View.GONE);
                            tvRemoveUploaded4.setVisibility(View.GONE);
                        }

                        imgUploadCount = tempCount;
                        imgUploadText.setText(imgUploadCount + "");

                        if (data.one_status.equals("0")) {
                            liveSportsSpinner.setSelection(1);
                        } else {
                            liveSportsSpinner.setSelection(0);
                            edtLiveSports.setVisibility(View.VISIBLE);
                            edtLiveSports.setText(data.one_comment);
                        }

                        if (data.two_status.equals("0")) {
                            foodSpinner.setSelection(1);
                        } else {
                            foodSpinner.setSelection(0);
                        }


                        if (data.three_status.equals("0")) {
                            karaokeSpinner.setSelection(1);
                        } else {
                            karaokeSpinner.setSelection(0);
                            edtKaraoke.setVisibility(View.VISIBLE);
                            edtKaraoke.setText(data.three_comment);
                        }

                        if (data.four_status.equals("0")) {
                            liveMusicSpinner.setSelection(1);
                        } else {
                            liveMusicSpinner.setSelection(0);
                            edtLiveMusic.setVisibility(View.VISIBLE);
                            edtLiveMusic.setText(data.four_comment);
                        }

                        edtAverageDrinkPrice.setText(data.five_comment);

                        if (data.six_status.equals("0")) {
                            doorChargeSpinner.setSelection(1);
                        } else {
                            doorChargeSpinner.setSelection(0);
                            edtDoorCharge.setVisibility(View.VISIBLE);
                            edtDoorCharge.setText(data.six_comment);
                        }

                        if (data.seven_status.equals("0")) {
                            freeWifiSpinner.setSelection(1);
                        } else {
                            freeWifiSpinner.setSelection(0);
                        }

                        if (data.eight_status.equals("0")) {
                            disabledAccessSpinner.setSelection(1);
                        } else {
                            disabledAccessSpinner.setSelection(0);
                        }


                        if (data.nine_status.equals("0")) {
                            quizNightsSpinner.setSelection(1);
                        } else {
                            quizNightsSpinner.setSelection(0);
                            edtQuizNights.setVisibility(View.VISIBLE);
                            edtQuizNights.setText(data.nine_comment);
                        }


                        if (data.ten_status.equals("0")) {
                            realAleSpinner.setSelection(1);
                        } else {
                            realAleSpinner.setSelection(0);
                        }

                        if (data.eleven_status.equals("0")) {
                            minimumAgeSpinner.setSelection(1);
                        } else {
                            minimumAgeSpinner.setSelection(0);
                            edtMinimumAge.setVisibility(View.VISIBLE);
                            edtMinimumAge.setText(data.eleven_comment);
                        }

                        if (data.twelve_status.equals("0")) {
                            childrenAllowedSpinner.setSelection(1);
                        } else {
                            childrenAllowedSpinner.setSelection(0);
                        }

                        if (data.thirteen_status.equals("0")) {
                            dogsAllowedSpinner.setSelection(1);
                        } else {
                            dogsAllowedSpinner.setSelection(0);
                        }
                    } else {
                        CommonUtilities.ShowToast(getContext(), getPubProfileModel.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(getContext(), "Something went wrong!");
            }
        });
    }

    public void callUpdatePubProfileApi() {
        SharedPreferences preferences = getContext().getSharedPreferences("pub_details", MODE_PRIVATE);
        Map<String, String> params = new HashMap<String, String>();

        StringBuilder otherImageArr = new StringBuilder();

        params.put("pub_id", preferences.getString("pub_id", ""));
        params.put("about_pub", edtPremisesName.getText().toString().trim());

        if(!profilePicturePath.equals("")) {
            params.put("pub_profile", getStringImage(profilePicturePath));
        }

        if(!featuredImagePath.equals("")) {
            params.put("pub_featured_Image", getStringImage(featuredImagePath));
        }

        StringBuilder removedImageString = new StringBuilder();
        for(int i=0; i<removedImagesList.size(); i++){
            if(i < removedImagesList.size() - 1) {
                removedImageString.append(removedImagesList.get(i)).append(",");
            }else{
                removedImageString.append(removedImagesList.get(i));
            }
        }
        params.put("remove_img_arr", String.valueOf(removedImageString));

        Log.d(TAG, "selectedImagesCount: "+selectedImagesCount);


        for (int i = 0; i < selectedImagesCount; i++) {
            Log.d(TAG, "callUpdatePubProfileApi: "+otherImagePathTemp[i]);

            if (i < selectedImagesCount - 1) {
                otherImageArr.append(getStringImage(otherImagePathTemp[i])).append(",");
            } else {
                otherImageArr.append(getStringImage(otherImagePathTemp[i]));
            }
        }

        params.put("other_photos", String.valueOf(otherImageArr));

        //live sports
        if (liveSportsSpinner.getSelectedItem().toString().equals(yesNoItems[1])) {
            params.put("1_status", "0");
        } else {
            params.put("1_status", "1");
            params.put("1_comment", edtLiveSports.getText().toString().trim());
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
            params.put("3_comment", edtKaraoke.getText().toString().trim());
        }

        //live music
        if (liveMusicSpinner.getSelectedItem().toString().equals(yesNoItems[1])) {
            params.put("4_status", "0");
        } else {
            params.put("4_status", "1");
            params.put("4_comment", edtLiveMusic.getText().toString().trim());
        }

        //average drink price
        params.put("5_comment", edtAverageDrinkPrice.getText().toString().trim());

        //door charge
        if (doorChargeSpinner.getSelectedItem().toString().equals(yesNoItems[1])) {
            params.put("6_status", "0");
        } else {
            params.put("6_status", "1");
            params.put("6_comment", edtDoorCharge.getText().toString().trim());
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
            params.put("9_comment", edtQuizNights.getText().toString().trim());
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
            params.put("11_comment", edtMinimumAge.getText().toString().trim());
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


        ServerAccess.getResponse(getContext(), CommonUtilities.key_update_pub_profile, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                ChangePasswordModel changePasswordModel = new ChangePasswordModel().ChangePasswordModel(result);
                if (changePasswordModel != null) {
                    CommonUtilities.ShowToast(getContext(), changePasswordModel.response.msg);
                    if(changePasswordModel.response.code.equals(CommonUtilities.key_success_code)){
                        imgUploadCount = 0;
                        selectedImagesCount = 0;
                        checkImage = 0;
                        profilePicturePath = "";
                        profilePictureLink = "";
                        featuredImagePath = "";
                        featuredImageLink = "";
                        otherImagePathTemp = new String[]{"", "", "", ""};
                        removedImagesList.clear();
                        selectedImagesLayout.setVisibility(View.GONE);
                        tvNoImageSelected.setVisibility(View.VISIBLE);
                        tvRemoveProfile.setVisibility(View.INVISIBLE);
                        tvRemoveFeaturedImage.setVisibility(View.INVISIBLE);
                        callGetPubProfileApi();
                    }
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(getContext(), "Something went wrong!");
            }
        });
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
        String uriSting = getContext().getCacheDir().getPath() + "/" + System.currentTimeMillis() + ".jpg";
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
}