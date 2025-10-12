package com.Gr8niteout.pub;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.config.ServerAccess;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;


public class  ImageUploadService extends IntentService {

    private int waitingIntentCount = 0;
    String userid="";
    public ImageUploadService() {
        super("ImageUploadService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {

        waitingIntentCount--;
        Log.i("","waitingIntentCount "+waitingIntentCount);

        if (intent != null && intent.hasExtra("Data")) {
            Image ImageModel = (Image) intent.getParcelableExtra("Data");
            uploadImage(ImageModel,waitingIntentCount);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        waitingIntentCount++;
        return super.onStartCommand(intent, flags, startId);
    }

    private void uploadImage(Image model,final int waitingIntentCount) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pub_id", CommonUtilities.getPreference(getApplicationContext(),CommonUtilities.pref_pub_id));
        params.put("user_id",CommonUtilities.getPreference(getApplicationContext(),CommonUtilities.pref_UserId));
        params.put("image_string", getStringImage(model.getPath()));
        params.put("caption", model.getMessage());
        ServerAccess.getResponse(getApplicationContext(), CommonUtilities.key_upload_photos, params, false, new ServerAccess.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.i("","image response "+result);
                Log.i("","image response waitingIntentCount "+waitingIntentCount);
            }

            @Override
            public void onError(String error) {
                Log.i("","image error "+error);
            }
        });
    }

    public String getStringImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}
