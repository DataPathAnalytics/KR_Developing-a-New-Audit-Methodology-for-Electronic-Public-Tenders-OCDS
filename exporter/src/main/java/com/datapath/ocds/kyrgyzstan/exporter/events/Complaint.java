package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

import java.util.List;

@Data
public class Complaint {

    private Integer id;
    private String status;
    private String reviewDate;
    private String responseDate;
    private String response;
    private String submissionDate;
    private String type;
    private String title;
    private String description;
    private List<Document> documents;
    private Author author;
}