package com.datapath.ocds.kyrgyzstan.exporter.dao.entity;

import lombok.Data;

@Data
public class PartyDAO {

    private String id;
    private String role;
    private String nameEn;
    private String nameRu;
    private String nameKg;
    private String ateCode;
    private Integer countryCode;
    private String countryName;
    private String region;
    private String subRegion;
    private String district;
    private String subDistrict;
    private String subSubDistrict;
    private String locality;
    private String streetAddress;

}
