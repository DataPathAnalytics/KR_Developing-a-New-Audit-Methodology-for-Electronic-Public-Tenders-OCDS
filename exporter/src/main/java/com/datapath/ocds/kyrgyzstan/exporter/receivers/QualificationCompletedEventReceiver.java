package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.OrderStatus;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Order;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.QualificationCompletedDAOService;
import com.datapath.ocds.kyrgyzstan.exporter.events.BidDetail;
import com.datapath.ocds.kyrgyzstan.exporter.events.Bids;
import com.datapath.ocds.kyrgyzstan.exporter.events.QualificationCompletedEvent;
import com.datapath.ocds.kyrgyzstan.exporter.events.Tender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.DASH;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.OCID_PREFIX;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.convert;

@Component
public class QualificationCompletedEventReceiver {

    @Autowired
    private QualificationCompletedDAOService dao;

    public QualificationCompletedEvent receive(JsonEvent jsonEvent) {
        Order order = dao.getOrder(jsonEvent.getOrderId());

        QualificationCompletedEvent event = new QualificationCompletedEvent();

        event.setDate(convert(jsonEvent.getDateCreated()));
        event.setSingleTag("qualification");
        event.setOcid(OCID_PREFIX + jsonEvent.getOrderId());
        event.setId(event.getOcid() + DASH + jsonEvent.getId());
        event.setEventType(jsonEvent.getType());

        Tender tender = new Tender();
        tender.setId(String.valueOf(order.getId()));
        tender.setCurrentStage(OrderStatus.getStatus(order.getStatus()).getName());
        event.setTender(tender);

        event.setBids(getBids(jsonEvent.getId()));
        return event;
    }

    private Bids getBids(Integer jsonEventId) {
        ArrayList<BidDetail> details = new ArrayList<>();

        dao.getBidDetails(jsonEventId).forEach(daoDetail -> {
            BidDetail detail = new BidDetail();
            detail.setId(daoDetail.getId());
            detail.setStatus(daoDetail.getStatus());
            details.add(detail);
        });

        return new Bids(details);
    }

}