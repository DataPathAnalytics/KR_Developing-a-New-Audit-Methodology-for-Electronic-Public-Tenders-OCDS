package com.datapath.ocds.kyrgyzstan.api.events;

import lombok.Data;

import java.util.List;

@Data
public class ContractRegisteredEvent extends Event {

    private Tender tender;
    private List<Contract> contracts;

    @Data
    public static class Tender {
        private String id;
        private String status;
        private String currentStage;
        private String date;
        private List<Lot> lots;
    }

    @Data
    public static class Lot {
        private String id;
        private String status;
    }

    @Data
    public static class Contract {
        private String id;
        private String status;
        private String date;
        private String dateSigned;
        private List<Long> awardIDs;
        private List<Item> items;
        private Value value;
    }

    @Data
    public static class Item {
        private String id;
        private String relatedLot;
    }

    @Data
    public static class Value {
        private Double amount;
        private String currency;
    }

}
