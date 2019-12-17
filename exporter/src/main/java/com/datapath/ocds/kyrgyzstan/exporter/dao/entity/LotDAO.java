package com.datapath.ocds.kyrgyzstan.exporter.dao.entity;

import lombok.Data;

@Data
public class LotDAO {
    private Integer id;
    private Integer status;
    private Double amount;
    private String number;
}