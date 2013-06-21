package org.ei.drishti.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.ei.drishti.AllConstants;
import org.ei.drishti.domain.mapper.TTMapper;

import java.util.Map;

import static org.ei.drishti.AllConstants.ANCVisitFields.*;
import static org.ei.drishti.util.EasyMap.create;
import static org.ei.drishti.util.EasyMap.mapOf;

public class ServiceProvided {
    public static final String IFA_SERVICE_PROVIDED_NAME = "IFA";
    public static final String TT_1_SERVICE_PROVIDED_NAME = "TT 1";
    public static final String TT_2_SERVICE_PROVIDED_NAME = "TT 2";
    public static final String TT_BOOSTER_SERVICE_PROVIDED_NAME = "TT Booster";
    public static final String HB_TEST_SERVICE_PROVIDED_NAME = "Hb Test";
    public static final String ANC_SERVICE_PREFIX = "ANC ";
    public static final String PNC_SERVICE_PROVIDED_NAME = "PNC";
    public static final String ANC_1_SERVICE_PROVIDED_NAME = ANC_SERVICE_PREFIX + "1";
    public static final String ANC_2_SERVICE_PROVIDED_NAME = ANC_SERVICE_PREFIX + "2";
    public static final String ANC_3_SERVICE_PROVIDED_NAME = ANC_SERVICE_PREFIX + "3";
    public static final String ANC_4_SERVICE_PROVIDED_NAME = ANC_SERVICE_PREFIX + "4";

    private final String entityId;
    private final String name;
    private final String date;
    private final Map<String, String> data;

    public ServiceProvided(String entityId, String name, String date, Map<String, String> data) {
        this.name = name;
        this.date = date;
        this.data = data;
        this.entityId = entityId;
    }

    public static ServiceProvided forTTDose(String entityId, String ttDose, String date) {
        String mappedTTDose = TTMapper.valueOf(ttDose).value();
        return new ServiceProvided(entityId, mappedTTDose, date, mapOf("dose", mappedTTDose));
    }

    public static ServiceProvided forHBTest(String entityId, String hbLevel, String date) {
        return new ServiceProvided(entityId, HB_TEST_SERVICE_PROVIDED_NAME, date, mapOf("hbLevel", hbLevel));
    }

    public static ServiceProvided forIFATabletsGiven(String entityId, String numberOfIFATabletsGiven, String date) {
        return new ServiceProvided(entityId, IFA_SERVICE_PROVIDED_NAME, date, mapOf("dose", numberOfIFATabletsGiven));
    }

    public static ServiceProvided forANCCareProvided(String entityId, String ancVisitNumber, String date, String bpSystolic, String bpDiastolic, String weight) {
        return new ServiceProvided(entityId, ANC_SERVICE_PREFIX + ancVisitNumber, date,
                create(BP_SYSTOLIC, bpSystolic)
                        .put(BP_DIASTOLIC, bpDiastolic)
                        .put(WEIGHT, weight)
                        .map()
        );
    }

    public static ServiceProvided forMotherPNCVisit(String entityId, String pncVisitNumber, String date) {
        return new ServiceProvided(entityId, PNC_SERVICE_PROVIDED_NAME, date,
                mapOf(AllConstants.PNCVisitFields.PNC_VISIT_NUMBER, pncVisitNumber)
        );
    }

    public String name() {
        return name;
    }

    public String date() {
        return date;
    }

    public Map<String, String> data() {
        return data;
    }

    public String entityId() {
        return entityId;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
