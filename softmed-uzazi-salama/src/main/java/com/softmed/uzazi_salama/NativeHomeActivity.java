package com.softmed.uzazi_salama;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.ControllerFilterMap;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import com.softmed.uzazi_salama.Application.BoreshaAfyaApplication;
import com.softmed.uzazi_salama.util.OrientationHelper;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.service.PendingFormSubmissionService;
import org.ei.opensrp.sync.SyncAfterFetchListener;
import org.ei.opensrp.sync.SyncProgressIndicator;
import org.ei.opensrp.sync.UpdateActionsTask;
import org.ei.opensrp.view.activity.SecuredActivity;
import org.ei.opensrp.view.contract.HomeContext;
import org.ei.opensrp.view.controller.NativeAfterANMDetailsFetchListener;
import org.ei.opensrp.view.controller.NativeUpdateANMDetailsTask;
import org.ei.opensrp.view.fragment.DisplayFormFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.widget.Toast.LENGTH_SHORT;
import static java.lang.String.valueOf;
import static org.ei.opensrp.event.Event.ACTION_HANDLED;
import static org.ei.opensrp.event.Event.FORM_SUBMITTED;
import static org.ei.opensrp.event.Event.SYNC_COMPLETED;
import static org.ei.opensrp.event.Event.SYNC_STARTED;

public class NativeHomeActivity extends SecuredActivity {
    private static final String TAG = NativeHomeActivity.class.getSimpleName();
    private NavigationController navigationController1;
    private MenuItem updateMenuItem;
    private MenuItem remainingFormsToSyncMenuItem;
    private PendingFormSubmissionService pendingFormSubmissionService;
    static final String DATABASE_NAME = "drishti.db";

    private Listener<Boolean> onSyncStartListener = new Listener<Boolean>() {
        @Override
        public void onEvent(Boolean data) {
            if (updateMenuItem != null) {
                updateMenuItem.setActionView(com.softmed.uzazi_salama.R.layout.progress);
            }
        }
    };

    private Listener<Boolean> onSyncCompleteListener = new Listener<Boolean>() {
        @Override
        public void onEvent(Boolean data) {
            //#TODO: RemainingFormsToSyncCount cannot be updated from a back ground thread!!
            updateRemainingFormsToSyncCount();
            if (updateMenuItem != null) {
                updateMenuItem.setActionView(null);
            }
            updateRegisterCounts();
        }
    };

    private Listener<String> onFormSubmittedListener = new Listener<String>() {
        @Override
        public void onEvent(String instanceId) {
            updateRegisterCounts();
        }
    };

    private Listener<String> updateANMDetailsListener = new Listener<String>() {
        @Override
        public void onEvent(String data) {
            updateRegisterCounts();
        }
    };


    public static int hhcount;
    private long unsuccesfulCount;
    private long succesfulCount;

