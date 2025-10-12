package com.Gr8niteout.pub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.RoundedTransformation;
import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.resource.drawable.GlideDrawable;
//import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.target.Target;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.BindView;

public class CaptionActivity extends AppCompatActivity {

    Image ImageModel;
//    @BindView(R.id.captiontext)
    EditText captiontext;

//    @BindView(R.id.galleryImage)
    ImageView galleryImage;
//@BindView(R.id.galleryImagePlaceHolder)
    ImageView galleryImagePlaceHolder;


//    @BindView(R.id.toolbar)
    Toolbar toolbar;
//    @BindView(R.id.tool_text)
    TextView tool_text;
    CaptionActivity act;
    private MenuItem menuDone;
    private final int menuDoneId = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = CaptionActivity.this;
        setContentView(R.layout.activity_caption);
        ButterKnife.bind(this);

        captiontext = (EditText) findViewById(R.id.captiontext);
        galleryImage = (ImageView) findViewById(R.id.galleryImage);
        galleryImagePlaceHolder = (ImageView) findViewById(R.id.galleryImagePlaceHolder);
        toolbar = (Toolbar) findViewById(R.id.toolbar);


        captiontext.setCursorVisible(true);
        CommonUtilities.showSoftKeyboard(act,captiontext);
        setSupportActionBar(toolbar);
        setTitle(null);
        toolbar.setContentInsetStartWithNavigation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CommonUtilities.setFontFamily(act,tool_text,CommonUtilities.AvenirLTStd_Medium);


        if (getIntent() != null && getIntent().hasExtra("Data")) {
            ImageModel = (Image) getIntent().getParcelableExtra("Data");
            Uri uri = Uri.fromFile(new File(ImageModel.getPath()));
            captiontext.setText(ImageModel.getMessage());
            Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
            int screenWidth = display.getWidth();

//            Glide.with(act)
//                    .load(uri) // Uri of the picture
//                    .override(screenWidth, screenWidth-200)
//                    .bitmapTransform(new RoundedTransformation(act,15,0, RoundedTransformation.CornerType.TOP))
//                    .listener(new RequestListener<Uri, GlideDrawable>() {
//                        @Override
//                        public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                            galleryImagePlaceHolder.setVisibility(View.GONE);
//                            return false;
//                        }
//                    })
//                    .into(galleryImage);


            CommonUtilities.setFontFamily(act,captiontext,CommonUtilities.Avenir);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuDone = menu.add(Menu.NONE, menuDoneId, 1, "Done").setIcon(R.mipmap.done);
        menuDone.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case menuDoneId:
                ImageModel.setMessage(captiontext.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("ImageModel", ImageModel);
                setResult(2, intent);
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        finish();
        super.onBackPressed();
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
}

