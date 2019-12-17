package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

@Data
public class ConditionOfContract {
    private Integer id;
    private Double lateDeliveryRate;
    private Double latePaymentRate;
    private String lateGuaranteeRate;

    private Double guaranteePercent;
    private Double maxDeductibleAmountDelivery;
    private Double maxDeductibleAmountPayment;
    private Double maxDeductibleAmountGuarantee;

    private Boolean hasGuarantee;
    private Boolean hasInsurance;
    private Boolean hasRelatedServices;
    private Boolean hasSpares;
    private Boolean hasTechnicalControl;
    private Boolean hasPrepayment;
    private Boolean hasAcceptancePayment;
    private Boolean hasShipmentPayment;
    private Double prepaymentPercent;
    private Double acceptancePaymentPercent;
    private Double shipmentPaymentPercent;
    private String insuranceType;
    private Boolean hasArbitralTribunal;
}