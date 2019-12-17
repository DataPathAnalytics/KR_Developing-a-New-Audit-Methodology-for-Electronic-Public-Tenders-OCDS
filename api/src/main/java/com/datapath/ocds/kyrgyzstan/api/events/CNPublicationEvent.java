package com.datapath.ocds.kyrgyzstan.api.events;

import lombok.Data;

import java.util.List;

@Data
public class CNPublicationEvent extends Event {

    private List<Party> parties;
    private List<RelatedProcess> relatedProcesses;
    private List<Identifier> biddersLimitations;
    private Tender tender;

    @Data
    public static class Party {
        private String id;
        private Identifier identifier;
        private List<String> roles;
        private Address address;
        private List<ContactPoint> additionalContactPoints;
    }

    @Data
    public static class Identifier {
        private String id;
        private String scheme;
        private String legalName;
        private String legalName_ru;
        private String legalName_kg;
    }

    @Data
    public static class Address {
        private String ateCode;
        private String countryName;
        private String region;
        private String subRegion;
        private String district;
        private String subDistrict;
        private String subSubDistrict;
        private String locality;
        private String streetAddress;
    }

    @Data
    public static class ContactPoint {
        private String name;
        private String phone;
        private String email;
        private String role;
    }

    @Data
    public static class RelatedProcess {
        private String identifier;
        private List<String> relationship;
    }

    @Data
    public static class Tender {
        private String id;
        private String status;
        private String currentStage;
        private String datePublished;
        private String date;
        private String procurementMethod;
        private String procurementMethodDetails;
        private String procurementMethodRationale;
        private List<String> submissionMethod;
        private Value value;
        private Guarantee guarantee;
        private Period tenderPeriod;
        private Period enquiryPeriod;
        private List<QualificationRequirement> qualificationRequirements;
        private List<Lot> lots;
        private List<TenderItem> items;
        private ConditionOfContract conditionOfContract;
        private List<TenderDocument> documents;
        private String number;
    }

    @Data
    public static class Period {
        private String startDate;
        private String endDate;
    }

    @Data
    public static class Value {
        private Double amount;
        private String currency;
    }

    @Data
    public static class Guarantee {
        private Double amount;
        private Boolean monetary;
    }

    @Data
    public static class QualificationRequirement {
        private String id;
        private String type;
    }

    @Data
    public static class Lot {
        private String id;
        private String status;
        private Value value;
        private String number;

    }

    @Data
    public static class TenderItem {
        private String id;
        private String relatedLot;
        private Classification classification;
        private Double quantity;
        private Unit unit;
    }

    @Data
    public static class Unit {
        private String id;
        private String name;
        private Value value;
    }

    @Data
    public static class Classification {
        private String id;
        private String scheme;
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
        private List<String> relatedLots;
        private String relatedItem;
        private String datePublished;
    }

}
