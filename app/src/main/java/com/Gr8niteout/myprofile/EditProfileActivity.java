package com.Gr8niteout.myprofile;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CircleTransform;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.RoundImageview;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.model.Edit_Profile_Model;
import com.Gr8niteout.model.SignUpModel;
import com.Gr8niteout.signup.SignUpMobile;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.BindView;

import static android.graphics.BitmapFactory.decodeFile;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;


public class EditProfileActivity extends AppCompatActivity {

    public static Uri fileUri; // file url to store image/video
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
//    @BindView(R.id.toolbar)
    Toolbar toolbar;
//    @BindView(R.id.imgProfile)
    RoundImageview imgProfile;

//    @BindView(R.id.input_firstname)
    EditText input_firstname;
//    @BindView(R.id.input_lastname)
    EditText input_lastname;
//    @BindView(R.id.input_email)
    EditText input_email;
//    @BindView(R.id.input_mobile_no)
    EditText input_mobile_no;

//    @BindView(R.id.txtEditPhoto)
    TextView txtEditPhoto;

//    @BindView(R.id.txtEmail_static)
    TextView txtEmail_static;

//    @BindView(R.id.txtfirstname_static)
    TextView txtfirstname_static;

//    @BindView(R.id.txtsecname_static)
    TextView txtsecname_static;

//    @BindView(R.id.txtMobile_static)
    TextView txtMobile_static;

//    @BindView(R.id.txtEditProfile)
    TextView txtEditProfile;
//    @BindView(R.id.image_layout)
    RelativeLayout image_layout;

//    @BindView(R.id.txtSave)txtMobile_static
    TextView txtSave;
//    @BindView(R.id.First_Lett)
    TextView First_Lett;
    SignUpModel model;
    String prof_img = "";
    Edit_Profile_Model model2;

