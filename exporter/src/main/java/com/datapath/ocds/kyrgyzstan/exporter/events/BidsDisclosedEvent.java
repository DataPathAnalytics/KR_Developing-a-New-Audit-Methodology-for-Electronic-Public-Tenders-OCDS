package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BidsDisclosedEvent extends Event {
    private List<Party> parties;
    private Tender tender;
    private Bids bids;
}