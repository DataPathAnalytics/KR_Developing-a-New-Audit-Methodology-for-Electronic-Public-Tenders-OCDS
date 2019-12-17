package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

import java.util.List;

@Data
public class EnquiryReceivedEvent extends Event {

    private Tender tender;
    private List<Party> parties;

}
