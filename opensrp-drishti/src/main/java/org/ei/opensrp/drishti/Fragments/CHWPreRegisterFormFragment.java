package org.ei.opensrp.drishti.Fragments;


import android.app.AlertDialog;
import android.content.Context;
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

import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.drishti.Application.UzaziSalamaApplication;
import org.ei.opensrp.drishti.DataModels.ClientReferral;
import org.ei.opensrp.drishti.DataModels.Facility;
import org.ei.opensrp.drishti.R;
import org.ei.opensrp.drishti.Repository.FacilityObject;
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
public class CHWPreRegisterFormFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {
    private static final String TAG = CHWPreRegisterFormFragment.class.getSimpleName();
    public static TextView textDate, textPhone;
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

    private LinearLayout CTCLayout,tbLayout;
    private List<String> facilityList = new ArrayList<>();
    private List<String> serviceList = new ArrayList<>();
    public String message = "";
    public static Context context;
    public static int clientServiceSelection = -1,facilitySelection = -1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private String  formName = "client_referral_form";
    private String recordId,wardId,fName ="";
    private ClientReferral clientReferral;
    private Gson gson = new Gson();
    private JSONObject fieldOverides = new JSONObject();
    private CommonRepository commonRepository;
    private Cursor cursor;
    private MaterialEditText dobTextView;
    private List<FacilityObject> facility = new ArrayList<>();

    public CHWPreRegisterFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        today = Calendar.getInstance();



        facilityList.add("facility A");
        facilityList.add("facility b");
        serviceList.add("Ushauri nasaha na kupima");
        serviceList.add("Rufaa kwenda kliniki ya TB na Matunzo (CTC)");
        serviceList.add("Rufaa kwenda kituo cha kutoa huduma za afya kutokana na magonjwa nyemelezi");
        serviceList.add("Kliniki ya kutibu kifua kikuu");
        serviceList.add("Rufaa kwenda kliniki ya kutibu Malaria");
        serviceList.add("Huduma za kuzuia maambukizi toka kwa mama kwenda mtoto");
        serviceList.add("Huduma ya afya ya uzazi na mtoto (RCH)");
        serviceList.add("Huduma ya Tohara (VMMC)");
        serviceList.add("Msaada wa kisheria");
        serviceList.add("Huduma za kuzuia ukatili wa kijinsia(Dawati la jinsia)");
        serviceList.add("Huduma za kuzuia maambukizi toka kwa mama kwenda mtoto");

