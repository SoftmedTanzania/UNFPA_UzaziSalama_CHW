package com.softmed.uzazi_salama.Repository;

import com.google.gson.Gson;

import org.ei.opensrp.domain.Facility;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Kency on 14/09/2017.
 */

public class FacilityObject {
    private String id, relationalId, Name;

    private String details;
    private Map<String, String> columnMap;

    public FacilityObject(String id,
                          String relationalId,
                          String name,
                          String details
                                      ) {
        this.details = details;
        this.id = id;
        this.Name = name;
        this.relationalId = relationalId;
    }

    // alternative constructor so you don't pass bucha stuff, PregnantMom contains everything

    public FacilityObject(String relationalId, Facility facility) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyy", Locale.getDefault());

        this.id = facility.getOpenMRSUIID();
        this.relationalId = relationalId;
        this.Name = facility.getFacilityName();
        this.details = new Gson().toJson(facility);

    }
    public FacilityObject(String id, String name) {

        this.id = id;
        this.Name = name;

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

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    }