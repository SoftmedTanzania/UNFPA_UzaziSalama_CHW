package com.softmed.uzazi_salama;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonRepository;

import com.softmed.uzazi_salama.Fragments.ClientDetailFragment;
import com.softmed.uzazi_salama.Repository.ClientFollowupPersonObject;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class ClientsDetailsActivity extends SecuredNativeSmartRegisterActivity {
    private static final String TAG = ClientsDetailsActivity.class.getSimpleName();
    private  TextView name,age,gender,facility,feedback,contacts,sponsor,referedReason,residence,referedDate,note;
    private ClientFollowupPersonObject clientFollowupPersonObject;
    private CommonRepository commonRepository;
    private Cursor cursor;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    private Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.softmed.uzazi_salama.R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(com.softmed.uzazi_salama.R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(com.softmed.uzazi_salama.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ClientDetailFragment.CLIENT_FOLLOWUP,
                    getIntent().getStringExtra(ClientDetailFragment.CLIENT_FOLLOWUP));
            ClientDetailFragment fragment = new ClientDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(com.softmed.uzazi_salama.R.id.item_detail_container, fragment)
                    .commit();
        }

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        clientFollowupPersonObject = (ClientFollowupPersonObject) bundle.getSerializable("client_followup");


        name = (TextView) findViewById(com.softmed.uzazi_salama.R.id.name);
        contacts = (TextView)   findViewById(com.softmed.uzazi_salama.R.id.contacts);
        sponsor = (TextView)   findViewById(com.softmed.uzazi_salama.R.id.sponsor);
        residence = (TextView)   findViewById(com.softmed.uzazi_salama.R.id.residence);
        age = (TextView)   findViewById(com.softmed.uzazi_salama.R.id.age);
        gender = (TextView)   findViewById(com.softmed.uzazi_salama.R.id.gender);
        facility = (TextView)   findViewById(com.softmed.uzazi_salama.R.id.refered);
        referedDate = (TextView)   findViewById(com.softmed.uzazi_salama.R.id.refered_date);
        referedReason = (TextView)   findViewById(com.softmed.uzazi_salama.R.id.followUp_reason);
        feedback = (TextView)   findViewById(com.softmed.uzazi_salama.R.id.feedback);
        note = (TextView)   findViewById(com.softmed.uzazi_salama.R.id.note);


        String reg_date = dateFormat.format(clientFollowupPersonObject.getDate_of_birth());
        String ageS="";
        try {
            Date d = dateFormat.parse(reg_date);
            Calendar cal = Calendar.getInstance();
            Calendar today = Calendar.getInstance();
            cal.setTime(d);

            int age = today.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
            Integer ageInt = new Integer(age);
            ageS = ageInt.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if((clientFollowupPersonObject.getGender()).equalsIgnoreCase(getResources().getString(com.softmed.uzazi_salama.R.string.female))){
            gender.setText(getResources().getString(com.softmed.uzazi_salama.R.string.female));
        }
        else     {
            gender.setText(getResources().getString(com.softmed.uzazi_salama.R.string.male));
        }
        age.setText(ageS + " years");
        name . setText(clientFollowupPersonObject.getFirst_name()+" "+clientFollowupPersonObject.getMiddle_name()+", "+ clientFollowupPersonObject.getSurname());
        contacts.setText(clientFollowupPersonObject.getPhone_number());
        facility.setText(getFacilityName(clientFollowupPersonObject.getFacility_id()));
        referedReason.setText(getFacilityName(clientFollowupPersonObject.getReferral_reason()));
        referedDate.setText(dateFormat.format(clientFollowupPersonObject.getReferral_date()));
//        residence.setText(clientFollowupPersonObject.getVillage()+" M/kiti -:"+clientFollowupPersonObject.getVillage_leader());
        note.setText("-");

        setLanguage();
    }

    public String getFacilityName(String id){

        commonRepository = context().commonrepository("facility");
        cursor = commonRepository.RawCustomQueryForAdapter("select * FROM facility where id ='"+ id +"'");

        List<CommonPersonObject> commonPersonObjectList = commonRepository.readAllcommonForField(cursor, "facility");

        return commonPersonObjectList.get(0).getColumnmaps().get("name");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
//            navigateUpTo(new Intent(this, ItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    public static void setLanguage() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(Context.getInstance().applicationContext()));
        String preferredLocale = allSharedPreferences.fetchLanguagePreference();


        android.util.Log.d(TAG,"set Locale : "+preferredLocale);

        Resources res = Context.getInstance().applicationContext().getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(preferredLocale);
        res.updateConfiguration(conf, dm);

    }
}
