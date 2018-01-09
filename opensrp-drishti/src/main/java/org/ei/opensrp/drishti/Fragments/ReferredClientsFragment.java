package org.ei.opensrp.drishti.Fragments;

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
import org.ei.opensrp.drishti.ChwSmartRegisterActivity;
import org.ei.opensrp.drishti.R;
import org.ei.opensrp.drishti.Repository.ClientReferralPersonObject;
import org.ei.opensrp.drishti.Repository.LocationSelectorDialogFragment;
import org.ei.opensrp.drishti.pageradapter.ReferredClientsListAdapter;
import org.ei.opensrp.drishti.pageradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.ei.opensrp.drishti.util.AsyncTask;
import org.ei.opensrp.drishti.util.DividerItemDecoration;
import org.ei.opensrp.drishti.util.Utils;
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
    private android.content.Context appContext;
    private List<ClientReferralPersonObject> clientReferralPersonObjectList = new ArrayList<>();
    private Cursor cursor, cursor2;
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
//        cursor = commonRepository.RawCustomQueryForAdapter("select * FROM "+TABLE_NAME+" where IS_VALID='true'" );

        List<CommonPersonObject> commonPersonObjectList = commonRepository.readAllcommonForField(cursor, TABLE_NAME);
        clientReferralPersonObjectList = Utils.convertToClientReferralPersonObjectList(commonPersonObjectList);

        clientsListAdapter = new ReferredClientsListAdapter(getActivity(), clientReferralPersonObjectList, commonRepository);

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
//                     execute query
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
//        if (spinnerType.getSelectedItemPosition() == 0) {
//            // nothing selected
//            message ="chagua succesfullness option.";
//            makeToast();
//            return false;
//
//        }

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
        cursor = commonRepository.RawCustomQueryForAdapter("select * FROM "+TABLE_NAME+" where is_valid ='true'" );

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
        datePickerDialog.setAccentColor(ContextCompat.getColor(getContext(), R.color.primary));

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
            CommonRepository commonRepository = context.commonrepository(tableName);
            Cursor cursor = commonRepository.RawCustomQueryForAdapter(query);

            // obtains client from result
            ClientReferral client = null;
            List<ClientReferral> ClientReferrals = new ArrayList<>();

            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            formatter.setLenient(false);

            try {
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        // get anc mothers from query result and add them to list
                        String details = cursor.getString(cursor.getColumnIndex("details"));
                        Log.d(TAG, "column details = " + details);
                        // convert and add to list
                        if (!fName.isEmpty()) {
                            client = gson.fromJson(details, ClientReferral.class);
                            if (client.getFirst_name().contains(fname.getText().toString()))
                                ClientReferrals.add(client);
                        } else if (!other_name.isEmpty()) {
                            client = gson.fromJson(details, ClientReferral.class);
                            if (client.getMiddle_name().contains(other_name)||client.getSurname().contains(other_name))
                                ClientReferrals.add(client);
                        } else if (!ctc_number.isEmpty()) {
                            client = gson.fromJson(details, ClientReferral.class);
                            if (client.getCtc_number().contains(ctc_number))
                                ClientReferrals.add(client);
                        } else if (!sdate.isEmpty()) {
                            client = gson.fromJson(details, ClientReferral.class);
                            if (startDate <= client.getReferral_date())
                                ClientReferrals.add(client);
                        } else if (!edate.isEmpty()) {
                            Log.d(TAG, "am in enddate");
                            client = gson.fromJson(details, ClientReferral.class);
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
                }
            } catch (Exception e) {
                Log.d(TAG, "error: " + e.getMessage());
                return null;

            } finally {
                cursor.close();
            }

            return ClientReferrals;
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
