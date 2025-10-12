package com.Gr8niteout.RegisterPubScreens;

import static android.graphics.BitmapFactory.decodeFile;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.Gr8niteout.RegisterPubScreens.ValidationCases.params;
import static com.Gr8niteout.myprofile.EditProfileActivity.fileUri;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
//import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.CheckUserEmailModel;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PersonalInformationActivity1 extends AppCompatActivity {

//    RadioGroup radioGroup;
    Spinner titleSpinner, genderSpinner;
    TextView btnNext, tvDOB, tvProfile, tvRemove;
    EditText edtUserName, edtFirstName, edtLastName, edtPositionHeld, edtMobileNumber, edtConfirmMobileNumber, edtEmail, edtConfirmEmail, edtPassword, edtConfirmPassword, edtDob;
    LinearLayout btnUpload;
    ImageView imgProfile;

    String[] titleItems = new String[]{"Mr.", "Miss", "Mrs."};
    String[] genderItems = new String[]{"Male", "Female"};
    boolean imgAttached = false;

    String selectedDate = "";
    String selectedMonth = "";
    String selectedYear = "";

    EditText[] list;
    String[] errorTextList;

    ValidationCases validation;

    List<String> items;
    private AlertDialog dialogPic;
    private ArrayList<Image> images = new ArrayList<>();

    private final int CAMERA_REQUEST_CODE = 100;
    private final int GALLERY_REQUEST_CODE = 2000;
    private final String IMAGE_DIRECTORY_NAME = "Gr8niteout";
    String timeStamp;

    String profilePath = "";

    public CheckUserEmailModel checkUserEmailModel;

    boolean check1 = false;
    boolean check2 = false;
    boolean check3 = false;
    boolean check4 = false;
    boolean check5 = false;
    boolean check6 = false;
    boolean check7 = false;
    boolean check8 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information1);

//        radioGroup = findViewById(R.id.radioGroup);
        titleSpinner = findViewById(R.id.titleSpinner);
        genderSpinner = findViewById(R.id.genderSpinner);
        edtDob = findViewById(R.id.edtDob);
        btnNext = findViewById(R.id.btnNext);
        edtUserName = findViewById(R.id.edtUserName);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtPositionHeld = findViewById(R.id.edtPositionHeld);
        edtMobileNumber = findViewById(R.id.edtMobileNumber);
        edtConfirmMobileNumber = findViewById(R.id.edtConfirmMobileNumber);
        edtEmail = findViewById(R.id.edtEmail);
        edtConfirmEmail = findViewById(R.id.edtConfirmEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        tvDOB = findViewById(R.id.tvDOB);
        btnUpload = findViewById(R.id.btnUpload);
        imgProfile = findViewById(R.id.imgProfile);
        tvProfile = findViewById(R.id.tvProfile);
        tvRemove = findViewById(R.id.tvRemove);
//        txtAcceptPubCredit = findViewById(R.id.txtAcceptPubCredit);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        list = new EditText[]{
                edtUserName, edtFirstName, edtLastName, edtPositionHeld, edtMobileNumber, edtConfirmMobileNumber, edtEmail, edtConfirmEmail
                , edtPassword, edtConfirmPassword
        };

        errorTextList = new String[]{
                "User name is required and should be alphanumeric.",
                "First name is required and must be a string.",
                "Last name is required and must be a string.",
                "Position held is required and must be a string.",
                "Mobile number is required and it must be numeric.",
                "Mobile number and confirm number must be same.",
                "Email address is required.",
                "Email address and confirm email must be same.",
                "Password must be 8-20 characters long.",
                "Password and confirm password must be same."
        };

        validation = new ValidationCases();

        validation.firstLetterSpace(list);

        titleSpinner.setAdapter(getArrayAdapter(titleItems));
        genderSpinner.setAdapter(getArrayAdapter(genderItems));

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    String[] PERMISSIONS = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA};
                    if (!CommonUtilities.hasPermissions(PersonalInformationActivity1.this, PERMISSIONS)) {
                        CommonUtilities.setPermission(PersonalInformationActivity1.this, PERMISSIONS);
                    }else{
                        captureImageInitialization(1);
                    }
                }else{
                    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                    if (!CommonUtilities.hasPermissions(PersonalInformationActivity1.this, PERMISSIONS)) {
                        CommonUtilities.setPermission(PersonalInformationActivity1.this, PERMISSIONS);
                    } else {
                        captureImageInitialization(1);
                    }
                }

