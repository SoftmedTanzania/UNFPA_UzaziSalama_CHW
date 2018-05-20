package com.softmed.uzazi_salama.pageradapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.softmed.uzazi_salama.ChwSmartRegisterActivity;
import com.softmed.uzazi_salama.R;

import org.ei.opensrp.domain.ClientReferral;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Coze on 05/19/18.
 */

public class ReferredClientsListAdapter extends
        RecyclerView.Adapter<ReferredClientsListAdapter.ViewHolder> {
    private static String TAG = ReferredClientsListAdapter.class.getSimpleName();
    private List<ClientReferral> clients = new ArrayList<>();;
    private Context mContext;

    public ReferredClientsListAdapter(Context context, List<ClientReferral> client) {
        clients = client;
        mContext = context;

    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View contactView = inflater.inflate(R.layout.referal_list_client_item, parent, false);


        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {


        ClientReferral client = clients.get(position);


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());


        viewHolder.nameTextView.setText(client.getFirst_name()+" "+client.getMiddle_name()+" "+client.getSurname());
        viewHolder.referralReason.setText(client.getReferral_reason());
        viewHolder.scheduleDateTextView.setText(dateFormat.format(client.getReferral_date()));


        if(client.getReferral_status() == 0){
            viewHolder.referralStatus.setText(R.string.pending_label);
            viewHolder.statusIcon.setBackgroundColor(mContext.getResources().getColor(R.color.blue_400));
        }else if(client.getReferral_status() == 1){
            viewHolder.referralStatus.setText(R.string.suceessful_label);
            viewHolder.statusIcon.setBackgroundColor(mContext.getResources().getColor(R.color.green_400));
        }else{
            viewHolder.referralStatus.setText(R.string.unsuccessful_label);
            viewHolder.statusIcon.setBackgroundColor(mContext.getResources().getColor(R.color.red_400));
        }


    }

    @Override
    public int getItemCount() {
        return clients.size();
//        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, referralReason, scheduleDateTextView, referralStatus;
        public View statusIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.client_name);
            referralReason = (TextView) itemView.findViewById(R.id.referral_reasons);
            referralStatus = (TextView) itemView.findViewById(R.id.status);
            scheduleDateTextView = (TextView) itemView.findViewById(R.id.ref_date);
            statusIcon = itemView.findViewById(R.id.status_color);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // pass client referral to show details
                    Log.d(TAG, "gsonReferral3 = " + new Gson().toJson(clients.get(getAdapterPosition())));
                    ((ChwSmartRegisterActivity) mContext).showPreRegistrationDetailsDialog(clients.get(getAdapterPosition()));
                }
            });


        }

    }


}