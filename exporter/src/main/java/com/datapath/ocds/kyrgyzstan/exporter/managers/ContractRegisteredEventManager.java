package com.datapath.ocds.kyrgyzstan.exporter.managers;

import com.datapath.ocds.kyrgyzstan.exporter.IdResponse;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.events.ContractRegisteredEvent;
import com.datapath.ocds.kyrgyzstan.exporter.receivers.ContractRegisteredReceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class ContractRegisteredEventManager implements EventManager {

    @Autowired
    private ContractRegisteredReceiver receiver;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void process(JsonEvent jsonEvent) {
        ContractRegisteredEvent event = receiver.receive(jsonEvent);
        log.info("Finish receiving event");
        IdResponse response = restTemplate.postForObject("/events/contract-registered", event, IdResponse.class);
        log.info("Event saved. Received id - {}", response);
    }

}