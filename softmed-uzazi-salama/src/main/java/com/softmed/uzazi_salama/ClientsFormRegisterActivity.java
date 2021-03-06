package com.softmed.uzazi_salama;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.softmed.uzazi_salama.Application.BoreshaAfyaApplication;
import com.softmed.uzazi_salama.Repository.FacilityObject;
import com.softmed.uzazi_salama.util.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.domain.ClientReferral;
import org.ei.opensrp.domain.SyncStatus;
import org.ei.opensrp.domain.form.FormData;
import org.ei.opensrp.domain.form.FormField;
import org.ei.opensrp.domain.form.FormInstance;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.repository.ClientReferralRepository;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import fr.ganfra.materialspinner.MaterialSpinner;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.softmed.uzazi_salama.util.Utils.generateRandomUUIDString;

/**
 * Created by Coze on 05/17/18.
 */

public class ClientsFormRegisterActivity extends SecuredNativeSmartRegisterActivity {

    private Toolbar toolbar;
    private static final String TAG = ClientsFormRegisterActivity.class.getSimpleName();
    public static AutoCompleteTextView facilitytextView;
    public static EditText editTextfName,editTextmName,editTextlName,editTextVillageLeader, editTextAge, editTextCTCNumber,
            editTextDiscountId, mapcueText,editTextReferralReason,editTextGravida ,editTextPara,editTextSpouseName,gravidaText ,paraText,spouseNameText;
    public static Button saveButton,cancelButton;
    public static MaterialSpinner spinnerHeight, pmtctStatusSpinner,levelOfEducationSpinner;
    private ArrayAdapter<String> heightAdapter,pmctcAdapter,levelOfEducationAdapter;
    private ArrayAdapter<String>  facilityAdapter;
    private Calendar today;
    private long dob;
    private LinearLayout parentLayout;
    private EditText textPhone;
    private List<String> facilityList = new ArrayList<String>();
    private List<String> heightList = new ArrayList<String>();
    private List<String> hpmctcList = new ArrayList<String>();
    private List<String> levelOfEducationList = new ArrayList<String>();
    private List<String> category = new ArrayList<String>();
    private List<String> AllCheckbox = new ArrayList<String>();
    public String message = "";
    public static Context context;
    public static int clientServiceSelection = -1,genderSelection = -1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private String  formName = "anc_client_referral_form";
    private String recordId,wardId="";
    private ClientReferral clientReferral;
    private Gson gson = new Gson();
    private JSONObject fieldOverides = new JSONObject();
    private ClientReferralRepository clientReferralRepository;
    private CommonRepository commonRepository;
    private Cursor cursor;
    private MaterialEditText dobTextView,lnmpTextView,eddTextView ;
    private List<FacilityObject> facilitiesList;
    ArrayList<String> genderList = new ArrayList<String>();
    public Dialog referalDialogue;
    public String categoryValue;
    private long edd, lnmp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLanguage();
        setContentView(R.layout.activity_anc_registration_form);
        setDataLists();
        setFacilistList();
        setupviews();


