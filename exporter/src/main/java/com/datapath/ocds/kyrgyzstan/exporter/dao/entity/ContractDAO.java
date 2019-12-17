package com.datapath.ocds.kyrgyzstan.exporter.dao.entity;

import lombok.Data;

import javax.persistence.Id;

@Data
public class ContractDAO {

    @Id
    private Long id;
    private String dateSigned;
    private Double amount;

}