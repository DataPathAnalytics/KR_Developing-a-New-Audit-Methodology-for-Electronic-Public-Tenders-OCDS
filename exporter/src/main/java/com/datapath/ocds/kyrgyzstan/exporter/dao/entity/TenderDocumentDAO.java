package com.datapath.ocds.kyrgyzstan.exporter.dao.entity;

import lombok.Data;

@Data
public class TenderDocumentDAO {

    private Integer id;
    private Integer relatedItem;
    private Integer relatedLot;
    private String title;

}