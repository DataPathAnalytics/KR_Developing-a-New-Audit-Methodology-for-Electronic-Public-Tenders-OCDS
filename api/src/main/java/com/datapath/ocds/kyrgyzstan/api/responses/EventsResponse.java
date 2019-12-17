package com.datapath.ocds.kyrgyzstan.api.responses;

import com.datapath.ocds.kyrgyzstan.api.events.Event;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EventsResponse {

    private List<Event> events;

}