package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

import java.util.List;

@Data
public class TenderCancelledEvent extends Event {
    private Tender tender;
    private List<Award> awards;
    private List<Contract> contracts;
}