    final int PIC_CROP = 3;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_new);
        ButterKnife.bind(this);


        toolbar = findViewById(R.id.toolbar);
        imgProfile = findViewById(R.id.imgProfile);
        input_firstname = findViewById(R.id.input_firstname);
        input_lastname = findViewById(R.id.input_lastname);
        input_email = findViewById(R.id.input_email);
        input_mobile_no = findViewById(R.id.input_mobile_no);
        txtEditPhoto = findViewById(R.id.txtEditPhoto);
        txtEmail_static = findViewById(R.id.txtEmail_static);
        txtfirstname_static = findViewById(R.id.txtfirstname_static);
        txtsecname_static = findViewById(R.id.txtsecname_static);
        txtMobile_static = findViewById(R.id.txtMobile_static);
        txtEditProfile = findViewById(R.id.txtEditProfile);
        image_layout = findViewById(R.id.image_layout);
        First_Lett = findViewById(R.id.First_Lett);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        setSupportActionBar(toolbar);
        toolbar.setContentInsetStartWithNavigation(0);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setFont();

        model = new SignUpModel().SignUpModel(CommonUtilities.getPreference(EditProfileActivity.this, CommonUtilities.pref_UserData));

        input_firstname.setText(model.response.user_data.fname);

        input_firstname.setSelection(input_firstname.getText().length());

        input_lastname.setText(model.response.user_data.lname);

        input_email.setText(model.response.user_data.email);


        CommonUtilities.setAsteric(EditProfileActivity.this, "First Name", txtfirstname_static);
        CommonUtilities.setAsteric(EditProfileActivity.this, "Last Name", txtsecname_static);
        CommonUtilities.setAsteric(EditProfileActivity.this, "Mobile Number", txtMobile_static);

        if (!input_email.getText().toString().equals("")) {
            input_email.setFocusable(false);
            input_email.setClickable(false);
            input_email.setFocusableInTouchMode(false);
            input_email.setEnabled(false);
        } else {
            input_email.setFocusable(true);
            input_email.setClickable(true);
            input_email.setFocusableInTouchMode(true);
            input_email.setEnabled(true);
        }

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PERMISSIONS = new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA};
        }
        CommonUtilities.setPermission(EditProfileActivity.this, PERMISSIONS);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_cc_code) ||
                getIntent().getExtras().containsKey(CommonUtilities.key_mobile)) {
            String cc_code = getIntent().getExtras().getString(CommonUtilities.key_cc_code);
            String mob = getIntent().getExtras().getString(CommonUtilities.key_mobile);
            if (mob.length() > 6) {
                String firstthree = mob.substring(0, 3);
                String secthree = mob.substring(3, 6);
                String lastfour = mob.substring(6, mob.length());
                input_mobile_no.setText("+" + cc_code + " " + firstthree + " " + secthree + " " + lastfour);
            } else {
                input_mobile_no.setText("+" + cc_code + mob);
            }
        } else {
            if (!model.response.user_data.getMobile().equals("") && model.response.user_data.getMobile().length() > 6) {
                String main = model.response.user_data.getMobile();
                String firstthree = main.substring(0, 3);
                String secthree = main.substring(3, 6);
                String lastfour = main.substring(6, model.response.user_data.getMobile().length());
                input_mobile_no.setText("+" + model.response.user_data.getCc_code() + " " + firstthree + " " + secthree + " " + lastfour);
            } else
                input_mobile_no.setText("+" + model.response.user_data.getCc_code() + " " + model.response.user_data.getMobile());
        }


        if (model.response.user_data.photo.equals("")) {
            image_layout.setBackgroundResource(R.drawable.round);
            First_Lett.setText(model.response.user_data.getFname().substring(0, 1));
        } else {
//            changed on 28-jan-2019
            Picasso.get()
                    .load(CommonUtilities.Gr8niteoutURL + CommonUtilities.User_Profile_URL +
                            model.response.user_data.photo)
                    .transform(new CircleTransform())
                    .error(R.mipmap.user).placeholder(R.mipmap.user) // optional
                    .into(imgProfile, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            scanLicence();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)) {
                    CommonUtilities.alertdialog(EditProfileActivity.this, "Allow Media Images permission manually");
                } else if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    CommonUtilities.alertdialog(EditProfileActivity.this, "Allow Camera permission manually");
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    CommonUtilities.alertdialog(EditProfileActivity.this, "Allow External Storage permission manually");
                } else if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    CommonUtilities.alertdialog(EditProfileActivity.this, "Allow Camera permission manually");
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void mobileClick(View v) {
        Intent i = new Intent(EditProfileActivity.this, SignUpMobile.class);
        i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_profile);
        startActivity(i);
    }

    public void picChange(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String[] PERMISSIONS = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA};
            if (!CommonUtilities.hasPermissions(EditProfileActivity.this, PERMISSIONS)) {
                CommonUtilities.setPermission(EditProfileActivity.this, PERMISSIONS);
            } else
                scanLicence();
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            if (!CommonUtilities.hasPermissions(EditProfileActivity.this, PERMISSIONS)) {
                CommonUtilities.setPermission(EditProfileActivity.this, PERMISSIONS);
            } else
                scanLicence();

        } else {
            scanLicence();
        }
    }

    private boolean checkReadPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;

        }
    }

    private void requestReadCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE);

    }

    public void scanLicence() {
        final CharSequence[] items = {"Take Photo", "Choose Existing", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {

                    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
                    intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                    intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                    startActivityForResult(intent, REQUEST_CAMERA);

                } else if (items[item].equals("Choose Existing")) {
                    if (!checkReadPermission()) {
                        requestReadCameraPermission();

                    } else {

                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select File"),
                                SELECT_FILE);
                    }

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
     * returning image / video
     */
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

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
//        mediaFile = new File(mediaStorageDir.getPath() + File.separator
//                + "IMG_" + timeStamp + ".jpg");

        mediaFile = new File(picturesDirectory + File.separator
                + "IMG_" + timeStamp + ".jpg");


        return mediaFile;
    }

    private void onSelectFromGalleryResult(Intent data) {


        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(selectedImageUri, projection, null,
                null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);
        String path = compressImage(selectedImagePath);
        Bitmap bm = decodeFile(path);
        //imgProfile.setImageBitmap(bm);

//        changed on 28-jan-2019
        Picasso.get()
                .load(Uri.fromFile(new File(path)))
                .transform(new CircleTransform())
                .error(R.mipmap.user).placeholder(R.mipmap.user) // optional
                .into(imgProfile, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }

                });

        prof_img = convert_img_base64(bm);
        First_Lett.setText("");
        // isImageSelected = true;

