package com.datapath.ocds.kyrgyzstan.exporter.dao.entity;

import lombok.Data;

@Data
public class BidDetailDAO {

    private Integer id;
    private String status;
    private String date;
    private String tendererId;
    private Integer countryCode;

}