package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

@Data
public class CNAutoProlongedEvent extends Event {
    private Tender tender;
}