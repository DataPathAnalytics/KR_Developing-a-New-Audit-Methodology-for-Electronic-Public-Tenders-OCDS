package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

import java.util.List;

@Data
public class ComplaintResolvedEvent extends Event {

    private List<Complaint> complaints;

}
