package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

@Data
public class Address {

    private String ateCode;
    private String countryName;
    private String region;
    private String subRegion;
    private String district;
    private String subDistrict;
    private String subSubDistrict;
    private String locality;
    private String streetAddress;

}