package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

@Data
public class PriceProposal {
    private Integer id;
    private Integer relatedItem;
    private Integer relatedLot;
    private Unit unit;
}