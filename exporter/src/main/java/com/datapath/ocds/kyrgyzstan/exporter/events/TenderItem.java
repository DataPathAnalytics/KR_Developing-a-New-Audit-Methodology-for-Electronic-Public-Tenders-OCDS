package com.datapath.ocds.kyrgyzstan.exporter.events;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Classification;
import lombok.Data;

@Data
public class TenderItem {
    private Integer id;
    private String relatedLot;
    private Classification classification;
    private Double quantity;
    private Unit unit;

}