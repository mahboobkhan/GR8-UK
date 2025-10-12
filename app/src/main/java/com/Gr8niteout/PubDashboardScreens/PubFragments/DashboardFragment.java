package com.Gr8niteout.PubDashboardScreens.PubFragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.graphics.BitmapFactory.decodeFile;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.Gr8niteout.myprofile.EditProfileActivity.fileUri;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Gr8niteout.PubDashboardScreens.DashboardHomeActivity;
import com.Gr8niteout.PubDashboardScreens.Models.AddNewNoticeModel;
import com.Gr8niteout.PubDashboardScreens.Models.GetAccountDetailsModel;
import com.Gr8niteout.PubDashboardScreens.Models.SummaryModel;
import com.Gr8niteout.R;
import com.Gr8niteout.RegisterPubScreens.ValidationCases;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashboardFragment extends Fragment {

    TextView tvPublish, tvUploadAnImage, tvTitleLength, tvTellUsMoreLength, tvPubCredit;
    LinearLayout btnUpload;
    EditText edtTitle, edtTellUsMore;
    RelativeLayout selectedImageLayout;
    ImageView ivClose, ivSelectedImage;

    String imagePath = "";
    ValidationCases validation;
    EditText[] edtList;

    List<String> items;
    private AlertDialog dialogPic;
    private ArrayList<Image> images = new ArrayList<>();

    private final int CAMERA_REQUEST_CODE = 100;
    private final int GALLERY_REQUEST_CODE = 2000;
    private final String IMAGE_DIRECTORY_NAME = "Gr8niteout";
    String timeStamp;

    AddNewNoticeModel addNewNoticeModel;
    String[] titleItems = new String[]{"Mr.", "Miss", "Mrs."};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvPublish = view.findViewById(R.id.tvPublish);
        tvUploadAnImage = view.findViewById(R.id.tvUploadAnImage);
        tvTitleLength = view.findViewById(R.id.tvTitleLength);
        tvTellUsMoreLength = view.findViewById(R.id.tvTellUsMoreLength);
        btnUpload = view.findViewById(R.id.btnUpload);
        edtTitle = view.findViewById(R.id.edtTitle);
        edtTellUsMore = view.findViewById(R.id.edtTellUsMore);
        selectedImageLayout = view.findViewById(R.id.selectedImageLayout);
        ivClose = view.findViewById(R.id.ivClose);
        ivSelectedImage = view.findViewById(R.id.ivSelectedImage);
        tvPubCredit = view.findViewById(R.id.tvPubCredit);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        validation = new ValidationCases();
        edtList = new EditText[]{edtTitle, edtTellUsMore};

        validation.firstLetterSpace(edtList);

        callPubCreditApi();

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnUpload.setVisibility(View.VISIBLE);
                selectedImageLayout.setVisibility(View.GONE);
                imagePath = "";
            }
        });

        edtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if((70-edtTitle.getText().toString().length())>=0){
                    tvTitleLength.setText(70-edtTitle.getText().toString().length()+"");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtTellUsMore.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if((180-edtTellUsMore.getText().toString().length())>=0){
                    tvTellUsMoreLength.setText(180-edtTellUsMore.getText().toString().length()+"");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    String[] PERMISSIONS = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA};
                    if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
                        CommonUtilities.setPermission(getContext(), PERMISSIONS);
                    }else{
                        captureImageInitialization(1);
                    }
                }else{
                    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                    if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
                        CommonUtilities.setPermission(getContext(), PERMISSIONS);
                    } else {
                        captureImageInitialization(1);
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
//                        }
//                    }
//                }else{
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
//                        if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
//                            CommonUtilities.setPermission(getContext(), PERMISSIONS);
//                        } else {
//                            captureImageInitialization(1);
//                        }
//                    } else {
//                        captureImageInitialization(1);
//                    }
//                }
            }
        });

        tvPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check1 = false;
                boolean check2 = false;

                if(edtTitle.getText().toString().trim().equals("")){
                    check1 = true;
                    edtTitle.setError("Please enter title!");
                }else{
                    edtTitle.setError(null);
                }

                if(edtTellUsMore.getText().toString().trim().equals("")){
                    check2 = true;
                    edtTellUsMore.setError("Please enter tell us more!");
                }else{
                    edtTellUsMore.setError(null);
                }

                if(!check1 && !check2){
                    CommonUtilities.hideKeyboard(true, getActivity());
                    callAddNoticeApi();
                }

            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            images.clear();
            images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);

            File file = new File(images.get(0).getPath());

            if(file.length() > 8010000){
                CommonUtilities.ShowToast(getContext(), "Image size must be less than 8 MB.");
            }else{
                imagePath = compressImage(images.get(0).getPath());
                ivSelectedImage.setImageURI(Uri.parse(images.get(0).getPath()));
                tvUploadAnImage.setError(null);
                selectedImageLayout.setVisibility(View.VISIBLE);
                btnUpload.setVisibility(View.GONE);
            }
        }

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                images.clear();
                new SingleMediaScanner(getContext(), fileUri.getPath());
                images.add(new Image(1, timeStamp, fileUri.getPath(), false, ""));
                File file = new File(images.get(0).getPath());

                if(file.length() > 8010000){
                    CommonUtilities.ShowToast(getContext(), "Image size must be less than 8 MB.");
                } else {
                    imagePath = compressImage(images.get(0).getPath());
                    ivSelectedImage.setImageURI(Uri.parse(images.get(0).getPath()));
                    tvUploadAnImage.setError(null);
                    selectedImageLayout.setVisibility(View.VISIBLE);
                    btnUpload.setVisibility(View.GONE);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.select_dialog_item, items);
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

    public void callAddNoticeApi() {
        SharedPreferences preferences = getContext().getSharedPreferences("pub_details",MODE_PRIVATE);

        Map<String, String> paramsTemp = new HashMap<>();
        paramsTemp.put("pub_id", preferences.getString("pub_id",""));
        paramsTemp.put("title", edtTitle.getText().toString().trim());
        paramsTemp.put("description", edtTellUsMore.getText().toString().trim());
        paramsTemp.put("notices_image", imagePath.equals("") ? "" : getStringImage());

        ServerAccess.getResponse(getContext(), CommonUtilities.key_add_notice, paramsTemp, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                addNewNoticeModel = new AddNewNoticeModel().AddNewNoticeModel(result);
                if (addNewNoticeModel != null) {
                    if(addNewNoticeModel.response.code.equals(CommonUtilities.key_success_code)){
                        CommonUtilities.ShowToast(getContext(), addNewNoticeModel.response.data.success);
                        ((DashboardHomeActivity) getContext()).selectItem(1);
                    }else{
                        CommonUtilities.ShowToast(getContext(), addNewNoticeModel.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(getContext(), "Something went wrong!");
            }
        });
    }

    public void callPubCreditApi() {
        SharedPreferences preferences = getContext().getSharedPreferences("pub_details",MODE_PRIVATE);
        Map<String, String> paramsTemp = new HashMap<>();

        String url = CommonUtilities.key_summary + "&pub_id=" + preferences.getString("pub_id","");

        ServerAccess.getResponse(getContext(), url, paramsTemp, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                SummaryModel summaryModel = new SummaryModel().SummaryModel(result);
                if (summaryModel != null) {
                    if(summaryModel.response.code.equals(CommonUtilities.key_success_code)){
                        tvPubCredit.setText(summaryModel.response.data.pub_credits_transactions);
                        callGetProfileApi();
                    }else{
                        CommonUtilities.ShowToast(getContext(), summaryModel.response.msg);
                    }
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(getContext(), "Something went wrong!");
            }
        });
    }

    public void callGetProfileApi() {
        Map<String, String> paramsTemp = new HashMap<>();

        SharedPreferences preferences = getContext().getSharedPreferences("pub_details", MODE_PRIVATE);
        String method = CommonUtilities.key_get_account_details + "&owner_id=" + preferences.getString("owner_id", "");

        ServerAccess.getResponse(getContext(), method, paramsTemp, true, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                GetAccountDetailsModel accountDetailsModel = new GetAccountDetailsModel().GetAccountDetailsModel(result);
                if (accountDetailsModel.response.code.equals(CommonUtilities.key_success_code)) {

                    String title;

                    if (accountDetailsModel.response.data.title.equals("0")) {
                        title = titleItems[0];
                    } else if (accountDetailsModel.response.data.title.equals("1")) {
                        title = titleItems[1];
                    } else {
                        title = titleItems[2];
                    }

                    String fullName = title + " "+
                            accountDetailsModel.response.data.first_name + " "+
                            accountDetailsModel.response.data.last_name;

                    ((DashboardHomeActivity) getContext()).updateDrawerProfile(
                            accountDetailsModel.response.data.profile_pic,
                            fullName,
                            accountDetailsModel.response.data.username,
                            0
                    );

                } else {
                    CommonUtilities.ShowToast(getContext(), accountDetailsModel.response.msg);
                }
            }

            @Override
            public void onError(String error) {
                CommonUtilities.ShowToast(getContext(), "Something went wrong!");
            }
        });
    }

    public String getStringImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
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