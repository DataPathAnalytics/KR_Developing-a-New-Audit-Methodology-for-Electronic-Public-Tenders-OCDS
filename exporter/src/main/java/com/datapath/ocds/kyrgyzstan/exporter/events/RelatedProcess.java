package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

import java.util.List;

@Data
public class RelatedProcess {

    private String identifier;
    private List<String> relationship;

}