        context = getContext();
    }

    @Override
    protected void onCreation() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.activity_chwedit_pre_registration, container, false);


        textDate = (TextView) fragmentView.findViewById(R.id.textDate);
        textPhone = (TextView) fragmentView.findViewById(R.id.textPhone);
        layoutDatePick = (LinearLayout) fragmentView.findViewById(R.id.layoutDatePick);
        dobTextView = (MaterialEditText) fragmentView.findViewById(R.id.reg_dob);
        tbLayout = (LinearLayout)fragmentView.findViewById(R.id.outlayer);
        CTCLayout = (LinearLayout)fragmentView.findViewById(R.id.ctc_number);


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
        textviewReferralProvider.setText(((UzaziSalamaApplication)getActivity().getApplication()).getUsername());
        textviewReferralProviderSupportGroup.setText(((UzaziSalamaApplication)getActivity().getApplication()).getTeam_name());


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

        checkBoxAreasonOne = (CheckBox) fragmentView.findViewById(R.id.checkbox2weekCough);
        checkBoxreasonTwo = (CheckBox) fragmentView.findViewById(R.id.checkboxfever);
        checkBoxreasonThree = (CheckBox) fragmentView.findViewById(R.id.checkboxWeightLoss);
        checkBoxreasonFour = (CheckBox) fragmentView.findViewById(R.id.checkboxSevereSweating);
        checkBoxresonFive = (CheckBox) fragmentView.findViewById(R.id.checkboxBloodCough);
        checkBoxreasonSix = (CheckBox) fragmentView.findViewById(R.id.checkboxLostFollowup);


        spinnerService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= 0) {
                    spinnerService.setFloatingLabelText("Aina za Huduma");
                    clientServiceSelection = i;
                }


                if((spinnerService.getSelectedItem().toString()).equals("Kliniki ya kutibu kifua kikuu")){
                    tbLayout.setVisibility(View.VISIBLE);
                    CTCLayout.setVisibility(View.VISIBLE);
                    fName = "client_tb_referral_form";

                }else if((spinnerService.getSelectedItem().toString()).equals("Rufaa kwenda kliniki ya TB na Matunzo (CTC)")||(spinnerService.getSelectedItem().toString()).equals("Huduma ya afya ya uzazi na mtoto (RCH)") ){
                    tbLayout.setVisibility(View.GONE);
                    CTCLayout.setVisibility(View.VISIBLE);
                    fName = "client_hiv_referral_form";

                }else{
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

        textPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show edit phone dialog
                showEditPhoneDialog();
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


    public void showEditPhoneDialog() {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_dialog_edit_phone, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);

        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        final EditText editTextPhone = (EditText) dialogView.findViewById(R.id.editTextLocation);
        // get previously entered location
        if (textPhone.getText() != null)
            editTextPhone.setText(textPhone.getText());

        // positive button
        dialogView.findViewById(R.id.textOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = editTextPhone.getText().toString();

                if (TextUtils.isEmpty(phone)) {
                    editTextPhone.setError("Please enter a valid phone number.");
                    return;
                }

                // update view
                textPhone.setText(phone);

                // close dialog
                dialog.dismiss();
            }
        });

        // negative button
        dialogView.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // close dialog
                dialog.dismiss();
            }
        });
    }

    public boolean isFormSubmissionOk() {
        if (     TextUtils.isEmpty(editTextfName.getText())
                || TextUtils.isEmpty(editTextlName.getText())
                || TextUtils.isEmpty(editTextKijiji.getText())
                || TextUtils.isEmpty(editTextReferralReason.getText())
                || TextUtils.isEmpty(editTextVillageLeader.getText())
                || TextUtils.isEmpty(textPhone.getText())
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

        }  else
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
        referral.setVillage_leader(editTextVillageLeader.getText().toString());
        referral.setReferral_reason(editTextReferralReason.getText().toString());
        referral.setReferral_service_id(spinnerService.getSelectedItem().toString());
        referral.setProviderMobileNumber(textviewReferralNumber.getText().toString());
        referral.setWard(wardId);
        referral.setService_provider_uiid(((UzaziSalamaApplication)getActivity().getApplication()).getCurrentUserID());
        referral.setService_provider_group(((UzaziSalamaApplication)getActivity().getApplication()).getTeam_uuid());
        if(fName.equals("client_tb_referral_form")){
            referral.setCtc_number(editTextCTCNumber.getText().toString());
            referral.setHas_2Week_cough(checkBoxAreasonOne.isChecked());
            referral.setHas_fever(checkBoxreasonTwo.isChecked());
            referral.setHad_weight_loss(checkBoxreasonThree.isChecked());
            referral.setHas_severe_sweating(checkBoxreasonFour.isChecked());
            referral.setHas_blood_cough(checkBoxresonFive.isChecked());
            referral.setIs_lost_follow_up(checkBoxreasonSix.isChecked());
        }
        if(fName.equals("client_hiv_referral_form")){
            referral.setCtc_number(editTextCTCNumber.getText().toString());
        }


        Log.d(TAG, "referral 1 ="+ new Gson().toJson(referral));
        return referral;
    }

    public JSONObject getFormFieldsOverrides() {
        return fieldOverides;
    }

    public String getFacilityId(String name){
//        commonRepository = context().commonrepository("facility");
//        //todo martha edit the query
//        cursor = commonRepository.RawCustomQueryForAdapter("select * FROM facility where Name ='"+name+"'" );
//
//        List<CommonPersonObject> commonPersonObjectList = commonRepository.readAllcommonForField(cursor, "facility");
//        Log.d(TAG, "commonPersonList = " + gson.toJson(commonPersonObjectList));
//
//        this.facility = Utils.convertToFacilityObjectList(commonPersonObjectList);
//        Log.d(TAG, "repo count = " + commonRepository.count() + ", list count = " + facility.size());
//        String id = facility.get(0).getId();
//        Log.d(TAG,"facility id selected ="+id);
        String id = name;
        return id;
    }

    public void setRecordId(String recordId) {

        Log.d("TAG","record id = "+recordId);
        this.recordId = recordId;
    }

    public void setWardId(String locationId) {

        Log.d("TAG","ward id = "+locationId);
        this.wardId = recordId;
    }
    private void makeToast() {
        Toast.makeText(context,
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
