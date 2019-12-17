package com.datapath.ocds.kyrgyzstan.api.events;

import lombok.Data;

import java.util.List;

@Data
public class ComplaintResolvedEvent extends Event {

    private List<Complaint> complaints;

    @Data
    public static class Complaint {
        private String id;
        private String status;
        private String responseDate;
        private String response;
        private List<Document> documents;
    }

    @Data
    public static class Document {
        private String id;
        private String datePublished;
        private String documentType;
        private String title;
    }

}