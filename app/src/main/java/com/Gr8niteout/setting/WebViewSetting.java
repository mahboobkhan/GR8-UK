package com.Gr8niteout.setting;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.Dialog;

import static com.Gr8niteout.R.id.webview;

public class WebViewSetting extends AppCompatActivity {

    String getUrl,getTitle;
    Dialog progressBar;
    Toolbar toolbar;
    TextView tool_text;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);
        WebView webView = (WebView) findViewById(webview);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = new Dialog(WebViewSetting.this);
        tool_text = (TextView) findViewById(R.id.tool_text);
//        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
//        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
//        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        if(intent!= null) {
            getUrl = intent.getStringExtra("ViewUrl");
            getTitle = intent.getStringExtra("title");
            getSupportActionBar().setTitle(null);
            tool_text.setText(getTitle);
            CommonUtilities.setFontFamily(WebViewSetting.this,tool_text,CommonUtilities.AvenirLTStd_Medium);
        }

        progressBar.show();
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }
        });

        webView.loadUrl(getUrl);
//        WebSettings settings = webview.getSettings();
//        settings.setJavaScriptEnabled(true);
//        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//
//        webView.loadDataWithBaseURL("x-data://base", getUrl, "text/html", "UTF-8", null);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
