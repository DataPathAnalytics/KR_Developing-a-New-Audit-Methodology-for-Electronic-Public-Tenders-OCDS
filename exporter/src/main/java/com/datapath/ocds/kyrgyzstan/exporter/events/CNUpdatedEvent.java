package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

@Data
public class CNUpdatedEvent extends Event {
    private Tender tender;
}