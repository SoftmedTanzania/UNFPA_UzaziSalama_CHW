package org.ei.opensrp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.ei.opensrp.AllConstants;
import org.ei.opensrp.Context;
import org.ei.opensrp.R;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.ei.opensrp.view.activity.FormActivity;
import org.ei.opensrp.view.activity.LoginActivity;
import org.ei.opensrp.view.activity.MicroFormActivity;
import org.ei.opensrp.view.activity.SecuredActivity;
import org.ei.opensrp.view.controller.ANMController;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.controller.NavigationController;

import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;
import static org.ei.opensrp.AllConstants.ENTITY_ID_PARAM;
import static org.ei.opensrp.AllConstants.FIELD_OVERRIDES_PARAM;
import static org.ei.opensrp.AllConstants.FORM_NAME_PARAM;
import static org.ei.opensrp.AllConstants.FORM_SUCCESSFULLY_SUBMITTED_RESULT_CODE;
import static org.ei.opensrp.event.Event.ON_LOGOUT;

/**
 * Created by koros on 10/12/15.
 */
public abstract class SecuredFragment extends Fragment {

    protected Listener<Boolean> logoutListener;
    protected FormController formController;
    protected ANMController anmController;
    protected NavigationController navigationController;
    private String metaData;
    private boolean isPaused;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logoutListener = new Listener<Boolean>() {
            public void onEvent(Boolean data) {
                getActivity().finish();
            }
        };
        ON_LOGOUT.addListener(logoutListener);

        if (context().IsUserLoggedOut()) {
            DrishtiApplication application = (DrishtiApplication)this.getActivity().getApplication();
            application.logoutCurrentUser();
            return;
        }
        formController = new FormController((SecuredActivity)getActivity());
        anmController = context().anmController();
        navigationController = new NavigationController(getActivity(), anmController);
        onCreation();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (context().IsUserLoggedOut()) {
            DrishtiApplication application = (DrishtiApplication)this.getActivity().getApplication();
            application.logoutCurrentUser();
            return;
        }

        onResumption();
        isPaused = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        isPaused = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.switchLanguageMenuItem) {
            String newLanguagePreference = context().userService().switchLanguagePreference();
            Toast.makeText(getActivity(), "Language preference set to " + newLanguagePreference + ". Please restart the application.", LENGTH_SHORT).show();

            return super.onOptionsItemSelected(item);
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void logoutUser() {
        context().userService().logout();
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    protected abstract void onCreation();

    protected abstract void onResumption();

    public void startFormActivity(String formName, String entityId, String metaData) {
        launchForm(formName, entityId, metaData, FormActivity.class);
    }

    public void startMicroFormActivity(String formName, String entityId, String metaData) {
        launchForm(formName, entityId, metaData, MicroFormActivity.class);
    }

    private void launchForm(String formName, String entityId, String metaData, Class formType) {
        this.metaData = metaData;

        Log.d("softmed","opening form name = "+formName);

        Intent intent = new Intent(getActivity(), formType);
        intent.putExtra(FORM_NAME_PARAM, formName);
        intent.putExtra(ENTITY_ID_PARAM, entityId);
        addFieldOverridesIfExist(intent);
        startActivityForResult(intent, FORM_SUCCESSFULLY_SUBMITTED_RESULT_CODE);
    }

    private void addFieldOverridesIfExist(Intent intent) {
        if (hasMetadata()) {
            Map<String, String> metaDataMap = new Gson().fromJson(
                    this.metaData, new TypeToken<Map<String, String>>() {
                    }.getType());
            if (metaDataMap.containsKey(FIELD_OVERRIDES_PARAM)) {
                intent.putExtra(FIELD_OVERRIDES_PARAM, metaDataMap.get(FIELD_OVERRIDES_PARAM));
            }
        }
    }

    private boolean isSuccessfulFormSubmission(int resultCode) {
        return resultCode == AllConstants.FORM_SUCCESSFULLY_SUBMITTED_RESULT_CODE;
    }

    private boolean hasMetadata() {
        return this.metaData != null && !this.metaData.equalsIgnoreCase("undefined");
    }

    protected Context context() {
        return Context.getInstance().updateApplicationContext(this.getActivity().getApplicationContext());
    }

    public boolean isPaused() {
        return isPaused;
    }


}
