package com.softmed.uzazi_salama.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.ei.opensrp.commonregistry.CommonPersonObject;

import com.softmed.uzazi_salama.R;
import com.softmed.uzazi_salama.Repository.ClientFollowupPersonObject;
import com.softmed.uzazi_salama.pageradapter.CHWRegisterRecyclerAdapter;
import com.softmed.uzazi_salama.pageradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import com.softmed.uzazi_salama.util.Utils;

import org.ei.opensrp.domain.ClientFollowup;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.repository.ClientFollowupRepository;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;

import java.util.ArrayList;
import java.util.List;


public class FollowupClientsFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {
    private ClientFollowupRepository clientFollowupRepository;
    private Gson gson = new Gson();
    private android.content.Context appContext;
    private Cursor cursor;
    private boolean mTwoPane;
    private View v;
    private RecyclerView recyclerView;
    private static final String TAG = FollowupClientsFragment.class.getSimpleName();
    private String TABLE_NAME = "anc_followup_client";
    private List<ClientFollowup> clientFollowups = new ArrayList<>();;


    public FollowupClientsFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment ReferredClientsFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        v = inflater.inflate(R.layout.fragment_chwregistration, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.item_list);
        clientFollowupRepository = context().clientFollowupRepository();


        if (clientFollowupRepository != null) {
            try {
                clientFollowups = clientFollowupRepository.all();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        Log.d(TAG, "repo count = " + clientFollowupRepository.count() + ", list count = " + clientFollowups.size());
        ;
        Log.d(TAG, "followup commonPersonList = " + gson.toJson(clientFollowups));

        CHWRegisterRecyclerAdapter chwRegisterRecyclerAdapter = new CHWRegisterRecyclerAdapter(getActivity(),clientFollowups);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());


        if (v.findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp) or in landscape.
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            chwRegisterRecyclerAdapter.setIsInTwoPane(mTwoPane);
            chwRegisterRecyclerAdapter.notifyDataSetChanged();

            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }else{
            int numberOfColumns = 3;
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        recyclerView.setAdapter(chwRegisterRecyclerAdapter);

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

    }

    @Override
    protected void startRegistration() {

    }
    protected void populateData() {
        clientFollowupRepository = context().clientFollowupRepository();
        clientFollowups = clientFollowupRepository.all();


        Log.d(TAG, "followup commonPersonList = " + gson.toJson(clientFollowups));

        CHWRegisterRecyclerAdapter chwRegisterRecyclerAdapter = new CHWRegisterRecyclerAdapter(getActivity(),clientFollowups);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());


        if (v.findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp) or in landscape.
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            chwRegisterRecyclerAdapter.setIsInTwoPane(mTwoPane);
            chwRegisterRecyclerAdapter.notifyDataSetChanged();

            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }else{
            int numberOfColumns = 3;
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        recyclerView.setAdapter(chwRegisterRecyclerAdapter);
    }


}
