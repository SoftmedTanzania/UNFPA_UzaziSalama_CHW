package com.softmed.uzazi_salama.Repository;

import com.google.gson.Gson;

import com.softmed.uzazi_salama.DataModels.FollowUp;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Kency on 14/09/2017.
 */

public class FollowUpPersonObject {
    private String id, relationalId, comment, token, follow_up_reason, is_valid, sponser_number, sponser_name;
    private long follow_up_date;
    private String details;
    private Map<String, String> columnMap;

    public FollowUpPersonObject(String id,
                                String relationalId,
                                String Comment,
                                String Token,
                                String sponser_name,
                                String sponser_number,
                                long follow_up_date,
                                String follow_up_reason,
                                String is_valid,
                                String details
    ) {
        this.details = details;
        this.id = id;
        this.comment = Comment;
        this.token = Token;
        this.sponser_name = sponser_name;
        this.sponser_number = sponser_number;
        this.follow_up_reason = follow_up_reason;
        this.follow_up_date = follow_up_date;
        this.relationalId = relationalId;
        this.is_valid = is_valid;
    }

    // alternative constructor so you don't pass bucha stuff, Follow Up contains everything

    public FollowUpPersonObject(String id, String relationalId, FollowUp followUp) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyy", Locale.getDefault());

        this.id = id;
        this.relationalId = relationalId;
        this.follow_up_date = followUp.getFollow_up_date();
        this.comment = followUp.getComment();
        this.follow_up_reason = followUp.getFollow_up_reason();
        this.token = followUp.getToken();
        this.sponser_number = followUp.getSponser_mobile_number();
        this.sponser_name = followUp.getSponser_name();
        this.is_valid = followUp.getIsValid();
        this.details = new Gson().toJson(followUp);

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSponser_number() {
        return sponser_number;
    }

    public void setSponser_number(String sponser_number) {
        this.sponser_number = sponser_number;
    }

    public String getSponser_name() {
        return sponser_name;
    }

    public void setSponser_name(String sponser_name) {
        this.sponser_name = sponser_name;
    }

    public long getFollow_up_date() {
        return follow_up_date;
    }

    public void setFollow_up_date(long follow_up_date) {
        this.follow_up_date = follow_up_date;
    }

    public String getFollow_up_reason() {
        return follow_up_reason;
    }

    public void setFollow_up_reason(String follow_up_reason) {
        this.follow_up_reason = follow_up_reason;
    }

    public String getIs_valid() {
        return is_valid;
    }

    public void setIs_valid(String is_valid) {
        this.is_valid = is_valid;
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