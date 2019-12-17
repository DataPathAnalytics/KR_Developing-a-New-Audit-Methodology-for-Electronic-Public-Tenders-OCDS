package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Order;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.EnquiryAnsweredDAOService;
import com.datapath.ocds.kyrgyzstan.exporter.events.Enquiry;
import com.datapath.ocds.kyrgyzstan.exporter.events.EnquiryAnsweredEvent;
import com.datapath.ocds.kyrgyzstan.exporter.events.Tender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.DASH;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.OCID_PREFIX;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.convert;

@Component
public class EnquiryAnsweredEventReceiver extends EventReceiverAbs {

    @Autowired
    private EnquiryAnsweredDAOService dao;

    public EnquiryAnsweredEvent receive(JsonEvent jsonEvent) {
        Order order = dao.getOrder(jsonEvent.getOrderId());

        EnquiryAnsweredEvent event = new EnquiryAnsweredEvent();
        event.setDate(convert(jsonEvent.getDateCreated()));
        event.setSingleTag("enquiryUpdate");
        event.setOcid(OCID_PREFIX + jsonEvent.getOrderId());
        event.setId(event.getOcid() + DASH + jsonEvent.getId());
        event.setEventType(jsonEvent.getType());

        Tender tender = new Tender();
        tender.setId(String.valueOf(order.getId()));
        tender.setEnquiries(getEnquiries(jsonEvent.getId()));
        event.setTender(tender);
        event.setEventErrors(getSequenceErrors(jsonEvent));

        return event;
    }

    private List<Enquiry> getEnquiries(Integer jsonEventId) {
        List<Enquiry> enquiries = new ArrayList<>();
        dao.getEnquiries(jsonEventId).forEach(daoEnquiry -> {
            Enquiry enquiry = new Enquiry();
            enquiry.setId(daoEnquiry.getId());
            enquiry.setAnswer(daoEnquiry.getAnswer());
            enquiry.setDateAnswered(convert(daoEnquiry.getDateAnswered()));
            enquiries.add(enquiry);
        });
        return enquiries.isEmpty() ? null : enquiries;
    }

}
