package com.softmed.uzazi_salama.Fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.domain.ClientReferral;
import com.softmed.uzazi_salama.ChwSmartRegisterActivity;
import com.softmed.uzazi_salama.R;
import com.softmed.uzazi_salama.Repository.ClientReferralPersonObject;
import com.softmed.uzazi_salama.Repository.LocationSelectorDialogFragment;
import com.softmed.uzazi_salama.pageradapter.ReferredClientsListAdapter;
import com.softmed.uzazi_salama.pageradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import com.softmed.uzazi_salama.util.AsyncTask;
import com.softmed.uzazi_salama.util.DividerItemDecoration;
import com.softmed.uzazi_salama.util.Utils;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import fr.ganfra.materialspinner.MaterialSpinner;


public class ReferredClientsFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {
    private CommonRepository commonRepository;
    private List<ClientReferralPersonObject> clientReferralPersonObjectList = new ArrayList<>();
    private Cursor cursor;
    private String locationDialogTAG = "locationDialogTAG";
    private static final String TAG = ReferredClientsFragment.class.getSimpleName(),
            TABLE_NAME = "client_referral";
    private long startDate = 0, endDate = 0;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private Gson gson = new Gson();
    private EditText fname, othername, ctc_number, textStartDate, textEndDate;
    public String message = "";
    MaterialSpinner spinnerType;
    private RecyclerView recyclerView;
    private ReferredClientsListAdapter clientsListAdapter;

    public ReferredClientsFragment() {
    }

    public static ReferredClientsFragment newInstance() {
        ReferredClientsFragment fragment = new ReferredClientsFragment();

        return fragment;
    }

