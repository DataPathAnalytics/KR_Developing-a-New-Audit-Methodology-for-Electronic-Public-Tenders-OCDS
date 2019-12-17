package com.datapath.ocds.kyrgyzstan.api.events;

import lombok.Data;

import java.util.List;

@Data
public class ComplaintReceivedEvent extends Event {

    private List<Complaint> complaints;
    private List<Party> parties;

    @Data
    public static class Complaint {
        private String id;
        private String status;
        private String submissionDate;
        private String type;
        private String title;
        private String description;
        private Author author;
        private List<Document> documents;
    }

    @Data
    public static class Document {
        private String id;
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
        private Address address;
        private List<ContactPoint> additionalContactPoints;
    }

    @Data
    public static class Identifier {
        private String id;
        private String scheme;
        private String legalName;
        private String legalName_ru;
        private String legalName_kg;
    }

    @Data
    public static class Address {
        private String ateCode;
        private String countryName;
        private String region;
        private String subRegion;
        private String district;
        private String subDistrict;
        private String subSubDistrict;
        private String locality;
        private String streetAddress;
    }

    @Data
    public static class ContactPoint {
        private String name;
        private String phone;
        private String email;
        private String role;
    }

}