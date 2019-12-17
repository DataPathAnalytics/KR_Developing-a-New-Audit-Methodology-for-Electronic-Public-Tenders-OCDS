package com.datapath.ocds.kyrgyzstan.api.events;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "events")
public class Event {

    private String id;
    @Indexed
    private String ocid;
    private String date;
    private List<String> tag;
    private String initiationType;
    private Integer eventType;
    private List<String> eventErrors;

}