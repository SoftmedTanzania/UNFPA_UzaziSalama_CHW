package com.softmed.uzazi_salama.Repository;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Kency on 14/09/2017.
 */

public class ClientFollowupPersonObject implements Serializable {
    private String id, relationalId, first_name, middle_name, surname, community_based_hiv_service, ctc_number,
            is_valid,
            referral_reason,
            gender,
            phone_number,
            care_taker_name,
            care_taker_name_phone_number,
            care_taker_relationship,
            facility_id,
            referral_service_id,
            village,
            referral_status;
    private long visit_date;
    private long date_of_birth;
    private long referral_date;



    private String details;
    private Map<String, String> columnMap;

    public ClientFollowupPersonObject(String id, String relationalId, String first_name, String middle_name, String surname, String community_based_hiv_service, String ctc_number, String is_valid, String referral_reason, String gender, String phone_number, String care_taker_name, String care_taker_name_phone_number, String care_taker_relationship, String facility_id, String referral_service_id, String referral_status, long visit_date, long date_of_birth,long referral_date,String village,String details) {
        this.id = id;
        this.relationalId = relationalId;
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.surname = surname;
        this.community_based_hiv_service = community_based_hiv_service;
        this.ctc_number = ctc_number;
        this.is_valid = is_valid;
        this.referral_reason = referral_reason;
        this.gender = gender;
        this.phone_number = phone_number;
        this.care_taker_name = care_taker_name;
        this.care_taker_name_phone_number = care_taker_name_phone_number;
        this.care_taker_relationship = care_taker_relationship;
        this.facility_id = facility_id;
        this.referral_service_id = referral_service_id;
        this.referral_status = referral_status;
        this.visit_date = visit_date;
        this.date_of_birth = date_of_birth;
        this.referral_date = referral_date;
        this.village = village;
        this.details = details;
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

    public String getCommunity_based_hiv_service() {
        return community_based_hiv_service;
    }

    public void setCommunity_based_hiv_service(String community_based_hiv_service) {
        this.community_based_hiv_service = community_based_hiv_service;
    }

    public String getCtc_number() {
        return ctc_number;
    }

    public void setCtc_number(String ctc_number) {
        this.ctc_number = ctc_number;
    }

    public String getIs_valid() {
        return is_valid;
    }

    public void setIs_valid(String is_valid) {
        this.is_valid = is_valid;
    }

    public String getReferral_reason() {
        return referral_reason;
    }

    public void setReferral_reason(String referral_reason) {
        this.referral_reason = referral_reason;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getCare_taker_name() {
        return care_taker_name;
    }

    public void setCare_taker_name(String care_taker_name) {
        this.care_taker_name = care_taker_name;
    }

    public String getCare_taker_name_phone_number() {
        return care_taker_name_phone_number;
    }

    public void setCare_taker_name_phone_number(String care_taker_name_phone_number) {
        this.care_taker_name_phone_number = care_taker_name_phone_number;
    }

    public String getCare_taker_relationship() {
        return care_taker_relationship;
    }

    public void setCare_taker_relationship(String care_taker_relationship) {
        this.care_taker_relationship = care_taker_relationship;
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

    public String getReferral_status() {
        return referral_status;
    }

    public void setReferral_status(String referral_status) {
        this.referral_status = referral_status;
    }

    public long getVisit_date() {
        return visit_date;
    }

    public void setVisit_date(long visit_date) {
        this.visit_date = visit_date;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(long date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public long getReferral_date() {
        return referral_date;
    }

    public void setReferral_date(long referral_date) {
        this.referral_date = referral_date;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public Map<String, String> getColumnMap() {
        return columnMap;
    }

    public void setColumnMap(Map<String, String> columnMap) {
        this.columnMap = columnMap;
    }
}