        Log.d(TAG,"provider UUID = "+((BoreshaAfyaApplication)getApplication()).getCurrentUserID());
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        toolbar = (Toolbar) findViewById(com.softmed.uzazi_salama.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        referalDialogue = new Dialog(this);
        referalDialogue.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle == null) {
            wardId= null;
        } else {
            wardId= bundle.getString("selectedLocation");
        }
        today = Calendar.getInstance();


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormSubmissionOk()) {
                    //setting default values
                    clientReferral = getClientReferral();
                    clientReferral.setReferral_status(0);

                    // convert to json
                    String gsonReferral = gson.toJson(clientReferral);
                    Log.d(TAG, "referral = " + gsonReferral);
                    Log.d(TAG, "fname = " + formName);

                    saveFormSubmission(gsonReferral, generateRandomUUIDString(), formName, getFormFieldsOverrides());
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("status", true);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormSubmissionOk()) {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }

            }
        });

    }


    @Override
    public void saveFormSubmission(String formSubmission, final String id, String formName, JSONObject fieldOverrides) {
        // save the form
            final ClientReferral clientReferral = gson.fromJson(formSubmission, ClientReferral.class);
            clientReferral.setId(id);
            ContentValues values = new ClientReferralRepository().createValuesFor(clientReferral);
            Log.d(TAG, "values = " + gson.toJson(values));

            clientReferralRepository = context().clientReferralRepository();
            clientReferralRepository.add(clientReferral);

            List<FormField> formFields = new ArrayList<>();

            formFields.add(new FormField("id", id, ClientReferralRepository.TABLE_NAME + "." + "id"));
            formFields.add(new FormField("relationalid", clientReferral.getRelationalid(), ClientReferralRepository.TABLE_NAME + "." + "relationalid"));

            FormData formData;
            FormInstance formInstance;
            FormSubmission submission;

                for (Field field : ClientReferral.class.getDeclaredFields()) {
                    Log.d(TAG,"key = "+field);
                  if(field.getName().equals("facility_id")){
                      FormField f = null;
                      try {
                          f = new FormField(field.getName(), String.valueOf(field.get(clientReferral)), "facility.id");
                      } catch (IllegalAccessException e) {
                          e.printStackTrace();
                      }
                      formFields.add(f);
                    }else{
                      FormField f = null;
                      try {
                          f = new FormField(field.getName(), String.valueOf(field.get(clientReferral)), ClientReferralRepository.TABLE_NAME + "." + field.getName());
                      } catch (IllegalAccessException e) {
                          e.printStackTrace();
                      }
                      formFields.add(f);
                    }
                }
                Log.d(TAG,"form field = "+ new Gson().toJson(formFields));
                formData = new FormData("anc_client_referral","/model/instance/anc_client_referral_form/",formFields,null);
                formInstance  = new FormInstance(formData,"1");
                submission= new FormSubmission(generateRandomUUIDString(),id,"anc_client_referral_form",new Gson().toJson(formInstance),"4", SyncStatus.PENDING,"4");
                context().formDataRepository().saveFormSubmission(submission);

            new  com.softmed.uzazi_salama.util.AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    if(!clientReferral.getPhone_number().equals(""))
                        Utils.sendRegistrationAlert(clientReferral.getPhone_number());
                    return null;
                }
            }.execute();


    }

    private void setupviews(){

        textPhone = (EditText)   findViewById(com.softmed.uzazi_salama.R.id.phone);
        dobTextView = (MaterialEditText)   findViewById(com.softmed.uzazi_salama.R.id.date_of_birth);
        lnmpTextView = (MaterialEditText)   findViewById(com.softmed.uzazi_salama.R.id.date_of_lnmp);
        eddTextView = (MaterialEditText)   findViewById(com.softmed.uzazi_salama.R.id.date_of_delivery);


        editTextfName = (EditText)   findViewById(com.softmed.uzazi_salama.R.id.first_name);
        editTextmName = (EditText)   findViewById(com.softmed.uzazi_salama.R.id.middle_name);
        editTextlName = (EditText)   findViewById(com.softmed.uzazi_salama.R.id.last_name);
        editTextGravida = (EditText)   findViewById(com.softmed.uzazi_salama.R.id.gravida);
        editTextPara = (EditText)   findViewById(com.softmed.uzazi_salama.R.id.para);
        editTextSpouseName = (EditText)   findViewById(com.softmed.uzazi_salama.R.id.spouse_name);


        mapcueText = (EditText)   findViewById(com.softmed.uzazi_salama.R.id.village);
        gravidaText = (EditText)   findViewById(com.softmed.uzazi_salama.R.id.gravida);
        paraText= (EditText)   findViewById(com.softmed.uzazi_salama.R.id.para);
        spouseNameText = (EditText)   findViewById(com.softmed.uzazi_salama.R.id.spouse_name);

        mapcueText = (EditText)   findViewById(com.softmed.uzazi_salama.R.id.village);
        editTextReferralReason = (EditText)   findViewById(com.softmed.uzazi_salama.R.id.reason_for_referral);
        saveButton = (Button)   findViewById(com.softmed.uzazi_salama.R.id.registered_client_save_button);
        cancelButton = (Button)   findViewById(com.softmed.uzazi_salama.R.id.registered_client_cancel_button);

        heightAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, heightList);
        heightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHeight = (MaterialSpinner)   findViewById(com.softmed.uzazi_salama.R.id.height_spinner);
        spinnerHeight.setAdapter(heightAdapter);

        pmctcAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hpmctcList);
        pmctcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pmtctStatusSpinner = (MaterialSpinner)   findViewById(com.softmed.uzazi_salama.R.id.pmtct_status_spinner);
        pmtctStatusSpinner.setAdapter(pmctcAdapter);


        levelOfEducationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, levelOfEducationList);
        levelOfEducationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelOfEducationSpinner = (MaterialSpinner)   findViewById(com.softmed.uzazi_salama.R.id.level_of_education_spinner);
        levelOfEducationSpinner.setAdapter(levelOfEducationAdapter);




        facilityAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, facilityList);
         facilitytextView = (AutoCompleteTextView) findViewById(com.softmed.uzazi_salama.R.id.autocomplete_facility);
        facilitytextView.setThreshold(1);
        facilitytextView.setAdapter(facilityAdapter);

        dobTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"DOB has been clicked");
                // pick date
                pickDate(com.softmed.uzazi_salama.R.id.reg_dob);
            }
        });

        lnmpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pick date
                pickDate(com.softmed.uzazi_salama.R.id.date_of_lnmp);
            }
        });

    }
    
    private void setDataLists(){
        heightList.clear();
        heightList.add("Height < 150 cm");
        heightList.add("Height > 150 cm");

        hpmctcList.clear();
        hpmctcList.add("1");
        hpmctcList.add("2");
        hpmctcList.add("U");

        levelOfEducationList.clear();
        levelOfEducationList.add("None");
        levelOfEducationList.add("Primary Education");
        levelOfEducationList.add("Secondary Education");
        levelOfEducationList.add("Higher Level Education");


    }


    private void setFacilistList(){
        commonRepository = context().commonrepository("facility");
        cursor = commonRepository.RawCustomQueryForAdapter("select * FROM facility");
        List<CommonPersonObject> commonPersonObjectList2 = commonRepository.readAllcommonForField(cursor, "facility");
        Log.d(TAG, "commonPersonList = " + gson.toJson(commonPersonObjectList2));

        this.facilitiesList = Utils.convertToFacilityObjectList(commonPersonObjectList2);
        int size2 = facilitiesList.size();
        for(int i =0; size2 > i; i++  ){

            facilityList.add(facilitiesList.get(i).getName());
        }
        Log.d(TAG, "facility list"+facilityList.toString());
    }


    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return null;
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {

    }

    @Override
    public void startRegistration() {

    }

    private void pickDate(final int id) {
        // listener
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                GregorianCalendar pickedDate = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                if (id == com.softmed.uzazi_salama.R.id.reg_dob) {
                    dob = pickedDate.getTimeInMillis();
                    dobTextView.setText(dateFormat.format(pickedDate.getTimeInMillis()));
                }else if (id == R.id.date_of_lnmp) {
                    lnmp = pickedDate.getTimeInMillis();
                    lnmpTextView.setText(dateFormat.format(lnmp));
                    // update edd
                    edd = Utils.calculateEDDFromLNMP(lnmp);
                    eddTextView.setText(dateFormat.format(edd));
                }
            }
        };

        // dialog
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                onDateSetListener);

        datePickerDialog.setOkColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_blue_light));
        datePickerDialog.setCancelColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_light));

        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_1);
        datePickerDialog.setAccentColor(ContextCompat.getColor(getApplicationContext(), com.softmed.uzazi_salama.R.color.colorPrimary));

        // show dialog
        datePickerDialog.show(this.getFragmentManager(), "DatePickerDialog");
    }


    public boolean isFormSubmissionOk() {
        if (TextUtils.isEmpty(editTextfName.getText())  ||
                TextUtils.isEmpty(editTextlName.getText())) {
            message = getResources().getString(com.softmed.uzazi_salama.R.string.unfilled_information);
            makeToast();
            return false;
        } else if (TextUtils.isEmpty(facilitytextView.getText())) {
            message = getResources().getString(com.softmed.uzazi_salama.R.string.missing_facility);
            makeToast();
            return false;

        }else if (!TextUtils.isEmpty(facilitytextView.getText())) {
            String facilityName = facilitytextView.getText().toString();


            facilityName = facilityName.trim();
            int index = facilityList.indexOf(facilityName);

            Log.d(TAG,"Selected health facility = "+facilityName);
            Log.d(TAG,"Selected health index = "+index);

            if(index<=0){
                message = getResources().getString(com.softmed.uzazi_salama.R.string.wrong_facility);
                makeToast();
                return false;
            }else {
                return true;
            }

        }else if (pmtctStatusSpinner.getSelectedItemPosition() <=0) {
            message = getResources().getString(com.softmed.uzazi_salama.R.string.missing_pmtct_status);
            makeToast();
            return false;

        }else if (spinnerHeight.getSelectedItemPosition() <= 0 ) {
            message = getResources().getString(com.softmed.uzazi_salama.R.string.missing_services);
            makeToast();
            return false;

        } else if (TextUtils.isEmpty(mapcueText.getText())) {
            message = getResources().getString(com.softmed.uzazi_salama.R.string.missing_physical_address);
            makeToast();
            return false;
        } else  if (TextUtils.isEmpty(editTextReferralReason.getText())) {
            message = getResources().getString(com.softmed.uzazi_salama.R.string.missing_missing_referral_reason);
            makeToast();
            return false;
        }else
            // all good
            return true;
    }

    public ClientReferral getClientReferral() {
        ClientReferral referral = new ClientReferral();
        referral.setReferral_date(today.getTimeInMillis());
        referral.setDate_of_birth(dob);
        referral.setFirst_name(editTextfName.getText().toString());
        referral.setMiddle_name(editTextmName.getText().toString());
        referral.setSurname(editTextlName.getText().toString());
        referral.setMap_cue(mapcueText.getText().toString());
        referral.setPhone_number(textPhone.getText().toString());
        referral.setVillage(wardId);
        referral.setPmtct_status(pmtctStatusSpinner.getSelectedItemPosition());
        referral.setHeight_below_average(spinnerHeight.getSelectedItemPosition()==0);
        referral.setLevel_of_education(levelOfEducationSpinner.getSelectedItemPosition());
        referral.setSpouse_name(spouseNameText.getText().toString());
        referral.setGravida(Integer.valueOf(gravidaText.getText().toString()));
        referral.setPara(Integer.valueOf(paraText.getText().toString()));
        referral.setLmnp_date(lnmp);
        referral.setEdd(edd);
        referral.setIs_valid(true);
        referral.setReferral_status(0);
        referral.setPhone_number(textPhone.getText().toString());
        referral.setFacility_id(getFacilityId(facilitytextView.getText().toString()));
        referral.setReferral_reason(editTextReferralReason.getText().toString());
        referral.setWard(wardId);
        referral.setService_provider_uuid(((BoreshaAfyaApplication)getApplication()).getCurrentUserID());


        return referral;
    }

    public JSONObject getFormFieldsOverrides() {
        return fieldOverides;
    }

    public String getFacilityId(String name){
        cursor = commonRepository.RawCustomQueryForAdapter("select * FROM facility where name ='"+ name +"'");

        List<CommonPersonObject> commonPersonObjectList = commonRepository.readAllcommonForField(cursor, "facility");
        Log.d(TAG, "commonPersonList = " + gson.toJson(commonPersonObjectList));

        return commonPersonObjectList.get(0).getColumnmaps().get("id");
    }
    private void makeToast() {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG).show();
    }

    public static void setLanguage() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(org.ei.opensrp.Context.getInstance().applicationContext()));
        String preferredLocale = allSharedPreferences.fetchLanguagePreference();


        android.util.Log.d(TAG,"set Locale : "+preferredLocale);

        Resources res = org.ei.opensrp.Context.getInstance().applicationContext().getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(preferredLocale);
        res.updateConfiguration(conf, dm);

    }

}
