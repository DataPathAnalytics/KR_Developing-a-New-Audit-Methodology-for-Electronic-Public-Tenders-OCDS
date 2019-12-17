package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

import java.util.List;

@Data
public class ComplaintReceivedEvent extends Event {

    private List<Complaint> complaints;
    private List<Party> parties;

}
