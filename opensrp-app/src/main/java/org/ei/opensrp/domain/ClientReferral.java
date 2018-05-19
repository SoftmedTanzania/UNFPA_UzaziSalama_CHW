package org.ei.opensrp.domain;

import java.io.Serializable;

/**
 * Created by Coze on 05/19/18.
 */

public class ClientReferral implements Serializable {

    private String  referral_feedback, other_notes, referral_uuid, first_name, middle_name, surname, facility_id,
            referral_reason, service_provider_mobile_number, ward, village, map_cue, service_provider_uiid, phone_number,spouse_name;
    private long date_of_birth, referral_date,lmnp_date,edd;
    private int id,relationalid,gravida,para,referral_status,level_of_education,pmtct_status;
    private boolean height_below_average,is_valid;

    public ClientReferral() {

    }

    public String getReferral_feedback() {
        return referral_feedback;
    }

    public void setReferral_feedback(String referral_feedback) {
        this.referral_feedback = referral_feedback;
    }

    public String getOther_notes() {
        return other_notes;
    }

    public void setOther_notes(String other_notes) {
        this.other_notes = other_notes;
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

    public String getService_provider_uiid() {
        return service_provider_uiid;
    }

    public void setService_provider_uiid(String service_provider_uiid) {
        this.service_provider_uiid = service_provider_uiid;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public int getPmtct_status() {
        return pmtct_status;
    }

    public void setPmtct_status(int pmtct_status) {
        this.pmtct_status = pmtct_status;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRelationalid() {
        return relationalid;
    }

    public void setRelationalid(int relationalid) {
        this.relationalid = relationalid;
    }

    public int getGravida() {
        return gravida;
    }

    public void setGravida(int gravida) {
        this.gravida = gravida;
    }

    public int getPara() {
        return para;
    }

    public void setPara(int para) {
        this.para = para;
    }

    public int getReferral_status() {
        return referral_status;
    }

    public void setReferral_status(int referral_status) {
        this.referral_status = referral_status;
    }

    public int getLevel_of_education() {
        return level_of_education;
    }

    public void setLevel_of_education(int level_of_education) {
        this.level_of_education = level_of_education;
    }

    public boolean isHeight_below_average() {
        return height_below_average;
    }

    public void setHeight_below_average(boolean height_below_average) {
        this.height_below_average = height_below_average;
    }

    public boolean is_valid() {
        return is_valid;
    }

    public void setIs_valid(boolean is_valid) {
        this.is_valid = is_valid;
    }
}
