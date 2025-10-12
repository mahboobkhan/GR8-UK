package com.Gr8niteout;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.Gr8niteout.config.CommonUtilities;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    Context ctx;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        ctx = this;
        if (remoteMessage.getData().size() > 0) {

        }

        if (remoteMessage.getNotification() != null) {

        }

        if(CommonUtilities.getSecurityBooleanPreference(getApplicationContext(),CommonUtilities.pref_Notification_Access)){

            Log.d(TAG, "Message Notification Body: " + remoteMessage.getData());

            sendNotification(remoteMessage.getData().get("message"), remoteMessage.getData().get("not_type"));

        }else{

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody, String not_type) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (not_type.equals("1")) {
            intent.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_birtday);
            CommonUtilities.setPreference(this,CommonUtilities.pref_from_birthday,"true");
        }
        else if (not_type.equals("2")){
            intent.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_received_credit);
            CommonUtilities.setPreference(this,CommonUtilities.pref_from_birthday,"false");
        }
        else{
            intent.putExtra(CommonUtilities.key_flag, CommonUtilities.flag_drinks);
            CommonUtilities.setPreference(this,CommonUtilities.pref_from_birthday,"false");
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Bitmap largeIcon1 = BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getResources().getString(R.string.default_channel_id))
                .setSmallIcon(R.mipmap.g_icon)
                .setLargeIcon(largeIcon1)
                .setContentTitle(getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.default_channel_id), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }
}