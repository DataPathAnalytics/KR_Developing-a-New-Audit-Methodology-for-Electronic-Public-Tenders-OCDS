package com.datapath.ocds.kyrgyzstan.exporter.dao.entity;

import lombok.Data;

@Data
public class AwardDAO {

    private Integer id;
    private String status;
    private Integer relatedLot;
    private Integer relatedBid;
    private Double amount;
    private Integer countryCode;

}