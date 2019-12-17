package com.datapath.ocds.kyrgyzstan.exporter.dao.entity;

import lombok.Data;

@Data
public class CNPublishedDAO {

    private String tenderId;
    private Integer currentStage;
    private String datePublished;
    private String procurementMethod;
    private String procurementMethodDetails;
    private String procurementMethodRationale;
    private String submissionMethod;
    private Double valueAmount;
    private String valueCurrency;
    private String tenderStartDate;
    private String tenderEndDate;


}
