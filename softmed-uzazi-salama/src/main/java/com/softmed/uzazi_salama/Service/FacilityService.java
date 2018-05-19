package com.softmed.uzazi_salama.Service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.softmed.uzazi_salama.Application.BoreshaAfyaApplication;


/**
 * Created by Kency Shaka on 3/12/17.
 */
public class FacilityService extends IntentService {
    private static final String TAG = FacilityService.class.getSimpleName();

    public FacilityService() {
        super("FacilityService");
    }
    public FacilityService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG,"Facility Service on handle intent");
        ((BoreshaAfyaApplication)getApplication()).getFacilities();
    }
}