    @Override
    protected void onCreation() {
        setContentView(com.softmed.uzazi_salama.R.layout.smart_registers_home);
        navigationController = new NavigationController(this, anmController);
        navigationController1 = new NavigationController(this, anmController);
        setupViews();
        initialize();
        DisplayFormFragment.formInputErrorMessage = getResources().getString(com.softmed.uzazi_salama.R.string.forminputerror);
        DisplayFormFragment.okMessage = getResources().getString(R.string.button_ok);



        // orientation
        OrientationHelper.setProperOrientationForDevice(NativeHomeActivity.this);


        String userlocation = context().allSettings().settingsRepository.querySetting("anmLocation","");
        String userDetailsString = context().allSettings().settingsRepository.querySetting("userInformation","");
        String teamDetailsString = context().allSettings().settingsRepository.querySetting("teamInformation","");
        Log.d(TAG,"team details "+teamDetailsString);
        JSONObject teamSettings = null;
        try {
            teamSettings = new JSONObject(teamDetailsString);


            JSONObject team_details = null;
            try {
                Log.d(TAG,"teamSettings = "+teamSettings.toString());
                team_details = teamSettings.getJSONObject("team");
                Log.d(TAG,"team jason "+team_details.get("uuid").toString()+" "+team_details.get("teamName").toString());
                ((BoreshaAfyaApplication)getApplication()).setTeam_uuid(team_details.get("uuid").toString());
                ((BoreshaAfyaApplication)getApplication()).setTeam_name(team_details.get("teamName").toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject userLocationSettings = null;
            try {
                userLocationSettings = team_details.getJSONObject("location");
                Log.d(TAG,"teamSettings location id= "+userLocationSettings.get("uuid").toString());
                ((BoreshaAfyaApplication)getApplication()).setTeam_location_id(userLocationSettings.get("uuid").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject userSettings = null;
        try {
            userSettings = new JSONObject(userDetailsString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray roles = null;
        try {
            Log.d(TAG,"usersettings = "+userSettings.toString());
            roles = userSettings.getJSONArray("roles");
            int count = roles.length();
            for (int i =0 ; i<count ; i++){
                try {
                    if(roles.getString(i).equals("Organizational: Health Facility User")){
                        ((BoreshaAfyaApplication)getApplication()).setUserType(0);
                    }else if (roles.getString(i).equals("Organizational: CHW")){
                        ((BoreshaAfyaApplication)getApplication()).setUserType(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject attributes = null;
        try {
            attributes = userSettings.getJSONObject("attributes");

            ((BoreshaAfyaApplication)getApplication()).setCurrentUserID(attributes.get("_PERSON_UUID").toString());
            ((BoreshaAfyaApplication)getApplication()).setUsername(userSettings.get("username").toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }





    }

    private void setupViews() {
//        findViewById(R.id.btn_chw_register).setOnClickListener(onRegisterStartListener);
//        findViewById(R.id.btn_pnc_register).setOnClickListener(onRegisterStartListener);
        findViewById(com.softmed.uzazi_salama.R.id.btn_anc_register).setOnClickListener(onRegisterStartListener);
//        findViewById(R.id.btn_pnc_register).setOnClickListener(onRegisterStartListener);
        findViewById(com.softmed.uzazi_salama.R.id.btn_reporting).setOnClickListener(onRegisterStartListener);
    }

    private void initialize() {
        pendingFormSubmissionService = context().pendingFormSubmissionService();
        SYNC_STARTED.addListener(onSyncStartListener);
        SYNC_COMPLETED.addListener(onSyncCompleteListener);
        FORM_SUBMITTED.addListener(onFormSubmittedListener);
        ACTION_HANDLED.addListener(updateANMDetailsListener);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Uzazi Salama");
        LoginActivity.setLanguage();
    }

    @Override
    protected void onResumption() {
        LoginActivity.setLanguage();
        updateRegisterCounts();
        updateSyncIndicator();
        updateRemainingFormsToSyncCount();
    }

    private void updateRegisterCounts() {
        NativeUpdateANMDetailsTask task = new NativeUpdateANMDetailsTask(Context.getInstance().anmController());
        task.fetch(new NativeAfterANMDetailsFetchListener() {
            @Override
            public void afterFetch(HomeContext anmDetails) {
                // TODO: 9/14/17 update counts after fetch
//                updateRegisterCounts(anmDetails);
            }
        });
    }

    private void updateRegisterCounts(final HomeContext homeContext) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();

                succesfulCount = homeContext.getSucessReferralCount();
                unsuccesfulCount = homeContext.getUnsucessReferralCount();



                    Handler mainHandler = new Handler(getMainLooper());

                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                        }
                    };
                    mainHandler.post(myRunnable);
//                } else {
//
//                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.softmed.uzazi_salama.R.menu.menu_main, menu);
        attachLogoutMenuItem(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        updateMenuItem = menu.findItem(com.softmed.uzazi_salama.R.id.updateMenuItem);
        remainingFormsToSyncMenuItem = menu.findItem(com.softmed.uzazi_salama.R.id.remainingFormsToSyncMenuItem);

        updateSyncIndicator();
        updateRemainingFormsToSyncCount();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case com.softmed.uzazi_salama.R.id.updateMenuItem:
                updateFromServer();
                return true;
            case com.softmed.uzazi_salama.R.id.switchLanguageMenuItem:
                String newLanguagePreference = LoginActivity.switchLanguagePreference();
                LoginActivity.setLanguage();
                Toast.makeText(this, "Language preference set to " + newLanguagePreference + ". Please restart the application.", LENGTH_SHORT).show();
                this.recreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateFromServer() {
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(
                this, context().actionService(), context().formSubmissionSyncService(),
                new SyncProgressIndicator(), context().allFormVersionSyncService());
        updateActionsTask.updateFromServer(new SyncAfterFetchListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SYNC_STARTED.removeListener(onSyncStartListener);
        SYNC_COMPLETED.removeListener(onSyncCompleteListener);
        FORM_SUBMITTED.removeListener(onFormSubmittedListener);
        ACTION_HANDLED.removeListener(updateANMDetailsListener);
    }

    private void updateSyncIndicator() {
        if (updateMenuItem != null) {
            if (context().allSharedPreferences().fetchIsSyncInProgress()) {
                updateMenuItem.setActionView(com.softmed.uzazi_salama.R.layout.progress);
            } else
                updateMenuItem.setActionView(null);
        }
    }

    private void updateRemainingFormsToSyncCount() {
        if (remainingFormsToSyncMenuItem == null) {
            return;
        }

        long size = pendingFormSubmissionService.pendingFormSubmissionCount();
        if (size > 0) {
            remainingFormsToSyncMenuItem.setTitle(valueOf(size) + " " + getString(com.softmed.uzazi_salama.R.string.unsynced_forms_count_message));
            remainingFormsToSyncMenuItem.setVisible(true);
        } else {
            remainingFormsToSyncMenuItem.setVisible(false);
        }
    }

    private View.OnClickListener onRegisterStartListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case com.softmed.uzazi_salama.R.id.btn_anc_register:
                    navigationController.startCHWSmartRegistry();
                    break;

//                case R.id.btn_pnc_register:
//                    navigationController.startPNCSmartRegistry();
//                    break;

//                case R.id.btn_chw_register:
//                    navigationController1.startCHWSmartRegistry();
//                    break;

//                case R.id.btn_reporting:
//                    navigationController.startFPSmartRegistry();
//                    break;
            }
        }
    };

    private View.OnClickListener onButtonsClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case com.softmed.uzazi_salama.R.id.btn_reporting:
//                    navigationController.startReports();
                    break;

//                case R.id.btn_videos:
//                    navigationController.startVideos();
//                    break;
            }
        }
    };

    class pncControllerfiltermap extends ControllerFilterMap {

        @Override
        public boolean filtermapLogic(CommonPersonObject commonPersonObject) {
            boolean returnvalue = false;
            if (commonPersonObject.getDetails().get("IS_VALID") != null) {
                if (commonPersonObject.getDetails().get("IS_VALID").equalsIgnoreCase("1")) {
                    returnvalue = true;
                    if (commonPersonObject.getDetails().get("Is_PNC") != null) {
                        if (commonPersonObject.getDetails().get("Is_PNC").equalsIgnoreCase("1")) {
                            returnvalue = true;
                        }

                    } else {
                        returnvalue = false;
                    }
                }
            }
            Log.v("the filter", "" + returnvalue);
            return returnvalue;
        }
    }

    class ancControllerfiltermap extends ControllerFilterMap {

        @Override
        public boolean filtermapLogic(CommonPersonObject commonPersonObject) {
            boolean returnvalue = false;
            if (commonPersonObject.getDetails().get("IS_VALID") != null) {
                if (commonPersonObject.getDetails().get("IS_VALID").equalsIgnoreCase("1")) {
                    returnvalue = true;
                    if (commonPersonObject.getDetails().get("Is_PNC") != null) {
                        if (commonPersonObject.getDetails().get("Is_PNC").equalsIgnoreCase("1")) {
                            returnvalue = false;
                        }

                    }
                }
            }
            Log.v("the filter", "" + returnvalue);
            return returnvalue;
        }
    }


    public boolean backUpDataBase() {
        boolean result = true;

        // Source path in the application database folder
        String appDbPath = "/data/data/com.my.application/databases/" + DATABASE_NAME;

        // Destination Path to the sdcard app folder
        String sdFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + DATABASE_NAME;


        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            //Open your local db as the input stream
            myInput = new FileInputStream(appDbPath);
            //Open the empty db as the output stream
            myOutput = new FileOutputStream(sdFolder);

            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
        } catch (IOException e) {
            result = false;
            e.printStackTrace();
        } finally {
            try {
                //Close the streams
                if (myOutput != null) {
                    myOutput.flush();
                    myOutput.close();
                }
                if (myInput != null) {
                    myInput.close();
                }
            } catch (IOException e) {
            }
        }

        return result;
    }
}
