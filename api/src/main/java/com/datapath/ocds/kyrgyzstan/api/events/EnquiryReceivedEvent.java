package com.datapath.ocds.kyrgyzstan.api.events;

import lombok.Data;

import java.util.List;

@Data
public class EnquiryReceivedEvent extends Event {

    private Tender tender;
    private List<Party> parties;

    @Data
    public static class Tender {
        private String id;
        private List<Enquiry> enquiries;
    }

    @Data
    public static class Enquiry {
        private String id;
        private String date;
        private String description;
        private Author author;
    }

    @Data
    public static class Author {
        private String id;
    }

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

}