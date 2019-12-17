package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

import java.util.List;

@Data
public class Award {

    private Integer id;
    private String status;
    private List<Integer> relatedLots;
    private Integer relatedBid;
    private String date;
    private String datePublished;
    private Value value;

}