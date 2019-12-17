package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

import java.util.List;

@Data
public class TenderDocument {
    private Integer id;
    private List<Integer> relatedLots;
    private Integer relatedItem;
    private String datePublished;
    private String documentType;
    private String title;

}