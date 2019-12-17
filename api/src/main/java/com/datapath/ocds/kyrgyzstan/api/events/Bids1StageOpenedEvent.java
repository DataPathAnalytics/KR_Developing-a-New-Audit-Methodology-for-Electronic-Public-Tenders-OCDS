package com.datapath.ocds.kyrgyzstan.api.events;

import lombok.Data;

import java.util.List;

@Data
public class Bids1StageOpenedEvent extends Event {

    private List<Party> parties;
    private Tender tender;
    private Bids bids;

    @Data
    public static class Party {
        private String id;
        private Identifier identifier;
        private List<String> roles;
    }

    @Data
    public static class Identifier {
        private String id;
        private String scheme;
    }

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
        private List<Tenderer> tenderers;

    }

    @Data
    public static class Tenderer {
        private String id;
        private Identifier identifier;
    }


}
