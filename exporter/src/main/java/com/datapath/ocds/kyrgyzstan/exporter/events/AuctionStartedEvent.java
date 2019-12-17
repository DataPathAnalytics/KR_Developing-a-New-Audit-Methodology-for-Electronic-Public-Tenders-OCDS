package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

@Data
public class AuctionStartedEvent extends Event {

    private Tender tender;

}
