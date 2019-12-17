package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.OrderStatus;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Order;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.CNAutoProlongedDAOService;
import com.datapath.ocds.kyrgyzstan.exporter.events.CNAutoProlongedEvent;
import com.datapath.ocds.kyrgyzstan.exporter.events.Period;
import com.datapath.ocds.kyrgyzstan.exporter.events.Tender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.DASH;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.OCID_PREFIX;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.convert;
import static java.util.Objects.nonNull;

@Component
public class CNAutoProlongedReceiver extends EventReceiverAbs{

    private static final String TENDER_UPDATE = "tenderUpdate";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_LOCAL_TIME).toFormatter();
    @Autowired
    private CNAutoProlongedDAOService dao;

    public CNAutoProlongedEvent receive(JsonEvent jsonEvent) {
        Order order = dao.getOrder(jsonEvent.getOrderId());

        CNAutoProlongedEvent event = new CNAutoProlongedEvent();
        event.setDate(convert(jsonEvent.getDateCreated()));
        event.setSingleTag(TENDER_UPDATE);
        event.setOcid(OCID_PREFIX + jsonEvent.getOrderId());
        event.setId(event.getOcid() + DASH + jsonEvent.getId());
        event.setEventType(jsonEvent.getType());

        Tender tender = new Tender();
        tender.setId(String.valueOf(order.getId()));
        tender.setCurrentStage(OrderStatus.getStatus(order.getStatus()).getName());

        Period tenderPeriod = new Period();
        tenderPeriod.setStartDate(convert(order.getDatePublished()));
        tenderPeriod.setEndDate(convert(order.getDateContest()));
        tender.setTenderPeriod(tenderPeriod);

        if (nonNull(order.getDateContest()) && nonNull(order.getDatePublished())) {
            Period enquiryPeriod = new Period();
            LocalDateTime datePublished = LocalDateTime.parse(order.getDatePublished(), DATE_TIME_FORMATTER);
            LocalDateTime dateContest = LocalDateTime.parse(order.getDateContest(), DATE_TIME_FORMATTER);

            if (dateContest.minusDays(5).isAfter(datePublished)) {
                enquiryPeriod.setStartDate(convert(order.getDatePublished()));
            }

            if (dateContest.minusDays(5).isAfter(datePublished)) {
                enquiryPeriod.setEndDate(convert(dateContest.minusDays(5).format(DATE_TIME_FORMATTER)));
            }

            tender.setEnquiryPeriod(enquiryPeriod);
        }


        event.setTender(tender);
        event.setEventErrors(getSequenceErrors(jsonEvent));

        return event;
    }
}