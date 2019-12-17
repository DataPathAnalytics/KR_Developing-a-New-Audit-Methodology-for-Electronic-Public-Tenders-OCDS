package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

@Data
public class Unit {

    private Integer id;
    private String name;
    private Value value;

}