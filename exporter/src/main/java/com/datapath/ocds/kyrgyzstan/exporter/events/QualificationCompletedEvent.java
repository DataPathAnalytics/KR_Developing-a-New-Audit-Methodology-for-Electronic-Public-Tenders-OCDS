package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

@Data
public class QualificationCompletedEvent extends Event {

    private Tender tender;
    private Bids bids;

}
