package com.datapath.ocds.kyrgyzstan.exporter.managers;

import com.datapath.ocds.kyrgyzstan.exporter.IdResponse;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.events.EnquiryAnsweredEvent;
import com.datapath.ocds.kyrgyzstan.exporter.receivers.EnquiryAnsweredEventReceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class EnquiryAnsweredEventManager implements EventManager {

    @Autowired
    private EnquiryAnsweredEventReceiver receiver;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void process(JsonEvent jsonEvent) {
        EnquiryAnsweredEvent event = receiver.receive(jsonEvent);
        log.info("Finish receiving event");
        IdResponse response = restTemplate.postForObject("/events/enquiry-answered", event, IdResponse.class);
        log.info("Event saved. Received id - {}", response);
    }

}