package org.ei.opensrp.drishti.Application;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Base64;
import android.util.Pair;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.apache.http.protocol.HTTP;
import org.ei.opensrp.Context;
import org.ei.opensrp.DristhiConfiguration;
import org.ei.opensrp.commonregistry.CommonFtsObject;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.domain.Facility;
import org.ei.opensrp.domain.ReferralServiceDataModel;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.domain.ResponseStatus;
import org.ei.opensrp.drishti.LoginActivity;
import org.ei.opensrp.drishti.NativeHomeActivity;
import org.ei.opensrp.drishti.R;
import org.ei.opensrp.repository.AllSettings;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.repository.FacilityRepository;
import org.ei.opensrp.repository.ReferralServiceRepository;
import org.ei.opensrp.sync.DrishtiSyncScheduler;
import org.ei.opensrp.sync.SaveRegistrationIdInfoTask;
import org.ei.opensrp.sync.SavehasFacilityInfoTask;
import org.ei.opensrp.sync.SavehasReferralServiceInfoTask;
import org.ei.opensrp.view.BackgroundAction;
import org.ei.opensrp.view.LockingBackgroundTask;
import org.ei.opensrp.view.ProgressIndicator;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.ei.opensrp.view.activity.SecuredActivity;
import org.ei.opensrp.view.receiver.SyncBroadcastReceiver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import io.fabric.sdk.android.Fabric;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static org.ei.opensrp.AllConstants.DRISHTI_BASE_PATH;
import static org.ei.opensrp.AllConstants.GSM_SERVER_URL;
import static org.ei.opensrp.AllConstants.OPENSRP_FACILITY_URL_PATH;
import static org.ei.opensrp.AllConstants.OPENSRP_LOCATION_URL_PATH;
import static org.ei.opensrp.AllConstants.OPENSRP_REFERRAL_SERVICES_URL_PATH;
import static org.ei.opensrp.util.Log.logInfo;
import java.util.Iterator;
import java.util.Random;
import java.util.Map.Entry;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.util.Log;
/**
 * Created by koros on 1/22/16.
 */
@ReportsCrashes(
        formKey = "",
        formUri = "https://drishtiapp.cloudant.com/acra-drishtiapp/_design/acra-storage/_update/report",
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.POST,
        formUriBasicAuthLogin = "sompleakereepeavoldiftle",
        formUriBasicAuthPassword = "ecUMrMeTKf1X1ODxHqo3b43W",
        mode = ReportingInteractionMode.SILENT
)
public class BoreshaAfyaApplication extends DrishtiApplication {
    private static final String TAG = BoreshaAfyaApplication.class.getSimpleName();
    private ReferralServiceRepository serviceRepository;
    private int userType=0;//0=CHW and 1=Facility health care worker
    public  String currentUserID,team_uuid, phone_number,team_name,team_location_id,registration_id = "";
    public static String username,password;
    private  final int MAX_ATTEMPTS = 5;
    private  final int BACKOFF_MILLI_SECONDS = 2000;
    private  final Random random = new Random();
    public Context context;
    private CommonRepository commonRepository1,commonRepository;
    private boolean hasFacility = false;
    private boolean hasService = false;

