package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

@Data
public class Lot {
    private Integer id;
    private String status;
    private Value value;
    private String number;
}