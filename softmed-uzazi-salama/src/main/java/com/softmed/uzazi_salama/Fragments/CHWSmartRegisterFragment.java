package com.softmed.uzazi_salama.Fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.softmed.uzazi_salama.Application.BoreshaAfyaApplication;
import com.softmed.uzazi_salama.ChwSmartRegisterActivity;
import com.softmed.uzazi_salama.LoginActivity;
import com.softmed.uzazi_salama.NavigationController;
import com.softmed.uzazi_salama.R;
import com.softmed.uzazi_salama.Repository.LocationSelectorDialogFragment;
import com.softmed.uzazi_salama.pageradapter.CHWPagerAdapter;
import com.softmed.uzazi_salama.pageradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import com.softmed.uzazi_salama.util.FitDoughnut;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.service.PendingFormSubmissionService;
import org.ei.opensrp.sync.SyncAfterFetchListener;
import org.ei.opensrp.sync.SyncProgressIndicator;
import org.ei.opensrp.sync.UpdateActionsTask;
import org.ei.opensrp.view.activity.SecuredActivity;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.json.JSONObject;

import static android.os.Looper.getMainLooper;
import static android.widget.Toast.LENGTH_SHORT;
import static java.lang.String.valueOf;
import static org.ei.opensrp.event.Event.ACTION_HANDLED;
import static org.ei.opensrp.event.Event.FORM_SUBMITTED;
import static org.ei.opensrp.event.Event.SYNC_COMPLETED;
import static org.ei.opensrp.event.Event.SYNC_STARTED;

public class CHWSmartRegisterFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {
    SecuredActivity securedActivity;
    private static final String TAG = CHWSmartRegisterFragment.class.getSimpleName();
    private String locationDialogTAG = "locationDialogTAG";
    private JSONObject fieldOverides = new JSONObject();
    private TabLayout tabs;
    private LayoutInflater inflater;
    private ImageButton imageButton;
    private CHWPagerAdapter adapter;
    private ViewPager feeds;
    private Toolbar toolbar;
    private View v;
    private TextView pending;
    TextView successView, unsuccessView;
    Long success = (long) 0;
    Long unsuccess = (long) 0;
    private FitDoughnut donutChart;

    private android.content.Context context;
    private MenuItem updateMenuItem;
    private MenuItem remainingFormsToSyncMenuItem;
    private PendingFormSubmissionService pendingFormSubmissionService;
    static final String DATABASE_NAME = "drishti.db";
    RelativeLayout pendingForm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        navigationController = new NavigationController(getActivity(), anmController);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.inflater = inflater;
        v = inflater.inflate(R.layout.activity_chwregister, container, false);
        onInitialization();
        setHasOptionsMenu(true);

        imageButton = (ImageButton) v.findViewById(R.id.register_client);

        adapter = new CHWPagerAdapter(getActivity().getSupportFragmentManager());

        feeds = (ViewPager) v.findViewById(R.id.viewPager);
        feeds.setAdapter(adapter);

