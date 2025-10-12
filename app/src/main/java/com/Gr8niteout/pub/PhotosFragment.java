package com.Gr8niteout.pub;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;

import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Gr8niteout.MainActivity;
import com.Gr8niteout.R;
import com.Gr8niteout.config.CircleTransform;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.MyApplication;
import com.Gr8niteout.config.RoundImageview;
import com.Gr8niteout.config.ServerAccess;
import com.Gr8niteout.home.HomeFragment;
import com.Gr8niteout.model.Pub_photo_model;
import com.Gr8niteout.model.SignUpModel;
import com.Gr8niteout.signup.SignupLogin;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.resource.drawable.GlideDrawable;
//import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.target.Target;
import com.google.android.gms.analytics.HitBuilders;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.Gr8niteout.R.id.First_Lett;
import static com.facebook.FacebookSdk.getApplicationContext;

public class PhotosFragment extends Fragment {

    PubActivity mActivity;
    String pub_id = "";
    Pub_photo_model model;
    Pub_photo_model model_pic;
    SwipeRefreshLayout swipeRefreshLayout;
    int pageCount = 1, totalCount, threshold = 0;
    ArrayList<Pub_photo_model.response.photos_list.photos_lists> list = new ArrayList<>();
    ListView photoList;
    TextView no_text;
    FrameLayout listviewLayout;
    View footer;
    PubPhotoAdapter adapter;
    String userid = "";
    ProgressBar progressBar;
    SignUpModel modelSignup;
    ImageView btnUpload;
    private int REQUEST_CODE_PICKER = 2000;
    private ArrayList<Image> images = new ArrayList<>();
    List<String> items;
    private AlertDialog dialogPic;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final String IMAGE_DIRECTORY_NAME = "Gr8niteout";
    private Uri fileUri; // file url to store image/video
    String timeStamp;
    private boolean m_iAmVisible = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        modelSignup = new SignUpModel().SignUpModel(CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserData));
        listviewLayout = (FrameLayout) view.findViewById(R.id.listview_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        btnUpload = (ImageView) view.findViewById(R.id.btn_upload);
        photoList = (ListView) view.findViewById(R.id.photoList);
        no_text = (TextView) view.findViewById(R.id.comment_txt);
        footer = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.progressbar, null, false);
        progressBar = (ProgressBar) footer.findViewById(R.id.progressBar);
        photoList.addFooterView(footer);
        progressBar.setVisibility(View.INVISIBLE);
        photoList.setDivider(null);
        photoList.setDividerHeight(0);
        adapter = new PubPhotoAdapter();
        setHasOptionsMenu(true);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        userid = CommonUtilities.getPreference(mActivity, CommonUtilities.pref_UserId);
        CommonUtilities.setFontFamily(mActivity, no_text, CommonUtilities.AvenirLTStd_Medium);
        if (getArguments() != null && getArguments().containsKey(CommonUtilities.key_pub_id)) {
            pub_id = getArguments().getString(CommonUtilities.key_pub_id);
            CommonUtilities.setPreference(mActivity, CommonUtilities.pref_pub_id, pub_id);
            if (m_iAmVisible)
                getPubPhoto(pageCount, true);

        }

        m_iAmVisible = true;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageCount = 1;
                list.clear();
                swipeRefreshLayout.setRefreshing(true);
                getPubPhoto(pageCount, false);

                photoList.setEnabled(false);

            }
        });

