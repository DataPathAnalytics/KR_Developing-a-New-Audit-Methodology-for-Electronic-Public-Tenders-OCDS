package com.datapath.ocds.kyrgyzstan.exporter.dao.entity;

import lombok.Data;

@Data
public class ComplaintDAO {

    private Integer id;
    private String reviewDate;
    private String responseDate;
    private String response;
    private String author;
    private String submissionDate;
    private String type;
    private String title;
    private String description;
    private Integer countryCode;

}