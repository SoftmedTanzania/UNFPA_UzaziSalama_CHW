package org.ei.opensrp.drishti.Fragments;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.domain.ClientReferral;
import org.ei.opensrp.drishti.Application.BoreshaAfyaApplication;
import org.ei.opensrp.domain.Facility;
import org.ei.opensrp.drishti.R;
import org.ei.opensrp.drishti.Repository.FacilityObject;
import org.ei.opensrp.drishti.Repository.ReferralServiceObject;
import org.ei.opensrp.drishti.util.Utils;
import org.ei.opensrp.repository.FacilityRepository;
import org.ei.opensrp.repository.ReferralServiceRepository;
import org.ei.opensrp.drishti.pageradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClientRegistrationFormFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {
    private static final String TAG = ClientRegistrationFormFragment.class.getSimpleName();
    public static TextView textDate;
    LinearLayout layoutDatePick;
    CardView cardDatePickLNMP;
    public static EditText editTextfName,editTextmName,editTextlName,editTextClinicName, editTextVillageLeader, editTextAge, editTextCTCNumber,
            editTextDiscountId,editTextKijiji,editTextReferralReason,editTextReferralFacility;
    public static TextView textviewReferralProviderSupportGroup,textviewReferralProvider,textviewReferralNumber;
    public static Button button;
    public static RadioGroup radioGroupGender;
    public static MaterialSpinner spinnerService, spinnerFacility;
    private ArrayAdapter<String>  serviceAdapter;
    private ArrayAdapter<String>  facilityAdapter;
    private ArrayList<Facility> facilities;

    private Calendar today;
    private static CheckBox checkBoxAreasonOne, checkBoxreasonTwo, checkBoxreasonThree,
            checkBoxreasonFour, checkBoxresonFive, checkBoxreasonSix;

    private LinearLayout tbLayout;
    private EditText CTCLayout,textPhone;
    private List<String> facilityList = new ArrayList<>();
    private List<String> serviceList = new ArrayList<>();
    public String message = "toast message";
    public static Context context;
    public static int clientServiceSelection = -1,facilitySelection = -1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private String  formName = "client_referral_form";
    private String recordId,wardId,fName ="";
    private ClientReferral clientReferral;
    private Gson gson = new Gson();
    private JSONObject fieldOverides = new JSONObject();
    private CommonRepository commonRepository,commonRepository1;
    private Cursor cursor;
    private MaterialEditText dobTextView;
    private List<FacilityObject> facility = new ArrayList<>();
    private ReferralServiceRepository referralServiceRepository = new ReferralServiceRepository();
    private FacilityRepository facilityRepository = new FacilityRepository();
    private List<ReferralServiceObject> referralServiceList;
    private List<FacilityObject> facilitiesList;
    public ClientRegistrationFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        today = Calendar.getInstance();


        commonRepository = context().commonrepository("referral_service");
        //todo martha edit the query
        cursor = commonRepository.RawCustomQueryForAdapter("select * FROM referral_service");

        List<CommonPersonObject> commonPersonObjectList = commonRepository.readAllcommonForField(cursor, "referral_service");
        Log.d(TAG, "commonPersonList = " + gson.toJson(commonPersonObjectList));

        this.referralServiceList = Utils.convertToServiceObjectList(commonPersonObjectList);
            int size = referralServiceList.size();

            for(int i =0; size > i; i++  ){

                serviceList.add(referralServiceList.get(i).getName());
            }

        commonRepository1 = context().commonrepository("facility");
        //todo martha edit the query
        cursor = commonRepository.RawCustomQueryForAdapter("select * FROM facility");

        List<CommonPersonObject> commonPersonObjectList2 = commonRepository.readAllcommonForField(cursor, "facility");
        Log.d(TAG, "commonPersonList = " + gson.toJson(commonPersonObjectList2));

        this.facilitiesList = Utils.convertToFacilityObjectList(commonPersonObjectList2);
            int size2 = facilitiesList.size();

            for(int i =0; size2 > i; i++  ){

                facilityList.add(facilitiesList.get(i).getName());
            }

        context = getContext();
    }

    @Override
    protected void onCreation() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final   View fragmentView = inflater.inflate(R.layout.activity_chwedit_pre_registration, container, false);


        textDate = (TextView) fragmentView.findViewById(R.id.textDate);
        textPhone = (EditText) fragmentView.findViewById(R.id.edittextPhone);
        layoutDatePick = (LinearLayout) fragmentView.findViewById(R.id.layoutDatePick);
        dobTextView = (MaterialEditText) fragmentView.findViewById(R.id.reg_dob);
        tbLayout = (LinearLayout)fragmentView.findViewById(R.id.outlayer);
        CTCLayout = (EditText)fragmentView.findViewById(R.id.editTextOthers);


        editTextfName = (EditText) fragmentView.findViewById(R.id.editTextfName);
        editTextmName = (EditText) fragmentView.findViewById(R.id.editTextmName);
        editTextlName = (EditText) fragmentView.findViewById(R.id.editTextlName);
        editTextAge = (EditText) fragmentView.findViewById(R.id.editTextMotherAge);
        editTextReferralReason = (EditText) fragmentView.findViewById(R.id.reason_for_referral);
        editTextVillageLeader = (EditText) fragmentView.findViewById(R.id.editTextVillageLeader);
        textviewReferralProvider = (TextView) fragmentView.findViewById(R.id.provider_name);
        textviewReferralNumber = (TextView) fragmentView.findViewById(R.id.provider_number);
        textviewReferralProviderSupportGroup = (TextView) fragmentView.findViewById(R.id.provider_support_group);

        //setting CHW details
        textviewReferralProvider.setText(((BoreshaAfyaApplication)getActivity().getApplication()).getUsername());
        textviewReferralProviderSupportGroup.setText(((BoreshaAfyaApplication)getActivity().getApplication()).getTeam_name());


        Log.d(TAG, "username"+((BoreshaAfyaApplication)getActivity().getApplication()).getUsername());
        Log.d(TAG, "team name "+((BoreshaAfyaApplication)getActivity().getApplication()).getTeam_name());

        editTextDiscountId = (EditText) fragmentView.findViewById(R.id.editTextDiscountId);
        editTextKijiji = (EditText) fragmentView.findViewById(R.id.editTextKijiji);
        editTextCTCNumber = (EditText) fragmentView.findViewById(R.id.editTextOthers);

        button = (Button) fragmentView.findViewById(R.id.save);

        serviceAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, serviceList);
        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerService = (MaterialSpinner) fragmentView.findViewById(R.id.spinnerService);
        spinnerService.setAdapter(serviceAdapter);

        facilityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, facilityList);
        facilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFacility = (MaterialSpinner) fragmentView.findViewById(R.id.spinnerFacility);
        spinnerFacility.setAdapter(facilityAdapter);




        spinnerService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= 0) {
                    spinnerService.setFloatingLabelText("Aina za Huduma");
                    clientServiceSelection = i;
                }


                if((spinnerService.getSelectedItem().toString()).equals("Rufaa kwenda kliniki ya kutibu kifua kikuu")){


                    fragmentView.findViewById(R.id.checkboxis_at_hot_spot).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxHasHeadache).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxIsVomiting).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxis_at_hot_spot).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxhas_affected_partner).setVisibility(View.GONE);
                    checkBoxAreasonOne = (CheckBox) fragmentView.findViewById(R.id.checkbox2weekCough);
                    checkBoxreasonTwo = (CheckBox) fragmentView.findViewById(R.id.checkboxfever);
                    checkBoxreasonThree = (CheckBox) fragmentView.findViewById(R.id.checkboxWeightLoss);
                    checkBoxreasonFour = (CheckBox) fragmentView.findViewById(R.id.checkboxSevereSweating);
                    checkBoxresonFive = (CheckBox) fragmentView.findViewById(R.id.checkboxBloodCough);
                    checkBoxreasonSix = (CheckBox) fragmentView.findViewById(R.id.checkboxLostFollowup);
                    checkBoxAreasonOne.setVisibility(View.VISIBLE);
                    checkBoxreasonTwo.setVisibility(View.VISIBLE);
                    checkBoxreasonThree.setVisibility(View.VISIBLE);
                    checkBoxreasonFour.setVisibility(View.VISIBLE);
                    checkBoxresonFive.setVisibility(View.VISIBLE);
                    checkBoxreasonSix.setVisibility(View.VISIBLE);
                    tbLayout.setVisibility(View.VISIBLE);
                    CTCLayout.setVisibility(View.VISIBLE);
                    fName = "client_tb_referral_form";

                }else if((spinnerService.getSelectedItem().toString()).equals("Rufaa kwenda kliniki ya TB na Matunzo (CTC)")||(spinnerService.getSelectedItem().toString()).equals("Rufaa kwenda kupata huduma za kuzuia maambukizi toka kwa mama kwenda kwa mtoto") ){


                    fragmentView.findViewById(R.id.checkboxHasHeadache).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxWeightLoss).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxSevereSweating).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxhas_loss_of_appetite).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxIsVomiting).setVisibility(View.GONE);
                    checkBoxAreasonOne = (CheckBox) fragmentView.findViewById(R.id.checkboxis_at_hot_spot);
                    checkBoxreasonTwo = (CheckBox) fragmentView.findViewById(R.id.checkboxfever);
                    checkBoxreasonThree = (CheckBox) fragmentView.findViewById(R.id.checkboxWeightLoss);
                    checkBoxreasonFour = (CheckBox) fragmentView.findViewById(R.id.checkboxhas_symptomps_for_associative_diseases);
                    checkBoxresonFive = (CheckBox) fragmentView.findViewById(R.id.checkboxhas_affected_partner);
                    checkBoxreasonSix = (CheckBox) fragmentView.findViewById(R.id.checkboxLostFollowup);
                    checkBoxAreasonOne.setVisibility(View.VISIBLE);
                    checkBoxreasonTwo.setVisibility(View.VISIBLE);
                    checkBoxreasonThree.setVisibility(View.VISIBLE);
                    checkBoxreasonFour.setVisibility(View.VISIBLE);
                    checkBoxresonFive.setVisibility(View.VISIBLE);
                    checkBoxreasonSix.setVisibility(View.VISIBLE);

                    tbLayout.setVisibility(View.GONE);
                    CTCLayout.setVisibility(View.VISIBLE);
                    fName = "client_hiv_referral_form";

                }else if((spinnerService.getSelectedItem().toString()).equals("Rufaa kwenda kliniki kutibiwa malaria") ){

                    fragmentView.findViewById(R.id.checkboxis_at_hot_spot).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxWeightLoss).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxBloodCough).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxhas_symptomps_for_associative_diseases).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxhas_affected_partner).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxLostFollowup).setVisibility(View.GONE);
                    checkBoxAreasonOne = (CheckBox) fragmentView.findViewById(R.id.checkboxfever);
                    checkBoxreasonTwo = (CheckBox) fragmentView.findViewById(R.id.checkboxHasHeadache);
                    checkBoxreasonThree = (CheckBox) fragmentView.findViewById(R.id.checkboxSevereSweating);
                    checkBoxreasonFour = (CheckBox) fragmentView.findViewById(R.id.checkboxIsVomiting);
                    checkBoxresonFive = (CheckBox) fragmentView.findViewById(R.id.checkboxhas_loss_of_appetite);
                    checkBoxAreasonOne.setVisibility(View.VISIBLE);
                    checkBoxreasonTwo.setVisibility(View.VISIBLE);
                    checkBoxreasonThree.setVisibility(View.VISIBLE);
                    checkBoxreasonFour.setVisibility(View.VISIBLE);
                    checkBoxresonFive.setVisibility(View.VISIBLE);
                    tbLayout.setVisibility(View.GONE);
                    CTCLayout.setVisibility(View.VISIBLE);
                    fName = "client_malaria_referral_form";

                }else{
                    fragmentView.findViewById(R.id.checkboxis_at_hot_spot).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxIsVomiting).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxHasHeadache).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxWeightLoss).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxSevereSweating).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxhas_loss_of_appetite).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxIsVomiting).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxhas_symptomps_for_associative_diseases).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxhas_affected_partner).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.checkboxLostFollowup).setVisibility(View.GONE);
                    tbLayout.setVisibility(View.GONE);
                    CTCLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerFacility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= 0) {
                    spinnerFacility.setFloatingLabelText("Jina la kliniki aliyoshauriwa kwenda");
                    facilitySelection = i;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerService.setSelection(clientServiceSelection);
        spinnerFacility.setSelection(facilitySelection);


        radioGroupGender = (RadioGroup) fragmentView.findViewById(R.id.radioGroupGender);

        // initialize date to today's date
        textDate.setText(dateFormat.format(today.getTimeInMillis()));

        layoutDatePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pick date
                pickDate(R.id.textDate);
            }
        });
        dobTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pick date
                pickDate(R.id.reg_dob);
            }
        });







        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormSubmissionOk()) {
                    //setting default values
                    clientReferral = getClientReferral();
                    clientReferral.setStatus("0");

                    // convert to json
                    String gsonReferral = gson.toJson(clientReferral);
                    Log.d(TAG, "referral = " + gsonReferral);

                    // todo start form submission

                    ((SecuredNativeSmartRegisterActivity) getActivity()).saveFormSubmission(gsonReferral, recordId, formName, getFormFieldsOverrides());
                    getActivity().finish();
                }

            }
        });

        return fragmentView;
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
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
    protected void startRegistration() {

    }


    private void pickDate(final int id) {
        // listener
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                // get picked date
                // update view
                GregorianCalendar pickedDate = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                if (id == R.id.textDate)
                    textDate.setText(dateFormat.format(pickedDate.getTimeInMillis()));
                if (id == R.id.reg_dob)
                    dobTextView.setText(dateFormat.format(pickedDate.getTimeInMillis()));



            }
        };

        // dialog
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                onDateSetListener);

        datePickerDialog.setOkColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));
        datePickerDialog.setCancelColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_light));

        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_1);
        datePickerDialog.setAccentColor(ContextCompat.getColor(getContext(), R.color.primary));

        // show dialog
        datePickerDialog.show(getActivity().getFragmentManager(), "DatePickerDialog");
    }


    public boolean isFormSubmissionOk() {
        if (     TextUtils.isEmpty(editTextfName.getText())
                || TextUtils.isEmpty(editTextlName.getText())
                || TextUtils.isEmpty(editTextVillageLeader.getText())
                || TextUtils.isEmpty(editTextDiscountId.getText())
                ) {

            message = "Tafadhali jaza taarifa zote muhimu";
            makeToast();

            return false;

        } else if (radioGroupGender.getCheckedRadioButtonId() == -1) {
            // no radio checked
            message = "Tafadhali chagua jinsia ya mteja";
            makeToast();
            return false;

        } else if (spinnerService.getSelectedItemPosition() < 0 ||spinnerFacility.getSelectedItemPosition() < 0) {

            message = "Tafadhali chagua aina ya huduma";
            makeToast();
            return false;

        } else if (TextUtils.isEmpty(textPhone.getText())) {
            message = "Tafadhali andika namba ya simu sahihi";
            makeToast();
            return false;
        } else if (TextUtils.isEmpty(editTextKijiji.getText())) {
            message = "Tafadhali jaza mahali anapoishi";
            makeToast();
            return false;
        } else  if (TextUtils.isEmpty(editTextReferralReason.getText())) {
            message = "Tafadhali andika sababu ya rufaa ya mteja";
            makeToast();
            return false;
        }else
            // all good
            return true;
    }

    public ClientReferral getClientReferral() {
        ClientReferral referral = new ClientReferral();

        referral.setReferral_date(textDate.getText().toString());
        if((dobTextView.getText().toString()).equals("dd mmm yyyy")){
            int age = Integer.parseInt(editTextAge.getText().toString());
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int Byear = year - age;
            referral.setDate_of_birth("1 Jul "+Byear);

        }else{
            referral.setDate_of_birth(dobTextView.getText().toString());
        }

        referral.setCommunity_based_hiv_service(editTextDiscountId.getText().toString());
        referral.setFirst_name(editTextfName.getText().toString());
        referral.setMiddle_name(editTextmName.getText().toString());
        referral.setSurname(editTextlName.getText().toString());
        if(radioGroupGender.getCheckedRadioButtonId() == R.id.male)
            referral.setGender("Male");
        else
            referral.setGender("Female");
        referral.setVillage(editTextKijiji.getText().toString());
        referral.setIs_valid("true");
        referral.setPhone_number(textPhone.getText().toString());
        referral.setFacility_id(getFacilityId(spinnerFacility.getSelectedItem().toString()));
        referral.setFacility_id(spinnerFacility.getSelectedItem().toString());
        referral.setVillage_leader(editTextVillageLeader.getText().toString());
        referral.setReferral_reason(editTextReferralReason.getText().toString());
        referral.setReferral_service_id(getReferralServiceId(spinnerService.getSelectedItem().toString()));
        referral.setProviderMobileNumber(textviewReferralNumber.getText().toString());
        referral.setWard(wardId);
        referral.setService_provider_uiid(((BoreshaAfyaApplication)getActivity().getApplication()).getCurrentUserID());
        referral.setService_provider_group(((BoreshaAfyaApplication)getActivity().getApplication()).getTeam_uuid());

        //tb referral
        if(fName.equals("client_tb_referral_form")){
            referral.setCtc_number(editTextCTCNumber.getText().toString());
            referral.setHas_2Week_cough(checkBoxAreasonOne.isChecked());
            referral.setHas_fever(checkBoxreasonTwo.isChecked());
            referral.setHad_weight_loss(checkBoxreasonThree.isChecked());
            referral.setHas_severe_sweating(checkBoxreasonFour.isChecked());
            referral.setHas_blood_cough(checkBoxresonFive.isChecked());
            referral.setIs_lost_follow_up(checkBoxreasonSix.isChecked());
        }

        //for malaria referral
        if(fName.equals("client_malaria_referral_form")){
            referral.setCtc_number(editTextCTCNumber.getText().toString());
            referral.setHas_fever(checkBoxAreasonOne.isChecked());
            referral.setHas_headache(checkBoxreasonTwo.isChecked());
            referral.setHas_severe_sweating(checkBoxreasonThree.isChecked());
            referral.setIs_vomiting(checkBoxreasonFour.isChecked());
            referral.setHas_loss_of_appetite(checkBoxresonFive.isChecked());
//            referral.setIs_lost_follow_up(checkBoxreasonSix.isChecked());
        }

        //for hiv referral
        if(fName.equals("client_hiv_referral_form")){
            referral.setCtc_number(editTextCTCNumber.getText().toString());
            referral.setIs_at_hot_spot(checkBoxAreasonOne.isChecked());
            referral.setHas_fever(checkBoxreasonTwo.isChecked());
            referral.setHad_weight_loss(checkBoxreasonThree.isChecked());
            referral.setHas_symptomps_for_associative_diseases(checkBoxreasonFour.isChecked());
            referral.setHas_affected_partner(checkBoxresonFive.isChecked());
            referral.setIs_lost_follow_up(checkBoxreasonSix.isChecked());
            referral.setCtc_number(editTextCTCNumber.getText().toString());
            referral.setLast_ctc_date(dobTextView.getText().toString());
        }


        Log.d(TAG, "referral 1 ="+ new Gson().toJson(referral));
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

    public String getReferralServiceId(String name){
        cursor = commonRepository.RawCustomQueryForAdapter("select * FROM referral_service where name ='"+ name +"'");

        List<CommonPersonObject> commonPersonObjectList = commonRepository.readAllcommonForField(cursor, "referral_service");
        Log.d(TAG, "commonPersonList = " + gson.toJson(commonPersonObjectList));

        return commonPersonObjectList.get(0).getColumnmaps().get("id");
    }

    public void setRecordId(String recordId) {

        Log.d("TAG","record id = "+recordId);
        this.recordId = recordId;
    }

    public void setWardId(String locationId) {

        Log.d("TAG","ward id = "+locationId);
        this.wardId = locationId;
    }

    private void makeToast() {
        Toast.makeText(getActivity(),
                message,
                Toast.LENGTH_LONG).show();
    }

    //TODO martha Implement this method to initialize a form data
    public void setFormData(String data) {
        Log.d(TAG, "Setting form data");
//        ((SecuredNativeSmartRegisterActivity) getActivity()).saveFormSubmission(data, recordId, formName, getFormFieldsOverrides());
    }

    public void setFieldOverides(String overrides) {
        try {
            //get the field overrides map
            if (overrides != null) {
                JSONObject json = new JSONObject(overrides);
                String overridesStr = json.getString("fieldOverrides");
                this.fieldOverides = new JSONObject(overridesStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
