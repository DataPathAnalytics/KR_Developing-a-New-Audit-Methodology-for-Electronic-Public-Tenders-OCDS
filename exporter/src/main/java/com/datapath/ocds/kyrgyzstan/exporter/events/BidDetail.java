package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

import java.util.List;

@Data
public class BidDetail {
    private Integer id;
    private String status;
    private String date;
    private String dateDisclosed;
    private List<Party> tenderers;
    private List<Lot> relatedLots;
    private List<PriceProposal> priceProposals;

}