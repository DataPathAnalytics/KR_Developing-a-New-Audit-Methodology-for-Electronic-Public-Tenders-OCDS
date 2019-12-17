package com.datapath.ocds.kyrgyzstan.api.events;

import lombok.Data;

import java.util.List;

@Data
public class SupplierRefusedEvent extends Event {

    private List<Party> parties;
    private Tender tender;
    private Bids bids;
    private List<Award> awards;

    @Data
    public static class Party {
        private String id;
        private List<String> roles;
    }

    @Data
    public static class Tender {
        private String id;
        private String currentStage;
        private List<Lot> lots;
    }

    @Data
    public static class Lot {
        private String id;
        private String status;
    }

    @Data
    public static class Bids {
        private List<BidDetail> details;
    }

    @Data
    public static class BidDetail {
        private String id;
        private String status;
        private String date;
    }

    @Data
    public static class Award {
        private String id;
        private String status;
        private List<String> relatedLots;
        private String relatedBid;
        private Value value;
    }

}