//                if(Build.VERSION.SDK_INT >= 30) {
//                    if (!Environment.isExternalStorageManager()){
//                        Intent intent = new Intent();
//                        intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                        Uri uri = Uri.fromParts("package", PersonalInformationActivity1.this.getPackageName(), null);
//                        intent.setData(uri);
//                        startActivity(intent);
//                    }else{
//                        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
//                        if (!CommonUtilities.hasPermissions(PersonalInformationActivity1.this, PERMISSIONS)) {
//                            CommonUtilities.setPermission(PersonalInformationActivity1.this, PERMISSIONS);
//                        } else {
//                            captureImageInitialization(1);
//                        }
//                    }
//                }else{
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
//                        if (!CommonUtilities.hasPermissions(PersonalInformationActivity1.this, PERMISSIONS)) {
//                            CommonUtilities.setPermission(PersonalInformationActivity1.this, PERMISSIONS);
//                        } else {
//                            captureImageInitialization(1);
//                        }
//                    } else {
//                        captureImageInitialization(1);
//                    }
//                }
            }
        });

        tvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgAttached = false;
                imgProfile.setImageResource(R.drawable.pub_user_profile);
                tvRemove.setVisibility(View.INVISIBLE);
            }
        });

        edtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(PersonalInformationActivity1.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        edtDob.setText(day + "-" + (month + 1) + "-" + year);
                        selectedDate = day + "";
                        selectedMonth = (month + 1) + "";
                        selectedYear = year + "";
                        tvDOB.setError(null);
                    }
                }, year - 18, month, day);
                Calendar minDate = new GregorianCalendar(year - 18, month, day);
                dialog.getDatePicker().setMaxDate(minDate.getTimeInMillis());
                dialog.show();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check1 = validation.isEmptyEditText(list, errorTextList);
                check2 = validation.matchTwoFields(edtPassword, edtConfirmPassword, "Password and confirm password must be same.");
                check3 = validation.matchTwoFields(edtEmail, edtConfirmEmail, "Email address and confirm email must be same.");
                check4 = validation.matchTwoFields(edtMobileNumber, edtConfirmMobileNumber, "Mobile number and confirm number must be same.");
                check5 = validation.isEmailValid(edtEmail);
                check6 = false;
                check7 = false;
                check8 = false;
//                check8 = false;

