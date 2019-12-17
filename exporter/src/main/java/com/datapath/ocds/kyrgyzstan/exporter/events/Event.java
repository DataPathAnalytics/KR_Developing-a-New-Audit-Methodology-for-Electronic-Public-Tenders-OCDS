package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

import java.util.List;

@Data
public class Event {

    private String id;
    private String date;
    private String ocid;
    private List<String> tag;
    private String initiationType;
    private Integer eventType;
    private List<String> eventErrors;

    public void setSingleTag(String tag) {
        this.tag = List.of(tag);
    }

}