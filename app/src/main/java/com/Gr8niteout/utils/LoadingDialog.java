package com.Gr8niteout.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.Gr8niteout.R;

public class LoadingDialog {
    
    private Dialog dialog;
    private Context context;
    private TextView messageTextView;
    
    public LoadingDialog(Context context) {
        this.context = context;
        createDialog();
    }
    
    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_loading, null);
        
        messageTextView = dialogView.findViewById(R.id.tvLoadingMessage);
        
        builder.setView(dialogView);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        
        // Make dialog background transparent
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
    
    public void show(String message) {
        if (messageTextView != null) {
            messageTextView.setText(message);
        }
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }
    
    public void show() {
        show("Please wait...");
    }
    
    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
    
    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }
    
    public void updateMessage(String message) {
        if (messageTextView != null) {
            messageTextView.setText(message);
        }
    }
}
