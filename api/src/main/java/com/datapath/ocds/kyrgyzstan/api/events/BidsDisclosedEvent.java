package com.datapath.ocds.kyrgyzstan.api.events;

import lombok.Data;

import java.util.List;

@Data
public class BidsDisclosedEvent extends Event {

    private List<Party> parties;
    private Tender tender;
    private Bids bids;

    @Data
    public static class Party {
        private String id;
        private Identifier identifier;
        private List<String> roles;
        private Address address;
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
        private String date;
        private String dateDisclosed;
        private List<Party> tenderers;
        private List<Lot> relatedLots;
        private List<PriceProposal> priceProposals;
    }

    @Data
    public static class Lot {
        private String id;
        private Value value;
    }

    @Data
    public static class PriceProposal {
        private String id;
        private String relatedItem;
        private String relatedLot;
        private Unit unit;
    }

}