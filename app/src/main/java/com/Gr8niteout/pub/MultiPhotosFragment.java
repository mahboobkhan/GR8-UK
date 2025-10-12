package com.Gr8niteout.pub;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.Gr8niteout.MainActivity;
import com.Gr8niteout.R;
import com.Gr8niteout.RegisterPubScreens.PersonalInformationActivity1;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.home.HomeFragment;
import com.Gr8niteout.model.Pub_photo_model;
import com.Gr8niteout.signup.SignupLogin;
import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.resource.drawable.GlideDrawable;
//import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MultiPhotosFragment extends Fragment {

    String pub_id = "", shareKey = "";
    String userid = "";
    private Uri fileUri;
    private AlertDialog dialogPic;
    int totalCount, pageCount = 1, threshold = 0;
    PubActivity mActivity;
    Pub_photo_model model_pic;
    List<String> items;
    Pub_photo_model model;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    View footer;
    ImageView btnUpload;
    ArrayList<Pub_photo_model.response.photos_list.photos_lists> list = new ArrayList<>();
    private ArrayList<Image> images = new ArrayList<>();
    private boolean m_iAmVisible = false;
    String timeStamp;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private int REQUEST_CODE_PICKER = 2000;
    private static final String IMAGE_DIRECTORY_NAME = "Gr8niteout";
    MultiPhotosRecyclerAdapter adapter;
    String responseStatus = "";
    boolean isApiCalled = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multi_photos, container, false);

        recyclerView = view.findViewById(R.id.photosRecyclerView);
        btnUpload = view.findViewById(R.id.btn_upload);
        footer = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.progressbar, null, false);
        progressBar = (ProgressBar) footer.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        adapter = new MultiPhotosRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        userid = CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId);

        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));

        shareKey = getArguments().getString(CommonUtilities.key_pub_name);

        if (getArguments() != null && getArguments().containsKey(CommonUtilities.key_pub_id)) {
            pub_id = getArguments().getString(CommonUtilities.key_pub_id);
            CommonUtilities.setPreference(mActivity, CommonUtilities.pref_pub_id, pub_id);
//            if (m_iAmVisible)
//                getPubPhoto(pageCount, true);
        }

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!responseStatus.equals("Error")) {
                    if (getLastVisiblePosition(recyclerView) >= (list.size() - 3)) {
                        progressBar.setVisibility(View.VISIBLE);
                        getPubPhoto(pageCount, true);
                    }
                }
//                if (getLastVisiblePosition(recyclerView) >= recyclerView.getChildCount() - 1 - threshold) {
//                    if (totalCount >= pageCount && totalCount != 2) {
//                        Toast.makeText(mActivity, "on scroll changed", Toast.LENGTH_SHORT).show();
//                        pageCount++;
//                        progressBar.setVisibility(View.VISIBLE);
//                        getPubPhoto(pageCount, false);
//                    }
//                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        m_iAmVisible = true;

