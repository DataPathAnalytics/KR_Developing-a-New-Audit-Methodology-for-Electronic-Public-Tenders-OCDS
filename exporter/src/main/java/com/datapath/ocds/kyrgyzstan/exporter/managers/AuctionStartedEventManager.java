package com.datapath.ocds.kyrgyzstan.exporter.managers;

import com.datapath.ocds.kyrgyzstan.exporter.IdResponse;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.events.AuctionStartedEvent;
import com.datapath.ocds.kyrgyzstan.exporter.receivers.AuctionStartedEventReceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class AuctionStartedEventManager implements EventManager {

    @Autowired
    private AuctionStartedEventReceiver receiver;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void process(JsonEvent jsonEvent) {
        AuctionStartedEvent event = receiver.receive(jsonEvent);
        log.info("Finish receiving event");
        IdResponse response = restTemplate.postForObject("/events/auction-started", event, IdResponse.class);
        log.info("Event saved. Received id - {}", response);
    }

}