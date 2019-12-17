package com.datapath.ocds.kyrgyzstan.exporter.events;

import lombok.Data;

@Data
public class TwoStageInvitationPublishedEvent extends Event {

    private Tender tender;

}