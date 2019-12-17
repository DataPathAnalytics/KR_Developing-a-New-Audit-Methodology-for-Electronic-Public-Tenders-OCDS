package com.datapath.ocds.kyrgyzstan.exporter;

import com.datapath.ocds.kyrgyzstan.exporter.events.Event;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RequestManager {

    private RestTemplate restTemplate;

    public RequestManager(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Event getLastEvent() {
        return restTemplate.getForObject("/events/last", Event.class);
    }

}