//        Intent intent2 = new Intent(EditProfileActivity.this, CropImage.class);
//        intent2.putExtra("imageUri", selectedImagePath);
//        intent2.putExtra("flag","2");
//        startActivityForResult(intent2, PIC_CROP);

        // performCrop();
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth,
                                                     int reqHeight) { // BEST QUALITY MATCH

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            // if(Math.round((float)width / (float)reqWidth) > inSampleSize) //
            // If bigger SampSize..
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    private void onCaptureImageResult(Intent data) {

//        compressImage(fileUri.getPath());
        Bitmap bm = decodeSampledBitmapFromFile(compressImage(fileUri.getPath()),
                300, 300);

        // isImageSelected = true;
        imgProfile.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //  imgProfile.setImageBitmap(bm);

//        changed on 28-jan-2019
        Picasso.get()
                .load(Uri.fromFile(new File(fileUri.getPath())))
                .transform(new CircleTransform())
                .error(R.mipmap.user).placeholder(R.mipmap.user) // optional
                .into(imgProfile, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
        prof_img = convert_img_base64(bm);
        First_Lett.setText("");

        // originalIcon.setImageBitmap(bm);

//        Intent intent2 = new Intent(EditProfileActivity.this, CropImage.class);
//        intent2.putExtra("imageUri", fileUri.getPath());
//        intent2.putExtra("flag","2");
//        startActivityForResult(intent2, PIC_CROP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if (resultCode == RESULT_OK) {

            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
//            else if (requestCode == PIC_CROP) {
//                bitmap = Bitmap.createScaledBitmap(CropImage.transferDat, (int) getResources().getDimension(R.dimen.imageHeight), 300, false);
//
//                if (licenceFlag == 0) {
//                    licenceFrontImg.setImageBitmap(bitmap);
//                    strlicenceFront = convert_img_base64(bitmap).toString();
//
//                } else {
//                    licenceBackImg.setImageBitmap(bitmap);
//                    strlicenceBack = convert_img_base64(bitmap).toString();
//                }
//            }
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    public String convert_img_base64(Bitmap bmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;

    }

    public void SaveClick(View v) {

        if (input_firstname.getText().toString().equals("")) {
            CommonUtilities.showSnackbar(findViewById(R.id.input_firstname), "Please Enter First Name");
        } else if (input_lastname.getText().toString().equals("")) {
            CommonUtilities.showSnackbar(findViewById(R.id.input_lastname), "Please Enter Last Name");
        }
//        else if (input_email.getText().toString().equals("")) {
//            CommonUtilities.showSnackbar(findViewById(R.id.input_email),"Please enter email");
//        }
        else if (input_email.getText().toString().length() > 0 && !CommonUtilities.emailValidator(input_email.getText().toString())) {
            CommonUtilities.showSnackbar(findViewById(R.id.input_email), "Please enter valid email address");
        } else {
            onSave();
        }
    }

    public void onSave() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(CommonUtilities.key_user_id, model.response.user_data.user_id);
        params.put(CommonUtilities.pref_fb_fname, input_firstname.getText().toString());
        params.put(CommonUtilities.pref_fb_lname, input_lastname.getText().toString());
        params.put(CommonUtilities.pref_fb_photo, prof_img);
//        params.put(CommonUtilities.pref_fb_photo, "");

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CommonUtilities.key_cc_code) ||
                getIntent().getExtras().containsKey(CommonUtilities.key_mobile)) {
            String cc_code = getIntent().getExtras().getString(CommonUtilities.key_cc_code);
            String mob = getIntent().getExtras().getString(CommonUtilities.key_mobile);
//            input_mobile_no.setText("+" + cc_code + mob);
            params.put(CommonUtilities.pref_fb_mobile, mob);
            params.put(CommonUtilities.key_cc_code, cc_code);
        } else {
            params.put(CommonUtilities.pref_fb_mobile, model.response.user_data.getMobile());
            params.put(CommonUtilities.key_cc_code, model.response.user_data.getCc_code());
        }
        params.put(CommonUtilities.pref_fb_email, input_email.getText().toString());
//        params.put(CommonUtilities.pref_fb_email, "");

        ServerAccess.getResponse(EditProfileActivity.this, CommonUtilities.key_edit_profile, params, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                model2 = new Edit_Profile_Model().Edit_Profile(result);
                if (model2 != null) {
                    if (model2.response.status.equals(CommonUtilities.key_Success)) {

                        model.response.user_data.setFname(input_firstname.getText().toString());
                        model.response.user_data.setLname(input_lastname.getText().toString());
                        model.response.user_data.setMobile(model2.response.profile_status.getMobile());
                        model.response.user_data.setCc_code(model2.response.profile_status.getCc_code());
//                        model.response.user_data.setEmail("");
                        model.response.user_data.setEmail(input_email.getText().toString());
                        if (!prof_img.equals("")) {
                            model.response.user_data.setPhoto(model2.response.profile_status.getPhoto());
//                            model.response.user_data.setPhoto("");
                        }

                        CommonUtilities.setPreference(EditProfileActivity.this, CommonUtilities.pref_UserData, new Gson().toJson(model));
                        CommonUtilities.ShowToast(EditProfileActivity.this, "Your profile is now up to date");
                        finish();
                    } else {
                        CommonUtilities.alertdialog(EditProfileActivity.this, model2.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void onBackPressed() {
        finish();
    }

    public void setFont() {

        CommonUtilities.setFontFamily(EditProfileActivity.this, txtEditProfile, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(EditProfileActivity.this, txtSave, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(EditProfileActivity.this, txtEditPhoto, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(EditProfileActivity.this, txtMobile_static, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(EditProfileActivity.this, txtEmail_static, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(EditProfileActivity.this, txtfirstname_static, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(EditProfileActivity.this, txtsecname_static, CommonUtilities.AvenirLTStd_Medium);
        CommonUtilities.setFontFamily(EditProfileActivity.this, input_firstname, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(EditProfileActivity.this, input_lastname, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(EditProfileActivity.this, input_mobile_no, CommonUtilities.Avenir_Heavy);
        CommonUtilities.setFontFamily(EditProfileActivity.this, input_email, CommonUtilities.Avenir_Heavy);
    }


    @Override
    protected void onStart() {
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
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
}
