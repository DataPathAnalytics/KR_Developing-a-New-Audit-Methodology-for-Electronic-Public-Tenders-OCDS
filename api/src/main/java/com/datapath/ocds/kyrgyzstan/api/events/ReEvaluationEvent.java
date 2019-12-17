package com.datapath.ocds.kyrgyzstan.api.events;

import lombok.Data;

import java.util.List;

@Data
public class ReEvaluationEvent extends Event {

    private Tender tender;
    private List<Contract> contracts;

    @Data
    public static class Tender {
        private String id;
        private String status;
        private String currentStage;

    }

    @Data
    public static class Contract {
        private String id;
        private String status;
    }

}
