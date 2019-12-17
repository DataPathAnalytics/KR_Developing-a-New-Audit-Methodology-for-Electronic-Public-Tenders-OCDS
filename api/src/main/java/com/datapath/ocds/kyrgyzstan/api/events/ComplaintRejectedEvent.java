package com.datapath.ocds.kyrgyzstan.api.events;

import lombok.Data;

import java.util.List;

@Data
public class ComplaintRejectedEvent extends Event{

    private List<Complaint> complaints;

    @Data
    public static class Complaint {
        private String id;
        private String status;
        private String responseDate;
        private String response;

    }

}