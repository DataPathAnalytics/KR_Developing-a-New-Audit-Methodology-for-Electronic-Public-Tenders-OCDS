package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

@Data
public class Enquiry {

    private Integer id;
    private String dateAnswered;
    private String answer;
    private String date;
    private String description;
    private Author author;

}