    private AllSettings settings;
    private AllSharedPreferences allSharedPreferences;
    private DristhiConfiguration configuration;
    private SaveRegistrationIdInfoTask saveRegistrationIdInfoTask;
    private SavehasFacilityInfoTask savehasFacilityInfoTask;
    private SavehasReferralServiceInfoTask savehasReferralServiceInfoTask;
    private SecuredActivity securedActivity;
    public void register(final Context context, final String userId,final  String facility, final String regId) {

        Log.i(TAG, "registering device (regId = " + regId + ")");

        String serverUrl =  DRISHTI_BASE_PATH + GSM_SERVER_URL;
        Log.d(TAG,"URL to register = "+serverUrl);

        JSONObject object = new JSONObject();
        try {
            object.put("facilityUiid",facility);
            object.put("userUiid",userId);
            object.put("googlePushNotificationToken",regId);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        Response response1 = null;
        Log.d(TAG,"parameters string = "+object.toString());
        try{
            response1 = Context.getInstance().getHttpAgent().post(serverUrl,object.toString());
            Log.d(TAG,"response is failure "+response1.isFailure());
            if(response1.isFailure()){

            }else{
                context.userService().saveRegistrationInfo(regId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setReferralService() {
        commonRepository1 = context.commonrepository("referral_service");
        long count = commonRepository.count();
        if (count == 0 ) {

            //String to place our result in
            final  String myUrl =  DRISHTI_BASE_PATH + OPENSRP_REFERRAL_SERVICES_URL_PATH;
            final String result = null;

            Response<String> stringResponse  = Context.getInstance().getHttpAgent().fetchWithCredentials(myUrl,"sean", "Admin123");
            ReferralServiceDataModel service;
            Log.d(TAG,"referral service failure is "+stringResponse.isFailure());
            JSONArray jsonArray = new JSONArray();
            try {
                jsonArray = new JSONArray(stringResponse.payload());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject explrObject = null;
                try {
                    explrObject = jsonArray.getJSONObject(i);
                    Log.d(TAG,"string response "+explrObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                service = new Gson().fromJson(explrObject.toString(), ReferralServiceDataModel.class);
                if (service.getId().equals("")) {
                    Log.d(TAG, "service table is empty");

                } else {
                    Log.d(TAG, "referral services downloaded name " + service.getName());
                    Log.d(TAG, "referral services downloaded " + service.toString());
                    ContentValues values = new ReferralServiceRepository().createValuesFor(service);
                    android.util.Log.d(TAG, "values services = " + new Gson().toJson(values));
                    commonRepository1.customInsert(values);
                    context.userService().saveHasReferralServiceInfo("true");
                }
            }
            setHasService(true);
        }
    }

    public void initializeReferralService() {
        try {
            commonRepository1 = context.commonrepository("referral_service");
            long count = commonRepository.count();
            if (count > 0) {
                setHasService(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteReferralService( String value){
        ( (NativeHomeActivity)getApplicationContext()).updateFromServer();
        Log.d(TAG,"message = "+value);
    }

    public void updateReferralService(String value){

        ( (NativeHomeActivity)getApplicationContext()).updateFromServer();
        Log.d(TAG,"message = "+value);
    }

    public void deleteFacility( String value){

        ( (NativeHomeActivity)getApplicationContext()).updateFromServer();
        Log.d(TAG,"message = "+value);
    }

    public void updateFacility(String value){
        ((NativeHomeActivity)getApplicationContext()).updateFromServer();
        Log.d(TAG,"message = "+value);
    }

    public void getFacilities() {
        commonRepository = context.commonrepository("facility");

        long count = commonRepository.count();
        if (count == 0 ) {
            //String to place our result in

            final  String myUrl = DRISHTI_BASE_PATH + OPENSRP_FACILITY_URL_PATH;
            Response<String>  results = Context.getInstance().getHttpAgent().fetchWithCredentials(myUrl,"sean", "Admin123");
            Log.d(TAG,"facility failure is "+results.isFailure());
            Log.d(TAG,"this is the result of facility"+results.payload());

            try {
                JSONArray jsonArray = new JSONArray(results.payload());
                for (int i = 0; i < jsonArray.length(); i++) {
                    Facility facility;
                    JSONObject explrObject = jsonArray.getJSONObject(i);
                    facility = new Gson().fromJson(explrObject.toString(), Facility.class) ;
                    if (facility.getId().equals("")) {
                        Log.d(TAG,"facility table is empty");

                    } else {
                        Log.d(TAG,"facility downloaded "+facility.getName());
                        ContentValues values = new FacilityRepository().createValuesFor(facility);
                        android.util.Log.d(TAG, "values facility = " + new Gson().toJson(values));
                        commonRepository.customInsert(values);
                        context.userService().saveHasFacilityInfo("true");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public void initializeHasFacilities() {
        try {
            commonRepository = context.commonrepository("facility");
            long count = commonRepository.count();
            if (count > 0) {
                setHasFacility(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setFollowContent(String value) {
        //synchronization process
        ( (NativeHomeActivity)getApplicationContext()).updateFromServer();
        Log.d(TAG,"message = "+value);

    }

    public void updateReferralStatus(String value) {
        //get data from the server
        ( (NativeHomeActivity)getApplicationContext()).updateFromServer();
        Log.d(TAG,"message = "+value);
    }



    // Checking for all possible internet providers
    public boolean isConnectingToInternet(){

        ConnectivityManager connectivity =
                (ConnectivityManager) getSystemService(
                        android.content.Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    // Notifies UI to display a message.
    void displayMessageOnScreen(Context context, String message) {




        // Send Broadcast to Broadcast receiver with message


    }


    //Function to display simple Alert Dialog
    public void showAlertDialog(Context context, String title, String message,
                                Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();

        // Set Dialog Title
        alertDialog.setTitle(title);

        // Set Dialog Message
        alertDialog.setMessage(message);

        if(status != null)
            // Set alert dialog icon
            alertDialog.setIcon((status) ? R.drawable.ic_check_black_24dp : R.drawable.ic_clear_black_24dp);

        // Set OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // Show Alert Message
        alertDialog.show();
    }

    private PowerManager.WakeLock wakeLock;

    public  void acquireWakeLock() {
        if (wakeLock != null) wakeLock.release();

        PowerManager pm = (PowerManager)
                getSystemService(android.content.Context.POWER_SERVICE);

        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "WakeLock");

        wakeLock.acquire();
    }

    public  void releaseWakeLock() {
        if (wakeLock != null) wakeLock.release(); wakeLock = null;
    }

    @Override
    public void onCreate() {
        DrishtiSyncScheduler.setReceiverClass(SyncBroadcastReceiver.class);
        super.onCreate();
        Fabric.with(this, new Crashlytics());
//        ACRA.init(this);

        DrishtiSyncScheduler.setReceiverClass(SyncBroadcastReceiver.class);

        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());
        applyUserLanguagePreference();
        cleanUpSyncState();

        if (serviceRepository == null) {
            serviceRepository = new ReferralServiceRepository();
        }
        initializeReferralService();
        initializeHasFacilities();
    }


    @Override
    public void logoutCurrentUser(){
        cleanUpSyncState();
        securedActivity = new SecuredActivity() {
            @Override
            protected void onCreation() {

            }

            @Override
            protected void onResumption() {

            }
        };
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }
    public void logoutUser(){
        cleanUpSyncState();
    }

    private void cleanUpSyncState() {
        DrishtiSyncScheduler.stop(getApplicationContext());
        context.allSharedPreferences().saveIsSyncInProgress(false);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        logInfo("HTM Referral Application is terminating. Stopping Dristhi Sync scheduler and resetting isSyncInProgress setting.");
        cleanUpSyncState();
    }

    private void applyUserLanguagePreference() {
        Configuration config = getBaseContext().getResources().getConfiguration();

        String lang = context.allSharedPreferences().fetchLanguagePreference();
        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
            locale = new Locale(lang);
            updateConfiguration(config);
        }
    }

    private void updateConfiguration(Configuration config) {
        config.locale = locale;
        Locale.setDefault(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    private String[] getFtsSearchFields(String tableName){
        if (tableName.equals("wazazi_salama_mother")){
            String[] ftsSearchFields =  { "MOTHERS_FIRST_NAME","MOTHERS_LAST_NAME", "MOTHERS_LAST_MENSTRUATION_DATE", "MOTHERS_ID", "Is_PNC", "alerts.visitCode" };
            return ftsSearchFields;
        }
        return null;
    }

    private String[] getFtsSortFields(String tableName){
        if(tableName.equals("wazazi_salama_mother")){
            String[] sortFields = { "MOTHERS_FIRST_NAME","MOTHERS_LAST_NAME", "EXPECTED_DELIVERY_DATE", "MOTHERS_SORTVALUE", "PNC_STATUS", "alerts.Ante_Natal_Care_Reminder_Visit", "alerts.BirthNotificationPregnancyStatusFollowUp", "alerts.Post_Natal_Care_Reminder_Visit"};
            return sortFields;
        } else if(tableName.equals("child")){
            String[] sortFields = {"GENDER",  "motherCaseId", "alerts.Essential_Newborn_Care_Checklist"};
            return sortFields;
        }
        return null;
    }

    private String[] getFtsMainConditions(String tableName){
        if(tableName.equals("wazazi_salama_mother")){
            String[] mainConditions = {"MOTHERS_NAME", "Is_PNC", "details"};
            return mainConditions;
        } else if(tableName.equals("child")){
            String[] mainConditions = {"GENDER",  "details"};
            return mainConditions;
        }
        return null;
    }

    private String[] getFtsTables(){
        String[] ftsTables = {  "wazazi_salama_mother", "child" };
        return ftsTables;
    }

    /**
     * Map value Pair<TableName, updateVisitCode>
     * @return
     */
    private Map<String, Pair<String, Boolean>> getAlertScheduleMap(){
        Map<String, Pair<String, Boolean>> map = new HashMap<String, Pair<String, Boolean>>();
        map.put("Ante Natal Care Reminder Visit", Pair.create("wazazi_salama_mother", true));
//        map.put("BirthNotificationPregnancyStatusFollowUp",  Pair.create("mcaremother", false));
//        map.put("Post Natal Care Reminder Visit", Pair.create("mcaremother", true));
//        map.put("Essential Newborn Care Checklist", Pair.create("mcarechild", true));

        return map;
    }

    private String[] getAlertFilterVisitCodes(){
        String[] ftsTables = { "ancrv_1", "ancrv_2", "ancrv_3", "ancrv_4" };
        return ftsTables;
    }

    private CommonFtsObject createCommonFtsObject(){
        CommonFtsObject commonFtsObject = new CommonFtsObject(getFtsTables());
        for(String ftsTable: commonFtsObject.getTables()){
            commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
            commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
            commonFtsObject.updateMainConditions(ftsTable, getFtsMainConditions(ftsTable));
        }
        commonFtsObject.updateAlertScheduleMap(getAlertScheduleMap());
        commonFtsObject.updateAlertFilterVisitCodes(getAlertFilterVisitCodes());
        return commonFtsObject;
    }
    public static void setCrashlyticsUser(Context context) {
        if(context != null && context.userService() != null
                && context.allSharedPreferences() != null) {
            Crashlytics.setUserName(context.allSharedPreferences().fetchRegisteredANM());
        }
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getCurrentUserID() {
        return currentUserID;
    }

    public  void setCurrentUserID(String currentUserID) {
        this.currentUserID = currentUserID;
    }

    public String getTeam_uuid() {
        return team_uuid;
    }

    public void setTeam_uuid(String team_uuid) {
        this.team_uuid = team_uuid;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isHasFacility() {
        return hasFacility;
    }

    public void setHasFacility(boolean hasFacility) {
        this.hasFacility = hasFacility;
    }

    public boolean isHasService() {
        return hasService;
    }

    public void setHasService(boolean hasService) {
        this.hasService = hasService;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getTeam_location_id() {
        return team_location_id;
    }

    public void setTeam_location_id(String team_location_id) {
        this.team_location_id = team_location_id;
    }

    public String getRegistration_id() {
        return registration_id;
    }

    public void setRegistration_id(String registration_id) {
        this.registration_id = registration_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}