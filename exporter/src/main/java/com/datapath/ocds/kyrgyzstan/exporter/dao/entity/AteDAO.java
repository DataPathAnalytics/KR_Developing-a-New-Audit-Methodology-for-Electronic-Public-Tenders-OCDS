package com.datapath.ocds.kyrgyzstan.exporter.dao.entity;

import lombok.Data;

@Data
public class AteDAO {

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
