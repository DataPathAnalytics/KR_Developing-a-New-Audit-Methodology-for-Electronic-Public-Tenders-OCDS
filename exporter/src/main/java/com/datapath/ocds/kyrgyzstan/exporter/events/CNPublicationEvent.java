package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CNPublicationEvent extends Event {

    private List<Party> parties;
    private Tender tender;
    private List<RelatedProcess> relatedProcesses;

    public CNPublicationEvent() {
        parties = new ArrayList<>();
    }

}