//        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
//        CommonUtilities.setPermission(mActivity,PERMISSIONS);

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
//                        if(Build.VERSION.SDK_INT >= 30) {
//                            if (!Environment.isExternalStorageManager()){
//                                Intent intent = new Intent();
//                                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                                Uri uri = Uri.fromParts("package",getContext().getPackageName(), null);
//                                intent.setData(uri);
//                                startActivity(intent);
//                            }else{
//                                String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
//                                if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
//                                    CommonUtilities.setPermission(getContext(), PERMISSIONS);
//                                } else {
//                                    captureImageInitialization();
//                                }
//                            }
//                        }else{
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
//                                if (!CommonUtilities.hasPermissions(getContext(), PERMISSIONS)) {
//                                    CommonUtilities.setPermission(getContext(), PERMISSIONS);
//                                } else {
//                                    captureImageInitialization();
//                                }
//                            } else {
//                                captureImageInitialization();
//                            }
//                        }
//                    }
                }
            }

        });

        photoList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (photoList.getLastVisiblePosition() >= photoList.getCount() - 1 - threshold) {
                    if (totalCount >= pageCount && totalCount != 1) {
                        pageCount++;
                        progressBar.setVisibility(View.VISIBLE);
                        getPubPhoto(pageCount, false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

//        if(!modelSignup.response.user_data.user_id.equals("")) {
//            userid = modelSignup.response.user_data.user_id;
//        }

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            captureImageInitialization();
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                CommonUtilities.alertdialog(mActivity, "Allow External Storage permission manually");
            } else if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                CommonUtilities.alertdialog(mActivity, "Allow Camera permission manually");
            }
        }
    }

    public void getPubPhoto(final int pageCountt, boolean loader) {
//        Toast.makeText(mActivity, "get pub photo", Toast.LENGTH_SHORT).show();

        Map<String, String> params = new HashMap<String, String>();
        params.put(CommonUtilities.key_pub_id, pub_id);
        params.put(CommonUtilities.key_page_no, String.valueOf(pageCountt));

        Log.d("getPhotos","pagecount : "+pageCountt+"");
        Log.d("getPhotos","pub id: "+pub_id+"");
        Log.d("getPhotos","pub id: "+CommonUtilities.key_get_photos+"");

        ServerAccess.getResponse(mActivity, CommonUtilities.key_get_photos, params, loader, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                model_pic = new Pub_photo_model().Pub_photo_model(result);
                if (model_pic != null) {
                    if (model_pic.response.status.equals(CommonUtilities.key_Success)) {
                        if (model_pic.response != null) {

                            if (pageCountt == 1) {
                                Double count = Math.ceil(Double.parseDouble(model_pic.response.photos_list.count) / Double.parseDouble(String.valueOf(model_pic.response.photos_list.limit)));
                                totalCount = count.intValue();
                                model = model_pic;
                                list.addAll(model.response.photos_list.getPhotos_lists());
                                photoList.setAdapter(adapter);
                                swipeRefreshLayout.setRefreshing(false);
                                photoList.setEnabled(true);

                            } else {
                                progressBar.setVisibility(View.GONE);
                                list.addAll(model_pic.response.photos_list.getPhotos_lists());
                                model.response.photos_list.setPhotos_lists(list);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        if (pageCountt == 1) {
                            photoList.setAdapter(null);
                            swipeRefreshLayout.setRefreshing(false);
                            listviewLayout.setVisibility(View.GONE);
                            no_text.setText(model_pic.response.msg);
                            no_text.setVisibility(View.VISIBLE);
                        } else {
                            no_text.setVisibility(View.GONE);
                            photoList.removeFooterView(footer);
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (PubActivity) activity;

    }

    public class PubPhotoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return model.response.photos_list.getPhotos_lists().size();
        }

        @Override
        public Object getItem(int position) {
            return model.response.photos_list.getPhotos_lists().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            ViewHolder holder;
            //if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mActivity).inflate(R.layout.row_pub_photo_adapter, null);
            holder.userImage = (RoundImageview) view.findViewById(R.id.user_image);
            holder.userName = (TextView) view.findViewById(R.id.user_name);
            holder.daysAgo = (TextView) view.findViewById(R.id.days_ago);
            holder.pubimageView = (ImageView) view.findViewById(R.id.pubimageView);
            holder.imgPlaceHolder = (ImageView) view.findViewById(R.id.imgPlaceHolder);
            holder.txtDescription = (TextView) view.findViewById(R.id.txtDescription);
            holder.line = (LinearLayout) view.findViewById(R.id.line);
            holder.image_layout = (RelativeLayout) view.findViewById(R.id.image_layout);
            holder.First_Lett = (TextView) view.findViewById(First_Lett);

            holder.shareImg = (ImageView) view.findViewById(R.id.share_img);
            holder.viewComments = (TextView) view.findViewById(R.id.view_comments);

            view.setTag(holder);
           /* } else {
                holder = (ViewHolder) view.getTag();
            }*/
            holder.daysAgo.setText(model.response.photos_list.getPhotos_lists().get(position).days_ago);
            holder.userName.setText(model.response.photos_list.getPhotos_lists().get(position).user_name);

            holder.First_Lett.setVisibility(View.GONE);
            holder.userImage.setImageResource(0);
            if (model.response.photos_list.getPhotos_lists().get(position).photo_share_url.equals("")) {
                holder.image_layout.setBackgroundResource(R.drawable.round);
                if (!model.response.photos_list.getPhotos_lists().get(position).user_name.equals("")) {
                    holder.First_Lett.setVisibility(View.VISIBLE);
                    holder.First_Lett.setText(model.response.photos_list.getPhotos_lists().get(position).user_name.substring(0, 1));
                } else {
                    holder.First_Lett.setVisibility(View.GONE);
                    holder.userName.setText("Unknown");
                    holder.userImage.setBackgroundResource(R.mipmap.user);
                }
            } else {
                if (model.response.photos_list.getPhotos_lists().get(position).user_name.equals("")) {
                    holder.First_Lett.setVisibility(View.GONE);
                    holder.userName.setText("Unknown");
                }
                holder.First_Lett.setVisibility(View.GONE);

                Log.d("PicassoImage", "getView:--- "+ model.response.photos_list.getPhotos_lists().get(position).photo_share_url);

//                changed on 28-jan-2019
                Picasso.get()
                        .load(CommonUtilities.Gr8niteoutURL + CommonUtilities.User_Profile_URL +
                                model.response.photos_list.getPhotos_lists().get(position).user_image)
                        .placeholder(R.mipmap.user)
                        .transform(new CircleTransform())
                        .into(holder.userImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
            }


            if (model.response.photos_list.getPhotos_lists().get(position).description.equals("")) {
                holder.line.setVisibility(View.GONE);
                holder.txtDescription.setVisibility(View.GONE);
            } else {
                holder.txtDescription.setText(model.response.photos_list.getPhotos_lists().get(position).description);
                holder.line.setVisibility(View.VISIBLE);
                holder.txtDescription.setVisibility(View.VISIBLE);
            }

            holder.shareImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent share = new Intent();
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, "Gr8niteout - " + getArguments().getString(CommonUtilities.key_pub_name) + " " +
                            model.response.photos_list.getPhotos_lists().get(position).photo_share_url);
                    share.setAction(Intent.ACTION_SEND);
                    startActivity(Intent.createChooser(share, "Share link!"));
                }
            });

            holder.viewComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mActivity, CommentActivity.class);
                    i.putExtra("pub_id", pub_id);
                    i.putExtra("img_id", model.response.photos_list.getPhotos_lists().get(position).photo_id);
                    startActivity(i);
                }
            });

            final ViewHolder finalHolder1 = holder;
            DisplayMetrics displayMetrics = new DisplayMetrics();
            mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;

