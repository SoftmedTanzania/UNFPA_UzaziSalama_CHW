package com.softmed.uzazi_salama.pageradapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import com.softmed.uzazi_salama.ChwSmartRegisterActivity;
import com.softmed.uzazi_salama.ClientsDetailsActivity;
import com.softmed.uzazi_salama.Fragments.ClientDetailFragment;
import com.softmed.uzazi_salama.R;
import com.softmed.uzazi_salama.Repository.ClientFollowupPersonObject;

import org.ei.opensrp.domain.ClientFollowup;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


/**
 * Created by martha on 8/22/17.
 */

public class CHWRegisterRecyclerAdapter extends
        RecyclerView.Adapter<CHWRegisterRecyclerAdapter.ViewHolder> {

    private static String TAG = CHWRegisterRecyclerAdapter.class.getSimpleName();
    private List<ClientFollowup> clients;
    private Context mContext;
    private boolean mTwoPane=true;

    public CHWRegisterRecyclerAdapter(Context context, List<ClientFollowup> clients) {
        this.clients = clients;
        this.mContext = context;


        Log.d(TAG,"follow up adapter constructor : "+new Gson().toJson(clients));
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View contactView = inflater.inflate(R.layout.card_chw_pre_reg, parent, false);


        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        final ClientFollowup client = clients.get(position);

        Log.d(TAG,"follow up adapter : "+new Gson().toJson(clients));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        // Set item views based on your views and data model
        TextView phoneNumberTextView = viewHolder.phoneNumberTextView;
        TextView CBHS = viewHolder.CBHSTextView;

        phoneNumberTextView.setText(client.getPhone_number());
        viewHolder.nameTextView.setText(client.getFirst_name()+" " + client.getMiddle_name()+", "+client.getSurname());


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {
                    ClientDetailFragment fragment = ClientDetailFragment.newInstance(client);
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ClientsDetailsActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("client_followup", client);
                    intent.putExtras(bundle);

                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return clients.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, phoneNumberTextView, CBHSTextView;
        Button details,followup;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name);
            phoneNumberTextView = (TextView) itemView.findViewById(R.id.phone_number);
            CBHSTextView = (TextView) itemView.findViewById(R.id.community_based_hiv_service);
            details = (Button) itemView.findViewById(R.id.button_details);
            followup = (Button) itemView.findViewById(R.id.button_followup);


            followup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ChwSmartRegisterActivity) mContext).showFollowUpFormDialog(clients.get(getAdapterPosition()));
                }
            });


        }

    }

    public void setIsInTwoPane(boolean mTwoPane){
        this.mTwoPane = mTwoPane;
    }
}