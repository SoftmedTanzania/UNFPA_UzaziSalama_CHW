package com.softmed.uzazi_salama;

import android.app.Activity;
import android.content.Intent;

import org.ei.opensrp.view.controller.ANMController;

public class NavigationController extends org.ei.opensrp.view.controller.NavigationController {
    private Activity activity;
    private ANMController anmController;

    public NavigationController(Activity activity, ANMController anmController) {
        super(activity,anmController);
        this.activity = activity;
        this.anmController = anmController;
    }

    @Override
    public void startCHWSmartRegistry() {
        activity.startActivity(new Intent(activity, ChwSmartRegisterActivity.class));
    }

    @Override
    public void startPNCActivity() {
    }

}
