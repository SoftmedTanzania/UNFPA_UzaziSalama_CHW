package com.softmed.uzazi_salama.Repository;

import com.google.gson.Gson;

import org.ei.opensrp.domain.ClientReferral;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Kency on 14/09/2017.
 */

public class ClientReferralPersonObject {
    private String id, relationalId, first_name, middle_name, surname, community_based_hiv_service, ctc_number, is_valid, referral_reason, facility_id, referral_service_id, referral_status;
    private long referral_date;
    private String details;
    private Map<String, String> columnMap;

    public ClientReferralPersonObject(String id,
                                      String relationalId,
                                      String first_name,
                                      String middle_name,
                                      String surname,
                                      String community_based_hiv_service,
                                      String ctc_number,
                                      long referral_date,
                                      String facility_id,
                                      String ReferralReason,
                                      String referral_service_id,
                                      String referral_status,
                                      String is_valid,
                                      String details
                                      ) {
        this.details = details;
        this.id = id;
        this.referral_status = referral_status;
        this.community_based_hiv_service = community_based_hiv_service;
        this.ctc_number = ctc_number;
        this.relationalId = relationalId;
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.surname = surname;
        this.referral_date = referral_date;
        this.facility_id = facility_id;
        this.referral_reason = ReferralReason;
        this.referral_service_id = referral_service_id;
        this.is_valid = is_valid;
    }

    // alternative constructor so you don't pass bucha stuff, PregnantMom contains everything

    public ClientReferralPersonObject(String id, String relationalId, ClientReferral clientReferral) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyy", Locale.getDefault());

        this.id = id;
        this.relationalId = relationalId;
        this.first_name = clientReferral.getFirst_name();
        this.middle_name = clientReferral.getMiddle_name();
        this.surname = clientReferral.getSurname();
        this.referral_service_id = clientReferral.getReferral_service_id();
        this.community_based_hiv_service = clientReferral.getCommunity_based_hiv_service();
        this.ctc_number = clientReferral.getCtc_number();
        this.referral_date = clientReferral.getReferral_date();
        this.referral_reason = clientReferral.getReferral_reason();
        this.is_valid = clientReferral.getIs_valid();
        this.referral_status = clientReferral.getReferral_status();
        this.facility_id = clientReferral.getFacility_id();
        this.details = new Gson().toJson(clientReferral);

    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getReferral_status() {
        return referral_status;
    }

    public void setReferral_status(String referral_status) {
        this.referral_status = referral_status;
    }

    public String getCtc_number() {
        return ctc_number;
    }

    public void setCtc_number(String ctc_number) {
        this.ctc_number = ctc_number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelationalId() {
        return relationalId;
    }

    public void setRelationalId(String relationalId) {
        this.relationalId = relationalId;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getCommunity_based_hiv_service() {
        return community_based_hiv_service;
    }

    public void setCommunity_based_hiv_service(String community_based_hiv_service) {
        this.community_based_hiv_service = community_based_hiv_service;
    }

    public String getIs_valid() {
        return is_valid;
    }

    public void setIs_valid(String is_valid) {
        this.is_valid = is_valid;
    }

    public long getReferral_date() {
        return referral_date;
    }

    public void setReferral_date(long referral_date) {
        this.referral_date = referral_date;
    }

    public String getReferral_reason() {
        return referral_reason;
    }

    public void setReferral_reason(String referral_reason) {
        this.referral_reason = referral_reason;
    }

    public String getFacility_id() {
        return facility_id;
    }

    public void setFacility_id(String facility_id) {
        this.facility_id = facility_id;
    }

    public String getReferral_service_id() {
        return referral_service_id;
    }

    public void setReferral_service_id(String referral_service_id) {
        this.referral_service_id = referral_service_id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Map<String, String> getColumnMap() {
        return columnMap;
    }

    public void setColumnMap(Map<String, String> columnMap) {
        this.columnMap = columnMap;
    }
}