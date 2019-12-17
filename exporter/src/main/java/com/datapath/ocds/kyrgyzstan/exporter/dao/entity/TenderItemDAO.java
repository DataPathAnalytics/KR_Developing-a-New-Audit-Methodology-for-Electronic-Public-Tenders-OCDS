package com.datapath.ocds.kyrgyzstan.exporter.dao.entity;

import lombok.Data;

@Data
public class TenderItemDAO {

    private Integer id;
    private String relatedLot;
    private String classificationId;
    private Double quantity;
    private Integer unitId;
    private String unitName;
    private Double unitValueAmount;

}