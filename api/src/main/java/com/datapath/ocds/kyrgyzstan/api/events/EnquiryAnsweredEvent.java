package com.datapath.ocds.kyrgyzstan.api.events;

import lombok.Data;

import java.util.List;

@Data
public class EnquiryAnsweredEvent extends Event {

    private Tender tender;

    @Data
    public static class Tender {
        private String id;
        private List<Enquiry> enquiries;
    }

    @Data
    public static class Enquiry {
        private String id;
        private String dateAnswered;
        private String answer;
    }

}