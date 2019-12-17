package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

import java.util.List;

@Data
public class Contract {

    private Long id;
    private String status;
    private String dateSigned;
    private String date;
    private List<Long> awardIDs;
    private Value value;
    private List<TenderItem> items;

}