//        getPubPhoto(pageCount, true);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userid.equals("")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mActivity);
                    builder1.setTitle(R.string.app_name);
                    builder1.setIcon(R.mipmap.app_icon);
                    builder1.setMessage("Please login to upload a photo");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Intent i = new Intent(mActivity, SignupLogin.class);
                            i.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_comment);
                            startActivityForResult(i, 111);
                        }
                    });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        String[] PERMISSIONS = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA};
                        if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
                            CommonUtilities.setPermission(getContext(), PERMISSIONS);
                        }else{
                            captureImageInitialization();
                        }
                    }else{
                        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                        if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
                            CommonUtilities.setPermission(getContext(), PERMISSIONS);
                        } else {
                            captureImageInitialization();
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
//                                captureImageInitialization();
//                            }
//                        }
//                    }else{
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
//                            if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
//                                CommonUtilities.setPermission(getContext(), PERMISSIONS);
//                            } else {
//                                captureImageInitialization();
//                            }
//                        } else {
//                            captureImageInitialization();
//                        }
//                    }
                }
            }

        });

        return view;
    }

    public static int getLastVisiblePosition(RecyclerView rv) {
        if (rv != null) {
            final RecyclerView.LayoutManager layoutManager = rv
                    .getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) layoutManager)
                        .findLastVisibleItemPosition();
            }
        }
        return 0;
    }

    public void getPubPhoto(final int pageCountt, boolean loader) {
        if (!isApiCalled) {
            isApiCalled = true;
            Map<String, String> params = new HashMap<String, String>();
            params.put(CommonUtilities.key_pub_id, pub_id);
            params.put(CommonUtilities.key_page_no, String.valueOf(pageCountt));

            ServerAccess.getResponse(mActivity, CommonUtilities.key_get_photos, params, loader, new ServerAccess.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    model_pic = new Pub_photo_model().Pub_photo_model(result);
                    if (model_pic.response.status.equals("Error")) {
                        responseStatus = model_pic.response.status;
                    }
                    if (model_pic != null) {
                        if (model_pic.response.status.equals(CommonUtilities.key_Success)) {
                            if (model_pic.response != null) {
//                            adapter = new MultiPhotosRecyclerAdapter(mActivity,model_pic.response.photos_list.photos_lists,shareKey,pub_id);
                                if (pageCountt == 1) {
//                                Double count = Math.ceil(Double.parseDouble(model_pic.response.photos_list.count) / Double.parseDouble(String.valueOf(model_pic.response.photos_list.limit)));
                                    totalCount = Integer.parseInt(model_pic.response.photos_list.count);
                                    model = model_pic;
                                    list.addAll(model.response.photos_list.getPhotos_lists());
                                    recyclerView.setAdapter(adapter);
                                    pageCount++;

                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    list.addAll(model_pic.response.photos_list.getPhotos_lists());
                                    model.response.photos_list.setPhotos_lists(list);
                                    adapter.notifyDataSetChanged();
                                    pageCount++;
                                }
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                    isApiCalled = false;
                }

                @Override
                public void onError(String error) {
                    isApiCalled = false;
                }
            });
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (PubActivity) activity;

    }

    @Override
    public void onResume() {
        super.onResume();
        userid = CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId);
    }

    private void captureImageInitialization() {

        items = new ArrayList<String>();
        items.add(getResources().getString(R.string.take_camera));
        items.add(getResources().getString(R.string.select_photo));

        items.add(getResources().getString(R.string.cancel_dialog));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int index) { // pick
                // from
                dialog.dismiss(); // camera
                if (items.get(index).equals(
                        getResources().getString(R.string.take_camera))) {

                    captureImage();


                } else if (items.get(index).equals(
                        getResources().getString(R.string.select_photo))) {

                    getImages();


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

    private void getImages() {
        ImagePicker.create(this)
                .folderMode(true)
                .folderTitle("Select photos to upload")
                .imageTitle("Tap to select")
                .single()
                .multi()
                .limit(10)
                .showCamera(false)
                .imageDirectory("Camera")
                .origin(images)
                .start(REQUEST_CODE_PICKER);
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);

            Intent i = new Intent(getActivity(), UploadPhotoesActivity.class);
            i.putExtra("ImageList", images);
            startActivity(i);
            images.clear();
        }
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                new SingleMediaScanner(getActivity(), fileUri.getPath());
                images.add(new Image(1, timeStamp, fileUri.getPath(), true, ""));
                Intent i = new Intent(getActivity(), UploadPhotoesActivity.class);
                i.putExtra("ImageList", images);
                startActivity(i);
                images.clear();
            } else if (resultCode == RESULT_CANCELED) {
            } else {
                Toast.makeText(getApplicationContext(),
                                "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        if (requestCode == 111 && resultCode == RESULT_OK) {
            if (getArguments() != null && getArguments().containsKey(CommonUtilities.key_flag)) {
                MainActivity.act.setDrawerItem();
                MainActivity.act.mDrawerList.setItemChecked(1, true);
            } else {
                MainActivity.act.setDrawerItem();
//            MainActivity.act.adapter.notifyDataSetChanged();
                MainActivity.act.mDrawerList.setItemChecked(0, true);
                userid = CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId);
                FragmentManager fm = ((FragmentActivity) MainActivity.act).getSupportFragmentManager();
                HomeFragment currentFragment = (HomeFragment) fm.findFragmentByTag("Home");
                currentFragment.setDetails();
            }

        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            if (m_iAmVisible)
                getPubPhoto(pageCount, true);
        } else {

        }
    }


    public class MultiPhotosRecyclerAdapter extends RecyclerView.Adapter<MultiPhotosRecyclerAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MultiPhotosRecyclerAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.multi_photos_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MultiPhotosRecyclerAdapter.ViewHolder holder, int position) {

            Glide.with(mActivity)
                    .load(CommonUtilities.Gr8niteoutURL + CommonUtilities.Pub_photos_URL + list.get(position).photo)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // Show placeholder when image fails to load
                            holder.imgPlaceHolder.setVisibility(View.VISIBLE);
                            return false; // Return false to let Glide handle the error placeholder
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            // Hide placeholder when image is successfully loaded
                            holder.imgPlaceHolder.setVisibility(View.GONE);
                            return false; // Return false to let Glide display the image
                        }
                    })
                    .into(holder.pubImageView);

//            Glide.with(mActivity).load(CommonUtilities.Gr8niteoutURL + CommonUtilities.Pub_photos_URL +
//                    list.get(position).photo).listener(new RequestListener<String, GlideDrawable>() {
//                @Override
//                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                    holder.imgPlaceHolder.setVisibility(View.VISIBLE);
//                    return false;
//                }
//
//                @Override
//                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                    holder.imgPlaceHolder.setVisibility(View.GONE);
//                    return false;
//                }
//            }).into(holder.pubImageView);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("user_image------>>>", "onClick: "+list.get(holder.getAbsoluteAdapterPosition()).photo_share_url);
                    Log.d("user_name------>>>", "onClick: "+list.get(holder.getAbsoluteAdapterPosition()).user_name);

                    Intent intent = new Intent(mActivity, PhotoExtendActivity.class);
                    intent.putExtra("days_ago", list.get(holder.getAbsoluteAdapterPosition()).days_ago);
                    intent.putExtra("user_image", list.get(holder.getAbsoluteAdapterPosition()).user_image);
                    intent.putExtra("user_name", list.get(holder.getAbsoluteAdapterPosition()).user_name);
                    intent.putExtra("description", list.get(holder.getAbsoluteAdapterPosition()).description);
                    intent.putExtra("photo_share_url", list.get(holder.getAbsoluteAdapterPosition()).photo_share_url);
                    intent.putExtra("photo_id", list.get(holder.getAbsoluteAdapterPosition()).photo_id);
                    intent.putExtra("shareKey", shareKey);
                    intent.putExtra("pub_id", pub_id);
                    intent.putExtra("photo", CommonUtilities.Gr8niteoutURL + CommonUtilities.Pub_photos_URL + list.get(position).photo);
                    mActivity.startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView pubImageView, imgPlaceHolder;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                pubImageView = itemView.findViewById(R.id.pubImageView);
                imgPlaceHolder = itemView.findViewById(R.id.imgPlaceHolder);
            }
        }
    }


}