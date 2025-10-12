package com.Gr8niteout.adapter;


import android.app.Activity;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.Gr8niteout.R;
import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.RoundedTransformation;
import com.Gr8niteout.pub.UploadPhotoesActivity.OnEventListener;
import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.resource.drawable.GlideDrawable;
//import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.target.Target;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.File;
import java.util.ArrayList;

public class GalleyImageAdapter extends RecyclerView.Adapter<GalleyImageAdapter.MyViewHolder> {

    ArrayList<Image> model;
    private Activity mContext;
    OnEventListener listner;

    public GalleyImageAdapter(Activity mContext, ArrayList<Image> model, OnEventListener lis) {
        this.model = model;
        this.mContext = mContext;
        this.listner = lis;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img_gallery, img_cancel,galleryImagePlaceHolder;
        TextView edt_caption;

        public MyViewHolder(View view) {
            super(view);
            img_gallery = (ImageView) view.findViewById(R.id.galleryImage);
            galleryImagePlaceHolder = (ImageView) view.findViewById(R.id.galleryImagePlaceHolder);
            img_cancel = (ImageView) view.findViewById(R.id.imageCancel);
            edt_caption = (TextView) view.findViewById(R.id.captiontext);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upload_image_item, parent, false);
        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listner.ClickEvent(view);
            }
        });
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Uri uri = Uri.fromFile(new File(model.get(position).getPath()));
        holder.edt_caption.setText(model.get(position).getMessage());
        holder.edt_caption.setEnabled(false);
        holder.img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(model.size() == 1){
                    mContext.finish();
                }else {
                    model.remove(position);
                    notifyDataSetChanged();
                }
            }
        });
//        Glide.with(mContext)
//                .load(new File(model.get(position).getPath())) // Uri of the picture
//                .bitmapTransform(new RoundedTransformation(mContext,15,0, RoundedTransformation.CornerType.TOP))
//                .listener(new RequestListener<File, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        return false;
//                    }
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        holder.galleryImagePlaceHolder.setVisibility(View.GONE);
//                        return false;
//                    }
//                })
//                .into(holder.img_gallery);
        CommonUtilities.setFontFamily(mContext,holder.edt_caption,CommonUtilities.Avenir);

    }

    @Override
    public int getItemCount() {
        return this.model.size();
    }
}
