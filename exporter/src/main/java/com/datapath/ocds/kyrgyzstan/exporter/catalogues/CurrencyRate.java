package com.datapath.ocds.kyrgyzstan.exporter.catalogues;

import lombok.Data;

@Data
public class CurrencyRate {

    private String date;
    private String rate;
    private String code;
    private String name;

}
