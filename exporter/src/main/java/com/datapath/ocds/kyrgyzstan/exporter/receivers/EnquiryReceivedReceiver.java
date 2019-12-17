package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Order;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.EnquiryReceivedDAOService;
import com.datapath.ocds.kyrgyzstan.exporter.events.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.DASH;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.OCID_PREFIX;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.convert;
import static java.util.Objects.nonNull;

@Component
public class EnquiryReceivedReceiver extends EventReceiverAbs {

    @Autowired
    private EnquiryReceivedDAOService dao;

    public EnquiryReceivedEvent receive(JsonEvent jsonEvent) {
        Order order = dao.getOrder(jsonEvent.getOrderId());

        EnquiryReceivedEvent event = new EnquiryReceivedEvent();
        event.setDate(convert(jsonEvent.getDateCreated()));
        event.setSingleTag("enquiry");
        event.setOcid(OCID_PREFIX + jsonEvent.getOrderId());
        event.setId(event.getOcid() + DASH + jsonEvent.getId());
        event.setEventType(jsonEvent.getType());

        Tender tender = new Tender();
        tender.setId(String.valueOf(order.getId()));
        tender.setEnquiries(getEnquiries(jsonEvent.getId()));

        tender.getEnquiries().forEach(enquiry -> {
            if (nonNull(enquiry.getAuthor())) {
                if (enquiry.getAuthor().getId().equals("01707201410029") &&
                        CollectionUtils.isEmpty(event.getParties())) {
                    Party party = new Party();
                    party.setId("KG-INN-01707201410029");

                    Identifier identifier = new Identifier();
                    identifier.setId("01707201410029");
                    identifier.setScheme("KG-INN");
                    party.setIdentifier(identifier);
                    party.setRoles(Collections.singletonList("enquirer"));

                    event.setParties(Collections.singletonList(party));
                }
            }
        });

        event.setTender(tender);
        event.setEventErrors(getSequenceErrors(jsonEvent));

        return event;
    }

    private List<Enquiry> getEnquiries(Integer jsonEventId) {
        List<Enquiry> enquiries = new ArrayList<>();
        dao.getEnquiries(jsonEventId).forEach(daoEnquiry -> {
            Enquiry enquiry = new Enquiry();
            enquiry.setId(daoEnquiry.getId());
            enquiry.setDate(convert(daoEnquiry.getDate()));
            enquiry.setDescription(daoEnquiry.getDescription());
            if (daoEnquiry.getAuthorId() != null) {
                enquiry.setAuthor(new Author(daoEnquiry.getAuthorId()));
            }
            enquiries.add(enquiry);
        });
        return enquiries.isEmpty() ? null : enquiries;
    }

}