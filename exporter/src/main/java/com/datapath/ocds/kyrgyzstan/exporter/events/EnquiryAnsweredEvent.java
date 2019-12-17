package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

@Data
public class EnquiryAnsweredEvent extends Event {

    private Tender tender;

}