package com.Gr8niteout.config;

import android.content.Context;
import android.view.View;

import com.Gr8niteout.R;

import pl.droidsonroids.gif.GifImageButton;


public class Dialog extends android.app.Dialog
{
//    com.GoCarDev.model.GIFView view;
    public Dialog(Context context) {

    super(context, R.style.FullHeightDialog);
    this.setContentView(R.layout.loading_dialog);
    this.setCancelable(false);
}
}
