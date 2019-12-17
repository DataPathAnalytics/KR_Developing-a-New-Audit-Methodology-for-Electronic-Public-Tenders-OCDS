package com.datapath.ocds.kyrgyzstan.exporter.dao.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "orders")
@Data
public class Order {

    @Id
    private Integer id;
    private Integer companyId;
    private Integer procurementMethod;
    private Integer status;
    private String datePublished;
    @Column(name = "pre_qualification")
    private Boolean preQualification;
    private String dateContest;
    private Double totalSum;
    private Double warrantyProvision;
    private Boolean allowMonetaryValue;

}