package com.datapath.ocds.kyrgyzstan.exporter.dao.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class JsonEvent {

    @Id
    private Integer id;
    private String dateCreated;
    private Integer objectId;
    private Integer orderId;
    private Integer type;

}