//                changed on 28-jan-2019
            Picasso.get()
                    .load(CommonUtilities.Gr8niteoutURL + CommonUtilities.Pub_photos_URL +
                            model.response.photos_list.getPhotos_lists().get(position).photo)
//                    .resize(width, 211)
                    .into(holder.pubimageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            finalHolder1.imgPlaceHolder.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            finalHolder1.imgPlaceHolder.setVisibility(View.VISIBLE);
                        }
                    });


            CommonUtilities.setFontFamily(mActivity, holder.userName, CommonUtilities.AvenirLTStd_Medium);
            CommonUtilities.setFontFamily(mActivity, holder.daysAgo, CommonUtilities.AvenirLTStd_Medium);
            CommonUtilities.setFontFamily(mActivity, holder.txtDescription, CommonUtilities.AvenirLTStd_Medium);
            CommonUtilities.setFontFamily(mActivity, holder.viewComments, CommonUtilities.AvenirLTStd_Medium);

            return view;
        }

        public class ViewHolder {

            RoundImageview userImage;
            TextView userName;
            TextView daysAgo;
            ImageView pubimageView;
            ImageView imgPlaceHolder;
            TextView txtDescription;
            ImageView shareImg;
            LinearLayout line;
            RelativeLayout image_layout;
            TextView viewComments;
            TextView First_Lett;
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
    public void onStart() {
        super.onStart();

        ((MyApplication) mActivity.getApplication()).getDefaultTracker().setScreenName("Pub Photos Screen");
        ((MyApplication) mActivity.getApplication()).getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }
}
