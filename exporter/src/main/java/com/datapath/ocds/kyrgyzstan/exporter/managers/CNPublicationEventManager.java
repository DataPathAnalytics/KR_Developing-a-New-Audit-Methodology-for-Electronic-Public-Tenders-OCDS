package com.datapath.ocds.kyrgyzstan.exporter.managers;

import com.datapath.ocds.kyrgyzstan.exporter.IdResponse;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.events.CNPublicationEvent;
import com.datapath.ocds.kyrgyzstan.exporter.receivers.CnPublicationEventReceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class CNPublicationEventManager implements EventManager {

    private CnPublicationEventReceiver eventReceiver;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public void setEventReceiver(CnPublicationEventReceiver eventReceiver) {
        this.eventReceiver = eventReceiver;
    }

    @Override
    public void process(JsonEvent jsonEvent) {
        CNPublicationEvent event = eventReceiver.receive(jsonEvent);
        log.info("Finish receiving event");
        IdResponse response = restTemplate.postForObject("/events/cn-publication", event, IdResponse.class);
        log.info("Event saved. Received id - {}", response);
    }
}
