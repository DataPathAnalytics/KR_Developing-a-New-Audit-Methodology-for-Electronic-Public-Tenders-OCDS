package com.datapath.ocds.kyrgyzstan.exporter.dao.entity;

import lombok.Data;

@Data
public class CurrencyRateDAO {
    private String date;
    private String rate;
    private String code;
    private String name;
}