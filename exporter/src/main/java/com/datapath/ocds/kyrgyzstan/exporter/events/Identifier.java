package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

@Data
public class Identifier {

    private String id;
    private String scheme;
    private String legalName;
    private String legalName_ru;
    private String legalName_kg;

}