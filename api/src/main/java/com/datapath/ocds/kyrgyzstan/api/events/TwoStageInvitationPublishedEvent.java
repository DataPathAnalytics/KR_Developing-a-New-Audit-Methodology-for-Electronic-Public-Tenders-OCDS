package com.datapath.ocds.kyrgyzstan.api.events;

import lombok.Data;

import java.util.List;

@Data
public class TwoStageInvitationPublishedEvent extends Event {

    private Tender tender;

    @Data
    public static class Tender {
        private String id;
        private String currentStage;
        private String datePublished2;
        private ConditionOfContract conditionOfContract;
        private List<TenderDocument> documents;
    }

    @Data
    public static class ConditionOfContract {
        private String id;
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

    @Data
    public static class TenderDocument {
        private String id;
        private String datePublished;
        private String documentType;
        private String title;
    }


}