        feeds.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegistration();

            }
        });

        tabs = (TabLayout) v.findViewById(R.id.tabs);
        tabs.setupWithViewPager(feeds);


        LinearLayout tabLinearLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);

        TextView tabContent = (TextView) tabLinearLayout.findViewById(R.id.tabContent);
        tabContent.setText(R.string.received_referrals_label);
        tabContent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_trending_down_white_24dp, 0, 0, 0);



        LinearLayout tabLinearLayout2 = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        TextView tabContent2 = (TextView) tabLinearLayout2.findViewById(R.id.tabContent);
        tabContent2.setText(R.string.sent_referrals_label);
        tabContent2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_trending_up_white_24dp, 0, 0, 0);


        tabs.getTabAt(0).setCustomView(tabContent2);
        tabs.getTabAt(1).setCustomView(tabContent);


        final int colorWhite = ContextCompat.getColor(getActivity(), android.R.color.white);
        final int colorPrimaryLight = ContextCompat.getColor(getActivity(), R.color.primary_light);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getIcon() != null)
                    tab.getIcon().setColorFilter(colorWhite, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getIcon() != null)
                    tab.getIcon().setColorFilter(colorPrimaryLight, PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        toolbar = (Toolbar) v.findViewById(R.id.toolbar);


        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case com.softmed.uzazi_salama.R.id.updateMenuItem:
                                onOptionsItemSelected(item);
                                return true;
                            case com.softmed.uzazi_salama.R.id.switchLanguageMenuItem:
                                String newLanguagePreference = LoginActivity.switchLanguagePreference();
                                LoginActivity.setLanguage();
                                Toast.makeText(getActivity(), "Language preference set to " + newLanguagePreference + ". Please restart the application.", LENGTH_SHORT).show();
                                getActivity().recreate();
                                return true;
                            default:
                                return onOptionsItemSelected(item);
                        }
                    }
                });

        TextView username = (TextView) v.findViewById(R.id.toolbar_user_name);
        pendingForm = (RelativeLayout) v.findViewById(R.id.key_three);
        pending = (TextView) v.findViewById(R.id.count_three);
        username.setText(getResources().getString(R.string.logged_user)+" "+((BoreshaAfyaApplication)getActivity().getApplication()).getUsername());
        successView =  (TextView) v.findViewById(R.id.count_one);
        unsuccessView =  (TextView) v.findViewById(R.id.count_two);

        donutChart = (FitDoughnut) v.findViewById(R.id.donutChart);
        donutChart.startAnimateLoading();
        updateFromServer();

        return v;
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
        pendingFormSubmissionService = context().pendingFormSubmissionService();
        SYNC_STARTED.addListener(onSyncStartListener);
        SYNC_COMPLETED.addListener(onSyncCompleteListener);
        FORM_SUBMITTED.addListener(onFormSubmittedListener);
        ACTION_HANDLED.addListener(updateANMDetailsListener);
        updateRegisterCounts();
    }

    private Listener<Boolean> onSyncCompleteListener = new Listener<Boolean>() {
        @Override
        public void onEvent(Boolean data) {
            //#TODO: RemainingFormsToSyncCount cannot be updated from a back ground thread!!
            updateRemainingFormsToSyncCount();
            if (updateMenuItem != null) {
                updateMenuItem.setActionView(null);
            }
            updateRegisterCounts();
            refreshListView();
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

    protected void onResumption() {
        updateRegisterCounts();
        updateSyncIndicator();
        updateRemainingFormsToSyncCount();
        refreshListView();
    }

    public void updateRegisterCounts() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                final long successfullCount =  context().allBeneficiaries().successCount();
                final long unsuccessfullCount =  context().allBeneficiaries().unsuccessCount();

                Handler mainHandler = new Handler(getMainLooper());

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        successView =  (TextView) v.findViewById(R.id.count_one);
                        unsuccessView =  (TextView) v.findViewById(R.id.count_two);
                        successView.setText(valueOf(successfullCount));
                        unsuccessView.setText(valueOf(unsuccessfullCount));

                        donutChart.stopAnimateLoading(60.f);
                    }
                };
                mainHandler.post(myRunnable);

            }
        }).start();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }


    public void onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG,"am in preparationOptionMenu");
        super.onPrepareOptionsMenu(menu);
        updateMenuItem = menu.findItem(R.id.updateMenuItem);
        remainingFormsToSyncMenuItem = menu.findItem(R.id.remainingFormsToSyncMenuItem);

        updateRemainingFormsToSyncCount();

    }

    private Listener<Boolean> onSyncStartListener = new Listener<Boolean>() {
        @Override
        public void onEvent(Boolean data) {
            if (updateMenuItem != null) {
                updateMenuItem.setActionView(R.layout.progress);
            }
        }
    };

    public void updateFromServer() {
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(
                context, context().actionService(), context().formSubmissionSyncService(),
                new SyncProgressIndicator(), context().allFormVersionSyncService());
        updateActionsTask.updateFromServer(new SyncAfterFetchListener());

        updateRemainingFormsToSyncCount();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SYNC_STARTED.removeListener(onSyncStartListener);
        SYNC_COMPLETED.removeListener(onSyncCompleteListener);
        FORM_SUBMITTED.removeListener(onFormSubmittedListener);
        ACTION_HANDLED.removeListener(updateANMDetailsListener);
    }

    private void updateSyncIndicator() {

        if (updateMenuItem != null) {
            if (context().allSharedPreferences().fetchIsSyncInProgress()) {
                updateMenuItem.setActionView(R.layout.progress);
            } else
                updateMenuItem.setActionView(null);
        }

    }

    public void updateRemainingFormsToSyncCount() {


        long size = pendingFormSubmissionService.pendingFormSubmissionCount();
        Log.d(TAG,"pending from submission ="+size);
        if (size > 0) {
            pending.setText(valueOf(size) + " " + getString(R.string.unsynced_forms_count_message));
            pendingForm.setVisibility(View.VISIBLE);
        } else {
            pendingForm.setVisibility(View.GONE);
        }
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
    protected void onCreation() {

    }

    @Override
    public void refreshListView() {
        try {
            ReferredClientsFragment referredClientsFragment = (ReferredClientsFragment) findFragmentByPosition(0);
            referredClientsFragment.populateData();

        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            FollowupClientsFragment followupClientsFragment = (FollowupClientsFragment) findFragmentByPosition(1);
            followupClientsFragment.populateData();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public android.support.v4.app.Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = adapter;
        return getActivity().getSupportFragmentManager().findFragmentByTag("android:switcher:" + feeds.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.updateMenuItem:
                donutChart.startAnimateLoading();
                updateMenuItem = item;
                updateFromServer();
                if (context().allSharedPreferences().fetchIsSyncInProgress()) {
                    Log.d(TAG,"am in sync progress");
                    item.setActionView(R.layout.progress);
                } else{
                    item.setActionView(null);
                    Log.d(TAG,"am in sync progress after");}

                return true;

            case R.id.help:
                Toast.makeText(getActivity(), "help implementation under construction", LENGTH_SHORT).show();
                return true;

            case R.id.logout:
                ((BoreshaAfyaApplication)getActivity().getApplication()).logoutCurrentUser();

                return true;
            case R.id.switchLanguageMenuItem:
                String newLanguagePreference = context().userService().switchLanguagePreference();

                LoginActivity.setLanguage();
                Toast.makeText(getActivity(), "Language preference set to " + newLanguagePreference + ". Please restart the application.", LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
