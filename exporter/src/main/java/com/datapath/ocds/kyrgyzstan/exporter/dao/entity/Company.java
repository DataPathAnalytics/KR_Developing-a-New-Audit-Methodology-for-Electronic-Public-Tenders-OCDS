package com.datapath.ocds.kyrgyzstan.exporter.dao.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Company {

    @Id
    private Integer id;
    private String inn;

}
