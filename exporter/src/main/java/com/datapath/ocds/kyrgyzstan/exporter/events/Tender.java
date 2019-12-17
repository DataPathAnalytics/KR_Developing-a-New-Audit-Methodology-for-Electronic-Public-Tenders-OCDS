package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

import java.util.List;

@Data
public class Tender {

    private String id;
    private String status;
    private String currentStage;
    private String datePublished;
    private String datePublished2;
    private String date;
    private List<QualificationRequirement> qualificationRequirements;
    private List<Lot> lots;
    private List<TenderItem> items;
    private ConditionOfContract conditionOfContract;
    private List<TenderDocument> documents;
    private Period tenderPeriod;
    private Period enquiryPeriod;
    private Value value;
    private Guarantee guarantee;
    private String procurementMethod;
    private String procurementMethodDetails;
    private String procurementMethodRationale;
    private List<String> submissionMethod;
    private List<Enquiry> enquiries;
    private String number;

}