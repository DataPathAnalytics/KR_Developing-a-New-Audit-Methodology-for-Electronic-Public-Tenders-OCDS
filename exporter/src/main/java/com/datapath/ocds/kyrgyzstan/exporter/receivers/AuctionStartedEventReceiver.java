package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.OrderStatus;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Order;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.AuctionStartedDAOService;
import com.datapath.ocds.kyrgyzstan.exporter.events.AuctionStartedEvent;
import com.datapath.ocds.kyrgyzstan.exporter.events.Tender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.DASH;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.OCID_PREFIX;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.convert;

@Component
public class AuctionStartedEventReceiver extends EventReceiverAbs {

    private static final String TENDER_UPDATE = "tenderUpdate";

    @Autowired
    private AuctionStartedDAOService dao;

    public AuctionStartedEvent receive(JsonEvent jsonEvent) {
        Order order = dao.getOrder(jsonEvent.getOrderId());

        AuctionStartedEvent event = new AuctionStartedEvent();
        event.setDate(convert(jsonEvent.getDateCreated()));
        event.setSingleTag(TENDER_UPDATE);
        event.setOcid(OCID_PREFIX + jsonEvent.getOrderId());
        event.setId(event.getOcid() + DASH + jsonEvent.getId());
        event.setEventType(jsonEvent.getType());

        Tender tender = new Tender();
        tender.setId(String.valueOf(order.getId()));
        tender.setCurrentStage(OrderStatus.getStatus(order.getStatus()).getName());
        event.setTender(tender);
        event.setEventErrors(getSequenceErrors(jsonEvent));

        return event;
    }

}