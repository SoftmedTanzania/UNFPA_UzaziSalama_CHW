package org.ei.drishti.domain;

import java.util.Map;

import static org.ei.drishti.domain.TimelineEvent.*;
import static org.ei.drishti.util.Log.logWarn;

public enum FPMethod {
    CONDOM {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return forFPCondomRenew(caseId, details);
        }

        @Override
        public String displayName() {
            return "Condom";
        }
    },
    IUD {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return forFPIUDRenew(caseId, details);
        }

        @Override
        public String displayName() {
            return "IUD";
        }
    },
    OCP {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return forFPOCPRenew(caseId, details);
        }

        @Override
        public String displayName() {
            return "OCP";
        }
    },
    DMPA {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return forFPDMPARenew(caseId, details);
        }

        @Override
        public String displayName() {
            return "DMPA/Injectable";
        }
    },
    MALE_STERILIZATION {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return null;
        }

        @Override
        public String displayName() {
            return "Male Sterilization";
        }
    },
    FEMALE_STERILIZATION {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return null;
        }

        @Override
        public String displayName() {
            return "Female Sterilization";
        }
    },
    ECP {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return null;
        }

        @Override
        public String displayName() {
            return "ECP";
        }
    },
    TRADITIONAL_METHODS {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return null;
        }

        @Override
        public String displayName() {
            return "Traditional Methods";
        }
    },
    LAM {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return null;
        }

        @Override
        public String displayName() {
            return "LAM";
        }
    },
    CENTCHROMAN {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return null;
        }

        @Override
        public String displayName() {
            return "Centchroman";
        }
    },
    NONE_PS {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return null;
        }

        @Override
        public String displayName() {
            return "None - PS";
        }
    },
    NONE_SS {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return null;
        }

        @Override
        public String displayName() {
            return "None - SS";
        }
    },
    NONE {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return null;
        }

        @Override
        public String displayName() {
            return "No FP";
        }
    };

    public abstract TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details);

    public abstract String displayName();

    public static FPMethod tryParse(String method, FPMethod defaultMethod) {
        try {
            return FPMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            logWarn("Unknown current FP method : " + method + " Exception : " + e);
            return defaultMethod;
        }
    }
}
