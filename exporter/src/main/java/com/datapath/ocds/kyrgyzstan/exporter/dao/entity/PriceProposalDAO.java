package com.datapath.ocds.kyrgyzstan.exporter.dao.entity;

import lombok.Data;

@Data
public class PriceProposalDAO {
    private Integer id;
    private Integer relatedItem;
    private Integer relatedLot;
    private Double unitValueAmount;
    private String unitValueCurrency;
}