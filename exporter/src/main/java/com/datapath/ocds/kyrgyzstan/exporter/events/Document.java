package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

@Data
public class Document {

    private Long id;
    private String datePublished;
    private String documentType;
    private String title;

}