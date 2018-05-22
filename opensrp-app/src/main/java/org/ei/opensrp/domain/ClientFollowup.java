package org.ei.opensrp.domain;

import java.io.Serializable;

/**
 * Created by Coze on 05/19/18.
 */

public class ClientFollowup implements Serializable {

    public String  id,relationalid,referral_feedback, comment, referral_uuid, first_name, middle_name, surname, facility_id,
            referral_reason, service_provider_mobile_number, ward, village, map_cue, service_provider_uuid, phone_number,spouse_name,details;
    public long date_of_birth, referral_date,lmnp_date,edd;
    public boolean is_valid;
    public int referral_status;

    public ClientFollowup() {

    }

    public String getReferral_feedback() {
        return referral_feedback;
    }

    public void setReferral_feedback(String referral_feedback) {
        this.referral_feedback = referral_feedback;
    }



    public String getReferral_uuid() {
        return referral_uuid;
    }

    public void setReferral_uuid(String referral_uuid) {
        this.referral_uuid = referral_uuid;
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

    public String getFacility_id() {
        return facility_id;
    }

    public void setFacility_id(String facility_id) {
        this.facility_id = facility_id;
    }

    public String getReferral_reason() {
        return referral_reason;
    }

    public void setReferral_reason(String referral_reason) {
        this.referral_reason = referral_reason;
    }

    public String getService_provider_mobile_number() {
        return service_provider_mobile_number;
    }

    public void setService_provider_mobile_number(String service_provider_mobile_number) {
        this.service_provider_mobile_number = service_provider_mobile_number;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getMap_cue() {
        return map_cue;
    }

    public void setMap_cue(String map_cue) {
        this.map_cue = map_cue;
    }

    public String getService_provider_uuid() {
        return service_provider_uuid;
    }

    public void setService_provider_uuid(String service_provider_uuid) {
        this.service_provider_uuid = service_provider_uuid;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getSpouse_name() {
        return spouse_name;
    }

    public void setSpouse_name(String spouse_name) {
        this.spouse_name = spouse_name;
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

    public long getLmnp_date() {
        return lmnp_date;
    }

    public void setLmnp_date(long lmnp_date) {
        this.lmnp_date = lmnp_date;
    }

    public long getEdd() {
        return edd;
    }

    public void setEdd(long edd) {
        this.edd = edd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelationalid() {
        return relationalid;
    }

    public void setRelationalid(String relationalid) {
        this.relationalid = relationalid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getReferral_status() {
        return referral_status;
    }

    public void setReferral_status(int referral_status) {
        this.referral_status = referral_status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean is_valid() {
        return is_valid;
    }

    public void setIs_valid(boolean is_valid) {
        this.is_valid = is_valid;
    }
}
