package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

@Data
public class ContactPoint {

    private String name;
    private String phone;
    private String email;
    private String role;

}