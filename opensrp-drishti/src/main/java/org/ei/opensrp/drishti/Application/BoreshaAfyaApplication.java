package org.ei.opensrp.drishti.Application;

import android.content.Intent;
import android.content.res.Configuration;
import android.util.Pair;

import com.crashlytics.android.Crashlytics;

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonFtsObject;
import org.ei.opensrp.drishti.LoginActivity;
import org.ei.opensrp.repository.ReferralServiceRepository;
import org.ei.opensrp.sync.DrishtiSyncScheduler;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.ei.opensrp.view.receiver.SyncBroadcastReceiver;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

import static org.ei.opensrp.util.Log.logInfo;

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
    private ReferralServiceRepository serviceRepository;
    private int userType=0;//0=CHW and 1=Facility health care worker
    public  String currentUserID,team_uuid, phone_number,username,team_name,team_location_id;


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
    }

    @Override
    public void logoutCurrentUser(){
        cleanUpSyncState();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
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
}