//                if (radioGroup.getCheckedRadioButtonId() == -1) {
//                    txtAcceptPubCredit.setError("Please select accept pub credits option");
//                    check8 = true;
//                } else {
//                    txtAcceptPubCredit.setError(null);
//                }

                if(!edtPositionHeld.getText().toString().trim().matches(".*[a-zA-Z].*")){
                    edtPositionHeld.setError("Position held is required and must be a string.");
                    check8 = true;
                }else{
                    edtPositionHeld.setError(null);
                }

                if (edtPassword.getText().toString().length() < 8) {
                    edtPassword.setError("Password must be 8-20 characters long.");
                    check6 = true;
                }

                if (selectedDate.equals("") || selectedMonth.equals("") || selectedYear.equals("")) {
                    tvDOB.setError("Please Select Date Of Birth!");
                    check7 = true;
                } else {
                    tvDOB.setError(null);
                }

                if (!imgAttached) {
                    tvProfile.setError("Please upload profile picture.");
                } else {
                    tvProfile.setError(null);
                }

                if (!check1 && !check2 && !check3 && !check4 && !check5 && !check6 && !check7 && !check8 && imgAttached) {
                    callCheckEmailUsernameApi();
                }

            }
        });

    }

    public ArrayAdapter<String> getArrayAdapter(String[] items) {
        return new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
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

            File file = new File(images.get(0).getPath());

            if(file.length() > 8010000){
                CommonUtilities.ShowToast(PersonalInformationActivity1.this, "Image size must be less than 8 MB.");
            }else if(getDropboxIMGSize(Uri.parse(images.get(0).getPath()),120,120)){
                CommonUtilities.ShowToast(PersonalInformationActivity1.this,"Your image is too small, please upload an image bigger than 120*120");
            }else{
                profilePath = compressImage(images.get(0).getPath());
                imgProfile.setImageURI(Uri.parse(images.get(0).getPath()));
                imgAttached = true;
                tvProfile.setError(null);
                tvRemove.setVisibility(View.VISIBLE);
            }
        }

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                images.clear();
                new SingleMediaScanner(PersonalInformationActivity1.this, fileUri.getPath());
                images.add(new Image(1, timeStamp, fileUri.getPath(), false, ""));

                File file = new File(images.get(0).getPath());

                if(file.length() > 8010000){
                    CommonUtilities.ShowToast(PersonalInformationActivity1.this, "Image size must be less than 8 MB.");
                } else if(getDropboxIMGSize(Uri.parse(images.get(0).getPath()),120,120)){
                    CommonUtilities.ShowToast(PersonalInformationActivity1.this,"Your image is too small, please upload an image bigger than 120*120");
                } else {
                    profilePath = compressImage(images.get(0).getPath());
                    imgProfile.setImageURI(Uri.parse(images.get(0).getPath()));
                    imgAttached = true;
                    tvProfile.setError(null);
                    tvRemove.setVisibility(View.VISIBLE);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PersonalInformationActivity1.this,
                android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInformationActivity1.this);
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

    public String getStringImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(profilePath, options);
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

    public void callCheckEmailUsernameApi() {
        Map<String, String> paramsTemp = new HashMap<>();
        paramsTemp.put("username", edtUserName.getText().toString().trim());
        paramsTemp.put("email", edtEmail.getText().toString().trim());
        ServerAccess.getResponse(PersonalInformationActivity1.this, CommonUtilities.key_user_email_check, paramsTemp, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                checkUserEmailModel = new CheckUserEmailModel().CheckUserEmailModel(result);
                if (checkUserEmailModel.response.ResponseInfo.valid) {
                    // Accept pub credits
//                    if (radioGroup.getCheckedRadioButtonId() == R.id.radio1) {
//                        params.put("accept_credit", "1");
//                    } else {
//                        params.put("accept_credit", "0");
//                    }
                    params.put("accept_credit", "0");

                    // title
                    if (titleSpinner.getSelectedItem().toString().equals(titleItems[0])) {
                        params.put("title", "0");
                    } else if (titleSpinner.getSelectedItem().toString().equals(titleItems[1])) {
                        params.put("title", "1");
                    } else {
                        params.put("title", "2");
                    }

                    //username
                    params.put("user_name", edtUserName.getText().toString());

                    //first name
                    params.put("first_name", edtFirstName.getText().toString());

                    //last name
                    params.put("last_name", edtLastName.getText().toString());

                    //position held
                    params.put("position", edtPositionHeld.getText().toString());

                    //gender
                    if (genderSpinner.getSelectedItem().toString().equals(genderItems[0])) {
                        params.put("gender", "0");
                    } else {
                        params.put("gender", "1");
                    }

                    params.put("day", selectedDate);
                    params.put("month", selectedMonth);
                    params.put("year", selectedYear);
                    params.put("phone", edtMobileNumber.getText().toString());
                    params.put("cphone", edtConfirmMobileNumber.getText().toString());
                    params.put("email", edtEmail.getText().toString());
                    params.put("cemail", edtConfirmEmail.getText().toString());
                    params.put("password", edtPassword.getText().toString());
                    params.put("cpassword", edtConfirmPassword.getText().toString());
                    params.put("user_profile_picture", getStringImage());

                    startActivity(new Intent(PersonalInformationActivity1.this, PersonalInformationActivity2.class));
                } else {
                    CommonUtilities.ShowToast(PersonalInformationActivity1.this, checkUserEmailModel.response.ResponseInfo.message);
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(PersonalInformationActivity1.this, "Something went wrong!");
            }
        });
    }

}