package com.datapath.ocds.kyrgyzstan.exporter.dao.entity;

import lombok.Data;

@Data
public class EnquiryDAO {

    private Integer id;
    private String date;
    private String dateAnswered;
    private String answer;
    private String authorId;
    private String description;

}
