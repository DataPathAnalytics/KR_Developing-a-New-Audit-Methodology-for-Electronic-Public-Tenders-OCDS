package com.datapath.ocds.kyrgyzstan.exporter.managers;

import com.datapath.ocds.kyrgyzstan.exporter.RequestManager;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.repository.JsonEventRepository;
import com.datapath.ocds.kyrgyzstan.exporter.events.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.PG_DATE_TIME_FORMATTER;

@Service
@Slf4j
public class KyrgyzstanManager {

    @Autowired
    private JsonEventRepository eventRepository;
    @Autowired
    private RequestManager requestManager;
    @Autowired
    private EventManagerFactory eventManagerFactory;

    @Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}")
    public void run() {
        Event lastEvent = requestManager.getLastEvent();
        List<JsonEvent> jsonEvents = getJsonEvents(lastEvent);
        for (JsonEvent jsonEvent : jsonEvents) {
            processEvaluationCompletedEvent(jsonEvent);
            log.info("Parse json event with id = " + jsonEvent.getId());
            EventManager eventManager = eventManagerFactory.getManager(jsonEvent.getType());
            eventManager.process(jsonEvent);
        }
    }

    private List<JsonEvent> getJsonEvents(Event lastEvent) {
        if (lastEvent.getDate() == null) {
            return eventRepository.findByOrderByDateCreated();
        } else {
            return eventRepository.findNewEvents(
                    OffsetDateTime.parse(lastEvent.getDate()).toLocalDateTime().format(PG_DATE_TIME_FORMATTER));
        }
    }

    private void processEvaluationCompletedEvent(JsonEvent event) {
        if (event.getType() == 5) {
            boolean exists = eventRepository.existBidsDisclosedEvent(event.getOrderId(), event.getDateCreated());
            if (!exists) {

                JsonEvent bidsEvent = new JsonEvent();
                bidsEvent.setType(4);
                bidsEvent.setId(event.getId());
                bidsEvent.setDateCreated(event.getDateCreated());
                bidsEvent.setOrderId(event.getOrderId());

                EventManager eventManager = eventManagerFactory.getManager(bidsEvent.getType());
                eventManager.process(bidsEvent);
            }
        }
    }

}