package com.datapath.ocds.kyrgyzstan.api.events;

import lombok.Data;

import java.util.List;

@Data
public class CNUpdatedEvent extends Event {

    private Tender tender;

    @Data
    public static class Tender {
        private String id;
        private String currentStage;
        private Value value;
        private Period tenderPeriod;
        private Period enquiryPeriod;
        private List<Lot> lots;
        private List<TenderItem> items;
        private List<TenderDocument> documents;
    }

    @Data
    public static class Lot {
        private String id;
        private Value value;
    }

    @Data
    public static class TenderItem {
        private String id;
        private Double quantity;
        private Unit unit;
    }

    @Data
    public static class TenderDocument {
        private String id;
        private String datePublished;
    }

}