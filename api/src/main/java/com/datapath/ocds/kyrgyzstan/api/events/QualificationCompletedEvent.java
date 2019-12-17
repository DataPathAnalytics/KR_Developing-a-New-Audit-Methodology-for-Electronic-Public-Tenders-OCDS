package com.datapath.ocds.kyrgyzstan.api.events;

import lombok.Data;

import java.util.List;

@Data
public class QualificationCompletedEvent extends Event {

    private Tender tender;
    private Bids bids;

    @Data
    public static class Tender {
        private String id;
        private String currentStage;
    }

    @Data
    public static class Bids {
        private List<BidDetail> details;
    }

    @Data
    public static class BidDetail {
        private String id;
        private String status;
    }

}