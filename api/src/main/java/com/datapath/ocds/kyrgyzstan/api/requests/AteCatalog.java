package com.datapath.ocds.kyrgyzstan.api.requests;

import lombok.Data;

@Data
public class AteCatalog {

    private Integer id;
    private String code;
    private String country;
    private String region;
    private String subRegion;
    private String district;
    private String subDistrict;
    private String subSubDistrict;
    private String locality;

}
