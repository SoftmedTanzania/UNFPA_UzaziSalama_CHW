package com.softmed.uzazi_salama;

/**
 * Created by kency on 12/11/17.
 */

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.softmed.uzazi_salama.Application.BoreshaAfyaApplication;

import java.util.Random;

public class BackgroundUpdateService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private  final int MAX_ATTEMPTS = 5;
    private  final int BACKOFF_MILLI_SECONDS = 2000;
    private  final Random random = new Random();
    public BackgroundUpdateService() {
        super(TAG);
    }
    public String id,status,referral_reason,message;

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            if(((BoreshaAfyaApplication)getApplication()).isHasFacility()) {
                Log.d(TAG,"has the list of facility already");
            }else if(intent.getBundleExtra("type").equals("facility_update")){
                message="update a facility";
                ((BoreshaAfyaApplication)getApplication()).updateFacility(message);
            }else if(intent.getBundleExtra("type").equals("facility_delete")){
                message="delete a facility";
                ((BoreshaAfyaApplication)getApplication()).deleteFacility(message);
            }  else{
                ((BoreshaAfyaApplication)getApplication()).getFacilities();
            }


            if(intent.getBundleExtra("type").equals("update")){
                message = "updated referral status";
                ((BoreshaAfyaApplication)getApplication()).updateReferralStatus(message);
            }

            if(intent.getBundleExtra("type").equals("follow_up")){
                message = "created follow up content";
                ((BoreshaAfyaApplication)getApplication()).setFollowContent(message);
            }

        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {


        // sending  userid , facility id  and token to the server

        ((BoreshaAfyaApplication)getApplication()).register(((BoreshaAfyaApplication)getApplication()).context,((BoreshaAfyaApplication)getApplication()).getCurrentUserID(),((BoreshaAfyaApplication)getApplication()).getTeam_location_id(),token);

    }



}