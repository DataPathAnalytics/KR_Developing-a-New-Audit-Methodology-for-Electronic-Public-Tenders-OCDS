package com.datapath.ocds.kyrgyzstan.exporter.managers;

import com.datapath.ocds.kyrgyzstan.exporter.IdResponse;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.events.Bids1StageOpenedEvent;
import com.datapath.ocds.kyrgyzstan.exporter.receivers.Bids1StageOpenedEventReceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class Bids1StageOpenedEventManager implements EventManager {

    private Bids1StageOpenedEventReceiver receiver;
    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setReceiver(Bids1StageOpenedEventReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void process(JsonEvent jsonEvent) {
        Bids1StageOpenedEvent event = receiver.receive(jsonEvent);
        log.info("Finish receiving event");
        IdResponse response = restTemplate.postForObject("/events/bids1-stage-opened", event, IdResponse.class);
        log.info("Event saved. Received id - {}", response);
    }
}