    @Override
    protected void onCreation() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_refered_clients, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.clients_recycler);

        commonRepository = context().commonrepository("client_referral");
        //todo martha edit the query
        cursor = commonRepository.RawCustomQueryForAdapter("select * FROM " + TABLE_NAME);

        List<CommonPersonObject> commonPersonObjectList = commonRepository.readAllcommonForField(cursor, TABLE_NAME);

        Log.d(TAG,"clients_list : "+new Gson().toJson(commonPersonObjectList));

        clientReferralPersonObjectList = Utils.convertToClientReferralPersonObjectList(commonPersonObjectList);
        Log.d(TAG, "clientReferrallist = " + gson.toJson(commonPersonObjectList));
        clientsListAdapter = new ReferredClientsListAdapter(getActivity(), clientReferralPersonObjectList, commonRepository);
        Log.d(TAG, "repo count = " + commonRepository.count() + ", list count = " + clientReferralPersonObjectList.size());

        spinnerType = (MaterialSpinner) v.findViewById(R.id.spin_status);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        Button filter = (Button) v.findViewById(R.id.filter_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegistration();
            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isQueryInitializationOk()) {
                    StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ");

                    queryBuilder.append(TABLE_NAME);
                    new QueryTask().execute(
                            queryBuilder.toString(),
                            TABLE_NAME,
                            getFname(), getOthername(), getCTCNumber(), getFromDate(), getToDate(), isDateRangeSet());

                } else {
                    Log.d(TAG, "am in false else");
                    cursor = commonRepository.RawCustomQueryForAdapter("select * FROM " + TABLE_NAME + " where is_valid ='true'");
                    List<CommonPersonObject> commonPersonObjectList = commonRepository.readAllcommonForField(cursor, TABLE_NAME);
                    Log.d(TAG, "commonPersonList = " + gson.toJson(commonPersonObjectList));
                    clientReferralPersonObjectList = Utils.convertToClientReferralPersonObjectList(commonPersonObjectList);
                    ReferredClientsListAdapter pager = new ReferredClientsListAdapter(getActivity(), clientReferralPersonObjectList, commonRepository);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.addItemDecoration(
                            new DividerItemDecoration(getActivity(), null));
                    recyclerView.setAdapter(pager);
                }
            }
        });

        fname = (EditText) v.findViewById(R.id.client_name_et);
        othername = (EditText) v.findViewById(R.id.client_last_name_et);
        ctc_number = (EditText) v.findViewById(R.id.client_ctc_number_et);
        textStartDate = (EditText) v.findViewById(R.id.from_date);
        textEndDate = (EditText) v.findViewById(R.id.to_date);

        textStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate(0);
            }
        });
        textEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate(1);
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity(), null));

        recyclerView.setAdapter(clientsListAdapter);

        return v;
    }

    private String getFname() {
        String value = "";
        if (!(fname.getText().toString()).isEmpty()) {
            value = fname.getText().toString();
        }
        return value;
    }

    private String getOthername() {
        String value = "";
        if (!(othername.getText().toString()).isEmpty()) {
            value = othername.getText().toString();
        }
        return value;
    }

    private String getCTCNumber() {
        String value = "";
        if (!(ctc_number.getText().toString()).isEmpty()) {
            value = ctc_number.getText().toString();
        }
        return value;
    }

    private String getFromDate() {
        String value = "";
        if (!(textStartDate.getText().toString()).isEmpty()) {
            value = textStartDate.getText().toString();
        }
        return value;
    }

    private String getToDate() {
        String value = "";
        if (!(textEndDate.getText().toString()).isEmpty()) {
            value = textEndDate.getText().toString();
        }
        return value;
    }

    private void makeToast() {
        Toast.makeText(getActivity(),
                message,
                Toast.LENGTH_LONG).show();
    }

    private boolean isQueryInitializationOk() {
        if ((fname.getText().toString()).isEmpty() && (othername.getText().toString()).isEmpty() && (ctc_number.getText().toString()).isEmpty() && (textStartDate.getText().toString()).isEmpty() && (textEndDate.getText().toString()).isEmpty()) {
            // date range not defined properly
            message = "hujachagua kitu cha kutafuta";
            makeToast();
            return false;
        }

        return true;
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
    public void startRegistration() {
        Log.d(TAG, "starting registrations");
        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        Fragment prev = getActivity().getFragmentManager().findFragmentByTag(locationDialogTAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        LocationSelectorDialogFragment
                .newInstance((ChwSmartRegisterActivity) getActivity(), null, context().anmLocationController().get(),
                        "pregnant_mothers_pre_registration")
                .show(ft, locationDialogTAG);
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {

    }

    protected void populateData() {
        //todo need to select all mothers with usertype id similar to the logged chw user
        commonRepository = context().commonrepository("client_referral");

         Log.d(TAG,"am in refresh list view");
        cursor = commonRepository.RawCustomQueryForAdapter("select * FROM "+TABLE_NAME);

        List<CommonPersonObject> commonPersonObjectList = commonRepository.readAllcommonForField(cursor, TABLE_NAME);
        Log.d(TAG, "commonPersonList = " + gson.toJson(commonPersonObjectList));

        clientReferralPersonObjectList = Utils.convertToClientReferralPersonObjectList(commonPersonObjectList);
        Log.d(TAG, "repo count = " + commonRepository.count() + ", list count = " + clientReferralPersonObjectList.size());
        ReferredClientsListAdapter pager = new ReferredClientsListAdapter(getActivity(), clientReferralPersonObjectList,commonRepository);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity(), null));
        recyclerView.setAdapter(pager);
    }

    private String isDateRangeSet() {
        if ((startDate == 0 && endDate == 0))
            return "no";
        else
            return "yes";
    }

    private void pickDate(final int id) {
        // listener
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                // todo get picked date update view
                GregorianCalendar pickedDate = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                Log.d(TAG, "pickedDate = " + pickedDate.getTimeInMillis());

                if (id == 0) {
                    // start date
                    startDate = pickedDate.getTimeInMillis();
                    Log.d(TAG, "chosen date" + startDate);
                    textStartDate.setText(dateFormat.format(startDate));

                } else if (id == 1) {
                    // end date
                    endDate = pickedDate.getTimeInMillis();
                    textEndDate.setText(dateFormat.format(endDate));
                }
            }
        };

        // dialog
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                onDateSetListener);

        datePickerDialog.setOkColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));
        datePickerDialog.setCancelColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_light));

        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_1);
        datePickerDialog.setAccentColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        // show dialog
        datePickerDialog.show(getActivity().getFragmentManager(), "DatePickerDialog");
    }

    private class QueryTask extends AsyncTask<String, Void, List<ClientReferral>> {

        @Override
        protected List<ClientReferral> doInBackground(String... params) {
            publishProgress();
            String query = params[0];
            String tableName = params[1];
            String fName = params[2];
            String other_name = params[3];
            String ctc_number = params[4];
            String sdate = params[5];
            String edate = params[6];
            String daterange = params[7];
            Log.d(TAG, "query = " + query);
            Log.d(TAG, "tableName = " + tableName + ", parameter = " + fName + ", rangeisset = " + daterange);

            Context context = Context.getInstance().updateApplicationContext(getActivity().getApplicationContext());
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select * FROM "+tableName);

            List<CommonPersonObject> commonPersonObjectList = commonRepository.readAllcommonForField(cursor, tableName);


            // obtains client from result
            ClientReferral client = null;
            List<ClientReferral> ClientReferrals = new ArrayList<>();

            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            formatter.setLenient(false);

            try {
                for (CommonPersonObject commonPersonObject : commonPersonObjectList) {

                        // get anc mothers from query result and add them to list

                        // convert and add to list
                        if (!fName.isEmpty()) {
                            client = getclientReferral(commonPersonObject);
                            if ((client.getFirst_name().toLowerCase()).contains((fname.getText().toString()).toLowerCase()))
                                ClientReferrals.add(client);
                        } else if (!other_name.isEmpty()) {
                            client = getclientReferral(commonPersonObject);
                            if ((client.getMiddle_name().toLowerCase()).contains(other_name.toLowerCase())||(client.getSurname().toLowerCase()).contains(other_name.toLowerCase()))
                                ClientReferrals.add(client);
                        } else if (!ctc_number.isEmpty()) {
                            client = getclientReferral(commonPersonObject);
                            if ((client.getCtc_number().toLowerCase()).contains(ctc_number.toLowerCase()))
                                ClientReferrals.add(client);
                        } else if (!sdate.isEmpty()) {
                            client = getclientReferral(commonPersonObject);
                            if (startDate <= client.getReferral_date())
                                ClientReferrals.add(client);
                        } else if (!edate.isEmpty()) {
                            Log.d(TAG, "am in enddate");
                            client = getclientReferral(commonPersonObject);
                            if (endDate >= client.getReferral_date())
                                ClientReferrals.add(client);
                        }

                        cursor.moveToNext();
                    }

                    Log.d(TAG, "result client referral size" + ClientReferrals.size());
                    // check date range
                    if (daterange.equals("yes")) {
                        Log.d(TAG, "am in the date range");
                        for (ClientReferral clients : ClientReferrals) {
                            if (clients.getReferral_date() < startDate || clients.getReferral_date() > endDate)
                                ClientReferrals.remove(clients); // remove client referral
                        }
                    }

            } catch (Exception e) {
                Log.d(TAG, "error: " + e.getMessage());
                return null;

            } finally {
                cursor.close();
            }

            return ClientReferrals;
        }

        public ClientReferral getclientReferral(CommonPersonObject commonPersonObject){
            Log.d(TAG,"person  ="+gson.toJson(commonPersonObject));
            String details = Utils.convertStandardJSONString(commonPersonObject.getColumnmaps().get("details"));
            Log.d(TAG, "column details = " + details);
            ClientReferral client = null;
            client = gson.fromJson(details, ClientReferral.class);
            String id = commonPersonObject.getColumnmaps().get("id");
            String relationid = commonPersonObject.getColumnmaps().get("relationalid");
            String fname = commonPersonObject.getColumnmaps().get("first_name");
            String mname = commonPersonObject.getColumnmaps().get("middle_name");
            String facility_id = commonPersonObject.getColumnmaps().get("facility_id");
            Long referral_date = Long.parseLong(commonPersonObject.getColumnmaps().get("referral_date"));
            String referral_reason = commonPersonObject.getColumnmaps().get("referral_reason");
            String referral_service_id = commonPersonObject.getColumnmaps().get("referral_service_id");
            String surname = commonPersonObject.getColumnmaps().get("surname");
            String ctc_number = commonPersonObject.getColumnmaps().get("ctc_number");
            String community_based_hiv_service = commonPersonObject.getColumnmaps().get("community_based_hiv_service");


            client.setFirst_name(fname);
            client.setId(id);
            client.setCtc_number(ctc_number);
            client.setRelationalid(relationid);
            client.setMiddle_name(mname);
            client.setSurname(surname);
            client.setCommunity_based_hiv_service(community_based_hiv_service);
            client.setReferral_date(referral_date);
            client.setFacility_id(facility_id);
            client.setReferral_reason(referral_reason);
            client.setReferral_service_id(referral_service_id);
            client.setDetails(details);

            Log.d(TAG,"client gotten = "+new Gson().toJson(client));
            return  client;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<ClientReferral> resultList) {
            super.onPostExecute(resultList);

            if (resultList == null) {
                Log.d(TAG, "Query failed!");
            } else if (resultList.size() > 0) {
                Log.d(TAG, "resultList " + resultList.size() + "items");
                Log.d(TAG, "resultList " + new Gson().toJson(resultList));

                clientReferralPersonObjectList = Utils.convertToClientReferralList(resultList);
                ReferredClientsListAdapter pager = new ReferredClientsListAdapter(getActivity(), clientReferralPersonObjectList, commonRepository);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());

                recyclerView.addItemDecoration(
                        new DividerItemDecoration(getActivity(), null));
                recyclerView.setAdapter(pager);


            } else {
                Log.d(TAG, "Query result is empty!");
                message = "hakuna taarifa yoyote";
                makeToast();
                clientReferralPersonObjectList = Utils.convertToClientReferralList(resultList);
                ReferredClientsListAdapter pager = new ReferredClientsListAdapter(getActivity(), clientReferralPersonObjectList, commonRepository);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());

                recyclerView.addItemDecoration(
                        new DividerItemDecoration(getActivity(), null));
                recyclerView.setAdapter(pager);

            }
        }
    }
}
