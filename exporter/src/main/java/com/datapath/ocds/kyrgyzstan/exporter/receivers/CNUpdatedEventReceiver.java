package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.OrderStatus;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.*;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.CNUpdatedDAOService;
import com.datapath.ocds.kyrgyzstan.exporter.events.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.DASH;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.OCID_PREFIX;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.convert;
import static java.util.Objects.nonNull;

@Component
public class CNUpdatedEventReceiver extends EventReceiverAbs {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_LOCAL_TIME).toFormatter();

    @Autowired
    private CNUpdatedDAOService dao;

    public CNUpdatedEvent receive(JsonEvent jsonEvent) {
        Order order = dao.getOrder(jsonEvent.getOrderId());

        CNUpdatedEvent event = new CNUpdatedEvent();
        event.setDate(convert(jsonEvent.getDateCreated()));
        event.setSingleTag("tenderUpdate");
        event.setOcid(OCID_PREFIX + jsonEvent.getOrderId());
        event.setId(event.getOcid() + DASH + jsonEvent.getId());
        event.setEventType(jsonEvent.getType());

        Tender tender = new Tender();
        tender.setId(String.valueOf(order.getId()));
        tender.setCurrentStage(OrderStatus.getStatus(order.getStatus()).getName());
        tender.setLots(getLots(order.getId()));
        tender.setItems(getItems(jsonEvent.getId()));
        tender.setDocuments(getDocuments(jsonEvent));

        Value value = new Value();
        value.setAmount(order.getTotalSum());
        tender.setValue(value);

        Period tenderPeriod = new Period();
        tenderPeriod.setStartDate(convert(order.getDatePublished()));
        tenderPeriod.setEndDate(convert(order.getDateContest()));
        tender.setTenderPeriod(tenderPeriod);

        tender.setEnquiryPeriod(getEnquiryPeriod(order));

        event.setTender(tender);
        event.setEventErrors(getSequenceErrors(jsonEvent));
        return event;
    }

    private Period getEnquiryPeriod(Order order) {
        Period enquiryPeriod = null;
        if (nonNull(order.getDateContest()) && nonNull(order.getDatePublished())) {
            enquiryPeriod = new Period();
            LocalDateTime datePublished = LocalDateTime.parse(order.getDatePublished(), DATE_TIME_FORMATTER);
            LocalDateTime dateContest = LocalDateTime.parse(order.getDateContest(), DATE_TIME_FORMATTER);

            if (dateContest.minusDays(5).isAfter(datePublished)) {
                enquiryPeriod.setStartDate(convert(order.getDatePublished()));
            }

            if (dateContest.minusDays(5).isAfter(datePublished)) {
                enquiryPeriod.setEndDate(convert(dateContest.minusDays(5).format(DATE_TIME_FORMATTER)));
            }
        }
        return enquiryPeriod;
    }

    private List<TenderDocument> getDocuments(JsonEvent jsonEvent) {
        List<TenderDocument> documents = new ArrayList<>();

        List<TenderDocumentDAO> daoDocuments = dao.getDocuments(jsonEvent.getOrderId());
        for (TenderDocumentDAO daoDocument : daoDocuments) {
            TenderDocument document = new TenderDocument();
            document.setId(daoDocument.getId());
            document.setDatePublished(convert(jsonEvent.getDateCreated()));
            documents.add(document);
        }

        return documents;
    }

    private List<TenderItem> getItems(Integer jsonEventId) {
        List<TenderItem> items = new ArrayList<>();
        List<TenderItemDAO> daoItems = dao.getItems(jsonEventId);
        for (TenderItemDAO daoItem : daoItems) {
            TenderItem item = new TenderItem();
            item.setId(daoItem.getId());
            item.setQuantity(daoItem.getQuantity());

            Unit unit = new Unit();
            unit.setId(daoItem.getUnitId());
            unit.setName(daoItem.getUnitName());

            Value unitValue = new Value();
            unitValue.setAmount(daoItem.getUnitValueAmount());
            unit.setValue(unitValue);
            item.setUnit(unit);

            items.add(item);
        }
        return items;
    }

    private List<Lot> getLots(Integer orderId) {
        List<Lot> lots = new ArrayList<>();

        List<LotDAO> daoLots = dao.getLots(orderId);
        for (LotDAO daoLot : daoLots) {
            Lot lot = new Lot();
            lot.setId(daoLot.getId());

            Value value = new Value();
            value.setAmount(daoLot.getAmount());
            lot.setValue(value);

            lots.add(lot);
        }
        return lots;
    }

}