package com.Gr8niteout.pub;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;


public class PostCommentSuccessfulAct extends Activity {

    ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_success);
        background = (ImageView) findViewById(R.id.background);
        if(getIntent().getExtras()!=null && getIntent().getExtras().containsKey(CommonUtilities.key_flag) && getIntent().getExtras().getString(CommonUtilities.key_flag).equals(CommonUtilities.flag_drinks)) {
            background.setImageResource(R.mipmap.complete);
        }
    }
    public void close(View v){
        finish();
    }
}
