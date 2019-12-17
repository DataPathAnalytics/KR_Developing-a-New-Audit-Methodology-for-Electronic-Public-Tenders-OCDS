package com.datapath.ocds.kyrgyzstan.api.events;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CNAutoProlongedEvent extends Event {

    private Tender tender;

    @Data
    public static class Tender {
        private String id;
        private String currentStage;
        private Period tenderPeriod;
        private Period enquiryPeriod;
    }

}