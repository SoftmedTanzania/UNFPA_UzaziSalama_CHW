package com.softmed.uzazi_salama.Service;

/**
 * Created by kency on 12/11/17.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import com.softmed.uzazi_salama.Application.BoreshaAfyaApplication;
import com.softmed.uzazi_salama.BackgroundUpdateService;
import org.ei.opensrp.view.activity.LoginActivity;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    public MyGcmListenerService() {
    }


    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = "successful referral feedback";
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "data bundle: " + data.toString());
        Log.d(TAG, "data size: " + data.size());

        String client_id= data.getString("referralUUID");
        String feedback= data.getString("otherNotes");
        String serviceGivenToPatient= data.getString("serviceGivenToPatient");
        boolean testResult= Boolean.parseBoolean(data.getString("testResults"));
        String referralStatus= data.getString("referralStatus");
        ((BoreshaAfyaApplication)getApplication()).updateReferralStatus(client_id,feedback,serviceGivenToPatient,testResult,referralStatus);



         if (from.startsWith("/chw_feedback/")){

            startService(new Intent(this, BackgroundUpdateService.class)
                    .putExtra("type", "follow_up")
                    .putExtra("client_id", data.getString("client_id"))
                    .putExtra("follow_up_reason", data.getString("follow_up_reason")));



        }else if (from.startsWith("/facility/")){
            //todo martha how to process a list of a feedback
            startService(new Intent(this, BackgroundUpdateService.class)
                    .putExtra("type", "facility"));
        }else if (from.startsWith("/facility_update/")){
            //todo martha how to process a list of a feedback
            startService(new Intent(this, BackgroundUpdateService.class)
                    .putExtra("type", "facility_delete"));

        }else if (from.startsWith("/facility_delete/")){
            //todo martha how to process a list of a feedback
            startService(new Intent(this, BackgroundUpdateService.class)
                    .putExtra("type", "facility_update"));
        }else if (from.startsWith("/referral_service_update/")){
            //todo martha how to process a list of a feedback
            startService(new Intent(this, BackgroundUpdateService.class)
                    .putExtra("type", "referral_service_delete"));

        }else if (from.startsWith("/referral_service_delete/")){
            //todo martha how to process a list of a feedback
            startService(new Intent(this, BackgroundUpdateService.class)
                    .putExtra("type", "referral_service_update"));
        }else{
            // normal downstream message.
        }

        sendNotification(message);
    }

    /**
     * Method called on Receiving a new message from GCM server
     * */
    @Override
    public void onMessageSent(String s) {


    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    public void onDeletedMessages() {


        Log.i(TAG, "Received deleted messages notification");
//        String message = getString(R.string.gcm_deleted, total);
//        aController.displayMessageOnScreen(context, message);
//        // notifies user
//        sendNotification( message);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onSendError(String s1, String errorId) {


    }
    public void handleIntent(Intent intent) { /* compiled code */ }


    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(org.ei.opensrp.R.drawable.ic_htmr)
                .setContentTitle("Uzazi Salama Notification")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}