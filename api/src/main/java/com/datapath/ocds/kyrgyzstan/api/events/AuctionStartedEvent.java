package com.datapath.ocds.kyrgyzstan.api.events;

import lombok.Data;

@Data()
public class AuctionStartedEvent extends Event {

    private Tender tender;

    @Data
    public static class Tender {
        private String id;
        private String currentStage;
    }

}
