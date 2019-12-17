package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Bids {

    private List<BidDetail> details;

}
