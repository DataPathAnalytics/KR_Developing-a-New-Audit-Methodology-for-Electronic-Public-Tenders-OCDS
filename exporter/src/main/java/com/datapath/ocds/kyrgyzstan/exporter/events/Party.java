package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

import java.util.List;

@Data
public class Party {

    private String id;
    private Identifier identifier;
    private List<String> roles;
    private Address address;
    private List<ContactPoint> additionalContactPoints;

}
