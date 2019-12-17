package com.datapath.ocds.kyrgyzstan.api.events;

import lombok.Data;

import java.util.List;

@Data
public class TenderCancelledEvent extends Event {

    private Tender tender;
    private List<Award> awards;
    private List<Contract> contracts;

    @Data
    public static class Tender {
        private String id;
        private String status;
        private String currentStage;
        private String date;
        private List<Lot> lots;
        private List<Document> documents;
    }

    @Data
    public static class Lot {
        private String id;
        private String status;
    }

    @Data
    public static class Document {
        private String id;
        private String datePublished;
    }

    @Data
    public static class Award {
        private String id;
        private String status;
        private String date;
    }

    @Data
    public static class Contract {
        private String id;
        private String status;
        private String date;
    }

}