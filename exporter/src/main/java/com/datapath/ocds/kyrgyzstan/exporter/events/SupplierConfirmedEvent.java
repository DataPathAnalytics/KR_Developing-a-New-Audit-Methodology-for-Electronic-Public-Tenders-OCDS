package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

import java.util.List;

@Data
public class SupplierConfirmedEvent extends Event {

    private List<Party> parties;
    private Tender tender;
    private Bids bids;
    private List<Award> awards;

}
